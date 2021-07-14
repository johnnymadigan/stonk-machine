package ClientSide.Exceptions;

/**
 * Custom Exception for parameters (strings) that are invalid inputs
 */
public class IllegalString extends Throwable {
    /**
     * Throw an exception with a given message and the string that was not in a valid format
     * @param s Message to display with the exception
     * @param username String that was not in a valid format
     */
    public IllegalString(String s, String username) {
    }

    /**
     * Throw an exception with the given message if a string is not in a valid format
     * @param s Message to display with the exception
     */
    public IllegalString(String s) {
    }
}
