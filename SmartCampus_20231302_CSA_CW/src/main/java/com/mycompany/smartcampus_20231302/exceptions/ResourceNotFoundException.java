package com.mycompany.smartcampus_20231302.exceptions;

/**
 * Thrown when a request references another resource that does not exist.
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message exception message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
