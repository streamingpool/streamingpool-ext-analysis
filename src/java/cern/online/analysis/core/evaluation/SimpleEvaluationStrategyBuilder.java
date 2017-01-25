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

package cern.online.analysis.core.evaluation;

import java.util.Objects;

import cern.streaming.pool.ext.tensorics.evaluation.EvaluationStrategy;
import cern.streaming.pool.ext.tensorics.evaluation.EvaluationStrategyBuilder;

/**
 * A mutable builder, which creates an Evaluation strategy. (Actually it is more a mutable container for the moment,
 * since the full strategy is set by a with method. However this is the mutable thing which is passed along the fluent
 * clause)
 * 
 * @author kfuchsbe
 */
public class SimpleEvaluationStrategyBuilder extends EvaluationStrategyBuilder {

    private EvaluationStrategy strategy;

    public SimpleEvaluationStrategyBuilder(EvaluationStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy must not be null");
    }

    @Override
    public EvaluationStrategy build() {
        if (strategy == null) {
            throw new IllegalStateException(
                    "No evaluation strategy defined! (Check e.g. triggered() and buffered() clauses in analysis module for completeness!)");
        }
        return this.strategy;
    }

}
