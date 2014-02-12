package org.example.camel.five.one;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelEipEnricherTest extends CamelTestSupport {

    @Test
    public void testEnrich() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        template.sendBody("direct:start", "Hello world");
        
        assertMockEndpointsSatisfied();

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            	getContext().setTracing(true);
            	
                from("direct:start")
	                .enrich("direct:enrich", new AggregationStrategy() {
	            		@Override
	            		public Exchange aggregate(Exchange first, Exchange second) {
	                        if (second != null) {
	                            Object firstBody = first.getIn().getBody();
	                            Object secondBody = second.getIn().getBody();
	                            second.getOut().setBody(firstBody + "\n---\n" + secondBody);
	                        }
	            			return second;
	            		}        
	                }).to("log:enricheded").to("mock:result");
                
                from("direct:enrich")
                	.process(new Processor() {
	                    public void process(Exchange exchange) throws Exception {
	                        String s = exchange.getIn().getBody(String.class);
	                        exchange.getOut().setBody(s + " again...");
	                    }
	                }).to("log:processed");

            }
        };
    }
}
