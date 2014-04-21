
var solutionUpdateInterval;
var available_forms;

function init()
{
  var problems = ['standard','dbsmorning','dbsafternoon'];
  available_forms = new Object();
  
  for( var i = 0; i < problems.length; i++ )
  {
    var frm = $('#' + problems[i] + '_form');
    available_forms[ problems[i] ] = frm.html();
    console.log(frm);
    frm.remove();
  }
  
  $('#form_container').html( available_forms['standard'] );
}

/*
 * Error and information handling 
 */
function display_panel_message( panel, message )
{
   var info_panel = $('#information_panel');
   
   info_panel.removeClass('warning info error success');
   info_panel.addClass( panel );
   
   // Set to visible
   info_panel.css('visibility', 'visible');
   
   // Set the message
   info_panel.html( message );
   
   // Attach a timer
   window.setTimeout( function(){
      info_panel.html("");
      info_panel.css('visibility', 'hidden');
   }, 3000);
}

function display_warning( warning_message )
{
}

function display_information( info_message )
{
   display_panel_message('info',info_message);
}

function display_settings( mode )
{
   console.log("changing form to " + mode);
   console.log( available_forms );
   form={};
   $.each($('#settings_form').serializeArray(), function() {
          form[this.name] = this.value;
   });
   
   $('#form_container').html( available_forms[ mode ] );
   for (var key in form) {
     if (form.hasOwnProperty(key)) {
       console.log(key);
       console.log($('input[name='+key+']') );
       if( key == 'problem' )
       {
         $('#problem').attr('id','problem_bk');
         $('#problem_bk').val( form['problem'] );
         $('#problem_bk').attr('id','problem');
       }
       else if( $('input[name='+key+']') != undefined )
       {
         console.log('input[name='+key+']');
         $('input[name='+key+']').val( form[key] );
       }
       else if( $('select[name='+key+']') != undefined )
       {
         $('select[name='+key+']').val( form[key] );
       }
     }
   }
   console.log("changed form");
}

function throw_exception( error_message )
{
   window.clearInterval(solutionUpdateInterval);
   display_panel_message('error',error_message);
}

/*
 * Templates 
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
  else if( Session.get("solutionStatus").toLowerCase() == "solved" )
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

var clt_id = 15;

/* 
 * Events
 */


Template.settings.events({
      'submit': function( event ){  
	form={};
	$.each($('#settings_form').serializeArray(), function() {
          form[this.name] = this.value;
        });
	//Session.set('solutionStatus','Solving... Please Wait');
	//Session.set('properties', form);
	Meteor.call('callTest', {api_call: "api_clear_solution",
				 client_id: ''+clt_id}, function(){});
		    
	// Run the algorithm, passing in the properties as we do	    
	Meteor.call('callTest', {api_call: "api_run",
				 body: JSON.stringify(form),
				 client_id: ''+clt_id},
		    function(err,res)
		    {
		      console.log(err);
		      console.log(res);
		    });
	Session.set('properties', form);
	//console.log();
        event.preventDefault();
        event.stopPropagation();
        return false; 
      },
      'click #terminate': function(event){
 	Meteor.call('callTest', {api_call: "api_terminate_early",
				 client_id: ''+clt_id}, function(err,res){});
        console.log("Terminate clicked");
        event.preventDefault();
        event.stopPropagation();
	return false;
      },
      'click #solution': function(event){
 	Meteor.call('callTest', {api_call: "api_get_best_solution",
				 client_id: ''+clt_id}, function(err,res){});
        console.log("Terminate clicked");
        event.preventDefault();
        event.stopPropagation();
	return false;
      },
      'click #testinfo': function(event){
         display_information( "This works" );
      },
      'change #problem': function(event){ 
      	 //console.log( $('#problem').val() );
         display_settings( $('#problem').val() ); 
      }
    });
    

Meteor.ClientCall.methods({
    'receiveMessage': function( json ){
      if(json == null)
        return;
      
      var package = JSON.parse(json);
      var header  = package.header;
      
      if(header == "data")
      {
        if(package.body == "Solved" || package.body == "Unsolved" || package.body == "Solving")
	{
	   if(package.body == "Solved")
	     window.clearInterval(solutionUpdateInterval);
	   
	   Session.set('solutionStatus', package.body);
	}
	else if(package.body == "started")
	{
	   solutionUpdateInterval = window.setInterval(function(){
	  			    Meteor.call('callTest', 
				                { api_call: "api_get_status",
				    		  client_id: ''+clt_id },
						  function(err,res){})
 				     Meteor.call('callTest', 
				                { api_call: "api_get_best_solution",
				                  client_id: ''+clt_id}, 
						  function(err,res){});
				    
				 }, 2000);
	}
	else
	  Session.set("json_obj", JSON.parse(package.body));
      }
      else if(header == "error")
        throw_exception( package.body );
    }
});
Meteor.ClientCall.setClientId(clt_id);

Template.settings.rendered = function(){
init();
};
