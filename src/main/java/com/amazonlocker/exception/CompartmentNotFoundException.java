package com.amazonlocker.exception;

public class CompartmentNotFoundException extends RuntimeException {

    public CompartmentNotFoundException() {
        super("Compartment not found.");
    }
}
