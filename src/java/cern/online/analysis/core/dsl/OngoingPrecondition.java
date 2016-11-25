/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvedExpression;
import org.tensorics.expression.IsEqualToExpression;

import cern.online.analysis.core.AssertionBuilder;

public class OngoingPrecondition<T> {

    private final AssertionBuilder builder;
    private final Expression<T> source;

    public OngoingPrecondition(AssertionBuilder builder, Expression<T> whenSource) {
        this.builder = requireNonNull(builder, "builder must not be null");
        this.source = requireNonNull(whenSource, "source must not be null");
    }

    public OngoingPrecondition(AssertionBuilder builder, T source) {
        this(builder, ResolvedExpression.of(source));
    }

    public OngoingPrecondition<T> isEqualTo(Expression<T> other) {
        this.builder.withPreCondition(new IsEqualToExpression<>(source, other));
        return this;
    }

    public OngoingPrecondition<T> isEqualTo(T other) {
        return isEqualTo(ResolvedExpression.of(other));
    }

    public OngoingPrecondition<T> or() {
        this.builder.withPreConditionReducer(new AnyOf());
        return this;
    }

    public final <T1> OngoingCondition<T1> thenAssertThat(Expression<T1> thatSource) {
        return new OngoingCondition<>(builder, thatSource);
    }

    public final <T1> OngoingCondition<T1> thenAssertThat(T1 thatSource) {
        return thenAssertThat(ResolvedExpression.of(thatSource));
    }

    public final OngoingBooleanCondition thenAssertBoolean(Expression<Boolean> thatSource) {
        return new OngoingBooleanCondition(builder, thatSource);
    }

    public final OngoingAllBooleanExcludableCondition thenAssertAllBoolean(Set<? extends Expression<Boolean>> thatSource) {
        return new OngoingAllBooleanExcludableCondition(builder, thatSource);
    }
    
    public final OngoingAnyBooleanCondition thenAssertAtLeastOneBooleanOf(Expression<? extends Iterable<Boolean>> thatSource) {
        return new OngoingAnyBooleanCondition(builder, thatSource);
    }

    public final OngoingBooleanCondition thenAssertBoolean(Boolean thatSource) {
        return thenAssertBoolean(ResolvedExpression.of(thatSource));
    }

}
