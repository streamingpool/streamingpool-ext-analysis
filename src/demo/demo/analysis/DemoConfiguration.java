/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo.analysis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.tensorics.core.resolve.engine.ResolvingEngine;
import org.tensorics.core.resolve.engine.ResolvingEngines;
import org.tensorics.core.resolve.resolvers.PickResolver;
import org.tensorics.expression.resolvers.IterableResolvingExpressionResolver;
import org.tensorics.expression.resolvers.PredicateConditionResolver;
import org.tensorics.expression.resolvers.WindowedExpressionResolver;

import cern.online.analysis.core.conf.AnalysisConfiguration;
import cern.online.analysis.core.resolver.AssertionGroupResolver;
import cern.online.analysis.core.resolver.AssertionResolver;
import cern.streaming.pool.core.conf.DefaultStreamFactories;
import cern.streaming.pool.core.conf.EmbeddedPoolConfiguration;
import cern.streaming.pool.core.conf.StreamCreatorFactoryConfiguration;
import cern.streaming.pool.ext.tensorics.conf.TensoricsStreamingConfiguration;

@Configuration
@Import({ AnalysisConfiguration.class, TensoricsStreamingConfiguration.class, DefaultStreamFactories.class,
        EmbeddedPoolConfiguration.class, StreamCreatorFactoryConfiguration.class, DemoStreamCreators.class })
public class DemoConfiguration {
    /* Just configuration */

    @Bean
    public ResolvingEngine createResolvingEngine() {
        return ResolvingEngines.defaultEngineWithAdditional(new AssertionGroupResolver(), new AssertionResolver(),
                new PredicateConditionResolver<>(), new IterableResolvingExpressionResolver<>(),
                new WindowedExpressionResolver(), new PickResolver<>());
    }

}
