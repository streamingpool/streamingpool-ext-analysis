/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

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
