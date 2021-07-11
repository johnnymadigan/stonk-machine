package ClientSide.Exceptions;

/**
 * Custom Exception for parameters (integers) that are invalid inputs
 */
public class InvalidAmount extends Throwable {
    /**
     * Throw an exception with the given message and the amount that was attempted to be added to the units balance
     * @param s Message to display with the exception
     * @param amount Amount that was attempted to be added to the units balance
     */
    public InvalidAmount(String s, Integer amount) {}
}
