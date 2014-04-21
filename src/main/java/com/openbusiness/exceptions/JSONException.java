package com.openbusiness.exceptions;
public class JSONException extends UsageException
{   
   public JSONException(String message, Throwable cause)
   {
      super(cause);
      
      System.err.println("[EXCEPTION] JSON String: " + message);
      // Print to debug log, including cause
   }
}
