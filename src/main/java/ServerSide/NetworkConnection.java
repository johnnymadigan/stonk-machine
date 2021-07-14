package ServerSide;

import ClientSide.*;
import ClientSide.Exceptions.*;
import common.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The core SQL commands to construct the database and where
 * the DatabaseSource interface methods are defined.
 * @author Alistair Ridge, Johnny Madigan and Scott Peachey
 */
public class NetworkConnection implements DatabaseSource {
    // Network Connection ----------------------------------------------------------------------------------------------
    public EstablishConnection server;
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // SQL Statements --------------------------------------------------------------------------------------------------
    /**
    These are the generic SQL statements that will have an objects information added to it before being passed over the
    network stream. Included are statements used to setup the database tables to allow for simple deployment.
     */
    public static final String CREATE_USERS_TABLE =
            "CREATE TABLE IF NOT EXISTS users( "
                    + " username VARCHAR(30) PRIMARY KEY NOT NULL UNIQUE,"
                    + " password VARCHAR(200) NOT NULL,"
                    + " salt VARCHAR(30) NOT NULL UNIQUE,"
                    + " unit VARCHAR(30),"
                    + " access VARCHAR(30) NOT NULL" + " );";

    public static final String CREATE_ORDERS_TABLE =
            "CREATE TABLE IF NOT EXISTS orders( "
                    + " orderID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " type VARCHAR(30),"
                    + " orgunit VARCHAR(30),"
                    + " assetID VARCHAR(30),"
                    + " qty INTEGER,"
                    + " price INTEGER,"
                    + " date VARCHAR(19)" + " );";

    public static final String CREATE_ORDER_HISTORY_TABLE =
            "CREATE TABLE IF NOT EXISTS history( "
                    + " orderID INTEGER PRIMARY KEY /*!40101 AUTO_INCREMENT */ NOT NULL UNIQUE,"
                    + " type VARCHAR(30),"
                    + " orgunit VARCHAR(30),"
                    + " assetID INTEGER,"
                    + " qty INTEGER,"
                    + " price INTEGER,"
                    + " date VARCHAR(19)" + " );"; // Used to track the trade that was used to fulfill the order

    public static final String CREATE_ASSETS_TABLE =
            "CREATE TABLE IF NOT EXISTS assets( "
                    + " assetID INTEGER PRIMARY KEY NOT NULL UNIQUE,"
                    + " desc VARCHAR(250) NOT NULL UNIQUE" + " );"; // Needed for search

    public static final String CREATE_ORGUNITS_TABLE =
            "CREATE TABLE IF NOT EXISTS orgunits( "
                    + " rowID INTEGER PRIMARY KEY /*!40101 AUTO_INCREMENT */ NOT NULL UNIQUE,"
                    + " unitname VARCHAR(30) NOT NULL,"
                    + " credits INTEGER NOT NULL,"
                    + " assetID VARCHAR(10),"
                    + " assetqty INTEGER" + " );";

    // WARNING RESETS TABLES
    private static final String CLEAR_USER_TABLE = "DELETE FROM main.users";
    private static final String CLEAR_ORDERS_TABLE = "DELETE FROM main.orders";
    private static final String CLEAR_HISTORY_TABLE = "DELETE FROM main.history";
    private static final String CLEAR_ASSETS_TABLE = "DELETE FROM main.assets";
    private static final String CLEAR_ORGUNITS_TABLE = "DELETE FROM main.orgunits";

    // Users table generic sql statements
    private static final String GET_ALL_USERS = "SELECT * FROM main.users;";
    private static final String GET_USER = "SELECT * FROM main.users WHERE username=?;";
    private static final String INSERT_USER = "INSERT INTO main.users (username, password, salt, unit, access) VALUES (?, ?, ?, ?, ?);";
    private static final String REMOVE_USER = "DELETE FROM main.users WHERE username=?;";
    private static final String UPDATE_USER_UNIT = "UPDATE main.users SET unit=? WHERE username=?;";
    private static final String REMOVE_USER_UNIT = "UPDATE main.users SET unit=NULL WHERE username=?;";
    private static final String UPDATE_USER_PASSWORD = "UPDATE main.users SET password=? WHERE username=?;";
    private static final String UPDATE_USER_ACCESS = "UPDATE main.users SET access=? WHERE username=?;";
    private static final String GET_ALL_USERNAMES = "SELECT username FROM main.users;"; // to check if name is taken

    // Unit table generic sql statements
    private static final String GET_ALL_UNITS = "SELECT * FROM main.orgunits";
    private static final String GET_UNIT = "SELECT * FROM main.orgunits WHERE unitname=?";
    private static final String INSERT_UNIT = "INSERT INTO main.orgunits (unitname, credits) VALUES (?, ?);";
    private static final String ADJUST_BALANCE = "UPDATE main.orgunits SET credits=? WHERE unitname=?;";
    private static final String ADJUST_QUANTITY = "UPDATE main.orgunits SET assetqty=? WHERE unitname=? AND assetID=?;";
    private static final String REMOVE_UNIT = "DELETE FROM main.orgunits WHERE unitname=?";
    private static final String ADD_ASSET_TO_UNIT = "INSERT INTO main.orgunits (unitname, credits, assetID, assetqty) VALUES (?, ?, ?, ?);";
    private static final String REMOVE_ASSET_FROM_UNIT = "DELETE FROM main.orgunits WHERE unitname=? AND assetID=?";
    private static final String GET_ASSETS_FROM_UNIT = "SELECT * FROM main.orgunits WHERE unitname=?";
    private static final String GET_ALL_UNITNAMES = "SELECT unitname FROM main.orgunits"; // to check if name is taken

    // Asset table generic sql statements
    private static final String GET_ALL_ASSET_DESC = "SELECT desc FROM main.assets";
    private static final String GET_ALL_ASSETS = "SELECT * FROM main.assets";
    private static final String GET_ASSET = "SELECT * FROM main.assets WHERE assetID=?";
    private static final String INSERT_ASSET = "INSERT INTO main.assets (assetID, desc) VALUES (?, ?);";
    private static final String REMOVE_ASSET = "DELETE FROM main.assets WHERE assetID=?";
    private static final String UPDATE_ASSET = "UPDATE main.assets SET desc=? WHERE assetID=?";
    private static final String GET_ALL_ASSET_IDS = "SELECT assetID FROM main.assets"; // to check if name is taken
    private static final String REMOVE_ASSET_FROM_ALL_UNIT = "DELETE FROM main.orgunits WHERE assetID=?";

    // Order table generic sql statements
    private static final String INSERT_ORDER = "INSERT INTO main.orders (orderID, type, orgunit, assetID, qty, price, date) values (?,?,?,?,?,?,?)";
    private static final String REMOVE_ORDER = "DELETE FROM main.orders WHERE orderID=?";
    private static final String GET_ORDERS = "SELECT * FROM main.orders";
    private static final String GET_UNIT_ORDERS = "SELECT * FROM main.orders WHERE orgunit=?";
    private static final String GET_ASSET_ORDERS = "SELECT * FROM main.orders WHERE assetID=?";

    // Order history generic sql statements
    private static final String INSERT_ORDER_HISTORY = "INSERT INTO main.history (orderID, type, orgunit, assetID, qty, price, date) values (?,?,?,?,?,?,?)";
    private static final String GET_ORDER_HISTORY = "SELECT * FROM main.orders WHERE assetID=?";
    private static final String GET_UNIT_ORDER_HISTORY = "SELECT * FROM main.history WHERE orgunit=?";
    private static final String REMOVE_ORDER_HISTORY = "";

    // Prepared Statements ---------------------------------------------------------------------------------------------
    /**
    * These are the statements that will be sent over the network stream to interact with the DB.
    * These prepared statements will get passed the provided objects parameters to form the final SQL statement.
    */
    private Connection connection;

    // RESET TABLES
    private PreparedStatement clearUsersTable;
    private PreparedStatement clearOrdersTable;
    private PreparedStatement clearHistoryTable;
    private PreparedStatement clearAssetsTable;
    private PreparedStatement clearUnitsTable;

    // User prepared statements
    private PreparedStatement getAllUsers;
    private PreparedStatement getUser;
    private PreparedStatement addUser;
    private PreparedStatement removeUser;
    private PreparedStatement updateUserUnit;
    private PreparedStatement removeUserUnit;
    private PreparedStatement updateUserPassword;
    private PreparedStatement updateUserAccess;
    private PreparedStatement getAllUsernames;

    // Unit prepared statements
    private PreparedStatement getAllUnits;
    private PreparedStatement getUnit;
    private PreparedStatement addUnit;
    private PreparedStatement adjustBalance;
    private PreparedStatement adjustQuantity;
    private PreparedStatement removeUnit;
    private PreparedStatement removeAssetFromUnit;
    private PreparedStatement addAssetToUnit;
    private PreparedStatement getAllUnitNames;

    // Asset prepared statements
    private PreparedStatement getAllAssetDecs;
    private PreparedStatement getAllAssets;
    private PreparedStatement getAsset;
    private PreparedStatement addAsset;
    private PreparedStatement removeAsset;
    private PreparedStatement updateAsset;
    private PreparedStatement getUnitAssets;
    private PreparedStatement getAllAssetID;
    private PreparedStatement removeAssetFromAllUnits;

    // Order prepared statements
    private PreparedStatement addOrder;
    private PreparedStatement removeOrder;
    private PreparedStatement getOrders;
    private PreparedStatement getUnitOrders;
    private PreparedStatement getAssetOrders;

    // Order history prepared statements
    private PreparedStatement addOrderHistory;
    private PreparedStatement getOrderHistory;
    private PreparedStatement getUnitOrderHistory;
    private PreparedStatement removeOrderHistory;

    // Construct Database Interaction ----------------------------------------------------------------------------------
    /**
     * Constructor that:
     * -establishes the connection
     * -assigns SQL commands to statements
     * -adds the configured admin account
     */
    public NetworkConnection() {
        server = new EstablishConnection();
        connection = DBConnection.getInstance();
        try {
            Statement st = connection.createStatement();

            // Execute SQL table creation commands (checks if they exist first)
            st.execute(CREATE_USERS_TABLE);
            st.execute(CREATE_ORGUNITS_TABLE);
            st.execute(CREATE_ASSETS_TABLE);
            st.execute(CREATE_ORDERS_TABLE);
            st.execute(CREATE_ORDER_HISTORY_TABLE);

            // WARNING CLEARS DATA FROM ALL TABLES
            clearUsersTable = connection.prepareStatement(CLEAR_USER_TABLE);
            clearOrdersTable = connection.prepareStatement(CLEAR_ORDERS_TABLE);
            clearHistoryTable = connection.prepareStatement(CLEAR_HISTORY_TABLE);
            clearAssetsTable = connection.prepareStatement(CLEAR_ASSETS_TABLE);
            clearUnitsTable = connection.prepareStatement(CLEAR_ORGUNITS_TABLE);

            // Users Table: Assign SQL command with prepared statement
            getAllUsers = connection.prepareStatement(GET_ALL_USERS);
            getUser = connection.prepareStatement(GET_USER);
            addUser = connection.prepareStatement(INSERT_USER);
            removeUser = connection.prepareStatement(REMOVE_USER);
            updateUserUnit = connection.prepareStatement(UPDATE_USER_UNIT);
            removeUserUnit = connection.prepareStatement(REMOVE_USER_UNIT);
            updateUserPassword = connection.prepareStatement(UPDATE_USER_PASSWORD);
            updateUserAccess = connection.prepareStatement(UPDATE_USER_ACCESS);
            getAllUsernames = connection.prepareStatement(GET_ALL_USERNAMES);

            // Units Table: Assign SQL command with prepared statement
            getUnit = connection.prepareStatement(GET_UNIT);
            addUnit = connection.prepareStatement(INSERT_UNIT);
            getAllUnits = connection.prepareStatement(GET_ALL_UNITS);
            adjustBalance = connection.prepareStatement(ADJUST_BALANCE);
            adjustQuantity = connection.prepareStatement(ADJUST_QUANTITY);
            removeUnit = connection.prepareStatement(REMOVE_UNIT);
            removeAssetFromUnit = connection.prepareStatement(REMOVE_ASSET_FROM_UNIT);
            addAssetToUnit = connection.prepareStatement(ADD_ASSET_TO_UNIT);
            getAllUnitNames = connection.prepareStatement(GET_ALL_UNITNAMES);

            // Assets Table: Assign SQL command with prepared statement
            getAllAssetDecs = connection.prepareStatement(GET_ALL_ASSET_DESC);
            getAllAssets = connection.prepareStatement(GET_ALL_ASSETS);
            getAsset = connection.prepareStatement(GET_ASSET);
            addAsset = connection.prepareStatement(INSERT_ASSET);
            removeAsset = connection.prepareStatement(REMOVE_ASSET);
            updateAsset = connection.prepareStatement(UPDATE_ASSET);
            getUnitAssets = connection.prepareStatement(GET_ASSETS_FROM_UNIT);
            getAllAssetID = connection.prepareStatement(GET_ALL_ASSET_IDS);
            removeAssetFromAllUnits = connection.prepareStatement(REMOVE_ASSET_FROM_ALL_UNIT);

            // Orders Placed + Orders' History Tables: Assign SQL command with prepared statement
            addOrder = connection.prepareStatement(INSERT_ORDER);
            addOrderHistory = connection.prepareStatement(INSERT_ORDER_HISTORY);
            getOrderHistory = connection.prepareStatement(GET_ORDER_HISTORY);
            getUnitOrderHistory = connection.prepareStatement(GET_UNIT_ORDER_HISTORY);
            getOrders = connection.prepareStatement(GET_ORDERS);
            getUnitOrders = connection.prepareStatement(GET_UNIT_ORDERS);
            getAssetOrders = connection.prepareStatement(GET_ASSET_ORDERS);
            removeOrder = connection.prepareStatement(REMOVE_ORDER);

            // Add default user configured via the ServerSettings.props file
            String username = server.getUsername();
            String password = server.getPassword();

            if (!(username.isEmpty() | password.isEmpty())) {
                addUser(server.getUsername(), server.getPassword(), true);
                System.out.println("Default account initialised");
            } else {
                System.out.println("WARNING no default account configured");
            }

        } catch (SQLException | AlreadyExists | IllegalString ex) {
            if (ex instanceof AlreadyExists) {
                System.out.println("Default account already in the database");
            }
            // ex.printStackTrace();
        }
    }

    // WARNING Reset tables, fresh start
    public void resetEverything() {
        try {
            clearUsersTable.execute();
            clearOrdersTable.execute();
            clearHistoryTable.execute();
            clearAssetsTable.execute();
            clearUnitsTable.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // User methods ----------------------------------------------------------------------------------------------------
    /**
     * Method used when a user attempts to log in. Uses the username
     * to get the user from the database, then hash the input password
     * with that user's salt to see if there's a match.
     * @param username username input
     * @param password password input
     * @return the user object for the log-in session
     */
    @Override
    public User login(String username, String password) throws DoesNotExist, IllegalString {
        User u = getUser(username);

        // Hash the input password with the corresponding SALT
        String hashedInputPassword = HashPassword.hashPassword(password, u.getSalt());

        // If passwords do not match
        if (!hashedInputPassword.equals(u.getPassword())) {
            throw new IllegalString("Invalid password, please try again.");
        }

        // Return the user object, who will be logged-in for the session
        return u;
    }

    /**
     * Method used to get the data for a specified user from the database
     * @param name username of user to get
     * @return a User object that holds all the users data
     */
    @Override
    public User getUser(String name) throws DoesNotExist {
        User u = null;
        ResultSet rs;
        try {
            // Retrieve the user's data row
            getUser.setString(1, name); //replaces '?' with the field we want
            rs = getUser.executeQuery();
            rs.next();
            String username = rs.getString("username");
            String password = rs.getString("password");
            String salt = rs.getString("salt");
            String unit = rs.getString("unit");
            boolean access = Boolean.parseBoolean(rs.getString("access"));
            u = new User(username, password, salt, getUnit(unit), access);
        } catch (SQLException | IllegalString ex) {
            System.out.println(ex.getMessage());
            //ex.printStackTrace();
        }

        // If the SQL command fails because the user does not exist
        if (u == null){
            throw new DoesNotExist("User %s does not exist", name);
        }

        // Return the user object
        return u;
    }

    /**
     * Method used to add a user to the User table in the database
     * As methods should be single-purposed, the user will be added to a unit in the usersUnit() method
     * @param username user's unique username
     * @param password password the user will use to login
     * @param access admin access for the user
     */
    @Override
    public void addUser(String username, String password, boolean access) throws IllegalString, AlreadyExists {
        try {
            String usernameLC;

            // Check the username is valid
            if (username.matches("[a-zA-Z]+") && !(username.contains(" "))) {
                usernameLC = username.toLowerCase();
            } else {
                throw new IllegalString("Username '%s' must be letters only. Please try again.", username);
            }

            // Check if the username is taken
            Set<String> names = new TreeSet<>();
            ResultSet rs = getAllUsernames.executeQuery();
            while (rs.next()) {
                names.add(rs.getString("username"));
            }

            // For best practice, all usernames entering the database are converted to lowercase
            if (names.contains(usernameLC)) {
                throw new AlreadyExists("Username '%s' is already taken.", username);
            }

            // Generate SALT & password which also checks the given password is valid
            String salt = HashPassword.generateSALT(username);
            String hashedPassword = HashPassword.hashPassword(password, salt);

            // Setup generic SQL statement to insert the user row
            addUser.setString(1, usernameLC);
            addUser.setString(2, hashedPassword);
            addUser.setString(3, salt);
            addUser.setString(5, String.valueOf(access));
            addUser.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        System.out.printf("%s added\n", username);
    }

    /**
     * Method used to add a user to the database but using POLYMORPHISM (method overloading)
     * Like the method above, this method explicitly defines a user's unit when adding to the database
     * @param username user's unique username
     * @param password password the user will use to login
     * @param access admin access for the user
     */
    @Override
    public void addUser(String username, String password, String unit, boolean access) throws IllegalString, AlreadyExists {
        try {
            String usernameLC;

            // Check the username is valid
            if (username.matches("[a-zA-Z]+") && !(username.contains(" "))) {
                usernameLC = username.toLowerCase();
            } else {
                throw new IllegalString("Username '%s' must be letters only. Please try again.", username);
            }

            // Check if the username is taken
            Set<String> names = new TreeSet<>();
            ResultSet rs = getAllUsernames.executeQuery();
            while (rs.next()) {
                names.add(rs.getString("username"));
            }

            // For best practice, all usernames entering the database are converted to lowercase
            if (names.contains(usernameLC)) {
                throw new AlreadyExists("Username '%s' is already taken.", username);
            }

            // Generate SALT & password which also checks the given password is valid
            String salt = HashPassword.generateSALT(username);
            String hashedPassword = HashPassword.hashPassword(password, salt);

            // Setup generic SQL statement to insert the user row
            addUser.setString(1, usernameLC);
            addUser.setString(2, hashedPassword);
            addUser.setString(3, salt);
            addUser.setString(4, unit);
            addUser.setString(5, String.valueOf(access));
            addUser.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        System.out.printf("%s added\n", username);
    }

    /**
     * Method to remove a user's data from the database
     * @param username user to delete
     */
    @Override
    public void removeUser(String username) {
        try {
            removeUser.setString(1, username.toLowerCase());
            removeUser.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.printf("%s deleted\n", username);
    }

    /**
     * Method to get a list of users currently in the database
     * @return usernames
     */
    @Override
    public ArrayList<User> getAllUsers() {
        ArrayList<User> list = new ArrayList<>(); // list of all user instances
        ResultSet rs;
        try {
            // Retrieve all user rows & create User instances before adding to the list
            rs = getAllUsers.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String salt = rs.getString("salt");
                String unit = rs.getString("unit");
                boolean access = Boolean.parseBoolean(rs.getString("access"));
                User u = new User(username, password, salt, getUnit(unit), access);
                list.add(u);
            }
        } catch (SQLException | IllegalString ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return list; // return the list of all user instances
    }

    /**
     * Method used by any logged in user to change their own password in the database.
     * The same method will be used for admins to change any user's password in the database.
     * @param username username of the user's password to change
     * @param password password we want to change it to
     */
    @Override
    public void changePassword(String username, String password) throws DoesNotExist, IllegalString {
        try {
            // For best practice, all usernames entering the database are converted to lowercase
            User u = getUser(username.toLowerCase());

            // Hash the new password with the corresponding SALT
            String hashedPassword = HashPassword.hashPassword(password, u.getSalt());

            // Update user's records (user's row)
            updateUserPassword.setString(1,hashedPassword);
            updateUserPassword.setString(2,username.toLowerCase());
            updateUserPassword.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Method used by admins to update a user's unit in the database.
     * @param username username of the user's unit to change
     * @param unit unit we want to change it to
     */
    @Override
    public void updateUsersUnit(String username, String unit) throws DoesNotExist {
        try {
            // check if unit exists in the database unit table first
            Unit u = getUnit(unit.toLowerCase());
            if (u == null) {
                throw new DoesNotExist("Unit %s does not exist", unit);
            }

            // Update user's records (user's row)
            updateUserUnit.setString(1,u.getName().toLowerCase());
            updateUserUnit.setString(2,username.toLowerCase());
            updateUserUnit.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Method used by admins to remove a user's from their unit in the database.
     * @param username username of the user's unit to change
     */
    @Override
    public void removeUserUnit(String username) {
        try {
            // Update user's records (user's row)
            removeUserUnit.setString(1,username.toLowerCase());
            removeUserUnit.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Method used by admins to update a user's access in the database.
     * @param username username of the user's access to change
     * @param access access we want to change it to
     */
    @Override
    public void updateUsersAccess(String username, boolean access) {
        try {
            // Update user's records (user's row)
            updateUserAccess.setString(1, String.valueOf(access));
            updateUserAccess.setString(2,username.toLowerCase());
            updateUserAccess.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Order methods ---------------------------------------------------------------------------------------------------

    /**
     * Method used to add an order to the database.
     * @param order The order that is being placed
     * @throws OrderException
     * @throws DoesNotExist
     */
    @Override
    public void addOrder(Order order) throws OrderException, DoesNotExist {
        ArrayList<Asset> availableAssets = this.getAllAssets();
        ArrayList <Integer> availableID = new ArrayList<>();

        for (Asset asset: availableAssets) {
            availableID.add(asset.getId());
        }

        HashMap<Asset, Integer> unitAssets = order.unit.getAssets();
        HashMap<Integer, Integer> unitAssetIDs = new HashMap<>();

        for (Asset asset : unitAssets.keySet()) {
            unitAssetIDs.put(asset.getId(), unitAssets.get(asset));
        }

        String placedOn = order.datePlaced.format(formatter);

        int totalPrice = order.qty * order.price;
        int funds = order.unit.getCredits();

        //System.out.println("Asset is in database: " + availableID.contains(order.asset.getId()));
        //System.out.println("Asset in unit inventory: " + unitAssetIDs.containsKey(order.asset.getId()));


        if (order.isBuy) {
            if (availableID.contains(order.asset.getId())) {
                if (totalPrice <= funds) {
                    // Add buy order to the order queue to be reconciled
                    try {
                        System.out.println("Adding order to Database!");
                        //addOrder.setInt(1, order.id);
                        addOrder.setString(2, String.valueOf(order.isBuy));
                        addOrder.setString(3, order.unit.getName());
                        addOrder.setString(4, order.asset.getIdString());
                        addOrder.setInt(5, order.qty);
                        addOrder.setInt(6, order.price);
                        addOrder.setString(7, placedOn);
                        addOrder.executeUpdate();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    System.out.println("Buy too expensive");
                    throw new OrderException("Order price exceeds the total funds held by the users organisation!");
                }
            } else {
                System.out.println("Asset not in database");
                throw new DoesNotExist("Asset has not yet been added to the database!");
            }
        } else {
            if (unitAssetIDs.containsKey(order.asset.getId())) {
                if (unitAssetIDs.get(order.asset.getId()) >= order.qty) {
                    // Add sell order to the order queue to be reconciled
                    try {
                        //addOrder.setInt(1, order.id);
                        addOrder.setString(2, String.valueOf(order.isBuy));
                        addOrder.setString(3, order.unit.getName());
                        addOrder.setString(4, order.asset.getIdString());
                        addOrder.setInt(5, order.qty);
                        addOrder.setInt(6, order.price);
                        addOrder.setString(7, placedOn);
                        addOrder.executeUpdate();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    throw new OrderException("Order quantity exceeds the amount held by the users organisation!");
                }
            } else {
                throw new OrderException("Asset not in Organisations Stock!");
            }
        }
    }

    /**
     * Method used to get a list of all outstanding orders in the order table of the database.
     * @return A HashMap in the form of (OrderID, Order) where Order is the order object for the OrderID entry in the order table of the database
     */
    @Override
    public HashMap<Integer, Order> getOrders() {
        HashMap<Integer, Order> outstanding= new HashMap<>();

        ResultSet rs;

        try {
            rs = getOrders.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt(1);
                boolean isBuy = Boolean.parseBoolean(rs.getString(2));
                String unitID = rs.getString(3);
                Unit unit = this.getUnit(unitID);
                int assetID = rs.getInt(4);
                Asset asset = this.getAsset(assetID);
                int qty = rs.getInt(5);
                int price = rs.getInt(6);

                Order o = new Order(unit, asset, qty, price, isBuy, orderID);

                outstanding.put(orderID, o);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return outstanding;
    }

    /**
     * Method used to return a list of outstanding orders for a specified unit.
     * @param unit The unit that's orders are being collected
     * @return A list of outstanding orders
     */
    @Override
    public ArrayList<Order> getUnitOrders(Unit unit) {
        ArrayList<Order> orders = new ArrayList<>();
        Order o;
        ResultSet rs;
        try {
            getUnitOrders.setString(1, unit.getName());
            rs = getUnitOrders.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("orderID");
                Asset a = getAsset(rs.getInt("assetID"));
                boolean isBuy = rs.getString("type").equalsIgnoreCase("true");
                int qty = rs.getInt("qty");
                int price = rs.getInt("price");
                LocalDateTime date = LocalDateTime.parse(rs.getString("date"), formatter);
                o = new Order(unit, a, qty, price, isBuy, orderID);
                o.setDateResolved(date);
                orders.add(o);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return orders;
    };

    /**
     * Method used to get a list of outstanding orders for a specified asset
     * @param asset The asset that's orders are being collected
     * @return A list of outstanding orders
     */
    @Override
    public ArrayList<Order> getAssetOrders(Asset asset) {
        ArrayList<Order> orders = new ArrayList<>();
        Order o;
        ResultSet rs;
        try {
            getAssetOrders.setString(1, asset.getIdString());
            rs = getAssetOrders.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("orderID");
                Unit unit = getUnit(rs.getString("orgunit"));
                boolean isBuy = rs.getString("type").equalsIgnoreCase("true");
                int qty = rs.getInt("qty");
                int price = rs.getInt("price");
                LocalDateTime date = LocalDateTime.parse(rs.getString("date"), formatter);
                o = new Order(unit, asset, qty, price, isBuy, orderID);
                o.setDateResolved(date);
                orders.add(o);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return orders;
    }

    /**
     * Method used to move an order from the order table of the database to the order history table of the database
     * @param order The order that is being reconciled
     */
    @Override
    public void reconcileOrder(Order order) {
        try {
            addOrderHistory.setInt(1, order.id);
            addOrderHistory.setString(2, String.valueOf(order.isBuy));
            addOrderHistory.setString(3, order.unit.getName());
            addOrderHistory.setInt(4, order.asset.getId());
            addOrderHistory.setInt(5, order.qty);
            addOrderHistory.setInt(6, order.price);
            addOrderHistory.setString(7, order.dateResolved.format(formatter));
            addOrderHistory.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        cancelOrder(order); // Remove the reconciled order from the outstanding orders table
    }

    /**
     * Method used to cancel/remove an order from the order table of the database.
     * @param order The order that is being cancelled
     */
    @Override
    public void cancelOrder(Order order) {
        try {
            removeOrder.setInt(1, order.id);
            removeOrder.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.printf("Order %s cancelled\n", order.id);
    }

    /**
     * Method used to get the outstanding orders for a specified unit.
     * @param unit The unit to get the outstanding orders for
     * @return
     */
    @Override
    public ArrayList<Order> getUnitOrderHistory(Unit unit) {
        ArrayList<Order> orders = new ArrayList<>();
        Order o;
        ResultSet rs;
        try {
            getUnitOrderHistory.setString(1, unit.getName());
            rs = getUnitOrderHistory.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("orderID");
                Asset a = getAsset(rs.getInt("assetID"));
                boolean isBuy = rs.getString("type").equalsIgnoreCase("buy");
                int qty = rs.getInt("qty");
                int price = rs.getInt("price");
                LocalDateTime date = LocalDateTime.parse(rs.getString("date"), formatter);
                o = new Order(unit, a, qty, price, isBuy, orderID);
                o.setDateResolved(date);
                orders.add(o);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return orders;
    };

    // Unit methods ----------------------------------------------------------------------------------------------------

    /**
     * Gets the unit from the database
     * @param unitName unit to be retrieved
     * @return returns an object of the Unit class
     */
    @Override
    public Unit getUnit(String unitName) {
        // Initialise a Unit object
        Unit u = null;

        ResultSet rs;
        // Try to get the unit name and credits from the database
        try {
            getUnit.setString(1, unitName); //replaces '?' with the field we want
            rs = getUnit.executeQuery();

            // Gather the data required to construct a Unit
            String name = rs.getString("unitname");
            int credits = rs.getInt("credits");
            HashMap<Asset, Integer> assets = new HashMap<>();

            while (rs.next()) {
                // Get the units list of assets
                if (!(rs.getString("assetID") == null)) {
                    assets.put(
                            getAsset(Integer.parseInt(rs.getString("assetID"))),
                            rs.getInt("assetqty"));
                }
            }

            // Construct the unit object
            u = new Unit(name, credits, assets);

        } catch (SQLException | IllegalString | InvalidAmount ex) {
            System.out.println("unit is null");
            //ex.printStackTrace();
        }

        // we allow the unit to be returned as null in cases where a user needs to be assigned a null unit
        return u;
    }

    /**
     * Adds a unit to the database
     * @param orgName The unit that is being added to the database
     * @param balance The initial balance of the unit
     * @throws IllegalString if unit name contains numbers, special character or spaces
     * @throws AlreadyExists if the unit name is already in the database
     */
    @Override
    public void addUnit(String orgName, int balance) throws IllegalString, AlreadyExists {
        try {
            String orgNameLC;

            // Check the org name is valid
            if (orgName.matches("[a-zA-Z]+") && !(orgName.contains(" "))) {
                orgNameLC = orgName.toLowerCase();
            } else {
                throw new IllegalString("Unit name '%s' must be letters only. Please try again.", orgName);
            }

            // Check if the unit name is taken
            Set<String> names = new TreeSet<>();
            ResultSet rs = getAllUnitNames.executeQuery();
            while (rs.next()) {
                names.add(rs.getString("unitname"));
            }

            if (names.contains(orgNameLC)) {
                throw new AlreadyExists("Unit name '%s' is already taken.", orgName);
            }

            // Add unit to the database
            addUnit.setString(1, orgNameLC);
            addUnit.setString(2, String.valueOf(balance));
            addUnit.executeUpdate();
        } catch (SQLException ex) {
            System.out.printf("could not add unit");
            //ex.printStackTrace();
        }
    }

    /**
     * Removes a unit from the database
     * @param unitname the Unit that is being removed
     */
    @Override
    public void removeUnit(String unitname) {
        // Try to remove the unit from the database
        try {
            removeUnit.setString(1, unitname);
            removeUnit.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        // If successful, print message
        System.out.printf("%s deleted\n", unitname);
    }

    /**
     * Changes the amount of credits held by a unit in the database
     * @param unitname the unit to have their credits changed
     * @param amount the new amount of credits
     */
    @Override
    public void adjustBalance(String unitname, int amount) {
        // Try to update the credits in the database
        try {
            adjustBalance.setString(1, String.valueOf(amount));
            adjustBalance.setString(2, unitname.toLowerCase());
            adjustBalance.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

    }

    /**
     * Changes the quantity of an asset held by a unit
     * @param unitName the unit to have their asset quantity changed
     * @param assetID the asset in question
     * @param qty the new quantity
     */
    @Override
    public void adjustAssetQuantity(String unitName, int assetID, int qty) {
        // Try find the units current assets
        try {
            unitName = unitName.toLowerCase();
            HashMap<Asset, Integer> unitAssets = getUnitsAssets(getUnit(unitName));
            ArrayList<Integer> unitIDs = new ArrayList<>();
            // Add each asset to ArrayList
            for (Asset asset : unitAssets.keySet()) {
                unitIDs.add(asset.getId());
            }
            // Check if the unit has the asset in question
            boolean unitHasAsset = unitIDs.contains(getAsset(assetID).getId());
            // If the unit does not hold the asset, add it to the unit
            if (!unitHasAsset) {
                addAssetToUnit(unitName, 0, assetID, qty);
            } else {
                // Update unit's records
                adjustQuantity.setInt(1, qty);
                adjustQuantity.setString(2, unitName.toLowerCase());
                adjustQuantity.setString(3, String.valueOf(assetID));
                adjustQuantity.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } catch (DoesNotExist doesNotExist) {
            doesNotExist.printStackTrace();
        } catch (AlreadyExists alreadyExists) {
            alreadyExists.printStackTrace();
        }

    }

    /**
     * Add an existing asset from the database to a unit
     * @param unitname the unit to add an asset to
     * @param balance the units new credit balance
     * @param assetID the asset which will be added to the unit
     * @param assetqty the quantity of the asset
     * @throws AlreadyExists if the asset is already held by the unit
     * @throws DoesNotExist if the asset does not exist
     */
    @Override
    public void addAssetToUnit(String unitname, int balance, int assetID, int assetqty) throws AlreadyExists, DoesNotExist {
        try {
            // Check if the asset exists first
            Set<String> ids = new TreeSet<>();
            ResultSet rs = getAllAssetID.executeQuery();
            while (rs.next()) {
                ids.add(rs.getString("assetID"));
            }

            if (!ids.contains(String.valueOf(assetID))) {
                throw new DoesNotExist("Asset '%s' does not exist.", String.valueOf(assetID));
            }

            // Check if the unit already has the asset
            Unit u = getUnit(unitname.toLowerCase());
            HashMap<Asset, Integer> unitAssetsList = getUnitsAssets(u);

            for (Asset a: unitAssetsList.keySet()) {
                if (a.getId() == assetID) {
                    throw new AlreadyExists("Unit '%s' already has the asset '%s'.", unitname, String.valueOf(assetID));
                }
            }


            // New record for an existing unit but with the new asset
            addAssetToUnit.setString(1, unitname.toLowerCase());
            addAssetToUnit.setString(2, String.valueOf(balance));
            addAssetToUnit.setString(3, String.valueOf(assetID));
            addAssetToUnit.setString(4, String.valueOf(assetqty));
            addAssetToUnit.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

    }

    /**
     * Remove a currently held asset from a unit
     * @param unitname the unit to remove the asset from
     * @param assetID the asset to be removed
     */
    @Override
    public void removeAssetFromUnit(String unitname, int assetID) {
        try {
            // Remove all records of unit's with this asset
            removeAssetFromUnit.setString(1, unitname.toLowerCase());
            removeAssetFromUnit.setString(2, String.valueOf(assetID));
            removeAssetFromUnit.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

    }

    /**
     * Method to get a list of all units currently in the database
     * @return ArrayList of Unit objects
     */
    @Override
    public ArrayList<Unit> getAllUnits() {
        ArrayList<Unit> list = new ArrayList<>();
        ResultSet rs;

        // Try to add each unit from the database into an ArrayList
        try {
            rs = getAllUnits.executeQuery();
            while (rs.next()) {

                String name = rs.getString("unitname");
                int credits = rs.getInt("credits");
                String assetID = rs.getString("assetID");
                HashMap<Asset, Integer> assets = new HashMap<>();
                if (!(assetID == null))
                    assets.put(getAsset(Integer.parseInt(assetID)), rs.getInt("assetqty"));

                list.add(new Unit(name, credits, assets));
            }
        } catch (SQLException | IllegalString | InvalidAmount ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * Get the assets currently held by a unit
     * @param unit The unit to retrieve a list of assets for
     * @return returns a HashMap containing Asset object keys with their quantities as values
     */
    @Override
    public HashMap<Asset, Integer> getUnitsAssets(Unit unit) {
        HashMap<Asset, Integer> list = new HashMap<>();
        ResultSet rs;
        try {
            getUnit.setString(1, unit.getName()); //replaces '?' with the field we want
            rs = getUnit.executeQuery();
            // While there are still rows
            while (rs.next()) {
                // skips blank unit rows (from creating a unit with no asset)
                String assetIDString = rs.getString("assetID");
                if (!(assetIDString == null)) {
                    // Add asset and quantity to HashMap
                    int assetID = Integer.parseInt(assetIDString);
                    int qty = Integer.parseInt(rs.getString("assetqty"));
                    String description = getAsset(assetID).getDescription();
                    Asset a = new Asset(assetID, description);
                    list.put(a, qty);
                }
            }


        } catch (SQLException ex) {
            System.out.println("no asset for this unit");
            //ex.printStackTrace();
        }
        return list;
    }

    // Asset methods ---------------------------------------------------------------------------------------------------

    /**
     * Gets an asset from the database
     * @param assetID ID of the asset to retrieve from the database.
     * @return returns an object of the Asset class
     */
    @Override
    public Asset getAsset(int assetID) {
        // Initialise Asset object
        Asset a = null;
        ResultSet rs;
        // Try to create the object with data from the database
        try {
            getAsset.setInt(1, assetID); //replaces '?' with the field we want
            rs = getAsset.executeQuery();
            rs.next();
            int assetIDString = rs.getInt("assetID");
            String description = rs.getString("desc");
            a = new Asset(assetIDString, description);
        } catch (SQLException ex) {
            System.out.println("no asset found");
            //ex.printStackTrace();
        }
        return a;
    }

    /**
     * Gets a list of all of the assets currently in the database
     * @return returns an ArrayList containing all Asset objects
     */
    @Override
    public ArrayList<Asset> getAllAssets() {
        ArrayList<Asset> list = new ArrayList<>();
        ResultSet rs;

        // Try to construct an Asset object for every asset in the database and add it to the ArrayList
        try {
            rs = getAllAssets.executeQuery();
            while (rs.next()) {
                int assetID = rs.getInt("assetID");
                String description = rs.getString("desc");
                Asset a = new Asset(assetID, description);
                list.add(a);
            }
        } catch (SQLException ex) {
            System.out.println("it did not work");
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * Adds a new asset to the database
     * @param id the new asset's ID
     * @param desc the new asset's description
     * @throws AlreadyExists if the asset ID or description already exist in the database
     */
    @Override
    public void addAsset(String id, String desc) throws AlreadyExists {
        try {
            // Get all assets and check if the asset ID and desc has already been taken
            ArrayList<Asset> list = getAllAssets();
            for (Asset a : list) {
                if (String.valueOf(a.getId()).equalsIgnoreCase(id) | a.getDescription().equalsIgnoreCase(desc)) {
                    throw new AlreadyExists("The asset ID '%s' or description has already been taken in the database.", id);
                }
            }

            // If the above exception is never caught, we can now add the asset
            addAsset.setString(1, id);
            addAsset.setString(2, desc);
            addAsset.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes an asset from the database
     * @param id the asset ID to be removed
     */
    @Override
    public void removeAsset(String id) {
        try {
            // Check if the asset ID exists first, execute then break when found
            ArrayList<Asset> list = getAllAssets();
            for (Asset a : list) {
                if (String.valueOf(a.getId()).equalsIgnoreCase(id)) {
                    // If the above exception is never caught, we can now add the asset
                    removeAsset.setString(1, id);
                    removeAsset.executeUpdate();

                    // remove the asset from all unit's too
                    removeAssetFromAllUnits.setString(1, id);
                    removeAssetFromAllUnits.executeUpdate();
                    break;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Updates an existing assets description
     * @param id the asset ID to update
     * @param desc the new description
     * @throws AlreadyExists if the new description is the same as the original
     */
    @Override
    public void updateAssetDesc(String id, String desc) throws AlreadyExists {
        try {
            // Get all assets and check if the asset desc has already been taken
            ArrayList<Asset> list = getAllAssets();
            for (Asset a : list) {
                if (a.getDescription().equalsIgnoreCase(desc)) {
                    throw new AlreadyExists("Asset '%s' description is already taken in the database.", desc);
                }
            }

            // If the above exception is never caught, we can now update the asset
            // still check if it exists for safety to avoid an SQL error in all cases
            // break immediately when done
            for (Asset a : list) {
                if (String.valueOf(a.getId()).equalsIgnoreCase(id)) {
                    // If the above exception is never caught, we can now update the asset
                    updateAsset.setString(1, desc);
                    updateAsset.setString(2, id);
                    updateAsset.executeUpdate();
                    break;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

}
