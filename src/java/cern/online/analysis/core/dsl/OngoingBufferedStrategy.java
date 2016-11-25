/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import static java.util.Objects.requireNonNull;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.ext.tensorics.evaluation.BufferedEvaluation;

public class OngoingBufferedStrategy {

    private final BufferedEvaluation.Builder builder;

    public OngoingBufferedStrategy(BufferedEvaluation.Builder builder) {
        this.builder = requireNonNull(builder, "builder must not be null");
    }

    public <T> OngoingBufferedStrategyStarted<T> startedBy(StreamId<T> startStreamId) {
        return new OngoingBufferedStrategyStarted<>(builder.withStartStreamId(startStreamId));
    }

}
