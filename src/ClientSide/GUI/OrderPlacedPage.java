package ClientSide.GUI;

import ClientSide.Order;
import ServerSide.NetworkConnection;

import javax.swing.*;
import java.awt.*;

/**
 * The orders placed message page
 * @author Alistair Ridge
 */
public class OrderPlacedPage {
    private final NetworkConnection server;
    private final CreateShell shell;
    private final Order order;
    private JPanel content;

    /**
     * Constructor for the order placed message page. This page is only display if an order is successfully placed.
     * @param order The order that was placed
     * @param shell The shell that the content will be displayed in
     * @param frame The overarching frame of the application
     * @param server The connection to the database
     */
    public OrderPlacedPage(Order order, CreateShell shell, JFrame frame, NetworkConnection server) {
        this.order = order;
        this.shell = shell;
        this.server = server;

        createContent(frame);
    }

    /**
     * Method used to create the content of the page
     * @param frame The overarching frame of the application
     */
    private void createContent(JFrame frame) {
        GridBagLayout mainContent = new GridBagLayout();
        GridBagConstraints layoutConstraints = new GridBagConstraints();
        this.content = new JPanel();
        this.content.setBackground(Color.lightGray);
        this.content.setLayout(mainContent);

        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;

        layoutConstraints.insets = new Insets(5, 5, 5, 5);

        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 0;
        JLabel messageLabel = new JLabel("Order successfully placed for " + this.order.qty + " of " + order.asset.getDescription(), SwingConstants.CENTER);
        this.content.add(messageLabel, layoutConstraints);

        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 1;
        JLabel totalPrice = new JLabel("Order total: " + this.order.price*this.order.qty + " credits", SwingConstants.CENTER);
        this.content.add(totalPrice, layoutConstraints);
    }

    /**
     * Method used to get the panel that houses the content displayed on this page
     * @return The panel containing the content of this page
     */
    public JPanel getPanel() {
        return this.content;
    }
}
