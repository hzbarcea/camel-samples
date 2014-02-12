/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.camel.sample;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.processor.validation.PredicateValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessProcessBuilder extends RouteBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessProcessBuilder.class);

    @Override
    public void configure() throws Exception {
        LOG.info("Configuring routes using the Java DSL in {}", getClass().getName());
        Namespaces ns = new Namespaces("sample", "http://example.org/camel/sample")
            .add("xs", "http://www.w3.org/2001/XMLSchema");

        errorHandler(deadLetterChannel("mock:dlc"));

        from("direct:errors")
            // could do any processing we want here, say escalate if some condition is met
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    String header = (String) exchange.getIn().getHeader("SampleRef");
                    if (header != null && header.endsWith("007")) {
                        LOG.warn("Activity on James Bond accounts gets reported to MI6");
                    }
                }
            })
            .to("direct:monitor-errors"); 

        from("direct:bank")
            .onException(PredicateValidationException.class).handled(true).maximumRedeliveries(0).to("direct:errors").end()
            .to("direct:monitor-teller")
            .setHeader("SampleRef", ns.xpath("/operation/@ref", String.class))
            .validate(header("SampleRef").startsWith("C-"))
            .delay(3000)
            .to("log:BANK")
            .to("direct:monitor-office");
    }
}
