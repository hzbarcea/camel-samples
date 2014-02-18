package org.example.camel.four.two;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelDirectTest extends CamelTestSupport {
    @SuppressWarnings("unused")
	private static final transient Logger LOG = LoggerFactory.getLogger(CamelDirectTest.class);
    private static final String COMMON_URI = "direct:common";

    @Test
    public void testInMemoryEndpoints() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(3);
        mock.expectedBodiesReceived("Goodbye World");
        
        template.sendBody("direct:channel", "Hello World");
        template.sendBody("seda:channel", "Hello World");
        template.sendBody("vm:channel", "Hello World");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:channel").to(COMMON_URI);
                from("seda:channel").to(COMMON_URI);
                from("vm:channel").to(COMMON_URI);

                from(COMMON_URI)
                    .setBody(constant("Goodbye World"))
                    .to("mock:result");
            }
        };
    }
    
}
