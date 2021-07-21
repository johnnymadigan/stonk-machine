package common;

import ClientSide.Asset;
import ClientSide.Exceptions.AlreadyExists;
import ClientSide.Exceptions.DoesNotExist;
import ClientSide.Exceptions.IllegalString;
import ClientSide.Exceptions.OrderException;
import ClientSide.Order;
import ClientSide.Unit;
import ClientSide.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The interface class used to interact with the database specified by the client.
 * @author Alistair Ridge, Johnny Madigan and Scott Peachey
 */
public interface DatabaseSource {

    // USER METHODS ----------------------------------------------------------------------------------------------------

    /**
     * Method used when a user attempts to log in. Uses the username
     * to get the user from the database, then hash the input password
     * with that user's salt to see if there's a match.
     * @param username username input
     * @param password password input
     * @return the user object for the log-in session
     * @throws DoesNotExist Throw an exception if the user does not exist
     * @throws IllegalString Throw an exception if the password is not a valid string
     */
    User login(String username, String password) throws DoesNotExist, IllegalString;

    /**
     * Method used to get the data for a specified user from the database
     * @param name username of user to get
     * @return a User object that holds all the users data
     * @throws DoesNotExist Throw an exception if the user does not exist
     */
    User getUser(String name) throws DoesNotExist;

    /**
     * Method used to add a user to the User table in the database
     * As methods should be single-purposed, the user will be added to a unit in the usersUnit() method
     * @param username user's unique username
     * @param password password the user will use to login
     * @param access admin access for the user
     * @throws IllegalString Throw an exception if the password is not a valid string
     * @throws AlreadyExists Throw an exception if the user already exists
     */
    void addUser(String username, String password, boolean access) throws IllegalString, AlreadyExists;

    /**
     * Method used to add a user to the database but using POLYMORPHISM (method overloading)
     * Like the method above, this method explicitly defines a user's unit when adding to the database
     * @param username User's unique username
     * @param password Password the user will use to login
     * @param unit The unit that the user belongs to
     * @param access Admin access for the user
     * @throws IllegalString Throw an exception if the password is not a valid string
     * @throws AlreadyExists Throw an exception if the user already exists
     */
    void addUser(String username, String password, String unit, boolean access) throws IllegalString, AlreadyExists;

    /**
     * Method to remove a user's data from the database
     * @param username user to delete
     */
    void removeUser(String username);

    /**
     * Method to get a list of users currently in the database
     * @return usernames
     */
    ArrayList<User> getAllUsers();

    /**
     * Method used by any logged in user to change their own password in the database.
     * The same method will be used for admins to change any user's password in the database.
     * @param username username of the user's password to change
     * @param password password we want to change it to
     * @throws DoesNotExist Throw an exception if the user does not exist
     * @throws IllegalString Throw an exception if the password is not a valid string
     */
    void changePassword(String username, String password) throws DoesNotExist, IllegalString;

    /**
     * * Method used by admins to update a user's unit in the database.
     * @param username username of the user's unit to change
     * @param unit unit we want to change it to
     * @throws DoesNotExist Throw exception if the user does not exist
     */
    void updateUsersUnit(String username, String unit) throws DoesNotExist;

    /**
     * Method used by admins to remove a user's from their unit in the database.
     * @param username username of the user's unit to change
     */
    void removeUserUnit(String username);

    /**
     * Method used by admins to update a user's access in the database.
     * @param username username of the user's access to change
     * @param access access we want to change it to
     */
    void updateUsersAccess(String username, boolean access);

    // ORDER METHODS ---------------------------------------------------------------------------------------------------

    /**
     * Method used to add an order to the orders table in the database
     * @param order The order that is being placed
     * @throws OrderException Throw exception if there is an issue with the order
     * @throws DoesNotExist Throw exception if the order details (e.g. unit or asset) do not exist
     */
    void addOrder(Order order) throws OrderException, DoesNotExist;

    /**
     * Method used to get outstanding orders from the orders Database
     * @return A list of outstanding orders
     */
    HashMap<Integer, Order> getOrders();

    /**
     * Method used to get a units outstanding orders from the orders Database
     * @return Array list containing the unit's outstanding orders
     * @param unit The unit that's orders are being collected
     */
    ArrayList<Order> getUnitOrders(Unit unit);

    /**
     * Method used to get outstanding orders of a certain asset from the orders Database
     * @return Array list containing the outstanding orders involving a given asset
     * @param asset The asset that's orders are being collected
     */
    ArrayList<Order> getAssetOrders(Asset asset);

    /**
     * Method used to move an order to the order history table in the database and remove it from the order table
     * @param order The order that is being reconciled
     */
    void reconcileOrder(Order order);

    /**
     * Method used to cancel/remove a specified order in the orders table in the database
     * @param order The order that is being cancelled
     */
    void cancelOrder(Order order);

    /**
     * Method used to get a list of outstanding orders for a specified unit
     * @param unit Unit to get a list of outstanding orders for
     * @return A list of outstanding orders
     */
    ArrayList<Order> getUnitOrderHistory(Unit unit);

    /**
     * Method used to get the historical price for a specified asset.
     * @param assetID The asset to get price history of
     * @return most recent 10 prices (sell orders)
     */
    ArrayList<Integer> getAssetOrderHistory(String assetID);

    // UNIT METHODS ----------------------------------------------------------------------------------------------------

    /**
     * Method used to get the data for an Organisation from the database
     * @param unitID The id for the Organisation
     * @return an OrgUnit object that holds all the organisations data.
     * @throws DoesNotExist Throw exception if the unit does not exist
     */
    Unit getUnit(String unitID) throws DoesNotExist;

    /**
     * * Method used to add an Organisation to the database
     * @param orgName The unit that is being added to the database
     * @param balance The initial balance of the unit
     * @throws IllegalString Throw exception if the unit name is not a valid string
     * @throws AlreadyExists Throw exception if unit already exists
     */
    void addUnit(String orgName, int balance) throws IllegalString, AlreadyExists;

    /**
     * Method used to remove an Organisation from the database
     * @param unitname the Unit that is being removed
     */
    void removeUnit(String unitname);

    /**
     * Method used to update the local instance of the OrgUnit object. This is used to update the organisations list of
     * assets, asset qty, users, and the amount of credits the organisation holds.
     * @param unitname Name of the unit that's balance is being adjusted
     * @param amount Amount to adjust the units balance by
     */
    void adjustBalance(String unitname, int amount);

    /**
     * Changes the quantity of an asset held by a unit
     * @param unitName the unit to have their asset quantity changed
     * @param assetID the asset in question
     * @param qty the new quantity
     */
    void adjustAssetQuantity(String unitName, int assetID, int qty);

    /**
     * Add an existing asset from the database to a unit
     * @param unitname the unit to add an asset to
     * @param balance the units new credit balance
     * @param assetID the asset which will be added to the unit
     * @param assetqty the quantity of the asset
     * @throws AlreadyExists if the asset is already held by the unit
     * @throws DoesNotExist if the asset does not exist
     */
    void addAssetToUnit(String unitname, int balance, int assetID, int assetqty) throws AlreadyExists, DoesNotExist;

    /**
     * Remove a currently held asset from a unit
     * @param unitname the unit to remove the asset from
     * @param assetID the asset to be removed
     */
    void removeAssetFromUnit(String unitname, int assetID);

    /**
     * Method to get a list of all units currently in the database
     * @return ArrayList of Unit objects
     */
    ArrayList<Unit> getAllUnits();

    /**
     * Method used to get a list of all assets held by an Organisation
     * @param unit The organisation to retrieve a list of assets for
     * @return A TreeMap of asset objects with their quantity values
     */
    HashMap<Asset, Integer> getUnitsAssets(Unit unit);

    // ASSET METHODS ---------------------------------------------------------------------------------------------------
    /**
     * Method used to get a specified asset from the database
     * @param assetID ID of the asset to retrieve from the database.
     * @return An Asset object containing all the relevant historical data
     */
    Asset getAsset(int assetID);

    /**
     * Method used to get a list of all asset types held in the database
     * @return An array list of all assets held in the Database
     */
    ArrayList<Asset> getAllAssets();

    /**
     * Method used to add an asset to the database
     * @param id ID of the asset that is being added
     * @param desc Description of the asset that is being added
     * @throws AlreadyExists Exception thrown of the asset already exists in the database
     */
    void addAsset(String id, String desc) throws AlreadyExists;

    /**
     * Method used to remove an asset from the database
     * @param id ID of the asset to remove from the database
     */
    void removeAsset(String id);

    /**
     * Updates an existing assets description
     * @param id the asset ID to update
     * @param desc the new description
     * @throws AlreadyExists if the new description is the same as the original
     */
    void updateAssetDesc(String id, String desc) throws AlreadyExists;

}
