package ClientSide.GUI;

import ClientSide.User;
import ServerSide.NetworkConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Admin home panel, has a tabbed pane with 3 tabs (one per table)
 * Users with admin privileges are able to view and edit all objects
 * in the database. This class uses a single instance of...
 * AdminTables/AdminRefresh/AdminFunctions
 * @author Johnny Madigan
 */
public class AdminHome implements ActionListener {
    // Carry over the following to keep things in sync (logged in user, main JFrame, database interface methods)
    private final User user;

    // Admin home's helper instances (table creator, table refresher, interactions with the database)
    public AdminRefresh tableRefresher;
    public AdminTables tableCreator;
    public AdminFunctions interactions;

    // Admin home itself + welcome label
    public JLabel welcomeLabel = new JLabel("Welcome back!", SwingConstants.CENTER); // default txt
    public JPanel adminHome = new JPanel();

    // Tables to display data from the database
    public JTable allUsersTable = new JTable();
    public JTable allAssetsTable = new JTable();
    public JTable allUnitsTable = new JTable();

    // Tabs (one per table)
    public JPanel usersTableTab = new JPanel();
    public JPanel unitsTableTab = new JPanel();
    public JPanel assetsTableTab = new JPanel();

    // Edit users control panel buttons
    public JButton refreshUserTable = new JButton("Refresh");
    public JButton addUser = new JButton("New user");
    public JButton deleteUser = new JButton("Delete user");
    public JButton updateUser = new JButton("Update user");
    public JButton removeUserFromUnit = new JButton("Remove from unit");

    // Edit units control panel buttons
    public JButton refreshUnitTable = new JButton("Refresh");
    public JButton addUnit = new JButton("New unit");
    public JButton deleteUnit = new JButton("Delete unit");
    public JButton adjustBalance = new JButton("Adjust balance");
    public JButton addAssetToUnit = new JButton("Add asset to unit");
    public JButton adjustQuantity = new JButton("Adjust asset quantity");
    public JButton removeAssetFromUnit = new JButton("Remove asset from unit");

    // Edit assets control panel buttons
    public JButton refreshAssetTable = new JButton("Refresh");
    public JButton addAsset = new JButton("New asset");
    public JButton deleteAsset = new JButton("Delete asset");
    public JButton updateAsset = new JButton("Update asset");

    /**
     * Constructor for the admin home
     * (makes instances of Admin Refresh/Admin Tables/Admin Functions)
     * @param user the currently logged-in user for the session
     * @param frame the main Java Swing frame
     * @param data the network data interface methods to carry over
     */
    public AdminHome(User user, JFrame frame, NetworkConnection data) {
        // Assign the following to keep things in sync
        this.user = user;

        // Admin home's helper instances
        tableRefresher = new AdminRefresh(data);
        tableCreator = new AdminTables(tableRefresher);
        interactions = new AdminFunctions(this.user, frame, data, tableRefresher);

        // Action listener for users control panel
        refreshUserTable.addActionListener(this);
        addUser.addActionListener(this);
        deleteUser.addActionListener(this);
        updateUser.addActionListener(this);
        removeUserFromUnit.addActionListener(this);

        // Action listener for units control panel
        refreshUnitTable.addActionListener(this);
        addUnit.addActionListener(this);
        deleteUnit.addActionListener(this);
        adjustBalance.addActionListener(this);
        addAssetToUnit.addActionListener(this);
        adjustQuantity.addActionListener(this);
        removeAssetFromUnit.addActionListener(this);

        // Action listener for assets control panel
        refreshAssetTable.addActionListener(this);
        addAsset.addActionListener(this);
        deleteAsset.addActionListener(this);
        updateAsset.addActionListener(this);

        createContent(); // fill the admin home content panel
    }

    /**
     * Fill the admin home content panel...
     * This panel is dense and packed with features hence the admin helper classes.
     * The general layout is a label with a tab pane below which has 3 tabs.
     * Each tab has a table (users, units, assets) and a control panel to modify the data.
     */
    public void createContent() {
        // New admin home panel to create & new tab pane to fill out
        adminHome = new JPanel();
        adminHome.setPreferredSize(new Dimension(620,300));
        JTabbedPane tp = new JTabbedPane();

        // New tab panels & set layout
        usersTableTab = new JPanel();
        usersTableTab.setLayout(new BoxLayout(usersTableTab, BoxLayout.PAGE_AXIS));

        unitsTableTab = new JPanel();
        unitsTableTab.setLayout(new BoxLayout(unitsTableTab, BoxLayout.PAGE_AXIS));

        assetsTableTab = new JPanel();
        assetsTableTab.setLayout(new BoxLayout(assetsTableTab, BoxLayout.PAGE_AXIS));

        // Construct tables one-time here
        usersTableTab.add(tableCreator.constructUserTable(allUsersTable));
        unitsTableTab.add(tableCreator.constructUnitTable(allUnitsTable));
        assetsTableTab.add(tableCreator.constructAssetTable(allAssetsTable));

        // Control panel for 'Users' tab, a series of buttons to edit users
        JPanel editUserControls = new JPanel();
        editUserControls.add(refreshUserTable);
        editUserControls.add(addUser);
        editUserControls.add(deleteUser);
        editUserControls.add(updateUser);
        editUserControls.add(removeUserFromUnit);
        usersTableTab.add(editUserControls);

        // Control panel for 'Units' tab, a series of buttons to edit units (2 rows)
        JPanel editUnitControlsRow1 = new JPanel();
        editUnitControlsRow1.add(refreshUnitTable);
        editUnitControlsRow1.add(addUnit);
        editUnitControlsRow1.add(deleteUnit);
        editUnitControlsRow1.add(adjustBalance);

        JPanel editUnitControlsRow2 = new JPanel();
        editUnitControlsRow2.add(addAssetToUnit);
        editUnitControlsRow2.add(adjustQuantity);
        editUnitControlsRow2.add(removeAssetFromUnit);

        unitsTableTab.add(editUnitControlsRow1);
        unitsTableTab.add(editUnitControlsRow2);

        // Control panel for 'Assets' tab, a series of buttons to edit assets
        JPanel editAssetControls = new JPanel();
        editAssetControls.add(refreshAssetTable);
        editAssetControls.add(addAsset);
        editAssetControls.add(deleteAsset);
        editAssetControls.add(updateAsset);
        assetsTableTab.add(editAssetControls);



        // Refresh button icon for a more intuitive UI
        ImageIcon refreshIcon = new ImageIcon("./Images/GUI images/refresh_icon.png");
        refreshUserTable.setIcon(refreshIcon);
        refreshUnitTable.setIcon(refreshIcon);
        refreshAssetTable.setIcon(refreshIcon);

        ImageIcon addIcon = new ImageIcon("./Images/GUI images/add_icon.png");
        addAsset.setIcon(addIcon);
        addUnit.setIcon(addIcon);
        addUser.setIcon(addIcon);
        addAssetToUnit.setIcon(addIcon);

        ImageIcon updateIcon = new ImageIcon("./Images/GUI images/update_icon.png");
        updateAsset.setIcon(updateIcon);
        updateUser.setIcon(updateIcon);
        adjustQuantity.setIcon(updateIcon);
        adjustBalance.setIcon(updateIcon);

        ImageIcon deleteIcon = new ImageIcon("./Images/GUI images/delete_icon.png");
        deleteAsset.setIcon(deleteIcon);
        deleteUser.setIcon(deleteIcon);
        deleteUnit.setIcon(deleteIcon);

        // Add the tabs along with their name, icons, & content
        ImageIcon usersIcon = new ImageIcon("./Images/GUI images/users_icon.png");
        ImageIcon unitsIcon = new ImageIcon("./Images/GUI images/units_icon.png");
        ImageIcon assetsIcon = new ImageIcon("./Images/GUI images/assets_icon.png");

        tp.addTab("Users", usersIcon, usersTableTab);
        tp.addTab("Units", unitsIcon, unitsTableTab);
        tp.addTab("Assets", assetsIcon, assetsTableTab);

        // Format welcome label with the user's username but uppercase the first letter
        String usernameUC = user.getUsername().substring(0, 1).toUpperCase() + user.getUsername().substring(1);
        welcomeLabel.setText(String.format("Welcome back %s", usernameUC));

        // Add the welcome label with the tab pane below it
        adminHome.add(welcomeLabel);
        adminHome.add(tp);
    }

    /**
     * Getter for the admin home panel...
     * to easily switch contents when going from login screen to admin home
     * @return the admin home panel
     */
    public JPanel getPanel() {
        return adminHome;
    }

    /**
     * Action listeners for all admin buttons
     * @param e the event (which button was pressed)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addUser) {
            interactions.addUserFunc(allUsersTable);
        } else if (e.getSource() == updateUser) {
            interactions.updateUserFunc(allUsersTable);
        } else if (e.getSource() == deleteUser) {
            interactions.deleteUserFunc(allUsersTable);
        } else if (e.getSource() == refreshUserTable | e.getSource() == refreshUnitTable | e.getSource() == refreshAssetTable) {
            tableRefresher.updateUserTable(allUsersTable);
            tableRefresher.updateUnitTable(allUnitsTable);
            tableRefresher.updateAssetTable(allAssetsTable);
        } else if (e.getSource() == removeUserFromUnit) {
            interactions.removeFromUnitFunc(allUsersTable);
        } else if (e.getSource() == addUnit) {
            interactions.addUnitFunc(allUnitsTable);
        } else if (e.getSource() == deleteUnit) {
            interactions.deleteUnitFunc(allUnitsTable, allUsersTable);
        } else if (e.getSource() == adjustBalance) {
            interactions.adjustBalanceFunc(allUnitsTable);
        } else if (e.getSource() == addAssetToUnit) {
            interactions.addAssetToUnitFunc(allUnitsTable);
        } else if (e.getSource() == removeAssetFromUnit) {
            interactions.removeAssetFromUnitFunc(allUnitsTable);
        } else if (e.getSource() == adjustQuantity) {
            interactions.adjustAssetQuantityFunc(allUnitsTable);
        } else if (e.getSource() == addAsset) {
            interactions.addAssetFunc(allAssetsTable);
        } else if (e.getSource() == deleteAsset) {
            interactions.deleteAssetFunc(allAssetsTable, allUnitsTable);
        } else if (e.getSource() == updateAsset) {
            interactions.updateAssetFunc(allAssetsTable, allUnitsTable);
        }
    }
}
