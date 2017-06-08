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

package org.streamingpool.ext.analysis.expression;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.streamingpool.ext.analysis.AssertionBuilder;
import org.streamingpool.ext.analysis.AssertionStatus;
import org.streamingpool.ext.analysis.resolver.AssertionResolver;
import org.tensorics.core.tree.domain.AbstractDeferredExpression;
import org.tensorics.core.tree.domain.ExceptionHandlingNode;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.expression.CombinedBooleanExpression;
import org.tensorics.expression.IterableResolvingExpression;

import com.google.common.collect.ImmutableList;

/**
 * {@link Expression} that resolves to a {@link AssertionStatus}. An {@link AssertionExpression} is composed by a
 * condition expression and a precondition expression. The precondition specifies if it makes sense to evaluate the
 * {@link AssertionExpression}.
 * 
 * @see AssertionResolver
 * @author acalia, caguiler, kfuchsbe
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
