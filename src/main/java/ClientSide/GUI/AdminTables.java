package ClientSide.GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Constructs the admin tables
 * @author Johnny Madigan
 */
public class AdminTables {

    AdminRefresh refresh;

    /**
     * Constructor, uses the same table refresher instance to keep the GUI in sync
     * @param refresh the instance of refresh methods to refresh each corresponding table
     */
    public AdminTables(AdminRefresh refresh) {
        this.refresh = refresh;
    }

    /**
     * Called ONCE in the admin home constructor
     * Creates the users units for admins to view and edit assets
     * @param t the blank table to fill
     * @return The scrollable filled table
     */
    public JScrollPane constructUserTable(JTable t) {
        // Table Data
        String[] columnNames = {"User", "Unit", "Admin access"};

        //update table data
        String [][] tableData = refresh.updateUserTable(t);

        // Make cells not editable
        DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };
        //c.allUsers = new JTable(tableData, columnNames);
        t.setModel(tableModel);
        JScrollPane scrollPane = new JScrollPane(t);
        scrollPane.setPreferredSize(new Dimension(595,203));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        t.getColumnModel().getColumn(0).setWidth(30);
        t.getColumnModel().getColumn(1).setWidth(30);
        t.getColumnModel().getColumn(2).setWidth(30);
        return scrollPane;
    }

    /**
     * Called ONCE in the admin home constructor
     * Creates the assets units for admins to view and edit assets
     * @param t the blank table to fill
     * @return The scrollable filled table
     */
    public JScrollPane constructUnitTable(JTable t) {
        // Table Data
        String[] columnNames = {"Unit", "Credits", "Asset ID", "Asset Desc", "Asset Qty"};

        //update table data
        String [][] tableData = refresh.updateUnitTable(t);

        // Make cells not editable
        DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };
        //c.allUsers = new JTable(tableData, columnNames);
        t.setModel(tableModel);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        t.getColumnModel().getColumn(0).setWidth(18);
        t.getColumnModel().getColumn(1).setWidth(18);
        t.getColumnModel().getColumn(2).setWidth(18);
        t.getColumnModel().getColumn(3).setWidth(18);
        t.getColumnModel().getColumn(4).setWidth(18);
        JScrollPane scrollPane = new JScrollPane(t);
        scrollPane.setPreferredSize(new Dimension(595,160));
        return scrollPane;
    }

    /**
     * Called ONCE in the admin home constructor
     * Creates the assets table for admins to view and edit assets
     * @param t the blank table to fill
     * @return The scrollable filled
     */
    public JScrollPane constructAssetTable(JTable t) {
        // Table Data
        String[] columnNames = {"Asset ID", "Description"};

        //update table data
        String [][] tableData = refresh.updateAssetTable(t);

        // Make cells not editable
        DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };
        //c.allUsers = new JTable(tableData, columnNames);
        t.setModel(tableModel);
        JScrollPane scrollPane = new JScrollPane(t);
        scrollPane.setPreferredSize(new Dimension(595,203));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        t.getColumnModel().getColumn(0).setWidth(45);
        t.getColumnModel().getColumn(1).setWidth(45);
        return scrollPane;
    }

}
