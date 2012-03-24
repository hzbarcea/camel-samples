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

import org.apache.camel.bam.ActivityBuilder;
import org.apache.camel.bam.ProcessBuilder;
import org.apache.camel.util.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class ActivityMonitoringBuilder extends ProcessBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ActivityMonitoringBuilder.class);

    public ActivityMonitoringBuilder() {
    }

    public ActivityMonitoringBuilder(JpaTemplate jpaTemplate, TransactionTemplate transactionTemplate) {
    	super(jpaTemplate, transactionTemplate);
    }

    @Override
    public void configure() throws Exception {
        LOG.info("Configuring monitoring activities using the Camel BAM DSL in {}", getClass().getName());

        ActivityBuilder teller = activity("direct:monitor-teller").name("teller").correlate(xpath("/operation/@account"));
        ActivityBuilder office = activity("direct:monitor-office").name("office").correlate(xpath("/operation/@account"));
        ActivityBuilder errors = activity("direct:monitor-errors").name("errors").correlate(header("SampleRef"));
        
        office
            .starts().after(teller.completes())
            .expectWithin(Time.seconds(1))
            .errorIfOver(Time.seconds(1)).to("mock:overdue");
        
        errors
            .starts().after(teller.completes())
            .expectWithin(Time.seconds(1))
            .errorIfOver(Time.seconds(5)).to("mock:error");
    }
}
