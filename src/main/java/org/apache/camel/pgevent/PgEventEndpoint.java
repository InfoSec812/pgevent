package org.apache.camel.pgevent;

import com.impossibl.postgres.jdbc.PGDataSource;
import java.io.InvalidClassException;
import java.security.InvalidParameterException;
import javax.sql.DataSource;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

/**
 * Represents a PgEvent endpoint.
 */
@UriEndpoint(scheme = "pgevent")
public class PgEventEndpoint extends DefaultEndpoint {

    @UriParam
    private String pgHost = "localhost";

    @UriParam
    private Integer pgPort = 5432;

    @UriParam
    private String pgDatabase;

    @UriParam
    private String channel;

    @UriParam
    private String pgUser = "postgres";

    @UriParam
    private String pgPass;

    @UriParam
    private DataSource pgDataSource;

    /**
     * @return the pgHost
     */
    public String getPgHost() {
        return pgHost;
    }

    /**
     * @param pgHost the pgHost to set
     */
    public void setPgHost(String pgHost) {
        this.pgHost = pgHost;
    }

    /**
     * @return the pgPort
     */
    public Integer getPgPort() {
        return pgPort;
    }

    /**
     * @param pgPort the pgPort to set
     */
    public void setPgPort(Integer pgPort) {
        this.pgPort = pgPort;
    }

    /**
     * @return the pgDatabase
     */
    public String getPgDatabase() {
        return pgDatabase;
    }

    /**
     * @param pgDatabase the pgDatabase to set
     */
    public void setPgDatabase(String pgDatabase) {
        this.pgDatabase = pgDatabase;
    }

    /**
     * @return the channel
     */
    public String getChannel() {
        return channel;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * @return the pgUser
     */
    public String getPgUser() {
        return pgUser;
    }

    /**
     * @param pgUser the pgUser to set
     */
    public void setPgUser(String pgUser) {
        this.pgUser = pgUser;
    }

    /**
     * @return the pgPass
     */
    public String getPgPass() {
        return pgPass;
    }

    /**
     * @param pgPass the pgPass to set
     */
    public void setPgPass(String pgPass) {
        this.pgPass = pgPass;
    }

    /**
     * @return the pgDataSource
     */
    public DataSource getPgDataSource() {
        return pgDataSource;
    }

    /**
     * @param pgDataSource the pgDataSource to set
     */
    public void setPgDataSource(DataSource pgDataSource) {
        this.pgDataSource = pgDataSource;
    }

    public PgEventEndpoint() {
    }

    public PgEventEndpoint(String uri, PgEventComponent component) {
        super(uri, component);
    }

    public PgEventEndpoint(String endpointUri) {
        super(endpointUri);
    }

    @Override
    public Producer createProducer() throws Exception {
        validateInputs();
        return new PgEventProducer(this);
    }

    private void validateInputs() throws InvalidClassException, InvalidParameterException {
        if (pgDataSource!=null) {
            if (!PGDataSource.class.isInstance(pgDataSource)) {
                throw new InvalidClassException("The datasource passed to the "+
                    "pgevent component is NOT a PGDataSource class from the"+
                    "pgjdbc-ng library. See: "+
                    "https://github.com/impossibl/pgjdbc-ng");
            }
        } else {
            if (pgUser==null || channel==null) { 
                throw new InvalidParameterException("A required parameter was "+
                    "not set when creating this Endpoint (pgUser, "+
                    "pgDataSource, or channel)");
            }
        }
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new PgEventConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}