package ServerSide;

import ClientSide.Exceptions.AlreadyExists;
import ClientSide.Exceptions.DoesNotExist;
import ClientSide.Exceptions.IllegalString;
import ClientSide.Exceptions.OrderException;
import ClientSide.Order;

/**
 * Mock database used for testing only
 * @author Johnny Madigan, Scott Peachey, Alistair Ridge
 */
public class MockObjects {

    public NetworkConnection data;

    /**
     * Constructor
     * @param data network interface methods
     */
    public MockObjects(NetworkConnection data) {this.data = data;}

    /**
     * Completely reset the database
     */
    public void killMockDatabase() { data.resetEverything(); }

    /**
     * Create a mock database with hard-coded data
     */
    public void createMockDatabase() {

        // Add users if they do not already exist in the database
        try {
            data.addUser("scott", "scotty", false);
            data.addUser("alistair", "rich", false);
            data.addUser("johnny", "bo$$man", true);
        } catch (IllegalString | AlreadyExists ex) {
            ex.printStackTrace();
        }

        // Add units if they do not already exist in the database
        try {
            data.addUnit("developers", 999);
            data.addUnit("engineers", 500);
            data.addUnit("designers", 7);
        } catch (IllegalString | AlreadyExists ex) {
            ex.printStackTrace();
        }

        // Add users to units
        try {
            data.updateUsersUnit("user", "developers");
            data.updateUsersUnit("scott", "developers");
            data.updateUsersUnit("alistair", "developers");
        } catch (DoesNotExist ex) {
            ex.printStackTrace();
        }

        // Add assets if they do not already exist in the database
        try {
            data.addAsset("1", "CPU hours");
            data.addAsset("2", "Adobe Creative Cloud");
            data.addAsset("3", "HDMI cables");
            data.addAsset("4", "Macbooks");
            data.addAsset("5", "Staff breaks");
        } catch (AlreadyExists ex) {
            ex.printStackTrace();
        }

        // Add assets to a unit
        try {
            data.addAssetToUnit("developers", 999, 1, 22);
            data.addAssetToUnit("developers", 999, 2, 17);
            data.addAssetToUnit("developers", 999, 3, 6);
            data.addAssetToUnit("engineers", 500, 4, 80);
            data.addAssetToUnit("engineers", 500, 2, 66);
            data.addAssetToUnit("designers", 7, 5, 5);
        } catch (DoesNotExist | AlreadyExists ex) {
            ex.printStackTrace();
        }

        // Add orders if they do not already exist in the database
        try {
            data.addOrder(new Order(data.getUnit("developers"),
                    data.getAsset(1), 1, 10, true));
            data.addOrder(new Order(data.getUnit("developers"),
                    data.getAsset(2), 2, 30, false));
            data.addOrder(new Order(data.getUnit("developers"),
                    data.getAsset(3), 3, 20, true));

        } catch (DoesNotExist | OrderException doesNotExist) {
            doesNotExist.printStackTrace();
        }
    }
    }
