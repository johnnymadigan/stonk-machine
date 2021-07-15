package ServerSide;

import ClientSide.Asset;
import ClientSide.Exceptions.InvalidAmount;
import ClientSide.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Reconciles orders live behind the scenes
 * @author Alistair Ridge
 */
public class ReconcileTrades implements Runnable {
    private NetworkConnection data;
    private HashMap<Integer, Order> outstanding;
    private ArrayList<Integer> AssetIDs;
    private ArrayList<Order> buys = new ArrayList<>();
    private ArrayList<Order> sells = new ArrayList<>();

    private volatile boolean running = true;

    /**
     * Method used to stop reconciliations from occuring. This method does not affect previously reconciled orders.
     */
    public void terminate() {
        running = false;
    }

    /**
     * Allows the reconcile method to be run on a thread every six seconds.
     */
    @Override
    public void run() {
        while (running) {
            //System.out.println("i'm running in the background around every 6 seconds!");
            try {
                TimeUnit.SECONDS.sleep(6);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.outstanding = data.getOrders();

            //reconcile trade method();
            getOrderType();
            checkOrders();
        }
    }

    /**
     * Constructor used to initialise the reconciliation method. This class checks the outstanding orders table of the specified
     * database for any trades that can be reconciled.
     * @param data The database connection.
     */
    public ReconcileTrades(NetworkConnection data) {
        this.data = data;
    }

    /**
     * Method used to split the outstanding trades in the database into a list for buy orders and a list for sell orders
     */
    private void getOrderType() {
        // Add the buy and sell orders to their respective lists
        for (int i : this.outstanding.keySet()) {
            if (this.outstanding.get(i).isBuy) {
                this.buys.add(this.outstanding.get(i));
            } else {
                this.sells.add(this.outstanding.get(i));
            }
        }
    }

    /**
     * Method used to check the outstanding orders to see if any orders can be reconciled.
     * If an order can be reconciled both the database and client side instances of the order are updated to reflect the
     * reconciliation. A buy order is reconciled if there is a sell order for the same asset, with either the exact or
     * a greater number of the asset. Furthermore the reconciliation only occurs if the buy price is greater than or
     * equal to the sell price. In the case where the sell price is less than the buy price the trade occurs at the lower
     * price.
     */
    private void checkOrders() {
        try {
            if (!buys.isEmpty()) {
                for (Order bo : buys) {
                    if (!sells.isEmpty()) {
                        for (Order so : sells) {
                            Asset ba = bo.asset;
                            Asset sa = so.asset;

                            int bq = bo.qty;
                            int sq = so.qty;

                            int bp = bo.price;
                            int sp = so.price;
                            // Check to see if a reconciliation can occur
                            if (bo.asset.getId() == so.asset.getId() && bo.qty <= so.qty && bo.price >= so.price) {
                                try {
                                    bo.unit.adjustBalance(bo.price * bo.qty * -1); // Subtract the required number of credits from the buyers balance
                                    data.adjustBalance(bo.unit.getName(), bo.unit.getCredits()); // Update the database
                                    bo.unit.adjustAsset(bo.asset, bo.qty); // Add the bought qty of assets to the unit

                                    HashMap<Asset, Integer> bassets = bo.unit.getAssets();
                                    HashMap<Integer, Integer> baIDs = new HashMap<>();
                                    for (Asset buyAssetID : bassets.keySet()) {
                                        baIDs.put(sa.getId(), bassets.get(buyAssetID));
                                    }

                                    data.adjustAssetQuantity(bo.unit.getName(), bo.asset.getId(), baIDs.get(ba.getId())+bo.qty); // Update the database


                                    so.unit.adjustBalance(bo.price * bo.qty); // Add the required number of credits to the sellers balance
                                    data.adjustBalance(so.unit.getName(), so.unit.getCredits()); // Update the database
                                    so.unit.adjustAsset(bo.asset, bo.qty * -1); // Subtract the qty of assets sold to the buys from the unit

                                    HashMap<Asset, Integer> sassets = so.unit.getAssets();
                                    HashMap<Integer, Integer> saIDs = new HashMap<>();
                                    for (Asset sellAssetID : sassets.keySet()) {
                                        saIDs.put(sa.getId(), sassets.get(sellAssetID));
                                    }

                                    data.adjustAssetQuantity(so.unit.getName(), so.asset.getId(), saIDs.get(sa.getId())- bo.qty); // Update the database
                                } catch (InvalidAmount invalidAmount) {
                                    invalidAmount.printStackTrace();
                                }
                                int sellQty = so.qty;
                                so.qty = sellQty - bo.qty;

                                if (so.qty == 0) {
                                    System.out.println("\nAll of an asset's orders have been reconciled...\n" +
                                            "Now cancelling these orders as they are complete");
                                    this.data.cancelOrder(so); // Cancel the sell order if it has no more assets to sell
                                    this.sells.remove(so); // Remove the empty order from the checking list
                                }
                                bo.setDateResolved(LocalDateTime.now());
                                System.out.println("Trade reconciled");
                                this.data.reconcileOrder(bo); // Reconcile the buy order
                                this.buys.remove(bo); // remove the fulfilled order from the checking list
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // acting like a fisherman and catching errors
        }
    }



}
