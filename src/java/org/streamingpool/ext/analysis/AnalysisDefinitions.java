/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import org.streamingpool.ext.analysis.modules.StreamBaseAnalysisModule;
import org.streamingpool.ext.tensorics.streamid.DetailedExpressionStreamId;
import org.tensorics.core.analysis.AnalysisModule;
import org.tensorics.core.analysis.AnalysisResult;
import org.tensorics.core.analysis.expression.AnalysisExpression;
import org.tensorics.core.analysis.expression.AssertionExpression;

public final class AnalysisDefinitions {

    private AnalysisDefinitions() {
        /* Only static methods */
    }

    public static DetailedExpressionStreamId<AnalysisResult, AnalysisExpression> detailedStreamIdFor(
            StreamBaseAnalysisModule<?> analysisModule) {
        AnalysisDefinition analysisDefinition = process(analysisModule);
        return DetailedExpressionStreamId.of(analysisDefinition.expression(), analysisDefinition.initalContext());
    }

    public static AnalysisStreamId streamIdFor(StreamBaseAnalysisModule<?> analysisModule) {
        AnalysisDefinition analysisDefinition = process(analysisModule);
        return new AnalysisStreamId(analysisDefinition);
    }

    public static AnalysisDefinition process(StreamBaseAnalysisModule<?> module) {
        AnalysisExpression assertionSet = assertionSetFrom(module);
        return new AnalysisDefinition(assertionSet, module.evaluationStrategy());
    }

    public static AnalysisExpression assertionSetFrom(AnalysisModule module) {
        return module.assertionBuilders().stream().map(AssertionExpression::new)
                .collect(collectingAndThen(toList(), AnalysisExpression::new));
    }

}
