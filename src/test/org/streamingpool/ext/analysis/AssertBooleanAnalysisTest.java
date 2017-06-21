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

import static com.google.common.collect.ImmutableSet.of;
import static io.reactivex.Flowable.just;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.streamingpool.ext.analysis.AssertionStatus.FAILURE;
import static org.streamingpool.ext.analysis.AssertionStatus.SUCCESSFUL;
import static org.streamingpool.ext.tensorics.expression.StreamIdBasedExpression.of;

import java.util.List;

import org.junit.Test;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.ext.analysis.AnalysisExpression;
import org.streamingpool.ext.analysis.AnalysisModule;
import org.streamingpool.ext.analysis.util.AbstractAnalysisTest;
import org.streamingpool.ext.analysis.util.RxAnalysisSupport;
import org.tensorics.core.expressions.EvaluationStatus;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;
import org.tensorics.core.tree.domain.Expression;

import io.reactivex.subscribers.TestSubscriber;

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
