package org.apache.camel.pgevent;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

/**
 * The PgEvent consumer.
 */
public class PgEventConsumer extends DefaultConsumer {
    private final PgEventEndpoint endpoint;

    public PgEventConsumer(PgEventEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }
}
