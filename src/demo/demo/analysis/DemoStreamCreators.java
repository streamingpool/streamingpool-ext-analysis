/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo.analysis;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static demo.analysis.DemoStreams.BOOLEAN_INTERVAL;
import static demo.analysis.DemoStreams.START_ANALYSIS_A;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.Observable.interval;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.streaming.pool.core.service.impl.IdentifiedStreamCreator;
import cern.streaming.pool.core.service.util.StreamCreators;

@Configuration
public class DemoStreamCreators {

    @Bean
    public IdentifiedStreamCreator<Integer> startStream() {
        return StreamCreators.create(discovery -> fromRx(interval(1, SECONDS).map(Long::intValue)))
                .as(START_ANALYSIS_A);
    }

    @Bean
    public IdentifiedStreamCreator<Boolean> booleanIntervalStream() {
        return StreamCreators.create(discovery -> fromRx(interval(1, SECONDS).map(n -> n % 2 == 0)))
                .as(BOOLEAN_INTERVAL);
    }
}
