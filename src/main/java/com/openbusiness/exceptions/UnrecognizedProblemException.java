package com.openbusiness.exceptions;

public class UnrecognizedProblemException extends Exception {
    public UnrecognizedProblemException(String message) {
        super("Problem " + message + " not recognized."
		+ "Please modify your config file and check for any errors."
		+ "\nCurrent supported problems: [standard|timewindowed]");
    }
}
