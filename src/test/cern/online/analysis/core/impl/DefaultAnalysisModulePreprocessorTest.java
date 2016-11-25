/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.online.analysis.core.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.tensorics.core.tree.domain.ResolvedExpression;

import cern.online.analysis.core.AnalysisExpression;
import cern.online.analysis.core.AnalysisModule;
import cern.online.analysis.core.AnalysisModulePreprocessor;
import cern.online.analysis.core.expression.AssertionGroupExpression;

public class DefaultAnalysisModulePreprocessorTest {

    private static final AnalysisModule EMPTY_MODULE = new AnalysisModule() {
        /* empty on purpose */
    };

    private AnalysisModulePreprocessor processor;

    @Before
    public void setUp() {
        processor = new DefaultAnalysisModulePreprocessor();
    }

    @Test
    public void emptyModuleIsCorrectlyProcessed() {
        AnalysisExpression result = processor.process(EMPTY_MODULE).expression();
        assertThat(result.targetExpression()).isInstanceOf(AssertionGroupExpression.class);
        assertThat(result.targetExpression().getChildren()).isEmpty();
    }

    @Test
    public void emptyModuleIsEnabledByDefault() {
        AnalysisExpression result = processor.process(EMPTY_MODULE).expression();
        assertThat(result.enablingExpression()).isInstanceOf(ResolvedExpression.class);
        assertThat(result.enablingExpression().get()).isTrue();
    }

}
