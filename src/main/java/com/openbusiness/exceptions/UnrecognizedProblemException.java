package com.openbusiness.exceptions;

public class UnrecognizedProblemException extends UsageException {
    public UnrecognizedProblemException(String message) {
        super(new Exception());
	
	System.err.println("Problem " + message + " not recognized."
		+ "Please modify your config file and check for any errors."
		+ "\nCurrent supported problems: [standard|timewindowed]");
    }
}
