package ServerSide;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Server {

    public static void createServerGUI(String port, String username) {

        if (username.isEmpty()) {
            username = "None";
        }

        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(new JLabel("Port: " + port));
        panel.add(new JLabel("Default user: " + username));
        frame.add(panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Server");
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {

        Properties props = new Properties();
        InputStream inputSettings = new FileInputStream("./Setup/ServerSettings.props");

        props.load(inputSettings);
        inputSettings.close();

        // Get the port & default username from the Server Settings txt file
        String port = props.getProperty("port");
        String username = props.getProperty("username");

        // THREAD: BOILERPLATE needed when running the GUI to make sure it's thread safe
        javax.swing.SwingUtilities.invokeLater(() -> createServerGUI(port, username));

        // Contained in a try-block
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port), 5)) {
            System.out.println("Server now running...");

            for (;;) {
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                System.out.println("Got int from client: " + objectInputStream.readInt());
                objectInputStream.close();
                socket.close();
            }
        } finally {
            System.out.println("Server closed");
        }
    }



}