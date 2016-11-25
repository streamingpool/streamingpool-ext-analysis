/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import java.util.function.Predicate;

import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvedExpression;
import org.tensorics.expression.IsEqualToExpression;
import org.tensorics.expression.IsNotEqualExpression;
import org.tensorics.expression.PredicateExpression;

import cern.online.analysis.core.ConditionBuilder;

public class OngoingCondition<T> extends OngoingNamedCondition<T> {

    public OngoingCondition(ConditionBuilder builder, Expression<T> source) {
        super(builder, source);
    }

    public OngoingNamedCondition<T> is(Expression<Predicate<T>> predicate) {
        builder.withCondition(PredicateExpression.ofSourceAndPredicate(source, predicate));
        return this;
    }

    public OngoingNamedCondition<T> is(Predicate<T> predicate) {
        is(ResolvedExpression.of(predicate));
        return this;
    }

    public OngoingNamedCondition<T> isEqualTo(Expression<T> other) {
        this.builder.withCondition(new IsEqualToExpression<>(source, other));
        return this;
    }

    public OngoingNamedCondition<T> isNotEqualTo(Expression<T> other) {
        this.builder.withCondition(new IsNotEqualExpression<>(source, other));
        return this;
    }

    public OngoingNamedCondition<T> isEqualTo(T other) {
        isEqualTo(ResolvedExpression.of(other));
        return this;
    }

    public OngoingNamedCondition<T> isNotEqualTo(T other) {
        isNotEqualTo(ResolvedExpression.of(other));
        return this;
    }

}
