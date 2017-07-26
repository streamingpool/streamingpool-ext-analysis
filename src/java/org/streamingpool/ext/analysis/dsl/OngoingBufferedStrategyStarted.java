// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
* 
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
// @formatter:on

package org.streamingpool.ext.analysis.dsl;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.function.BiPredicate;

import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.BufferSpecification.EndStreamMatcher;
import org.streamingpool.ext.tensorics.evaluation.BufferedEvaluation;

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
    public OngoingBufferedStrategyStarted<T> endedOnEvery(StreamId<?> endStreamId) {
        builder.withEndMatcher(EndStreamMatcher.endingOnEvery(endStreamId));
        return this;
    }

    public <U> OngoingBufferedStrategyStarted<T> endedBy(StreamId<U> endStreamId, BiPredicate<T, U> predicate) {
        builder.withEndMatcher(EndStreamMatcher.endingOnMatch(endStreamId, predicate));
        return this;
    }
    
    public <U> OngoingBufferedStrategyStarted<T> endedOnMatch(StreamId<U> endStreamId) {
        builder.withEndMatcher(EndStreamMatcher.endingOnMatch(endStreamId, (start, end) -> start.equals(end)));
        return this;
    }

}
