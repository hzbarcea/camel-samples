package org.example.camel.five.one;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelEipConsumersTest extends CamelTestSupport {
    private static final transient Logger LOG = LoggerFactory.getLogger(CamelEipConsumersTest.class);
    private IdempotentRepository<String> store;

    @Override
    public void setUp() throws Exception {
        store = MemoryIdempotentRepository.memoryIdempotentRepository();
        LOG.info("Created MemoryIdempotentRepository");
        super.setUp();
    }
    
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        LOG.info("Destroying MemoryIdempotentRepository");
        store = null;
    }

    @Test
    public void testEventDrivenConsumer() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);

        template.sendBodyAndHeader("direct:start", "Hello World", "id", "msg-0");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testIdempotentConsumer() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(5);

        template.sendBodyAndHeader("direct:idempotent", "Hello World 1", "id", "msg-0");
        template.sendBodyAndHeader("direct:idempotent", "Hello World 2", "id", "msg-1");
        template.sendBodyAndHeader("direct:idempotent", "Hello World 3", "id", "msg-0");
        template.sendBodyAndHeader("direct:idempotent", "Hello World 4", "id", "msg-2");
        template.sendBodyAndHeader("direct:idempotent", "Hello World 5", "id", "msg-0");
        template.sendBodyAndHeader("direct:idempotent", "Hello World 6", "id", "msg-3");
        template.sendBodyAndHeader("direct:idempotent", "Hello World 7", "id", "msg-1");
        template.sendBodyAndHeader("direct:idempotent", "Hello World 8", "id", "msg-0");
        template.sendBodyAndHeader("direct:idempotent", "Hello World 9", "id", "msg-4");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").routeId("camel-webinar")
                    .to("log:big-brother").to("mock:result");
                
                from("direct:idempotent").idempotentConsumer(header("id"), store)
                    .log(LoggingLevel.INFO, "big-brother", "Processing message with id='${header.id}' body='${body}'")
                    .to("mock:result");
            }
        };
    }
}
