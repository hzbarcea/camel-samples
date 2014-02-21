package org.example.camel.eight.one;

import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelErrorHandlerTest extends CamelTestSupport {
    static final transient Logger LOG = LoggerFactory.getLogger(CamelErrorHandlerTest.class);

    @Test
    public void testErrorHandler() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:error");
        mock.expectedMessageCount(1);

        try {
	        template.sendBody("direct:start", "Hello world");
	        fail("Expected exception thrown");
        } catch (RuntimeCamelException e) {
        	assertTrue(e.getCause() instanceof BaseException);
        }

        assertMockEndpointsSatisfied();
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry jndi = super.createRegistry();
        jndi.bind("myBean", new MyBean());
        return jndi;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                errorHandler(deadLetterChannel("mock:error").redeliveryDelay(0).maximumRedeliveries(3));

                onException(BaseException.class)
                    .maximumRedeliveries(0)
                    .setHeader("ExampleHeader", constant("BaseException"))
                    .to("mock:error");
                onException(BaseException.class)
                    .maximumRedeliveries(0)
                    .setHeader("ExampleHeader", constant("ChildException"))
                    .to("mock:error");


                from("direct:start")
                    .to("bean:myBean?method=throwBase")
                    .to("mock:result");
            }
        };
    }

    public final static class MyBean {
    	public void throwBase(String body) throws Exception {
    		throw new BaseException("BaseException thrown when received " + body);
    	}
    	public void throwChild(String body) throws Exception {
    		throw new ChildException("ChildException thrown when received " + body);
    	}
    	public void throwWrapped(String body) throws Exception {
    		throw new RuntimeCamelException(new BaseException("BaseException thrown when received " + body));
    	}
    	public void throwOther(String body) throws Exception {
    		throw new IllegalArgumentException("IAE thrown when received " + body);
    	}
    }
    
    @SuppressWarnings("serial")
	public static class BaseException extends Exception {
    	public BaseException(String message) {
    		super(message);
    	}
    }

    @SuppressWarnings("serial")
	public static class ChildException extends BaseException {
    	public ChildException(String message) {
    		super(message);
    	}
    }
}
