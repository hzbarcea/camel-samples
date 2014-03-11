package org.example.camel.four.two;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelNettyProxyTest extends CamelTestSupport {
    static final transient Logger LOG = LoggerFactory.getLogger(CamelNettyProxyTest.class);

    @Test
    public void testTcpProxy() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        String result = template.requestBody("netty:tcp://localhost:9201", "Hello World", String.class);
        assertMockEndpointsSatisfied();
        assertEquals("Hello World", result);

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("netty:tcp://localhost:9201?sync=true")
                    .to("netty:tcp://localhost:9211?sync=true");
                from("netty:tcp://localhost:9211?sync=true")
                    .to("log:ECHO")
                    .to("mock:result");
            }
        };
    }
}
