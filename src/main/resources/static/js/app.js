
$( document ).ready( function() {

	setTimeout( function() {
		
		$( '.ajaxWordcount' ).each( function() {
			
			var element = $( this );
			element.html( '<i class="fa fa-spinner fa-spin"></i>' );
			
			$.ajax( {
				url: '/rest/1.0/stories/' + element.data( 'key' ) + '/words',
				dataType: 'json',
				cache: false
			}).done( function( data ) {
				element.html( data.words );
			}).fail( function( jqXHR, textStatus, errorThrown ) {
//				alert( jqXHR.responseText );
				var responseObj = $.parseJSON( jqXHR.responseText );
				element.html( '<i class="fa fa-exclamation-circle" style="color: red;" title="' + responseObj.message + '"></i>' );
			});
		});
	}, 5000 );
});
