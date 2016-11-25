/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.online.analysis.core.impl.DefaultAnalysisModulePreprocessor;

@Configuration
public class AnalysisConfiguration {
    
    @Bean
    public DefaultAnalysisModulePreprocessor moduleProcessor() {
        return new DefaultAnalysisModulePreprocessor();
    }
    
}
