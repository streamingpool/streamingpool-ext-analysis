/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import org.tensorics.core.expressions.ConversionOperationExpression;
import org.tensorics.core.tree.domain.Expression;

import cern.online.analysis.core.ConditionBuilder;

public class OngoingAnyBooleanCondition {

    private final AnyOf ANY_OF = new AnyOf();
    private final ConditionBuilder builder;
    private final Expression<Iterable<Boolean>> sources;

    /**
     * @param builder
     * @param sources
     */
    public OngoingAnyBooleanCondition(ConditionBuilder builder, Expression<? extends Iterable<Boolean>> source) {
        super();
        this.builder = builder;
        @SuppressWarnings("unchecked")
        Expression<Iterable<Boolean>> castedSource = (Expression<Iterable<Boolean>>) source;
        this.sources = castedSource;

    }

    public OngoingAnyBooleanCondition withName(String name) {
        this.builder.withName(name);
        return this;
    }

    public OngoingAnyBooleanCondition isTrue() {
        
        this.builder.withCondition(new ConversionOperationExpression<Iterable<Boolean>, Boolean>(ANY_OF, sources));

        return this;
    }

}
