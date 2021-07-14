package ServerSideTests;

import ClientSide.Asset;
import ClientSide.Exceptions.AlreadyExists;
import ClientSide.Exceptions.DoesNotExist;
import ClientSide.Exceptions.IllegalString;
import ClientSide.Order;
import ClientSide.Unit;
import ClientSide.User;
import ServerSide.MockObjects;
import ServerSide.NetworkConnection;
import ServerSide.Server;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The following tests are used to test the functionality and reliability of the Network Connection class.
 * Uses Mock Objects to simulate a fake database so these tests can manipulate fake data instead of the
 * real database. Please ensure you have changed the schema in ServerSettings.props to use "MockStonkMachine".
 * @author Johnny Madigan, Scott Peachey, Alistair Ridge
 */
public class TestNetworkConnection {

    NetworkConnection data = new NetworkConnection();
    MockObjects mock = new MockObjects(data);



    // BEFORE EACH TEST-------------------------------------------------------------------------------------------------

    /**
     * Reset the mock objects for testing
     */
    @BeforeEach
    @DisplayName("Before each...")
    public void before() throws IOException {
        mock.killMockDatabase();
        mock.createMockDatabase();
    }

    @Nested
    @DisplayName("User interface methods tests")
    class userInterfaceMethodsTests {

        /**
         * Testing a user is added to the database correctly.
         *
         * @throws DoesNotExist if the queried user does not exist
         */
        @Test
        @DisplayName("Users are added to the database correctly")
        public void testAddUserToDatabase() throws DoesNotExist {
            // retrieve the mock admin user via the network connection interface methods
            User mockAdmin = data.getUser("johnny");

            // check that the user object retrieved has the right data we specified when added
            assertAll(
                    // the username is assigned correctly
                    () -> assertEquals("johnny", mockAdmin.getUsername()),
                    // the admin access is true
                    () -> assertTrue(mockAdmin.getAccess()),
                    // the salt string is made unique by concatenating with the username
                    () -> assertTrue(mockAdmin.getSalt().contains("johnny")),
                    // the salt string is generated as alphanumeric
                    () -> assertTrue(mockAdmin.getSalt().matches("[A-Za-z0-9]+")),
                    // the password has been hashed (SHA-512 gives a hashed string 128 chars in length)
                    () -> assertEquals(128, mockAdmin.getPassword().length()),
                    // the unit is null by default as no unit has been set
                    () -> assertNull(mockAdmin.getUnit()));
        }

        /**
         * Testing a user is NOT added due to an invalid username.
         */
        @Test
        @DisplayName("Exception is thrown when trying to add a user with an invalid username")
        public void testAddUsersError() {
            assertAll(() -> assertThrows(IllegalString.class,
                    () -> data.addUser("1nval1d", "$adboy", true)),
                    () -> assertThrows(IllegalString.class,
                            () -> data.addUser("i n v a l i d", "$adboy", true)));
        }

        /**
         * Testing a user is NOT added because the username is already taken.
         */
        @Test
        @DisplayName("Exception is thrown when trying to add a user but the username is taken")
        public void testUsernameTaken() {
            // Attempt to add another admin user with the same name as a mock user
            assertThrows(AlreadyExists.class, () -> data.addUser("johnny", "$adboy", true));
        }

        /**
         * Testing attempting to get a user but the user does not exist.
         */
        @Test
        @DisplayName("Exception is thrown when trying to return a user that does not exist")
        public void testGetUserError() {
            assertThrows(DoesNotExist.class, () -> data.getUser("idontexist"));
        }

        /**
         * Testing login successful with correct credentials.
         */
        @Test
        @DisplayName("Login successful when the logged-in user object is returned ")
        public void testLoginSuccess() throws DoesNotExist, IllegalString {
            assertEquals("johnny", data.login("johnny", "bo$$man").getUsername());
        }

        /**
         * Testing attempting to login but the password is invalid.
         */
        @Test
        @DisplayName("Exception is thrown when trying to login with an invalid password")
        public void testLoginPasswordError() {
            assertThrows(IllegalString.class, () -> data.login("johnny", "sweetChips"));
        }

        /**
         * Testing attempting to login but the username does not exist.
         */
        @Test
        @DisplayName("Exception is thrown when trying to login but the username does not exist")
        public void testLoginUsernameError() {
            assertThrows(DoesNotExist.class, () -> data.login("dummy", "bo$$man"));
        }

        /**
         * Testing to successfully delete a user from the database.
         */
        @Test
        @DisplayName("The user no longer exists in the database")
        public void testRemoveUserSuccessful() {
            // First remove the user then attempt to retrieve the same user...
            // we should get an exception that the user no longer exists
            data.removeUser("johnny");
            assertThrows(DoesNotExist.class, () -> data.getUser("johnny"));
        }

        /**
         * Testing to successfully retrieve all mock users from the database.
         */
        @Test
        @DisplayName("All mock users have been found")
        public void testGetAllUsers() {
            // This mock objects class has 3 mock users
            assertEquals(3, data.getAllUsers().size());
        }

        /**
         * Change the user's password then attempt to login again, this confirms
         * the test passes as the user returned via logging in is the same user
         * but we logged in with the new password instead
         * @throws DoesNotExist if the username does not exist
         * @throws IllegalString if the new password is invalid
         */
        @Test
        @DisplayName("Changed password successfully")
        public void testChangePasswordSuccess() throws DoesNotExist, IllegalString {
            data.changePassword("johnny", "$adboy$");
            assertEquals("johnny", data.login("johnny", "$adboy$").getUsername());
        }

        /**
         * DoesNotExist exception thrown when trying to change a password for a non-existent user
         */
        @Test
        @DisplayName("Correct exception thrown when trying to change a password for a non-existent user")
        public void testChangePasswordUsernameError() {
            assertThrows(DoesNotExist.class, () -> data.changePassword("jimbro", "$adboy$"));

        }

        /**
         * IllegalString exception thrown when the new password is invalid
         */
        @Test
        @DisplayName("Correct exception thrown when new password is invalid")
        public void testChangePasswordError() {
            assertThrows(IllegalString.class, () -> data.changePassword("johnny", "l o l"));
        }

        /**
         * Successful updating the user's unit
         */
        @Test
        @DisplayName("The user's unit has been successfully updated")
        public void updateUsersUnitSuccess() throws DoesNotExist {
            // Update the user's unit
            data.updateUsersUnit("johnny", "engineers");

            // Retrieve the user object
            User mockAdmin = data.getUser("johnny");

            // Check if the user's new unit updated correctly
            assertEquals("engineers", mockAdmin.getUnit().getName());
        }

        /**
         * Successful remove a user from their unit
         */
        @Test
        @DisplayName("The user has been successfully removed from their unit")
        public void removeUsersUnitSuccess() throws DoesNotExist {
            // Update the user's unit to ensure the user is part of a unit
            data.updateUsersUnit("johnny", "engineers");

            // Then remove them from their unit & retrieve the user object
            data.removeUserUnit("johnny");
            User mockAdmin = data.getUser("johnny");

            // Check if the user's unit is now empty (null)
            assertNull(mockAdmin.getUnit());
        }

        /**
         * Successful updating the user's access level
         */
        @Test
        @DisplayName("The user's access level has been successfully updated")
        public void updateUsersAccessSuccess() throws DoesNotExist {
            // Update the user's access level
            data.updateUsersAccess("johnny", false);

            // Retrieve the user object
            User mockAdmin = data.getUser("johnny");

            // Check if the user's new access level updated correctly
            assertFalse(mockAdmin.getAccess());
        }

        /**
         * DoesNotExist exception is thrown when either the username or unit name does not exist
         */
        @Test
        @DisplayName("Correct error when attempting to update a user's unit with a non-existent unit")
        public void updateUsersUnitDoesNotExist() {
            assertThrows(DoesNotExist.class, () -> data.updateUsersUnit("johnny", "yeetusMaximus"));
        }
    }

    @Nested
    @DisplayName("Unit interface methods tests")
    class unitInterfaceMethodsTests {
        /**
         * Testing to successfully retrieve all mock units from the database.
         */
        @Test
        @DisplayName("All mock units have been found")
        public void testGetAllUnits() {
            // As there are multiple rows for each unit (to easily show a unit and each of their assets)
            // we must cull all duplicate unit rows from the array list as we are only testing for
            // retrieving one instance of each mock unit
            ArrayList<Unit> units = data.getAllUnits();
            Collection<Unit> nonDuplicateUnits = units.stream()
                    .collect(Collectors.toMap(Unit::getName, Function.identity(), (a, b) -> a))
                    .values();

            // This mock objects class has 3 mock units
            assertEquals(3, nonDuplicateUnits.size());
        }

        /**
         * Testing to successfully delete a mock unit from the database.
         */
        @Test
        @DisplayName("Mock unit successfully deleted")
        public void testDeleteUnitSuccess() {
            // First remove the unit then attempt to retrieve the same unit...
            // we should get an exception that the user no longer exists
            data.removeUnit("developers");

            // As the client will need to display users to admin, no exception
            // is thrown in-case the user is not part of a unit yet, therefore
            // we will test that the unit returned is null.
            assertNull(data.getUnit("developers"));
        }

        /**
         * Testing to successfully add an asset to a unit.
         */
        @Test
        @DisplayName("Added mock asset to mock unit")
        public void testAddAssetToUnit() throws DoesNotExist, AlreadyExists {
            // Add asset to the unit
            data.addAssetToUnit("developers", 999, 5, 1);

            // Retrieve the unit, unit's assets, and asset to find
            Unit mockUnit = data.getUnit("developers");
            HashMap<Asset, Integer> mockUnitsAssets = data.getUnitsAssets(mockUnit);
            Asset mockAsset = data.getAsset(5);

            // For each of the unit's assets, if the asset matches the asset to find
            // set found flag to true and break immediately
            boolean found = false;
            for (Asset a : mockUnitsAssets.keySet()) {
                if (a.getId() == mockAsset.getId()) {
                    found = true;
                    break;
                }
            }

            // Test will pass if the unit possesses the asset as we gave the unit the asset
            assertTrue(found);
        }

        /**
         * Testing to successfully remove an asset from a unit.
         */
        @Test
        @DisplayName("Removed mock asset from mock unit")
        public void testRemoveAssetFromUnit() {
            // Remove asset from the unit
            data.removeAssetFromUnit("developers", 1);

            // Retrieve the unit, unit's assets, and asset to find
            Unit mockUnit = data.getUnit("developers");
            HashMap<Asset, Integer> mockUnitsAssets = data.getUnitsAssets(mockUnit);
            Asset mockAsset = data.getAsset(1);

            // For each of the unit's assets, if the asset matches the asset to find
            // set found flag to true and break immediately
            boolean found = false;
            for (Asset a : mockUnitsAssets.keySet()) {
                if (a.getId() == mockAsset.getId()) {
                    found = true;
                    break;
                }
            }

            // Test will pass if the unit no longer has possession of the asset as we removed it
            assertFalse(found);
        }

        /**
         * Testing to successfully retrieve all assets belonging to a mock unit.
         */
        @Test
        @DisplayName("All mock units assets have been retrieved")
        public void testGetUnitsAssets() {
            // Retrieve the unit & unit's assets
            Unit mockUnit = data.getUnit("developers");
            HashMap<Asset, Integer> mockUnitsAssets = data.getUnitsAssets(mockUnit);

            // This mock unit has 3 assets associated with it (see mock objects class)
            assertEquals(3, mockUnitsAssets.size());
        }

        /**
         * Testing to successfully set a unit's balance.
         */
        @Test
        @DisplayName("Mock unit's balance can be adjusted in all ways")
        public void testAdjustBalance() {
            // This mock unit starts off with 22 of the asset (see mock objects class)
            data.adjustBalance("developers", 50); // $50

            // Retrieve the unit
            Unit mockUnit = data.getUnit("developers");

            // Test will pass if the unit's balance has been set to $50
            assertEquals(50, mockUnit.getCredits());
        }

        /**
         * Testing to successfully set a unit's asset quantity.
         */
        @Test
        @DisplayName("Mock unit's asset quantity can be adjusted in all ways")
        public void testAdjustUnitsAssetQuantity() {
            // This mock unit starts off with 22 of the asset (see mock objects class)
            data.adjustAssetQuantity("developers", 1, 50);

            // Retrieve the unit, unit's assets, and asset to find
            Unit mockUnit = data.getUnit("developers");
            Asset mockAsset = data.getAsset(1);
            HashMap<Asset, Integer> mockUnitsAssets = data.getUnitsAssets(mockUnit);

            // For each of the unit's assets, if the asset matches the asset to find
            // save the new quantity & break immediately
            int newQty = 0;
            for (Map.Entry<Asset, Integer> a : mockUnitsAssets.entrySet()) {
                if (a.getKey().getId() == mockAsset.getId()) {
                    newQty = a.getValue();
                    break;
                }
            }

            // Test will pass if the unit's asset quantity has been set to 50
            assertEquals(50, newQty);
        }

        /**
         * Testing a unit is added to the database correctly.
         */
        @Test
        @DisplayName("Units are added to the database correctly")
        public void testAddUnitToDatabase() throws AlreadyExists, IllegalString {
            // add a unit via the network connection interface methods
            data.addUnit("testunit", 100);
            // retrieve the unit via the network connection interface methods
            Unit mockUnit = data.getUnit("testunit");

            // check that the asset object retrieved has the right data we specified when added
            assertAll(
                    // the unit name is assigned correctly
                    () -> assertEquals("testunit", mockUnit.getName()),
                    // the unit's credits are assigned correctly
                    () -> assertEquals(100, mockUnit.getCredits())
            );

            data.removeUnit(mockUnit.getName()); // remove
        }

        /**
         * Testing a unit is NOT added due to an invalid unit name.
         */
        @Test
        @DisplayName("Exception is thrown when trying to add a unit with an invalid unit name")
        public void testAddUnitError() {
            assertAll(() -> assertThrows(IllegalString.class,
                    () -> data.addUnit("unit1", 100)),
                    () -> assertThrows(IllegalString.class,
                            () -> data.addUnit("test unit", 100)));
        }

        /**
         * Testing a unit is NOT added because the unit name is already taken.
         */
        @Test
        @DisplayName("Exception is thrown when trying to add a unit but the unit name is taken")
        public void testUnitNameTaken() throws AlreadyExists, IllegalString {
            // add an admin user via the network connection interface methods
            data.addUnit("test", 100);
            // retrieve the admin user via the network connection interface methods
            Unit mockUnit = data.getUnit("test");

            assertThrows(AlreadyExists.class, () -> data.addUnit("test", 100));

            data.removeUser(mockUnit.getName()); // remove
        }
    }

    @Nested
    @DisplayName("Asset interface methods tests")
    class assetInterfaceMethodsTests {

        /**
         * Testing an asset is added to the database correctly.
         */
        @Test
        @DisplayName("Assets are added to the database correctly")
        public void testAddAssetToDatabase() throws AlreadyExists {
            // add an asset via the network connection interface methods
            data.addAsset("123", "Test Asset!");
            // retrieve the asset via the network connection interface methods
            Asset mockAsset = data.getAsset(123);

            // check that the asset object retrieved has the right data we specified when added
            assertAll(
                    // the asset ID is assigned correctly
                    () -> assertEquals(123, mockAsset.getId()),
                    // the asset description is assigned correctly
                    () -> assertEquals("Test Asset!", mockAsset.getDescription())
            );

            data.removeAsset(mockAsset.getIdString()); // remove
        }

        /**
         * Testing an asset is NOT added because the ID or description is already taken.
         */
        @Test
        @DisplayName("Exception is thrown when trying to add an asset but the asset ID or description is taken")
        public void testAssetFieldsTaken() throws AlreadyExists {
            // add an asset via the network connection interface methods
            data.addAsset("123", "Test");
            // retrieve the asset via the network connection interface methods
            Asset mockAsset = data.getAsset(123);

            assertThrows(AlreadyExists.class, () -> data.addAsset("123", "New Test"));

            assertThrows(AlreadyExists.class, () -> data.addAsset("234", "Test"));

            data.removeAsset(mockAsset.getIdString()); // remove
        }

        /**
         * Testing all assets are retrieved correctly from the database.
         */
        @Test
        @DisplayName("All assets are collected from the database correctly")
        public void testGetAllAssets() {
            int mockAssets = 5; // this is equal to the number of assets created added in MockObjects

            assertEquals(mockAssets, data.getAllAssets().size());
        }

        /**
         * Testing an assets description is updated correctly in the database.
         */
        @Test
        @DisplayName("Asset description is updated correctly in the databse")
        public void testUpdateAssetDesc() throws AlreadyExists {
            String newDescription = "Updated description";

            // add an asset via the network connection interface methods
            data.addAsset("123", "Original description");
            // update the assets description via the network connection interface methods
            data.updateAssetDesc("123", newDescription);

            assertEquals(newDescription, data.getAsset(123).getDescription());

            data.removeAsset("123"); // remove
        }
    }

    @Nested
    @DisplayName("Order interface methods tests")
    class orderInterfaceMethodsTests {

        /**
         * Testing a unit's orders are retrieved from the database correctly.
         */
        @Test
        @DisplayName("Unit orders are retrieved from the database correctly")
        public void testGetUnitOrders() {
            int unitOrders = 3;

            assertEquals(unitOrders, data.getUnitOrders(data.getUnit("developers")).size());
        }

        /**
         * Testing an asset's orders are retrieved from the database correctly.
         */
        @Test
        @DisplayName("Asset orders are retrieved from the database correctly")
        public void testGetAssetOrders() {
            int assetOrders = 1;

            assertEquals(assetOrders, data.getAssetOrders(data.getAsset(1)).size());
        }

        /**
         * Testing unit historical orders are retrieved from the database correctly.
         */
        @Test
        @DisplayName("Unit historical orders are retrieved from the database correctly")
        public void testGetUnitOrderHistory() {
            int mockOrders = 3; // this is equal to the number of orders created added in MockObjects
            ArrayList<Order> orders = new ArrayList<>();
            for (Order order : data.getUnitOrders(data.getUnit("developers"))) {
                data.reconcileOrder(order);
            }

            assertEquals(mockOrders, data.getUnitOrderHistory(data.getUnit("developers")).size());
        }
    }

}
