/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.modules;

import org.streamingpool.ext.analysis.dsl.OngoingTriggeredStrategy;
import org.streamingpool.ext.tensorics.evaluation.TriggeredEvaluation;
import org.streamingpool.ext.tensorics.evaluation.TriggeredEvaluation.Builder;

public abstract class TriggeredAnalysisModule extends StreamBasedAnalysisModule<Builder> {

    public TriggeredAnalysisModule() {
        super(TriggeredEvaluation.builder());
    }

    public OngoingTriggeredStrategy triggered() {
        throwIfStrategySpecifiedTwice();
        return new OngoingTriggeredStrategy(getEvaluationStrategyBuilder());
    }

}
