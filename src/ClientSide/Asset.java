package ClientSide;

/**
 * This class contains the fields and methods that are used by assets. Fields include
 * their unique ID and description. A single constructor is used to create an Asset
 * object. The methods in this class are simple getters for the class's fields.
 * @author Scott Peachey
 */
public class Asset {

    // INSTANCE VARIABLES-----------------------------------------------------------------------------------------------
    private final int id;
    private String description;

    // CONSTRUCTOR------------------------------------------------------------------------------------------------------
    /**
     * Asset constructor to recreate an asset object with data from the database.
     * @param assetID the asset's unique ID
     * @param description the asset's description
     */
    public Asset(int assetID, String description) {
        // Store the asset ID locally
        this.id = assetID;

        // Store the asset description locally
        this.description = description;
    }

    // GETTERS & SETTERS------------------------------------------------------------------------------------------------
    /**
     * Getter for the asset's description.
     * No setter, however the description can be changed using the NetworkConnection
     * method updateAssetDesc.
     * @return description
     */
    public String getDescription() { return description; }

    /**
     * Getter for the asset's ID.
     * NO setter as the ID is unique & decided upon asset creation (hence final).
     * @return id
     */
    public int getId() { return id; }

    /**
     * Getter for the asset's ID, converted to a String.
     * Created to make code easier to read.
     * @return Integer.toString(id)
     */
    public String getIdString() {
        return Integer.toString(id);
    }

}


