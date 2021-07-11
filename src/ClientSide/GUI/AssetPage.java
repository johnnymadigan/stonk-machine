package ClientSide.GUI;

import ClientSide.User;
import ServerSide.NetworkConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The asset page has been designed to host the graphical representation of the
 * selected asset's price history. The idea was that graph would show the average
 * price for intervals that could be changed with the buttons Days, Weeks, Months and
 * Years. Currently the page functions as a fork where the user can decide to either
 * buy or sell the asset, which was selected in the user home. This page can also be
 * accessed by searching an asset ID with the Search button found in the top right corner.
 * @author Scott Peachey
 */
public class AssetPage implements ActionListener {

    // Instance variables
    private JFrame frame;
    private NetworkConnection server;
    private String assetID;
    private User user;
    private CreateShell shell;
    public JPanel content;

    // Shell components for the asset page
    public JButton daysButton = new JButton("Days");
    public JButton weeksButton = new JButton("Weeks");
    public JButton monthsButton = new JButton("Months");
    public JButton yearsButton = new JButton("Years");
    public JButton buyButton = new JButton("<- Buy Asset");
    public JButton sellButton = new JButton("Sell Asset ->");

    /**
     * Constructor for the asset page
     * @param asset The asset which was selected in the user's home screen
     * @param user The currently logged-in user for the session
     * @param shell The GUI shell that the contents of this page will be displayed in
     * @param frame The main Java Swing frame
     * @param server The network data interface methods to carry over
     */
    public AssetPage(String asset, User user, CreateShell shell, JFrame frame, NetworkConnection server) {
        // Assign the following to keep things in sync
        this.assetID = asset;
        this.user = user;
        this.shell = shell;
        this.frame = frame;
        this.server = server;

        // Action listeners for buy and sell buttons
        buyButton.addActionListener(this);
        sellButton.addActionListener(this);

        createContent(); // fill the admin home content panel
    }

    /**
     * Fill the asset page content panel...
     * This page is quite simple currently, using three panels to seperate
     * it into interval buttons, graph, and buy/sell buttons.
     */
    public void createContent() {
        // New asset page panel to create
        content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setPreferredSize(new Dimension(600,325));
        content.setBackground(Color.DARK_GRAY);

        // Insert the interval buttons at the top of the asset page panel
        JPanel intervalButtons = new JPanel();
        intervalButtons.add(daysButton);
        intervalButtons.add(weeksButton);
        intervalButtons.add(monthsButton);
        intervalButtons.add(yearsButton);

        // Check if current user is in a unit
        if (user.getUnit() == null) {
            // If no unit, disable buy and sell buttons
            buyButton.setEnabled(false);
            sellButton.setEnabled(false);
        }
        else {
            buyButton.setEnabled(true);
            sellButton.setEnabled(true);
        }

        // Insert graph panel into asset page panel
        JLabel graph = new JLabel();
        graph.setIcon(new ImageIcon("./Images/GUI images/graph.png"));

        // Insert buy/sell buttons at the bottom of the asset page panel
        JPanel orderButtons = new JPanel();
        orderButtons.add(buyButton);
        orderButtons.add(sellButton);
        content.add(intervalButtons, BorderLayout.NORTH);
        content.add(graph, BorderLayout.CENTER);
        content.add(orderButtons, BorderLayout.SOUTH);
    }

    /**
     * Getter for the asset page panel...
     * to easily switch contents when going to the asset page.
     * @return the asset page panel
     */
    public JPanel getPanel() {
        return this.content;
    }

    /**
     * Action listeners for all asset page buttons.
     * @param e the event (which button was pressed)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        // Go to the order screen with isBuy set depending on buy/sell buttons
        if (e.getSource() == buyButton) {
            shell.GoToOrder(this.assetID, true);
            System.out.println("Buy button pressed");
        } else if (e.getSource() == sellButton) {
            shell.GoToOrder(this.assetID, false);
            System.out.println("Sell button pressed");
        }
    }
}
