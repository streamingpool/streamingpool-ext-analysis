/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import org.tensorics.core.tree.domain.Expression;

public class EnablingConditionBuilder extends ConditionBuilder {

    public Expression<Boolean> build() {
        Expression<Boolean> condition = super.condition();
        if (condition == null) {
            throw new IllegalStateException(
                    "Condition is not specified! Check the enabled() clause in analysis module for completeness!");
        }
        return condition;
    }

}
