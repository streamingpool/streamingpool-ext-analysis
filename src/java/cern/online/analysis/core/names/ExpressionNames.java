/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.names;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.tensorics.core.expressions.BinaryPredicateExpression;
import org.tensorics.core.expressions.ConversionOperationExpression;
import org.tensorics.core.math.predicates.BinaryPredicate;
import org.tensorics.core.tree.domain.ResolvedExpression;
import org.tensorics.expression.PredicateExpression;

import cern.online.analysis.core.expression.AssertionExpression;
import cern.streaming.pool.ext.tensorics.expression.StreamIdBasedExpression;

public final class ExpressionNames {

    public static final BiFunction<AssertionExpression, Function<Object, String>, String> FROM_ASSERTION_EXPRESSION = ExpressionNames::fromAssertion;

    @SuppressWarnings("rawtypes")
    public static final BiFunction<BinaryPredicateExpression, Function<Object, String>, String> FROM_BINARY_PREDICATE_EXPRESSION = ExpressionNames::fromBinaryPredicate;

    @SuppressWarnings("rawtypes")
    public static final Function<ResolvedExpression, String> FROM_RESOLVED_EXPRESSION = ExpressionNames::fromResolvedExpression;

    @SuppressWarnings("rawtypes")
    public static final BiFunction<PredicateExpression, Function<Object, String>, String> FROM_PREDICATE_EXPRESSION = ExpressionNames::fromPredicateExpression;

    @SuppressWarnings("rawtypes")
    public static final BiFunction<ConversionOperationExpression, ? extends Function<Object, String>, String> FROM_CONVERSION_EXPRESSION = ExpressionNames::fromConversionExpression;

    private ExpressionNames() {
        /* Only static methods */
    }

    public static String fromAssertion(AssertionExpression expression, Function<Object, String> callback) {
        return callback.apply(expression.condition());
    }

    public static String fromBinaryPredicate(BinaryPredicateExpression<?> exp, Function<Object, String> callback) {
        BinaryPredicate<?> predicate = exp.getPredicate();
        String operatorName = predicate.getClass().getSimpleName();

        String leftName = callback.apply(exp.getLeft());
        String rightName = callback.apply(exp.getRight());

        return leftName + " " + operatorName + " " + rightName;
    }

    public static String fromResolvedExpression(ResolvedExpression<?> exp) {
        return "" + exp.get();
    }

    public static String fromPredicateExpression(PredicateExpression<?> exp, Function<Object, String> callback) {
        String leftName = callback.apply(exp.source());
        String rightName = callback.apply(exp.predicate());
        return leftName + " is " + rightName;
    }

    public static final String fromConversionExpression(ConversionOperationExpression<?, ?> exp,
            Function<Object, String> callback) {
        return callback.apply(exp.getOperation()) + "(" + callback.apply(exp.getSource()) + ")";
    }

    public static final String fromStreambasedExpression(StreamIdBasedExpression<?> exp,
            Function<Object, String> callback) {
        return callback.apply(exp.streamId());
    }

}
