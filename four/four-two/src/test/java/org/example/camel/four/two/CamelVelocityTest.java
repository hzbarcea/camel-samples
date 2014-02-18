package org.example.camel.four.two;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelVelocityTest extends CamelTestSupport {
    static final transient Logger LOG = LoggerFactory.getLogger(CamelVelocityTest.class);

    @Test
    public void testVelocityTransformation() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        String result = template.requestBody("direct:transform", 
            new Agent("007", "James", "Bond", "M", "Aston Martin"), String.class);
        assertMockEndpointsSatisfied();
        assertEquals("My name is Bond, James Bond", result.substring(0, result.indexOf('\n')));

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:transform")
                    .to("velocity:templates/transform.vm")
                    .to("mock:result");
            }
        };
    }

    public static class Agent {
    	private String id;
    	private String fname;
    	private String lname;
    	private String manager;
    	private String vehicle;

    	public Agent(String id, String fname, String lname, String manager, String vehicle) {
			this.id = id;
			this.fname = fname;
			this.lname = lname;
			this.manager = manager;
			this.vehicle = vehicle;
    	}
    	public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getFname() {
			return fname;
		}
		public void setFname(String fname) {
			this.fname = fname;
		}
		public String getLname() {
			return lname;
		}
		public void setLname(String lname) {
			this.lname = lname;
		}
		public String getManager() {
			return manager;
		}
		public void setManager(String manager) {
			this.manager = manager;
		}
		public String getVehicle() {
			return vehicle;
		}
		public void setVehicle(String vehicle) {
			this.vehicle = vehicle;
		}
    }
}
