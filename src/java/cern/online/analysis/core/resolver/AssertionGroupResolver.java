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

package cern.online.analysis.core.resolver;

import static cern.online.analysis.core.AssertionStatus.NONAPPLICABLE;
import static cern.online.analysis.core.AssertionStatus.SUCCESSFUL;
import static cern.online.analysis.core.AssertionStatus.fromBooleanSuccessful;
import static cern.online.analysis.core.util.Predicates.not;
import static org.tensorics.core.resolve.resolvers.Resolvers.contextResolvesAll;

import org.tensorics.core.resolve.resolvers.AbstractResolver;
import org.tensorics.core.tree.domain.ResolvingContext;

import cern.online.analysis.core.AssertionStatus;
import cern.online.analysis.core.expression.AssertionGroupExpression;

/**
 * Resolves an {@link AssertionGroupExpression} into a {@link AssertionStatus}.
 * <p>
 * The status will be {@link AssertionStatus#SUCCESSFUL} if only if all the assertions in the group are
 * {@link AssertionStatus#SUCCESSFUL}. Otherwise, the status will be {@link AssertionStatus#FAILURE}.
 * 
 * @see AssertionGroupExpression
 * @author acalia, caguiler, kfuchsberger
 */
public class AssertionGroupResolver extends AbstractResolver<AssertionStatus, AssertionGroupExpression> {

    @Override
    public boolean canResolve(AssertionGroupExpression assertionSet, ResolvingContext context) {
        return contextResolvesAll(assertionSet.getChildren(), context);
    }

    @Override
    public AssertionStatus resolve(AssertionGroupExpression assertionSet, ResolvingContext context) {
        return fromBooleanSuccessful(assertionSet.getChildren().stream().map(context::resolvedValueOf)
                .filter(not(NONAPPLICABLE::equals)).allMatch(SUCCESSFUL::equals));
    }

}
