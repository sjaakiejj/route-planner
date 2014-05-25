var canvas_width = window.innerWidth;	     //canvas_width = 1400;
var canvas_height = window.innerHeight - 100;//canvas_height = 700;
	
var stage;
var bg_layer;
var layer;
var ui_layer;
var stats;

var lines = new Array();
var circles = new Array();

var data_box;
var data_text;

var background;

var problem_type;
var depot_loc = new Object();
depot_loc.x = 39.5;
depot_loc.y = 39.5;

var cssDep = new Deps.Dependency;

var min_lat;
var min_lon;
var max_lat;
var max_lon;

var gmaps_zoom = 10;
var gmaps_center = new Object();
var gmaps_size = new Object();
gmaps_size.x = canvas_width;
gmaps_size.y = canvas_height;

function setZoom(zoom)
{
  gmaps_zoom = zoom;
  init();
  kineticVisualise();
}

function updateBounds()
{
  var bounds = maps.getBounds(gmaps_zoom, gmaps_size, gmaps_center);
  
  min_lat = bounds.bl.x * -1;
  min_lon = bounds.bl.y;
  max_lat = bounds.tr.x * -1;

  max_lon = bounds.tr.y;
}

var mapsUrl = '';

function init()
{
  if( Session.get('properties') != undefined )
    problem_type = Session.get('properties')['problem'];
  // Initialising data
  stats = new Kinetic.Text({
        x: 10,
        y: 10,
        fontFamily: 'Calibri',
        fontSize: 35,
        text: 'Unsolved',
        fill: 'white',
	stroke: 'black',
	strokeWidth: 1
      });
  data_text = new Kinetic.Text({
        x: 10,
        y: 10,
        fontFamily: 'Calibri',
        fontSize: 24,
        text: 'test',
        fill: 'black'
      });
      
  data_box = new Kinetic.Rect({
     x: 25,
     y: 25,
     width: 100,
     height: 200,
     fill: 'white',
     stroke: 'black',
     strokeWidth: 4
  });
  
  // Setting up the stage
     console.log($('#visualizerDiv').html());
  
  if( stage === undefined ||
  	$('#visualizerDiv').html() === "")
  {
     stage = new Kinetic.Stage({
       container: "visualizerDiv",
       width: canvas_width,
       height: canvas_height
     });
     
     
     if(bg_layer === undefined)
       bg_layer = new Kinetic.Layer(); 
     
     
     // Add the layers to the stage
     stage.add(bg_layer)
     layer = new Kinetic.Layer();
     ui_layer = new Kinetic.Layer(); 
  
     ui_layer.add(stats);
     stage.add(layer).add(ui_layer);
  }
  
  var url = generateMapsURL();
  if(mapsUrl != url)
  {
     bg_layer.removeChildren();
     
     background = new Image();
     background.onload = function() {
       var yoda = new Kinetic.Image({
	 x: 0,
	 y: 0,
	 image: background,
	 width: canvas_width,
	 height: canvas_height
       });

       bg_layer.add(yoda);
       bg_layer.draw();
     };

     background.src = url;
     mapsUrl = url;
     console.log(mapsUrl);
  }
}

function generateMapsURL()
{ 
   var markers = 'color:blue|label:DBS';
   var json = Session.get('json_obj');
   
   var new_dpt = new Object();
   var order_markers = new Array();
   new_dpt.x = 0;
   new_dpt.y = 0;
   
   var count = 0;
   
   if( json != undefined )
   {
      var i = 0;
      
      while( json['Vehicle_'+i+"_order"] != undefined )
      {
         var orders = json['Vehicle_'+i+'_order'];
	 
	 for( var j = 0; j < orders.length; j++)
	 {
	  // markers += '|' + orders[j].latitude.toFixed(4) + ',' + orders[j].longitute.toFixed(4);
	   order_markers.push(orders[j].latitude.toFixed(4) + ',' + orders[j].longitute.toFixed(4));
	   
	   new_dpt.x += orders[j].latitude;
	   new_dpt.y += orders[j].longitute;
	   
	   count ++;
	 }
	 i++;
      }
      
      order_markers.sort();
      
      for(var i = 0; i < order_markers.length; i++)
      {
      
         markers += "|" + order_markers[i];
      }
      
      depot_loc.x = (new_dpt.x / count).toFixed(4);
      depot_loc.y = (new_dpt.y / count).toFixed(4);
      
      gmaps_center.x = depot_loc.x;
      gmaps_center.y = depot_loc.y; 
      updateBounds();
   }
   
   var scale  = 2;
   var center = depot_loc.x + ',' + depot_loc.y;
   var zoom   = gmaps_zoom;
   var size   = parseInt(canvas_width/scale) + 'x' + parseInt(canvas_height/scale);
   var sensor = 'false';
     
   var mapsurl = 'http://maps.googleapis.com/maps/api/staticmap?center=' + center;
   mapsurl += '&zoom=' + zoom;
   mapsurl += '&size=' + size;
   mapsurl += '&sensor=' + sensor;
   mapsurl += '&scale=' + scale;
   mapsurl += '&markers=' + markers;
   
   return mapsurl;
}



      
function writeStats(message)
{
  // ui_layer.clear();
   stats.setText(message);
   ui_layer.draw();
}



Template.visualizer.canvasWidth = function(){ 
	return canvas_width; 
};

Template.visualizer.canvasHeight = function(){ 
	return canvas_height; 
};

function toCoord(lat,lon)
{ 
  var props = Session.get('properties');
  //var min_lat;
  //var min_lon;
  //var max_lat;
  //var max_lon;
  var lat_range = max_lat - min_lat;
  var lon_range = max_lon - min_lon;
  
  lon_range = lon_range / 2;
  lat_range = lat_range / 2;
  
  // Multipliers
  var lat_mult  = canvas_height / lat_range;
  var lon_mult  = canvas_width / lon_range;
  
  var point = new Object();
  
  point.y = canvas_height - (lat - min_lat) * lat_mult;
  point.x = (lon - min_lon) * lon_mult;
  
  point.x *= 1 + (1/9);
  
  point.x -= (canvas_width/2)*(1 + (2/9));
  point.y += canvas_height/2;
  
  return point;
}

function milliToTime(milli)
{
   var hour =((milli/3600000 - 0.5).toFixed(0) % 24);
   var min = ((milli/60000 - 0.5).toFixed(0) % 60);
   
   
   if(hour < 10)
     hour = "0" + hour;
   if(min < 10)
     min = "0" + min;

   var str = hour + ":" + min;
   //  str += "0";
   
   
   return str;
}

function createCallback(obj, ind, v)
{
   return function(){
     
     data_box.remove();
     data_text.remove();
     if( obj == "Vehicle" )
     {
       data_box = new Kinetic.Rect({
     	  x: 300,
	  y: 25,
	  width: 210,
	  height: 110,
	  fill: 'white',
	  stroke: 'black',
	  strokeWidth: 1
       });
       
       var volumeCap = v['volume_capacity'].toFixed(0);
       var weightCap = v['weight_capacity'].toFixed(0);
       var fuelCap   = v['fuel_capacity'].toFixed(0);
       
       if(volumeCap > 999999)
         volumeCap = "∞";
       if(weightCap > 999999)
         weightCap = "∞";
       if(fuelCap > 999999)
         fuelCap = "∞";
       
       var vehicle_type = "";
					 
       if( problem_type == "dbsmorning" 
       		|| problem_type == "dbsafternoon" )
       {
         vehicle_type = ": " + v['type'];
       }
       
       
       var vehicleInfoText = 'Vehicle '  + ind + vehicle_type + '\n\n'
		     +'Fuel Efficiency: '+ v['fuel_efficiency'].toFixed(2) + '\n'
		     +'Volume Carried: ' + v['volume_used'].toFixed(0) + '/' 
		     			 + volumeCap + '\n'
		     +'Weight Carried: ' + v['weight_used'].toFixed(0) + '/'
		     			 + weightCap + '\n'
		     +'Fuel:   ' 	 + v['fuel_used'].toFixed(0) + '/' 
		     			 + fuelCap;
					 
       // Add vehicle info into box
       data_text = new Kinetic.Text({
		fontFamily: 'Calibry',
		fontSize: 15,
		text: vehicleInfoText,
		fill: 'black'});
       
       ui_layer.add(data_box);
       ui_layer.add(data_text);
       
       stage.on('mousemove', function(){ 
       		var x = stage.getPointerPosition().x + 10;
		var y = stage.getPointerPosition().y + 10;
       		data_text.setX(x + 10);
		data_text.setY(y + 10);
       		data_box.setX(x);
		data_box.setY(y);
		ui_layer.draw();
  	});
	
     }
     else if( obj == "Order" )
     {
       
       var orderInfoText =  'Order ' + ind + '\n\n'
		    	   +'Weight: ' + v['weight'].toFixed(0) + '\n'
		    	   +'Volume: ' + v['volume'].toFixed(0);
			   
       var box_height = 90;
       var box_width = 120;
       if( problem_type == "dbsmorning" 
       		|| problem_type == "dbsafternoon" )
       {
       
          orderInfoText += '\nOpening Time: ' + milliToTime(v['opening_time'])
	  		  +'\nLatest Pickup: ' + milliToTime(v['latest_pickup'])
			  +'\nPicked up at: ' + milliToTime(v['picked_up']);
			  
	  // Make space for 3 lines
	  box_height += 45;
	  box_width  += 60;
       }
       
       data_box = new Kinetic.Rect({
     	  x: 100,
	  y: 25,
	  width: box_width,
	  height: box_height,
	  fill: 'white',
	  stroke: 'black',
	  strokeWidth: 1
       });
       
       
       // Add order info into box
       data_text = new Kinetic.Text({
		fontFamily: 'Calibry',
		fontSize: 15,
		text: orderInfoText,
		fill: 'black'});
       
       ui_layer.add(data_box);
       ui_layer.add(data_text);
       
       stage.on('mousemove', function(){ 
       		//var coord = toCoord( orders[index].latitude, orders[index].longitute );
       		var x = stage.getPointerPosition().x + 10;
		var y = stage.getPointerPosition().y + 10;
		
		if(data_box.getWidth() + x > canvas_width)
		{
		   x = x - 20 - data_box.getWidth();
		}
		
		if(data_box.getHeight() + y > canvas_height)
		{
		   y = y - 20 - data_box.getHeight();
		}
		
       		data_text.setX(x + 10);
		data_text.setY(y + 10);
       		data_box.setX(x);
		data_box.setY(y);
		ui_layer.draw();
  	});
     }
     else
     {
       ui_layer.draw();
     }
     
    // ui_layer.draw();
     
   };
}

function createStrokeHitFunc(lines)
{
   return function(context) {
          context.beginPath();
          //context.arc(0, 0, this.getOuterRadius() + 10, 0, Math.PI * 2, true);
          
	  context.moveTo( lines[0], lines[1] );
	  var i = 0; 
	  for ( i = 2; i < lines.length; i+=2 )
	    context.lineTo( lines[i], lines[i+1] );
	  
	  context.lineTo( lines[0], lines[1] );
	  
	  context.closePath();
	  
	  this.setStrokeWidth(20);
	  context.strokeShape(this);
	  this.setStrokeWidth(3);
        }
}

function createCircleHitFunc(coord)
{
   return function(context) {
          context.beginPath();
          context.arc(0,0,//coord.x, coord.y, 
	  		10, 0, Math.PI * 2, true);
	  context.closePath();
	  context.fillStrokeShape(this);
	  
   }
}

// This function was taken from StackOverflow, written by Anatoliy
function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.round(Math.random() * 15)];
    }
    return color;
}

function kineticVisualise()
{
   var json = Session.get('json_obj');
   
   var colors = ["#FF0000", "#FFFF00", "#00FF00", "#00FFFF"];
   var radius = 5;
   
  if(json == undefined || layer == null) 
     return;
  
  //init();
  // Clear the layer
  
  layer.remove();
  ui_layer.remove();
  
  layer = new Kinetic.Layer();
  ui_layer = new Kinetic.Layer();
  
  ui_layer.add(stats);
  stage.add(layer).add(ui_layer);
  
  //layer.removeChildren();
  //ui_layer.removeChildren();
  
  console.log("Now Rendering");
  
  writeStats("Hard/Soft Score: " + json['hard_score'] + '/' + json['soft_score'] + '\n'
  	     +"Total Fuel Used: " + Number(json['fuel_used']).toFixed(2) + " litre");
  
  var props = Session.get('properties');
  var centerX = Number(props['min_lat']) + ((Number(props['max_lat']) - 
  					     Number(props['min_lat'])) / 2.0);
  var centerY = Number(props['min_lon']) + ((Number(props['max_lon']) - 
  					     Number(props['min_lon'])) / 2.0);
  var depot = toCoord( depot_loc.x, depot_loc.y );
  var id = 0;
  while(json["Vehicle_"+id+"_order"] != undefined)
  {
    var line_points = new Array();
    var orders = json["Vehicle_"+id+"_order"];

    // Set color
    var color = colors[id];


   // var last_order = json["depot"];
    var last_order = depot;//toCoord( 39.5,39.5); // TODO: Pull from JSON
    var index = 0;
    
    line_points[0] = depot.x;
    line_points[1] = depot.y;
    
    while( orders[ index ] != undefined )
    {
       var order = orders[ index ];
       var coord = toCoord( orders[index].latitude, orders[index].longitute );
       
       // Remember line points
       line_points[ 2 + (index * 2)] = coord.x;
       line_points[ 3 + (index * 2)] = coord.y;
       
       //
       last_order = coord;
       index ++;
    }
    
    var line = new Kinetic.Line({
    	points: line_points,
	stroke: getRandomColor(),
	shadowColor: "#000000",
	shadowBlur: 7,
	strokeWidth: 2,
	hitFunc: createStrokeHitFunc(line_points)
    });
    
    
    var final_line = new Kinetic.Line({
    	points: [ last_order.x, last_order.y, depot.x, depot.y ],
	stroke: 'blue',
	strokeWidth: 2,
	dash: [5,10]
     });
     
     
     line.on('mouseover', createCallback("Vehicle", id, json["Vehicle_"+id]));
     line.on('mouseout', createCallback("Nothing",""));
     
     layer.add(line);
     layer.add(final_line);
     
     var i =0;
     for( i = 2; i < line_points.length; i+=2 )
     {

       var circle = new Kinetic.Circle({
       	  x: line_points[i],
	  y: line_points[i+1],
	  radius: radius,
	  strokeWidth: 4,
	  fill: 'black',
	  hitFunc: createCircleHitFunc(coord)
       });
       
       var orders = json["Vehicle_"+id+"_order"];
       circle.on('mouseover', createCallback("Order", i/2, orders[i/2-1]));
       circle.on('mouseout', createCallback("Nothing",""));
       layer.add(circle);
     }
     
     id ++;
  }
  
  var depotCircle = new Kinetic.Circle({
     x: depot.x,
     y: depot.y,
     radius: radius*2,
     fill: 'white',
     stroke: 'black'
  });
  
  layer.add(depotCircle);
  
  console.log("Adding to stage");
  stage.draw();
  
  console.log("Done");
  console.log(stage);
}

var dom_ready = false;

Template.visualizer.events({
   'change #maps_zoom': function(){
      var val = $('#maps_zoom').val();
      setZoom(val);
   }
});

//Template.visualizer.rendered = function(){ init(); };   

//if(Session.get("json_obj") != undefined)
visualizer = {
  jsonObj: null,
  getJson: function () {
      cssDep.depend()
      return this.jsonObj;
  },
  setJson: function (w) {
      if(w!=this.jsonObj) {
          this.jsonObj = w;
          cssDep.changed();
	  
	  // Visualise
          init();
	  kineticVisualise();
      }
  }
}


Template.visualizer.rendered = function(){
	/*
	Deps.autorun(function(){
	   Session.get("json_obj");
	   init();
	   kineticVisualise();
	});*/
	console.log("rendered");
	dom_ready = true;
};


// What stage are we currently in?
Template.visualizer.solutionStatus = function(){  
  var str = "";
  if( Session.get("solutionStatus") == undefined )
    str="<span style='color:red'>unsolved</span>";
  else if( Session.get("solutionStatus") == "solved" )
    str="<span style='color:green'>solved</span>";
  else
    str=Session.get("solutionStatus");
  
  return str;
}
