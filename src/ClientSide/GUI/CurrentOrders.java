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
 * The current orders page contains a tabbed pane with two tables, one showing the
 * current BUY orders of the user's unit, and another showing their current SELL orders.
 * There are also 'Cancel Order' and 'Refresh' buttons at the bottom of the page. A user
 * can select an order from either table and cancel it buy clicking the 'Cancel Order'
 * button. The current table will be refreshed when the 'Refresh' button is clicked. Tables
 * are also refreshed automatically when switching panes.
 * @author Scott Peachey
 */
public class CurrentOrders implements ActionListener {
    // Instance variables
    private JFrame frame;
    private NetworkConnection server;
    private User user;
    private Unit unit;
    private CreateShell shell;
    private ArrayList<Order> orders; // used to find the current orders made by the unit
    private Order order;
    public JPanel content;

    // Shell components for the current orders page
    public JPanel buttonsPanel = new JPanel();
    public JPanel buyTablesPanel = new JPanel();
    public JPanel sellTablesPanel = new JPanel();
    public JButton refresh = new JButton("Refresh");
    public JButton cancelOrder = new JButton("Cancel Order");
    public JTable buyTable = new JTable();
    public JTable sellTable = new JTable();
    public JScrollPane scrollPane = new JScrollPane();
    public JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * Constructor for the current orders page
     * @param unit The unit assigned to the current user
     * @param user The currently logged-in user for the session
     * @param shell The GUI shell that the contents of this page will be displayed in
     * @param frame The main Java Swing frame
     * @param server The network data interface methods to carry over
     */
    public CurrentOrders(Unit unit, User user, CreateShell shell, JFrame frame, NetworkConnection server) {
        // Assign the following to keep things in sync
        this.user = user;
        this.unit = unit;
        this.shell = shell;
        this.server = server;
        this.frame = frame;

        // Action listeners for refresh and cancel order buttons
        refresh.addActionListener(this);
        cancelOrder.addActionListener(this);

        createContent(); // fill the current orders content panel
    }

    /**
     * Fill the current orders content panel...
     * This page include a tabbed pane with two separate tables, as well as
     * refresh and cancel order buttons below it. The tables are filled using
     * the updateCurrentOrdersTable method.
     */
    public void createContent() {
        // New current orders panel to create
        content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setPreferredSize(new Dimension(600,300));

        // Table Data
        String[] columnNames = { "Asset ID", "Qty", "Price", "Date", "Order ID"};

        // Update table data
        String[][] buyTableData = updateCurrentOrdersTable(true);

        // Make buy orders table cells not editable
        DefaultTableModel buyTableModel = new DefaultTableModel(buyTableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };

        // Make sell orders table cells not editable
        String[][] sellTableData = updateCurrentOrdersTable(false);
        DefaultTableModel sellTableModel = new DefaultTableModel(sellTableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };

        // Set buy orders table model and dimensions
        buyTable.setModel(buyTableModel);
        JScrollPane buyScrollPane = new JScrollPane(buyTable);
        buyScrollPane.setPreferredSize(new Dimension(600,250));

        // Allow users to select individual rows
        buyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

        // Allow users to select individual rows
        sellTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
        ImageIcon plusIcon = new ImageIcon("./Images/GUI images/plus_icon.PNG");
        tabbedPane.addTab("Buy", plusIcon, buyTablesPanel);
        ImageIcon minusIcon = new ImageIcon("./Images/GUI images/minus_icon.PNG");
        tabbedPane.addTab("Sell", minusIcon, sellTablesPanel);
        refresh.setIcon(new ImageIcon("./Images/GUI images/refresh_icon.png"));

        // Create panel refresh and cancel order buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refresh);
        buttonPanel.add(cancelOrder);

        // Add inner panels to the current orders page
        content.add(buttonPanel, BorderLayout.SOUTH);
        content.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Getter for the current orders panel...
     * to easily switch contents when going from the user home to current orders.
     * @return the current orders panel
     */
    public JPanel getPanel() { return content; }

    /**
     * Refresh current orders table to get the latest orders placed by the unit
     * Useful if you don't wish to cancel an order, just see updates
     * @param buyTable indicates if the user is looking at the buy or sell orders
     * @return returns a 2D string array that will fill the table
     */
    public String[][] updateCurrentOrdersTable(boolean buyTable) {
        // Get current orders made by the unit
        orders = this.server.getUnitOrders(unit);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // BUY TABLE
        String[][] buyTableData = new String[0][5];

        // Get current buy table and start from the first row
        DefaultTableModel buyTableModel = (DefaultTableModel) this.buyTable.getModel();
        buyTableModel.setRowCount(0);

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
                buyTableModel.addRow(dataNew);
                buyTableData = Arrays.copyOf(buyTableData, buyTableData.length + 1);
                buyTableData[buyTableData.length - 1] = dataNew;
            }
        }

        // SELL TABLE
        String[][] sellTableData = new String[0][5];

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
     * Cancel the order which is currently selected in the table.
     * This removes the order from the database.
     * @param table the current table the user is viewing
     */
    public void cancelOrder(JTable table) {
        try {
            // Get the currently selected order ID
            int row = table.getSelectedRow();
            int col = 4; // always orderID column
            String orderID = (String) table.getModel().getValueAt(row, col);

            // Check if the order is in the database
            for (Order order : orders) {
                if (Integer.toString(order.id).equals(orderID)) {
                    this.order = order;
                }
            }

            // Prompt the user to confirm cancellation
            int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to cancel order " + order.id + "?",
                    "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            // If the user clicks okay, cancel the order
            if (result == JOptionPane.OK_OPTION) {
                server.cancelOrder(order);
                // Update the current table
                if (tabbedPane.getSelectedIndex() == 0) {
                    updateCurrentOrdersTable(true);
                }
                else if (tabbedPane.getSelectedIndex() == 1) {
                    updateCurrentOrdersTable(false);
                }
            } else {
                System.out.println("Cancelled");
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Please select an order to cancel",
                    "No order selected",
                    JOptionPane.WARNING_MESSAGE);
            //ex.printStackTrace();
        }
    }

    /**
     * Action listeners for all current order page buttons.
     * @param e the event (which button was pressed)
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        // refresh current tab when the refresh button is clicked
        if (e.getSource() == refresh && tabbedPane.getSelectedIndex() == 0) {
            updateCurrentOrdersTable(true);
        }
        else if (e.getSource() == refresh && tabbedPane.getSelectedIndex() == 1) {
            updateCurrentOrdersTable(false);
        }
        // Call the cancelOrder function depending on the current tab
        else if (e.getSource() == cancelOrder && tabbedPane.getSelectedIndex() == 0) {
            cancelOrder(buyTable);
        }
        else if (e.getSource() == cancelOrder && tabbedPane.getSelectedIndex() == 1) {
            cancelOrder(sellTable);
        }
    }

}
