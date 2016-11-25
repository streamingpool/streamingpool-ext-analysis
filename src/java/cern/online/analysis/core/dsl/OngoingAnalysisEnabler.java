/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import static java.util.Objects.requireNonNull;

import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvedExpression;

import cern.online.analysis.core.ConditionBuilder;

public class OngoingAnalysisEnabler {

    private final ConditionBuilder enablerBuilder;

    public OngoingAnalysisEnabler(ConditionBuilder enablerBuilder) {
        this.enablerBuilder = requireNonNull(enablerBuilder, "builder must not be null");
    }

    public <T> OngoingCondition<T> when(Expression<T> source) {
        return new OngoingCondition<>(enablerBuilder, source);
    }

    public OngoingBooleanCondition whenBoolean(Expression<Boolean> source) {
        return new OngoingBooleanCondition(enablerBuilder, source);
    }

    public void always() {
        this.enablerBuilder.withName("always evaluated").withCondition(ResolvedExpression.of(true));
    }

}
