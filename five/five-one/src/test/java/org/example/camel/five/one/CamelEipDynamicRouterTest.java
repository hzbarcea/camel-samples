package org.example.camel.five.one;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelEipDynamicRouterTest extends CamelTestSupport {

    @Test
    public void testRecipientList() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(3);

        template.sendBodyAndHeader("direct:recipient-list", "Hello World", "sendTo", "direct:non-latin,direct:english");
        template.sendBodyAndHeader("direct:recipient-list", "Bonjour Monde", "sendTo", "direct:french");
        template.sendBodyAndHeader("direct:recipient-list", "Guten Tag Welt", "sendTo", "direct:non-latin,direct:german");
        
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRoutingSlip() throws Exception {
        MockEndpoint nonLatin = getMockEndpoint("mock:non-latin");
        nonLatin.expectedMessageCount(2);

        template.sendBody("direct:routing-slip", "Hello World");
        template.sendBody("direct:routing-slip", "Bonjour Monde");
        template.sendBody("direct:routing-slip", "Guten Tag Welt");
        
        assertMockEndpointsSatisfied();
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:recipient-list")
                    .recipientList(header("sendTo"))
                    .to("mock:result");

                from("direct:routing-slip")
                    .choice()
                        .when(body().startsWith("Hello")).routingSlip(constant("direct:non-latin,direct:english")).end()
                        .when(body().startsWith("Bonjour")).routingSlip(constant("direct:french")).end()
                        .otherwise().routingSlip(constant("direct:non-latin,direct:german")).end();

                from("direct:non-latin").to("log:non-latin").to("mock:non-latin");
                from("direct:english").to("log:english").to("mock:english");
                from("direct:french").to("log:french").to("mock:french");
                from("direct:german").to("log:german").to("mock:german");
            }
        };
    }
}
