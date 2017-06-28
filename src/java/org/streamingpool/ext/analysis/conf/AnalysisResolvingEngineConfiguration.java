/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.streamingpool.ext.analysis.resolver.AssertionGroupResolver;
import org.streamingpool.ext.analysis.resolver.AssertionResolver;
import org.tensorics.core.resolve.engine.ResolvingEngine;
import org.tensorics.core.resolve.engine.ResolvingEngines;
import org.tensorics.core.resolve.resolvers.IterableResolvingExpressionResolver;
import org.tensorics.core.resolve.resolvers.PickResolver;
import org.tensorics.core.resolve.resolvers.PredicateConditionResolver;
import org.tensorics.core.resolve.resolvers.WindowedExpressionResolver;

@Configuration
public class AnalysisResolvingEngineConfiguration {

    @Bean
    public ResolvingEngine createResolvingEngine() {
        return ResolvingEngines.defaultEngineWithAdditional(new AssertionGroupResolver(), new AssertionResolver(),
                new PredicateConditionResolver<>(), new IterableResolvingExpressionResolver<>(),
                new WindowedExpressionResolver(), new PickResolver<>());
    }

}
