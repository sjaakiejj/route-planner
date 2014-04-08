var canvas_width = window.innerWidth;	     //canvas_width = 1400;
var canvas_height = window.innerHeight - 100;//canvas_height = 700;
	console.log(canvas_height);

var stage;
var layer;
var ui_layer;
var stats;

var lines = new Array();
var circles = new Array();

var data_box;
var data_text;

function init()
{
  stage = new Kinetic.Stage({
    container: "visualizerDiv",
    width: canvas_width,
    height: canvas_height
  });
  
  stats = new Kinetic.Text({
        x: 10,
        y: 10,
        fontFamily: 'Calibri',
        fontSize: 20,
        text: 'Unsolved',
        fill: 'black'
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

  layer = new Kinetic.Layer();
  ui_layer = new Kinetic.Layer(); 
  
  ui_layer.add(stats);    

  stage.add(layer).add(ui_layer);
  
  kineticVisualise();
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
  var min_lat = 39.0; // TODO: Pull from form 
  var min_lon = 39.0; // TODO: Pull from form 
  var max_lat = 40.0; // TODO: Pull from form 
  var max_lon = 40.0; // TODO: Pull from form 
  
  var lat_range = max_lat - min_lat;
  var lon_range = max_lon - min_lon;
  
  // Multipliers
  var lat_mult  = canvas_width / lat_range;
  var lon_mult  = canvas_height / lon_range;
  
  return { x: (lat - min_lat) * lat_mult,
  	   y: (lon - min_lon) * lon_mult};

}

// Draw the canvas once we get our data back
//Template.visualizer.helpers
function visualise()
{
   var json = Session.get('json_obj');
   var colors = ["#FF0000", "#FFFF00", "#00FF00", "#00FFFF"];
   var radius = 3;
   
   // Don't draw anything
   if(json == undefined) 
     return;
   
   var canvas = document.getElementById('visualizer');
   
   var g = canvas.getContext("2d");
   
   g.clearRect(0,0,canvas.width,canvas.height);
   
   var depot = toCoord( 39.5, 39.5 );
   // Depot
   g.beginPath();
   g.arc(depot.x, depot.y, radius * 2, 0, 2*Math.PI);
   g.closePath();
   g.fillStyle = "#000000";
  //ctx.closePath();
   g.fill();
   
   var id = 0;
   while(json["Vehicle_"+id+"_order"] != undefined)
   {
      var orders = json["Vehicle_"+id+"_order"];
   
      // Set color
      var color = colors[id];
      
      
     // var last_order = json["depot"];
      var last_order = depot;//toCoord( 39.5,39.5); // TODO: Pull from JSON
      var index = 0;
      
      while( orders[ index ] != undefined )
      {
         var order = orders[ index ];
	 var coord = toCoord( orders[index].latitude, orders[index].longitute );
	 
	 // Draw a circle
	 g.beginPath();
	   g.arc(coord.x, coord.y, radius, 0, 2*Math.PI);
	 g.closePath();
	 g.fillStyle = "#000000";
	 g.fill();
	 
	 // Draw a line
	 g.beginPath();
	   g.moveTo(last_order.x,last_order.y);
	   g.lineTo(coord.x, coord.y);
	 g.closePath();
	 g.strokeStyle = color;
	 g.stroke();
	 
	 last_order = coord;
	 
	 index ++;
      }
      
      g.beginPath();
        g.setLineDash([5,10]);
	g.moveTo(last_order.x,last_order.y);
	g.lineTo(depot.x, depot.y);
      g.closePath();
      g.strokeStyle = color;
      g.stroke();
      
      g.setLineDash([]);
      
      id ++;
   }
   
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
       
       // Add vehicle info into box
       data_text = new Kinetic.Text({
		fontFamily: 'Calibry',
		fontSize: 15,
		text: 'Vehicle ' + ind + '\n\n'
		     +'Fuel Efficiency: ' + v['fuel_efficiency'].toFixed(2) + '\n'
		     +'Volume Carried: ' + v['volume_used'].toFixed(0) + '/' + v['volume_capacity'].toFixed(0) + '\n'
		     +'Weight Carried: '  + v['weight_used'].toFixed(0) + '/' + v['weight_capacity'].toFixed(0) + '\n'
		     +'Fuel:   '  + v['fuel_used'].toFixed(0) + '/' + v['fuel_capacity'].toFixed(0),
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
       data_box = new Kinetic.Rect({
     	  x: 100,
	  y: 25,
	  width: 150,
	  height: 90,
	  fill: 'white',
	  stroke: 'black',
	  strokeWidth: 1
       });
       
       // Add order info into box
       data_text = new Kinetic.Text({
		fontFamily: 'Calibry',
		fontSize: 15,
		text: 'Order ' + ind + '\n\n'
		     +'Weight: ' + v['weight'].toFixed(0) + '\n'
		     +'Volume: ' + v['volume'].toFixed(0),
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

function kineticVisualise()
{
   var json = Session.get('json_obj');
   var colors = ["#FF0000", "#FFFF00", "#00FF00", "#00FFFF"];
   var radius = 5;
   
  if(json == undefined || layer == null) 
     return;
  
  // Clear the layer
  layer.removeChildren();
  
  console.log("Now Rendering");
  
  writeStats("Hard/Soft Score: " + json['hard_score'] + '/' + json['soft_score'] + '\n'
  	     +"Total Fuel Used: " + Number(json['fuel_used']).toFixed(2) + " litre");
  
  var depot = toCoord( 39.5, 39.5 );
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
	stroke: colors[id],
	strokeWidth: 2,
	hitFunc: createStrokeHitFunc(line_points)
    });
    
    console.log(line);
    
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
       layer.add(circle);
     }
     
     
     id ++;
  }
  
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
	init();
	kineticVisualise();
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

