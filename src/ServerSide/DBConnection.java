package ServerSide;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Taken from CAB302 prac 7 and modified to make it easier for setup of the application with a different server
 */
public class DBConnection {

    /**
     * The singleton instance of the database connection.
     */
    private static Connection instance = null;
    private static int TIMEOUT = 1000;

    /**
     * Constructor initializes the connection.
     */
    private DBConnection() {
        try {
            Properties props = new Properties();
            InputStream inputStream = this.getClass().getResourceAsStream("ServerSettings.props");
            //InputStream inputStream = new FileInputStream("ServerSettings.props");

            props.load(inputStream);
            inputStream.close();

            // Get the server information from the Server Settings txt file
            // specify the data source, username and password
            String subprotocol = props.getProperty("databaseProtocol");
            String url = props.getProperty("host");
            String port = props.getProperty("port");
            String username = props.getProperty("username");
            String password = props.getProperty("password");
            String schema = props.getProperty("schema");

            //Class.forName("org.sqlite.JDBC");

            // get a connection
            instance = DriverManager.getConnection("jdbc:" + subprotocol + ":"+ schema + ".db", username,
                    password);
            if (instance.isValid(TIMEOUT)) {
                System.out.println(subprotocol + " database connected on " + url + ":" + port);
            }
        } catch (SQLException sqle) {
            System.err.println(sqle);
        } catch (FileNotFoundException fnfe) {
            System.err.println(fnfe);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Provides global access to the singleton instance of the UrlSet.
     *
     * @return a handle to the singleton instance of the UrlSet.
     */
    public static Connection getInstance() {
        if (instance == null) {
            new DBConnection();
        }
        return instance;
    }
}
