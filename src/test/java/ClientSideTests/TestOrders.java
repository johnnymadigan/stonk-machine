package ClientSideTests;

import ClientSide.Asset;
import ClientSide.Exceptions.*;
import ClientSide.Order;
import ClientSide.Unit;
import ClientSide.User;
import ServerSide.NetworkConnection;
import ServerSide.ReconcileTrades;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static ClientSide.HashPassword.generateSALT;
import static ClientSide.HashPassword.hashPassword;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The following tests are used to test the functionality of the Orders class.
 *
 * The key functionality that will be tested is placing a buy and sell order.
 * As part of this this class will test that an exception is thrown in the following scenarios:
 *      - Buy order exceeds organisations total credits minus the cost of unconsolidated buy orders.
 *      - Sell order exceeds the amount of an asset the organisation holds.
 *      - Sell order for an asset the organisation does not hold.
 *      - Asset being traded does not exist in the system.
 *      - Trades are reconciled if there is another trade that satisfies the reconciliation requirements
 * @author Alistair Ridge
 */
public class TestOrders {
    NetworkConnection data = new NetworkConnection();

    Asset ABC;
    Asset XYZ;
    HashMap<Asset, Integer> assets = new HashMap<>();
    Unit SoldBy;
    Unit BoughtBy;
    User JaneDoe;
    User JohnDoe;

    boolean sell = false;
    boolean buy = true;

    Order testOrderSell;
    Order testOrderBuy;

    /**
     * Create the test assets and test units used for the tests.
     * @throws AlreadyExists Throw an exception if the asset or unit already exists
     * @throws IllegalString Throws an exception if the unit name is not a valid string
     * @throws InvalidAmount Throw an exception if the unit balance is not a valid amount
     */
    public TestOrders() throws AlreadyExists, IllegalString, InvalidAmount {
        // Create dummy objects required for testing.
        ABC = new Asset(777, "Test Asset ABC");
        XYZ = new Asset(666, "Test Asset XYZ"); // Used for checking that an exception is thrown when a unit does not have an asset
        assets.put(ABC, 50);

        // Add asset to the database if it doesn't exist
        ArrayList<Asset> assetList = data.getAllAssets();
        ArrayList<String> assetIDList = new ArrayList<>();
        for (Asset asset : assetList) {
            assetIDList.add(asset.getIdString());
        }

        if (!assetIDList.contains(ABC.getIdString())) {
            data.addAsset(ABC.getIdString(), ABC.getDescription());
        }

        SoldBy = new Unit("testunit", 999, assets);
        BoughtBy = new Unit("funkymonkey", 999, assets);

        // Add unit to the database if it doesn't exist
        ArrayList<Unit> units = data.getAllUnits();
        ArrayList<String> unitIDs = new ArrayList<>();
        for (Unit unit : units) {
            unitIDs.add(unit.getName());
        }
        if (unitIDs.contains(SoldBy.getName())) {
            data.removeUnit(SoldBy.getName());
        }
        if (unitIDs.contains(BoughtBy.getName())) {
            data.removeUnit(BoughtBy.getName());
        }
        data.addUnit(BoughtBy.getName(), BoughtBy.getCredits());
        data.addUnit(SoldBy.getName(), SoldBy.getCredits());

        for (Asset asset : BoughtBy.getAssets().keySet()) {
            data.adjustAssetQuantity(BoughtBy.getName(), asset.getId(), BoughtBy.getAssets().get(asset));
        }
        for (Asset asset : SoldBy.getAssets().keySet()) {
            data.adjustAssetQuantity(SoldBy.getName(), asset.getId(), SoldBy.getAssets().get(asset));
        }
    }

    /**
     * Ensure that the orders table does not contain any outstanding orders that may affect the tests.
     * Ensur that users are setup correctly for the tests.
     * @throws AlreadyExists Throw an exception if the user already exists
     * @throws IllegalString Throw an exception if the password is not a valid string
     */
    @BeforeEach
    public void cleanup() throws AlreadyExists, IllegalString {
        HashMap<Integer, Order> outstandingOrders = data.getOrders();
        for (int key : outstandingOrders.keySet()) {
            data.cancelOrder(outstandingOrders.get(key));
        }

        JaneDoe= new User("janedoe", "Password5678", hashPassword("Password5678", generateSALT("JaneDoe")), BoughtBy, false);
        JohnDoe = new User("johndoe", "Password1234", hashPassword("Password1234", generateSALT("JohnDoe")), SoldBy, false);



        // Add the user to the database if it doesn't exist
        ArrayList<User> users = data.getAllUsers();
        ArrayList<String> usernames = new ArrayList<>();
        for (User user : users) {
            usernames.add(user.getUsername());
        }

        if (usernames.contains(JohnDoe.getUsername())) {
            data.removeUser(JohnDoe.getUsername());
        }
        if (usernames.contains(JaneDoe.getUsername())) {
            data.removeUser(JaneDoe.getUsername());
        }

        data.addUser(JaneDoe.getUsername(), JaneDoe.getPassword(), JaneDoe.getUnit().getName(), JaneDoe.getAccess());
        data.addUser(JohnDoe.getUsername(), JohnDoe.getPassword(), JohnDoe.getUnit().getName(), JohnDoe.getAccess());


    }

    /**
     * Test that orders are created correctly
     */
    @Test
    public void TestOrdersSetup() {
        // Create a Sell & Buy order
        testOrderSell = new Order(JohnDoe.getUnit(), ABC, 5, 10, sell);
        testOrderBuy = new Order(JohnDoe.getUnit(), ABC, 5, 10, buy);

        assertAll(
                // Test that the parent class has been setup correctly
                () -> assertEquals(SoldBy, testOrderSell.unit),
                () -> assertEquals(ABC, testOrderSell.asset),
                () -> assertEquals(5, testOrderSell.qty),
                () -> assertEquals(10, testOrderSell.price),
                () -> assertEquals(sell, testOrderSell.isBuy));
        assertAll(
                // Test that the parent class has been setup correctly
                () -> assertEquals(SoldBy, testOrderBuy.unit),
                () -> assertEquals(ABC, testOrderBuy.asset),
                () -> assertEquals(5, testOrderBuy.qty),
                () -> assertEquals(10, testOrderBuy.price),
                () -> assertEquals(buy, testOrderBuy.isBuy));
    }

    /**
     * Test that orders are correctly added to the database.
     * @throws DoesNotExist Throw an exception if the order details do not exist (e.g. unit or asset)
     * @throws OrderException Throw an exception if there is an issue with the order
     */
    @Test
    public void testAddOrder() throws DoesNotExist, OrderException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Create a Sell & Buy order
        testOrderSell = new Order(JohnDoe.getUnit(), ABC, 5, 10, sell);
        testOrderBuy = new Order(JohnDoe.getUnit(), ABC, 5, 10, buy);

        data.addOrder(testOrderSell);
        HashMap<Integer, Order> outstanding = data.getOrders();
        Order placedOrder = null;
        for (int key : outstanding.keySet()) {
            placedOrder = outstanding.get(key);
        }
        Order finalPlacedSellOrder = placedOrder;
        assertAll( // Order ID's not check due to auto incrementation
                () -> assertAll(
                    () -> assertEquals(testOrderSell.unit.getName(), finalPlacedSellOrder.unit.getName()),
                    () -> assertEquals(testOrderSell.unit.getCredits(), finalPlacedSellOrder.unit.getCredits())),
                () -> assertAll(
                    () -> assertEquals(testOrderSell.asset.getId(), finalPlacedSellOrder.asset.getId()),
                    () -> assertEquals(testOrderSell.asset.getDescription(), finalPlacedSellOrder.asset.getDescription())),
                () -> assertEquals(testOrderSell.qty, finalPlacedSellOrder.qty),
                () -> assertEquals(testOrderSell.price, finalPlacedSellOrder.price),
                () -> assertEquals(testOrderSell.isBuy, finalPlacedSellOrder.isBuy),
                () -> assertEquals(testOrderSell.datePlaced.format(formatter), finalPlacedSellOrder.datePlaced.format(formatter))
        );
        data.cancelOrder(placedOrder);

        data.addOrder(testOrderBuy);
        outstanding = data.getOrders();
        placedOrder = null;
        for (int key : outstanding.keySet()) {
            placedOrder = outstanding.get(key);
        }
        Order finalPlacedBuyOrder = placedOrder;
        assertAll( // Order ID's not check due to auto incrementation
                () -> assertAll(
                    () -> assertEquals(testOrderBuy.unit.getName(), finalPlacedBuyOrder.unit.getName()),
                    () -> assertEquals(testOrderBuy.unit.getCredits(), finalPlacedBuyOrder.unit.getCredits())),
                () -> assertAll(
                    () -> assertEquals(testOrderBuy.asset.getId(), finalPlacedBuyOrder.asset.getId()),
                    () -> assertEquals(testOrderBuy.asset.getDescription(), finalPlacedBuyOrder.asset.getDescription())),
                () -> assertEquals(testOrderBuy.qty, finalPlacedBuyOrder.qty),
                () -> assertEquals(testOrderBuy.price, finalPlacedBuyOrder.price),
                () -> assertEquals(testOrderBuy.isBuy, finalPlacedBuyOrder.isBuy),
                () -> assertEquals(testOrderBuy.datePlaced.format(formatter), finalPlacedBuyOrder.datePlaced.format(formatter))
        );
        data.cancelOrder(placedOrder);
    }

    /**
     * Test that when too many assets are attempted to be sold an exception is thrown.
     */

    @Test
    public void TestSellTooMany() {
        Order testSellOrder = new Order(JohnDoe.getUnit(), ABC, 500, 10, sell);
        assertThrows(OrderException.class, () -> data.addOrder(testSellOrder));
        data.cancelOrder(testSellOrder); // Cleanup
    }

    /**
     * Test that when the total cost of a buy order exceeds the organisations credits an exception is thrown
     */
    @Test
    public void TestBuyTooExpensive() {
        Order tooExpensive = new Order(JohnDoe.getUnit(), ABC, 5, 999, buy);
        assertThrows(OrderException.class, () -> {data.addOrder(tooExpensive);});
    }

    /**
     * Test that when the amount of assets being sold exceeds the quantity held by an organisation an exception is thrown.
     */
    @Test
    public void TestBuyTooMany() {
        Order testBuyOrder = new Order(JohnDoe.getUnit(), ABC, 500, 10, buy);
        assertThrows(OrderException.class, () -> {data.addOrder(testBuyOrder);});
    }

    /**
     * Test to check that an exception is thrown if the specified asset either does not exist in the units assets or in
     * assets used in the application.
     */
    @Test
    public void TestAssetDoesNotExist() {
        Order testBuyOrder = new Order(JohnDoe.getUnit(), XYZ, 5, 10, buy);
        Order testSellOrder = new Order(JohnDoe.getUnit(), XYZ, 5, 10, sell);

        assertThrows(DoesNotExist.class, () -> {data.addOrder(testBuyOrder);}); // Test that an exception is thrown if not application asset list
        assertThrows(OrderException.class, () -> {data.addOrder(testSellOrder);}); // Test that exception is thrown if not in units asset list
    }

    /**
     * Test that upon consolidation of a trade the orders table is emptied.
     * @throws DoesNotExist Throw an exception if the order details do not exist (e.g. unit or asset)
     * @throws OrderException Throw an exception if there is an issue with the order
     * @throws InterruptedException Throw exception if the is an issue with the timer
     */
    @Test
    public void TestConsolidate() throws DoesNotExist, OrderException, InterruptedException {
        testOrderSell = new Order(JohnDoe.getUnit(), ABC, 5, 10, sell);
        testOrderBuy = new Order(JaneDoe.getUnit(), ABC, 5, 10, buy);

        int creditChange = 50;
        int assetChange = 5;
        int JohnInitialCredits = JohnDoe.getUnit().getCredits();
        int JohnInitialAssets = JohnDoe.getUnit().getAssets().get(testOrderSell.asset);
        int JaneInitialCredits = JaneDoe.getUnit().getCredits();
        int JaneInitialAssets = JaneDoe.getUnit().getAssets().get(testOrderBuy.asset);
        HashMap<Integer, Order> oldOrderTable = data.getOrders();

        data.addOrder(testOrderBuy);
        data.addOrder(testOrderSell);

        ReconcileTrades runnable = new ReconcileTrades(data);
        Thread thread = new Thread(runnable);
        thread.start();

        TimeUnit.SECONDS.sleep(8); // Extended wait time to ensure at least one reconciliation occurs

        User newJohnDoe = data.getUser(JohnDoe.getUsername());
        User newJaneDoe = data.getUser(JaneDoe.getUsername());

        HashMap<Integer, Order> newOrderTable = data.getOrders();
        assertEquals(oldOrderTable, newOrderTable);
        assertEquals(JohnInitialCredits+creditChange, newJohnDoe.getUnit().getCredits());

        HashMap<Asset, Integer> newJohnDoeAssets = newJohnDoe.getUnit().getAssets();
        HashMap<Integer, Integer> njdaQTY = new HashMap<>();
        for (Asset asset : newJohnDoeAssets.keySet()) {
            njdaQTY.put(asset.getId(), newJohnDoeAssets.get(asset));
        }

        assertEquals(JohnInitialAssets-assetChange, njdaQTY.get(testOrderSell.asset.getId()));
        assertEquals(JaneInitialCredits-creditChange, newJaneDoe.getUnit().getCredits());

        HashMap<Asset, Integer> newJaneDoeAssets = newJaneDoe.getUnit().getAssets();
        HashMap<Integer, Integer> njadaQTY = new HashMap<>();
        for (Asset asset : newJaneDoeAssets.keySet()) {
            njadaQTY.put(asset.getId(), newJaneDoeAssets.get(asset));
        }

        assertEquals(JaneInitialAssets+assetChange, njadaQTY.get(testOrderBuy.asset.getId()));

        runnable.terminate();
    }

    /**
     * Test that upon deletion of trade it no longer exists in the database.
     * @throws DoesNotExist Throw an exception if the order details do not exist (e.g. unit or asset)
     * @throws OrderException Throw an exception if there is an issue with the order
     */
    @Test
    public void TestDelete() throws DoesNotExist, OrderException {
        // Create a Sell & Buy order
        testOrderSell = new Order(JohnDoe.getUnit(), ABC, 5, 10, sell);
        testOrderBuy = new Order(JohnDoe.getUnit(), ABC, 5, 10, buy);

        HashMap<Integer, Order> oldOrderTable = data.getOrders();

        data.addOrder(testOrderBuy);
        HashMap<Integer, Order> currentBuyOrders = data.getOrders();
        int buyOrderID = 0;
        for (Integer buyid : currentBuyOrders.keySet()) {
            buyOrderID = buyid;
        }
        testOrderBuy.id = buyOrderID;
        data.cancelOrder(testOrderBuy);

        HashMap<Integer, Order> middleOrderTable = data.getOrders();
        assertEquals(oldOrderTable, middleOrderTable);

        data.addOrder(testOrderSell);
        HashMap<Integer, Order> currentSellOrders = data.getOrders();
        int sellOrderID = 0;
        for (Integer sellid : currentSellOrders.keySet()) {
            sellOrderID = sellid;
        }
        testOrderSell.id = sellOrderID;
        data.cancelOrder(testOrderSell);

        HashMap<Integer, Order> newOrderTable = data.getOrders();
        assertEquals(middleOrderTable, newOrderTable);
    }
}