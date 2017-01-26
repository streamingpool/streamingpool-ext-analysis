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

package org.streamingpool.ext.analysis.resolver;

import static org.streamingpool.ext.analysis.AssertionStatus.NONAPPLICABLE;
import static org.streamingpool.ext.analysis.AssertionStatus.fromBooleanSuccessful;

import org.streamingpool.ext.analysis.AssertionStatus;
import org.streamingpool.ext.analysis.expression.AssertionExpression;
import org.tensorics.core.resolve.resolvers.AbstractResolver;
import org.tensorics.core.resolve.resolvers.Resolvers;
import org.tensorics.core.tree.domain.ResolvingContext;

/**
 * Resolves an {@link AssertionExpression} into a {@link AssertionStatus}.
 * 
 * @see AssertionExpression
 * @author acalia, caguiler, kfuchsberger
 */
public class AssertionResolver extends AbstractResolver<AssertionStatus, AssertionExpression> {

    @Override
    public boolean canResolve(AssertionExpression expression, ResolvingContext context) {
        return Resolvers.contextResolvesAll(expression.getChildren(), context);
    }

    @Override
    public AssertionStatus resolve(AssertionExpression assertion, ResolvingContext context) {
        if (!context.resolvedValueOf(assertion.preConditionsExpression())) {
            return NONAPPLICABLE;
        }

        return fromBooleanSuccessful(context.resolvedValueOf(assertion.condition()));
    }

}
