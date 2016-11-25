/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo.analysis;

import static demo.analysis.DemoStreams.BOOLEAN_INTERVAL;
import static org.tensorics.core.tree.domain.ResolvedExpression.of;

import cern.online.analysis.core.AnalysisModule;
import cern.streaming.pool.ext.tensorics.expression.StreamIdBasedExpression;

public class DemoTriggeredAnalysis extends AnalysisModule {

    public DemoTriggeredAnalysis() {
        triggered().by(DemoStreams.START_ANALYSIS_A);
        assertBoolean(of(true)).isTrue();
        assertBoolean(StreamIdBasedExpression.of(BOOLEAN_INTERVAL)).isTrue();
    }
}
