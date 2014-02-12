package org.example.camel.five.one;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class CamelEipSplitAggregateTest extends CamelTestSupport {

    @Test
    public void testSplitAndAggregate() throws Exception {
        MockEndpoint result = getMockEndpoint("mock:result");
        result.expectedMessageCount(8);
        MockEndpoint vacation = getMockEndpoint("mock:vacation");
        vacation.expectedMessageCount(1);

        List<TouristTrap> destinations = Arrays.asList(new TouristTrap[] {
                new TouristTrap("Athens", "1", 10), 
                new TouristTrap("Toronto", "2", 7), 
                new TouristTrap("Moscow", "2", 12), 
                new TouristTrap("San Diego", "3", 8), 
                new TouristTrap("Aruba", "4", 0), 
                new TouristTrap("London", "4", 20), 
                new TouristTrap("Venice", "4", 4), 
                new TouristTrap("Cancun", "5", 0)});

        template.sendBodyAndHeader("direct:start", destinations, "account", "Hadrian");
        assertMockEndpointsSatisfied();

        Exchange out = vacation.getExchanges().get(0);
        List<Exchange> group = out.getProperty(Exchange.GROUPED_EXCHANGE, List.class);

        assertTrue(group.size() == 2);
        TouristTrap first = group.get(0).getIn().getBody(TouristTrap.class);
        TouristTrap second = group.get(1).getIn().getBody(TouristTrap.class);
        assertTrue((first.name == "Aruba" && second.name == "Cancun") 
        		|| (first.name == "Cancun" && second.name == "Aruba"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            	getContext().setTracing(true);
            	
                from("direct:start")
	                .split().body()
	                .to("log:splitter")
	                .to("mock:result")
                    .to("seda:travel");
                
                from("seda:travel")
                	.filter(new Predicate() {
						@Override
						public boolean matches(Exchange exchange) {
							TouristTrap body = exchange.getIn().getBody(TouristTrap.class);
							return body.museums == 0;
						}
                	})
                	.aggregate(constant(true)).completionSize(2).completionInterval(1000L).groupExchanges()
                	.to("log:vacation-time")
                	.to("mock:vacation");
            }
        };
    }

    public static final class TouristTrap {
        String name;
        String rank;
        int museums;             
        
        public TouristTrap(String name, String rank, int museums) {
            this.name = name;
            this.rank = rank;        
            this.museums = museums; 
        }

        @Override
        public String toString() {
        	return "Destination " + name + " ranked #" + rank 
        		+ (museums > 0 ? " featuring " + museums + " fine museums" : "");
        }
    }
}
