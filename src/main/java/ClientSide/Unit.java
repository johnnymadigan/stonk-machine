package ClientSide;

import ClientSide.Exceptions.*;
import java.util.HashMap;
import java.util.Set;

/**
 * This class contains the fields and methods that are used by units. Fields include
 * their unique unit name, their current credits, as well as the assets they currently
 * hold and their quantities. A single constructor is used to create an Asset object.
 * The methods in this class include simple getters and setters for the class's fields,
 * as well as methods used to adjust the amount of credits, and the quantities of assets.
 * @author Johnny Madigan, Scott Peachey, and Alistair Ridge
 */
public class Unit {

    private String orgName;
    private Integer orgCredits;
    private HashMap<Asset, Integer> assets;

    /**
     * Constructor for creating new organisational units.
     * @param unitName The name of the unit
     * @param credits The amount of credits assigned to the unit
     * @param assets The assets and their quantities assigned to the unit
     * @throws IllegalString Throw an exception if the unit name contains numbers, special characters or spaces
     * @throws InvalidAmount Throw an exception if the credits are not a valid amount
     */
    public Unit(String unitName, int credits, HashMap<Asset, Integer> assets) throws IllegalString, InvalidAmount {
        String orgNameLC;

        // Check the org name is valid
        if (unitName.matches("[a-zA-Z]+") && !(unitName.contains(" "))) {
            orgNameLC = unitName.toLowerCase();
        } else {
            throw new IllegalString("Unit name '%d' must be letters only. Please try again.", unitName);
        }

        this.orgName = orgNameLC;
        this.orgCredits = credits;
        this.assets = assets;
    }

    /**
     * Getter method for the org unit's name.
     * @return the org unit's name
     */
    public String getName() {
        return orgName;
    }

    /**
     * Setter method for the org unit name.
     * (for future changes / typo during during creation).
     * @param newName new org unit name as a string
     */
    public void setName(String newName) {
        this.orgName = newName;
    }

    /**
     * Getter method for the org unit's credits balance.
     * @return credit balance
     */
    public Integer getCredits() {
        return this.orgCredits;
    }

    /**
     * Setter method for the org unit's credit balance.
     * @param amount to set
     */
    public void setCredits(Integer amount) {
            this.orgCredits = amount;
    }

    /**
     * Add a single asset to the unit object
     * @param assetToAdd asset to add
     * @param quantity quantity of the asset
     */
    public void addAssets(Asset assetToAdd, Integer quantity) {
        this.assets.put(assetToAdd, quantity);
    }

    /**
     * Getter for the unit's assets
     * @return the unit's assets
     */
    public HashMap<Asset, Integer> getAssets() {
        return this.assets;
    }

    /**
     * Adjust the quantity for the unit's assets
     * @param asset asset to adjust quantity
     * @param addQty new quantity
     */
    public void adjustAsset(Asset asset, int addQty) {
        HashMap<Integer, Integer> assetIDs = new HashMap<>();
        // Add all of the current assets held to the HashMap
        for (Asset assettemp : assets.keySet()) {
            assetIDs.put(assettemp.getId(), assets.get(assettemp));
        }

        // If the HashMap contains asset,
        if (assetIDs.containsKey(asset.getId())) {
            this.assets.replace(asset, assetIDs.get(asset.getId())+addQty); // Add the adjustment qty to the asset qty (negative value to decrease)
        } else {
            this.assets.put(asset, addQty); // If the asset doesn't exist in the units assets, add it
        }

    }

    /**
     * Getter for a unit's assets (asset ID only)
     * @return the asset ID a.k.a. "names"
     */
    public Set<Asset> getAssetNames() { return this.assets.keySet(); }

    /**
     * Method to increase a unit's balance.
     * @param amount amount to add
     * @throws InvalidAmount if the amount to add is less than 0
     */
    public void adjustBalance(int amount) throws InvalidAmount {
        int newBalance;

        int minBalance = 0;

        // If decreasing, make sure the amount is within bounds
        if ((this.getCredits() + amount) < minBalance) {
            throw new InvalidAmount("Amount '%d' cannot be larger than the balance and cannot be" +
                    " less than 0 when subtracting.", amount);
        }
        // Increase the current credits by the amount
        newBalance = this.getCredits() + amount;
        this.setCredits(newBalance);
    }

}
