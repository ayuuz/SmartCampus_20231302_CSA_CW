package com.mycompany.smartcampus_20231302.exceptions;

/**
 * Thrown when a sensor cannot accept readings due to its current state.
 */
public class SensorNotAvailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message exception message
     */
    public SensorNotAvailableException(String message) {
        super(message);
    }
}
