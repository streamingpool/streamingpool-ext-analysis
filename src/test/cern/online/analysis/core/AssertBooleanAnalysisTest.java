/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import static cern.online.analysis.core.AssertionStatus.FAILURE;
import static cern.online.analysis.core.AssertionStatus.SUCCESSFUL;
import static cern.streaming.pool.ext.tensorics.expression.StreamIdBasedExpression.of;
import static com.google.common.collect.ImmutableSet.of;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static rx.Observable.just;

import java.util.List;

import org.junit.Test;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.expression.EvaluationStatus;

import cern.online.analysis.core.util.AbstractAnalysisTest;
import cern.online.analysis.core.util.RxAnalysisSupport;
import cern.streaming.pool.core.service.StreamId;
import rx.observers.TestSubscriber;

public class AssertBooleanAnalysisTest extends AbstractAnalysisTest implements RxAnalysisSupport {

    @Test
    public void testAllBooleanAreTrueSuccess() {
        StreamId<List<Boolean>> booleanSourceId = provide(just(asList(true, true))).withUniqueStreamId();

        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                assertAllBoolean(booleanSourceId).areTrue();
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent(1, SECONDS);
        assertThat(assertionsStatusesOf(subscriber)).containsOnly(SUCCESSFUL);
    }

    @Test
    public void testAllBooleanAreTrueFailure() {
        StreamId<List<Boolean>> booleanSourceId = provide(just(asList(true, false, true))).withUniqueStreamId();

        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                assertAllBoolean(booleanSourceId).areTrue();
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent(1, SECONDS);
        assertThat(assertionsStatusesOf(subscriber)).containsOnly(FAILURE);
    }

    @Test
    public void testAtLeastOneBooleanIsTrueSuccess() {
        StreamId<List<Boolean>> booleanSourceId = provide(just(asList(false, false, true))).withUniqueStreamId();

        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                assertAtLeastOneBooleanOf(booleanSourceId).isTrue();
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent(1, SECONDS);
        assertThat(assertionsStatusesOf(subscriber)).containsOnly(SUCCESSFUL);
    }

    @Test
    public void testAtLeastOneBooleanIsTrueFailure() {
        StreamId<List<Boolean>> booleanSourceId = provide(just(asList(false, false, false))).withUniqueStreamId();

        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                assertAtLeastOneBooleanOf(booleanSourceId).isTrue();
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent(1, SECONDS);
        assertThat(assertionsStatusesOf(subscriber)).containsOnly(FAILURE);
    }

    @Test
    public void testExcludingBooleans() {
        Expression<Boolean> booleanSource1Id = of(provide(just(true)).withUniqueStreamId());
        Expression<Boolean> booleanSource2Id = of(provide(just(true)).withUniqueStreamId());
        Expression<Boolean> booleanSource3Id = of(provide(just(false)).withUniqueStreamId());

        TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber = new TestSubscriber<>();
        rxFrom(new AnalysisModule() {
            {
                assertAllBoolean(of(booleanSource1Id, booleanSource2Id, booleanSource3Id)).excluding(booleanSource3Id)
                        .areTrue();
            }
        }).take(1).subscribe(subscriber);

        subscriber.awaitTerminalEvent(1, SECONDS);
        assertThat(assertionsStatusesOf(subscriber)).containsOnly(SUCCESSFUL);
    }

}
