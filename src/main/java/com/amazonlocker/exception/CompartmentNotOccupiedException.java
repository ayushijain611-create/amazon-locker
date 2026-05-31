package com.amazonlocker.exception;

public class CompartmentNotOccupiedException extends RuntimeException {

    public CompartmentNotOccupiedException() {
        super("Nothing to clear.");
    }
}
