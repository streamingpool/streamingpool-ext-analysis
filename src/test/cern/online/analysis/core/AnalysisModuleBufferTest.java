/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import static org.mockito.Mockito.mock;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.tensorics.core.tree.domain.Expression;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.OverlapBufferStreamId;
import cern.streaming.pool.ext.tensorics.expression.StreamIdBasedExpression;
import cern.streaming.pool.ext.tensorics.streamid.ExpressionBasedStreamId;

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
