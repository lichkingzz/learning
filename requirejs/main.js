require.config({
	paths : {
		'modules/a' : 'b'
	}
});
require(['modules/a'],function(a){
	a.hello();
});