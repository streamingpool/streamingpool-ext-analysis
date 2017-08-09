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
import org.streamingpool.ext.analysis.AnalysisDefinitions;
import org.streamingpool.ext.analysis.AnalysisStreamId;
import org.streamingpool.ext.analysis.DeprecatedAnalysisResult;
import org.streamingpool.ext.analysis.conf.AnalysisConfiguration;
import org.streamingpool.ext.analysis.conf.AnalysisResolvingEngineConfiguration;
import org.streamingpool.ext.analysis.expression.AnalysisExpression;
import org.streamingpool.ext.analysis.modules.StreamBaseAnalysisModule;
import org.streamingpool.ext.tensorics.conf.DefaultResolvingEngineConfiguration;
import org.tensorics.core.resolve.engine.ResolvingEngine;

@ContextConfiguration(classes = { AnalysisConfiguration.class, AnalysisResolvingEngineConfiguration.class,
        DefaultResolvingEngineConfiguration.class })
public abstract class AbstractAnalysisTest extends AbstractStreamTest implements AnalysisTest {

    @Autowired
    private ResolvingEngine engine;

    @Override
    public AnalysisStreamId analysisIdOf(StreamBaseAnalysisModule<?> analysisModule) {
        return AnalysisDefinitions.streamIdFor(analysisModule);
    }

    @Override
    public DeprecatedAnalysisResult  resolveAnalysisModule(StreamBaseAnalysisModule<?> analysisModule) {
        AnalysisExpression rootExpression = AnalysisDefinitions.assertionSetFrom(analysisModule);
        return DeprecatedAnalysisResult.fromResult(engine.resolveDetailed(rootExpression));
    }

}
