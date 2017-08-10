/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.support;

import static org.streamingpool.ext.analysis.AnalysisDefinitions.detailedStreamIdFor;
import static org.streamingpool.ext.analysis.AnalysisDefinitions.streamIdFor;

import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.ext.analysis.modules.StreamBasedAnalysisModule;
import org.tensorics.core.analysis.AnalysisResult;
import org.tensorics.core.analysis.expression.AnalysisExpression;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvingContext;

import io.reactivex.Flowable;

public interface RxAnalysisSupport extends RxStreamSupport {

    default <T> Flowable<T> rxFrom(Expression<T> expression) {
        return rxFrom(streamIdFor(expression));
    }

    default <T, E extends Expression<T>> Flowable<DetailedExpressionResult<T, E>> rxDetailedFrom(E expression) {
        return rxFrom(detailedStreamIdFor(expression));
    }

    default <T> Flowable<T> rxFrom(Expression<T> expression, ResolvingContext prefilledContext) {
        return rxFrom(streamIdFor(expression, prefilledContext));
    }

    default <T, E extends Expression<T>> Flowable<DetailedExpressionResult<T, E>> rxDetailedFrom(E expression,
            ResolvingContext prefilledContext) {
        return rxFrom(detailedStreamIdFor(expression, prefilledContext));
    }

    default Flowable<AnalysisResult> rxFrom(StreamBasedAnalysisModule<?> bufferedAnalysisModule) {
        return rxFrom(streamIdFor(bufferedAnalysisModule));
    }

    default Flowable<DetailedExpressionResult<AnalysisResult, AnalysisExpression>> rxDetailedFrom(
            StreamBasedAnalysisModule<?> analysisModule) {
        return rxFrom(detailedStreamIdFor(analysisModule));
    }

    default Flowable<AnalysisResult> rxFrom(StreamBasedAnalysisModule<?> bufferedAnalysisModule,
            ResolvingContext prefilledContext) {
        return rxFrom(streamIdFor(bufferedAnalysisModule, prefilledContext));
    }

    default Flowable<DetailedExpressionResult<AnalysisResult, AnalysisExpression>> rxDetailedFrom(
            StreamBasedAnalysisModule<?> analysisModule, ResolvingContext prefilledContext) {
        return rxFrom(detailedStreamIdFor(analysisModule, prefilledContext));
    }

}
