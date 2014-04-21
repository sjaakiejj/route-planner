package com.openbusiness.exceptions;

public class ProgrammerException extends RoutingAppException
{
   public ProgrammerException(String message, Throwable cause)
   {
      super(cause);
      
      System.err.println("[EXCEPTION] ProgrammerException: " + message);
      // Print to debug log, including cause
   }
}
