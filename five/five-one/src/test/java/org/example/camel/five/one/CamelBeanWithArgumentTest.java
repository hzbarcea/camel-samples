package org.example.camel.five.one;

import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.Context;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelBeanWithArgumentTest extends CamelTestSupport {
    static final transient Logger LOG = LoggerFactory.getLogger(CamelBeanWithArgumentTest.class);

    @Test
    public void testTimer() throws Exception {
    	int expected = 10;
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(expected);

        assertMockEndpointsSatisfied();
        assertTrue("Should have fired 2 or more times was: " + ExampleBean.COUNTER.get(), ExampleBean.COUNTER.get() >= expected);

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            	from("timer://eip?fixedRate=true&delay=0&period=500")
            		.beanRef("myBean", "firstMethod")
            		.to("mock:result");
            }
        };
    }
    
    @Override
    protected Context createJndiContext() throws Exception {
        JndiContext answer = new JndiContext();
        answer.bind("myBean", new ExampleBean());
        return answer;
    }

    public static class ExampleBean {
        public static AtomicInteger COUNTER = new AtomicInteger(0);

        public void firstMethod() {
            LOG.info("Invoked someMethod()");
            COUNTER.incrementAndGet();
        }

        public void secondMethod() {
            LOG.info("Invoked secondMethod()");
            COUNTER.decrementAndGet();
        }
    }
}
