package ServerSide;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

/**
 * Establishes the connection between the client and server
 * @author Johnny Madigan
 */
public class EstablishConnection {

    public static String username;
    public static String password;

    /**
     * Used to establish a network connection with the user specified server.
     */
    public EstablishConnection() {
        try {
            Properties props = new Properties();
            InputStream inputStream = this.getClass().getResourceAsStream("ServerSettings.props");
            //InputStream inputStream = new FileInputStream("ServerSettings.props");

            props.load(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }

            // Get the server information from the Server Settings txt file
            String host = props.getProperty("host");
            String port = props.getProperty("port");
            username = props.getProperty("username");
            password = props.getProperty("password");

            Socket socket = new Socket(host, Integer.parseInt(port));

            if (socket.isConnected()) {
                System.out.println("Socket successfully connected to " + host + " on port " + port);
            }

            // Sample output stream - need to replace one day
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeInt(89832);
            objectOutputStream.close();
            socket.close();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
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
