
var fs = Npm.require('fs');

function concatJson()
{
   var fileLoc = '../client/app/json/';
 //  var files   = [ "all_to_4_25", "all_to_8_25", "all_to_16_25", "all_to_20_25",
 //  		   "all_to_24_25", "all_to_25_25" ];
   var files = fs.readdirSync( fileLoc );	  
 
   var result = new Object();
   console.log("Reading");
   for(var i = 0; i < files.length; i++)
   {
//      var fd = fs.openSync(files[i],'r');
      var fileContent = fs.readFileSync(fileLoc + files[i]);
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

Meteor.methods({
	'getJson': function(){
		return concatJson();
	}
});
