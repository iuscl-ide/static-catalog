/*	
╭──────────────────────────────────────╮
│ static-catalog                       │
╰──────────────────────────────────────╯
*/

const StaticCatalog = (() => {

	/** https://gist.github.com/lovasoa/3361645 */
	function array_intersect() {
		var i, all, shortest, nShortest, n, len, ret = [], obj = {}, nOthers;
		nOthers = arguments.length - 1;
		nShortest = arguments[0].length;
		shortest = 0;
		for (i = 0; i <= nOthers; i++) {
			n = arguments[i].length;
			if (n < nShortest) {
				shortest = i;
				nShortest = n;
			}
		}

		for (i = 0; i <= nOthers; i++) {
			n = (i === shortest) ? 0 : (i || shortest); // Read the shortest array
														// first. Read the first
														// array instead of the
														// shortest
			len = arguments[n].length;
			for (var j = 0; j < len; j++) {
				var elem = arguments[n][j];
				if (obj[elem] === i - 1) {
					if (i === nOthers) {
						ret.push(elem);
						obj[elem] = 0;
					} else {
						obj[elem] = i;
					}
				} else if (i === 0) {
					obj[elem] = 0;
				}
			}
		}
		return ret;
	}

//	/** Union */
//	const _indexArraysUnion = (arr1, arr2) => {
//		
//		arr1.push()
//	    const cache = {};
//
//	    a.forEach(item => cache[item] = item);
//	    b.forEach(item => cache[item] = item);
//
//	    return Object.keys(cache).map(key => cache[key]);
//	};


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

	const _loadIndex = (indexFiles, indexIndex, searchData, indexValues, resultsCallback) => {
		
		const xmlHttpRequest = new XMLHttpRequest();
		xmlHttpRequest.onreadystatechange = () => {
			if ((xmlHttpRequest.readyState == 4) && (xmlHttpRequest.status == 200)) {
				
				const jsonString = xmlHttpRequest.responseText;
				const results = JSON.parse(jsonString);
				
				console.log(results);
				
				let indexFieldValues = [];
				for (value of searchData.searchFieldsValues[indexIndex].values) {
					console.log(value);
					indexFieldValues = indexFieldValues.concat(results[value]);
				}
				if (indexIndex === 0) {
					indexValues = indexFieldValues;
				}
				else {
					indexValues = array_intersect(indexValues, indexFieldValues);
				}
				
				//indexValues[indexIndex] = indexFieldValues;
				console.log(indexValues);

				indexIndex = indexIndex + 1;
				if (indexIndex < indexFiles.length) {
					_loadIndex(indexFiles, indexIndex, searchData, indexValues, resultsCallback);
				}
				else {
					
					
				}
	
				
//				for (let result of results.data) {
//
//					let ok = true;
//					for (searchFilter of searchFilters) {
//						let searchFieldName = searchFilter.field;
//						resultValue = result[searchFieldName];
//						if (!searchFilter.values.includes(resultValue)) {
//							ok = false;
//							break;
//						}
//					}
//					if (ok) {
//						resultLines.push(result);
//					}
//				}
//				
//				if (resultLines.length > 9) {
//					console.log("Found in block: " + blockIndex + " -> " + index);
//					//console.log(resultLines);
//					resultsCallback(resultLines.slice(0, 10));
//				}
//				else {
//					blockIndex++;
//					if (blockIndex >= searchBlocks.length) {
//						console.log("No more blocks");
//						resultsCallback(resultLines);
//					}
//					else {
//						_loadBlock(searchFilters, searchBlocks, blockIndex, resultLines, resultsCallback);	
//					}
//				}
				
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
		xmlHttpRequest.open("GET", indexFiles[indexIndex], true);
		xmlHttpRequest.overrideMimeType("text/json");
		xmlHttpRequest.send();
	}

	/** */
	const applyFilters = (searchData, resultsCallback) => {
		
		let searchFilters = searchData.searchFieldsValues;
		let filterNameIndex = searchData.filterNameIndex;
		
		let indexFiles = [];
		for (searchFilter of searchFilters) {
			indexFiles.push("static-catalog-index-" + filterNameIndex[searchFilter.field] + ".json"); 
		}
		console.log(indexFiles);
		
		_loadIndex(indexFiles, 0, searchData, [], resultsCallback);
		
//		let nameValuesBlocks = searchData.searchCatalog.nameValuesBlocks;
//		let searchBlocks;
//		
//		noResults:
//		for (searchFilter of searchFilters) {
//			
//			let searchFieldName = searchFilter.field;
//			//console.log(searchFieldName);
//			
//			let valuesBlocks;
//			for (searchValue of searchFilter.values) {
//				//console.log(searchValue);
//
//				let blocks = nameValuesBlocks[searchFieldName][searchValue];
//				//console.log(blocks);
//				
//				if (!valuesBlocks) {
//					valuesBlocks = blocks;
//				}
//				else {
//					//valuesBlocks = _union(valuesBlocks, blocks);
//					valuesBlocks = valuesBlocks.concat(blocks);
//				}
//			}
//			if (!searchBlocks) {
//				searchBlocks = valuesBlocks;
//			}
//			else {
//				console.log("Start intersection...");
//				searchBlocks = array_intersect(searchBlocks, valuesBlocks);
//					
//					//searchBlocks.filter(value => valuesBlocks.includes(value));
//				console.log("Done intersection.");
//				if (searchBlocks.length == 0) {
//					break noResults;
//				}
//			}
//		}
//		
//		console.log("Done apply filters, search blocks:");
//		console.log(searchBlocks);
//		
//		let resultLines = [];
//		_loadBlock(searchFilters, searchBlocks, 0, resultLines, resultsCallback);
	}
	
	return {
		applyFilters: applyFilters
	}
})();
