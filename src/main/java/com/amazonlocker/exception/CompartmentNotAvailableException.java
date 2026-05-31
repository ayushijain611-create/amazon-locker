package com.amazonlocker.exception;

public class CompartmentNotAvailableException extends RuntimeException {

    public CompartmentNotAvailableException() {
        super("Request rejected. No compartment of given size available.");
    }
}
