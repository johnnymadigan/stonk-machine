package ClientSideTests;

import ClientSide.Exceptions.IllegalString;
import ClientSide.HashPassword;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The following tests are used to test the functionality of the HashPassword class.
 * @author Johnny Madigan
 */
public class TestHashPassword {

    String johnnySalt;
    String scottSalt;
    String alistairSalt;

    // ============== BEFORE EACH TEST ==============
    /**
     * Before each test create a new unique yet random SALT string
     * in-case any test modifies one.
     */
    @BeforeEach
    @DisplayName("Before each test create mock SALT strings")
    public void createSalts() {
        johnnySalt = HashPassword.generateSALT("johnny");
        scottSalt = HashPassword.generateSALT("scott");
        alistairSalt = HashPassword.generateSALT("alistair");
    }

    // ============== TESTS ==============
    /**
     * Test if the SALT generator concatenates any username to the end
     */
    @Test
    @DisplayName("Tests if the SALT generator concatenates any username to the end")
    public void testSaltHasUsername() {
        // The purpose is to have the SALT contain the username, as each new
        // user will have a unique username in the database and therefore a unique
        // SALT string as a result. Hence why checking if the SALT contains the username
        // is suffice.
        assertAll(() -> assertTrue(johnnySalt.contains("johnny")),
                () -> assertTrue(scottSalt.contains("scott")),
                () -> assertTrue(alistairSalt.contains("alistair")));
    }

    /**
     * Tests if the SALT generator creates SALTs that are alphanumeric only
     */
    @Test
    @DisplayName("Tests if salt strings are alphanumeric")
    public void testSaltContents() {
        assertAll(
                () -> assertTrue(johnnySalt.matches("[A-Za-z0-9]+")),
                () -> assertTrue(scottSalt.matches("[A-Za-z0-9]+")),
                () -> assertTrue(alistairSalt.matches("[A-Za-z0-9]+")));
    }

    /**
     * Test if the password parameter inputs are within the bounds...
     * if no exception is thrown then the test passes
     * @throws IllegalString if the password is invalid
     */
    @Test
    @DisplayName("Tests if password bounds, if all passwords can be hashed, the test will pass")
    public void testHashing() throws IllegalString {
        // Upper & lower case letters
        HashPassword.hashPassword("abcABC", scottSalt);
        // Letters and numbers
        HashPassword.hashPassword("money123", scottSalt);
        // Letters and special characters
        HashPassword.hashPassword("money$$$", alistairSalt);
        // Letters, special characters, & numbers
        HashPassword.hashPassword("0123456789abcABC!@#$%^&*({[,.", johnnySalt);
    }

    /**
     * Although random, SHA-512 algorithms always produce keys 128 characters long
     * @throws IllegalString if the password is invalid
     */
    @Test
    @DisplayName("Tests if password was hashed correctly via it's length (128 characters)")
    public void testHashLength() throws IllegalString {
        String p1 = HashPassword.hashPassword("abcABC", scottSalt);
        String p2 = HashPassword.hashPassword("money$$$", scottSalt);
        String p3 = HashPassword.hashPassword("money$$$", alistairSalt);
        String p4 = HashPassword.hashPassword("0123456789abcABC!@#$%^&*({[,.", johnnySalt);

        assertAll(
                () -> assertEquals(128, p1.length()),
                () -> assertEquals(128, p2.length()),
                () -> assertEquals(128, p3.length()),
                () -> assertEquals(128, p4.length()));
    }

    /**
     * Tests if the IllegalString exception is thrown whenever the password is invalid.
     * Invalid passwords are those with white-spaces.
     */
    @Test
    @DisplayName("Tests if the given password does not meet the requirements")
    public void testInvalidPassword() {
        assertThrows(IllegalString.class, () ->
                HashPassword.hashPassword("p ass w o r d", johnnySalt));
    }
}
