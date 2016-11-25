/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo.analysis;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tensorics.core.tree.domain.ResolvingContext;

import cern.online.analysis.core.AnalysisDefinition;
import cern.online.analysis.core.AnalysisModulePreprocessor;
import cern.online.analysis.core.AnalysisStreamId;
import cern.online.analysis.core.expression.AssertionGroupExpression;
import cern.streaming.pool.core.support.RxStreamSupport;
import cern.streaming.pool.core.testing.AbstractStreamTest;

/**
 * Demo for a simple analysis
 * 
 * @author acalia
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DemoConfiguration.class })
public class DemoMain extends AbstractStreamTest implements RxStreamSupport {

    @Autowired
    private AnalysisModulePreprocessor preprocessor;

    @Test
    public void testTriggeredAnalysis() throws InterruptedException {
        DemoTriggeredAnalysis demoAnalysis = new DemoTriggeredAnalysis();
        AnalysisDefinition analysisDefinition = preprocessor.process(demoAnalysis);
        AnalysisStreamId analysisId = new AnalysisStreamId(analysisDefinition); 

        CountDownLatch sync = new CountDownLatch(1);
        rxFrom(analysisId).take(5).doOnTerminate(sync::countDown).subscribe(result -> {
            ResolvingContext context = result.context();
            System.out.println("\nAnalysis evaluation status: " + result.value());
            AssertionGroupExpression assertions = result.rootExpression().targetExpression();
            System.out.println("\tAssertions status: " + context.resolvedValueOf(assertions));
        });
        sync.await();
    }

}
