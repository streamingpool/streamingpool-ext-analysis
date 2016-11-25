/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.tensorics.core.tree.domain.ResolvedExpression;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.ext.tensorics.evaluation.BufferedEvaluation;
import cern.streaming.pool.ext.tensorics.evaluation.ContinuousEvaluation;
import cern.streaming.pool.ext.tensorics.evaluation.TriggeredEvaluation;

/**
 * Some basic tests of the syntax and constraints for buffering, triggering and enabling clauses in the analysis module.
 * 
 * @author kfuchsbe
 */
public class AnalysisModuleTest {

    private final static StreamId<?> ANY_STREAM_ID = mock(StreamId.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void specifyEnablingDoesNotThrow() {
        AnalysisModule module = new AnalysisModule() {
            {
                enabled().always();
            }
        };
        assertThat(module.enablingBuilder()).isNotNull();
    }
    
    @Test
    public void specifyingEnablingTwiceThrows() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("enabling");
        @SuppressWarnings("unused")
        AnalysisModule module = new AnalysisModule() {
            {
                enabled().always();
                enabled().always();
            }
        };
    }

    @Test
    public void incompleteEnablingClauseThrows() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("enabled()");
        AnalysisModule module = new AnalysisModule() {
            {
                enabled();
            }
        };
        module.enablingBuilder().build();
    }

    @Ignore("Will be fixed with next version of tensorics. equals() method still missing in resolved expression. Next test compensates for this at the moment.")
    @Test
    public void defaultEnabledConditionIsTrue() {
        assertThat(emptyModule().enablingBuilder().build()).isEqualTo(ResolvedExpression.of(true));
    }

    @Test
    public void defaultEnabledConditionValueIsTrue() {
        assertThat(emptyModule().enablingBuilder().build().get()).isEqualTo(true);
    }

    @Test
    public void defaultEvaluationTypeIsContinuous() {
        assertThat(emptyModule().evaluationStrategy()).isInstanceOf(ContinuousEvaluation.class);
    }

    @Test
    public void specifyingBufferedDoesNotThrow() {
        AnalysisModule module = new AnalysisModule() {
            {
                buffered().startedBy(ANY_STREAM_ID).endedBy(ANY_STREAM_ID);
            }
        };
        assertThat(module.evaluationStrategy()).isInstanceOf(BufferedEvaluation.class);
    }

    @Test
    public void specifyingTwoBufferedClausesThrows() {
        expectTriggeredOrBufferedException();
        @SuppressWarnings("unused")
        AnalysisModule module = new AnalysisModule() {
            {
                buffered().startedBy(ANY_STREAM_ID).endedBy(ANY_STREAM_ID);
                buffered().startedBy(ANY_STREAM_ID).endedBy(ANY_STREAM_ID);
            }
        };
    }

    @Test
    public void specifyinTwoTriggeredClausesThrows() {
        expectTriggeredOrBufferedException();
        @SuppressWarnings("unused")
        AnalysisModule module = new AnalysisModule() {
            {
                triggered().by(ANY_STREAM_ID);
                triggered().by(ANY_STREAM_ID);
            }
        };
    }

    @Test
    public void specifyinTriggeredAndBufferedClausesThrows() {
        expectTriggeredOrBufferedException();
        @SuppressWarnings("unused")
        AnalysisModule module = new AnalysisModule() {
            {
                triggered().by(ANY_STREAM_ID);
                buffered().startedBy(ANY_STREAM_ID).endedBy(ANY_STREAM_ID);
            }
        };
    }

    @Test
    public void incompleteTriggerClauseThrowsWhenBuilding() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("triggeringStreamId");
        AnalysisModule module = new AnalysisModule() {
            {
                triggered();
            }
        };
        module.evaluationStrategy();
    }

    @Test
    public void incompleteBufferedClauseThrowsWhenBuilding() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("startStreamId");
        AnalysisModule module = new AnalysisModule() {
            {
                buffered();
            }
        };
        module.evaluationStrategy();
    }

    @Test
    public void specifyingTriggeredDoesNotThrow() {
        AnalysisModule module = new AnalysisModule() {
            {
                triggered().by(ANY_STREAM_ID);
            }
        };
        assertThat(module.evaluationStrategy()).isInstanceOf(TriggeredEvaluation.class);
    }

    private void expectTriggeredOrBufferedException() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("triggered() or buffered()");
    }

    private static final AnalysisModule emptyModule() {
        return new AnalysisModule() {
            /* empty on purpose */
        };
    }

}
