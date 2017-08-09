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

package org.streamingpool.ext.analysis.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.streamingpool.ext.analysis.AnalysisDefinitions;
import org.streamingpool.ext.analysis.expression.AnalysisExpression;
import org.streamingpool.ext.analysis.modules.ContinuousAnalysisModule;
import org.streamingpool.ext.analysis.modules.StreamBaseAnalysisModule;

public class DefaultAnalysisModulePreprocessorTest {

    private static final StreamBaseAnalysisModule<?> EMPTY_MODULE = new ContinuousAnalysisModule() {
        /* empty on purpose */
    };

    @Test
    public void emptyModuleIsCorrectlyProcessed() {
        AnalysisExpression result = AnalysisDefinitions.process(EMPTY_MODULE).expression();
        assertThat(result).isInstanceOf(AnalysisExpression.class);
        assertThat(result.getChildren()).isEmpty();
    }

}
