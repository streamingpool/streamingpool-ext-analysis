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

package org.streamingpool.ext.analysis;

import static io.reactivex.Flowable.interval;
import static io.reactivex.Flowable.just;
import static java.lang.Integer.MIN_VALUE;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.streamingpool.ext.analysis.AssertionStatus.ERROR;
import static org.streamingpool.ext.analysis.AssertionStatus.SUCCESSFUL;
import static org.streamingpool.ext.tensorics.expression.StreamIdBasedExpression.of;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.streamingpool.core.service.StreamFactoryRegistry;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.ext.analysis.modules.BufferedAnalysisModule;
import org.streamingpool.ext.analysis.modules.ContinuousAnalysisModule;
import org.streamingpool.ext.analysis.testing.AbstractAnalysisTest;
import org.streamingpool.ext.analysis.testing.RxAnalysisTestingSupport;
import org.streamingpool.ext.tensorics.expression.BufferedStreamExpression;
import org.streamingpool.ext.tensorics.streamfactory.BufferedTensoricsExpressionStreamFactory;
import org.tensorics.core.iterable.expressions.PickExpression;
import org.tensorics.core.resolve.engine.ResolvingEngine;

import io.reactivex.subscribers.TestSubscriber;

public class PickFromIterablesAnalysisTest extends AbstractAnalysisTest implements RxAnalysisTestingSupport {

    @Autowired
    private StreamFactoryRegistry factoryRegistry;

    @Autowired
    private ResolvingEngine engine;

    @Before
    public void setUp() {
        factoryRegistry.addIntercept(new BufferedTensoricsExpressionStreamFactory(engine));
    }

    @Test
    public void testPickExpressionOfIterable() throws Exception {
        StreamId<? extends Iterable<Boolean>> sourceId = provide(just(asList(false, true, false))).withUniqueStreamId();
        TestSubscriber<AnalysisResult> subscriber = new TestSubscriber<>();
        rxFrom(new ContinuousAnalysisModule() {
            {
                enabled().always();
                assertBoolean(true).isEqualTo(PickExpression.fromFirst(of(sourceId), 1));
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent(5, SECONDS);
        subscriber.assertComplete();
        assertThat(assertionsStatusesOf(subscriber)).containsOnly(SUCCESSFUL);
    }

    @Test
    public void testPickExpressionOfBufferedLiveStream() throws Exception {
        StreamId<Long> startBuffer = provide(interval(1, SECONDS)).withUniqueStreamId();
        StreamId<Boolean> booleanData = provide(interval(1, SECONDS).map(v -> true)).withUniqueStreamId();
        String label = "any";

        TestSubscriber<AnalysisResult> subscriber = new TestSubscriber<>();
        rxFrom(new BufferedAnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(startBuffer).endedAfter(Duration.ofSeconds(4));
                assertBoolean(true).isEqualTo(PickExpression.fromFirst(BufferedStreamExpression.buffer(booleanData), 2))
                        .withName(label);
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent();
        subscriber.assertComplete();
        assertThat(statusesOfAssertion(subscriber, label)).containsOnly(SUCCESSFUL);
    }

    @Test
    public void testPickExpressionOutOfBound() throws Exception {
        StreamId<? extends Iterable<Boolean>> sourceId = provide(just(asList(false, true, false))).withUniqueStreamId();
        String label = "any";
        TestSubscriber<AnalysisResult> subscriber = new TestSubscriber<>();
        rxFrom(new ContinuousAnalysisModule() {
            {
                enabled().always();
                assertBoolean(true).isEqualTo(PickExpression.fromFirst(of(sourceId), MIN_VALUE)).withName(label);
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent(5, SECONDS);
        subscriber.assertComplete();
        assertThat(statusesOfAssertion(subscriber, label)).containsOnly(ERROR);
    }

}
