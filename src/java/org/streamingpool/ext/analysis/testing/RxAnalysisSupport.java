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

import org.streamingpool.core.service.diagnostic.ErrorStreamId;
import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.ext.analysis.AnalysisExpression;
import org.streamingpool.ext.analysis.AnalysisResult;
import org.streamingpool.ext.analysis.AnalysisStreamId;
import org.streamingpool.ext.analysis.AssertionStatus;
import org.streamingpool.ext.analysis.expression.AssertionExpression;
import org.streamingpool.ext.analysis.expression.AssertionGroupExpression;
import org.streamingpool.ext.analysis.modules.AnalysisModule;
import org.tensorics.core.expressions.EvaluationStatus;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;

public interface RxAnalysisSupport extends AnalysisTest, RxStreamSupport {

    default Flowable<AnalysisResult> rxFrom(AnalysisModule analysisModule) {
        AnalysisStreamId analysisId = analysisIdOf(analysisModule);
        PublishProcessor<AnalysisResult> plug = PublishProcessor.create();

        rxFrom(analysisId).subscribe(value -> plug.onNext(value));
        rxFrom(ErrorStreamId.of(analysisId)).subscribe(err -> plug.onError(err));

        return plug.onBackpressureBuffer();
    }

    default List<AssertionStatus> evaluationStatusesOf(
            TestSubscriber<DetailedExpressionResult<AssertionStatus, AssertionGroupExpression>> subscriber) {
        return subscriber.values().stream().map(DetailedExpressionResult::value).collect(toList());
    }

    default List<AssertionStatus> assertionsStatusesOf(TestSubscriber<AnalysisResult> subscriber) {
        return subscriber.values().stream()
                .map(result -> result.resolvingContext().resolvedValueOf(result.analysisExpression()))
                .collect(toList());
    }

    default List<AssertionStatus> statusesOfAssertion(TestSubscriber<AnalysisResult> subscriber, String name) {
        return subscriber.values().stream().map(detailedResult -> statusOfAssertion(detailedResult, name))
                .collect(toList());
    }

    default AssertionStatus statusOfAssertion(AnalysisResult result, String name) {
        List<AssertionExpression> assertionsWithName = result.analysisExpression().getChildren().stream()
                .filter(assertion -> assertion.name().equals(name)).collect(toList());

        if (assertionsWithName.size() != 1) {
            throw new IllegalArgumentException(
                    format("%s assertions match the name %s. Name should be unique", assertionsWithName.size(), name));
        }

        return result.resolvingContext().resolvedValueOf(assertionsWithName.get(0));
    }
}
