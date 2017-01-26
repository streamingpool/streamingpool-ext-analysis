// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
* 
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
// @formatter:on

package org.streamingpool.ext.analysis.names;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.streamingpool.ext.analysis.expression.AssertionExpression;
import org.streamingpool.ext.tensorics.expression.StreamIdBasedExpression;
import org.tensorics.core.expressions.BinaryPredicateExpression;
import org.tensorics.core.expressions.ConversionOperationExpression;
import org.tensorics.core.math.predicates.BinaryPredicate;
import org.tensorics.core.tree.domain.ResolvedExpression;
import org.tensorics.expression.PredicateExpression;

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
