/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import static cern.online.analysis.core.names.ExpressionNames.FROM_ASSERTION_EXPRESSION;
import static cern.online.analysis.core.names.ExpressionNames.FROM_BINARY_PREDICATE_EXPRESSION;
import static cern.online.analysis.core.names.ExpressionNames.FROM_CONVERSION_EXPRESSION;
import static cern.online.analysis.core.names.ExpressionNames.FROM_PREDICATE_EXPRESSION;
import static cern.online.analysis.core.names.ExpressionNames.FROM_RESOLVED_EXPRESSION;
import static cern.streaming.pool.core.names.resolve.Names.FROM_NAME_METHOD;

import java.util.function.Function;

import org.tensorics.core.expressions.BinaryPredicateExpression;
import org.tensorics.core.expressions.ConversionOperationExpression;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.Node;
import org.tensorics.core.tree.domain.ResolvedExpression;
import org.tensorics.core.tree.domain.ResolvingContext;
import org.tensorics.core.tree.walking.Trees;
import org.tensorics.expression.PredicateExpression;

import cern.online.analysis.core.expression.AssertionExpression;
import cern.online.analysis.core.names.ExpressionNames;
import cern.streaming.pool.core.names.resolve.Chains;
import cern.streaming.pool.core.names.resolve.Names;
import cern.streaming.pool.core.service.streamid.DerivedStreamId;
import cern.streaming.pool.core.service.streamid.OverlapBufferStreamId;
import cern.streaming.pool.ext.tensorics.expression.StreamIdBasedExpression;
import cern.streaming.pool.ext.tensorics.streamid.ExpressionBasedStreamId;

public class ResolvedSnapshot<R, E extends Expression<R>> {

    private final ResolvingContext context;
    private final Function<Object, String> nameRepository;
    private final Function<Object, String> nameResolving;
    private final E root;

    public ResolvedSnapshot(E rootExpression, ResolvingContext context, Function<Object, String> nameRepository) {
        super();
        this.root = rootExpression;
        this.context = context;
        this.nameRepository = nameRepository;
        this.nameResolving = createFullNameResolving(nameRepository);
    }

    private final Function<Object, String> createFullNameResolving(Function<Object, String> nameRepository) {
        // @formatter:off
        return Chains.<String>chain()
                .or(nameRepository)
                .or(FROM_NAME_METHOD)
                .or(Names::fromGetNameMethod)
                .branchCase(AssertionExpression.class, FROM_NAME_METHOD).or(FROM_ASSERTION_EXPRESSION).orElseNull()
                .branchCase(BinaryPredicateExpression.class, FROM_BINARY_PREDICATE_EXPRESSION).orElseNull()
                .branchCase(ResolvedExpression.class, FROM_RESOLVED_EXPRESSION).orElseNull()
                .branchCase(PredicateExpression.class, FROM_PREDICATE_EXPRESSION).orElseNull()
                .branchCase(ConversionOperationExpression.class, FROM_NAME_METHOD).or(FROM_CONVERSION_EXPRESSION).orElseNull()
                .branchCase(StreamIdBasedExpression.class, ExpressionNames::fromStreambasedExpression).orElseNull()
                .branchCase(OverlapBufferStreamId.class, (id,f) -> f.apply(id.sourceId())).orElseNull()
                .branchCase(ExpressionBasedStreamId.class, (id, f) -> f.apply(id.getDetailedId())).orElseNull()
                .branchCase(DerivedStreamId.class, (id,f) -> f.apply(id.conversion()) + "(" + f.apply(id.sourceStreamId()) + ")").orElseNull()
                .or(Names::fromSimpleClassName)
                .orElseNull();
        // @formatter:on
    }

    public String detailedStringFor(Node exp) {
        StringBuffer detailedResult = new StringBuffer();
        for (Node child : Trees.findBottomNodes(exp)) {
            // if (child.equals(INJECTION_ATTEMPT)) {
            // continue;
            // }
            // if (child instanceof ResolvedExpression) {
            // continue;
            // }
            String childResult = context.resolvedValueOf((Expression<?>) child).toString();
            // String childName = recursiveNameFor(child);
            return "DETAIL String not resolved";
            // detailedResult.append(childName).append("=").append(childResult).append("; ");
        }
        return detailedResult.toString();
    }

    /*
     * XXX Remove unnecessary stuff
     */

    public String nameFor(AssertionExpression exp) {
        return nameResolving.apply(exp);
    }

    public String nameFor(Node exp) {
        return nameResolving.apply(exp);
    }

    public ResolvingContext context() {
        return this.context;
    }

    public E root() {
        return root;
    }

    public Function<Object, String> nameRepository() {
        return this.nameRepository;
    }

}
