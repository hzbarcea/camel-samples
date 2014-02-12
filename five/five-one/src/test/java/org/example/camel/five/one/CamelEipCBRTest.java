package org.example.camel.five.one;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelEipCBRTest extends CamelTestSupport {

    @Test
    public void testContentBasedRouter() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(8);

        template.sendBodyAndHeader("direct:start", "Athens", "rank", "1");
        template.sendBodyAndHeader("direct:start", "Toronto", "rank", "2");
        template.sendBodyAndHeader("direct:start", "Moscow", "rank", "2");
        template.sendBodyAndHeader("direct:start", "San Diego", "rank", "3");
        template.sendBodyAndHeader("direct:start", "Aruba", "rank", "4");
        template.sendBodyAndHeader("direct:start", "London", "rank", "4");
        template.sendBodyAndHeader("direct:start", "Venice", "rank", "4");
        template.sendBodyAndHeader("direct:start", "Cancun", "rank", "5");
        
        assertMockEndpointsSatisfied();

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            	getContext().setTracing(true);
            	
                from("direct:start")
	                .choice()
	                	.when(header("rank").isEqualTo("5")).to("log:five-star")
	                	.when(header("rank").isEqualTo("4")).to("log:four-star")
	                	.when(header("rank").isEqualTo("3")).to("log:three-star")
	                	.otherwise().to("log:maybe-some-other-time")
	                .end()
	                .to("mock:result");
            }
        };
    }
}
