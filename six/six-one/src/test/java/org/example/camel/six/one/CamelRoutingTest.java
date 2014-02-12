package org.example.camel.six.one;

import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.Context;

import org.apache.camel.Body;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.jndi.JndiContext;
import org.example.camel.six.one.Individual;
import org.example.camel.six.one.Person;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelRoutingTest extends CamelTestSupport {
    static final transient Logger LOG = LoggerFactory.getLogger(CamelRoutingTest.class);

    @Test
    public void testRouting() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);

        template.sendBody("direct:start", "Hello world");

        assertMockEndpointsSatisfied();
        assertTrue("Should have an equal number of increments and decrements...", ExampleBean.COUNTER.get() == 0);

    }

    @Test
    public void testConversion() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);

        template.sendBody("direct:convert", new Person("John", "Doe", 20));

        assertMockEndpointsSatisfied();
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .to("bean:myBean?method=secondMethod")
                    .beanRef("myBean", "firstMethod")
                    .to("log:camel-example")
                    .log(LoggingLevel.WARN, "camel-log", "Processing message with id='${header.id}' body='${body}'")
                    .to("mock:result");

                from("direct:convert")
                    // .convertBodyTo(Individual.class)
                    .beanRef("fitness")
                    .to("log:camel-convert")
                    .to("mock:result");
            }
        };
    }
    
    @Override
    protected Context createJndiContext() throws Exception {
        JndiContext answer = new JndiContext();
        answer.bind("myBean", new ExampleBean());
        answer.bind("fitness", new FitnessBean());
        return answer;
    }

    public static class ExampleBean {
        public static AtomicInteger COUNTER = new AtomicInteger(0);

        public void firstMethod() {
            LOG.info("Invoked firstMethod()");
            COUNTER.incrementAndGet();
        }

        public void secondMethod() {
            LOG.info("Invoked secondMethod()");
            COUNTER.decrementAndGet();
        }
    }

    public static class FitnessBean {
        public static AtomicInteger COUNTER = new AtomicInteger(0);

        public void slim(@Body Individual in) {
            LOG.info("Invoked slim() for " + in);
            in.weight -= 20;
        }
    }
}
