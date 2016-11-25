/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import org.tensorics.core.tree.domain.Expression;
import org.tensorics.expression.WindowedExpression;

import cern.online.analysis.core.expression.AssertionGroupExpression;

public class AnalysisExpression extends WindowedExpression<AssertionGroupExpression> {

    public static AnalysisExpression of(AssertionGroupExpression targetExpression,
            Expression<Boolean> enablingExpression) {
        return new AnalysisExpression(targetExpression, enablingExpression);
    }

    private AnalysisExpression(AssertionGroupExpression targetExpression, Expression<Boolean> enablingExpression) {
        super(targetExpression, enablingExpression);
    }
}
