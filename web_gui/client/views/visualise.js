var canvas_width = window.innerWidth;	     //canvas_width = 1400;
var canvas_height = window.innerHeight - 100;//canvas_height = 700;
	console.log(canvas_height);

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


var min_lat;
var min_lon;
var max_lat;
var max_lon;

var gmaps_zoom = 11;
var gmaps_center = new Object();
var gmaps_size = new Object();
gmaps_size.x = canvas_width;
gmaps_size.y = canvas_height;

function bound(value, opt_min, opt_max) {
  if (opt_min != null) value = Math.max(value, opt_min);
  if (opt_max != null) value = Math.min(value, opt_max);
  return value;
}

function degreesToRadians(deg) {
  return deg * (Math.PI / 180);
}

function radiansToDegrees(rad) {
  return rad / (Math.PI / 180);
}

var PIXELS_PER_LON_DEG = 256/360;
var PIXELS_PER_LON_RAD = 256/(2*Math.PI);

function pointToLatLon( x, y )
{
   var latLon = new Object();
   
   var lng = ( x - 128 ) / PIXELS_PER_LON_DEG;
   var latRad = ( y - 128 ) / PIXELS_PER_LON_RAD;
   var lat = radiansToDegrees(2 * Math.atan(Math.exp(latRad)) - Math.PI / 2);
   
   latLon.x = lat;
   latLon.y = lng;
   
   return latLon;
}

function latLonToPoint( lat, lon )
{
   var point = new Object();
   
   point.x = 128 + lon * PIXELS_PER_LON_DEG;
   
   var siny = bound(Math.sin(degreesToRadians(lat)), -0.9999,0.9999);
   point.y = 128 + 0.5 * Math.log((1 + siny) / (1 - siny)) * -PIXELS_PER_LON_RAD;
   
   
   
   return point;
}

function updateBounds()
{
  var zf = Math.pow(2, gmaps_zoom) * 2;
  var dw = gmaps_size.x / zf;
  var dh = gmaps_size.y / zf;
  var cpx = latLonToPoint( gmaps_center.x, gmaps_center.y );
  
  var bounds = new Object();
  bounds.bl = pointToLatLon(cpx.x - dw, cpx.y + dh);
  bounds.tr = pointToLatLon(cpx.x + dw, cpx.y - dh);
  
  console.log("New Bounds: ");
  console.log(bounds);
  
  min_lat = bounds.bl.x * -1;
  min_lon = bounds.bl.y;
  max_lat = bounds.tr.x * -1;
  max_lon = bounds.tr.y;
}

var mapsUrl = '';

function init()
{
  stage = new Kinetic.Stage({
    container: "visualizerDiv",
    width: canvas_width,
    height: canvas_height
  });

  layer = new Kinetic.Layer();
  ui_layer = new Kinetic.Layer(); 
  bg_layer = new Kinetic.Layer(); 

 
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
  
  var url = generateMapsURL();
  if(mapsUrl != url)
  {
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

     background.src = generateMapsURL();
     mapsUrl = url;
  }
  
  ui_layer.add(stats);    

  stage.add(bg_layer).add(layer).add(ui_layer);
  
  if( Session.get('properties') != undefined )
    problem_type = Session.get('properties')['problem'];
//  kineticVisualise();
}

function generateMapsURL()
{ 
   var markers = 'color:blue|label:DBS';
   var json = Session.get('json_obj');
   
   var new_dpt = new Object();
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
	   markers += '|' + orders[j].latitude + ',' + orders[j].longitute;
	   
	   new_dpt.x += orders[j].latitude;
	   new_dpt.y += orders[j].longitute;
	   
	   count ++;
	 }
	 i++;
      }
      
      depot_loc.x = new_dpt.x / count;
      depot_loc.y = new_dpt.y / count;
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
   
   
   
   
   console.log(mapsurl);
   
   return mapsurl;
}


Template.visualizer.rendered = function(){ init(); };   

      
function writeStats(message)
{
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
  if(props['problem'] == "standard")
  {
   min_lat = props['min_lat']; 
   min_lon = props['min_lon']; 
   max_lat = props['max_lat']; 
   max_lon = props['max_lon']; 
  }
  else
  {
//   min_lat= 1;    
//   min_lon= 103;  
//   max_lat= 2;    
//   max_lon= 104;  
  }
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
   console.log(json);
  // Clear the layer
  layer.removeChildren();
  
  console.log("Now Rendering");
  
  writeStats("Hard/Soft Score: " + json['hard_score'] + '/' + json['soft_score'] + '\n'
  	     +"Total Fuel Used: " + Number(json['fuel_used']).toFixed(2) + " litre");
  
  var props = Session.get('properties');
  var centerX = Number(props['min_lat']) + ((Number(props['max_lat']) - 
  					     Number(props['min_lat'])) / 2.0);
  var centerY = Number(props['min_lon']) + ((Number(props['max_lon']) - 
  					     Number(props['min_lon'])) / 2.0);
  
  console.log(Session.get('properties'));
  console.log(centerX);
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
       
       // Draw the circle
       /*
       var circle = new Kinetic.Circle({
       	  x: coord.x,
	  y: coord.y,
	  radius: radius,
	  strokeWidth: 4,
	  fill: 'black',
	  hitFunc: createCircleHitFunc(coord)
       });
       
       circle.on('mouseover', createCallback("Order", index));
       circle.on('mouseout', createCallback("Nothing",""));*/
       
      // circles.push(circle);
      // circles[ circles.length-1 ].on('mouseover', function(){ console.log(index) });
       
       last_order = coord;
       index ++;
    //   layer.add(circle);
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
     
    // lines.push(line);
     
     
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
 // stage.add(layer);
  stage.draw();
  
  console.log("Done");
}

var dom_ready = false;

//if(Session.get("json_obj") != undefined)
Template.visualizer.rendered = function(){
	//canvas_width = window.innerWidth;
	//canvas_height = window.innerHeight - 200;
	//visualise(); 
	Deps.autorun(function(){
	   console.log(Session.get('json_obj'));
	   init();
	   kineticVisualise();
	});
//	init();
//	kineticVisualise();
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
  
  // Update the visualiser
  if( dom_ready == true && Session.get("solutionStatus") == "solved")
  {
   // visualise(); 
    kineticVisualise(); 
    dom_ready = false;
  }
  return str;
}

/*  
if(Session.get('solutionStatus') == "solved"
	&& dom_ready)
  visualise();
  */

