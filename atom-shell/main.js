var fs = require( 'fs' );


var currentPath = [];
function openDir( path ) {
  currentPath.push( path );
  fs.readdir( currentPath.join( '/' ), function( err, files ) {
    var ul = $( '#files' ).empty();
    var pathBar = $( '#path' );

    for ( var i = 0 , len = files.length; i < len; i++ ) {
      currentPath.push( files[ i ] );
      var newPath = currentPath.join( '/' );
      currentPath.pop();
      console.log( newPath );
      try {
        var stats = fs.statSync( newPath );
        var fileItem = document.createElement( 'li' );
        if ( stats.isDirectory() ) {
          $( fileItem ).text( files[ i ] ).addClass( 'dir' ).appendTo( ul );
        } else {
          $( fileItem ).text( files[ i ] ).appendTo( ul );
        }
      } catch (error) {}
      console.log( stats );
    }
  });
}



$(function() {
openDir( 'e:' );
$( '#files' ).on( 'click', '.dir', function() {
  var $this = $( this );
  var text = $this.text();
  openDir( text );
  $('#path').text(currentPath.join('/'));
});
});