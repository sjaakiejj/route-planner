var fs;

if(Meteor.isServer)
  fs = Npm.require('fs');

function concatJson()
{
   var fileLoc = "json/";
 //  var files   = [ "all_to_4_25", "all_to_8_25", "all_to_16_25", "all_to_20_25",
 //  		   "all_to_24_25", "all_to_25_25" ];
   var files = fs.readdirSync('/json');	  
   
   var result = new Object();
   console.log("Reading");
   for(var i = 0; i < files.length; i++)
   {
//      var fd = fs.openSync(files[i],'r');
      var fileContent = fs.readFileSync(files[0]);
      var json = JSON.parse(fileContent);
      
      if(result.origin_addresses == undefined)
      {
        result = json;
      }
      else
      {
        // Add the new destination addresses
        for(var j = 0; j < json.destination_addresses.length; j++)
          result.destination_addresses.push( json.destination_addresses[j] );
	
	// For each row (origin address)
	for(var j = 0; j < result.origin_addresses.length; j++) 
	{
	  var row_elements = json.rows[j].elements;
	  
	  for(var k = 0; k < json.destination_addresses.length; k++)
	    result.rows[j].elements.push(row_elements[k]);
	}
      }
   }
   
   return result; 
   
}



function generateQuery()
{
   
   var from = $('textarea#dist_from').val();
   var to   = $('textarea#dist_to').val();
   
   var fromLines = from.split('\n');
   var toLines   = to.split('\n');
   
   var origins = 'origins=';
   for(var i = 0; i < fromLines.length; i++)
   {
     origins = origins + fromLines[i];
     
     if( i+1 < fromLines.length )
       origins = origins + '|';
   }
   
   var destinations = 'destinations=';
   for(var i = 0; i < toLines.length; i++)
   {
     destinations = destinations + toLines[i];
     
     if( i+1 < toLines.length )
       destinations = destinations + '|';
   }
   var url = 'http://maps.googleapis.com/maps/api/distancematrix/json?';
   
   url += origins;
   url += '&' + destinations;
   url += '&mode=driving&sensor=false';
   
   console.log(url);
   return url;
}

Template.dist_mat.events({
   'click #get_json': function( e ){ 
   	$('#json_output').html(generateQuery()); 
   },
   'click #assemble_json': function( e ){
   	Meteor.call('getJson', function(err,res){
	        $('#json_output').html(JSON.stringify(res));
		console.log(err + ":::" + res);
	});
   }
});
