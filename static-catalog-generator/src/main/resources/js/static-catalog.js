/*	
╭──────────────────────────────────────╮
│ static-catalog                       │
╰──────────────────────────────────────╯
*/


const StaticCatalog = (() => {

	const _loadBlock = (searchFilters, searchBlocks, blockIndex, resultLines, resultsCallback) => {
		
		let index = searchBlocks[blockIndex];
		
		
		const xmlHttpRequest = new XMLHttpRequest();
		xmlHttpRequest.onreadystatechange = () => {
			if ((xmlHttpRequest.readyState == 4) && (xmlHttpRequest.status == 200)) {
				
				const csvString = xmlHttpRequest.responseText;
				const results = Papa.parse(csvString, {
					header: true
				});
				
				for (let result of results.data) {

					let ok = true;
					for (searchFilter of searchFilters) {
						let searchFieldName = searchFilter.field;
						resultValue = result[searchFieldName];
						if (!searchFilter.values.includes(resultValue)) {
							ok = false;
							break;
						}
					}
					if (ok) {
						resultLines.push(result);
					}
				}
				
				if (resultLines.length > 9) {
					console.log("Found in block: " + blockIndex + " -> " + index);
					//console.log(resultLines);
					resultsCallback(resultLines.slice(0, 10));
				}
				else {
					blockIndex++;
					if (blockIndex >= searchBlocks.length) {
						console.log("No more blocks");
						resultsCallback(resultLines);
					}
					else {
						_loadBlock(searchFilters, searchBlocks, blockIndex, resultLines, resultsCallback);	
					}
				}
				
				//resultsCallback(results);
				
//				console.log(results);
//				
//				var index = 0;
//				var concat = "";
//				for (let result of results.data) {
//					concat = concat + index + " " + results.data[index][1] + "\n";
//					index++;
//					
//				}
				
				//console.log(concat);
				
//				console.log("done");
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
	
	const _union = (a, b) => {
	    const cache = {};

	    a.forEach(item => cache[item] = item);
	    b.forEach(item => cache[item] = item);

	    return Object.keys(cache).map(key => cache[key]);
	};

	/** */
	const applyFilters = (searchData, resultsCallback) => {
		
		let searchFilters = searchData.searchFieldsValues;
		let nameValuesBlocks = searchData.searchCatalog.nameValuesBlocks;
		let searchBlocks;
		
		noResults:
		for (searchFilter of searchFilters) {
			
			let searchFieldName = searchFilter.field;
			//console.log(searchFieldName);
			
			let valuesBlocks;
			for (searchValue of searchFilter.values) {
				//console.log(searchValue);

				let blocks = nameValuesBlocks[searchFieldName][searchValue];
				//console.log(blocks);
				
				if (!valuesBlocks) {
					valuesBlocks = blocks;
				}
				else {
					valuesBlocks = _union(valuesBlocks, blocks);
				}
			}
			if (!searchBlocks) {
				searchBlocks = valuesBlocks;
			}
			else {
				searchBlocks = searchBlocks.filter(value => valuesBlocks.includes(value));
				if (searchBlocks.length == 0) {
					break noResults;
				}
			}
		}
		
		console.log("Done apply filters, search blocks:");
		console.log(searchBlocks);
		
		let resultLines = [];
		_loadBlock(searchFilters, searchBlocks, 0, resultLines, resultsCallback);
	}
	
	return {
		applyFilters: applyFilters
	}
})();
