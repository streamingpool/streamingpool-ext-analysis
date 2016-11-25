/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

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
