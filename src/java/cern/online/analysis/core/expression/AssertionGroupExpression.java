/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.expression;

import java.util.Collection;
import java.util.List;

import org.tensorics.core.tree.domain.AbstractDeferredExpression;

import com.google.common.collect.ImmutableList;

import cern.online.analysis.core.AssertionStatus;
import cern.online.analysis.core.resolver.AssertionGroupResolver;

/**
 * A group of assertions that are evaluated altogether. The strategy is defined in the {@link AssertionGroupResolver}.
 * 
 * @see AssertionGroupResolver
 * @author acalia, caguiler, kfuchsberger
 */
public class AssertionGroupExpression extends AbstractDeferredExpression<AssertionStatus> {
    
    private final List<AssertionExpression> assertions;

    public AssertionGroupExpression(Collection<AssertionExpression> assertions) {
        this.assertions = ImmutableList.copyOf(assertions);
    }

    @Override
    public final List<AssertionExpression> getChildren() {
        return assertions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((assertions == null) ? 0 : assertions.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AssertionGroupExpression other = (AssertionGroupExpression) obj;
        if (assertions == null) {
            if (other.assertions != null) {
                return false;
            }
        } else if (!assertions.equals(other.assertions)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AssertionGroupExpression [assertions=" + assertions + "]";
    }

}
