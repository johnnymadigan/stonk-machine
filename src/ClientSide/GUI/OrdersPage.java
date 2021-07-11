package ClientSide.GUI;

import ClientSide.Asset;
import ClientSide.Exceptions.DoesNotExist;
import ClientSide.Exceptions.OrderException;
import ClientSide.Order;
import ClientSide.User;
import ServerSide.NetworkConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The orders page
 * @author Alistair Ridge
 */
public class OrdersPage implements ActionListener {
    public NetworkConnection server;

    public final String DARKGREY = "#4D4D4D";
    public final int FRAME_WIDTH = 800;
    public final int FRAME_HEIGHT = 500;

    private JPanel content;

    private JTextField qtyInput, priceInput;
    private JButton placeOrder;

    public boolean isBuy;
    public Asset asset;
    public User user;
    public CreateShell shell;

    /**
     * Constrcustor for a generic order placing screen
     * @param asset The asset that is being ordered
     * @param isBuy The type of order being placed, true for buy, false for sell
     * @param user The user that is placing the order
     * @param shell The shell that the content is being displayed in
     * @param frame The overarching frame of the application
     * @param server the connection to the database
     */
    public OrdersPage(String asset, boolean isBuy, User user, CreateShell shell, JFrame frame, NetworkConnection server) {
        this.server = server;
        int assetID = Integer.parseInt(asset);
        this.asset = server.getAsset(assetID);
        this.isBuy = isBuy;
        this.user = user;
        this.shell = shell;

        ConstructPage(frame);
    }

    /**
     * Method used to create the content of the page.
     * @param frame The overarching frame of the application.
     */
    public void ConstructPage(JFrame frame) {
        GridBagLayout mainContent = new GridBagLayout();
        GridBagConstraints layoutConstraints = new GridBagConstraints();
        this.content = new JPanel();
        this.content.setBackground(Color.lightGray);
        this.content.setLayout(mainContent);

        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;

        layoutConstraints.insets = new Insets(5, 5, 5, 5);

        layoutConstraints.weightx = 0.25;
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 0;
        layoutConstraints.gridwidth = 1;
        JLabel qtyLabel = new JLabel("Quantity:");
        this.content.add(qtyLabel, layoutConstraints);

        layoutConstraints.weightx = 0.75;
        layoutConstraints.gridx = 1;
        layoutConstraints.gridy = 0;
        layoutConstraints.gridwidth = 2;
        qtyInput = new JTextField();
        this.content.add(qtyInput, layoutConstraints);

        layoutConstraints.weightx = 0.25;
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 1;
        layoutConstraints.gridwidth = 1;
        JLabel priceLabel = new JLabel("Price:");
        this.content.add(priceLabel, layoutConstraints);

        layoutConstraints.weightx = 0.75;
        layoutConstraints.gridx = 1;
        layoutConstraints.gridy = 1;
        layoutConstraints.gridwidth = 2;
        priceInput = new JTextField();
        this.content.add(priceInput, layoutConstraints);

        layoutConstraints.weightx = 0.0;
        layoutConstraints.gridx = 1;
        layoutConstraints.gridy = 2;
        layoutConstraints.gridwidth = 1;
        placeOrder = new JButton("Place Order");
        placeOrder.addActionListener(this);
        this.content.add(placeOrder, layoutConstraints);

        JLabel title;

        if (this.isBuy) {
            title = new JLabel("Placing a Buy Order for " + this.asset.getDescription());
        } else {
            title = new JLabel("Placing a Sell Order for " + this.asset.getDescription());
        }

        title.setHorizontalAlignment(SwingConstants.CENTER);

        frame.add(this.content, BorderLayout.CENTER);
        frame.add(title, BorderLayout.NORTH);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setBackground(Color.darkGray);
        frame.setVisible(true);
    }

    /**
     * Getter for the panel that houses all the content displayed by this page.
     * @return The panel housing the pages content
     */
    public JPanel getPanel() {
        return this.content;
    }

    /**
     * Handle user interactions such as:
     * quantity input
     * price input
     * place order button click
     * @param e The action that the user performed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int quantity = Integer.parseInt(qtyInput.getText());
        int price = Integer.parseInt(priceInput.getText());

        if (e.getSource() == placeOrder) {
            System.out.println("Buy Order: " + this.isBuy);
            System.out.println("QTY: " + quantity);
            System.out.println("Price: " + price);
            System.out.println("Asset ID: " + this.asset.getId());
            Order order = new Order(this.user.getUnit(), this.asset, quantity, price, this.isBuy);
            try {
                this.server.addOrder(order);
                shell.GoToOrderPlaced(order);
            } catch (OrderException orderException) {
                shell.GoToOrderFailed(order, orderException);
                orderException.printStackTrace();
            } catch (DoesNotExist doesNotExist) {
                doesNotExist.printStackTrace();
            }
        }
    }
}
