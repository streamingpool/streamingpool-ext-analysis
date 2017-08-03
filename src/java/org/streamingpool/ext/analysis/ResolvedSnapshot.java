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

import java.util.Optional;

import org.streamingpool.ext.analysis.expression.AssertionExpression;
import org.streamingpool.ext.analysis.repr.AnalysisTreeRepresentation;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.Node;
import org.tensorics.core.tree.domain.ResolvingContext;
import org.tensorics.core.tree.walking.Trees;

public class ResolvedSnapshot<R, E extends Expression<R>> {

    private final ResolvingContext context;
    private final AnalysisTreeRepresentation repr;
    private final E root;

    public ResolvedSnapshot(E rootExpression, ResolvingContext context, AnalysisTreeRepresentation repr) {
        super();
        this.root = rootExpression;
        this.context = context;
        this.repr = repr;
    }

    public String detailedStringFor(Node exp) {
        StringBuilder detailedResult = new StringBuilder();
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

    public String keyFor(AssertionExpression exp) {
        return Optional.ofNullable(exp.key()).orElse(formatAsKey(nameFor(exp)));
    }

    public static String formatAsKey(String name) {
        return name.toLowerCase().replaceAll("_", "-").replaceAll("[ ,:;\\[\\(\\]\\)]+", " ").trim().replaceAll(" ",
                ".") + ".[GENERATED]";
    }

    public String nameFor(Node exp) {
        return repr.formulaLike().apply(exp);
    }

    public ResolvingContext context() {
        return this.context;
    }

    public E root() {
        return root;
    }

    public AnalysisTreeRepresentation repr() {
        return repr;
    }
}
