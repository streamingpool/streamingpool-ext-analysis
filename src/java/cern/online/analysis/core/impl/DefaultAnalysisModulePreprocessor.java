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

package cern.online.analysis.core.impl;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import org.tensorics.core.tree.domain.Expression;

import cern.online.analysis.core.AnalysisDefinition;
import cern.online.analysis.core.AnalysisExpression;
import cern.online.analysis.core.AnalysisModule;
import cern.online.analysis.core.AnalysisModulePreprocessor;
import cern.online.analysis.core.expression.AssertionExpression;
import cern.online.analysis.core.expression.AssertionGroupExpression;

public class DefaultAnalysisModulePreprocessor implements AnalysisModulePreprocessor {

    @Override
    public AnalysisDefinition process(AnalysisModule module) {
        AssertionGroupExpression assertionSet = assertionSetFrom(module);
        Expression<Boolean> enablerExpression = enablerExpressionFrom(module);
        return new AnalysisDefinition(AnalysisExpression.of(assertionSet, enablerExpression),
                module.evaluationStrategy());
    }

    private AssertionGroupExpression assertionSetFrom(AnalysisModule module) {
        return module.assertionBuilders().stream().map(AssertionExpression::new)
                .collect(collectingAndThen(toList(), AssertionGroupExpression::new));
    }

    private Expression<Boolean> enablerExpressionFrom(AnalysisModule module) {
        return module.enablingBuilder().build();
    }
}
