/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.ext.analysis.repr;

import static java.util.stream.Collectors.joining;

import java.util.function.Function;

import org.streamingpool.core.service.streamid.DerivedStreamId;
import org.streamingpool.core.service.streamid.OverlapBufferStreamId;
import org.streamingpool.ext.tensorics.expression.BufferedStreamExpression;
import org.streamingpool.ext.tensorics.expression.UnresolvedStreamIdBasedExpression;
import org.streamingpool.ext.tensorics.streamid.ExpressionBasedStreamId;
import org.tensorics.core.analysis.expression.AnalysisExpression;
import org.tensorics.core.analysis.expression.AssertionExpression;
import org.tensorics.core.expressions.BinaryOperationExpression;
import org.tensorics.core.expressions.BinaryPredicateExpression;
import org.tensorics.core.expressions.ConversionOperationExpression;
import org.tensorics.core.expressions.LatestOfExpression;
import org.tensorics.core.functional.expressions.FunctionalExpression;
import org.tensorics.core.tree.domain.ResolvedExpression;
import org.tensorics.core.util.chains.AbstractRecursiveRepresenter;

import com.google.common.collect.Streams;

public class ExpressionTreeFormulaCaseMatching extends AbstractRecursiveRepresenter<String> {

    private final Function<Object, String> strictNaming;

    public ExpressionTreeFormulaCaseMatching(Function<Object, String> strictNaming) {
        this.strictNaming = strictNaming;
    }

    public String repr(Iterable<?> iterable) {
        return "[" + Streams.stream(iterable).map(this::recurse).collect(joining(",")) + "]";
    }

    public String repr(FunctionalExpression<?> exp) {
        return strictNaming.apply(exp);
    }

    public String repr(LatestOfExpression<?> exp) {
        return "latestOf(" + recurse(exp.iterableExpression()) + ")";
    }

//    public String repr(Binary<?> exp) {
//        return "latestOf(" + recurse(exp.iterableExpression()) + ")";
//    }


    // public String repr(AnalysisExpression ae) {
    // return "Analysis: " + recurse(ae.targetExpression());
    // }

    public String repr(AnalysisExpression ag) {
        return "AssertionGroup: " + recurse(ag.getChildren());
    }

    public String repr(AssertionExpression ae) {
        return "assertThat(" + recurse(ae.condition()) + ")";
    }

    public String repr(BinaryPredicateExpression<?> exp) {
        String operatorName = exp.getPredicate().getClass().getSimpleName();
        return recurse(exp.getLeft()) + " " + operatorName + " " + recurse(exp.getRight());
    }

    public String repr(ResolvedExpression<?> exp) {
        return exp.get() == null ? "Null" : exp.get().toString();
    }

    public String repr(ConversionOperationExpression<?, ?> exp) {
        return recurse(exp.getOperation()) + "(" + recurse(exp.getSource()) + ")";
    }

    public String repr(UnresolvedStreamIdBasedExpression<?> exp) {
        return recurse(exp.streamIdExpression());
    }

    public String repr(BufferedStreamExpression<?> exp) {
        return recurse(exp.streamIdExpression());
    }

    public String repr(OverlapBufferStreamId<?> id) {
        return recurse(id.sourceId());
    }

    public String repr(ExpressionBasedStreamId<?> id) {
        return recurse(id.expression());
    }

    public String repr(DerivedStreamId<?, ?> id) {
        return recurse(id.conversion()) + "(" + recurse(id.sourceStreamId()) + ")";
    }

}
