package com.openbusiness.app.amqp;

// AMQP
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.amqp.core.AmqpTemplate;

// To actually run the application
import com.openbusiness.app.RoutingEngine;
import com.openbusiness.app.JSONOutputWriter;
import com.openbusiness.exceptions.ProgrammerException;
import com.openbusiness.exceptions.UsageException;

// JSON
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import java.util.Map;
import java.util.HashMap;

/**
 * This class implements org.springframework.amqp.core.MessageListener.
 *  It implements an API for the Routing Engine
 */
public class RoutePlannerListener implements MessageListener {
    private JSONParser m_parser;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    
    private ApplicationContext m_sender_context;
    private AmqpTemplate m_sender_template;

    public Map<Integer,RoutingEngine> m_thread_map;
    
    public RoutePlannerListener()
    {
      super();
      m_thread_map = new HashMap<Integer,RoutingEngine>();	
      m_sender_context
	    = new ClassPathXmlApplicationContext("/com/openbusiness/configuration/rabbit-sender-context.xml");
      m_sender_template = (AmqpTemplate) m_sender_context.getBean("rpTemplate");
    }

    public void onMessage(Message message) {
    	String messageBody= new String(message.getBody());
        System.out.println(ANSI_PURPLE + "Received Message----->"+messageBody + ANSI_RESET);
	
	int thread_id = -1;
	// Read JSON content
	if(m_parser == null)
	  m_parser =new JSONParser();
	
	try{
	  JSONObject json = (JSONObject)m_parser.parse( messageBody );

	  String api_call = (String)json.get("api_call");
	  thread_id = Integer.parseInt( (String)json.get("client_id") );

	  if(m_thread_map.get(thread_id) == null)
	  {
	    // Create a new engine instance and start its thread
	    RoutingEngine r_thread = new RoutingEngine();
	    m_thread_map.put(thread_id, r_thread);
	  }

	  try{
	    if( api_call.equals("api_clear_solution") )
	      _api_clear_solution(thread_id);
	    else if( api_call.equals("api_terminate_early") )
	      _api_terminate_early(thread_id);
	    else if( api_call.equals("api_get_best_solution") )
	      _api_get_best_solution(thread_id);
	    else if( api_call.equals("api_get_status") )
	      _api_get_status(thread_id);
	    else if( api_call.equals("api_set_properties") )
	      _api_set_properties( thread_id, (String)json.get("body"));

	    else if( api_call.equals("api_run") )
	      _api_run(thread_id, (String)json.get("body"));
	      
	    else if( api_call.equals("api_heartbeat") )
	      _ctl_send_message(thread_id, "heartbeat");
	    else
	      _ctl_send_error(thread_id, "Unrecognized API call: " + messageBody);
	  }
	  catch(UsageException e){
	      _ctl_send_error(thread_id, "Exception in processing API call: " + e.getMessage());
	      // and log the stack trace
	  }
	  catch(ProgrammerException e){
	      _ctl_send_error(thread_id, "API Exception. Please contact RoutingEngine admin.");
	      // and log the stack trace
	  }
	}
	catch(ParseException e)
	{
	  _ctl_send_error(thread_id, "Ill-formatted JSON: " + e);
	}
	catch(Exception e)
	{
	  _ctl_send_error(thread_id, ""+e);
	  
	  // TODO: Throw appropriate exceptions here
	  e.printStackTrace();
	}
    }
    
    // *****************************************************
    // API CODE
    // *****************************************************
    private void _api_clear_solution(int thread_id)
    {
        m_thread_map.put(thread_id, new RoutingEngine());
    }
    
    private void _api_terminate_early(int thread_id)
    {
        _api_print("Terminating algorithm early...");
	m_thread_map.get(thread_id).terminateEarly();
    } 
    
    private void _api_get_best_solution(int thread_id)
    {
	String solution = m_thread_map.get(thread_id).getBestSolutionJSON();
	
	if( solution == null )
	{
	  if( !m_thread_map.get(thread_id).getStarting() )
	  {
            _ctl_send_error( thread_id, "The algorithm has not started yet" );
	    return;
	  }
	  else
	    return;
	}
	
	
	_ctl_send_message( thread_id, solution );
    }
    
    private void _api_get_status(int thread_id)
    {
        if(m_thread_map.get(thread_id).running())  
          _ctl_send_message( thread_id, "Solving" );
	
	else if( m_thread_map.get(thread_id).getBestSolutionJSON() == null )
	  _ctl_send_message( thread_id, "Unsolved" );
	  
	else
	  _ctl_send_message( thread_id, "Solved" );
    }
    
    private void _api_set_properties(int thread_id, String propertyJson) throws UsageException
    {
        _api_print("Setting properties...");
	m_thread_map.get(thread_id).loadPropertiesFromString(propertyJson);
	_api_print("Properties set");
	_ctl_send_message( thread_id, "properties_set");
    }
    
    private void _api_run(int thread_id, String propertyJson) throws UsageException,ProgrammerException
    {
        _api_print("Running algorithm");
	if( !m_thread_map.get(thread_id).running() )
	{
	   _api_clear_solution(thread_id);
	   // Set the properties
	   m_thread_map.get(thread_id).loadPropertiesFromString(propertyJson);
	   _api_print("Starting...");
	   m_thread_map.get(thread_id).setStarting(true);
	   
	   _ctl_send_message(thread_id,"started");
	   
	   m_thread_map.get(thread_id).apiSetup();
	   m_thread_map.get(thread_id).start();
	   
	   if (m_thread_map.get(thread_id).getErrorMessage() != "")
	   { 
	      _ctl_send_error(thread_id, m_thread_map.get(thread_id).getErrorMessage());
	   }
	}
	else if( !m_thread_map.get(thread_id).propertiesSet() )
	{
	   _ctl_send_error(thread_id, "Properties not set");
	}
	else
	   _ctl_send_message(thread_id, "Queue...");
    }
    
    private void _api_print(String message)
    {
        System.out.println(ANSI_GREEN + "[API] " + message + ANSI_RESET);
    }
    
    // *****************************************************
    // PUBLISHER CODE
    // *****************************************************
    private void _ctl_send_message(int thread_id, String message)
    {
        System.out.println(ANSI_GREEN + "[INFO] Generating message for client");
        JSONOutputWriter out = new JSONOutputWriter();
	out.writeData("header", "data");
	out.writeData("client", ""+thread_id);
	out.writeData("body", message);
	
	m_sender_template.convertAndSend("client.routeplanner."+thread_id, out.getString());
	
        System.out.println("[INFO] Sent Message: " + out.getString() + ANSI_RESET);
    }
    
    private void _ctl_send_error(int thread_id, String error)
    {
        System.out.println(ANSI_RED + "[ERROR] " + error + ANSI_RESET);
	JSONOutputWriter out = new JSONOutputWriter();
	
	out.writeData("header", "error");
	out.writeData("client", ""+thread_id);
	out.writeData("body", error);
	
	m_sender_template.convertAndSend("client.routeplanner."+thread_id, out.getString());
    }
    
    private void _ctl_send_error(int thread_id, int error_code, String error)
    {
        System.out.println(ANSI_RED + "[ERROR]["+error_code+"] " + error + ANSI_RESET);
	JSONOutputWriter out = new JSONOutputWriter();
	
	out.writeData("header", "error");
	out.writeData("client", ""+thread_id);
	out.writeData("code", ""+error_code);
	out.writeData("body", error);
	
	m_sender_template.convertAndSend("client.routeplanner."+thread_id, out.getString());
    }
}
