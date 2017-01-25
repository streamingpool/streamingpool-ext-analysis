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

package cern.online.analysis.core.dsl;

import org.tensorics.core.tree.domain.Expression;

import cern.online.analysis.core.ConditionBuilder;

public class OngoingBooleanCondition extends OngoingCondition<Boolean> {

    public OngoingBooleanCondition(ConditionBuilder builder, Expression<Boolean> source) {
        super(builder, source);
    }

    public OngoingNamedCondition<Boolean> isTrue() {
        isEqualTo(true);
        return this;
    }

    public OngoingNamedCondition<Boolean> isFalse() {
        isEqualTo(false);
        return this;
    }
}
