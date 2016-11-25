/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import org.tensorics.expression.WindowedExpression;

/**
 * Transforms an {@link AnalysisModule} into a {@link WindowedExpression}.
 * 
 * @author acalia, caguiler, kfuchsbe
 */
public interface AnalysisModulePreprocessor {

    /**
     * Compiles the specified {@link AnalysisModule} into the corresponding {@link WindowedExpression}
     */
    AnalysisDefinition process(AnalysisModule module);

}
