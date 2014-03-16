package com.openbusiness.exceptions;

public class InvalidModeException extends Exception {
    public InvalidModeException(String message) {
        super("Mode " + message + " not recognized. Please modify your config file.");
    }
}
