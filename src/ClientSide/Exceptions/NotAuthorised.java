package ClientSide.Exceptions;

/**
 * Custom Exception for trying to access methods without the right access
 */
public class NotAuthorised extends Throwable {
    /**
     * Throw an exception with the specified strings
     * @param s1 Main message to display with the exception
     * @param s Name of the exception
     */
    public NotAuthorised(String s1, String s) {

    }

    /**
     * Throw an exception with the specified message
     * @param s1 Message to display with the exception
     */
    public NotAuthorised(String s1) {
    }
}