// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
*
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
// @formatter:on

package org.streamingpool.ext.analysis.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.streamingpool.core.testing.AbstractStreamTest;
import org.streamingpool.ext.analysis.AnalysisDefinition;
import org.streamingpool.ext.analysis.AnalysisExpression;
import org.streamingpool.ext.analysis.AnalysisModule;
import org.streamingpool.ext.analysis.AnalysisModulePreprocessor;
import org.streamingpool.ext.analysis.AnalysisResult;
import org.streamingpool.ext.analysis.AnalysisStreamId;
import org.streamingpool.ext.analysis.conf.AnalysisConfiguration;
import org.streamingpool.ext.analysis.conf.AnalysisResolvingEngineConfiguration;
import org.tensorics.core.resolve.engine.ResolvingEngine;

@ContextConfiguration(classes = { AnalysisConfiguration.class, AnalysisResolvingEngineConfiguration.class, AnalysisTestConfiguration.class })
public abstract class AbstractAnalysisTest extends AbstractStreamTest implements AnalysisTest {

    @Autowired
    private AnalysisModulePreprocessor modulePreprocessor;

    @Autowired
    private ResolvingEngine engine;
    
    @Override
    public AnalysisStreamId analysisIdOf(AnalysisModule analysisModule) {
        AnalysisDefinition analysisDefinition = modulePreprocessor.process(analysisModule);
        return new AnalysisStreamId(analysisDefinition);
    }

    @Override
    public AnalysisResult resolveAnalysisModule(AnalysisModule analysisModule) {
        AnalysisDefinition definition = modulePreprocessor.process(analysisModule);
        AnalysisExpression rootExpression = definition.expression();
        return AnalysisResult.fromResult(engine.resolveDetailed(rootExpression));
    }

}
