/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.function.BiPredicate;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.BufferSpecification.EndStreamMatcher;
import cern.streaming.pool.ext.tensorics.evaluation.BufferedEvaluation;

/**
 * Part of a fluent clause to tell the analysis module to work in buffereing mode. This class provides methods to
 * specify the stream id which will end the buffering.
 * 
 * @author kfuchsbe
 */
public class OngoingBufferedStrategyStarted<T> {

    private final BufferedEvaluation.Builder builder;

    public OngoingBufferedStrategyStarted(BufferedEvaluation.Builder builder) {
        this.builder = requireNonNull(builder, "builder must not be null");
    }

    public OngoingBufferedStrategyStarted<T> endedAfter(Duration timeout) {
        builder.withTimeout(timeout);
        return this;
    }

    public OngoingBufferedStrategyStarted<T> or() {
        return this;
    }

    public OngoingBufferedStrategyStarted<T> endedAfter(long time, TemporalUnit unit) {
        return endedAfter(Duration.of(time, unit));
    }

    /**
     * Used to specify the stream Id which will stop the buffering.
     * 
     * @param endStreamId the Id to stop the buffering
     */
    public OngoingBufferedStrategyStarted<T> endedBy(StreamId<?> endStreamId) {
        builder.withEndMatcher(EndStreamMatcher.endingOnEvery(endStreamId));
        return this;
    }

    public <U> OngoingBufferedStrategyStarted<T> endedBy(StreamId<U> endStreamId, BiPredicate<T, U> predicate) {
        builder.withEndMatcher(EndStreamMatcher.endingOnMatch(endStreamId, predicate));
        return this;
    }

}
