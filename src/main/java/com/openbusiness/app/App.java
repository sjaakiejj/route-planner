package com.openbusiness.app;

import java.util.List;
import java.util.Arrays;

// Package specific AMQP classes
import com.openbusiness.app.amqp.*;

// AMQP
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.amqp.core.AmqpTemplate;
 
public class App
{
   private static ApplicationContext m_listener_context;

   public static void main(String [] args)
   {
      List<String> cmd_args = Arrays.asList(args);
      
      if( cmd_args.contains("-amqp") )
      {
	
	// Start a listener daemon
	m_listener_context 
	    = new ClassPathXmlApplicationContext("/com/openbusiness/configuration/rabbit-listener-context.xml");
	// Create a sender context and tell routing engine it can
	// send messages
      }
      else
      {
        RoutingEngine engine = new RoutingEngine();
	engine.defaultSetup(cmd_args);
	engine.start();
      }
   }
   
}
