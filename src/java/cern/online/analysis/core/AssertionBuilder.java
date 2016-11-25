/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import org.tensorics.core.tree.domain.Expression;

import com.google.common.collect.ImmutableList;

import cern.online.analysis.core.dsl.AllOf;
import cern.online.analysis.core.dsl.IterableBooleanConversion;

public class AssertionBuilder extends ConditionBuilder {
    public static final IterableBooleanConversion DEFAULT_CONDITIONS_REDUCER = new AllOf();

    private final ImmutableList.Builder<Expression<Boolean>> preConditions = ImmutableList.builder();
    private IterableBooleanConversion preConditionsReducer = DEFAULT_CONDITIONS_REDUCER;

    public AssertionBuilder withPreConditionReducer(IterableBooleanConversion newPreConditionsConversion) {
        requireNonNull(newPreConditionsConversion, "preConditionsConversion must not be null");
        this.preConditionsReducer = newPreConditionsConversion;
        return this;
    }

    public AssertionBuilder withPreCondition(Expression<Boolean> preCondition) {
        requireNonNull(preCondition, "preCondition must not be null");
        this.preConditions.add(preCondition);
        return this;
    }

    public Collection<Expression<Boolean>> preConditions() {
        return preConditions.build();
    }

    public IterableBooleanConversion preConditionsReducer() {
        return preConditionsReducer;
    }

}
