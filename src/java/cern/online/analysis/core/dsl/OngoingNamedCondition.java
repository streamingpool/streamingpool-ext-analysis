/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import static java.util.Objects.requireNonNull;

import org.tensorics.core.tree.domain.Expression;

import cern.online.analysis.core.ConditionBuilder;

public class OngoingNamedCondition<T> {

    protected final ConditionBuilder builder;
    protected final Expression<T> source;

    public OngoingNamedCondition(ConditionBuilder builder, Expression<T> source) {
        this.builder = requireNonNull(builder, "builder must not be null");
        this.source = requireNonNull(source, "source must not be null");
    }

    public OngoingNamedCondition<T> withName(String name) {
        this.builder.withName(name);
        return this;
    }

}