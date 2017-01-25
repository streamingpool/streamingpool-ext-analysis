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

package cern.online.analysis.core;

import static cern.streaming.pool.ext.tensorics.expression.StreamIdBasedExpression.of;
import static io.reactivex.Flowable.empty;
import static io.reactivex.Flowable.interval;
import static io.reactivex.Flowable.just;
import static io.reactivex.Flowable.merge;
import static io.reactivex.Flowable.never;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.tensorics.expression.EvaluationStatus.EVALUATED;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;
import org.tensorics.expression.EvaluationStatus;

import cern.online.analysis.core.util.AbstractAnalysisTest;
import cern.online.analysis.core.util.RxAnalysisSupport;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.ext.tensorics.exception.NoBufferedStreamSpecifiedException;
import cern.streaming.pool.ext.tensorics.expression.StreamIdBasedExpression;
import io.reactivex.subscribers.TestSubscriber;

@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("unchecked")
public class BufferedAnalysisTest extends AbstractAnalysisTest implements RxAnalysisSupport {

    private static final StreamId<Object> START_STREAM = mock(StreamId.class);
    private static final StreamId<Object> END_1_STREAM = mock(StreamId.class);
    private static final StreamId<Object> END_2_STREAM = mock(StreamId.class);
    private static final StreamId<Boolean> BOOLEAN_INTERVAL = mock(StreamId.class);
    private static final StreamIdBasedExpression<Boolean> ANY_BOOLEAN_EXPRESSION = of(BOOLEAN_INTERVAL);

    @Before
    public void setUp() {
        provide(interval(1, SECONDS).map(v -> v % 2 == 0)).as(BOOLEAN_INTERVAL);

    }

    @Test
    public void testEndedByTimeout() throws Exception {
        provide(just(new Object()).delay(1, TimeUnit.SECONDS)).as(START_STREAM);

        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(START_STREAM).endedAfter(ofSeconds(2));
                assertAllBoolean(buffered(ANY_BOOLEAN_EXPRESSION)).areTrue();
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent(4, SECONDS);
        subscriber.assertComplete();
        assertThat(evaluationStatusesOf(subscriber)).hasSize(1).containsOnly(EVALUATED);
    }

    @Test
    public void testEndedByStream() throws Exception {
        Object endingObject = new Object();
        provide(merge(just(endingObject).delay(1, SECONDS), never())).as(START_STREAM);
        provide(merge(just(endingObject).delay(2, SECONDS), never())).as(END_1_STREAM);

        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(START_STREAM).endedBy(END_1_STREAM);
                assertAllBoolean(buffered(ANY_BOOLEAN_EXPRESSION)).areTrue();
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent(5, SECONDS);
        subscriber.assertComplete();
        assertThat(evaluationStatusesOf(subscriber)).hasSize(1).containsOnly(EVALUATED);
    }

    @Test
    public void testEndedByFisrtOfMultipleStream() throws Exception {
        Object endingObject = new Object();
        provide(merge(just(endingObject).delay(1, TimeUnit.SECONDS), never())).as(START_STREAM);
        provide(merge(just(endingObject).delay(2, TimeUnit.SECONDS), never())).as(END_1_STREAM);
        provide(merge(just(endingObject).delay(3, TimeUnit.SECONDS), never())).as(END_2_STREAM);

        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(START_STREAM).endedBy(END_1_STREAM).or().endedBy(END_2_STREAM);
                assertAllBoolean(buffered(ANY_BOOLEAN_EXPRESSION)).areTrue();
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent(5, SECONDS);
        subscriber.assertComplete();
        assertThat(evaluationStatusesOf(subscriber)).hasSize(1).containsOnly(EVALUATED);
    }

    @Test
    public void testEndedByNeverPublishingStream() throws Exception {
        provide(merge(just(new Object()).delay(1, TimeUnit.SECONDS), never())).as(START_STREAM);
        provide(never()).as(END_1_STREAM);

        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(START_STREAM).endedBy(END_1_STREAM);
                assertAllBoolean(buffered(ANY_BOOLEAN_EXPRESSION)).areTrue();
            }
        }).subscribe(subscriber);

        subscriber.awaitTerminalEvent(5, TimeUnit.SECONDS);
        subscriber.assertNotTerminated();
    }

    @Test
    public void testEndedByCompleatedStream() throws Exception {
        provide(merge(just(new Object()).delay(1, TimeUnit.SECONDS), never())).as(START_STREAM);
        provide(empty()).as(END_1_STREAM);

        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(START_STREAM).endedBy(END_1_STREAM);
                assertAllBoolean(buffered(ANY_BOOLEAN_EXPRESSION)).areTrue();
            }
        }).subscribe(subscriber);

        subscriber.awaitTerminalEvent(2, SECONDS);
        subscriber.assertNotTerminated();
    }

    @Test(expected = NoBufferedStreamSpecifiedException.class)
    public void testNoBufferedAssertionInAnalysis() throws Exception {
        rxFrom(new AnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(START_STREAM).endedAfter(ofSeconds(1));
            }
        });
    }

}
