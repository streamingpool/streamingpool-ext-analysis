/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import org.tensorics.core.tree.domain.Expression;

import cern.online.analysis.core.ConditionBuilder;

public class OngoingBooleanCondition extends OngoingCondition<Boolean> {

    public OngoingBooleanCondition(ConditionBuilder builder, Expression<Boolean> source) {
        super(builder, source);
    }

    public OngoingNamedCondition<Boolean> isTrue() {
        isEqualTo(true);
        return this;
    }

    public OngoingNamedCondition<Boolean> isFalse() {
        isEqualTo(false);
        return this;
    }
}
