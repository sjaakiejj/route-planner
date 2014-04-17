Router.map(function(){
  this.route('home', {path: '/', layoutTemplate:'layout'});
  this.route('vehicleDetail', { 
  	path: '/vehicle/:_id',
	layoutTemplate: 'layout'
  });
  this.route('visualizer', {
  	path:'/Visualise',
	layoutTemplate: 'layout'
  });
  this.route('dist_mat', {
  	path:'/DistMat',
	layoutTemplate: 'layout'
  });
});
