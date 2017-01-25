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

package cern.online.analysis.core;

import static java.util.Objects.requireNonNull;

import org.tensorics.core.tree.domain.Expression;

public class AnalysisEnablerBuilder<T> {

    private Expression<T> startExpression;
    private Expression<T> endExpression;
    
    public AnalysisEnablerBuilder<T> withStartExpression(Expression<T> aStartExpression) {
        this.startExpression = requireNonNull(aStartExpression, "startExpression cannot be set to null");
        return this;
    }
    
    public AnalysisEnablerBuilder<T> withEndExpression(Expression<T> aEndExpression) {
        this.endExpression = requireNonNull(aEndExpression, "endExpression cannot be set to null");
        return this;
    }

    public Expression<T> getStartExpression() {
        return startExpression;
    }
    public Expression<T> getEndExpression() {
        return endExpression;
    }
    
    
}
