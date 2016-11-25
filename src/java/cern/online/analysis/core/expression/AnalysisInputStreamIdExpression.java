/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.expression;

import static java.util.Collections.emptyList;

import java.util.List;

import org.tensorics.core.tensor.Tensor;
import org.tensorics.core.tree.domain.AbstractDeferredExpression;
import org.tensorics.core.tree.domain.Node;

import cern.streaming.pool.core.service.StreamId;

/**
 * Wrapper for a {@link StreamId} input of the analysis. This will accepts a streamid<T> and declare an
 * Expression<Tensor<T>>. This way we have type checks in the dsl and we can inject the buffering.
 * 
 * @author acalia
 * @param <R>
 */
@Deprecated
public class AnalysisInputStreamIdExpression<R> extends AbstractDeferredExpression<Tensor<R>> {

    private final StreamId<R> sourceStreamId;

    public AnalysisInputStreamIdExpression(StreamId<R> sourceStreamId) {
        this.sourceStreamId = sourceStreamId;
    }

    @Override
    public List<? extends Node> getChildren() {
        return emptyList();
    }

    public StreamId<R> sourceStreamId() {
        return sourceStreamId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sourceStreamId == null) ? 0 : sourceStreamId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnalysisInputStreamIdExpression<?> other = (AnalysisInputStreamIdExpression<?>) obj;
        if (sourceStreamId == null) {
            if (other.sourceStreamId != null)
                return false;
        } else if (!sourceStreamId.equals(other.sourceStreamId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TensorStreamIdBasedExpression [sourceStreamId=" + sourceStreamId + "]";
    }

}
