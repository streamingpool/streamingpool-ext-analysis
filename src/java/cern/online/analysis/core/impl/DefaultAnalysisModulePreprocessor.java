/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.impl;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import org.tensorics.core.tree.domain.Expression;

import cern.online.analysis.core.AnalysisDefinition;
import cern.online.analysis.core.AnalysisExpression;
import cern.online.analysis.core.AnalysisModule;
import cern.online.analysis.core.AnalysisModulePreprocessor;
import cern.online.analysis.core.expression.AssertionExpression;
import cern.online.analysis.core.expression.AssertionGroupExpression;

public class DefaultAnalysisModulePreprocessor implements AnalysisModulePreprocessor {

    @Override
    public AnalysisDefinition process(AnalysisModule module) {
        AssertionGroupExpression assertionSet = assertionSetFrom(module);
        Expression<Boolean> enablerExpression = enablerExpressionFrom(module);
        return new AnalysisDefinition(AnalysisExpression.of(assertionSet, enablerExpression),
                module.evaluationStrategy());
    }

    private AssertionGroupExpression assertionSetFrom(AnalysisModule module) {
        return module.assertionBuilders().stream().map(AssertionExpression::new)
                .collect(collectingAndThen(toList(), AssertionGroupExpression::new));
    }

    private Expression<Boolean> enablerExpressionFrom(AnalysisModule module) {
        return module.enablingBuilder().build();
    }
}
