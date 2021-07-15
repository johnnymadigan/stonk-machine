package ClientSide.GUI;

import ClientSide.Exceptions.DoesNotExist;
import ClientSide.Exceptions.IllegalString;
import ClientSide.User;
import ServerSide.NetworkConnection;

import javax.swing.*;
import java.awt.*;

/**
 * Begins creation of the entire GUI invoked by Main.
 * @author Johnny Madigan, Scott Peachey and Alistair Ridge
 */
public class Gui {

    // Logged in user for the session
    public User user = null;

    // INSTANCE VARIABLES
    NetworkConnection data;
    public final JFrame mainFrame = new JFrame("STONK MACHINE");
    public final int WIDTH = 800;
    public final int HEIGHT = 500;
    public final String DARKGREY = "#4D4D4D";
    public final String WHITE = "#FCFCFC";

    // Menu bar & widgets
    public JMenuBar menuBar = new JMenuBar();
    public JMenuItem logoutMenu = new JMenuItem("Logout");
    public JMenuItem exitMenu = new JMenuItem("Exit");
    public JMenuItem passwordSelfService = new JMenuItem("Password Self-Service");
    public JMenuItem masterUserKey = new JMenuItem("Master User Key");
    public JMenuItem masterAdminKey = new JMenuItem("Master Admin Key");

    // Create the generic pages
    public CreateShell shell;
    public LoginPage loginPanel;

    /**
     * Constructor sets up user interface, adds listeners and displays.
     * @param data The underlying data/model class the UI needs.
     */
    public Gui(NetworkConnection data) {
        this.data = data;

        // Configuring the main window (aspect ratio, exiting program on close, etc)
        loginPanel = new LoginPage(this.mainFrame, this.data);
        this.shell = loginPanel.getShell();

        mainFrame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // creates & shows login portal as the first screen
        mainFrame.setContentPane(loginPanel.getPanel());
        mainFrame.revalidate();
        menuBar(); // attach the menu-bar to the screen

        // Boilerplate
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        // Menu-bar listeners
        logoutListener();
        exitListener();
        passwordSelfServiceListener();
    }

    /**
     * Adds a menu bar to the app
     */
    public  void menuBar() {
        menuBar = new JMenuBar(); // reset

        // 'File' drop down menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(logoutMenu);
        fileMenu.add(exitMenu);
        menuBar.add(fileMenu);

        // 'Account' drop down menu
        JMenu accountMenu = new JMenu("Account");
        accountMenu.add(passwordSelfService);
        menuBar.add(accountMenu);

        // Set & refresh so the menubar appears
        mainFrame.setJMenuBar(menuBar);
        mainFrame.revalidate();
    }

    // MENU BAR LISTENERS ----------------------------------------------------------------------------------------------

    /**
     * Logs out the user when 'Logout' is clicked in the File menu
     */
    public void logoutListener() {
        logoutMenu.addActionListener(e -> {
            // reset session user data
            loginPanel.setUser(null);
            this.user = null;

            // creates & shows login portal
            mainFrame.setContentPane(loginPanel.getPanel());
            mainFrame.revalidate();
        });
    }

    /**
     * The action listener for the password self service
     */
    public void passwordSelfServiceListener() {
        passwordSelfService.addActionListener(e -> {
            if (!(loginPanel.getUser() == null)) {
                // Prepare the pop-up for user inputs
                JPanel panel = new JPanel(new GridLayout(0, 1));
                JTextField passwordInput = new JTextField();
                panel.add(passwordInput);

                int result = JOptionPane.showConfirmDialog(null, panel, "Your New Password",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicks 'OK'...
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        // Attempt to change the password in the database
                        data.changePassword(loginPanel.getUser().getUsername(), passwordInput.getText());
                    } catch (DoesNotExist | IllegalString ex) {

                        // If the user inputs an invalid new password
                        if (ex instanceof IllegalString) {
                            JOptionPane.showMessageDialog(mainFrame,
                                    "Password cannot have spaces.",
                                    "Invalid password",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                        //ex.printStackTrace();
                    }
                } else {
                    // If the user clicks 'CANCEL'...
                    System.out.println("Cancelled");
                }
            } else {
                // If the user has not logged in yet, we cannot change their password, warn the user
                JOptionPane.showMessageDialog(mainFrame,
                        "Please log in first.",
                        "Not logged in",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    /**
     * Closes the GUI and stops running the app when 'Exit' is clicked in the File menu
     */
    public void exitListener() {
        exitMenu.addActionListener(ev -> System.exit(0));
    }

}
