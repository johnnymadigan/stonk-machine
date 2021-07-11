package ClientSide.GUI;

import ClientSide.Order;
import ServerSide.NetworkConnection;

import javax.swing.*;
import java.awt.*;

/**
 * Insightful warning to the user that their order has failed
 * @author Alistair Ridge
 */
public class OrderFailedPage {
    private final NetworkConnection server;
    private final CreateShell shell;
    private final Order order;
    private JPanel content;

    private Exception message;

    /**
     * Constructor used to create an Order failed message page
     * @param order The order that was unsuccessfully placed
     * @param shell The shell that the content will be displayed in
     * @param frame The overarching frame of the application
     * @param server The connection to the database
     * @param e The exception that was thrown when the order was unsuccessfully placed
     */
    public OrderFailedPage(Order order, CreateShell shell, JFrame frame, NetworkConnection server, Exception e) {
        this.server = server;
        this.shell = shell;
        this.order = order;

        this.message = e;

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
        JLabel messageLabel = new JLabel("Order was not placed!", SwingConstants.CENTER);
        this.content.add(messageLabel, layoutConstraints);

        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 1;
        JLabel totalPrice = new JLabel(message.getMessage(), SwingConstants.CENTER);
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
