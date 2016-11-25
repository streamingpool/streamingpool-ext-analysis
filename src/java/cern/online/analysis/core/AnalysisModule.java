/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import static cern.streaming.pool.ext.tensorics.expression.StreamIdBasedExpression.of;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvedExpression;

import cern.online.analysis.core.dsl.OngoingAllBooleanCondition;
import cern.online.analysis.core.dsl.OngoingAllBooleanExcludableCondition;
import cern.online.analysis.core.dsl.OngoingAnalysisEnabler;
import cern.online.analysis.core.dsl.OngoingAnyBooleanCondition;
import cern.online.analysis.core.dsl.OngoingBooleanCondition;
import cern.online.analysis.core.dsl.OngoingBufferedStrategy;
import cern.online.analysis.core.dsl.OngoingCondition;
import cern.online.analysis.core.dsl.OngoingPrecondition;
import cern.online.analysis.core.dsl.OngoingTriggeredStrategy;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.BufferSpecification;
import cern.streaming.pool.core.service.streamid.OverlapBufferStreamId;
import cern.streaming.pool.ext.tensorics.evaluation.BufferedEvaluation;
import cern.streaming.pool.ext.tensorics.evaluation.ContinuousEvaluation;
import cern.streaming.pool.ext.tensorics.evaluation.EvaluationStrategy;
import cern.streaming.pool.ext.tensorics.evaluation.EvaluationStrategyBuilder;
import cern.streaming.pool.ext.tensorics.evaluation.TriggeredEvaluation;
import cern.streaming.pool.ext.tensorics.evaluation.TriggeredEvaluation.Builder;
import cern.streaming.pool.ext.tensorics.expression.StreamIdBasedExpression;
import cern.streaming.pool.ext.tensorics.streamid.ExpressionBasedStreamId;

/**
 * Abstract base class for analysis modules. Provides fluent API methods to specify assertions.
 * <p>
 * This class is not threadsafe!
 * 
 * @author acalia, caguiler, kfuchsbe
 */
public abstract class AnalysisModule {

    private final List<AssertionBuilder> assertionBuilders = new ArrayList<>();

    private final AtomicBoolean enablingSpecified = new AtomicBoolean(false);
    private EnablingConditionBuilder enablerBuilder;

    private final AtomicBoolean strategySpecified = new AtomicBoolean(false);
    private EvaluationStrategyBuilder evaluationStrategyBuilder;
    private EvaluationStrategy evaluationStrategy = null;

    protected final OngoingAnalysisEnabler enabled() {
        throwIfEnablingSpecifiedTwice();
        newEnablerBuilder();
        return new OngoingAnalysisEnabler(enablerBuilder);
    }

    protected final OngoingTriggeredStrategy triggered() {
        throwIfStrategySpecifiedTwice();
        Builder builder = TriggeredEvaluation.builder();
        this.evaluationStrategyBuilder = builder;
        return new OngoingTriggeredStrategy(builder);
    }

    protected final OngoingBufferedStrategy buffered() {
        throwIfStrategySpecifiedTwice();
        BufferedEvaluation.Builder builder = BufferedEvaluation.builder();
        this.evaluationStrategyBuilder = builder;
        return new OngoingBufferedStrategy(builder);
    }

    protected final <T> Expression<List<T>> buffered(Expression<T> sourceExpression) {
        requireNonNull(sourceExpression, "sourceExpression must not be null.");
        if (sourceExpression instanceof StreamIdBasedExpression) {
            return buffered(((StreamIdBasedExpression<T>) sourceExpression).streamId());
        } else {
            return buffered(ExpressionBasedStreamId.of(sourceExpression));
        }
    }

    protected final <T> Expression<? extends Iterable<T>> bufferedIterable(Expression<T> sourceExpression) {
        return buffered(sourceExpression);
    }

    protected final <T> Expression<? extends Iterable<T>> bufferedIterable(StreamId<T> sourceStream) {
        return buffered(of(sourceStream));
    }

    protected final <T> Expression<List<T>> buffered(StreamId<T> sourceStreamId) {
        requireNonNull(sourceStreamId, "sourceStreamId must not be null.");
        if (sourceStreamId instanceof OverlapBufferStreamId) {
            throw new IllegalArgumentException("The given sourceStreamId is already a buffered stream id. "
                    + "Buffering a buffered stream makes limited sense and is currently not supported.");
            /* Should we allow this? It probably would create more confusion than usefulness */
        }
        return StreamIdBasedExpression.of(OverlapBufferStreamId.of(sourceStreamId, bufferSpecification()));
    }

    protected final <T> OngoingCondition<T> assertThat(Expression<T> thatSource) {
        return new OngoingCondition<>(newAssertionBuilder(), thatSource);
    }

    protected final <T> OngoingCondition<T> assertThat(T thatSource) {
        return assertThat(ResolvedExpression.of(thatSource));
    }

    protected final <T> OngoingCondition<T> assertThat(StreamId<T> thatSource) {
        return assertThat(StreamIdBasedExpression.of(thatSource));
    }

    protected final OngoingBooleanCondition assertBoolean(Expression<Boolean> thatSource) {
        return new OngoingBooleanCondition(newAssertionBuilder(), thatSource);
    }

    protected final OngoingBooleanCondition assertBoolean(Boolean thatSource) {
        return assertBoolean(ResolvedExpression.of(thatSource));
    }

    protected final OngoingAllBooleanExcludableCondition assertAllBoolean(
            Set<? extends Expression<Boolean>> thatSource) {
        return new OngoingAllBooleanExcludableCondition(newAssertionBuilder(), thatSource);
    }

    protected final OngoingAllBooleanCondition assertAllBoolean(Expression<? extends Iterable<Boolean>> thatSource) {
        return new OngoingAllBooleanCondition(newAssertionBuilder(), thatSource);
    }

    protected final OngoingAllBooleanCondition assertAllBoolean(StreamId<? extends Iterable<Boolean>> thatSourceId) {
        return assertAllBoolean(StreamIdBasedExpression.of(thatSourceId));
    }

    protected final OngoingAnyBooleanCondition assertAtLeastOneBooleanOf(
            Expression<? extends Iterable<Boolean>> thatSource) {
        return new OngoingAnyBooleanCondition(newAssertionBuilder(), thatSource);
    }

    protected final OngoingAnyBooleanCondition assertAtLeastOneBooleanOf(
            StreamId<? extends Iterable<Boolean>> thatSourceId) {
        return assertAtLeastOneBooleanOf(StreamIdBasedExpression.of(thatSourceId));
    }

    protected final <T> OngoingPrecondition<T> when(Expression<T> whenSource) {
        return new OngoingPrecondition<>(newAssertionBuilder(), whenSource);
    }

    protected final <T> OngoingPrecondition<T> when(T whenSource) {
        return when(ResolvedExpression.of(whenSource));
    }

    private AssertionBuilder newAssertionBuilder() {
        AssertionBuilder builder = new AssertionBuilder();
        assertionBuilders.add(builder);
        return builder;
    }

    private void newEnablerBuilder() {
        this.enablerBuilder = new EnablingConditionBuilder();
    }

    public List<AssertionBuilder> assertionBuilders() {
        return assertionBuilders;
    }

    public EnablingConditionBuilder enablingBuilder() {
        return Optional.ofNullable(enablerBuilder).orElse(defaultEnablingConditionBuilder());
    }

    private EvaluationStrategyBuilder evaluationStrategyBuilder() {
        return Optional.ofNullable(this.evaluationStrategyBuilder).orElse(defaultEvaluationStrategyBuilder());
    }

    public EvaluationStrategy evaluationStrategy() {
        if (evaluationStrategy == null) {
            evaluationStrategy = evaluationStrategyBuilder().build();
        }
        return evaluationStrategy;
    }

    private void throwIfEnablingSpecifiedTwice() {
        if (enablingSpecified.getAndSet(true)) {
            throw new IllegalStateException("Only one fluent clause specifying the enabling condition is allowed. "
                    + "Seems you tried to call evaluated() twice.");
        }
    }

    private void throwIfStrategySpecifiedTwice() {
        if (strategySpecified.getAndSet(true)) {
            throw new IllegalStateException(
                    "It is only allowed to specify once either triggered() or buffered() within the same analysis module. "
                            + "It seems that you tried to specify both or one twice.");
        }
    }

    private BufferSpecification bufferSpecification() {
        EvaluationStrategy strategy = evaluationStrategy();
        if (!(strategy instanceof BufferedEvaluation)) {
            throw new IllegalStateException("The usage of buffered values is only allowed while using the '"
                    + BufferedEvaluation.class.getSimpleName() + "' evaluation strategy. "
                    + "Probably you forgot to specify the buffered()... clause? "
                    + "(This has to be done before using a buffered stream/expression)");
        }
        return ((BufferedEvaluation) strategy).bufferSpecification();
    }

    private static final EnablingConditionBuilder defaultEnablingConditionBuilder() {
        return new EnablingConditionBuilder().withCondition(ResolvedExpression.of(true));
    }

    private static final EvaluationStrategyBuilder defaultEvaluationStrategyBuilder() {
        return ContinuousEvaluation.builder();
    }

}
