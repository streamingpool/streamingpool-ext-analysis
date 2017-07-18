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
import static org.mockito.Mockito.mock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.BufferSpecification;
import org.streamingpool.core.service.streamid.OverlapBufferStreamId;
import org.streamingpool.ext.tensorics.evaluation.BufferedEvaluation;
import org.streamingpool.ext.tensorics.evaluation.EvaluationStrategy;
import org.streamingpool.ext.tensorics.expression.StreamIdBasedExpression;
import org.streamingpool.ext.tensorics.expression.UnresolvedStreamIdBasedExpression;
import org.streamingpool.ext.tensorics.streamid.ExpressionBasedStreamId;
import org.tensorics.core.expressions.Placeholder;
import org.tensorics.core.resolve.engine.ResolvingEngine;
import org.tensorics.core.resolve.engine.ResolvingEngines;
import org.tensorics.core.tree.domain.Contexts;
import org.tensorics.core.tree.domain.EditableResolvingContext;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvingContext;

public class AnalysisModuleBufferTest {

    @SuppressWarnings("unchecked")
    private final static Expression<Object> ANY_EXPRESSION = mock(Expression.class);

    @SuppressWarnings("unchecked")
    private final static StreamId<Object> ANY_STREAM_ID = mock(StreamId.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void bufferedExpressionResultsInBufferOfStreamOfExpression() {
        UnresolvedStreamIdBasedExpression<Iterable<Object>> bufferExpression = (UnresolvedStreamIdBasedExpression<Iterable<Object>>) AnalysisModule
                .buffered(ANY_EXPRESSION);
        Expression<StreamId<Iterable<Object>>> bufferStreamIdExpression = bufferExpression.streamIdExpression();

        ResolvingContext resolvedContext = resolvedContextOf(bufferStreamIdExpression);

        @SuppressWarnings("rawtypes")
        OverlapBufferStreamId<?> overlapBufferStreamId = (OverlapBufferStreamId) resolvedContext
                .resolvedValueOf(bufferStreamIdExpression);

        assertThat(overlapBufferStreamId.sourceId()).isEqualTo(ExpressionBasedStreamId.of(ANY_EXPRESSION));
    }

    @Test
    public void bufferedExpressionOfStreamBasedExpressionResultsInBufferOfSource() {
        UnresolvedStreamIdBasedExpression<Iterable<Object>> bufferExpression = (UnresolvedStreamIdBasedExpression<Iterable<Object>>) AnalysisModule
                .buffered(StreamIdBasedExpression.of(ANY_STREAM_ID));
        Expression<StreamId<Iterable<Object>>> bufferStreamIdExpression = bufferExpression.streamIdExpression();

        ResolvingContext resolvedContext = resolvedContextOf(bufferStreamIdExpression);

        @SuppressWarnings("rawtypes")
        OverlapBufferStreamId<?> overlapBufferStreamId = (OverlapBufferStreamId) resolvedContext
                .resolvedValueOf(bufferStreamIdExpression);

        assertThat(overlapBufferStreamId.sourceId()).isEqualTo(ANY_STREAM_ID);
    }

    private ResolvingContext resolvedContextOf(Expression<StreamId<Iterable<Object>>> bufferStreamIdExpression) {
        ResolvingEngine engine = ResolvingEngines.defaultEngine();
        EditableResolvingContext initialCtx = initalContextWithBufferedEvaluationStrategy();

        ResolvingContext resolvedContext = engine.resolveDetailed(bufferStreamIdExpression, initialCtx).context();
        return resolvedContext;
    }

    private EditableResolvingContext initalContextWithBufferedEvaluationStrategy() {
        EditableResolvingContext initialCtx = Contexts.newResolvingContext();
        initialCtx.put(Placeholder.ofClass(EvaluationStrategy.class), mock(BufferedEvaluation.class));
        return initialCtx;
    }

    @Test
    public void bufferingOfBufferedStreamsIsThrown() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("already a buffered stream id");

        @SuppressWarnings("unused")
        AnalysisModule module = new BufferedAnalysisModule() {
            {
                buffered(OverlapBufferStreamId.of(ANY_STREAM_ID, mock(BufferSpecification.class)));
            }
        };
    }

    private static abstract class BufferedAnalysisModule extends AnalysisModule {
        {
            buffered().startedBy(ANY_STREAM_ID).endedBy(ANY_STREAM_ID);
        }
    }

}
