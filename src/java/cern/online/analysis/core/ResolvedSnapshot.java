/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core;

import org.tensorics.core.expressions.BinaryPredicateExpression;
import org.tensorics.core.math.predicates.BinaryPredicate;
import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.Node;
import org.tensorics.core.tree.domain.ResolvingContext;
import org.tensorics.core.tree.walking.Trees;
import org.tensorics.expression.PredicateExpression;

import cern.online.analysis.core.expression.AssertionExpression;
import cern.streaming.pool.core.names.NameRepository;

public class ResolvedSnapshot<R, E extends Expression<R>> {

    private final ResolvingContext context;
    private final NameRepository nameRepository;
    private final E root;

    public ResolvedSnapshot(E rootExpression, ResolvingContext context, NameRepository nameRepository) {
        super();
        this.root = rootExpression;
        this.context = context;
        this.nameRepository = nameRepository;
    }

    public String detailedStringFor(Node exp) {
        StringBuffer detailedResult = new StringBuffer();
        for (Node child : Trees.findBottomNodes(exp)) {
            // if (child.equals(INJECTION_ATTEMPT)) {
            // import static cern.lhc.filling.core.expressions.InjectionExpressions.INJECTION_ATTEMPT;
            // continue;
            // }
            // if (child instanceof ResolvedExpression) {
            // continue;
            // }
            String childResult = context.resolvedValueOf((Expression<?>) child).toString();
            String childName = recursiveNameFor(child);
            detailedResult.append(childName).append("=").append(childResult).append("; ");
        }
        return detailedResult.toString();
    }

    /*
     * XXX MAKE THIS NICER!? Visitor?
     */

    public String nameFor(AssertionExpression exp) {
        if (exp.name() != null) {
            return exp.name();
        }

        return recursiveNameFor(exp.condition());
    }

    public String nameFor(Node exp) {
        return recursiveNameFor(exp);
    }

    private String recursiveNameFor(Node exp) {
        if (exp instanceof BinaryPredicateExpression) {
            BinaryPredicateExpression<?> binaryPredicateExpression = (BinaryPredicateExpression<?>) exp;
            BinaryPredicate<?> predicate = binaryPredicateExpression.getPredicate();
            String operatorName = predicate.getClass().getSimpleName();

            String leftName = recursiveNameFor(binaryPredicateExpression.getLeft());
            String rightName = recursiveNameFor(binaryPredicateExpression.getRight());

            return leftName + " " + operatorName + " " + rightName;
        }
        // if (exp instanceof ResolvedExpression) {
        // return "" + ((ResolvedExpression<?>) exp).get();
        // }
        if (exp instanceof PredicateExpression) {
            String leftName = recursiveNameFor(((PredicateExpression<?>) exp).source());
            String rightName = recursiveNameFor(((PredicateExpression<?>) exp).predicate());
            return leftName + " is " + rightName;
        }
        return nameRepository.nameFor(exp);
    }

    public ResolvingContext context() {
        return this.context;
    }

    public E root() {
        return root;
    }

    public NameRepository nameRepository() {
        return this.nameRepository;
    }

}
