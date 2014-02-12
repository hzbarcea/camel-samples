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
package org.example.camel.digi;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigiMockRouteBuilder extends RouteBuilder {
      public static final String DIGI_REPLY = "C0 81 1F 00 00 00 00 00 3E 00 01 "
          + "73 9F 00 50 00 06 07 40 46 EE B1 99 F6 3A 00 4E BA AE 6F 33 6D C0";

    private static final Logger LOG = LoggerFactory.getLogger(DigiMockRouteBuilder.class);
    private static final int PORT_START = 9900;

    @Override
    public void configure() throws Exception {
        for (int i = 0; i < 10; i++) {
            from("netty:tcp://localhost:" + (PORT_START + i) + "?sync=true")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        LOG.info("Digi at {} received request: {}", 
                            exchange.getFromEndpoint().getEndpointKey(), exchange.getIn().getBody());
                        exchange.getOut().setBody(DIGI_REPLY);
                    }
                });
        }
    }
}
