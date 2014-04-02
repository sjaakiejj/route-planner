console.log("touched");
   //console.log("Test:" + Router.params._id);

Template.vehicleDetail.id = function(){ return Router.current().params._id;};

Template.vehicleDetail.vehicle = function(){
   //console.log("Test:" + Router.params._id);
   var id = Router.current().params._id;
   console.log(id);
   var v_arr = Session.get("json_obj")["Vehicle_"+id];
   var v_info = new Array();
   
   var index=0;
   for(var prop in v_arr){
     console.log("Key: " + prop);
     console.log("Value: " + v_arr[prop]);
     v_info[index++] = {"key":prop, "value": v_arr[prop]};
   }

   return v_info;
   //return null;
}

Template.vehicleDetail.deliveryOrder = function(){
   var id = Router.current().params._id;
   var o_arr = Session.get("json_obj")["Vehicle_"+id+"_order"];
   return o_arr;
//   return [{lat: "t", lon: 't', vol: "2", weight: '2'}];
}

Handlebars.registerHelper('decimal', function(number) {
  //return formatDecimal(number);
  return parseFloat(Math.round(number * 100) / 100).toFixed(2);
});
