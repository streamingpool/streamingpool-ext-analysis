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
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.streamingpool.ext.tensorics.expression.StreamIdBasedExpression.of;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.streamingpool.core.service.StreamFactoryRegistry;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.DelayedStreamId;
import org.streamingpool.ext.analysis.util.AbstractAnalysisTest;
import org.streamingpool.ext.analysis.util.RxAnalysisSupport;
import org.streamingpool.ext.tensorics.expression.BufferedStreamExpression;
import org.streamingpool.ext.tensorics.expression.StreamIdBasedExpression;
import org.streamingpool.ext.tensorics.streamfactory.BufferedTensoricsExpressionStreamFactory;
import org.tensorics.core.resolve.engine.ResolvingEngine;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;

public class BufferedAnalysisTest extends AbstractAnalysisTest implements RxAnalysisSupport {

    private static final StreamId<Object> START_STREAM = mock(StreamId.class);
    private static final StreamId<Object> END_1_STREAM = mock(StreamId.class);
    private static final StreamId<Object> END_2_STREAM = mock(StreamId.class);
    private static final StreamId<Boolean> BOOLEAN_INTERVAL = mock(StreamId.class);
    private static final StreamIdBasedExpression<Boolean> ANY_BOOLEAN_EXPRESSION = of(BOOLEAN_INTERVAL);

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

        Flowable<AnalysisResult> resultStream = rxFrom(new AnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(startStreamId).endedOnMatch(endStreamId);
                assertAllBoolean(BUFFERED_SOURCE).areTrue().withName(ASSERTION_NAME);
            }
        });

        TestSubscriber<AnalysisResult> testSubscriber = resultStream.test();

        startStream.onNext("A");
        await();

        sourceStream.onNext(true);
        sourceStream.onNext(false);
        sourceStream.onNext(true);

        await();
        endStream.onNext("A");

        AnalysisResult analysisResult = testSubscriber.awaitCount(1).assertValueCount(1).values().get(0);

        assertThat(statusOfAssertion(analysisResult, ASSERTION_NAME)).isEqualTo(AssertionStatus.FAILURE);
        assertThat(analysisResult.resolvingContext().resolvedValueOf(BUFFERED_SOURCE)).containsOnly(true, false, true);
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

        Flowable<AnalysisResult> resultStream = rxFrom(new AnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(startStreamId)
                        .endedOnEvery(DelayedStreamId.delayBy(endStreamId, Duration.ofSeconds(1)));
                assertThat(BUFFERED_END).is(buffer -> buffer.contains(END_VALUE)).withName(ASSERTION_NAME);
            }
        });

        TestSubscriber<AnalysisResult> testSubscriber = resultStream.test();

        startStream.onNext("any");
        await();

        await();
        endStream.onNext(END_VALUE);

        AnalysisResult analysisResult = testSubscriber.awaitCount(1).assertValueCount(1).values().get(0);

        assertThat(statusOfAssertion(analysisResult, ASSERTION_NAME)).isEqualTo(AssertionStatus.SUCCESSFUL);
        assertThat(analysisResult.resolvingContext().resolvedValueOf(BUFFERED_END)).containsOnly(END_VALUE);
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

        Flowable<AnalysisResult> resultStream = rxFrom(new AnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(startStreamId).endedOnMatch(endStreamId);
                assertAllBoolean(BUFFERED_SOURCE).areTrue().withName(ASSERTION_NAME);
            }
        });

        TestSubscriber<AnalysisResult> testSubscriber = resultStream.test();

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

        List<AnalysisResult> analysisResults = testSubscriber.awaitCount(3).assertValueCount(3).values();

        AnalysisResult resultA = analysisResults.get(0);
        AnalysisResult resultB = analysisResults.get(1);
        AnalysisResult resultC = analysisResults.get(2);
        /* analysis A */
        assertThat(statusOfAssertion(resultA, ASSERTION_NAME)).isEqualTo(AssertionStatus.FAILURE);
        assertThat(resultA.resolvingContext().resolvedValueOf(BUFFERED_SOURCE)).containsOnly(true, false, true);
        /* analysis B */
        assertThat(statusOfAssertion(resultB, ASSERTION_NAME)).isEqualTo(AssertionStatus.SUCCESSFUL);
        assertThat(resultB.resolvingContext().resolvedValueOf(BUFFERED_SOURCE)).containsOnly(true, true, true, true);
        /* analysis C */
        assertThat(statusOfAssertion(resultC, ASSERTION_NAME)).isEqualTo(AssertionStatus.FAILURE);
        assertThat(resultC.resolvingContext().resolvedValueOf(BUFFERED_SOURCE)).containsOnly(true, false, true, true, true,
                true, true);
    }

    private void await() {
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            /* */
        }
    }

    // @Test
    // public void testEndedByTimeout() throws Exception {
    // provide(just(new Object()).delay(1, TimeUnit.SECONDS)).as(START_STREAM);
    //
    // TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new
    // TestSubscriber<>();
    // rxFrom(new AnalysisModule() {
    // {
    // enabled().always();
    // buffered().startedBy(START_STREAM).endedAfter(ofSeconds(2));
    // assertAllBoolean(buffered(ANY_BOOLEAN_EXPRESSION)).areTrue();
    // }
    // }).take(1).subscribe(subscriber);
    //
    // subscriber.awaitTerminalEvent(4, SECONDS);
    // subscriber.assertComplete();
    // assertThat(evaluationStatusesOf(subscriber)).hasSize(1).containsOnly(EVALUATED);
    // }
    //
    // @Test
    // public void testEndedByStream() throws Exception {
    // Object endingObject = new Object();
    // provide(merge(just(endingObject).delay(1, SECONDS), never())).as(START_STREAM);
    // provide(merge(just(endingObject).delay(2, SECONDS), never())).as(END_1_STREAM);
    //
    // TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new
    // TestSubscriber<>();
    // rxFrom(new AnalysisModule() {
    // {
    // enabled().always();
    // buffered().startedBy(START_STREAM).endedBy(END_1_STREAM);
    // assertAllBoolean(buffered(ANY_BOOLEAN_EXPRESSION)).areTrue();
    // }
    // }).take(1).subscribe(subscriber);
    //
    // subscriber.awaitTerminalEvent(5, SECONDS);
    // subscriber.assertComplete();
    // assertThat(evaluationStatusesOf(subscriber)).hasSize(1).containsOnly(EVALUATED);
    // }
    //
    // @Test
    // public void testEndedByFisrtOfMultipleStream() throws Exception {
    // Object endingObject = new Object();
    // provide(merge(just(endingObject).delay(1, TimeUnit.SECONDS), never())).as(START_STREAM);
    // provide(merge(just(endingObject).delay(2, TimeUnit.SECONDS), never())).as(END_1_STREAM);
    // provide(merge(just(endingObject).delay(3, TimeUnit.SECONDS), never())).as(END_2_STREAM);
    //
    // TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new
    // TestSubscriber<>();
    // rxFrom(new AnalysisModule() {
    // {
    // enabled().always();
    // buffered().startedBy(START_STREAM).endedBy(END_1_STREAM).or().endedBy(END_2_STREAM);
    // assertAllBoolean(buffered(ANY_BOOLEAN_EXPRESSION)).areTrue();
    // }
    // }).take(1).subscribe(subscriber);
    //
    // subscriber.awaitTerminalEvent(5, SECONDS);
    // subscriber.assertComplete();
    // assertThat(evaluationStatusesOf(subscriber)).hasSize(1).containsOnly(EVALUATED);
    // }
    //
    // @Test
    // public void testEndedByNeverPublishingStream() throws Exception {
    // provide(merge(just(new Object()).delay(1, TimeUnit.SECONDS), never())).as(START_STREAM);
    // provide(never()).as(END_1_STREAM);
    //
    // TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new
    // TestSubscriber<>();
    // rxFrom(new AnalysisModule() {
    // {
    // enabled().always();
    // buffered().startedBy(START_STREAM).endedBy(END_1_STREAM);
    // assertAllBoolean(buffered(ANY_BOOLEAN_EXPRESSION)).areTrue();
    // }
    // }).subscribe(subscriber);
    //
    // subscriber.awaitTerminalEvent(5, TimeUnit.SECONDS);
    // subscriber.assertNotTerminated();
    // }
    //
    // @Test
    // public void testEndedByCompleatedStream() throws Exception {
    // provide(merge(just(new Object()).delay(1, TimeUnit.SECONDS), never())).as(START_STREAM);
    // provide(empty()).as(END_1_STREAM);
    //
    // TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new
    // TestSubscriber<>();
    // rxFrom(new AnalysisModule() {
    // {
    // enabled().always();
    // buffered().startedBy(START_STREAM).endedBy(END_1_STREAM);
    // assertAllBoolean(buffered(ANY_BOOLEAN_EXPRESSION)).areTrue();
    // }
    // }).subscribe(subscriber);
    //
    // subscriber.awaitTerminalEvent(2, SECONDS);
    // subscriber.assertNotTerminated();
    // }
    //
    // @Test(expected = NoBufferedStreamSpecifiedException.class)
    // public void testNoBufferedAssertionInAnalysis() throws Exception {
    // rxFrom(new AnalysisModule() {
    // {
    // enabled().always();
    // buffered().startedBy(START_STREAM).endedAfter(ofSeconds(1));
    // }
    // });
    // }

}
