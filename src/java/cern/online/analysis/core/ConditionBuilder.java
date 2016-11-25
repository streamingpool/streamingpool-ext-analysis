/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import static java.util.Objects.requireNonNull;

import org.tensorics.core.tree.domain.Expression;

public class ConditionBuilder {

    private String name;
    private Expression<Boolean> condition;

    public ConditionBuilder withName(String newName) {
        this.name = requireNonNull(newName, "name must not be null");
        return this;
    }

    public <T extends ConditionBuilder> T withCondition(Expression<Boolean> newCondition) {
        requireNonNull(newCondition, "condition must not be null");
        this.condition = newCondition;
        @SuppressWarnings("unchecked")
        T conditionBuilderOrSubclass = (T) this;
        return conditionBuilderOrSubclass;
    }

    public String name() {
        return name;
    }

    public Expression<Boolean> condition() {
        return condition;
    }

}