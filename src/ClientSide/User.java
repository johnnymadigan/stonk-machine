package ClientSide;

import ClientSide.Exceptions.IllegalString;

/**
 * Create user instances with the data encapsulated within an instance
 * @author Johnny Madigan
 */
public class User {

    // INSTANCE VARIABLES------------------------------------------------------------------------------------------------
    private final String name;
    private final String salt;
    private String password;
    private boolean access;
    private Unit unit;

    // CONSTRUCTOR------------------------------------------------------------------------------------------------------

    /**
     * User constructor to recreate a user object with data from the database
     * @param username the user's unique username
     * @param password the user's hashed password
     * @param salt the user's unique salt
     * @param unit the user's unit object
     * @param access the user's access level
     * @throws IllegalString Throw an exception it the password is not a valid string
     */
    public User(String username, String password, String salt, Unit unit, boolean access) throws IllegalString {
        String usernameLC;

        // Check the username is valid
        if (username.matches("[a-zA-Z]+") && !(username.contains(" "))) {
            usernameLC = username.toLowerCase();
        } else {
            throw new IllegalString("Username '%s' must be letters only. Please try again.", username);
        }

        this.name = username;
        this.password = password;
        this.salt = salt;
        this.unit = unit;
        this.access = access;
    }

    // GETTERS & SETTERS------------------------------------------------------------------------------------------------
    /**
     * Getter for the username.
     * NO setter as the username is unique and decided upon user creation (hence final).
     * @return username string
     */
    public String getUsername() {
        return this.name;
    }

    /**
     * Getter for the user's salt.
     * @return salt string
     */
    public String getSalt() {
        return this.salt;
    }

    /**
     * Getter for the user's password.
     * @return username string
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Getter for the admin access
     * @return admin access bool
     */
    public boolean getAccess() {
        return this.access;
    }

    /**
     * Getter for this user's unit.
     * @return The users unit
     */
    public Unit getUnit () {
        return this.unit;
    }

    /**
     * Setter for this user's unit.
     * @param unit to set
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

}
