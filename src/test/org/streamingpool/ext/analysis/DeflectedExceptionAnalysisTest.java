/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.streamingpool.core.service.StreamFactoryRegistry;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.diagnostic.ErrorStreamId;
import org.streamingpool.ext.analysis.dsl.And;
import org.streamingpool.ext.analysis.util.AbstractAnalysisTest;
import org.streamingpool.ext.analysis.util.RxAnalysisSupport;
import org.streamingpool.ext.tensorics.expression.BufferedStreamExpression;
import org.streamingpool.ext.tensorics.streamfactory.BufferedTensoricsExpressionStreamFactory;
import org.tensorics.core.expressions.EvaluationStatus;
import org.tensorics.core.iterable.expressions.PickExpression;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;
import org.tensorics.core.resolve.engine.ResolvingEngine;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvedExpression;

import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;

public class DeflectedExceptionAnalysisTest extends AbstractAnalysisTest implements RxAnalysisSupport {

    protected static final String ASSERTION_NAME = "a_name";

    @Autowired
    private StreamFactoryRegistry factoryRegistry;

    @Autowired
    private ResolvingEngine engine;

    @Before
    public void setUp() {
        factoryRegistry.addIntercept(new BufferedTensoricsExpressionStreamFactory(engine));
    }

    @Test
    public void test() {
        PublishProcessor<Boolean> SOURCE_STREAM = PublishProcessor.create();
        PublishProcessor<String> START_STREAM = PublishProcessor.create();
        PublishProcessor<String> END_STREAM = PublishProcessor.create();

        StreamId<Boolean> SOURCE_STREAM_ID = provide(SOURCE_STREAM).withUniqueStreamId();
        StreamId<String> START_STREAM_ID = provide(START_STREAM).withUniqueStreamId();
        StreamId<String> END_STREAM_ID = provide(END_STREAM).withUniqueStreamId();

        Expression<List<Boolean>> BUFFER_EXPRESSION = BufferedStreamExpression.buffer(SOURCE_STREAM_ID);

        AnalysisModule analysis = new AnalysisModule() {
            {
                buffered().startedBy(START_STREAM_ID).endedOnMatch(END_STREAM_ID);
                assertAllBoolean(BUFFER_EXPRESSION).areTrue().withName(ASSERTION_NAME);
            }
        };

        AnalysisStreamId analysisStreamId = analysisIdOf(analysis);

        TestSubscriber<AnalysisResult> testSubscriber = rxFrom(analysisStreamId).test();
        rxFrom(ErrorStreamId.of(analysisStreamId)).subscribe(System.err::println);

        START_STREAM.onNext("A");
        await();
        END_STREAM.onNext("A");

        List<AnalysisResult> results = testSubscriber.awaitCount(1).assertValueCount(1).values();

        AnalysisResult firstResult = results.get(0);

        System.out.println(firstResult.resolvingContext().resolvedValueOf(BUFFER_EXPRESSION));
        System.out.println(statusOfAssertion(firstResult, ASSERTION_NAME));

        await();
    }

    private void await() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            /* */
        }
    }

}
