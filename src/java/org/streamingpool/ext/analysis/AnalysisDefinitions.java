/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import org.streamingpool.ext.analysis.expression.AssertionExpression;
import org.streamingpool.ext.analysis.expression.AssertionGroupExpression;
import org.streamingpool.ext.analysis.modules.AnalysisModule;

public final class AnalysisDefinitions {

    private AnalysisDefinitions() {
        /* Only static methods */

    }

    public static AnalysisStreamId streamIdFor(AnalysisModule<?> analysisModule) {
        AnalysisDefinition analysisDefinition = AnalysisDefinitions.process(analysisModule);
        return new AnalysisStreamId(analysisDefinition);
    }

    public static AnalysisDefinition process(AnalysisModule<?> module) {
        AssertionGroupExpression assertionSet = assertionSetFrom(module);
        return new AnalysisDefinition(assertionSet, module.evaluationStrategy());
    }

    private static AssertionGroupExpression assertionSetFrom(AnalysisModule<?> module) {
        return module.assertionBuilders().stream().map(AssertionExpression::new)
                .collect(collectingAndThen(toList(), AssertionGroupExpression::new));
    }

}
