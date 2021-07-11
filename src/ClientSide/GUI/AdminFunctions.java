package ClientSide.GUI;

import ClientSide.Exceptions.AlreadyExists;
import ClientSide.Exceptions.DoesNotExist;
import ClientSide.Exceptions.IllegalString;
import ClientSide.User;
import ServerSide.NetworkConnection;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.NumberFormat;

/**
 * The Admin Home functions grouped in this class for simplicity and to keep other admin
 * classes single-purpose only. These functions are for the control panels that admins
 * will use to control database objects. Each of these functions will check a user's
 * admin access in-case their privileges are revoked mid-session by another admin user...
 * as we'd want them to stop using these abilities ASAP!
 * @author Johnny Madigan
 */
public class AdminFunctions implements ActionListener {
    // Instance variables
    public User user;
    public JFrame frame;
    public NetworkConnection data;
    public AdminRefresh refresher;

    // for the (Add user) Function
    public JTextField addUserName = new JTextField(10);
    public JTextField addUserPassword = new JTextField(10);

    // for the (Update user) Function
    public JTextField changeUnit = new JTextField(10);
    public JTextField changePassword = new JTextField(10);
    public JComboBox<String> changeAccess = new JComboBox<>();
    public JCheckBox checkUnitCheck = new JCheckBox("Change org unit");
    public JCheckBox changePasswordCheck = new JCheckBox("Change password");
    public JCheckBox changeAccessCheck = new JCheckBox("Change access");

    // for the (Add unit) Function
    public JTextField addUnitName = new JTextField(10);
    public JTextField addUnitCredits = new JTextField();
    public JButton resetBalanceInput = new JButton("Reset input to $0");

    // for the (Adjusting a unit's balance) Function
    public JTextField newBalance = new JTextField();
    public JComboBox<String> balanceOptions = new JComboBox<>();
    public JButton resetAdjustBalanceInput = new JButton("Reset input to $0");

    // for the (Adjusting a unit's asset quantity) Function
    public JTextField newQuantity = new JTextField();
    public JComboBox<String> quantityOptions = new JComboBox<>();
    public JButton resetAdjustQuantityInput = new JButton("Reset input to 0");

    // for the (Add an asset to a unit) function
    public JTextField assetIDInput = new JTextField();
    public JTextField assetQtyInput = new JTextField();
    public JButton resetAssetIDInput = new JButton("Reset ID input to 0");
    public JButton resetAssetQtyInput = new JButton("Reset quantity input to 0");

    // for the (Add asset) function
    public JTextField newAssetID = new JTextField();
    public JButton resetNewAssetIDInput = new JButton("Reset asset ID to 0");
    public JTextField newAssetDesc = new JTextField();

    // for the (Update asset description) function
    public JTextField updateAssetDesc = new JTextField();

    /**
     * Constructor that activates the action listeners for reset buttons
     * @param user the currently logged-in user for the session
     * @param frame the main Java Swing frame
     * @param data the network data interface methods to carry over
     * @param refresher the table refresher instance
     */
    public AdminFunctions(User user, JFrame frame, NetworkConnection data, AdminRefresh refresher) {
        this.user = user;
        this.frame = frame;
        this.data = data;
        this.refresher = refresher;

        // Start listening...
        checkboxListeners();
        resetBalanceInput.addActionListener(this);
        resetAdjustBalanceInput.addActionListener(this);
        resetAdjustQuantityInput.addActionListener(this);
        resetAssetIDInput.addActionListener(this);
        resetAssetQtyInput.addActionListener(this);
        resetNewAssetIDInput.addActionListener(this);
    }

    // FUNCTIONS FOR THE USER CONTROL PANEL-----------------------------------------------------------------------------
    /**
     * If an admin, call interface method to add users from the press of a button
     * Refresh all Users table immediately to show the user has been added
     * @param t the user table
     */
    public void addUserFunc(JTable t) {
        if (this.user.getAccess()) {
            try {
                // Setup the option drop down for the admin access
                // True = admin : False = not admin
                String[] items = {"True", "False"};
                JComboBox<String> combo = new JComboBox<>(items);

                // Reset the input fields to be empty so old inputs don't stick around
                addUserName.setText("");
                addUserPassword.setText("");

                // Put the input fields into the JOptionPane
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Username (converts to lowercase)"));
                panel.add(addUserName);
                panel.add(new JLabel("Password"));
                panel.add(addUserPassword);
                panel.add(new JLabel("Admin access"));
                panel.add(combo);

                int result = JOptionPane.showConfirmDialog(null, panel, "New user's details",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicks 'OK'...
                if (result == JOptionPane.OK_OPTION) {
                    // Retrieve the user's inputs
                    String name = addUserName.getText();
                    String password = addUserPassword.getText();
                    String access = (String) combo.getSelectedItem();

                    // If any of the fields are empty, show a warning.
                    if (name.isEmpty() | password.isEmpty() | access == null) {
                        showWarning("Please try again and fill in all fields to add a new user.", "Empty fields");
                    } else {
                        // Otherwise, attempt to add the user to the database & refresh the table
                        data.addUser(name, password, Boolean.parseBoolean(access));
                        refresher.updateUserTable(t);
                    }
                } else {
                    // If the user clicks 'CANCEL'...
                    System.out.println("Cancelled");
                }
            } catch (IllegalString | AlreadyExists ex) {
                if (ex instanceof IllegalString) {
                    showWarning("Username cannot have spaces and must only be letters.\nPassword cannot have spaces.",
                            "Invalid inputs");
                }
                if (ex instanceof  AlreadyExists) {
                    showWarning("Username already taken.", "Invalid inputs");
                }
                //ex.printStackTrace();
            }
        }
    }

    /**
     * If an admin, call interface method to update users from the press of a button
     * Refresh all Users table immediately to show the user has been updated
     * @param t the user table
     */
    public void updateUserFunc(JTable t) {
        if (user.getAccess()) {
            try {
                // Get the selected row (always the first column, username column)
                int row = t.getSelectedRow();
                String name = (String) t.getModel().getValueAt(row, 0);

                // Setup the option drop down for the admin access
                // True = admin : False = not admin
                String[] items = {"True", "False"};
                changeAccess = new JComboBox<>(items);

                // Disable all text fields until the user checks boxes
                changeUnit.setEnabled(false);
                changePassword.setEnabled(false);
                changeAccess.setEnabled(false);

                // Disable all check boxes so user's start with a clean-slate
                checkUnitCheck.setSelected(false);
                changePasswordCheck.setSelected(false);
                changeAccessCheck.setSelected(false);

                // Reset the input fields to be empty so old inputs don't stick around
                changeUnit.setText("");
                changePassword.setText("");

                // Put the input fields into the JOptionPane
                JPanel panel = new JPanel(new GridLayout(0, 2));
                panel.add(changeUnit);
                panel.add(checkUnitCheck);
                panel.add(changePassword);
                panel.add(changePasswordCheck);
                panel.add(changeAccess);
                panel.add(changeAccessCheck);

                int result = JOptionPane.showConfirmDialog(null, panel, "New user's details",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicks 'OK'...
                if (result == JOptionPane.OK_OPTION) {
                    if (changeUnit.isEnabled()) {
                        String unit = changeUnit.getText();
                        data.updateUsersUnit(name, unit);
                    }

                    if (changePassword.isEnabled()) {
                        String password = changePassword.getText();
                        data.changePassword(name, password);
                    }

                    if (changeAccess.isEnabled()) {
                        boolean access = Boolean.parseBoolean((String) changeAccess.getSelectedItem());
                        data.updateUsersAccess(name, access);
                    }

                    // After updating any data, refresh the table
                    refresher.updateUserTable(t);

                } else {
                    // If the user clicks 'CANCEL'...
                    System.out.println("Cancelled");
                }
            } catch (ArrayIndexOutOfBoundsException | DoesNotExist | IllegalString ex) {
                if (ex instanceof ArrayIndexOutOfBoundsException) {
                    showWarning("Please select a user to update.", "No user selected");
                }
                if (ex instanceof DoesNotExist) {
                    showWarning("Cannot add to '" + changeUnit.getText() + "' as unit does not exist.", "Non-existent unit");
                }
                if (ex instanceof IllegalString) {
                    showWarning("Password cannot have spaces.", "Invalid password");
                }
                //ex.printStackTrace();
            }
        }
    }

    /**
     * If an admin, call interface method to delete users from the press of a button.
     * Refresh all Users table immediately to show the user has been deleted.
     * @param t the user table
     */
    public void deleteUserFunc(JTable t) {
        if (user.getAccess()) {
            try {
                // Get the selected row (always the first column, username column)
                int row = t.getSelectedRow();
                String name = (String) t.getModel().getValueAt(row, 0);

                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete '" + name + "'?",
                        "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user pressed 'OK'...
                if (result == JOptionPane.OK_OPTION) {
                    // Attempt to delete the user from the database & refresh the table
                    data.removeUser(name);
                    refresher.updateUserTable(t);
                } else {
                    // If the user pressed 'CANCEL'...
                    System.out.println("Cancelled");
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                showWarning("Please select a user to delete.", "No user selected");
                //ex.printStackTrace();
            }
        }
    }

    /**
     * If an admin, confirm the selected user will be removed from their unit.
     * @param t the user table
     */
    public void removeFromUnitFunc(JTable t) {
        if (user.getAccess()) {
            try {
                // Get the selected row (always the first column, username column)
                int row = t.getSelectedRow();
                String name = (String) t.getModel().getValueAt(row, 0);

                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to remove '" + name + "' from their unit?",
                        "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user pressed 'OK'...
                if (result == JOptionPane.OK_OPTION) {
                    // Attempt to remove the user from their unit & refresh the table
                    data.removeUserUnit(name.toLowerCase());
                    refresher.updateUserTable(t);
                } else {
                    // If the user pressed 'CANCEL'...
                    System.out.println("Cancelled");
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                showWarning("Please select a user to remove from unit.", "No user selected");
                //ex.printStackTrace();
            }
        }
    }

    // FUNCTIONS FOR THE UNIT CONTROL PANEL-----------------------------------------------------------------------------
    /**
     * If an admin, call interface method to add units from the press of a button
     * Refresh all Units table immediately to show the unit has been added
     * @param t the unit table
     */
    public void addUnitFunc(JTable t) {
        if (this.user.getAccess()) {
            try {
                // Format the text-box to only allow numbers
                addUnitCredits = new JFormattedTextField(numbersOnlyFormat());

                // Reset the input fields to be empty so old inputs don't stick around
                addUnitCredits.setText("0");
                addUnitName.setText("");

                // Prepare the pop-up for user inputs
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Organisational Unit name"));
                panel.add(addUnitName);
                panel.add(new JLabel("Starting balance"));
                panel.add(addUnitCredits);
                panel.add(resetBalanceInput);

                int result = JOptionPane.showConfirmDialog(null, panel, "New unit's details",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicks 'OK'...
                if (result == JOptionPane.OK_OPTION) {

                    // Get the user inputs for the new unit name + starting balance
                    // and format the balance to remove any commas before converting to an integer
                    String name = (addUnitName.getText()).toLowerCase();
                    String balance = addUnitCredits.getText();
                    balance = balance.replaceAll("[^0-9]", "");
                    int balanceFormatted = Integer.parseInt(balance);

                    if (name.isEmpty()) {
                        showWarning("Please try again and fill in all fields to add a new unit.", "Empty fields");
                    } else {
                        // Add the unit to the database & refresh the units table
                        data.addUnit(name, balanceFormatted);
                        refresher.updateUnitTable(t);
                    }
                } else {
                    // If the user clicks 'CANCEL'...
                    System.out.println("Cancelled");
                }
            } catch (IllegalString | AlreadyExists ex) {
                if (ex instanceof  IllegalString) {
                    showWarning("Organisational Unit name cannot have spaces and must be letters only.", "Invalid unit name");
                }
                if (ex instanceof  AlreadyExists) {
                    showWarning("Unit name already taken.", "Invalid inputs");
                }
                //ex.printStackTrace();
            }
        }
    }

    /**
     * If an admin, call interface method to delete users from the press of a button
     * Refresh all Units table immediately to show the unit has been deleted
     * @param unitTable the unit table
     * @param userTable the user table
     */
    public void deleteUnitFunc(JTable unitTable, JTable userTable) {
        if (user.getAccess()) {
            try {
                // Get the selected row (always the first column, unit name column)
                int row = unitTable.getSelectedRow();
                String name = (String) unitTable.getModel().getValueAt(row, 0);

                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete '" + name + "'?",
                        "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicks 'OK'...
                if (result == JOptionPane.OK_OPTION) {
                    // Remove the unit from the database & refresh the units table
                    data.removeUnit(name.toLowerCase());
                    refresher.updateUnitTable(unitTable);
                    refresher.updateUserTable(userTable);
                } else {
                    // If the user clicks 'CANCEL'...
                    System.out.println("Cancelled");
                }

            } catch (ArrayIndexOutOfBoundsException ex) {
                showWarning("Please select a unit to delete.", "No unit selected");
                //ex.printStackTrace();
            }
        }
    }

    /**
     * If an admin, call interface method to adjust a unit's balance from the press of a button
     * Refresh all Units table immediately to show the new balance for all rows belonging to that unit
     * @param t the unit table
     */
    public void adjustBalanceFunc(JTable t) {
        if (user.getAccess()) {
            try {
                // Get the selected row (unit name + credits columns)
                int row = t.getSelectedRow();
                String name = (String) t.getModel().getValueAt(row, 0);
                String credits = (String) t.getModel().getValueAt(row, 1);
                credits = credits.replaceAll("[^0-9 -]", ""); // remove all '$' signs from credits
                int creditsFormatted = Integer.parseInt(credits); // convert credits string to int

                // Drop down for choosing to set, add, or subtract from the unit's balance
                String[] items = {"Set", "Increase", "Decrease"};
                balanceOptions = new JComboBox<>(items);

                // Format the text-box to only allow numbers
                newBalance = new JFormattedTextField(numbersOnlyFormat());
                newBalance.setText("0");

                // Prepare the pop-up for user inputs
                JPanel panel = new JPanel(new GridLayout(0, 2));
                panel.add(newBalance);
                panel.add(balanceOptions);
                panel.add(resetAdjustBalanceInput);

                int result = JOptionPane.showConfirmDialog(null, panel, name + "'s new balance",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicks 'OK'...
                if (result == JOptionPane.OK_OPTION) {
                    String balance = newBalance.getText();
                    balance = balance.replaceAll("[^0-9]", "");
                    int balanceFormatted = Integer.parseInt(balance);

                    if (balanceOptions.getSelectedIndex() == 0) {
                        data.adjustBalance(name, balanceFormatted);
                    } else if (balanceOptions.getSelectedIndex() == 1) {
                        int finalBalance = creditsFormatted + balanceFormatted;
                        data.adjustBalance(name, finalBalance);
                    } else if (balanceOptions.getSelectedIndex() == 2) {
                        int finalBalance = creditsFormatted - balanceFormatted;
                        data.adjustBalance(name, finalBalance);
                    } else { // default
                        data.adjustBalance(name, balanceFormatted);
                    }

                    refresher.updateUnitTable(t);

                } else {
                    // If the user clicks 'CANCEL'...
                    System.out.println("Cancelled");
                }

            } catch (ArrayIndexOutOfBoundsException ex) {
                showWarning("Please select a unit to change balance.", "No unit selected");
                //ex.printStackTrace();
            }

        }
    }

    /**
     * If an admin, call interface method to adjust the quantity of a specific asset a unit has
     * from the press of a button. Refresh all Units table immediately to show the new asset quantity
     * @param t the unit table
     */
    public void adjustAssetQuantityFunc(JTable t) {
        if (user.getAccess()) {
            try {
                // Get the selected row (unit name + asset ID columns)
                int row = t.getSelectedRow();
                String name = (String) t.getModel().getValueAt(row, 0);
                String assetID = (String) t.getModel().getValueAt(row, 2);

                // If the selected unit row has no asset ID, warn the user
                if (assetID.isEmpty()) {
                    throw new ArrayIndexOutOfBoundsException();
                }

                // Ensure the asset ID and quantity are numbers only for safety
                assetID = assetID.replaceAll("[^0-9]", "");
                int assetIDFormatted = Integer.parseInt(assetID);
                String assetQty = (String) t.getModel().getValueAt(row, 4);
                assetQty = assetQty.replaceAll("[^0-9]", "");
                int assetQtyFormatted = Integer.parseInt(assetQty);

                // Drop down for choosing to set, add, or subtract from the unit asset's quantity
                String[] items = {"Set", "Increase", "Decrease"};
                quantityOptions = new JComboBox<>(items);

                // Format the text-box to only allow numbers
                newQuantity = new JFormattedTextField(numbersOnlyFormat());
                newQuantity.setText("0");

                // Prepare the pop-up for user inputs
                JPanel panel = new JPanel(new GridLayout(0, 2));
                panel.add(newQuantity);
                panel.add(quantityOptions);
                panel.add(resetAdjustQuantityInput);

                int result = JOptionPane.showConfirmDialog(null, panel, name + "'s new asset quantity",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicks 'OK'...
                if (result == JOptionPane.OK_OPTION) {
                    String quantity = newQuantity.getText();
                    quantity = quantity.replaceAll("[^0-9]", "");
                    int quantityFormatted = Integer.parseInt(quantity);

                    if (quantityOptions.getSelectedIndex() == 0) {
                        data.adjustAssetQuantity(name, assetIDFormatted, quantityFormatted);
                    } else if (quantityOptions.getSelectedIndex() == 1) {
                        int finalQty = assetQtyFormatted + quantityFormatted;
                        data.adjustAssetQuantity(name, assetIDFormatted, finalQty);
                    } else if (quantityOptions.getSelectedIndex() == 2) {
                        int finalQty = assetQtyFormatted - quantityFormatted;
                        data.adjustAssetQuantity(name, assetIDFormatted, finalQty);
                    } else { // default
                        data.adjustAssetQuantity(name, assetIDFormatted, quantityFormatted);
                    }

                    refresher.updateUnitTable(t);

                } else {
                    // If the user clicks 'CANCEL'...
                    System.out.println("Cancelled");
                }

            } catch (ArrayIndexOutOfBoundsException ex) {
                showWarning("Please select a unit's asset to change the quantity.", "No unit asset selected");
                //ex.printStackTrace();
            }

        }
    }

    /**
     * If an admin, call interface method to add an asset to a unit (new row) from the press of a button
     * Refresh all Units table immediately to show the new asset has been attached to the unit
     * @param t the unit table
     */
    public void addAssetToUnitFunc(JTable t) {
        if (user.getAccess()) {
            try {
                // Get the selected row (unit name + credits columns)
                int row = t.getSelectedRow();
                String name = (String) t.getModel().getValueAt(row, 0);
                String credits = (String) t.getModel().getValueAt(row, 1);
                credits = credits.replaceAll("[^0-9 -]", ""); // remove "$" signs from credits
                int creditsFormatted = Integer.parseInt(credits); // convert credits string to int

                JPanel panel = new JPanel(new GridLayout(0, 1));

                // Format the text-boxes to only allow numbers
                assetIDInput = new JFormattedTextField(numbersOnlyFormat());
                assetQtyInput = new JFormattedTextField(numbersOnlyFormat());
                assetIDInput.setText("0");
                assetQtyInput.setText("0");

                // Prepare the pop-up for user inputs
                panel.add(new JLabel("Asset ID"));
                panel.add(assetIDInput);
                panel.add(resetAssetIDInput);
                panel.add(new JLabel("Asset Quantity"));
                panel.add(assetQtyInput);
                panel.add(resetAssetQtyInput);

                int result = JOptionPane.showConfirmDialog(null, panel, name + "'s new balance",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicks 'OK'...
                if (result == JOptionPane.OK_OPTION) {
                    String assetID = assetIDInput.getText();
                    assetID = assetID.replaceAll("[^0-9]", "");
                    int assetIDFormatted = Integer.parseInt(assetID);

                    String assetQty = assetQtyInput.getText();
                    assetQty = assetQty.replaceAll("[^0-9]", "");
                    int assetQtyFormatted = Integer.parseInt(assetQty);

                    data.addAssetToUnit(name, creditsFormatted, assetIDFormatted, assetQtyFormatted);

                    refresher.updateUnitTable(t);

                } else {
                    // If the user clicks 'CANCEL'...
                    System.out.println("Cancelled");
                }

            } catch (ArrayIndexOutOfBoundsException | AlreadyExists | DoesNotExist ex) {
                // IF statements rather than IF-ELSE so user can see all the errors they encounter, not just one
                if (ex instanceof ArrayIndexOutOfBoundsException) {
                    showWarning("Please select a row for the unit you wish to add an asset to.", "No unit selected");
                }
                if (ex instanceof AlreadyExists) {
                    showWarning("Unit already has this asset.", "Cannot add asset again");
                }
                if (ex instanceof DoesNotExist) {
                    showWarning("Asset ID does not exist.", "Cannot add non-existent asset");
                }
                //ex.printStackTrace();
            }
        }
    }

    /**
     * If an admin, call interface method to remove an asset from a unit (delete row) from the press of a button
     * Refresh all Units table immediately to show the asset has been removed from  the unit
     * @param t the unit table
     */
    public void removeAssetFromUnitFunc(JTable t) {
        if (user.getAccess()) {
            try {
                // Get the selected row (unit name + asset ID columns)
                int row = t.getSelectedRow();
                String name = (String) t.getModel().getValueAt(row, 0);
                String assetID = (String) t.getModel().getValueAt(row, 2);

                // Remove any values that are not integers for safety as asset ID should only be numbers
                assetID = assetID.replaceAll("[^0-9]", "");

                // If the field is empty, warn the user
                if (assetID.isEmpty()) {
                    throw new ArrayIndexOutOfBoundsException();
                }

                int assetIDFormatted = Integer.parseInt(assetID); // convert asset ID string to int

                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to remove '" +
                                assetID + "' from '" + name +  "'?",
                        "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicks 'OK'...
                if (result == JOptionPane.OK_OPTION) {
                    // Remove the row in the database corresponding to this unit & this asset
                    // then refresh the units table to show the update was a success
                    data.removeAssetFromUnit(name.toLowerCase(), assetIDFormatted);
                    refresher.updateUnitTable(t);
                } else {
                    // If the user clicks 'CANCEL'...
                    System.out.println("Cancelled");
                }

            } catch (ArrayIndexOutOfBoundsException ex) {
                showWarning("Please select a row for the asset you wish to remove from the unit.", "No asset selected");
                //ex.printStackTrace();
            }
        }
    }

    // FUNCTIONS FOR THE ASSET CONTROL PANEL ---------------------------------------------------------------------------
    /**
     * If an admin, call interface method to add a new asset from the press of a button
     * Refresh all Assets tables immediately to show the new asset exists
     * @param t the asset table
     */
    public void addAssetFunc(JTable t) {
        if (this.user.getAccess()) {
            try {
                // Format the text-box to only allow numbers
                newAssetID = new JFormattedTextField(numbersOnlyFormat());
                newAssetID.setText("0");
                newAssetDesc.setText("");

                // Prepare the pop-up for user inputs
                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Unique Asset ID"));
                panel.add(newAssetID);
                panel.add(resetNewAssetIDInput);
                panel.add(new JLabel("Unique Asset Desc"));
                panel.add(newAssetDesc);

                int result = JOptionPane.showConfirmDialog(null, panel, "New asset's details",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicks 'OK'...
                if (result == JOptionPane.OK_OPTION) {
                    String id = newAssetID.getText(); // get the user input for the asset ID
                    id = id.replaceAll("[^0-9]", ""); // ensure the asset ID is numbers only
                    String desc = newAssetDesc.getText(); // get the user input for the asset description

                    // If either of the fields are empty, warn the use
                    if (newAssetID.getText().isEmpty() | newAssetDesc.getText().isEmpty()) {
                        showWarning("Please try again and fill in all fields to add a new asset.", "Empty fields");
                    } else {
                        // Add the asset to the database and refresh the assets table
                        data.addAsset(id, desc);
                        refresher.updateAssetTable(t);
                    }
                } else {
                    // If the user clicks 'CANCEL'...
                    System.out.println("Cancelled");
                }
            } catch (AlreadyExists ex) {
                showWarning("Asset ID or description already taken.", "Invalid inputs");
                //ex.printStackTrace();
            }
        }
    }

    /**
     * If an admin, call interface method to delete the asset from the press of a button
     * Refresh all Assets + all Units tables immediately to show that the asset no longer exists, sync across all tables
     * @param assetsTable the asset table
     * @param unitsTable the unit table
     */
    public void deleteAssetFunc(JTable assetsTable, JTable unitsTable) {
        if (user.getAccess()) {
            try {
                // Get the selected row (asset ID column)
                int row = assetsTable.getSelectedRow();
                String id = (String) assetsTable.getModel().getValueAt(row, 0);

                int result = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete '" + id + "'?",
                        "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicks 'OK'...
                if (result == JOptionPane.OK_OPTION) {
                    // Delete the asset from the database & update the relevant tables
                    data.removeAsset(id.toLowerCase());
                    refresher.updateAssetTable(assetsTable);
                    refresher.updateUnitTable(unitsTable); // as the asset will be removed
                } else {
                    // If the user clicks 'CANCEL'...
                    System.out.println("Cancelled");
                }

            } catch (ArrayIndexOutOfBoundsException ex) {
                showWarning("Please select an asset to delete.", "No asset selected");
                //ex.printStackTrace();
            }
        }
    }

    /**
     * If an admin, call interface method to update the asset's description from the press of a button
     * Refresh all Assets + all Units tables immediately to show the new description synced across tables
     * @param assetsTable the asset table
     * @param unitsTable the unit table
     */
    public void updateAssetFunc(JTable assetsTable, JTable unitsTable) {
        if (this.user.getAccess()) {
            try {
                // Get the selected row (asset ID column)
                int row = assetsTable.getSelectedRow();
                String id = (String) assetsTable.getModel().getValueAt(row, 0);

                // Reset the input field to be empty so old inputs don't stick around
                updateAssetDesc.setText("");

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("New description for '" + id + "'"));
                panel.add(updateAssetDesc);

                int result = JOptionPane.showConfirmDialog(null, panel, "New asset description",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // If the user clicks 'OK'...
                if (result == JOptionPane.OK_OPTION) {

                    // If the input field is empty, warn the user
                    String desc = updateAssetDesc.getText();
                    if (desc.isEmpty()) {
                        showWarning("Please try again and fill in the field to change this asset's description.",
                                "Empty fields");
                    } else {
                        // Update the asset description via network connection interface method
                        // and refresh asset table AND unit table (as unit's asset descriptions will change)
                        data.updateAssetDesc(id, desc);
                        refresher.updateAssetTable(assetsTable);
                        refresher.updateUnitTable(unitsTable);
                    }
                } else {
                    // If the user clicks 'CANCEL'...
                    System.out.println("Cancelled");
                }
            } catch (AlreadyExists ex) {
                showWarning("Asset description already taken.", "Invalid inputs");
                //ex.printStackTrace();
            }
        }
    }

    // HELPER FUNCTIONS ------------------------------------------------------------------------------------------------
    /**
     * Generic pop-up warning template where the only the message and title changes
     * @param message the custom message
     * @param title the custom title
     */
    public void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(frame,
                message,
                title,
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Pass in this formatter into any text field to restrict it to numbers only
     * while also adding commas after every thousand for easy readability.
     * @return the number formatter that can be used with text fields
     */
    public NumberFormatter numbersOnlyFormat() {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(999999);
        formatter.setAllowsInvalid(false);

        return formatter;
    }

    // LISTENERS -------------------------------------------------------------------------------------------------------
    /**
     * Item listeners for checkboxes
     */
    public void checkboxListeners() {
        changeAccessCheck.addItemListener(e -> {
            // Enables / disables drop down for admin access
            if (e.getStateChange() == ItemEvent.SELECTED) {
                changeAccess.setEnabled(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                changeAccess.setEnabled(false);
            }
        });

        changePasswordCheck.addItemListener(e -> {
            // Enables / disables input field for new password
            if (e.getStateChange() == ItemEvent.SELECTED) {
                changePassword.setEnabled(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                changePassword.setEnabled(false);
            }
        });

        checkUnitCheck.addItemListener(e -> {
            // Enables / disables input field for new username
            if (e.getStateChange() == ItemEvent.SELECTED) {
                changeUnit.setEnabled(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                changeUnit.setEnabled(false);
            }
        });
    }

    /**
     * Action listeners for ALL refresh buttons (will refresh all number-only input fields)
     * @param e the event (which button was pressed)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == resetBalanceInput | e.getSource() == resetAdjustBalanceInput | e.getSource() == resetAdjustQuantityInput | e.getSource() == resetAssetIDInput | e.getSource() == resetAssetQtyInput | e.getSource() == resetNewAssetIDInput) {
        addUnitCredits.setText("0");
        newBalance.setText("0");
        newQuantity.setText("0");
        assetIDInput.setText("0");
        assetQtyInput.setText("0");
        newAssetID.setText("0");
        }
    }
}
