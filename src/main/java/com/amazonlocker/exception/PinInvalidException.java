package com.amazonlocker.exception;

public class PinInvalidException extends RuntimeException {

    public PinInvalidException() {
        super("Pin not valid or has expired. Please contact staff.");
    }
}
