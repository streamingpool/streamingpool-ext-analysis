/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.resolver;

import static cern.online.analysis.core.AssertionStatus.NONAPPLICABLE;
import static cern.online.analysis.core.AssertionStatus.fromBooleanSuccessful;

import org.tensorics.core.resolve.resolvers.AbstractResolver;
import org.tensorics.core.resolve.resolvers.Resolvers;
import org.tensorics.core.tree.domain.ResolvingContext;

import cern.online.analysis.core.AssertionStatus;
import cern.online.analysis.core.expression.AssertionExpression;

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
