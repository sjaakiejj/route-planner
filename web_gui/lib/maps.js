
var PIXELS_PER_LON_DEG = 256/360;
var PIXELS_PER_LON_RAD = 256/(2*Math.PI);

maps =
{
   bound: function(value, opt_min, opt_max) {
    if (opt_min != null) value = Math.max(value, opt_min);
    if (opt_max != null) value = Math.min(value, opt_max);
    return value;
  },

  degreesToRadians: function (deg) {
    return deg * (Math.PI / 180);
  },

  radiansToDegrees: function (rad) {
    return rad / (Math.PI / 180);
  },

  pointToLatLon: function ( x, y )
  {
     var latLon = new Object();

     var lng = ( x - 128 ) / PIXELS_PER_LON_DEG;
     var latRad = ( y - 128 ) / PIXELS_PER_LON_RAD;
     var lat = this.radiansToDegrees(2 * Math.atan(Math.exp(latRad)) - Math.PI / 2);

     latLon.x = lat;
     latLon.y = lng;

     return latLon;
  },

  latLonToPoint: function ( lat, lon )
  {
     var point = new Object();

     point.x = 128 + lon * PIXELS_PER_LON_DEG;

     var siny = this.bound(Math.sin(this.degreesToRadians(lat)), -0.9999,0.9999);
     point.y = 128 + 0.5 * Math.log((1 + siny) / (1 - siny)) * -PIXELS_PER_LON_RAD;



     return point;
  },

  getBounds: function (gmaps_zoom, gmaps_size, gmaps_center)
  {
    var zf = Math.pow(2, gmaps_zoom) * 2;
    var dw = gmaps_size.x / zf;
    var dh = gmaps_size.y / zf;
    var cpx = this.latLonToPoint( gmaps_center.x, gmaps_center.y );

    var bounds = new Object();
    bounds.bl = this.pointToLatLon(cpx.x - dw, cpx.y + dh);
    bounds.tr = this.pointToLatLon(cpx.x + dw, cpx.y - dh);

    return bounds;
  }
}
