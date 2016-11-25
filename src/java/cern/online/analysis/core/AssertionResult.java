/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import cern.online.analysis.core.expression.AssertionExpression;

public class AssertionResult {

    private final AssertionExpression assertion;
    private final ResolvedSnapshot<?, ?> snapshot;

    private AssertionResult(AssertionExpression assertion, ResolvedSnapshot<?, ?> snapshot) {
        this.assertion = assertion;
        this.snapshot = snapshot;
    }

    public static AssertionResult of(AssertionExpression assertion, ResolvedSnapshot<?, ?> snapshot) {
        return new AssertionResult(assertion, snapshot);
    }

    public String condition() {
        return snapshot.nameFor(assertion);
    }

    public AssertionStatus status() {
        return snapshot.context().resolvedValueOf(assertion);
    }

    public String detailedStringResult() {
        return snapshot.detailedStringFor(assertion);
    }

    public AssertionExpression assertion() {
        return this.assertion;
    }

    public ResolvedSnapshot<?, ?> snapshot() {
        return this.snapshot;
    }

    @Override
    public String toString() {
        return "ConditionResult [assertion=" + assertion + ", status=" + status() + ", detailedStringResult="
                + detailedStringResult() + "]";
    }

}
