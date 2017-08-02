/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.streamingpool.ext.analysis.resolver.AssertionGroupResolver;
import org.streamingpool.ext.analysis.resolver.AssertionResolver;

@Configuration
public class AnalysisResolvingEngineConfiguration {

    @Bean
    public AssertionGroupResolver assertionGroupResolver() {
        return new AssertionGroupResolver();
    }

    @Bean
    public AssertionResolver assertionResolver() {
        return new AssertionResolver();
    }

}