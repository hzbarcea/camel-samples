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
package org.example.camel.filesplit;

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSplitRouteBuilder extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(FileSplitRouteBuilder.class);
    private static final int PORT = 9000;
    
    @Override
    public void configure() throws Exception {
        from("{{demo.source}}").routeId("fetch")
            .multicast().parallelProcessing()
                .split(FileSplitter.chunks(1024))
                .loadBalance().roundRobin().to("seda:x", "seda:y", "seda:z");

        createRoute("seda:x");
        createRoute("seda:y");
        createRoute("seda:z");

        /*        
        from("netty:tcp://localhost:" + PORT + "?sync=true")
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    LOG.info("Daemon at localhost:{} received request: {}", 
                        exchange.getFromEndpoint().getEndpointKey(), exchange.getIn().getBody());
                    exchange.getOut().setBody("Bye World!");
                }
            });
        */
    }
    
    private void createRoute(String uri) {
    	from(uri)
    	    .convertBodyTo(InputStream.class)
            .split(body().tokenize("\n"))
            .process(new Processor() {
				@Override
				public void process(Exchange exchange) throws Exception {
					LOG.info("SPLIT: {}", exchange.getIn().getBody());
				}
            });
    }
}
