/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.streamingpool.ext.tensorics.evaluation.EvaluationStrategies.defaultEvaluation;
import static org.tensorics.core.tree.domain.Contexts.newResolvingContext;

import org.streamingpool.core.service.StreamId;
import org.tensorics.core.analysis.AnalysisResult;
import org.tensorics.core.analysis.AnalysisModule;
import org.tensorics.core.analysis.expression.AnalysisExpression;
import org.tensorics.core.analysis.expression.AssertionExpression;
import org.streamingpool.ext.analysis.modules.StreamBaseAnalysisModule;
import org.streamingpool.ext.tensorics.streamid.DetailedExpressionStreamId;
import org.streamingpool.ext.tensorics.streamid.ExpressionBasedStreamId;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvingContext;

public final class AnalysisDefinitions {

    private AnalysisDefinitions() {
        /* Only static methods */
    }

    public static <T, E extends Expression<T>> DetailedExpressionStreamId<T, E> detailedStreamIdFor(E expression,
            ResolvingContext prefilledContext) {
        return DetailedExpressionStreamId.of(expression, prefilledContext, defaultEvaluation());
    }

    public static DetailedExpressionStreamId<AnalysisResult, AnalysisExpression> detailedStreamIdFor(
            StreamBaseAnalysisModule<?> analysisModule) {
        return detailedStreamIdFor(analysisModule, newResolvingContext());
    }

    public static <T, E extends Expression<T>> DetailedExpressionStreamId<T, E> detailedStreamIdFor(E expression) {
        return DetailedExpressionStreamId.of(expression, newResolvingContext(), defaultEvaluation());
    }

    public static DetailedExpressionStreamId<AnalysisResult, AnalysisExpression> detailedStreamIdFor(
            StreamBaseAnalysisModule<?> analysisModule, ResolvingContext initialContext) {
        return DetailedExpressionStreamId.of(expressionFrom(analysisModule), initialContext,
                analysisModule.evaluationStrategy());
    }

    public static StreamId<AnalysisResult> streamIdFor(StreamBaseAnalysisModule<?> analysisModule) {
        return streamIdFor(analysisModule, newResolvingContext());
    }

    public static StreamId<AnalysisResult> streamIdFor(StreamBaseAnalysisModule<?> analysisModule,
            ResolvingContext initialContext) {
        return ExpressionBasedStreamId.of(expressionFrom(analysisModule), initialContext,
                analysisModule.evaluationStrategy());
    }

    public static <T> ExpressionBasedStreamId<T> streamIdFor(Expression<T> expression,
            ResolvingContext prefilledContext) {
        return ExpressionBasedStreamId.of(expression, prefilledContext, defaultEvaluation());
    }

    public static <T> ExpressionBasedStreamId<T> streamIdFor(Expression<T> expression) {
        return ExpressionBasedStreamId.of(expression);
    }

    public static AnalysisExpression expressionFrom(AnalysisModule module) {
        return module.assertionBuilders().stream().map(AssertionExpression::new)
                .collect(collectingAndThen(toList(), AnalysisExpression::new));
    }

}
