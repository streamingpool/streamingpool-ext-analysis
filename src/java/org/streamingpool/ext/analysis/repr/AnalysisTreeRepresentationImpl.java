/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.repr;

import java.util.function.Function;

import org.tensorics.core.util.chains.Chain;
import org.tensorics.core.util.chains.Chains;
import org.tensorics.core.util.names.ImmutableNameRepository;
import org.tensorics.core.util.names.NameRepository;
import org.tensorics.core.util.names.Names;

public class AnalysisTreeRepresentationImpl implements AnalysisTreeRepresentation {

    private final NameRepository nameRepository;
    private final Chain<String> strictNaming;
    private final Chain<String> fallbackNaming;
    private final Chain<String> formulaRepr;

    private AnalysisTreeRepresentationImpl(NameRepository nameRepository) {
        this.nameRepository = nameRepository;
        this.strictNaming = createStrictNaming();
        this.fallbackNaming = createFallbackNaming();
        this.formulaRepr = createFormulaRepr();
    }

    public static AnalysisTreeRepresentationImpl usingNamesFrom(NameRepository nameRepository) {
        return new AnalysisTreeRepresentationImpl(nameRepository);
    }

    public static AnalysisTreeRepresentationImpl usingEmptyNameRepo() {
        return usingNamesFrom(ImmutableNameRepository.empty());
    }

    /**
     * Retrieves a representation which only retrieves a name when it is really well defined, otherwise it will retrieve
     * {@code null}. Currently, as well defined we mean that it has either a valid name method or is stored as a
     * constant in one of the classes in the default scanning range.
     *
     * @return a string representation, that only returns non-null values if it is well defined.
     */
    @Override
    public final Chain<String> strictNaming() {
        return this.strictNaming;
    }

    private final Chain<String> createStrictNaming() {
        // @formatter:off
        return Chains.chainFor(String.class)
                .either(Names::fromNameMethod)
                .or(Names::fromGetNameMethod)
                .or(nameRepository::nameFor)
                .orElseNull();
        // @formatter:on
    }

    /**
     * Provides a naming representation, which will always returns a name and never null. The fallback here is currently
     * the simple class name.
     *
     * @return a function that maps objects to names and never returns {@code null}
     */
    @Override
    public final Chain<String> fallbackNaming() {
        return this.fallbackNaming;
    }

    private final Chain<String> createFallbackNaming() {
        // @formatter:off
        return Chains.chainFor(String.class)
                .either(strictNaming())
                .or(Names::fromSimpleClassName)
                .orElseThrow(); /* Should never happen, because each object has a class name ;-)*/
        // @formatter:on
    }

    /**
     * @see AnalysisTreeRepresentation#formulaLike()
     */
    @Override
    public Chain<String> formulaLike() {
        return this.formulaRepr;
    }

    private Chain<String> createFormulaRepr() {
        // @formatter:off
        Function<Object, String> endRecursion =
                Chains.chainFor(String.class)
                      .either(strictNaming())
                      .orElse("[..]");
        // @formatter:on

        // @formatter:off
        return Chains.chainFor(String.class)
                .endRecursionWith(endRecursion)
                .endRecursionDefaultDepth(100)
                .matchCasesFrom(ExpressionTreeFormulaCaseMatching.class, () ->  new ExpressionTreeFormulaCaseMatching(
                        strictNaming()))
                .or(fallbackNaming())
                .orElseNull();
        // @formatter:on
    }

}
