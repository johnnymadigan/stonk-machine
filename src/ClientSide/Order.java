package ClientSide;

import java.time.LocalDateTime;

/**
 * Organises orders
 * @author Alistair Ridge
 */
public class Order {
    public int id;
    public Unit unit;
    public Asset asset;
    public LocalDateTime datePlaced;
    public LocalDateTime dateResolved;
    public int qty;
    public int price;
    public boolean isBuy; // True for buy orders, False for sell orders

    /**
     * This constructor sets the trade ID of the order and adds the trade specific information to the object.
     * @param unit Unit of the user that placed the trade
     * @param asset Asset that is being traded
     * @param qty Amount of the asset that is being traded
     * @param price Price at which the asset is being traded
     * @param isBuy Used to track the order type, True for Buy orders, False for Sell orders
     */
    public Order(Unit unit, Asset asset, int qty, int price, Boolean isBuy) {
        // Set the time that the order was created.
        this.datePlaced = LocalDateTime.now();

        // Update trade specific data
        this.unit = unit;
        this.asset = asset;
        this.qty = qty;
        this.price = price;

        this.isBuy = isBuy;
    }

    /**
     * The constructor used to create an order from a database object.
     * @param unit Unit that the order was placed on behalf of
     * @param asset The asset that is being traded
     * @param qty The quantity of the assest that is beingtraded
     * @param price The price at which the asset is being traded at
     * @param isBuy The type of order, true for Buy orders, false for sell orders
     * @param ID The ID of the order from the database.
     */
    public Order(Unit unit, Asset asset, int qty, int price, Boolean isBuy, int ID) {
        // Increment the trade ID every time a new order is created so that all orders have a unique ID.
        this.id = ID;

        // Set the time that the order was created.
        this.datePlaced = LocalDateTime.now();

        // Update trade specific data
        this.unit = unit;
        this.asset = asset;
        this.qty = qty;
        this.price = price;

        this.isBuy = isBuy;
    }

    /**
     * Setter for the date resolved variable of the object
     * @param dateResolved The date that the trade was esolved on in the for YYYY-MM-DD HH:MM:SS
     */
    public void setDateResolved(LocalDateTime dateResolved) {
        this.dateResolved = dateResolved;
    }
}
