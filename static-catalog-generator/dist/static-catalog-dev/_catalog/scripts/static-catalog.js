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
	var isDebug = true;
	
	/** debug console */
	const c = (message, object) => {
		
		if (isDebug) {
			console.log(message);
			console.log(object);
		}
	}

	/** debug console */
	const cl = () => {
		
		if (isDebug) {
			console.clear();
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
	
	/** */
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
	const _getLine = (line) => {
		
		let value = Math.trunc(line / indexLinesModulo);
		if (value === 0) {
			value = line % indexLinesModulo;
		}

		return value;
	}

	/** Modulo start */
	const _getStartLine = (line) => {
		
		return Math.trunc(line / indexLinesModulo);
	}

	/** Modulo end */
	const _getEndLine = (line) => {
		
		return line % indexLinesModulo;
	}

	/** Add elements */
	const _addUntil = (arraySrc, arrayDest, startIndex, limitValue) => {
		
		let srcElement = arraySrc[startIndex];
		let value = _getLine(srcElement);
		while (value < limitValue) {
			arrayDest.push(srcElement);
			
			startIndex++;
			if (startIndex === arraySrc.length) {
				return startIndex;
			}
			srcElement = arraySrc[startIndex];
			value = _getLine(srcElement);
		}
		
		return startIndex;
	}

	/** Compact */
	const _compactLines = ( srcLines ) => {
		
		let destLines = [];
		
		for (let srcLine of srcLines) {

			let srcStartLine = _getStartLine(srcLine);
			let srcEndLine = _getEndLine(srcLine);

			let destLinesSize = destLines.length;
			if (destLinesSize == 0) {
				destLines.push(srcLine);	
			}
			else {
				let destLastIndex = destLinesSize - 1;
				let destLastLine = destLines[destLastIndex];
				let destStartLine = _getStartLine(destLastLine);
				let destEndLine = _getEndLine(destLastLine);
				
				if (destStartLine == 0) {
					/* One line destination */
					if (srcStartLine == 0) {
						/* One line source */
						if (srcEndLine - destEndLine == 1) {
							/* New interval */
							let newLastLine = destEndLine * indexLinesModulo + srcEndLine;
							destLines[destLastIndex] = newLastLine;
						}
						else {
							/* New line */
							destLines.push(srcEndLine);
						}
					}
					else {
						/* Interval line source */
						if (srcStartLine - destEndLine == 1) {
							/* New interval */
							let newLastLine = destEndLine * indexLinesModulo + srcEndLine;
							destLines[destLastIndex] = newLastLine;
						}
						else {
							/* New line */
							destLines.push(srcLine);
						}
					}
				}
				else {
					/* Interval destination */
					if (srcStartLine == 0) {
						/* One line source */
						if (srcEndLine - destEndLine == 1) {
							/* Add to interval */
							let newLastLine = destStartLine * indexLinesModulo + srcEndLine;
							destLines[destLastIndex] = newLastLine;
						}
						else {
							/* New line */
							destLines.push(srcEndLine);
						}
					}
					else {
						/* Interval line source */
						if (srcStartLine - destEndLine == 1) {
							/* Add to interval */
							let newLastLine = destStartLine * indexLinesModulo + srcEndLine;
							destLines[destLastIndex] = newLastLine;
						}
						else {
							/* New line */
							destLines.push(srcLine);
						}
					}
				}
			}
		}
		
		return destLines;
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
		
		if (_getLine(array1[0]) > _getLine(array2[0])) {
			currentArray = array2;
			otherArray = array1;
		}

		let tempArray;
		let tempIndex;
		while (otherIndex < otherArray.length) {
			
			currentIndex = _addUntil(currentArray, sortedUnion, currentIndex, _getLine(otherArray[otherIndex]));
			tempArray = currentArray;
			tempIndex = currentIndex;
			currentArray = otherArray;
			currentIndex = otherIndex;
			otherArray = tempArray;
			otherIndex = tempIndex;
		}
		_addUntil(currentArray, sortedUnion, currentIndex, Number.MAX_SAFE_INTEGER);
		
//		c("before", sortedUnion);
		
		sortedUnion = _compactLines(sortedUnion);

//		c("after", sortedUnion);

		return sortedUnion;
	}

	/** Intersection */
	const _createIntervalIntersection = (line1, line2) => {
		
		let startLine1 = _getStartLine(line1);
		let endLine1 = _getEndLine(line1);

		let startLine2 = _getStartLine(line2);
		let endLine2 = _getEndLine(line2);

		if (startLine1 === 0) {
			if (startLine2 === 0) {
				return (endLine1 === endLine2) ? endLine1 : null;
			}
			else {
				return ((startLine2 <= endLine1) && (endLine1 <= endLine2)) ? endLine1 : null;
			}
		}
		else {
			if (startLine2 === 0) {
				return ((startLine1 <= endLine2) && (endLine2 <= endLine1)) ? endLine2 : null;
			}
			else {
				if ((startLine2 > endLine1) || (startLine1 > endLine2)) {
					return null;
				}
				else {
					let startLine = (startLine1 >= startLine2) ? startLine1 : startLine2;
					let endLine = (endLine1 <= endLine2) ? endLine1 : endLine2;
					return (startLine === endLine) ? startLine : startLine * indexLinesModulo + endLine;
				}
			}
		}
	};

	/** Intersection */
	const _createIntersection = (array1, array2) => {
		
		let index1 = 0;
		let index2 = 0;
		let array1Length = array1.length;
		let array2Length = array2.length;
		
		let intersection = [];
		
		while ((index1 < array1Length) && (index2 < array2Length)) {
			let line1 = array1[index1];
//			c("index1", index1);
			while (index2 < array2Length) {
//				c("index2", index2);
				let line2 = array2[index2];
				let line = _createIntervalIntersection(line1, line2);
				if (line === null) {
					if (_getLine(line1) > _getEndLine(line2)) {
						index2++;
					}
					else {
						index1++;
						break;
					}
				}
				else {
					intersection.push(line);
					if (_getEndLine(line1) >= _getEndLine(line2)) {
						index2++;	
					}
					else {
						index1++;
						break;
					}
				}
			}
		}
		
		return intersection;
	};

	/** Load an index name */
	const _loadIndex = (indexTypeFiles, indexTypeFileIndex, searchData, indexLines, loadIndexResolve) => {
		
//		let p = new Promise( (resolve, reject) => {
//			c("Promise", "");
//			//resolve();
//		}).then( () => {
//			c("Then", "");
//		});
		
		let indexFiles = indexTypeFiles[indexTypeFileIndex];
		let indexNameValuesLines = [];
		let indexFilePromises = indexFiles.map( (indexFile) => {

			return new Promise( (resolve, reject) => {
				
				const xmlHttpRequest = new XMLHttpRequest();
				xmlHttpRequest.onreadystatechange = () => {
					if ((xmlHttpRequest.readyState == 4) && (xmlHttpRequest.status == 200)) {
						
						let lines = JSON.parse(xmlHttpRequest.responseText);
//						c("indexFile " + indexFile, lines);
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
	    	
//	    	c("All lines for name index finished downloading", indexTypeFileIndex);
	    	let indexNameLines = [];
	    	for (let indexNameValuesLine of indexNameValuesLines) {
//	    		c("indexNameValuesLine", indexNameValuesLine);
	    		if (indexNameLines.length === 0) {
	    			indexNameLines = indexNameValuesLine;
	    		}
	    		else {
	    			indexNameLines = _createSortedUnion(indexNameValuesLine, indexNameLines);
	    		}
	    	}
	    	c("union indexNameLines", indexNameLines);
	    	
	    	if (indexLines === null) {
	    		indexLines = indexNameLines;
	    	}
	    	else {
	    		indexLines = _createIntersection(indexLines, indexNameLines);
	    		if (indexLines.length === 0) {
	    			c("Intersection empty, exit", indexLines);
	    			loadIndexResolve(indexLines);
	    			return;
	    		}
	    		else {
	    			c("intersection", indexLines);
	    		}
	    	}
	    	
	    	indexTypeFileIndex++;
	    	if (indexTypeFileIndex < indexTypeFiles.length) {
	    		_loadIndex(indexTypeFiles, indexTypeFileIndex, searchData, indexLines, loadIndexResolve);
	    	}
	    	else {
	    		c("all files downloaded and union", "");
	    		loadIndexResolve(indexLines);
	    	}
	    });
	}

	/** Search received */
	const applyFilters = (searchData, resultsCallback) => {
		
		let startMs = (new Date()).getTime();
		cl();
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
		
		var indexLines = null;
		new Promise( (loadIndexResolve, reject) => {

			_loadIndex(indexTypeFiles, 0, searchData, indexLines, loadIndexResolve);
		}).then((indexLines) => {
			
			c("done in " + ((new Date()).getTime() - startMs), indexLines);
		});
		
	}

	/* Constructor */
	_initialize();
	
	/* Publish public */
	return {
		applyFilters: applyFilters
	}
})();
