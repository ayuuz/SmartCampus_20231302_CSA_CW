package com.mycompany.smartcampus_20231302.exceptions;

/**
 * Thrown when attempting to delete a room that still has sensors assigned.
 */
public class RoomIsNotEmptyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message exception message
     */
    public RoomIsNotEmptyException(String message) {
        super(message);
    }
}
