var jar_location = "/home/sjaakiejj/my-programs/OpenBusiness/route-planner/route-planner-1.0-SNAPSHOT-jar-with-dependencies.jar";
var Future = Npm.require('fibers/future');
Fiber = Npm.require('fibers');

function generateSettings(args,callback)
{
   
   var result = null;
   var modelPath = "/bin/cat";
   var require = Npm.require;
   var fs = require('fs');
   
   fs.writeFile("/tmp/settings.json", args, function(err){
   	if(err)
	  console.log(err);
	else
	{
	  console.log("/tmp/settings.json created");
	  callback(null,"");
	}
   });
   
//   var require = Npm.require;
//   var spawn = require('child_process').spawn;
//   var model = spawn(modelPath, ['>','/tmp/settings.json']);
//   model.stdin.write("" +args);
//   model.stdin.end();
   
//   model.on('close', function(code)
//   {
//     console.log('done writing');
//     callback(null,"/tmp/settings.json");
//   });
}

function planRoute(callback)
{
   var result = null;
   var modelPath = "/usr/bin/java";

   var require = Npm.require;
   var spawn = require('child_process').spawn;
   var model = spawn(modelPath, ["-jar",jar_location,
   "-json","/tmp/settings.json"]);
   var answerBuffer = "";

   //model.stdin.write(argument);
   //model.stdin.end(); //flush

   model.stdout.on('data', function(data)
   {
       answerBuffer+=data;
   });

   model.stderr.on('data', function(data)
   {
       console.log('stderr: ' + data);
   });

   model.on('close', function(code)
   {
       //result = JSON.parse(answerBuffer);
       console.log('child process exited with code ' + code);
       console.log(answerBuffer);
       callback(null,answerBuffer);
//       callback(result);
   });
}

function tester(args,callback)
{
  
}

// ************************************
// Boiler plate code
// ************************************
var _connection;

function createConnection()
{
  return amqp.createConnection({ host: 'localhost'},
  			       { defaultExchange: 'route-planner-exchange'});
}

function init()
{
  Fiber(function(){
  var connection = createConnection();
  console.log("Initialising...");
  
  connection.on('ready', function() {
  	connection.queue('myRpQueue', function(q){
		q.bind('route-planner-exchange', 'client.#.*'); // We should listen to any messages routed to the client
		
		q.subscribe(receiveMessage);
	});
	
  });
  }).run();
}


// ************************************
// Message Handling
// ************************************

function receiveMessage(msg)
{
  Fiber(function(){
  console.log(msg);
  var jsonStr = unescape(msg.data);
  var json = JSON.parse(jsonStr);
  
  console.log(Number(json.client));
  Meteor.ClientCall.apply(Number(json.client), 
  			  'receiveMessage', 
			  [JSON.stringify(json)]);
  console.log("Client " + json.client + "Called");
  }).run();
  //Meteor.publish("message_listener_"+json.client, publishToClient(json));
}

function apply()
{
  console.log("Sending..");
  Meteor.ClientCall.apply(15, 
  			  'receiveMessage', 
			  []);  
}

function getMethods(obj) {
  var result = [];
  for (var id in obj) {
    try {
      if (typeof(obj[id]) == "function") {
        result.push(id + ": " + obj[id].toString());
      }
    } catch (err) {
      result.push(id + ": inaccessible");
    }
  }
  return result;
}

function publishMessage(conn,msg)
{
  var created = false;
  if (conn === undefined) { 
     conn = createConnection();
     _connection = conn; 
  } 
  console.log("Connection created... Publishing...");
  conn.on('ready', function () { 
       console.log("Sending message..."); 
       
       conn.publish("apiRpQueue", msg, {}, function(){
       		console.log("Closing connection");
       });
  }); 
       conn.publish("apiRpQueue", msg, {}, function(){
       		console.log("Closing connection");
       });
       console.log("Disconnected");
}

//
Meteor.methods({
    "callPlanRoute": function (args) {
      console.log("Test");
      var fut = new Future();
      var cat_bound_callback = Meteor.bindEnvironment(function (err, res) {
        if(err) {
          fut.throw(err);
        }  else {
	  console.log(res);
        //  fut.return(res);
	  var route_bound_callback = Meteor.bindEnvironment(function(err,res){
	    if(err)
	      fut.throw(err);
	    else
	      fut.return(res);
	  });
          planRoute(route_bound_callback);
        }
      });
      generateSettings(args,cat_bound_callback);
      return fut.wait();
    },
    "callTest": function (args) {
      publishMessage(undefined, args);
    /*
      console.log("Test");
      var fut = new Future();
      var bound_callback = Meteor.bindEnvironment(function (err, res) {
        if(err) {
          fut.throw(err);
        }  else {
	  console.log(res);
          fut.return(res);
        }
      });
      tester(args,bound_callback);
      return fut.wait(); */
    }
  });


// Initialise the listener
init();

apply();
