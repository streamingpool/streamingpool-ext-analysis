/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.expression;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.tensorics.core.tree.domain.AbstractDeferredExpression;
import org.tensorics.core.tree.domain.ExceptionHandlingNode;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.expression.CombinedBooleanExpression;
import org.tensorics.expression.IterableResolvingExpression;

import com.google.common.collect.ImmutableList;

import cern.online.analysis.core.AssertionBuilder;
import cern.online.analysis.core.AssertionStatus;
import cern.online.analysis.core.resolver.AssertionResolver;

/**
 * {@link Expression} that resolves to a {@link AssertionStatus}. An {@link AssertionExpression} is composed by a
 * condition expression and a precondition expression. The precondition specifies if it makes sense to evaluate the
 * {@link AssertionExpression}.
 * 
 * @see AssertionResolver
 * @author acalia, caguiler, kfuchsberger
 */
public class AssertionExpression extends AbstractDeferredExpression<AssertionStatus>
        implements ExceptionHandlingNode<AssertionStatus> {

    private final String name;
    private final Expression<Boolean> condition;
    private final Expression<Boolean> preConditionsExpression;

    public AssertionExpression(AssertionBuilder builder) {
        requireNonNull(builder.condition(), "conditions must not be null");
        requireNonNull(builder.preConditions(), "preconditions must not be null");
        requireNonNull(builder.preConditionsReducer(), "preConditionsCollector must not be null");
        this.name = builder.name();
        this.condition = builder.condition();
        this.preConditionsExpression = new CombinedBooleanExpression(builder.preConditionsReducer(),
                new IterableResolvingExpression<Boolean>(builder.preConditions()));
    }

    @Override
    public List<Expression<Boolean>> getChildren() {
        return ImmutableList.of(condition, preConditionsExpression);
    }

    public Expression<Boolean> condition() {
        return this.condition;
    }

    public Expression<Boolean> preConditionsExpression() {
        return this.preConditionsExpression;
    }

    public String name() {
        return this.name;
    }

    @Override
    public AssertionStatus handle(Exception exception) {
        return AssertionStatus.ERROR;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((condition == null) ? 0 : condition.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        AssertionExpression other = (AssertionExpression) obj;
        if (condition == null) {
            if (other.condition != null) {
                return false;
            }
        } else if (!condition.equals(other.condition)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AssertionExpression [name=" + name + ", condition=" + condition + ", preConditionsExpression="
                + preConditionsExpression + "]";
    }

}
