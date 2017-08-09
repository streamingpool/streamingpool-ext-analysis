/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis;

import java.io.Serializable;

import org.streamingpool.ext.analysis.expression.AssertionGroupExpression;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvingContext;

public class AnalysisResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final DetailedExpressionResult<AssertionStatus, AssertionGroupExpression> detailedResult;

    private AnalysisResult(DetailedExpressionResult<AssertionStatus, AssertionGroupExpression> result) {
        this.detailedResult = result;
    }

    public static AnalysisResult fromResult(
            DetailedExpressionResult<AssertionStatus, AssertionGroupExpression> result) {
        return new AnalysisResult(result);
    }

    public AssertionGroupExpression analysisExpression() {
        return detailedResult.rootExpression();
    }

    public ResolvingContext resolvingContext() {
        return detailedResult.context();
    }

    public AssertionStatus evaluationStatus() {
        return detailedResult.value();
    }

    @Override
    public String toString() {
        return "AnalysisResult [result=" + detailedResult + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.detailedResult == null) ? 0 : this.detailedResult.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnalysisResult other = (AnalysisResult) obj;
        if (detailedResult == null) {
            if (other.detailedResult != null)
                return false;
        } else if (!detailedResult.equals(other.detailedResult))
            return false;
        return true;
    }

    public AssertionStatus overallStatus() {
        return resolvingContext().resolvedValueOf(analysisExpression());
    }

    public <T> T resolvedValueOf(Expression<T> exp) {
        return resolvingContext().resolvedValueOf(exp);
    }

}
