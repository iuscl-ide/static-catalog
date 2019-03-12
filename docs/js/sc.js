/** static-catalog */

var scInitPage = () => {
	
	$("#btnHere").click(() => {
		
		$.getJSON("data/data1.json").done( jsonData => {
			alert(jsonData);
		}); 
	});
}