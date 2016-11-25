/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.util;

import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import cern.online.analysis.core.AnalysisDefinition;
import cern.online.analysis.core.AnalysisModule;
import cern.online.analysis.core.AnalysisModulePreprocessor;
import cern.online.analysis.core.AnalysisStreamId;
import cern.online.analysis.core.conf.AnalysisConfiguration;
import cern.streaming.pool.core.testing.AbstractStreamTest;

@Ignore
@ContextConfiguration(classes = { AnalysisConfiguration.class, AnalysisTestConfiguration.class })
public class AbstractAnalysisTest extends AbstractStreamTest implements AnalysisTest {

    @Autowired
    AnalysisModulePreprocessor modulePreprocessor;

    @Override
    public AnalysisStreamId analysisIdOf(AnalysisModule analysisModule) {
        AnalysisDefinition analysisDefinition = modulePreprocessor.process(analysisModule);
        return new AnalysisStreamId(analysisDefinition);
    }

}
