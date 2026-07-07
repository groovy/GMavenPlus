package org.codehaus.gmavenplus.util;

/**
 * Exception thrown when Groovy version cannot be determined.
 *
 * @author Keegan Witt
 * @since 4.3.2
 */
public class GroovyVersionException extends RuntimeException {

    /**
     * Constructs a new GroovyVersionException with the specified message.
     *
     * @param message the detail message
     */
    public GroovyVersionException(final String message) {
        super(message);
    }

    /**
     * Constructs a new GroovyVersionException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public GroovyVersionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
