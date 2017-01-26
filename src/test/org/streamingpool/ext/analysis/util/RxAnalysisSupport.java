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

package org.streamingpool.ext.analysis.util;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.ext.analysis.AnalysisExpression;
import org.streamingpool.ext.analysis.AnalysisModule;
import org.streamingpool.ext.analysis.AssertionStatus;
import org.streamingpool.ext.analysis.expression.AssertionExpression;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;
import org.tensorics.expression.EvaluationStatus;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

public interface RxAnalysisSupport extends AnalysisTest, RxStreamSupport {

    public default Flowable<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> rxFrom(
            AnalysisModule analysisModule) {
        return rxFrom(analysisIdOf(analysisModule));
    }

    public default List<EvaluationStatus> evaluationStatusesOf(
            TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber) {
        return subscriber.values().stream().map(result -> result.value()).collect(toList());
    }

    public default List<AssertionStatus> assertionsStatusesOf(
            TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber) {
        return subscriber.values().stream()
                .map(result -> result.context().resolvedValueOf(result.rootExpression().targetExpression()))
                .collect(toList());
    }

    public default List<AssertionStatus> statusesOfAssertion(
            TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber, String name) {
        return subscriber.values().stream().map(detailedResult -> statusOfAssertion(detailedResult, name))
                .collect(toList());
    }

    public default AssertionStatus statusOfAssertion(
            DetailedExpressionResult<EvaluationStatus, AnalysisExpression> detailedResult, String name) {
        List<AssertionExpression> assertionsWithName = detailedResult.rootExpression().targetExpression().getChildren()
                .stream().filter(assertion -> assertion.name().equals(name)).collect(toList());

        if (assertionsWithName.size() != 1) {
            throw new IllegalArgumentException(
                    format("%s assertions match the name %s. Name should be unique", assertionsWithName.size(), name));
        }

        return detailedResult.context().resolvedValueOf(assertionsWithName.get(0));
    }
}
