/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.support;

import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.ext.tensorics.streamid.ExpressionBasedStreamId;
import org.tensorics.core.tree.domain.Expression;

import io.reactivex.Flowable;

public interface RxAnalysisSupport extends RxStreamSupport {

    default <T> Flowable<T> rxFrom(Expression<T> expression) {
        return rxFrom(ExpressionBasedStreamId.of(expression));
    }

}
