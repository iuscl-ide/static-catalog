/**	
╭──────────────────────────────────────╮
│ static-catalog                       │
╰──────────────────────────────────────╯
*/

const StaticCatalog = (() => {
	
	const apply = () => {
		
		console.log("apply");
		loadBlock(0);
	}
	
	const loadBlock = index => {
		
		const xmlHttpRequest = new XMLHttpRequest();
		xmlHttpRequest.onreadystatechange = () => {
			
			if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
				
				const csvString = xmlHttpRequest.responseText;
				//console.log(xmlHttpRequest.responseText);
				
				var results = Papa.parse(csvString);
				
				console.log(results);
				
				var index = 0;
				var concat = "";
				for (let result of results.data) {
					concat = concat + index + " " + results.data[index][1] + "\n";
					index++;
					
				}
				
				console.log(concat);
				
				console.log("done");
				//document.getElementById("demo").innerHTML = this.responseText;
				//console.log(xmlHttpRequest.responseText.slice( 0, 100 ));
				//console.log(this.responseText);
				//console.log("apply2");
			}
		};
		xmlHttpRequest.open("GET", "catalog/block_" + index + ".csv", true);
		xmlHttpRequest.overrideMimeType("text/csv");
		xmlHttpRequest.send();
	}
	
	return {
		apply: apply
	}
})();
