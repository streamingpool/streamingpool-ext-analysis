/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo.analysis;

import static cern.streaming.pool.core.testing.NamedStreamId.ofName;

import cern.streaming.pool.core.service.StreamId;

public class DemoStreams {

    public static final StreamId<Integer> START_ANALYSIS_A = ofName("start a");
    public static final StreamId<Boolean> BOOLEAN_INTERVAL = ofName("boolean interval");

}
