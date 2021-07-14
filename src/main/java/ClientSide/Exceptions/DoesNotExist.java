package ClientSide.Exceptions;

/**
 * Custom Exception for items that do not exist
 */
public class DoesNotExist extends Throwable {
    /**
     * Throw an exception if a string does not exist in the database
     * @param s Message to display with the exception
     * @param itemThatDoesNotExist String that does not exist in the database
     */
    public DoesNotExist(String s, String itemThatDoesNotExist) {

    }

    /**
     * Throw an exception if an item does not exist in the database
     * @param s Message to display with the exception
     */
    public DoesNotExist(String s) {
    }

    /**
     * Throw an exception if an int does not exist in the database
     * @param s Message to display with the exception
     * @param i Int that does not exist in the database
     */
    public DoesNotExist(String s, int i) {
    }
}
