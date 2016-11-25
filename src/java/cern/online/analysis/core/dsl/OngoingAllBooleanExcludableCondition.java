/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import java.util.Set;

import org.tensorics.core.expressions.ConversionOperationExpression;
import org.tensorics.core.iterable.expressions.IterableExpressionToIterable;
import org.tensorics.core.tree.domain.Expression;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import cern.online.analysis.core.ConditionBuilder;

public class OngoingAllBooleanExcludableCondition {

    private static final AllOf ALL_OF = new AllOf();
    private final ConditionBuilder builder;
    private final Set<? extends Expression<Boolean>> sources;

    public OngoingAllBooleanExcludableCondition(ConditionBuilder builder, Set<? extends Expression<Boolean>> source) {
        super();
        this.builder = builder;
        this.sources = source;
    }

    @SafeVarargs
    public final OngoingAllBooleanExcludableCondition excluding(Expression<Boolean>... excludedExpressions) {
        return excluding(Sets.newHashSet(excludedExpressions));
    }

    public OngoingAllBooleanExcludableCondition excluding(Set<Expression<Boolean>> excludedExpressions) {
        return new OngoingAllBooleanExcludableCondition(builder,
                ImmutableSet.copyOf(Sets.difference(sources, excludedExpressions)));
    }

    public OngoingAllBooleanExcludableCondition withName(String name) {
        this.builder.withName(name);
        return this;
    }

    public OngoingAllBooleanExcludableCondition areTrue() {
        this.builder.withCondition(new ConversionOperationExpression<Iterable<Boolean>, Boolean>(ALL_OF,
                new IterableExpressionToIterable<>(sources)));
        return this;
    }

}
