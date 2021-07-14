package ClientSide;

import ClientSide.Exceptions.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Security class, has the purpose of creating salt strings and hashing passwords
 * Used for hashing new passwords (with new salt), changing passwords (re-hashing with existing salt),
 * and logging in (hashing input password then comparing with database password).
 * @author Johnny Madigan
 */
public class HashPassword {

    // SALT ------------------------------------------------------------------------------------------------------------

    /**
     * Helper method that generates a random salt string. The string is then made globally unique
     * by concatenating the user's unique username on the end. This will later be converted into
     * bytes to hash with the corresponding user's password.
     * @param username Username of the user that the SALT string is being generated for
     * @return random alphanumeric salt string.
     * @see <a href="https://stackoverflow.com/a/20536597">Inspired from stack overflow</a>
     */
    public static String generateSALT(String username) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        // Create the random SALT string with a string builder
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 11) { // length of the random string
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }

        // most crucial step to ensure salt is globally unique
        // combining the salt string with the user's unique username
        String saltStr = salt.toString();
        saltStr = saltStr.concat(username);
        return saltStr;
    }

    // HASHING PASSWORDS -----------------------------------------------------------------------------------------------

    /**
     * This method uses a SHA-512 algorithm to break the password and salt down into bytes before
     * hashing into a string of 128 chars. Furthermore, this method is deterministic. So it can also
     * be used for authenticating user's when logging in, given the same password and salt parameters
     * are used and linked to the user's account in the database.
     * @param password the password pre-hash
     * @param salt the string which will become bytes before hashing the password
     * @return the hashed password
     * @see <a href="https://stackoverflow.com/a/33085670">Inspired from stack overflow</a>
     * @throws IllegalString Throw an exception if the password is not a valid string
     */
    public static String hashPassword(String password, String salt) throws IllegalString {
        String hashedPassword = null;
        String checkedPassword;

        // Check if the password contains white spaces, throw an exception if so
        if (password.contains(" ")) {
            throw new IllegalString("Password must not contain white-spaces. Please try again.", password);
        } else {
            checkedPassword = password;
        }

        // SHA-512 algorithm
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(checkedPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            hashedPassword = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return hashedPassword;
    }
}