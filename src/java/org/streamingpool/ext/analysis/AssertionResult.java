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

import java.io.Serializable;

import org.streamingpool.ext.analysis.expression.AssertionExpression;
import org.tensorics.core.tree.domain.ResolvingContext;

public class AssertionResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private final AssertionExpression assertion;
    private final ResolvingContext resolvingCtx;

    private AssertionResult(AssertionExpression assertion, ResolvingContext resolvingCtx) {
        this.assertion = assertion;
        this.resolvingCtx = resolvingCtx;
    }

    public static AssertionResult of(AssertionExpression assertion, ResolvingContext resolvingCtx) {
        return new AssertionResult(assertion, resolvingCtx);
    }

    public String condition() {
        return assertion.name();
    }

    public AssertionStatus status() {
        return resolvingCtx.resolvedValueOf(assertion);
    }

    public AssertionExpression assertion() {
        return this.assertion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((assertion == null) ? 0 : assertion.hashCode());
        result = prime * result + ((resolvingCtx == null) ? 0 : resolvingCtx.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AssertionResult other = (AssertionResult) obj;
        if (assertion == null) {
            if (other.assertion != null) {
                return false;
            }
        } else if (!assertion.equals(other.assertion)) {
            return false;
        }
        if (resolvingCtx == null) {
            if (other.resolvingCtx != null) {
                return false;
            }
        } else if (!resolvingCtx.equals(other.resolvingCtx)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AssertionResult [assertion=" + assertion + ", resolvingCtx=" + resolvingCtx + "]";
    }

}
