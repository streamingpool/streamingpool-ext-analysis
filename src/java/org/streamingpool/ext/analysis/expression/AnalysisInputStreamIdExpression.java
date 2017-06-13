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

package org.streamingpool.ext.analysis.expression;

import static java.util.Collections.emptyList;

import java.util.List;

import org.streamingpool.core.service.StreamId;
import org.tensorics.core.tensor.Tensor;
import org.tensorics.core.tree.domain.AbstractDeferredExpression;
import org.tensorics.core.tree.domain.Node;

/**
 * Wrapper for a {@link StreamId} input of the analysis. This will accepts a {@code StreamId<T>} and declare an
 * {@code Expression<Tensor<T>>}. This way we have type checks in the dsl and we can inject the buffering.
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
