package org.apache.camel.pgevent;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.jdbc.PGDataSource;
import java.io.InvalidClassException;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.naming.directory.InvalidAttributesException;
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

    private static final String FORMAT1 = 
        "^pgevent://([^:]*):(\\d+?)/(\\w+?)/(\\w+?).*$";
    
    private static final String FORMAT2 =
        "^pgevent://([^:]*)/(\\w+?)/(\\w+?).*$";
    
    private static final String FORMAT3 =
        "^pgevent:///(\\w+?)/(\\w+?).*$";
    
    private static final String FORMAT4 =
        "^pgevent:(\\w+?)/(\\w+?)/(\\w+?).*$";

    @UriParam
    private String host = "localhost";

    @UriParam
    private Integer port = 5432;

    @UriParam
    private String database;

    @UriParam
    private String channel;

    @UriParam
    private String user = "postgres";

    @UriParam
    private String pass;

    @UriParam
    private DataSource datasource;

    private final String uri;

    private PGConnection dbConnection;

    public final PGConnection initJdbc() throws ClassNotFoundException, SQLException {
        PGConnection conn;
        if (this.getDatasource()!=null) {
            conn = (PGConnection)this.getDatasource().getConnection();
        } else {
            System.setProperty("pgjdbc.test.user", this.getUser());
            System.setProperty("pgjdbc.test.password", this.getPass());
            
            Class.forName("com.impossibl.postgres.jdbc.PGDriver");
            conn = (PGConnection)DriverManager.getConnection(
                "jdbc:pgsql://"+this.getHost()+":"+this.getPort()+"/"+
                this.getDatabase());
        }
        return conn;
    }

    @Override
    protected String createEndpointUri() {
        return "pgevent";
    }

    public PgEventEndpoint(String uri, PgEventComponent component) throws InvalidAttributesException {
        super(uri, component);
        this.uri = uri;
        parseUri();
    }

    public PgEventEndpoint(String uri, PgEventComponent component, DataSource dataSource) throws InvalidAttributesException {
        super(uri, component);
        this.uri = uri;
        this.datasource = dataSource;
        parseUri();
    }

    public PgEventEndpoint(String uri) throws InvalidAttributesException {
        super(uri);
        this.uri = uri;
        parseUri();
    }

    /**
     * Parse the provided URI and extract available parameters
     * 
     * @throws InvalidAttributesException if there is an error in the parameters
     */
    protected final void parseUri() throws InvalidAttributesException {
        if (uri.matches(FORMAT1)) {
            String[] parts = uri
                                .replaceFirst(FORMAT1, "$1:$2:$3:$4")
                                .split(":");
            host = parts[0];
            port = Integer.parseInt(parts[1]);
            database = parts[2];
            channel = parts[3];
        } else if (uri.matches(FORMAT2)) {
            String[] parts = uri
                                .replaceFirst(FORMAT2, "$1:$2:$3")
                                .split(":");
            host = parts[0];
            port = 5432;
            database = parts[1];
            channel = parts[2];
        } else if (uri.matches(FORMAT3)) {
            String[] parts = uri
                                .replaceFirst(FORMAT3, "$1:$2")
                                .split(":");
            host = "localhost";
            port = 5432;
            database = parts[0];
            channel = parts[1];
        } else if (uri.matches(FORMAT4)) {
            String[] parts = uri
                                .replaceFirst(FORMAT4, "$1:$2")
                                .split(":");
            database = parts[0];
            channel = parts[1];
        } else {
            throw new InvalidAttributesException("The provided URL does not "+
                "match the acceptable patterns.");
        }
    }

    @Override
    public Producer createProducer() throws Exception {
        validateInputs();
        return new PgEventProducer(this);
    }

    private void validateInputs() throws InvalidClassException, InvalidAttributesException {
        if (getChannel()==null || getChannel().length()==0) {
            throw new InvalidAttributesException("A required parameter was "+
                    "not set when creating this Endpoint (channel)");
        }
        if (datasource!=null) {
            if (!PGDataSource.class.isInstance(datasource)) {
                throw new InvalidClassException("The datasource passed to the "+
                    "pgevent component is NOT a PGDataSource class from the"+
                    "pgjdbc-ng library. See: "+
                    "https://github.com/impossibl/pgjdbc-ng");
            }
        } else {
            if (user==null) { 
                throw new InvalidAttributesException("A required parameter was "+
                    "not set when creating this Endpoint (pgUser or "+
                    "pgDataSource)");
            }
        }
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        validateInputs();
        dbConnection = initJdbc();
        PgEventConsumer consumer = new PgEventConsumer(this, processor);
        dbConnection.addNotificationListener(consumer);
        return consumer;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
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
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the pass
     */
    public String getPass() {
        return pass;
    }

    /**
     * @param pass the pass to set
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     * @return the datasource
     */
    public DataSource getDatasource() {
        return datasource;
    }

    /**
     * @param datasource the datasource to set
     */
    public void setDatasource(DataSource datasource) {
        this.datasource = datasource;
    }
}