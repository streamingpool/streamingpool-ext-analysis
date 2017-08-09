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

import static org.assertj.core.api.Assertions.assertThat;
import static org.streamingpool.ext.analysis.AnalysisDefinitions.streamIdFor;
import static org.streamingpool.ext.analysis.AssertionStatus.SUCCESSFUL;
import static org.streamingpool.ext.tensorics.expression.BufferedStreamExpression.buffer;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.streamingpool.core.service.StreamFactoryRegistry;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.DelayedStreamId;
import org.streamingpool.ext.analysis.modules.BufferedAnalysisModule;
import org.streamingpool.ext.analysis.testing.AbstractAnalysisTest;
import org.streamingpool.ext.analysis.testing.RxAnalysisTestingSupport;
import org.streamingpool.ext.tensorics.expression.BufferedStreamExpression;
import org.streamingpool.ext.tensorics.streamfactory.BufferedTensoricsExpressionStreamFactory;
import org.tensorics.core.resolve.engine.ResolvingEngine;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;

public class BufferedAnalysisTest extends AbstractAnalysisTest implements RxAnalysisTestingSupport {

    @Autowired
    private StreamFactoryRegistry factoryRegistry;

    @Autowired
    private ResolvingEngine engine;

    @Before
    public void setUp() {
        factoryRegistry.addIntercept(new BufferedTensoricsExpressionStreamFactory(engine));
    }

    @Test
    public void testFullAnalysis() {
        PublishProcessor<String> startStream = PublishProcessor.create();
        PublishProcessor<String> endStream = PublishProcessor.create();
        PublishProcessor<Boolean> sourceStream = PublishProcessor.create();

        StreamId<String> startStreamId = provide(startStream).withUniqueStreamId();
        StreamId<String> endStreamId = provide(endStream).withUniqueStreamId();
        StreamId<Boolean> sourceStreamId = provide(sourceStream.onBackpressureBuffer()).withUniqueStreamId();

        final String ASSERTION_NAME = "name";
        final BufferedStreamExpression<Boolean> BUFFERED_SOURCE = BufferedStreamExpression.buffer(sourceStreamId);

        Flowable<DeprecatedAnalysisResult> resultStream = rxFrom(streamIdFor(new BufferedAnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(startStreamId).endedOnMatch(endStreamId);
                assertAllBoolean(BUFFERED_SOURCE).areTrue().withName(ASSERTION_NAME);
            }
        }));

        TestSubscriber<DeprecatedAnalysisResult> testSubscriber = resultStream.test();

        startStream.onNext("A");
        await();

        sourceStream.onNext(true);
        sourceStream.onNext(false);
        sourceStream.onNext(true);

        await();
        endStream.onNext("A");

        DeprecatedAnalysisResult analysisResult = testSubscriber.awaitCount(1).assertValueCount(1).values().get(0);

        assertThat(statusOfAssertion(analysisResult, ASSERTION_NAME)).isEqualTo(AssertionStatus.FAILURE);
        assertThat(analysisResult.resolvedValueOf(BUFFERED_SOURCE)).containsExactly(true, false,
                true);
    }

    @Test
    public void testEndStreamIsUsedAsBufferedInAnalysis() {
        PublishProcessor<String> startStream = PublishProcessor.create();
        PublishProcessor<String> endStream = PublishProcessor.create();

        StreamId<String> startStreamId = provide(startStream).withUniqueStreamId();
        StreamId<String> endStreamId = provide(endStream).withUniqueStreamId();

        final String END_VALUE = "end";
        final String ASSERTION_NAME = "name";
        final BufferedStreamExpression<String> BUFFERED_END = BufferedStreamExpression.buffer(endStreamId);

        Flowable<DeprecatedAnalysisResult> resultStream = rxFrom(streamIdFor(new BufferedAnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(startStreamId)
                        .endedOnEvery(DelayedStreamId.delayBy(endStreamId, Duration.ofSeconds(1)));
                assertThat(BUFFERED_END).is(buffer -> buffer.contains(END_VALUE)).withName(ASSERTION_NAME);
            }
        }));

        TestSubscriber<DeprecatedAnalysisResult> testSubscriber = resultStream.test();

        startStream.onNext("any");
        await();

        await();
        endStream.onNext(END_VALUE);

        DeprecatedAnalysisResult analysisResult = testSubscriber.awaitCount(1).assertValueCount(1).values().get(0);

        assertThat(statusOfAssertion(analysisResult, ASSERTION_NAME)).isEqualTo(AssertionStatus.SUCCESSFUL);
        assertThat(analysisResult.resolvedValueOf(BUFFERED_END)).containsExactly(END_VALUE);
    }

    @Test
    public void testOverlappingAnalyses() {
        PublishProcessor<String> startStream = PublishProcessor.create();
        PublishProcessor<String> endStream = PublishProcessor.create();
        PublishProcessor<Boolean> sourceStream = PublishProcessor.create();

        StreamId<String> startStreamId = provide(startStream).withUniqueStreamId();
        StreamId<String> endStreamId = provide(endStream).withUniqueStreamId();
        StreamId<Boolean> sourceStreamId = provide(sourceStream.onBackpressureBuffer()).withUniqueStreamId();

        final String ASSERTION_NAME = "name";
        final BufferedStreamExpression<Boolean> BUFFERED_SOURCE = BufferedStreamExpression.buffer(sourceStreamId);

        Flowable<DeprecatedAnalysisResult> resultStream = rxFrom(streamIdFor(new BufferedAnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(startStreamId).endedOnMatch(endStreamId);
                assertAllBoolean(BUFFERED_SOURCE).areTrue().withName(ASSERTION_NAME);
            }
        }));

        TestSubscriber<DeprecatedAnalysisResult> testSubscriber = resultStream.test();

        startStream.onNext("A");
        await();
        startStream.onNext("C");
        await();

        sourceStream.onNext(true);
        sourceStream.onNext(false);

        await();
        startStream.onNext("B");
        await();

        sourceStream.onNext(true);

        await();
        endStream.onNext("A");
        await();

        sourceStream.onNext(true);
        sourceStream.onNext(true);
        sourceStream.onNext(true);

        await();
        endStream.onNext("B");
        await();

        sourceStream.onNext(true);

        await();
        endStream.onNext("C");

        List<DeprecatedAnalysisResult> analysisResults = testSubscriber.awaitCount(3).assertValueCount(3).values();

        DeprecatedAnalysisResult resultA = analysisResults.get(0);
        DeprecatedAnalysisResult resultB = analysisResults.get(1);
        DeprecatedAnalysisResult resultC = analysisResults.get(2);
        /* analysis A */
        assertThat(statusOfAssertion(resultA, ASSERTION_NAME)).isEqualTo(AssertionStatus.FAILURE);
        assertThat(resultA.resolvedValueOf(BUFFERED_SOURCE)).containsExactly(true, false, true);
        /* analysis B */
        assertThat(statusOfAssertion(resultB, ASSERTION_NAME)).isEqualTo(AssertionStatus.SUCCESSFUL);
        assertThat(resultB.resolvedValueOf(BUFFERED_SOURCE)).containsExactly(true, true, true, true);
        /* analysis C */
        assertThat(statusOfAssertion(resultC, ASSERTION_NAME)).isEqualTo(AssertionStatus.FAILURE);
        assertThat(resultC.resolvedValueOf(BUFFERED_SOURCE)).containsExactly(true, false, true, true,
                true, true, true);
    }

    private void await() {
        await(200);
    }

    private void await(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            /* */
        }
    }

    @Test
    public void testEndedByTimeout() throws Exception {
        PublishProcessor<String> startStream = PublishProcessor.create();
        PublishProcessor<Boolean> sourceStream = PublishProcessor.create();

        StreamId<String> startStreamId = provide(startStream).withUniqueStreamId();
        StreamId<Boolean> sourceStreamId = provide(sourceStream.onBackpressureBuffer()).withUniqueStreamId();

        final BufferedStreamExpression<Boolean> SOURCE_EXPRESSION = buffer(sourceStreamId);
        final String ASSERTION_NAME = "any name";
        final int ANALYSIS_TIMEOUT_MS = 2_000;

        TestSubscriber<DeprecatedAnalysisResult> subscriber = new TestSubscriber<>();
        rxFrom(streamIdFor(new BufferedAnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(startStreamId).endedAfter(Duration.ofMillis(ANALYSIS_TIMEOUT_MS));
                assertAllBoolean(SOURCE_EXPRESSION).areTrue().withName(ASSERTION_NAME);
            }
        })).subscribe(subscriber);

        startStream.onNext("A");
        await();

        sourceStream.onNext(true);
        sourceStream.onNext(true);

        await(ANALYSIS_TIMEOUT_MS);

        DeprecatedAnalysisResult result = subscriber.awaitCount(1).assertValueCount(1).values().get(0);

        assertThat(result.resolvedValueOf(SOURCE_EXPRESSION)).containsExactly(true, true);
        assertThat(statusOfAssertion(result, ASSERTION_NAME)).isEqualTo(SUCCESSFUL);
    }

    @Test
    public void testEndedByFisrtOfMultipleStream() throws Exception {
        PublishProcessor<String> startStream = PublishProcessor.create();
        PublishProcessor<String> end1Stream = PublishProcessor.create();
        PublishProcessor<String> end2Stream = PublishProcessor.create();
        PublishProcessor<Boolean> sourceStream = PublishProcessor.create();

        StreamId<String> startStreamId = provide(startStream).withUniqueStreamId();
        StreamId<String> end1StreamId = provide(end1Stream).withUniqueStreamId();
        StreamId<String> end2StreamId = provide(end2Stream).withUniqueStreamId();
        StreamId<Boolean> sourceStreamId = provide(sourceStream.onBackpressureBuffer()).withUniqueStreamId();

        final String ASSERTION_NAME = "name";
        final BufferedStreamExpression<Boolean> BUFFERED_SOURCE = BufferedStreamExpression.buffer(sourceStreamId);

        TestSubscriber<DeprecatedAnalysisResult> testSubscriber = rxFrom(streamIdFor(new BufferedAnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(startStreamId).endedOnMatch(end1StreamId).or().endedOnMatch(end2StreamId);
                assertAllBoolean(BUFFERED_SOURCE).areTrue().withName(ASSERTION_NAME);
            }
        })).test();

        startStream.onNext("A");
        await();

        sourceStream.onNext(true);
        sourceStream.onNext(true);

        await();
        end1Stream.onNext("A");
        await();

        sourceStream.onNext(false); /* should not appear in the analysis result since the buffer is already closed */

        await();
        end2Stream.onNext("A");

        DeprecatedAnalysisResult analysisResult = testSubscriber.awaitCount(1).assertValueCount(1).values().get(0);

        assertThat(statusOfAssertion(analysisResult, ASSERTION_NAME)).isEqualTo(AssertionStatus.SUCCESSFUL);
        assertThat(analysisResult.resolvedValueOf(BUFFERED_SOURCE)).containsExactly(true, true);
    }

    @Test
    public void testNotEndedByNeverPublishingStream() throws Exception {
        PublishProcessor<String> startStream = PublishProcessor.create();
        PublishProcessor<String> endStream = PublishProcessor.create();

        StreamId<String> startStreamId = provide(startStream).withUniqueStreamId();
        StreamId<String> endStreamId = provide(endStream).withUniqueStreamId();

        TestSubscriber<DeprecatedAnalysisResult> subscriber = new TestSubscriber<>();
        rxFrom(streamIdFor(new BufferedAnalysisModule() {
            {
                buffered().startedBy(startStreamId).endedOnEvery(endStreamId);
                assertAllBoolean(buffer(provide(Flowable.<Boolean> never()).withUniqueStreamId())).areTrue();
            }
        })).take(1).subscribe(subscriber);

        startStream.onNext("A");
        await();

        subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS);
        subscriber.assertNotTerminated();
    }

    @Test
    public void testEndedByCompleatedStream() throws Exception {
        PublishProcessor<String> startStream = PublishProcessor.create();
        PublishProcessor<String> endStream = PublishProcessor.create();

        StreamId<String> startStreamId = provide(startStream).withUniqueStreamId();
        StreamId<String> endStreamId = provide(endStream).withUniqueStreamId();

        TestSubscriber<DeprecatedAnalysisResult> subscriber = new TestSubscriber<>();
        rxFrom(new BufferedAnalysisModule() {
            {
                buffered().startedBy(startStreamId).endedOnEvery(endStreamId);
                assertAllBoolean(buffer(provide(Flowable.<Boolean> never()).withUniqueStreamId())).areTrue();
            }
        }).take(1).subscribe(subscriber);

        startStream.onNext("A");
        await();
        endStream.onComplete();

        subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS);
        subscriber.assertNotTerminated();
    }

   
}
