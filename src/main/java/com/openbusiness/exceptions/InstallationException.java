package com.openbusiness.exceptions;

public class InstallationException extends RoutingAppException
{
   public InstallationException(String message, Throwable cause)
   {
      super(cause);
      
      System.err.println("[EXCEPTION] InstallationException: " + message);
      // Print to debug log, including cause
   }
}
