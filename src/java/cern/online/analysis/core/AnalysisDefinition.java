/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import static java.util.Objects.requireNonNull;

import cern.streaming.pool.ext.tensorics.evaluation.EvaluationStrategy;

public class AnalysisDefinition {

    private final AnalysisExpression expression;
    private final EvaluationStrategy evaluationStrategy;

    public AnalysisDefinition(AnalysisExpression expression, EvaluationStrategy evaluationStrategy) {
        super();
        this.expression = requireNonNull(expression, "expression must not be null");
        this.evaluationStrategy = requireNonNull(evaluationStrategy, "evaluationStrategy must not be null");
    }

    public AnalysisExpression expression() {
        return expression;
    }

    public EvaluationStrategy evaluationStrategy() {
        return evaluationStrategy;
    }
}
