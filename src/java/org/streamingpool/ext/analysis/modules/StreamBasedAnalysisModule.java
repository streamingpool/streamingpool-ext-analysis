/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.modules;

import java.util.concurrent.atomic.AtomicBoolean;

import org.streamingpool.ext.tensorics.evaluation.EvaluationStrategy;
import org.streamingpool.ext.tensorics.evaluation.EvaluationStrategyBuilder;
import org.tensorics.core.analysis.AnalysisModule;

public abstract class StreamBasedAnalysisModule<T extends EvaluationStrategyBuilder> extends AnalysisModule{

    private final AtomicBoolean strategySpecified = new AtomicBoolean(false);
    private final T evaluationStrategyBuilder;

    protected StreamBasedAnalysisModule(T evaluationStrategyBuilder) {
        this.evaluationStrategyBuilder = evaluationStrategyBuilder;
    }

    public EvaluationStrategy buildEvaluationStrategy() {
        return getEvaluationStrategyBuilder().build();
    }

    protected void throwIfStrategySpecifiedTwice() {
        if (strategySpecified.getAndSet(true)) {
            throw new IllegalStateException(
                    "It is only allowed to specify once either triggered() or buffered() within the same analysis module. "
                            + "It seems that you tried to specify both or one twice.");
        }
    }

    protected T getEvaluationStrategyBuilder() {
        return evaluationStrategyBuilder;
    }
}
