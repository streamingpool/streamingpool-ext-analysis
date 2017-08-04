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

import org.streamingpool.ext.analysis.expression.AssertionExpression;

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
        return assertion.name();
    }

    public AssertionStatus status() {
        return snapshot.context().resolvedValueOf(assertion);
    }

    public AssertionExpression assertion() {
        return this.assertion;
    }

    public ResolvedSnapshot<?, ?> snapshot() {
        return this.snapshot;
    }

    @Override
    public String toString() {
        return "AssertionResult [assertion=" + assertion + ", snapshot=" + snapshot + "]";
    }
}
