package org.apache.camel.pgevent;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PgEvent producer.
 */
public class PgEventProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(PgEventProducer.class);
    private PgEventEndpoint endpoint;

    public PgEventProducer(PgEventEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }

}
