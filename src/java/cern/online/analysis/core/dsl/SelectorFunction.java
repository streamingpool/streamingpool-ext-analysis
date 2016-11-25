/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import java.util.function.Function;

/**
 * Much like a standard {@link Function}. It exposes the single method using "of" for better legibility in some
 * conditions.
 * 
 * @see Function
 * @author acalia
 */
@FunctionalInterface
public interface SelectorFunction<T, U> {
    U of(T compound);
}
