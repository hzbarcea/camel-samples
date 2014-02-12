package org.example.camel.five.one;

import javax.naming.Context;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;

public class CamelEipLoadBalanceTest extends CamelTestSupport {

    @Test
    public void testRoundRobin() throws Exception {
    	int expected = 10;
        send("direct:round-robin", expected);
    }

    @Test
    public void testRandom() throws Exception {
    	int expected = 10;
        send("direct:random", expected);
    }
    
    protected void send(String endpoint, int count) throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(count);

        for (int i = 0; i < count; i++) {
            template.sendBodyAndHeader(endpoint, "Hello World", "id", Integer.toString(i));
        }
        
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            	getContext().setTracing(true);
            	
                from("direct:round-robin")
	                .loadBalance().roundRobin()
	                    .to("seda:one")
	                    .to("seda:two")
	                    .to("seda:three")
	                .end()
	                .to("mock:result");
                
                from("direct:random")
	                .loadBalance().random()
	                    .to("seda:one")
	                    .to("seda:two")
	                    .to("seda:three")
	                .end()
	                .to("mock:result");
                
                from("seda:one").to("log:ONE");
                from("seda:two").to("log:TWO");
                from("seda:three").to("log:THREE");
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
