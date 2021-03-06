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

package org.streamingpool.ext.analysis.testing;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.streamingpool.ext.analysis.support.RxAnalysisSupport;
import org.tensorics.core.analysis.AnalysisResult;
import org.tensorics.core.analysis.AssertionStatus;
import org.tensorics.core.analysis.expression.AnalysisExpression;
import org.tensorics.core.analysis.expression.AssertionExpression;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;

import io.reactivex.subscribers.TestSubscriber;

public interface RxAnalysisTestingSupport extends AnalysisTest, RxAnalysisSupport {

    default List<AssertionStatus> evaluationStatusesOf(
            TestSubscriber<DetailedExpressionResult<AnalysisResult, AnalysisExpression>> subscriber) {
        return subscriber.values().stream().map(r -> r.value().overallStatus()).collect(toList());
    }

    default List<AssertionStatus> assertionsStatusesOf(TestSubscriber<AnalysisResult> subscriber) {
        return subscriber.values().stream().map(result -> result.overallStatus()).collect(toList());
    }

    default List<AssertionStatus> statusesOfAssertion(TestSubscriber<AnalysisResult> subscriber, String name) {
        return subscriber.values().stream().map(detailedResult -> statusOfAssertion(detailedResult, name))
                .collect(toList());
    }

    default AssertionStatus statusOfAssertion(DetailedExpressionResult<AnalysisResult, AnalysisExpression> result,
            String name) {
        return statusOfAssertion(result.value(), name);
    }

    default AssertionStatus statusOfAssertion(AnalysisResult result, String name) {
        List<AssertionExpression> assertionsWithName = result.assertions().stream()
                .filter(assertion -> assertion.name().equals(name)).collect(toList());

        if (assertionsWithName.size() != 1) {
            throw new IllegalArgumentException(
                    format("%s assertions match the name %s. Name should be unique", assertionsWithName.size(), name));
        }

        return result.statusFor(assertionsWithName.get(0));
    }
}
