package ClientSide;

import ClientSide.GUI.Gui;
import ServerSide.NetworkConnection;
import ServerSide.ReconcileTrades;

/**
 * STONK MACHINE - An intuitive and responsive trading app.
 * Our app begins here, launching the GUI, connecting to the
 * database and checking reconciling orders live.
 *
 * Please ensure you have configured the ServerSettings.props file
 * to use your root user credentials and if you wish to test our app
 * with a fake database, please ensure you have changed the schema
 * in ServerSettings.props to use "MockStonkMachine".
 * @author Johnny Madigan, Alistair Ridge, Scott Peachey
 */
public class Main {

    private static NetworkConnection data = new NetworkConnection();
    private static void createAndShowGUI(NetworkConnection data) {
        new Gui(data);
    }

    /**
     * Invokes the Client-Side GUI (thread-safe) + reconcile trades thread
     * @param args args
     */
    public static void main(String[] args) {

         // THREAD: Start reconciling trades for session
        ReconcileTrades runnable = new ReconcileTrades(data);
        Thread thread = new Thread(runnable);
        thread.start();

        // THREAD: BOILERPLATE needed when running the GUI to make sure it's thread safe
        javax.swing.SwingUtilities.invokeLater(() -> {
            createAndShowGUI(data);
        });
    }
}
