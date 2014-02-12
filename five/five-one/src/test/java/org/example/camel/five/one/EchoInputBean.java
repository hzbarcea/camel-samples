package org.example.camel.five.one;

import org.apache.camel.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoInputBean {
    static final transient Logger LOG = LoggerFactory.getLogger(CamelEipTimerTest.class);
    private String prefix;

    public EchoInputBean() {
        this("ECHO");
    }
    
    public EchoInputBean(String prefix) {
        this.prefix = "[" + (prefix != null ? prefix : "?") + "] ";
    }
    
    public void echo(@Body String body) {
        CamelEipTimerTest.LOG.info(prefix + body);
    }
}
