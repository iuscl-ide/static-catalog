/*	
╭──────────────────────────────────────╮
│ static-catalog                       │
╰──────────────────────────────────────╯
*/

/** static-catalog engine */ 
const StaticCatalog = (() => {

	/* _catalog/static-catalog.json */
	var filterNameIndex;
	var filterNameValueIndex;
	var indexSplitType;
	var totalLinesCount;
	var blockLinesCount;
	var indexLinesModulo;

	/** debug */
	var isDebug = true
	
	/** debug console */
	const c = (message, object) => {
		
		if (isDebug) {
			console.log(message);
			console.log(object);
		}
	}
	
	/** Initialize */
	const _initialize = () => {
		
		const xmlHttpRequest = new XMLHttpRequest();
		xmlHttpRequest.onreadystatechange = () => {
			
			if ((xmlHttpRequest.readyState == 4) && (xmlHttpRequest.status == 200)) {
				let contents = JSON.parse(xmlHttpRequest.responseText);
				c("contents", contents);
				
				filterNameIndex = contents.filterNameIndex;
				filterNameValueIndex = contents.filterNameValueIndex;
				indexSplitType = contents.indexSplitType;
				totalLinesCount = contents.totalLinesCount;
				blockLinesCount = contents.blockLinesCount;
				indexLinesModulo = contents.indexLinesModulo;
			}
		};
		xmlHttpRequest.open("GET", "_catalog/static-catalog.json", true);
		xmlHttpRequest.overrideMimeType("text/json");
		xmlHttpRequest.send();
	};
	
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

	/** Modulo */
	const _getStartLine = (line) => {
		
		let value = Math.trunc(line / indexLinesModulo);
		if (value === 0) {
			value = line % indexLinesModulo;
		}

		return value;
	}
	
	/** Add elements */
	const _addUntil = (arraySrc, arrayDest, startIndex, limitValue) => {
		
		let srcElement = arraySrc[startIndex];
		let value = _getStartLine(srcElement);
		while (value < limitValue) {
			arrayDest.push(srcElement);
			
			startIndex++;
			if (startIndex === arraySrc.length) {
				return startIndex;
			}
			srcElement = arraySrc[startIndex];
			value = _getStartLine(srcElement);
		}
		
		return startIndex;
	}

	/** Reunion */
	const _createSortedUnion = (array1, array2) => {
		
		let sortedUnion = [];
		
		let index1 = 0;
		let index2 = 0;
		
		let currentArray = array1;
		let currentIndex = 0;
		let otherArray = array2;
		let otherIndex = 0;
		
		if (_getStartLine(array1[0]) > _getStartLine(array2[0])) {
			currentArray = array2;
			otherArray = array1;
		}

		let tempArray;
		let tempIndex;
		while (otherIndex < otherArray.length) {
			
			currentIndex = _addUntil(currentArray, sortedUnion, currentIndex, _getStartLine(otherArray[otherIndex]));
			tempArray = currentArray;
			tempIndex = currentIndex;
			currentArray = otherArray;
			currentIndex = otherIndex;
			otherArray = tempArray;
			otherIndex = tempIndex;
		}
		_addUntil(currentArray, sortedUnion, currentIndex, Number.MAX_SAFE_INTEGER);
		
		return sortedUnion;
	}
	
	/** Load an index name */
	const _loadIndex = (indexTypeFiles, indexTypeFileIndex, searchData, indexValues, resultsCallback) => {
		
//		let p = new Promise( (resolve, reject) => {
//			c("Promise", "");
//			//resolve();
//		}).then( () => {
//			c("Then", "");
//		});
		
//		let indexFilePromises = [];
		let indexFiles = indexTypeFiles[indexTypeFileIndex];
//		for (let indexFile of indexFiles) {
//			let indexFilePromise = (indexFile) => {
//				
//				return new Promise( (resolve, reject) => {
//					
//					c("indexFile", indexFile);
//					//resolve();
//				});
//			};
//			indexFilePromises.push(indexFilePromise);
//		};

		let indexNameValuesLines = [];
		let indexFilePromises = indexFiles.map( (indexFile) => {

			return new Promise( (resolve, reject) => {
				
				const xmlHttpRequest = new XMLHttpRequest();
				xmlHttpRequest.onreadystatechange = () => {
					if ((xmlHttpRequest.readyState == 4) && (xmlHttpRequest.status == 200)) {
						
						let lines = JSON.parse(xmlHttpRequest.responseText);
						c("indexFile " + indexFile, lines);
						indexNameValuesLines[indexFiles.indexOf(indexFile)] = lines; 
						resolve();
					}
				};
				xmlHttpRequest.open("GET", "_catalog/indexes/" + indexFile, true);
				xmlHttpRequest.overrideMimeType("text/json");
				xmlHttpRequest.send();
			});
		});  
		
	    Promise.all(indexFilePromises).then( () => {
	    	
//	    	c("All lines for name index finished downloading", "");
	    	let indexNameLines = [];
	    	for (let indexNameValuesLine of indexNameValuesLines) {
	    		c("indexNameValuesLine", indexNameValuesLine);
	    		if (indexNameLines.length === 0) {
	    			indexNameLines = indexNameValuesLine;
	    		}
	    		else {
	    			indexNameLines = _createSortedUnion(indexNameValuesLine, indexNameLines);
	    		}
	    	}
	    	c("indexNameLines", indexNameLines);
	    });
		
		
//		const xmlHttpRequest = new XMLHttpRequest();
//		xmlHttpRequest.onreadystatechange = () => {
//			if ((xmlHttpRequest.readyState == 4) && (xmlHttpRequest.status == 200)) {
//				
//				const jsonString = xmlHttpRequest.responseText;
//				const results = JSON.parse(jsonString);
//				
//				console.log(results);
//				
//				let indexFieldValues = [];
//				for (value of searchData.searchFieldsValues[indexIndex].values) {
//					console.log(value);
//					indexFieldValues = indexFieldValues.concat(results[value]);
//				}
//				if (indexIndex === 0) {
//					indexValues = indexFieldValues;
//				}
//				else {
//					indexValues = array_intersect(indexValues, indexFieldValues);
//				}
//				
//				//indexValues[indexIndex] = indexFieldValues;
//				console.log(indexValues);
//
//				indexIndex = indexIndex + 1;
//				if (indexIndex < indexFiles.length) {
//					_loadIndex(indexFiles, indexIndex, searchData, indexValues, resultsCallback);
//				}
//				else {
//					
//					
//				}
	
				
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
//			}
//		};
//		xmlHttpRequest.open("GET", indexFiles[indexIndex], true);
//		xmlHttpRequest.overrideMimeType("text/json");
//		xmlHttpRequest.send();
	}

	/** Search received */
	const applyFilters = (searchData, resultsCallback) => {
		
		let searchFilters = searchData.searchFieldsValues;
		
		let indexTypeFiles = [];
		for (let searchFilter of searchFilters) {
			
			let searchFilterName = searchFilter.field;
			let searchFilterNameIndex = filterNameIndex[searchFilterName];
			let indexFiles = [];
			indexTypeFiles.push(indexFiles);

			for (let searchFilterValue of searchFilter.values) {
				let searchFilterValueIndex = filterNameValueIndex[searchFilterName][searchFilterValue];
				indexFiles.push("static-catalog-index-value-" + searchFilterNameIndex + "-" + searchFilterValueIndex + ".json");	
			}
		}
		c("indexTypeFiles", indexTypeFiles);
		
		_loadIndex(indexTypeFiles, 0, searchData, [], resultsCallback);
		
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

	/* Constructor */
	_initialize();
	
	/* Publish public */
	return {
		applyFilters: applyFilters
	}
})();
