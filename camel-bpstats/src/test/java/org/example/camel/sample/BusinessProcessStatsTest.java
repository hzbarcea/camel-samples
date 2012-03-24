/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.camel.sample;

import org.apache.camel.bam.model.ActivityState;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class BusinessProcessStatsTest extends CamelSpringTestSupport{
    @Override
    protected ClassPathXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
    }

    @Test
    public void testNoErrors() throws Exception {
        MockEndpoint o = getMockEndpoint("mock:overdue");
        o.expectedMessageCount(1);
        
        template.sendBody("direct:bank", "<operation ref='C-0001' type='credit'><account>camel.001</account><amount>100.00</amount></operation>");
        this.assertMockEndpointsSatisfied();

    	ActivityState state = o.getExchanges().get(0).getIn().getBody(ActivityState.class);
    	assertNotNull(state);
    	assertEquals("office", state.getActivityDefinition().getName());
    }

    @Test
    public void testProcessErrors() throws Exception {
        getMockEndpoint("mock:overdue").expectedMessageCount(0);  // activity 'office' does not trigger anymore
        MockEndpoint e = getMockEndpoint("mock:error");
        e.expectedMinimumMessageCount(1);
        
        // will fail validation...
        template.sendBody("direct:bank", "<operation ref='X-007' type='debit'><account>camel.007</account><amount>1000000.00</amount></operation>");
        this.assertMockEndpointsSatisfied();
    }
}
