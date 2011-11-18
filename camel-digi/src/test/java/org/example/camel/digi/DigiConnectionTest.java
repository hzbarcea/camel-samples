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

import org.apache.camel.Endpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DigiConnectionTest extends CamelSpringTestSupport{
    @Override
    protected ClassPathXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
    }

    @Test
    public void testDigiRequest() throws Exception {
        // are we producing the correct requests
        Object reply = template.requestBody("{{digi.prepare-request}}", "localhost:9900");

        assertNotNull(reply);
        assert(reply instanceof String);
        assertEquals(DigiRequest.REQUEST, reply);
    }

    @Test
    public void testDigiPingOne() throws Exception {
        MockEndpoint mock = null;
        Endpoint reply = getMandatoryEndpoint("{{digi.reply}}");
        if (reply instanceof MockEndpoint) {
            mock = (MockEndpoint)reply;
            mock.expectedBodiesReceived(DigiMockRouteBuilder.DIGI_REPLY);
        }
        template.sendBody("seda:digi", "localhost:9900"); // use one mock digi at random

        if (mock != null) {
            mock.assertIsSatisfied();
        }
    }

    @Test
    public void testDigiPingAll() throws Exception {
        MockEndpoint mock = null;
        Endpoint reply = getMandatoryEndpoint("{{digi.reply}}");
        if (reply instanceof MockEndpoint) {
            mock = (MockEndpoint)reply;
            mock.expectedMessageCount(6);
        }

        template.sendBody("{{digi.schedule-ping}}", ""); // no body, only need a trigger

        if (mock != null) {
            mock.assertIsSatisfied();
        }
    }
}
