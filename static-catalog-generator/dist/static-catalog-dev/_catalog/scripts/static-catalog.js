/*	
╭──────────────────────────────────────────────────────────────────────────────╮
│ static-catalog distribution                        static-catalog.org - 2019 │
╰──────────────────────────────────────────────────────────────────────────────╯
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

//	var cachePagination = {
//		"paginationPage": 0,
//		"paginationResultsPerPage": 0
//	}
	
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
	const initialize = () => {
		
		const xmlHttpRequest = new XMLHttpRequest();
		xmlHttpRequest.onreadystatechange = () => {
			
			if ((xmlHttpRequest.readyState == 4) && (xmlHttpRequest.status == 200)) {
				let contents = JSON.parse(xmlHttpRequest.responseText);
//				c("contents", contents);
				
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
	
	/** Modulo */
	const getLine = (line) => {
		
		let value = Math.trunc(line / indexLinesModulo);
		if (value === 0) {
			value = line % indexLinesModulo;
		}

		return value;
	}

	/** Modulo start */
	const getStartLine = (line) => {
		
		return Math.trunc(line / indexLinesModulo);
	}

	/** Modulo end */
	const getEndLine = (line) => {
		
		return line % indexLinesModulo;
	}

	/** Add elements */
	const addUntil = (arraySrc, arrayDest, startIndex, limitValue) => {
		
		let srcElement = arraySrc[startIndex];
		let value = getLine(srcElement);
		while (value < limitValue) {
			arrayDest.push(srcElement);
			
			startIndex++;
			if (startIndex === arraySrc.length) {
				return startIndex;
			}
			srcElement = arraySrc[startIndex];
			value = getLine(srcElement);
		}
		
		return startIndex;
	}

	/** Compact */
	const compactLines = ( srcLines ) => {
		
		let destLines = [];
		
		for (let srcLine of srcLines) {

			let srcStartLine = getStartLine(srcLine);
			let srcEndLine = getEndLine(srcLine);

			let destLinesSize = destLines.length;
			if (destLinesSize == 0) {
				destLines.push(srcLine);	
			}
			else {
				let destLastIndex = destLinesSize - 1;
				let destLastLine = destLines[destLastIndex];
				let destStartLine = getStartLine(destLastLine);
				let destEndLine = getEndLine(destLastLine);
				
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
	const createUnion = (array1, array2) => {
		
		let sortedUnion = [];
		
		let index1 = 0;
		let index2 = 0;
		
		let currentArray = array1;
		let currentIndex = 0;
		let otherArray = array2;
		let otherIndex = 0;
		
		if (getLine(array1[0]) > getLine(array2[0])) {
			currentArray = array2;
			otherArray = array1;
		}

		let tempArray;
		let tempIndex;
		while (otherIndex < otherArray.length) {
			
			currentIndex = addUntil(currentArray, sortedUnion, currentIndex, getLine(otherArray[otherIndex]));
			tempArray = currentArray;
			tempIndex = currentIndex;
			currentArray = otherArray;
			currentIndex = otherIndex;
			otherArray = tempArray;
			otherIndex = tempIndex;
		}
		addUntil(currentArray, sortedUnion, currentIndex, Number.MAXSAFEINTEGER);
//		c("before", sortedUnion);
		sortedUnion = compactLines(sortedUnion);
//		c("after", sortedUnion);

		return sortedUnion;
	}

	/** Intersection */
	const createIntervalIntersection = (line1, line2) => {
		
		let startLine1 = getStartLine(line1);
		let endLine1 = getEndLine(line1);

		let startLine2 = getStartLine(line2);
		let endLine2 = getEndLine(line2);

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
	const createIntersection = (array1, array2) => {
		
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
				let line = createIntervalIntersection(line1, line2);
				if (line === null) {
					if (getLine(line1) > getEndLine(line2)) {
						index2++;
					}
					else {
						index1++;
						break;
					}
				}
				else {
					intersection.push(line);
					if (getEndLine(line1) >= getEndLine(line2)) {
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
	const loadIndex = (indexTypeFiles, indexTypeFileIndex, searchData, indexLines, loadIndexResolve) => {
		
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
	    			indexNameLines = createUnion(indexNameValuesLine, indexNameLines);
	    		}
	    	}
	    	c("union indexNameLines", indexNameLines);
	    	
	    	if (indexLines === null) {
	    		indexLines = indexNameLines;
	    	}
	    	else {
	    		indexLines = createIntersection(indexLines, indexNameLines);
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
	    		loadIndex(indexTypeFiles, indexTypeFileIndex, searchData, indexLines, loadIndexResolve);
	    	}
	    	else {
	    		c("all files downloaded and union", "");
	    		loadIndexResolve(indexLines);
	    	}
	    });
	}
	
	/** Load the CSV blocks */
	const loadBlocks = (indexLines, searchData, resultLines, loadBlocksResolve) => {
		
		let searchLinesCount = searchData.paginationResultsPerPage;
		let firstSearchIndexLine = (searchData.paginationPage - 1) * searchLinesCount + 1;
		
		
		/* Find result lines indexes */
		let resultIndexLines = [];
		break_lines:
		for (let indexLine of indexLines) {
			
			let startOrEndIndexLine = getLine(indexLine);
			if (startOrEndIndexLine >= firstSearchIndexLine) {
				
				let startIndexLine = getStartLine(indexLine);
				let endIndexLine = getEndLine(indexLine);
				if (startIndexLine === 0) {
					resultIndexLines.push(endIndexLine);
					if (resultIndexLines.length === searchLinesCount) {
						break break_lines;
					}
				}
				else {
					for (let index = startIndexLine; index <= endIndexLine; index++) {
						resultIndexLines.push(index);
						if (resultIndexLines.length === searchLinesCount) {
							break break_lines;
						}
					}
				}
			}
		}
		c("result index lines", resultIndexLines);

		/* Find result blocks indexes */
		let resultIndexBlocks = [];
		
		for (let resultIndexLine of resultIndexLines) {
			let resultIndexBlock = Math.trunc(resultIndexLine / blockLinesCount);
			if (!resultIndexBlocks.includes(resultIndexBlock)) {
				resultIndexBlocks.push(resultIndexBlock);
			}
		}
		c("result index blocks", resultIndexBlocks);
		
		/* Download result blocks */
		let blockResults = {};
		let blockFilePromises = resultIndexBlocks.map( (indexBlock) => {

			return new Promise( (resolve, reject) => {
				
				const xmlHttpRequest = new XMLHttpRequest();
				xmlHttpRequest.onreadystatechange = () => {
					if ((xmlHttpRequest.readyState == 4) && (xmlHttpRequest.status == 200)) {
						
						const csvString = xmlHttpRequest.responseText;
						const results = Papa.parse(csvString, {
							header: true
						});

						blockResults[indexBlock] = results.data;
						resolve();
					}
				};
				xmlHttpRequest.open("GET", "_catalog/data/block-" + indexBlock + ".csv", true);
				xmlHttpRequest.overrideMimeType("text/csv");
				xmlHttpRequest.send();
				c("Download..", "_catalog/data/block-" + indexBlock + ".csv");
			});
		});  
		
	    Promise.all(blockFilePromises).then( () => {
	    	
	    	c("All blocks finished downloading", blockResults);
	    	for (let resultIndexLine of resultIndexLines) {
	    		let resultIndexBlock = Math.trunc(resultIndexLine / blockLinesCount);
	    		let resultIndexBlockLine = resultIndexLine - (resultIndexBlock * blockLinesCount);
	    		
	    		//c("Line in block", resultIndexBlockLine);
	    		resultLines.push(blockResults[resultIndexBlock][resultIndexBlockLine]);
	    	}
	    	
	    	
	    	
	    	
	    	
	    	
	    	loadBlocksResolve(resultLines);
	    });
		
		
		
		
//		let index = searchBlocks[blockIndex];
//		
//		const xmlHttpRequest = new XMLHttpRequest();
//		xmlHttpRequest.onreadystatechange = () => {
//			if ((xmlHttpRequest.readyState == 4) && (xmlHttpRequest.status == 200)) {
//				
//				const csvString = xmlHttpRequest.responseText;
//				const results = Papa.parse(csvString, {
//					header: true
//				});
//				
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
//						loadBlock(searchFilters, searchBlocks, blockIndex, resultLines, resultsCallback);	
//					}
//				}
//				
//				//resultsCallback(results);
//				
////				console.log(results);
////				
////				var index = 0;
////				var concat = "";
////				for (let result of results.data) {
////					concat = concat + index + " " + results.data[index][1] + "\n";
////					index++;
////					
////				}
//				
//				//console.log(concat);
//				
////				console.log("done");
//				//document.getElementById("demo").innerHTML = this.responseText;
//				//console.log(xmlHttpRequest.responseText.slice( 0, 100 ));
//				//console.log(this.responseText);
//				//console.log("apply2");
//			}
//		};
//		xmlHttpRequest.open("GET", "_catalog/block" + index + ".csv", true);
//		xmlHttpRequest.overrideMimeType("text/csv");
//		xmlHttpRequest.send();
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
		
		if (indexTypeFiles.length === 0) {
			resultsCallback([], 0);
			return;
		}
		
		/* Indexes */
		var indexLines = null;
		new Promise( (loadIndexResolve, reject) => {

			loadIndex(indexTypeFiles, 0, searchData, indexLines, loadIndexResolve);
		}).then((indexLines) => {

			c("Indexes done in " + ((new Date()).getTime() - startMs), indexLines);
			
			/* Blocks */
			var resultLines = [];
			new Promise( (loadBlocksResolve, loadBlocksReject) => {

				loadBlocks(indexLines, searchData, resultLines, loadBlocksResolve);
			}).then((resultLines) => {
				
				c("Blocks done in " + ((new Date()).getTime() - startMs), resultLines);
				resultsCallback(resultLines, totalLinesCount, totalLinesCount, (new Date()).getTime() - startMs);
			});
		});
		
	}

	/* Constructor */
	initialize();
	
	/* Publish public */
	return {
		applyFilters: applyFilters
	}
})();
