package ClientSide.GUI;

import ClientSide.Order;
import ClientSide.Unit;
import ClientSide.User;
import ServerSide.NetworkConnection;

import javax.swing.*;

/**
 * Where the shell is created ready for content to be added.
 * Also used to change the content contained in the shell.
 * @author Alistair Ridge
 */
public class CreateShell {
    private NetworkConnection data;
    private JFrame mainFrame;
    private AdminHome adminHome;
    private UserHome userHome;
    private final Shell shell;
    private final boolean admin;

    private boolean showPage = true;
    private User user;

    /**
     * Method used to create a generic GUI shell.
     * @param user Current user that is logged into the application.
     * @param mainFrame The overarching JFrame that the GUI is comprised of
     * @param data The connection to the database
     */
    public CreateShell(User user, JFrame mainFrame, NetworkConnection data) {
        this.admin = user.getAccess();
        this.user = user;
        this.mainFrame = mainFrame;
        this.data = data;

        this.shell = new Shell(user, this, mainFrame, data);

        if (admin) {
            this.adminHome = new AdminHome(user, mainFrame, data);
        } else {
            this.userHome = new UserHome(user, this, mainFrame, data);
        }
    }

    /**
     * Method used to update the shell to display the home page of the GUI
     */
    public void GoHome() {
        if (this.admin) {
            this.shell.shellPanel(adminHome.getPanel(), showPage);
        } else {
            this.shell.shellPanel(userHome.getPanel(), showPage);
        }
    }

    /**
     * Method used to update the shell to display the order page of the GUI
     * @param assetID The ID of the asset that is being traded.
     * @param isBuy The type of order that is being placed, true for buy, false for sell
     */
    public void GoToOrder(String assetID, boolean isBuy) {
        OrdersPage orderPage = new OrdersPage(assetID, isBuy, this.user, this, this.mainFrame, this.data);
        this.shell.shellPanel(orderPage.getPanel(), showPage);
    }

    /**
     * Method used to go to the order placed page of the GUI. This page is only displayed if an order is successfully
     * added to the database.
     * @param order The order that has been placed and added to the database
     */
    public void GoToOrderPlaced(Order order) {
        OrderPlacedPage orderPlacedPage = new OrderPlacedPage(order, this, this.mainFrame, this.data);
        this.shell.shellPanel(orderPlacedPage.getPanel(), showPage);
    }

    /**
     * Method used to go to the order failed page. This page is only displayed if an order is not successfully added to
     * the database.
     * @param order The order that was unsuccessfully placed
     * @param e The exception that was thrown when the order as unsuccessfully placed
     */
    public void GoToOrderFailed(Order order, Exception e) {
        OrderFailedPage orderFailedPage = new OrderFailedPage(order, this, this.mainFrame, this.data, e);
        this.shell.shellPanel(orderFailedPage.getPanel(), showPage);
    }

    /**
     * Method used to update the shell to display the Asset page of the GUI. This page is used to display historical
     * data for the specified asset.
     * @param asset The ID of the asset that is being viewed.
     */
    public void GoToAssets(String asset) {
        AssetPage assetPage = new AssetPage(asset, this.user, this, this.mainFrame, this.data);
        this.shell.shellPanel(assetPage.getPanel(), showPage);
    }

    /**
     * Method used to update the shell to display a list of reconciled trades for a specified unit.
     * @param unit The unit that reconciled trades will be displayed for
     */
    public void GoToOrderHistory(Unit unit) {
        OrderHistory orderHistory = new OrderHistory(unit, this.user, this, this.mainFrame, this.data);
        this.shell.shellPanel(orderHistory.getPanel(), showPage);
    }

    /**
     * Method used to update the shell to display a list of outstanding trades for the specified unit.
     * @param unit The unit that the outstanding trades will be displayed for.
     */
    public void GoToCurrentOrders(Unit unit) {
        CurrentOrders currentOrders = new CurrentOrders(unit, this.user, this, this.mainFrame, this.data);
        this.shell.shellPanel(currentOrders.getPanel(), showPage);
    }
}
