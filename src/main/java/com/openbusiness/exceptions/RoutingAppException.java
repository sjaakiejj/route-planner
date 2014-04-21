package com.openbusiness.exceptions;

public class RoutingAppException extends Exception {
   
   public RoutingAppException(Throwable cause)
   {
      // Default behavior - just log to file
      super("RoutingAppException: " + cause.getMessage());
      System.err.println("[EXCEPTION]" + cause.getMessage());
   }
}
