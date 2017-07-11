// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
* 
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
// @formatter:on

package org.streamingpool.ext.analysis;

import static java.util.Objects.requireNonNull;
import static org.streamingpool.ext.tensorics.expression.StreamIdBasedExpression.of;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.BufferSpecification;
import org.streamingpool.core.service.streamid.OverlapBufferStreamId;
import org.streamingpool.ext.analysis.dsl.AllTrue;
import org.streamingpool.ext.analysis.dsl.OngoingAllBooleanCondition;
import org.streamingpool.ext.analysis.dsl.OngoingAllBooleanExcludableCondition;
import org.streamingpool.ext.analysis.dsl.OngoingAnalysisEnabler;
import org.streamingpool.ext.analysis.dsl.OngoingAnyBooleanCondition;
import org.streamingpool.ext.analysis.dsl.OngoingBooleanCondition;
import org.streamingpool.ext.analysis.dsl.OngoingBufferedStrategy;
import org.streamingpool.ext.analysis.dsl.OngoingCondition;
import org.streamingpool.ext.analysis.dsl.OngoingPrecondition;
import org.streamingpool.ext.analysis.dsl.OngoingTriggeredStrategy;
import org.streamingpool.ext.tensorics.evaluation.BufferedEvaluation;
import org.streamingpool.ext.tensorics.evaluation.ContinuousEvaluation;
import org.streamingpool.ext.tensorics.evaluation.EvaluationStrategy;
import org.streamingpool.ext.tensorics.evaluation.EvaluationStrategyBuilder;
import org.streamingpool.ext.tensorics.evaluation.TriggeredEvaluation;
import org.streamingpool.ext.tensorics.evaluation.TriggeredEvaluation.Builder;
import org.streamingpool.ext.tensorics.expression.StreamIdBasedExpression;
import org.streamingpool.ext.tensorics.streamid.ExpressionBasedStreamId;
import org.tensorics.core.expressions.ConversionOperationExpression;
import org.tensorics.core.iterable.expressions.IterableExpressionToIterable;
import org.tensorics.core.iterable.expressions.IterableOperationExpression;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvedExpression;

import com.google.common.collect.Iterables;

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

    /**
     * It buffers the source {@link Expression} and returns an expression with the latest value from the buffer
     */
    protected final <T1> Expression<T1> latestOf(Expression<T1> source) {
        Expression<List<T1>> bufferedSource = buffered(source);
        return new ConversionOperationExpression<>(Iterables::getLast, bufferedSource);
    }

    protected final <T> Expression<List<T>> buffered(StreamId<T> sourceStreamId) {
        requireNonNull(sourceStreamId, "sourceStreamId must not be null.");
        if (sourceStreamId instanceof OverlapBufferStreamId) {
            throw new IllegalArgumentException("The given sourceStreamId is already a buffered stream id. "
                    + "Buffering a buffered stream makes limited sense and is currently not supported.");
            /* Should we allow this? It probably would create more confusion than usefulness */
        }
        StreamIdBasedExpression<List<T>> of = StreamIdBasedExpression
                .of(OverlapBufferStreamId.of(sourceStreamId, bufferSpecification()));
        return of;
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

    public OngoingBooleanCondition assertLatestBooleanOf(Expression<List<Boolean>> buffered) {
        return new OngoingBooleanCondition(newAssertionBuilder(),
                new ConversionOperationExpression<>(Iterables::getLast, buffered));
    }

    protected final OngoingPrecondition<Boolean> whenTrue(Expression<Boolean> whenSource) {
        return new OngoingPrecondition<>(newAssertionBuilder(), whenSource).isEqualTo(true);
    }

    protected final OngoingPrecondition<Boolean> whenFalse(Expression<Boolean> whenSource) {
        return new OngoingPrecondition<>(newAssertionBuilder(), whenSource).isEqualTo(false);
    }

    protected final OngoingPrecondition<Boolean> whenNot(Expression<Boolean> whenSource) {
        return whenFalse(whenSource);
    }

    protected final OngoingPrecondition<Boolean> whenAllTrue(Iterable<Expression<Boolean>> expressions) {
        Expression<Iterable<Boolean>> booleans = new IterableExpressionToIterable<>(expressions);
        Expression<Boolean> combined = new IterableOperationExpression<>(new AllTrue(), booleans);
        return whenTrue(combined);
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
