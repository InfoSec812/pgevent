package org.apache.camel.pgevent;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class PgEventComponentTest extends CamelTestSupport {

    @Test
    public void testPgEvent() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);       
        
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("pgevent://foo")
                  .to("pgevent://bar")
                  .to("mock:result");
            }
        };
    }
}
