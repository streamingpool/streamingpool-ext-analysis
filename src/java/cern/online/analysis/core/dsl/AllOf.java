/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.dsl;

import static java.lang.Boolean.TRUE;
import static java.util.stream.StreamSupport.stream;

/**
 * {@link IterableBooleanConversion} that performs a logical AND over all the elements of the iterable.
 * 
 * @author acalia, caguiler, kfuchsberger
 */
public class AllOf implements IterableBooleanConversion {

    @Override
    public Boolean apply(Iterable<Boolean> booleanList) {
        return stream(booleanList.spliterator(), false).allMatch(TRUE::equals);
    }

    @Override
    public String toString() {
        return "AllOf";
    }

}
