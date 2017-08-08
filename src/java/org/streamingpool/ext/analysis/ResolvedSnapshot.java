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

import org.tensorics.core.tree.domain.Expression;
import org.tensorics.core.tree.domain.ResolvingContext;

/* Not sure yet if to deprecate this one! */
public class ResolvedSnapshot<R, E extends Expression<R>> {

    private final ResolvingContext context;
    private final E root;

    public ResolvedSnapshot(E rootExpression, ResolvingContext context) {
        this.root = rootExpression;
        this.context = context;
    }

    public ResolvingContext context() {
        return this.context;
    }

    public E root() {
        return root;
    }

}
