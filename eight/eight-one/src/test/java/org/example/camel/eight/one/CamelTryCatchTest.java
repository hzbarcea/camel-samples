package org.example.camel.eight.one;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelTryCatchTest extends CamelTestSupport {
    static final transient Logger LOG = LoggerFactory.getLogger(CamelTryCatchTest.class);

    @Test
    public void testTryCatch() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);

        template.sendBody("direct:start", "Hello world");

        assertMockEndpointsSatisfied();
        assertTrue(true);

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
                from("direct:start")
                    .doTry()
                        .to("mock:a")
                        .to("bean:myBean?method=throwBase")
                    .doCatch(BaseException.class)
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                            	LOG.info("FAILURE ENDPOINT: {}", exchange.getProperty(Exchange.FAILURE_ENDPOINT, String.class));
                            	LOG.info("FAILURE MESSAGE: {}", exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage());
                            }
                        })
                    .end()
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
