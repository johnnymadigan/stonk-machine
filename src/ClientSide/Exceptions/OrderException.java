package ClientSide.Exceptions;

/**
 * Custom Exception for orders that are illegal
 * @author Alistair Ridge
 */
public class OrderException extends Exception {
    /**
     * Generic constructor for the exception
     */
    public OrderException() {
        super();
    }

    /**
     * Throw an exception with the specified message
     * @param message The message the is printed with the exception
     */
    public OrderException(String message) {
        super(message);
    }

    /**
     * Throw a exception with the specified message and cause
     * @param message Message to print with the exception
     * @param cause The cause of the exception
     */
    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Throw an exception with a message and cause, and suppress the output
     * @param message Message associated to the exception
     * @param cause Cause of the exception
     * @param enableSuppression Bool used to suppress the output, true to suppress
     * @param writeableStackTrace Stack trace for the exception
     */
    public OrderException(String message, Throwable cause, boolean enableSuppression, boolean writeableStackTrace) {
        super(message, cause, enableSuppression, writeableStackTrace);
    }

    /**
     * Throw an exception with the specified cause
     * @param cause The cause of the exception
     */
    public OrderException(Throwable cause) {
        super(cause);
    }
}