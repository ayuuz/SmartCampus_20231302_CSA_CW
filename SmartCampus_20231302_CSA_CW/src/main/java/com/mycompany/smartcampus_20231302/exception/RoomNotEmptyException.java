package com.mycompany.smartcampus_20231302.exception;

/**
 * Thrown when attempting to delete a room that still has sensors assigned.
 */
public class RoomNotEmptyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message exception message
     */
    public RoomNotEmptyException(String message) {
        super(message);
    }
}
