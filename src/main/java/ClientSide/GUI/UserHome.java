package ClientSide.GUI;

import ClientSide.Asset;
import ClientSide.Order;
import ClientSide.Unit;
import ClientSide.User;
import ServerSide.NetworkConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The user home page contains a table showing the assets currently held by the user's
 * unit, their quantities and current average buy order price. The page also shows the unit's
 * current credits. At the bottom of the page, the user can switch to the order history or
 * current orders pages, as well as refresh the table and credits.
 * @author Scott Peachey
 */
public class UserHome implements ActionListener{
    // Instance variables
    private User user;
    private Unit unit;
    private CreateShell shell;
    private NetworkConnection data;
    private JFrame mainFrame;

    // Shell components for the order history page
    public JPanel userHome = new JPanel();
    public JButton currentOrdersButton = new JButton("Current Orders");
    public JButton orderHistoryButton = new JButton("Order History");
    public JButton refresh = new JButton("Refresh");
    public JLabel welcomeLabel = new JLabel("Welcome back (username)", SwingConstants.CENTER);
    public JLabel orgUnitLabel = new JLabel("ORG UNIT HERE", SwingConstants.CENTER);
    public JTable userHoldings = new JTable();
    public JLabel creditsLabel = new JLabel("Available Credits:", SwingConstants.CENTER);

    /**
     * Constructor for the user home page
     * @param user the currently logged-in user for the session
     * @param frame the main Java Swing frame
     * @param server the network data interface methods to carry over
     */
    public UserHome(User user, CreateShell shell, JFrame frame, NetworkConnection server) {
        // Assign the following to keep things in sync
        this.user = user;
        this.shell = shell;
        this.data = server;
        this.mainFrame = frame;
        this.unit = user.getUnit();

        // Action listeners for the refresh, order history, and current orders buttons
        orderHistoryButton.addActionListener(this);
        currentOrdersButton.addActionListener(this);
        refresh.addActionListener(this);

        createContent(); // fill the order history content panel
    }

    /**
     * Fill the user home content panel...
     * This page includes a table, as well as multiple buttons.
     */
    public void createContent() {
        // New user home panel to create
        userHome = new JPanel();
        userHome.setPreferredSize(new Dimension(600,300));

        // Assign icon to refresh button
        refresh.setIcon(new ImageIcon("./img/gui-images/refresh_icon.png"));

        // Set the welcome label to show the current users name
        String usernameUC = user.getUsername().substring(0, 1).toUpperCase() + user.getUsername().substring(1); // uppercase
        welcomeLabel.setText(String.format("Welcome back %s", usernameUC));

        // Set the credits label to display the unit's credits
        creditsLabel.setText("Credits: $" + unit.getCredits().toString());
        creditsLabel.setBorder(new EmptyBorder(0, 50, 0,0));

        // Table Data
        String[] columnNames = { "Asset ID", "Description", "Qty", "$ Average"};

        // Update table data
        String[][] tableData = updateUserHomeTable();

        // Make cells not editable
        DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        // Set table model and dimensions
        userHoldings = new JTable(tableData, columnNames);
        userHoldings.setModel(tableModel);
        JScrollPane scrollPane = new JScrollPane(userHoldings);
        scrollPane.setPreferredSize(new Dimension(600,210));

        // Set column widths
        userHoldings.getColumnModel().getColumn(0).setWidth(25);
        userHoldings.getColumnModel().getColumn(1).setPreferredWidth(400);
        userHoldings.getColumnModel().getColumn(2).setWidth(20);
        userHoldings.getColumnModel().getColumn(2).setWidth(30);

        // Add scroll pane to table panel

        JPanel tablePanel = new JPanel();
        tablePanel.add(scrollPane);

        // Add buttons to the button panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(orderHistoryButton);
        buttonsPanel.add(refresh);
        buttonsPanel.add(currentOrdersButton);

        // Add labels to label panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(welcomeLabel, BorderLayout.CENTER);
        topPanel.add(creditsLabel, BorderLayout.EAST);

        // Add panels to the page
        userHome.add(topPanel);
        userHome.add(tablePanel);
        userHome.add(buttonsPanel);

        // Begin listener to know when the user click a row of the table
        assetTableListener();
    }

    /**
     * Getter for the user home panel...
     * to easily switch contents when going from login screen to user home.
     * @return the order history panel
     */
    public JPanel getPanel() {
        return userHome;
    }

    /**
     * Refresh assets table to get the unit's latest assets.
     * @return returns a 2D string array that will fill the table
     */
    public String[][] updateUserHomeTable() {
        // Get the units currently held assets
        HashMap<Asset, Integer> unitAssets = data.getUnitsAssets(unit);

        // Initialise table data
        String[][] tableData = new String[0][4];

        // Get the current assets table and start from the first row
        DefaultTableModel tableModel = (DefaultTableModel) userHoldings.getModel();
        tableModel.setRowCount(0);

        // For each asset held by the unit
        for (Asset asset : unitAssets.keySet()) {
            // Get the average buy order price
            String avg = getAverageAssetPrice(asset).toString();
            if (avg.equalsIgnoreCase("0")) { // if there is no avg display n/a
                avg = "n/a";
            }

            // Fill the table columns
            String[] dataNew = new String[]{
                    asset.getIdString(), // assetID
                    asset.getDescription(), // asset description
                    unitAssets.get(asset).toString(), // quantity
                    avg }; // average order price
            tableModel.addRow(dataNew);
            tableData = Arrays.copyOf(tableData, tableData.length + 1);
            tableData[tableData.length - 1] = dataNew;
        }

        return tableData;
    }

    /**
     * Gets the current amount of credits and updates the label
     */
    public void updateCredits() {
        creditsLabel.setText("Credits: " + unit.getCredits().toString()); // this works
    }

    /**
     * Gets the average price of current buy orders for a given asset
     * @param asset an asset held by the current user's unit
     * @return the average price
     */
    public Integer getAverageAssetPrice(Asset asset) {
        // Get all current orders in the database that involve the given asset
        ArrayList<Order> orders = data.getAssetOrders(asset);

        // Initialise variables
        int count = 0;
        int sum = 0;
        int average;

        // For each order
        for (Order order : orders) {
            // If it is a buy order, add the price to the sum and increment counter
            if (order.isBuy) {
                sum += order.price;
                count++;
            }
        }

        // If counter is zero, return zero
        if (count == 0) {
            return 0;
        }

        // Otherwise, calculate average
        average = sum / count;

        return average;
    }

    /**
     * Mouse listener that detects when the user clicks on an asset in the table.
     * When this occurs, the asset page is created for the asset.
     */
    public void assetTableListener() {
        userHoldings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = userHoldings.rowAtPoint(e.getPoint());
                String asset = userHoldings.getValueAt(row, 0).toString();
                shell.GoToAssets(asset);
                userHome.remove(welcomeLabel);
            }
        });
    }

    /**
     * Action listeners for all current order page buttons.
     * @param e the event (which button was pressed)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // If order history button is pressed, go to order history page
        if (e.getSource() == orderHistoryButton) {
            shell.GoToOrderHistory(user.getUnit());
        }
        // If current orders button is pressed, go to current orders page
        else if (e.getSource() == currentOrdersButton) {
            shell.GoToCurrentOrders(user.getUnit());
        }
        // If refresh button is pressed, refresh table and credits
        else if (e.getSource() == refresh) {
            updateUserHomeTable();
            updateCredits();
        }
    }
}
