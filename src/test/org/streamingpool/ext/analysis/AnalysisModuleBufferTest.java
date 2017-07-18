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

import static org.mockito.Mockito.mock;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.OverlapBufferStreamId;
import org.streamingpool.ext.tensorics.expression.StreamIdBasedExpression;
import org.streamingpool.ext.tensorics.streamid.ExpressionBasedStreamId;
import org.tensorics.core.tree.domain.Expression;

public class AnalysisModuleBufferTest {

    @SuppressWarnings("unchecked")
    private final static Expression<Object> ANY_EXPRESSION = mock(Expression.class);

    @SuppressWarnings("unchecked")
    private final static StreamId<Object> ANY_STREAM_ID = mock(StreamId.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void defaultModuleDoesNotAllowBufferedExpressions() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("usage of buffered values");
        @SuppressWarnings("unused")
        AnalysisModule module = new AnalysisModule() {
            {
                buffered(ANY_EXPRESSION);
            }
        };
    }

    @Test
    public void bufferedStreamIdResultsInBufferOfSource() {
        @SuppressWarnings("unused")
        AnalysisModule module = new BufferedAnalysisModule() {
            {
                Expression<List<Object>> bufferedExpression = buffered(ANY_STREAM_ID);
                Assertions.assertThat(bufferedStreamIdFrom(bufferedExpression).sourceId()).isEqualTo(ANY_STREAM_ID);
            }
        };
    }

    @Test
    public void bufferedExpressionResultsInBufferOfStreamOfExpression() {
        @SuppressWarnings("unused")
        AnalysisModule module = new BufferedAnalysisModule() {
            {
                Expression<List<Object>> bufferedExpression = buffered(ANY_EXPRESSION);
                Assertions.assertThat(bufferedStreamIdFrom(bufferedExpression).sourceId())
                        .isEqualTo(ExpressionBasedStreamId.of(ANY_EXPRESSION));
            }
        };
    }

    @Test
    public void bufferedExpressionOfStreamBasedExpressionResultsInBufferOfSource() {
        @SuppressWarnings("unused")
        AnalysisModule module = new BufferedAnalysisModule() {
            {
                Expression<List<Object>> bufferedExpression = buffered(StreamIdBasedExpression.of(ANY_STREAM_ID));
                Assertions.assertThat(bufferedStreamIdFrom(bufferedExpression).sourceId()).isEqualTo(ANY_STREAM_ID);
            }
        };
    }

    @Test
    public void bufferingOfBufferedStreamsIsThrown() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("already a buffered stream id");

        @SuppressWarnings("unused")
        AnalysisModule module = new BufferedAnalysisModule() {
            {
                buffered(buffered(ANY_STREAM_ID));
            }
        };
    }

    private static abstract class BufferedAnalysisModule extends AnalysisModule {
        {
            buffered().startedBy(ANY_STREAM_ID).endedBy(ANY_STREAM_ID);
        }
    }

    private static final OverlapBufferStreamId<?> bufferedStreamIdFrom(Expression<List<Object>> bufferedExpression) {
        return (OverlapBufferStreamId<?>) ((StreamIdBasedExpression<?>) bufferedExpression).streamId();
    }

}
