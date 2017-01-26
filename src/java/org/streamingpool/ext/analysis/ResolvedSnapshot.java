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

package org.streamingpool.ext.analysis;

import static org.streamingpool.core.names.resolve.Names.FROM_NAME_METHOD;
import static org.streamingpool.ext.analysis.names.ExpressionNames.FROM_ASSERTION_EXPRESSION;
import static org.streamingpool.ext.analysis.names.ExpressionNames.FROM_BINARY_PREDICATE_EXPRESSION;
import static org.streamingpool.ext.analysis.names.ExpressionNames.FROM_CONVERSION_EXPRESSION;
import static org.streamingpool.ext.analysis.names.ExpressionNames.FROM_PREDICATE_EXPRESSION;
import static org.streamingpool.ext.analysis.names.ExpressionNames.FROM_RESOLVED_EXPRESSION;

import java.util.function.Function;

import org.streamingpool.core.names.resolve.Chains;
import org.streamingpool.core.names.resolve.Names;
import org.streamingpool.core.service.streamid.DerivedStreamId;
import org.streamingpool.core.service.streamid.OverlapBufferStreamId;
import org.streamingpool.ext.analysis.expression.AssertionExpression;
import org.streamingpool.ext.analysis.names.ExpressionNames;
import org.streamingpool.ext.tensorics.expression.StreamIdBasedExpression;
import org.streamingpool.ext.tensorics.streamid.ExpressionBasedStreamId;
import org.tensorics.core.expressions.BinaryPredicateExpression;
import org.tensorics.core.expressions.ConversionOperationExpression;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.Node;
import org.tensorics.core.tree.domain.ResolvedExpression;
import org.tensorics.core.tree.domain.ResolvingContext;
import org.tensorics.core.tree.walking.Trees;
import org.tensorics.expression.PredicateExpression;

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
        this.nameResolving = createFullNameResolving();
    }

    private final Function<Object, String> createFullNameResolving() {
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
            @SuppressWarnings("unused")
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
