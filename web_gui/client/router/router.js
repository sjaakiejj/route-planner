Router.map(function(){
  this.route('home', {path: '/', layoutTemplate:'layout'});
  this.route('vehicleDetail', { 
  	path: '/vehicle/:_id',
	layoutTemplate: 'layout'
  });
});
