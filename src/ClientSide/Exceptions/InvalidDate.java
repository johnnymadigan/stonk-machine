package ClientSide.Exceptions;

/**
 * Custom Exception for parameters (dates) that are invalid inputs
 */
public class InvalidDate extends Throwable{
    /**
     * Throw an exception if the date is not in the correct format
     * @param s Message to display with the exception
     */
    public InvalidDate(String s) {
    }
}
