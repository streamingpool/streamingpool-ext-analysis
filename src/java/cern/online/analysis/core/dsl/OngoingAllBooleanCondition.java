/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import org.tensorics.core.expressions.ConversionOperationExpression;
import org.tensorics.core.tree.domain.Expression;

import cern.online.analysis.core.ConditionBuilder;

public class OngoingAllBooleanCondition {

    private static final AllOf ALL_OF = new AllOf();
    private final ConditionBuilder builder;
    private final Expression<Iterable<Boolean>> sources;

    public OngoingAllBooleanCondition(ConditionBuilder builder, Expression<? extends Iterable<Boolean>> source) {
        super();
        this.builder = builder;
        @SuppressWarnings("unchecked")
        /* XXX this is not too nice... is there any better way?*/
        Expression<Iterable<Boolean>> castedSource = (Expression<Iterable<Boolean>>) source;
        this.sources = castedSource;
    }

    public OngoingAllBooleanCondition withName(String name) {
        this.builder.withName(name);
        return this;
    }

    public OngoingAllBooleanCondition areTrue() {
        this.builder.withCondition(new ConversionOperationExpression<Iterable<Boolean>, Boolean>(ALL_OF, sources));
        return this;
    }

}
