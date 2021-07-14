package ClientSide.GUI;

import ClientSide.Order;
import ClientSide.Unit;
import ClientSide.User;
import ServerSide.NetworkConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The order history page contains a tabbed pane with two tables, one showing the
 * past BUY orders of the user's unit, and another showing their past SELL orders.
 * There is also a 'Refresh' buttons at the bottom of the page. The current table
 * will be refreshed when the 'Refresh' button is clicked. Tables are also refreshed
 * automatically when switching panes.
 * @author Scott Peachey
 */
public class OrderHistory implements ActionListener {
    // Instance variables
    private JFrame frame;
    private NetworkConnection server;
    private User user;
    private Unit unit;
    private CreateShell shell;
    private ArrayList<Order> orders; // used to find the past orders made by the unit
    public JPanel content;

    // Shell components for the order history page
    public JPanel buttonsPanel = new JPanel();
    public JPanel buyTablesPanel = new JPanel();
    public JPanel sellTablesPanel = new JPanel();
    public JButton refresh = new JButton("Refresh");
    public JTable buyTable = new JTable();
    public JTable sellTable = new JTable();
    public JScrollPane scrollPane = new JScrollPane();
    public JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * Constructor for the order history page
     * @param unit the unit assigned to the current user
     * @param user the currently logged-in user for the session
     * @param frame the main Java Swing frame
     * @param server the network data interface methods to carry over
     */
    public OrderHistory(Unit unit, User user, CreateShell shell, JFrame frame, NetworkConnection server) {
        // Assign the following to keep things in sync
        this.user = user;
        this.unit = unit;
        this.shell = shell;
        this.server = server;
        this.frame = frame;

        // Action listener for the refresh button
        refresh.addActionListener(this);

        createContent(); // fill the order history content panel
    }

    /**
     * Fill the order history content panel...
     * This page include a tabbed pane with two separate tables, as well as
     * a refresh buttons below it. The tables are filled using
     * the updateOrderHistoryTable method.
     */
    public void createContent() {
        // New order history panel to create
        content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setPreferredSize(new Dimension(600,300));

        // Table Data
        String[] columnNames = { "Asset ID", "Qty", "Price", "Date", "Order ID"};

        // Update table data
        String[][] buyTableData = updateOrderHistoryTable(true);

        // Make buy orders table cells not editable
        DefaultTableModel buyTableModel = new DefaultTableModel(buyTableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };

        // Make sell orders table cells not editable
        String[][] sellTableData = updateOrderHistoryTable(false);
        DefaultTableModel sellTableModel = new DefaultTableModel(sellTableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };

        // Set buy orders table model and dimensions
        buyTable.setModel(buyTableModel);
        JScrollPane buyScrollPane = new JScrollPane(buyTable);
        buyScrollPane.setPreferredSize(new Dimension(600,250));

        // Set column widths
        buyTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        buyTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        buyTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        buyTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        buyTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Set sell orders table model and dimensions
        sellTable.setModel(sellTableModel);
        JScrollPane sellScrollPane = new JScrollPane(sellTable);
        sellScrollPane.setPreferredSize(new Dimension(600,250));

        // Set column widths
        sellTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        sellTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        sellTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        sellTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        sellTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Add scroll panes to their own panels
        buyTablesPanel.add(buyScrollPane);
        sellTablesPanel.add(sellScrollPane);

        // Assign icons to tab buttons and refresh button
        ImageIcon plusIcon = new ImageIcon("./img/gui-images/plus_icon.PNG");
        tabbedPane.addTab("Buy", plusIcon, buyTablesPanel);
        ImageIcon minusIcon = new ImageIcon("./img/gui-images/minus_icon.PNG");
        tabbedPane.addTab("Sell", minusIcon, sellTablesPanel);
        refresh.setIcon(new ImageIcon("./img/gui-images/refresh_icon.png"));

        // Create panel refresh and cancel order buttons
        JPanel refreshPanel = new JPanel();
        refreshPanel.add(refresh);

        // Add inner panels to the current orders page
        content.add(refreshPanel, BorderLayout.SOUTH);
        content.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Getter for the order history panel...
     * to easily switch contents when going from the user home to order history.
     * @return the order history panel
     */
    public JPanel getPanel() { return content; }

    /**
     * Refresh current order history table to get the unit's latest fulfilled orders.
     * @param buyTable indicates if the user is looking at the buy or sell order history
     * @return returns a 2D string array that will fill the table
     */
    public String[][] updateOrderHistoryTable(boolean buyTable) {
        // Get past orders made by the unit
        orders = this.server.getUnitOrderHistory(unit);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // BUY TABLE
        String[][] buyTableData = new String[0][6];

        // Get current buy table and start from the first row
        DefaultTableModel buyTableModelModel = (DefaultTableModel) this.buyTable.getModel();
        buyTableModelModel.setRowCount(0);

        // For all orders made by the unit
        for (Order order : orders) {
            // If it is a buy order
            if (order.isBuy) {
                // Fill the table columns
                String[] dataNew = new String[]{
                        order.asset.getIdString(),
                        Integer.toString(order.qty),
                        Integer.toString(order.price),
                        order.dateResolved.format(formatter),
                        Integer.toString(order.id)};
                buyTableModelModel.addRow(dataNew);
                buyTableData = Arrays.copyOf(buyTableData, buyTableData.length + 1);
                buyTableData[buyTableData.length - 1] = dataNew;
            }
        }

        // SELL TABLE
        String[][] sellTableData = new String[0][6];

        // Get current sell table and start from the first row
        DefaultTableModel sellTableModel = (DefaultTableModel) this.sellTable.getModel();
        sellTableModel.setRowCount(0);

        // For all orders made by the unit
        for (Order order : orders) {
            // If it is not a buy order
            if (!order.isBuy) {
                // Fill the table columns
                String[] dataNew = new String[]{
                        order.asset.getIdString(),
                        Integer.toString(order.qty),
                        Integer.toString(order.price),
                        order.dateResolved.format(formatter),
                        Integer.toString(order.id)};
                sellTableModel.addRow(dataNew);
                sellTableData = Arrays.copyOf(sellTableData, sellTableData.length + 1);
                sellTableData[sellTableData.length - 1] = dataNew;
            }
        }

        // Return table depending on current pane
        if (buyTable) return buyTableData;
        else return sellTableData;

    }

    /**
     * Action listeners for all current order page buttons.
     * @param e the event (which button was pressed)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // refresh current tab when the refresh button is clicked
        if (e.getSource() == refresh && tabbedPane.getSelectedIndex() == 0) {
            updateOrderHistoryTable(true);
        }
        else if (e.getSource() == refresh && tabbedPane.getSelectedIndex() == 1) {
            updateOrderHistoryTable(false);
        }
    }
}
