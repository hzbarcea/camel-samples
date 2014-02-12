package org.example.camel.five.one;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.Context;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.jndi.JndiContext;
import org.junit.Test;

public class CamelEipTranslatorTest extends CamelTestSupport {
    final Processor translatorProcessor = new Processor() {
        public void process(Exchange exchange) {
            exchange.getOut().setBody("Goodbye World");
        }
    };

    @Test
    public void testSetBody() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        template.sendBody("direct:set-body", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBean() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        template.sendBody("direct:bean", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testProcessor() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        template.sendBody("direct:processor", "Hello World");
        assertMockEndpointsSatisfied();
    }


    @Test
    public void testTemplateEngine() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("id", "ID-123");
        headers.put("bug-id", "CAMEL-0000");
        template.sendBodyAndHeaders("direct:template", "Hello World", headers);
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:set-body").setBody().constant("Goodbye World").to("log:logger").to("mock:result");
                from("direct:bean").to("bean:translatorBean").to("log:logger").to("mock:result");
                from("direct:processor").process(translatorProcessor).to("log:logger").to("mock:result");
                from("direct:template").to("velocity:templates/report.vm").to("log:logger").to("mock:result");
            }
        };
    }
    
    @Override
    protected Context createJndiContext() throws Exception {
        JndiContext answer = new JndiContext();
        answer.bind("translatorBean", new TranslatorBean());
        return answer;
    }

    public static class TranslatorBean {
        public static AtomicInteger COUNTER = new AtomicInteger(0);

        public String firstMethod(@Body String body) {
            return "Goodbye World";
        }
    }
}
