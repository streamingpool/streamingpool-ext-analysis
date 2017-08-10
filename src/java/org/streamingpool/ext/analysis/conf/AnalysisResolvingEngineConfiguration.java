/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tensorics.core.analysis.resolver.AnalysisResolver;
import org.tensorics.core.analysis.resolver.AssertionResolver;

@Configuration
public class AnalysisResolvingEngineConfiguration {

    @Bean
    public AnalysisResolver assertionGroupResolver() {
        return new AnalysisResolver();
    }

    @Bean
    public AssertionResolver assertionResolver() {
        return new AssertionResolver();
    }

}