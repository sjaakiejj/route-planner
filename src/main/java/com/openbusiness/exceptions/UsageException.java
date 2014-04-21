package com.openbusiness.exceptions;

public class UsageException extends RoutingAppException
{
   public UsageException(Throwable cause)
   {
      super(cause);
      
      System.err.println("[EXCEPTION] UsageException");
      // Print to debug log, including cause
   }
}
