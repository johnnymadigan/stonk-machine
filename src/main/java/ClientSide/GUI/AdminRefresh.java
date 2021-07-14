package ClientSide.GUI;

import ClientSide.Asset;
import ClientSide.Unit;
import ClientSide.User;
import ServerSide.NetworkConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Refreshes the admin tables
 * @author Johnny Madigan
 */
public class AdminRefresh {

    public NetworkConnection data;

    /**
     * Constructor, uses the same network connection interface instance to keep the GUI in sync
     * @param data the network connection interface instance
     */
    public AdminRefresh(NetworkConnection data) {
        this.data = data;
    }

    /**
     * Refresh all Users table to get the latest updates from any client
     * Useful if you don't wish to add/delete a user, just see updates
     * @param t The table to update
     * @return The table data
     */
    public String[][] updateAssetTable(JTable t) {
        ArrayList<Asset> unitList = data.getAllAssets();
        String[][] tableData = new String[0][2];

        DefaultTableModel tableModel = (DefaultTableModel) t.getModel();
        tableModel.setRowCount(0);

        for (Asset a : unitList) {
            // Format the unit name for the table
            String assetID = String.valueOf(a.getId());
            String assetDesc = a.getDescription();

            // Fill the table columns
            String[] dataNew = new String[]{
                    assetID,
                    assetDesc};
            tableModel.addRow(dataNew);
            tableData = Arrays.copyOf(tableData, tableData.length + 1);
            tableData[tableData.length - 1] = dataNew;
        }
        return tableData;
    }

    /**
     * Refresh all Users table to get the latest updates from any client
     * Useful if you don't wish to add/delete a user, just see updates
     * @param t The table to update
     * @return The table data
     */
    public String[][] updateUnitTable(JTable t) {
        ArrayList<Unit> unitList = data.getAllUnits();
        String[][] tableData = new String[0][5];

        DefaultTableModel tableModel = (DefaultTableModel) t.getModel();
        tableModel.setRowCount(0);

        for (Unit u : unitList) {
            // Format the unit name for the table
            String name = u.getName();
            name = name.substring(0, 1).toUpperCase() + name.substring(1);

            // Format the unit balance for the table
            String credits = "$" + u.getCredits().toString();

            // Format the unit assetID for the table
            HashMap<Asset, Integer> assets = u.getAssets();
            String assetID = "";
            String assetDesc = "";
            String assetQTY = "";

            for ( Map.Entry<Asset, Integer> entry : assets.entrySet()) {
                if (!(entry.getKey() == null)) {
                    assetDesc = entry.getKey().getDescription();
                    assetID = String.valueOf(entry.getKey().getId());
                    assetQTY = String.valueOf(entry.getValue());
                }
            }

            // Fill the table columns
            String[] dataNew = new String[]{
                    name,
                    credits,
                    assetID,
                    assetDesc,
                    assetQTY};
            tableModel.addRow(dataNew);
            tableData = Arrays.copyOf(tableData, tableData.length + 1);
            tableData[tableData.length - 1] = dataNew;
        }
        return tableData;
    }

    /**
     * Refresh all Users table to get the latest updates from any client
     * Useful if you don't wish to add/delete a user, just see updates
     * @param t The table to update
     * @return The table data
     */
    public String[][] updateUserTable(JTable t) {
        ArrayList<User> userList = data.getAllUsers();
        String[][] tableData = new String[0][3];

        DefaultTableModel tableModel = (DefaultTableModel) t.getModel();
        tableModel.setRowCount(0);

        for (User u : userList) {
            // Format the username for the table
            String username = u.getUsername();
            username = username.substring(0, 1).toUpperCase() + username.substring(1);

            // Format the unit name for the table
            String unitName;
            if (u.getUnit() == null) {
                unitName = "none";
            } else {
                unitName = u.getUnit().getName();
                unitName = unitName.substring(0, 1).toUpperCase() + unitName.substring(1);
            }

            // Format the access for the table
            String access = String.valueOf(u.getAccess());
            access = access.substring(0, 1).toUpperCase() + access.substring(1);

            // Fill the table columns
            String[] dataNew = new String[]{
                    username,
                    unitName,
                    access};
            tableModel.addRow(dataNew);
            tableData = Arrays.copyOf(tableData, tableData.length + 1);
            tableData[tableData.length - 1] = dataNew;
        }
        return tableData;
    }

}
