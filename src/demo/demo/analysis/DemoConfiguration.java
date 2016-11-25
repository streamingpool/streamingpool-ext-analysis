/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo.analysis;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import cern.lhc.filling.core.conf.commons.TensoricsConfiguration;
import cern.online.analysis.core.conf.AnalysisConfiguration;
import cern.streaming.pool.core.conf.DefaultStreamFactories;
import cern.streaming.pool.core.conf.EmbeddedPoolConfiguration;
import cern.streaming.pool.core.conf.StreamCreatorFactoryConfiguration;

@Configuration
@Import({ AnalysisConfiguration.class, TensoricsConfiguration.class, DefaultStreamFactories.class,
        EmbeddedPoolConfiguration.class, StreamCreatorFactoryConfiguration.class, DemoStreamCreators.class })
public class DemoConfiguration {
    /* Just configuration */
}
