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

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.streamingpool.core.service.StreamFactoryRegistry;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.BufferSpecification.EndStreamMatcher;
import org.streamingpool.core.service.streamid.DelayedStreamId;
import org.streamingpool.ext.analysis.testing.AbstractAnalysisTest;
import org.streamingpool.ext.analysis.testing.RxAnalysisTestingSupport;
import org.streamingpool.ext.tensorics.evaluation.BufferedEvaluation;
import org.streamingpool.ext.tensorics.evaluation.EvaluationStrategy;
import org.streamingpool.ext.tensorics.expression.BufferedStreamExpression;
import org.streamingpool.ext.tensorics.streamfactory.BufferedTensoricsExpressionStreamFactory;
import org.streamingpool.ext.tensorics.streamid.ExpressionBasedStreamId;
import org.tensorics.core.resolve.engine.ResolvingEngine;
import org.tensorics.core.tree.domain.Contexts;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;

public class BufferedTest extends AbstractAnalysisTest implements RxAnalysisTestingSupport {

    @Autowired
    private StreamFactoryRegistry factoryRegistry;

    @Autowired
    private ResolvingEngine engine;

    @Before
    public void setUp() {
        factoryRegistry.addIntercept(new BufferedTensoricsExpressionStreamFactory(engine));
    }

    @Test
    public void separateStartEndIdWorks() {
        PublishProcessor<String> startStream = PublishProcessor.create();
        PublishProcessor<String> endStream = PublishProcessor.create();
        PublishProcessor<Boolean> sourceStream = PublishProcessor.create();

        StreamId<String> startStreamId = provide(startStream).withUniqueStreamId();
        StreamId<String> endStreamId = provide(endStream).withUniqueStreamId();
        StreamId<Boolean> sourceStreamId = provide(sourceStream.onBackpressureBuffer()).withUniqueStreamId();

        // @formatter:off
        EvaluationStrategy strategy = BufferedEvaluation.builder()
                .withStartStreamId(startStreamId)
                .withEndMatcher(EndStreamMatcher.endingOnEvery(endStreamId))
                .withTimeout(Duration.ofSeconds(5))
                .build();
        // @formatter:on

        BufferedStreamExpression<Boolean> bufferedSource = BufferedStreamExpression.buffer(sourceStreamId);

        ExpressionBasedStreamId<List<Boolean>> id = ExpressionBasedStreamId.of(bufferedSource,
                Contexts.newResolvingContext(), strategy);

        Flowable<List<Boolean>> resultStream = rxFrom(id);
        TestSubscriber<List<Boolean>> testSubscriber = resultStream.test();

        startStream.onNext("A");
        await();

        sourceStream.onNext(true);
        sourceStream.onNext(false);
        sourceStream.onNext(true);

        await();
        endStream.onNext("A");

        List<Boolean> analysisResult = testSubscriber.awaitCount(1).assertValueCount(1).values().get(0);

        assertThat(analysisResult).containsExactly(true, false, true);

//        try {
//            Thread.sleep(200000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }


    @Ignore("Does not work ... must be a combination of delay + buffer....?")
    @Test
    public void delayedIdWorks() {
        PublishProcessor<String> startStream = PublishProcessor.create();
        PublishProcessor<String> endStream = PublishProcessor.create();
        PublishProcessor<Boolean> sourceStream = PublishProcessor.create();

        StreamId<String> startStreamId = provide(startStream).withUniqueStreamId();
        StreamId<String> endStreamId = provide(endStream).withUniqueStreamId();
        StreamId<String> delayedStreamId = DelayedStreamId.delayBy(startStreamId, Duration.ofMillis(200));

        // @formatter:off
        EvaluationStrategy strategy = BufferedEvaluation.builder()
                .withStartStreamId(startStreamId)
                .withEndMatcher(EndStreamMatcher.endingOnEvery(endStreamId))
                .withTimeout(Duration.ofSeconds(5))
                .build();
        // @formatter:on

        BufferedStreamExpression<String> bufferedSource = BufferedStreamExpression.buffer(delayedStreamId);

        ExpressionBasedStreamId<List<String>> id = ExpressionBasedStreamId.of(bufferedSource,
                Contexts.newResolvingContext(), strategy);

        Flowable<List<String>> resultStream = rxFrom(id);
        TestSubscriber<List<String>> testSubscriber = resultStream.test();



        startStream.onNext("A");
        await();

        startStream.onNext("B");
        startStream.onNext("C");

        await();
        endStream.onNext("A");

        List<String> analysisResult = testSubscriber.awaitCount(1).assertValueCount(1).values().get(0);

        assertThat(analysisResult).containsExactly("A", "B", "C");

//        try {
//            Thread.sleep(200000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
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

}
