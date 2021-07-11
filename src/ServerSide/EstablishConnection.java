package ServerSide;

import java.io.*;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.util.Properties;

/**
 * Establishes the connection between the client and server
 * @author Alistair Ridge
 */
public class EstablishConnection {
    // Network Interaction ---------------------------------------------------------------------------------------------
    /*
    Creates a network connection and sends the prepared statements ove to the DB.
     */
    private static Socket socket;
    public static String username;
    public static String password;

    /**
     * Used to establish a network connection with the user specified server.
     */
    public EstablishConnection() {
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream("./Setup/ServerSettings.props");
            props.load(in);
            in.close();

            // Get the server information from the Server Settings txt file
            String host = props.getProperty("host");
            String port = props.getProperty("port");
            username = props.getProperty("username");
            password = props.getProperty("password");

            // get a connection
            socket = new Socket(host, Integer.parseInt(port));

            if (socket.isConnected()) {
                System.out.println("Socket successfully connected to " + host + " on port " + port);
            }

        } catch (FileNotFoundException fnfe) {
            System.err.println(fnfe);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Writes the prepared SQL statements to the database via the network connection
     */
    public void storeAddresses(PreparedStatement statement) {
        try {
            try (
                    ObjectOutputStream objectOutputStream =
                            new ObjectOutputStream(socket.getOutputStream())
            ) {
                objectOutputStream.writeObject(statement);
            }
        } catch (IOException e) {
            // Print the exception, but no need for a fatal error
            // if the connection with the server happens to be down
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the contents of the SQL results via the network connection
     */
    public ObjectInputStream retrieveAddresses(PreparedStatement statement) {
        try {
            try (
                    ObjectOutputStream objectOutputStream =
                            new ObjectOutputStream(socket.getOutputStream())
            ) {
                objectOutputStream.writeObject(statement);

                // This flush is important. The ObjectOutputStream writes some
                // data when it is first created to initialise the stream of
                // objects, and the ObjectInputStream expects to read that
                // data when it is constructed. In practice, when we have a read call
                // followed by a write call (or vice-versa) it is important to flush
                // the first stream before trying to read/write from/to the second.
                // Otherwise, the data in the ObjectOutputStream may not have actually
                // been sent to the server by this point. Because the server will not
                // write anything to the client until it has received the first object,
                // this will result in both server and client waiting for data from each
                // other
                objectOutputStream.flush();

                try (
                        ObjectInputStream objectInputStream =
                                new ObjectInputStream(socket.getInputStream())
                ) {
                    return objectInputStream;
                }
            }
        } catch (IOException e) {
            // Print the exception, but no need for a fatal error
            // if the connection with the server happens to be down
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Getter for the configured username
     * @return the configured username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for the configured password
     * @return the configured password
     */
    public String getPassword() {
        return password;
    }
}
