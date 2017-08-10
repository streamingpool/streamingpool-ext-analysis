/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis;

import static org.streamingpool.ext.tensorics.evaluation.EvaluationStrategies.defaultEvaluation;
import static org.tensorics.core.tree.domain.Contexts.newResolvingContext;

import org.streamingpool.core.service.StreamId;
import org.streamingpool.ext.analysis.modules.StreamBasedAnalysisModule;
import org.streamingpool.ext.tensorics.streamid.DetailedExpressionStreamId;
import org.streamingpool.ext.tensorics.streamid.ExpressionBasedStreamId;
import org.tensorics.core.analysis.AnalysisResult;
import org.tensorics.core.analysis.expression.AnalysisExpression;
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
            StreamBasedAnalysisModule<?> analysisModule) {
        return detailedStreamIdFor(analysisModule, newResolvingContext());
    }

    public static <T, E extends Expression<T>> DetailedExpressionStreamId<T, E> detailedStreamIdFor(E expression) {
        return DetailedExpressionStreamId.of(expression, newResolvingContext(), defaultEvaluation());
    }

    public static DetailedExpressionStreamId<AnalysisResult, AnalysisExpression> detailedStreamIdFor(
            StreamBasedAnalysisModule<?> analysisModule, ResolvingContext initialContext) {
        return DetailedExpressionStreamId.of(analysisModule.buildExpression(), initialContext,
                analysisModule.buildEvaluationStrategy());
    }

    public static StreamId<AnalysisResult> streamIdFor(StreamBasedAnalysisModule<?> analysisModule) {
        return streamIdFor(analysisModule, newResolvingContext());
    }

    public static StreamId<AnalysisResult> streamIdFor(StreamBasedAnalysisModule<?> analysisModule,
            ResolvingContext initialContext) {
        return ExpressionBasedStreamId.of(analysisModule.buildExpression(), initialContext, analysisModule.buildEvaluationStrategy());
    }

    public static <T> ExpressionBasedStreamId<T> streamIdFor(Expression<T> expression,
            ResolvingContext prefilledContext) {
        return ExpressionBasedStreamId.of(expression, prefilledContext, defaultEvaluation());
    }

    public static <T> ExpressionBasedStreamId<T> streamIdFor(Expression<T> expression) {
        return ExpressionBasedStreamId.of(expression);
    }

}
