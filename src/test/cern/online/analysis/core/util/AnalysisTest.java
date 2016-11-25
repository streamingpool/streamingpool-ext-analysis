/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.util;

import cern.online.analysis.core.AnalysisModule;
import cern.online.analysis.core.AnalysisStreamId;

public interface AnalysisTest {

    AnalysisStreamId analysisIdOf(AnalysisModule analysisModule);

}