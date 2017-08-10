/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.modules;

import org.streamingpool.ext.tensorics.evaluation.ContinuousEvaluation;
import org.streamingpool.ext.tensorics.evaluation.ContinuousEvaluation.Builder;

public abstract class ContinuousAnalysisModule extends StreamBasedAnalysisModule<Builder> {

    public ContinuousAnalysisModule() {
        super(ContinuousEvaluation.builder());
    }

}
