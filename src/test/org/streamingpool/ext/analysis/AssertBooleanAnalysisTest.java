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
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.streamingpool.ext.analysis.AssertionStatus.FAILURE;
import static org.streamingpool.ext.analysis.AssertionStatus.SUCCESSFUL;

import org.junit.Test;
import org.streamingpool.ext.analysis.modules.AnalysisModule;
import org.streamingpool.ext.analysis.modules.ContinuousAnalysisModule;
import org.streamingpool.ext.analysis.testing.AbstractAnalysisTest;
import org.streamingpool.ext.analysis.testing.RxAnalysisTestingSupport;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvedExpression;

/**
 * Tests on boolean operations in the analysis
 */
public class AssertBooleanAnalysisTest extends AbstractAnalysisTest implements RxAnalysisTestingSupport {

    private static final String AN_ASSERTION_NAME = "name";

    @Test
    public void testAllBooleanAreTrueSuccess() {
        AnalysisModule<?> analysisModule = new ContinuousAnalysisModule() {
            {
                assertAllBoolean(ResolvedExpression.of(asList(true, true))).areTrue().withName(AN_ASSERTION_NAME);
            }
        };

        DeprecatedAnalysisResult result = resolveAnalysisModule(analysisModule);
        assertThat(statusOfAssertion(result, AN_ASSERTION_NAME)).isEqualTo(SUCCESSFUL);
    }

    @Test
    public void testAllBooleanAreTrueFailure() {
        AnalysisModule<?> analysisModule = new ContinuousAnalysisModule() {
            {
                assertAllBoolean(ResolvedExpression.of(asList(true, false, false))).areTrue().withName(AN_ASSERTION_NAME);
            }
        };

        DeprecatedAnalysisResult result = resolveAnalysisModule(analysisModule);
        assertThat(statusOfAssertion(result, AN_ASSERTION_NAME)).isEqualTo(FAILURE);
    }

    @Test
    public void testAtLeastOneBooleanIsTrueSuccess() {
        AnalysisModule<?> analysisModule = new ContinuousAnalysisModule() {
            {
                assertAtLeastOneBooleanOf(ResolvedExpression.of(asList(false, false, true))).isTrue()
                        .withName(AN_ASSERTION_NAME);
            }
        };

        DeprecatedAnalysisResult result = resolveAnalysisModule(analysisModule);
        assertThat(statusOfAssertion(result, AN_ASSERTION_NAME)).isEqualTo(SUCCESSFUL);
    }

    @Test
    public void testAtLeastOneBooleanIsTrueFailure() {
        AnalysisModule<?> analysisModule = new ContinuousAnalysisModule() {
            {
                assertAtLeastOneBooleanOf(ResolvedExpression.of(asList(false, false, false))).isTrue()
                .withName(AN_ASSERTION_NAME);
            }
        };
        
        DeprecatedAnalysisResult result = resolveAnalysisModule(analysisModule);
        assertThat(statusOfAssertion(result, AN_ASSERTION_NAME)).isEqualTo(FAILURE);
    }

    @Test
    public void testExcludingBooleans() {
        Expression<Boolean> booleanSource1Id = ResolvedExpression.of(true);
        Expression<Boolean> booleanSource2Id = ResolvedExpression.of(true);
        Expression<Boolean> booleanSource3Id = ResolvedExpression.of(false);

        AnalysisModule<?> analysisModule = new ContinuousAnalysisModule() {
            {
                assertAllBoolean(of(booleanSource1Id, booleanSource2Id, booleanSource3Id)).excluding(booleanSource3Id)
                        .areTrue().withName(AN_ASSERTION_NAME);
            }
        };
        
        DeprecatedAnalysisResult result = resolveAnalysisModule(analysisModule);
        assertThat(statusOfAssertion(result, AN_ASSERTION_NAME)).isEqualTo(SUCCESSFUL);
    }

}
