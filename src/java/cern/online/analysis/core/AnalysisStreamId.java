/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import org.tensorics.core.tree.domain.ResolvingContext;
import org.tensorics.expression.EvaluationStatus;

import cern.streaming.pool.ext.tensorics.streamid.DetailedExpressionStreamId;

/**
 * Specialization of a {@link DetailedExpressionStreamId} specifically for the analysis framework
 *
 * @author acalia
 */
public class AnalysisStreamId extends DetailedExpressionStreamId<EvaluationStatus, AnalysisExpression> {

    public AnalysisStreamId(AnalysisDefinition analysisDefinition) {
        super(analysisDefinition.expression(), analysisDefinition.evaluationStrategy());
    }

    public AnalysisStreamId(AnalysisDefinition analysisDefinition, ResolvingContext initialCtxForAnalysis) {
        super(analysisDefinition.expression(), analysisDefinition.evaluationStrategy(), initialCtxForAnalysis);
    }
}
