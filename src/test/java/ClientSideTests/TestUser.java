package ClientSideTests;

import ClientSide.Exceptions.IllegalString;
import ClientSide.Exceptions.InvalidAmount;
import ClientSide.HashPassword;
import ClientSide.Unit;
import ClientSide.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The following tests are used to test the functionality of the User class.
 * @author Johnny Madigan
 */
public class TestUser {

    User mockUser;
    Unit mockUnit;

    // ============== BEFORE EACH TEST ==============
    /**
     * Before each test, create a mock user and unit
     * @throws IllegalString Throw an exception if a parameter is invalid
     * @throws InvalidAmount Throw an exception if the credits are not a valid amount
     */
    @BeforeEach
    @DisplayName("Before each... create new mock users & a unit")
    public void createMockObjects() throws IllegalString, InvalidAmount {
        String username = "johnny";
        String password = "bo$$man";
        String salt = HashPassword.generateSALT(username);
        String hashedPassword = HashPassword.hashPassword(password, salt);

        String unitname = "developers";
        mockUnit = new Unit(unitname, 999, null);
        mockUser = new User(username, hashedPassword, salt, null, true);
    }

    // ============== AFTER EACH TEST ==============
    /**
     * After each test, reset the mock user and unit
     */
    @AfterEach
    @DisplayName("After each... delete mock users & the unit")
    public void clearMockObjects() {
        mockUser = null;
        mockUnit = null;
    }

    // ============== TESTS ==============
    /**
     * Testing the User constructor works via the getters
     */
    @Test
    @DisplayName("Users constructor assigns the data correctly")
    public void testUserConstructor() {
        assertAll(
                // the username is assigned correctly
                () -> assertEquals("johnny", mockUser.getUsername()),
                // the admin access is true
                () -> assertTrue(mockUser.getAccess()),
                // the salt string is made unique by concatenating with the username
                () -> assertTrue(mockUser.getSalt().contains("johnny")),
                // the salt string is generated as alphanumeric
                () -> assertTrue(mockUser.getSalt().matches("[A-Za-z0-9]+")),
                // the password has been hashed (SHA-512 gives a hashed string 128 chars in length)
                () -> assertEquals(128, mockUser.getPassword().length()),
                // the unit is null by default as no unit has been set
                () -> assertNull(mockUser.getUnit()));
    }

    /**
     * Tests the Unit setter works for the user object
     */
    @Test
    @DisplayName("Setters (only 1 for user's unit) works correctly")
    public void getAndSetUserUnit() {
        mockUser.setUnit(mockUnit);
        assertEquals(mockUnit, mockUser.getUnit());
    }

    /**
     * Tests if the IllegalString exception is thrown when trying to create a user with an invalid username.
     * The first assert tests a username with numbers, while the second assert tests a username with white-spaces.
     */
    @Test
    @DisplayName("Exception is thrown if the username is invalid in any ways")
    public void usernameInvalid() {
        assertAll(() -> assertThrows(IllegalString.class, () ->
                        mockUser = new User("guy123","password", "SALT", null, false)),
                () -> assertThrows(IllegalString.class, () ->
                        mockUser = new User("g u y","password", "SALT", null, false)));
    }

    /**
     * Tests the username bounds, if no exceptions are thrown when assigning usernames, the test will pass.
     * @throws IllegalString if the username is invalid in any way
     */
    @Test
    @DisplayName("Create users with different usernames to test the bounds")
    public void usernameBounds() throws IllegalString {
        mockUser = new User("lowercase", "password", "SALT", null, false);
        mockUser = new User("UPPERCASE", "password", "SALT", null, false);
        mockUser = new User("bOtH", "password", "SALT", null, false);
    }
}
