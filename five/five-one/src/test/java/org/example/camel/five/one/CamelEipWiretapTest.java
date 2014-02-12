package org.example.camel.five.one;

import javax.naming.Context;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;

public class CamelEipWiretapTest extends CamelTestSupport {

    @Test
    public void testWiretap() throws Exception {
        int expected = 2;
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(expected);

        template.sendBody("direct:start", "Hello World");
        
        assertMockEndpointsSatisfied();

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                getContext().setTracing(true);
                
                from("direct:start")
                    .wireTap("seda:wiretap")
                    .to("bean:foo")
                    .to("mock:result");

                from("seda:wiretap")
                    .delay(3000).setBody().simple("${in.body} again...")
                    .to("bean:bar?method=echo")
                    .to("mock:wiretap")
                    .to("mock:result");
            }
        };
    }
    
    @Override
    protected Context createJndiContext() throws Exception {
        JndiContext answer = new JndiContext();
        answer.bind("foo", new EchoInputBean());
        answer.bind("bar", new EchoInputBean("WIRETAP"));
        return answer;
    }
}
