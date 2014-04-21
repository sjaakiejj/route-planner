package com.openbusiness.exceptions;
public class PropertyFileException extends UsageException
{   
   public PropertyFileException(String message, Throwable cause)
   {
      super(cause);
      
      System.err.println("[EXCEPTION] File: " + message);
      // Print to debug log, including cause
   }
}

