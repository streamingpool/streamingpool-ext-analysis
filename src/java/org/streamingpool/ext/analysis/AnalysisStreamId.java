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

package org.streamingpool.ext.analysis;

import static java.util.Arrays.asList;
import static org.tensorics.core.tree.domain.Contexts.mergeContextsOrdered;

import org.streamingpool.core.service.streamid.DerivedStreamId;
import org.streamingpool.ext.tensorics.streamid.DetailedExpressionStreamId;
import org.tensorics.core.analysis.AnalysisResult;
import org.tensorics.core.analysis.expression.AnalysisExpression;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;
import org.tensorics.core.tree.domain.ResolvingContext;

/**
 * Specialization of a {@link DetailedExpressionStreamId} specifically for the analysis framework
 *
 * @author acalia
 */
@Deprecated
public class AnalysisStreamId
        extends DerivedStreamId<DetailedExpressionResult<AnalysisResult, AnalysisExpression>, AnalysisResult> {
    private static final long serialVersionUID = 1L;

    public AnalysisStreamId(AnalysisDefinition analysisDefinition) {
        super(DetailedExpressionStreamId.of(analysisDefinition.expression(), analysisDefinition.initalContext()),
                d -> d.value());
    }

    public AnalysisStreamId(AnalysisDefinition analysisDefinition, ResolvingContext initialContext) {
        super(DetailedExpressionStreamId.of(analysisDefinition.expression(),
                mergeContextsOrdered(asList(initialContext, analysisDefinition.initalContext()))), d -> d.value());
    }
}
