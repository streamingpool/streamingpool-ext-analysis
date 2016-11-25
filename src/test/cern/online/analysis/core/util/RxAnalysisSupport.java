/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.util;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.tensorics.core.resolve.domain.DetailedExpressionResult;
import org.tensorics.expression.EvaluationStatus;

import cern.online.analysis.core.AnalysisExpression;
import cern.online.analysis.core.AnalysisModule;
import cern.online.analysis.core.AssertionStatus;
import cern.online.analysis.core.expression.AssertionExpression;
import cern.streaming.pool.core.support.RxStreamSupport;
import rx.Observable;
import rx.observers.TestSubscriber;

public interface RxAnalysisSupport extends AnalysisTest, RxStreamSupport {

    public default Observable<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> rxFrom(
            AnalysisModule analysisModule) {
        return rxFrom(analysisIdOf(analysisModule));
    }

    public default List<EvaluationStatus> evaluationStatusesOf(
            TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber) {
        return subscriber.getOnNextEvents().stream().map(result -> result.value()).collect(toList());
    }

    public default List<AssertionStatus> assertionsStatusesOf(
            TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber) {
        return subscriber.getOnNextEvents().stream()
                .map(result -> result.context().resolvedValueOf(result.rootExpression().targetExpression()))
                .collect(toList());
    }

    public default List<AssertionStatus> statusesOfAssertion(
            TestSubscriber<DetailedExpressionResult<EvaluationStatus, AnalysisExpression>> subscriber, String name) {
        return subscriber.getOnNextEvents().stream().map(detailedResult -> statusOfAssertion(detailedResult, name))
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
