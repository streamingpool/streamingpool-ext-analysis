/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import org.tensorics.core.commons.operations.Conversion;

/**
 * Reduces an iterable of {@link Boolean} into a single {@link Boolean} value. Implementations of this interface will
 * define reduction strategies.
 * 
 * @author acalia, caguiler, kfuchsberger
 */
public interface IterableBooleanConversion extends Conversion<Iterable<Boolean>, Boolean> {
    /* marker interface */
}
