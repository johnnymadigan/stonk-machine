package ClientSide.Exceptions;

/**
 * Custom Exception for items that already exist
 */
public class AlreadyExists extends Throwable {
    /**
     * Throw an exception if a string already exists in the database
     * @param s Message to display with the exception
     * @param s1 String that does not exist in the database
     */
    public AlreadyExists(String s, String s1) {

    }

    /**
     * Throw an exception if the given object already has the given item in its inventory
     * @param s Message to display with the exception
     * @param s1 Identifier of the object
     * @param s2 Identifier of the item that already exists in the objects inventory
     */
    public AlreadyExists(String s, String s1, String s2) {
    }

    /**
     * Throw an exception if an int already exists in the database
     * @param s Message to display with the exception
     * @param i Integer that already exists in the database
     */
    public AlreadyExists(String s, int i) {
    }
}
