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

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigiRequest {
	public static final String REQUEST = "C0 80 05 00 54 72 C0 C0 80 05 00 54 72 C0";
    private static final Logger LOG = LoggerFactory.getLogger(DigiRequest.class);

	public String prepare(@Header(value = "DigiId") String id) {
    	// We can use the id to prepare a personalized request
    	// ... but hardcode for now, per 'requirements'
		String request = REQUEST;
		LOG.info("Prepared request for digi id='{}' with value='{}'", id, request);
    	return request;
    }
}
