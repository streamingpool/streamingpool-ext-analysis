/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.modules;

import org.streamingpool.ext.analysis.dsl.OngoingBufferedStrategy;
import org.streamingpool.ext.tensorics.evaluation.BufferedEvaluation;
import org.streamingpool.ext.tensorics.evaluation.BufferedEvaluation.Builder;

public abstract class BufferedAnalysisModule extends StreamBasedAnalysisModule<Builder> {

    public BufferedAnalysisModule() {
        super(BufferedEvaluation.builder());
    }

    protected OngoingBufferedStrategy buffered() {
        throwIfStrategySpecifiedTwice();
        return new OngoingBufferedStrategy(getEvaluationStrategyBuilder());
    }

}
