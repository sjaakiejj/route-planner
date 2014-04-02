var jar_location = "/home/.../route-planner-1.0-SNAPSHOT-jar-with-dependencies.jar";
var Future = Npm.require('fibers/future');

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
  console.log(args);
}

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
      return fut.wait();
    }
  });
