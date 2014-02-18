package org.example.camel.four.two;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelJmsTest extends CamelTestSupport {
    static final transient Logger LOG = LoggerFactory.getLogger(CamelJmsTest.class);

    @Test
    public void testJmsQueue() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        mock.expectedMessageCount(1);

        template.sendBody("direct:hello", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:hello")
                    .to("activemq:queue:in")
                    .to("mock:sent");
                from("activemq:queue:in")
                    .to("log:HELLO")
                    .to("mock:result");
            }
        };
    }

}
