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

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.ext.analysis.modules.BufferedAnalysisModule;
import org.streamingpool.ext.analysis.modules.ContinuousAnalysisModule;
import org.streamingpool.ext.analysis.modules.TriggeredAnalysisModule;
import org.streamingpool.ext.tensorics.evaluation.BufferedEvaluation;
import org.streamingpool.ext.tensorics.evaluation.ContinuousEvaluation;
import org.streamingpool.ext.tensorics.evaluation.TriggeredEvaluation;
import org.tensorics.core.tree.domain.ResolvedExpression;

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
        ContinuousAnalysisModule module = new ContinuousAnalysisModule() {
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
        ContinuousAnalysisModule module = new ContinuousAnalysisModule() {
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
        ContinuousAnalysisModule module = new ContinuousAnalysisModule() {
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
        BufferedAnalysisModule module = new BufferedAnalysisModule() {
            {
                buffered().startedBy(ANY_STREAM_ID).endedOnEvery(ANY_STREAM_ID);
            }
        };
        assertThat(module.evaluationStrategy()).isInstanceOf(BufferedEvaluation.class);
    }

    @Test
    public void specifyingTwoBufferedClausesThrows() {
        expectTriggeredOrBufferedException();
        @SuppressWarnings("unused")
        BufferedAnalysisModule module = new BufferedAnalysisModule() {
            {
                buffered().startedBy(ANY_STREAM_ID).endedOnEvery(ANY_STREAM_ID);
                buffered().startedBy(ANY_STREAM_ID).endedOnEvery(ANY_STREAM_ID);
            }
        };
    }

    @Test
    public void specifyinTwoTriggeredClausesThrows() {
        expectTriggeredOrBufferedException();
        @SuppressWarnings("unused")
        TriggeredAnalysisModule module = new TriggeredAnalysisModule() {
            {
                triggered().by(ANY_STREAM_ID);
                triggered().by(ANY_STREAM_ID);
            }
        };
    }

    @Test
    public void incompleteTriggerClauseThrowsWhenBuilding() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("triggeringStreamId");
        TriggeredAnalysisModule module = new TriggeredAnalysisModule() {
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
        BufferedAnalysisModule module = new BufferedAnalysisModule() {
            {
                buffered();
            }
        };
        module.evaluationStrategy();
    }

    @Test
    public void specifyingTriggeredDoesNotThrow() {
        TriggeredAnalysisModule module = new TriggeredAnalysisModule() {
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

    private static final ContinuousAnalysisModule emptyModule() {
        return new ContinuousAnalysisModule() {
            /* empty on purpose */
        };
    }

}
