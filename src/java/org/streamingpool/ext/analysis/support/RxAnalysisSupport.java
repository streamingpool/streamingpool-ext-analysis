/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.support;

import static org.streamingpool.ext.analysis.AnalysisDefinitions.streamIdFor;

import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.ext.analysis.AssertionStatus;
import org.streamingpool.ext.analysis.DeprecatedAnalysisResult;
import org.streamingpool.ext.analysis.expression.AnalysisExpression;
import org.streamingpool.ext.analysis.modules.AnalysisModule;
import org.streamingpool.ext.tensorics.streamid.DetailedExpressionStreamId;
import org.streamingpool.ext.tensorics.streamid.ExpressionBasedStreamId;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvingContext;

import io.reactivex.Flowable;

public interface RxAnalysisSupport extends RxStreamSupport {

    default <T> Flowable<T> rxFrom(Expression<T> expression) {
        return rxFrom(ExpressionBasedStreamId.of(expression));
    }

    default <T, E extends Expression<T>> Flowable<DetailedExpressionResult<T, E>> rxDetailedFrom(E expression) {
        return rxFrom(DetailedExpressionStreamId.of(expression));
    }

    default <T> Flowable<T> rxFrom(Expression<T> expression, ResolvingContext prefilledContext) {
        throw new UnsupportedOperationException();
        // return rxFrom(ExpressionBasedStreamId.of(expression, prefilledContext));
    }

    default <T, E extends Expression<T>> Flowable<DetailedExpressionResult<T, E>> rxDetailedFrom(E expression,
            ResolvingContext prefilledContext) {
        return rxFrom(DetailedExpressionStreamId.of(expression, prefilledContext));
    }

    /* DeprecatedAnalysisResult should become AnalysisResult */
    default Flowable<DeprecatedAnalysisResult> rxFrom(AnalysisModule<?> bufferedAnalysisModule) {
        return rxFrom(streamIdFor(bufferedAnalysisModule));
    }

    /* AssertionStatus should become AnalysisResult */
    default Flowable<DetailedExpressionResult<AssertionStatus, AnalysisExpression>> rxDetailedFrom(
            AnalysisModule<?> bufferedAnalysisModule) {
        throw new UnsupportedOperationException();
        // return rxFrom(DetailedExpressionStreamId.of(streamIdFor(bufferedAnalysisModule)));
    }

    /* DeprecatedAnalysisResult should become AnalysisResult */
    default Flowable<DeprecatedAnalysisResult> rxFrom(AnalysisModule<?> bufferedAnalysisModule,
            ResolvingContext prefilledContext) {
        throw new UnsupportedOperationException();
        // return rxFrom(streamIdFor(bufferedAnalysisModule));
    }

    /* AssertionStatus should become AnalysisResult */
    default Flowable<DetailedExpressionResult<AssertionStatus, AnalysisExpression>> rxDetailedFrom(
            AnalysisModule<?> bufferedAnalysisModule, ResolvingContext prefilledContext) {
        throw new UnsupportedOperationException();
        // return rxFrom(DetailedExpressionStreamId.of(streamIdFor(bufferedAnalysisModule)));
    }

}
