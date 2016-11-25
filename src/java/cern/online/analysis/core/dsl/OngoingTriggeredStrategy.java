/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import static java.util.Objects.requireNonNull;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.ext.tensorics.evaluation.TriggeredEvaluation;

public class OngoingTriggeredStrategy {

    private final TriggeredEvaluation.Builder builder;

    public OngoingTriggeredStrategy(TriggeredEvaluation.Builder builder) {
        this.builder = requireNonNull(builder, "builder must not be null");
    }

    public void by(StreamId<?> triggeringStreamId) {
        this.builder.withTriggeringStreamId(triggeringStreamId);
    }

}
