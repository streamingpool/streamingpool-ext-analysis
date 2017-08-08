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

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.stream.Collectors;

import org.streamingpool.ext.analysis.AnalysisDefinition;
import org.streamingpool.ext.analysis.AnalysisDefinitions;
import org.streamingpool.ext.analysis.AnalysisExpression;
import org.streamingpool.ext.analysis.AnalysisModulePreprocessor;
import org.streamingpool.ext.analysis.expression.AssertionExpression;
import org.streamingpool.ext.analysis.expression.AssertionGroupExpression;
import org.streamingpool.ext.analysis.modules.AnalysisModule;
import org.tensorics.core.tree.domain.Expression;

@Deprecated
public class DefaultAnalysisModulePreprocessor implements AnalysisModulePreprocessor {

    @Override
    public AnalysisDefinition process(AnalysisModule module) {
        return AnalysisDefinitions.process(module);
    }

}
