/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis;

import java.io.Serializable;

import org.streamingpool.ext.analysis.expression.AnalysisExpression;
import org.tensorics.core.resolve.domain.DetailedExpressionResult;
import org.tensorics.core.tree.domain.Expression;

@Deprecated
public class DeprecatedAnalysisResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final DetailedExpressionResult<AnalysisResult, AnalysisExpression> detailedResult;

    private DeprecatedAnalysisResult(DetailedExpressionResult<AnalysisResult, AnalysisExpression> result) {
        this.detailedResult = result;
    }

    public static DeprecatedAnalysisResult fromResult(DetailedExpressionResult<AnalysisResult, AnalysisExpression> result) {
        return new DeprecatedAnalysisResult(result);
    }

    public AnalysisExpression analysisExpression() {
        return detailedResult.rootExpression();
    }

    public AssertionStatus evaluationStatus() {
        return detailedResult.value().overallStatus();
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
        DeprecatedAnalysisResult other = (DeprecatedAnalysisResult) obj;
        if (detailedResult == null) {
            if (other.detailedResult != null)
                return false;
        } else if (!detailedResult.equals(other.detailedResult))
            return false;
        return true;
    }

    public AssertionStatus overallStatus() {
        return detailedResult.value().overallStatus();
    }

    public <T> T resolvedValueOf(Expression<T> exp) {
        return detailedResult.context().resolvedValueOf(exp);
    }

    public boolean resolves(Expression<?> exp) {
        return detailedResult.context().resolves(exp);
    }

}
