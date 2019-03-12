
var initPage = () => {
	
	$("#btnHere").click(() => {
	
		$.getJSON("data/data1.json").done( jsonData => {
			alert(jsonData);
		});		
//		$.ajax({
//			  url: "data/data1.json",
//			  cache: false,
//			  dataType: "text"
//			}).done( json => {
//				  alert(json);
//			    // $( "#results" ).append( html );
//			  });
//		
		
//		// This is the client-side script.
//
//		// Initialize the HTTP request.
//		var xhr = new XMLHttpRequest();
//		xhr.open('GET', "data/data1.json");
//
//		// Track the state changes of the request.
//		xhr.onreadystatechange = function () {
//			var DONE = 4; // readyState 4 means the request is done.
//			var OK = 200; // status 200 is a successful return.
//			if (xhr.readyState === DONE) {
//				if (xhr.status === OK) {
//					console.log(xhr.responseText); // 'This is the output.'
//					alert(xhr.responseText);
//				} else {
//					console.log('Error: ' + xhr.status); // An error occurred
//															// during the
//															// request.
//				}
//			}
//		};
//
//		// Send the request to send-ajax-data.php
//		xhr.send(null);
//	  // alert( "Handler for .click() called." );
	});
}

