/*
Meteor.call('callPlanRoute', function (err, res) {
  if(err){
    console.log(err);
    Session.set('response', err);
  } else {
    Session.set('response', res);
  }
});

Template.test.greeting = function() {
  return Session.get("response");
}
*/

Template.vehicleList.vehicles = function(){
  var res = Session.get('json_obj');
  if(res == undefined) 
    return;
  
  var ind = 0;
  var arr = new Array();
  while( res["Vehicle_"+ind] != undefined )
  {
   arr[ind] = {id:ind};
   ind++;
  }
    
  return arr;
};

Template.score.solutionStatus = function(){
  var str = "";
  if( Session.get("solutionStatus") == undefined )
    str="<span style='color:red'>unsolved</span>";
  else if( Session.get("solutionStatus") == "solved" )
    str="<span style='color:green'>solved</span>";
  else
    str=Session.get("solutionStatus");
  return str;
}

Template.score.hardScore = function(){
  if(Session.get("json_obj") == undefined) return;
  return Session.get("json_obj")['hard_score'];
}
Template.score.softScore = function(){
  if(Session.get("json_obj") == undefined) return;
  return Session.get("json_obj")['soft_score'];
}
Template.score.fuelUsed = function(){
  if(Session.get("json_obj") == undefined) return;
  return Session.get("json_obj")['fuel_used'];
}
Template.score.distanceTravelled = function(){
  if(Session.get("json_obj") == undefined) return;
  return Session.get("json_obj")['distance_travelled'];
}

Template.settings.events({
      'submit': function( event ){  
	form={};
	$.each($('#settings_form').serializeArray(), function() {
          form[this.name] = this.value;
        });
	Session.set('solutionStatus','Solving... Please Wait');
	
	Meteor.call('callPlanRoute', "[" + JSON.stringify(form) + "]", 
	function (err, res) {
          if(err){
   	    console.log(err);
   	    Session.set('response', err);
 	  } else {
 	   // Session.set('response', res);
	    Session.set('json_obj', JSON.parse(res));
	    Session.set('solutionStatus','solved');
 	  }
	});
	console.log();
        event.preventDefault();
        event.stopPropagation();
        return false; 
      }
    });
