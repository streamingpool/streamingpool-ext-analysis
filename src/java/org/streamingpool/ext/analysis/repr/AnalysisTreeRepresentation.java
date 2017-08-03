/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.repr;

import org.tensorics.core.util.chains.Chain;

public interface AnalysisTreeRepresentation {

    /**
     * Retrieves a representation which only retrieves a name when it is really well defined, otherwise it will retrieve
     * {@code null}. Currently, as well defined we mean that it has either a valid name method or is stored as a
     * constant in one of the classes in the default scanning range.
     *
     * @return a string representation, that only returns non-null values if it is well defined.
     */
    Chain<String> strictNaming();

    /**
     * Provides a naming representation, which will always returns a name and never null. The fallback here is currently
     * the simple class name.
     *
     * @return a function that maps objects to names and never returns {@code null}
     */
    Chain<String> fallbackNaming();

    /**
     * Retrieves a representation function, which expresses the expression tree like formula.
     *
     * @return a function that maps expressions to a text representing the formula
     */
    Chain<String> formulaLike();

}
