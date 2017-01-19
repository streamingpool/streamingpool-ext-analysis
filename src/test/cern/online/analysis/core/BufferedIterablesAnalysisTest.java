/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import static cern.online.analysis.core.AssertionStatus.ERROR;
import static cern.online.analysis.core.AssertionStatus.SUCCESSFUL;
import static cern.streaming.pool.ext.tensorics.expression.StreamIdBasedExpression.of;
import static java.lang.Integer.MIN_VALUE;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static rx.Observable.just;

import java.time.Duration;

import org.junit.Test;
import org.junit.Ignore;
import org.tensorics.core.iterable.expressions.PickExpression;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;
import org.tensorics.expression.EvaluationStatus;

import cern.online.analysis.core.util.AbstractAnalysisTest;
import cern.online.analysis.core.util.RxAnalysisSupport;
import cern.streaming.pool.core.service.StreamId;
import rx.Observable;
import rx.observers.TestSubscriber;

public class BufferedIterablesAnalysisTest extends AbstractAnalysisTest implements RxAnalysisSupport {

    @Test
    public void testPickExpressionOfIterable() throws Exception {
        StreamId<? extends Iterable<Boolean>> sourceId = provide(just(asList(false, true, false))).withUniqueStreamId();
        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                enabled().always();
                assertBoolean(true).isEqualTo(PickExpression.fromFirst(of(sourceId), 1));
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent(5, SECONDS);
        subscriber.assertCompleted();
        assertThat(assertionsStatusesOf(subscriber)).containsOnly(SUCCESSFUL);
    }

    @Test
    public void testPickExpressionOfBufferedLiveStream() throws Exception {
        StreamId<Long> startBuffer = provide(Observable.interval(1, SECONDS)).withUniqueStreamId();
        StreamId<Boolean> booleanData = provide(Observable.interval(1, SECONDS).map(v -> true)).withUniqueStreamId();
        String label = "any";

        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                enabled().always();
                buffered().startedBy(startBuffer).endedAfter(Duration.ofSeconds(4));
                assertBoolean(true).isEqualTo(PickExpression.fromFirst(bufferedIterable(of(booleanData)), 2))
                        .withName(label);
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent();
        subscriber.assertCompleted();
        assertThat(statusesOfAssertion(subscriber, label)).containsOnly(SUCCESSFUL);
    }

    @Ignore
    @Test
    public void testPickExpressionOutOfBound() throws Exception {
        StreamId<? extends Iterable<Boolean>> sourceId = provide(just(asList(false, true, false))).withUniqueStreamId();
        String label = "any";
        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                enabled().always();
                assertBoolean(true).isEqualTo(PickExpression.fromFirst(of(sourceId), MIN_VALUE)).withName(label);
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent(5, SECONDS);
        subscriber.assertCompleted();
        assertThat(statusesOfAssertion(subscriber, label)).containsOnly(ERROR);
    }

}
