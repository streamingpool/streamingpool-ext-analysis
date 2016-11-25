/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

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
