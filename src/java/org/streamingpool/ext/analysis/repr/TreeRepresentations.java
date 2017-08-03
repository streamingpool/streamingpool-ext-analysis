/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.repr;

import java.util.Set;

import org.tensorics.core.util.chains.Chain;
import org.tensorics.core.util.names.ClasspathConstantScanner;
import org.tensorics.core.util.names.NameRepository;

import com.google.common.collect.ImmutableSet;

public final class TreeRepresentations {

    private TreeRepresentations() {
        /* only static methods */
    }

    private final static Set<String> DEFAULT_BASE_PACKAGE_NAMES = //
    ImmutableSet.of("org.tensorics", "org.streamingpool", "cern.lhc");

    private final static AnalysisTreeRepresentation REPRESENTATION = createRepresentation();

    /**
     * @see AnalysisTreeRepresentation#strictNaming()
     */
    public final static Chain<String> strictNaming() {
        return REPRESENTATION.strictNaming();
    }

    /**
     * @see AnalysisTreeRepresentation#fallbackNaming()
     */
    public static final Chain<String> fallbackNaming() {
        return REPRESENTATION.fallbackNaming();
    }

    /**
     * @see AnalysisTreeRepresentation#strictNaming()
     */
    public final static Chain<String> formulaLike() {
        return REPRESENTATION.formulaLike();
    }

    public static final AnalysisTreeRepresentation repr() {
        return REPRESENTATION;
    }

    private static final AnalysisTreeRepresentation createRepresentation() {
        ClasspathConstantScanner scanner = new ClasspathConstantScanner(DEFAULT_BASE_PACKAGE_NAMES);
        NameRepository nameRepository = scanner.scan();
        return AnalysisTreeRepresentationImpl.usingNamesFrom(nameRepository);
    }

}
