/*	
╭──────────────────────────────────────────────────────────────────────────────╮
│ static-catalog distribution                        static-catalog.org - 2019 │
╰──────────────────────────────────────────────────────────────────────────────╯
*/

"use strict";

/* static-catalog engine */ 
const StaticCatalog = (() => {

	/* _catalog/static-catalog.json */
//	var filterNameIndex;
//	var filterNameValueIndex;
//	var indexSplitType;
	var totalLinesCount;
	var blockLinesCount;
	var indexLinesModulo;

//	var cachePagination = {
//		"paginationPage": 0,
//		"paginationResultsPerPage": 0
//	}
	
	/* debug */
	var isDebug = false;
	
	/* debug console */
	const c = (message, object) => {
		
		if (isDebug) {
			console.log(message);
			console.log(object);
		}
	}

	/* debug console */
	const cl = () => {
		
		if (isDebug) {
			console.clear();
		}
	}

	/* _underscore.js */
	const findInsertionIndex = (array, value) => {

		let low = 0;
		let high = array.length;
		while (low < high) {
			let mid = Math.floor((low + high) / 2);
			if (array[mid] < value) {
				low = mid + 1;
			}
			else {
				high = mid;
			}
		}
		return low;
	};

//	/* Initialize */
//	const initialize = () => {
//		
//		const xmlHttpRequest = new XMLHttpRequest();
//		xmlHttpRequest.onreadystatechange = () => {
//			
//			if ((xmlHttpRequest.readyState == 4) && (xmlHttpRequest.status == 200)) {
//				let contents = JSON.parse(xmlHttpRequest.responseText);
////				c("contents", contents);
//				
//				filterNameIndex = contents.filterNameIndex;
//				filterNameValueIndex = contents.filterNameValueIndex;
//				indexSplitType = contents.indexSplitType;
//				totalLinesCount = contents.totalLinesCount;
//				blockLinesCount = contents.blockLinesCount;
//				indexLinesModulo = contents.indexLinesModulo;
//			}
//		};
//		xmlHttpRequest.open("GET", "_catalog/static-catalog.json", true);
//		xmlHttpRequest.overrideMimeType("text/json");
//		xmlHttpRequest.send();
//	};
	
	/* Modulo */
	const getLine = (line) => {
		
		let value = Math.trunc(line / indexLinesModulo);
		if (value === 0) {
			value = line % indexLinesModulo;
		}

		return value;
	}

	/* Modulo start */
	const getStartLine = (line) => {
		
		return Math.trunc(line / indexLinesModulo);
	}

	/* Modulo end */
	const getEndLine = (line) => {
		
		return line % indexLinesModulo;
	}

	/* Count line */
	const getLineCount = ( line ) => {
		
		let startLine = getStartLine(line);
		if (startLine === 0) {
			return 1;
		}
		return (getEndLine(line) - startLine) + 1;
	}

	/* Count lines */
	const getLinesCount = ( lines ) => {

		let count = 0;
		for (let line of lines) {
			count = count + getLineCount(line);
		}
		return count;
	}
	
	/* Add elements */
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

	/* Compact */
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

	/* Reunion */
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
		addUntil(currentArray, sortedUnion, currentIndex, Number.MAX_SAFE_INTEGER);
//		c("before", sortedUnion);
		sortedUnion = compactLines(sortedUnion);
//		c("after", sortedUnion);

		return sortedUnion;
	}

	/* Intersection */
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

	/* Intersection */
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

	/* s */
	const getJson = async (url) => {
		
		return await new Promise( (resolve, reject) => {

			const xmlHttpRequest = new XMLHttpRequest();
			xmlHttpRequest.onreadystatechange = () => {
				
				if ((xmlHttpRequest.readyState == 4) && (xmlHttpRequest.status == 200)) {
					resolve(JSON.parse(xmlHttpRequest.responseText));
				}
			};
			xmlHttpRequest.open("GET", url, true);
			xmlHttpRequest.overrideMimeType("text/json");
			xmlHttpRequest.send();
		});
	};
	
	/* d */
	const loadIndexes = async (searchData) => {
		
		let searchFieldsValues = searchData.searchFieldsValues;
		
		let indexFieldsFiles = [];
		for (let searchFieldValues of searchFieldsValues) {
			let fieldIndex = searchFieldValues.fieldIndex;
			let indexFieldFiles = [];
			for (let filterIndex of searchFieldValues.filterIndexes) {
				indexFieldFiles.push("static-catalog-index-" + fieldIndex + "-" + filterIndex + ".json");
			}
			indexFieldsFiles.push(indexFieldFiles);
		}
		c("indexFieldsFiles", indexFieldsFiles);
		console.log(indexFieldsFiles);
		
		/* Indexes */
		var indexFieldIntersectLines = null;
		if (indexFieldsFiles.length === 0) {
			indexFieldIntersectLines = [];
			indexFieldIntersectLines.push(indexLinesModulo + totalLines);
		}
		else {
			for (let indexFieldsFileIndex = 0; indexFieldsFileIndex < indexFieldsFiles.length; indexFieldsFileIndex++) {
				
				let indexFieldFiles = indexFieldsFiles[indexFieldsFileIndex];

				let indexFieldValuesLines = [];
				let indexFieldFilesPromises = indexFieldFiles.map( (indexFieldFile) => {
					
					return (async () => {
						
						indexFieldValuesLines[indexFieldFiles.indexOf(indexFieldFile)] = await getJson("_catalog/indexes/" + indexFieldFile);
					})();
				});
				
				await Promise.all(indexFieldFilesPromises);
				console.log("All lines for name index finished downloading");
				console.log(indexFieldValuesLines);
				
				/* Union */
		    	let indexFieldUnionLines = [];
		    	for (let indexFieldValueLines of indexFieldValuesLines) {
		    		if (indexFieldUnionLines.length === 0) {
		    			indexFieldUnionLines = indexFieldValueLines;
		    		}
		    		else {
		    			indexFieldUnionLines = createUnion(indexFieldValueLines, indexFieldUnionLines);
		    		}
		    	}
		    	console.log("union");
		    	console.log(indexFieldUnionLines);
		    	
		    	/* Intersection */
		    	if (indexFieldIntersectLines === null) {
		    		indexFieldIntersectLines = indexFieldUnionLines;
		    	}
		    	else {
		    		indexFieldIntersectLines = createIntersection(indexFieldIntersectLines, indexFieldUnionLines);
		    	}
		    	console.log("intersect");
		    	console.log(indexFieldIntersectLines);
	    		if (indexFieldIntersectLines.length === 0) {
	    			console.log("Intersection empty, exit");
	    			return indexFieldIntersectLines;
	    		}
			}
			
//		    Promise.all(indexFilePromises).then( () => {
//		    	
////		    	c("All lines for name index finished downloading", indexTypeFileIndex);
//		    	let indexNameLines = [];
//		    	for (let indexNameValuesLine of indexNameValuesLines) {
////		    		c("indexNameValuesLine", indexNameValuesLine);
//		    		if (indexNameLines.length === 0) {
//		    			indexNameLines = indexNameValuesLine;
//		    		}
//		    		else {
//		    			indexNameLines = createUnion(indexNameValuesLine, indexNameLines);
//		    		}
//		    	}
////		    	c("union indexNameLines", indexNameLines);
//		    	
//		    	if (indexLines === null) {
//		    		indexLines = indexNameLines;
//		    	}
//		    	else {
//		    		indexLines = createIntersection(indexLines, indexNameLines);
//		    		if (indexLines.length === 0) {
//		    			c("Intersection empty, exit", indexLines);
//		    			loadIndexResolve(indexLines);
//		    			return;
//		    		}
//		    		else {
////		    			c("intersection", indexLines);
//		    		}
//		    	}
//		    	
//		    	indexTypeFileIndex++;
//		    	if (indexTypeFileIndex < indexTypeFiles.length) {
//		    		loadIndex(indexTypeFiles, indexTypeFileIndex, searchData, indexLines, loadIndexResolve);
//		    	}
//		    	else {
//		    		c("all files downloaded and union", "");
//		    		loadIndexResolve(indexLines);
//		    	}
//		    });

		}
		
		return indexFieldIntersectLines;
	}
	
	/* Load an index name */
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
//	    	c("union indexNameLines", indexNameLines);
	    	
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
//	    			c("intersection", indexLines);
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

	/* Load sort */
	const loadSort = (searchSort, loadSortResolve) => {
		
		var sortLines;
		//let sortFiles = sortTypeFiles[sortTypeFileIndex];
		let sortFiles = [];
		sortFiles[0] = "static-catalog-sort-" + searchSort.sortFieldIndex + "-" + searchSort.sortDirection + ".json"; 
		
		let sortNameValuesLines = [];
		let sortFilePromises = sortFiles.map( (sortFile) => {

			return new Promise( (resolve, reject) => {
				
				const xmlHttpRequest = new XMLHttpRequest();
				xmlHttpRequest.onreadystatechange = () => {
					if ((xmlHttpRequest.readyState == 4) && (xmlHttpRequest.status == 200)) {
						
						sortLines = JSON.parse(xmlHttpRequest.responseText);
//						c("sortFile " + sortFile, lines);
						//sortNameValuesLines[sortFiles.sortOf(sortFile)] = lines; 
						resolve();
					}
				};
				xmlHttpRequest.open("GET", "_catalog/sort/" + sortFile, true);
				xmlHttpRequest.overrideMimeType("text/json");
				xmlHttpRequest.send();
			});
		});  
		
	    Promise.all(sortFilePromises).then( () => {
	    	
	    	c("all sort files downloaded", "");
	    	loadSortResolve(sortLines);
	    });
	}
	
	/* Load the CSV blocks */
	const loadBlocks = (indexLines, sortLines, searchData, resultLines, loadBlocksResolve) => {
		
		let startMs1 = (new Date()).getTime();
		
		let indexLinesLength = indexLines.length;
		let sortIndexLines = [];
		let indexLinesStarts = [];
		for (let indexLine of indexLines) {
			indexLinesStarts.push(getLine(indexLine));
		}

		if (sortLines === null) {
			sortIndexLines = indexLines;
		}
		else {
			for (let sortLine of sortLines) {
				
				let sortLineStart = getLine(sortLine);

				let startIndex = findInsertionIndex(indexLinesStarts, sortLineStart);
				if (startIndex >= indexLinesLength) {
					let lastIndexLine = indexLines[indexLinesLength - 1];
					if (sortLineStart <= getEndLine(lastIndexLine)) {
						let found = createIntervalIntersection(sortLine, lastIndexLine);
						if (found !== null) {
							sortIndexLines.push(found);
						}
					}
				}
				else {
					if (startIndex > 0) {
						let prevIndexLine = indexLines[startIndex - 1];
						let found = createIntervalIntersection(sortLine, prevIndexLine);
						if (found !== null) {
							sortIndexLines.push(found);
						}
					}
					let indexLine = indexLines[startIndex];
					let found = createIntervalIntersection(sortLine, indexLine);
					while (found !== null) {
						sortIndexLines.push(found);
						startIndex++;
						if (startIndex < indexLinesLength - 1) {
							startIndex++;
							indexLine = indexLines[startIndex];
							found = createIntervalIntersection(sortLine, indexLine);
						}
						else {
							found = null;
						}
					}
				}
			}
			c("sortIndexLines done in " + ((new Date()).getTime() - startMs1), sortIndexLines);
		}
		
		let searchLinesCount = searchData.searchPagination.paginationResultsPerPage;
		let firstSearchIndexLine = (searchData.searchPagination.paginationPage - 1) * searchLinesCount + 1;
		
		
		/* Find result lines indexes */
		let resultIndexLines = [];
		let linesCnt = 0;
		break_lines:
		for (let indexLine of sortIndexLines) {

			let newLinesCnt = linesCnt + getLineCount(indexLine);
			
			if (newLinesCnt >= firstSearchIndexLine) {
				
				let startIndexLine = getStartLine(indexLine);
				let endIndexLine = getEndLine(indexLine);
				
				if (startIndexLine === 0) {
					resultIndexLines.push(endIndexLine);
					if (resultIndexLines.length === searchLinesCount) {
						break break_lines;
					}
				}
				else {
					let delta = firstSearchIndexLine - linesCnt;
					delta = delta > 0 ? (delta - 1) : 0;
					for (let index = startIndexLine + delta; index <= endIndexLine; index++) {
						resultIndexLines.push(index);
						if (resultIndexLines.length === searchLinesCount) {
							break break_lines;
						}
					}
				}
				
			}
			linesCnt = newLinesCnt;
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
							header: false
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
	    		let resultIndexBlockLine = resultIndexLine - (resultIndexBlock * blockLinesCount + 1);
	    		//let resultIndexBlockLine = resultIndexLine - (resultIndexBlock * blockLinesCount);
	    		
	    		//c("Line in block", resultIndexBlockLine);
	    		resultLines.push(blockResults[resultIndexBlock][resultIndexBlockLine]);
	    	}
	    	
	    	loadBlocksResolve(resultLines);
	    });
	}

	/* Search received */
	const applyFilters = (searchData, resultsCallback) => {

		applyFiltersAsync(searchData, resultsCallback);
	}

	/* Search received */
	const applyFiltersAsync = async (searchData, resultsCallback) => {
		
//		let resl = await getJson("_catalog/indexes/static-catalog-index-1-0.json");
//		console.log(resl);
		
//		let indexDownloadPromises = [];
//		for (let i = 0; i < 10; i++) {
//			indexDownloadPromises.push((async () => {
//				console.log(i);
//				let rr = await getJson("_catalog/indexes/static-catalog-index-1-" + i + ".json");
//				console.log(i);
//				console.log(rr);
//			})());
//		}
////		console.log(pr);
//		await Promise.all(indexDownloadPromises);
//		console.log("after all pr");

		totalLinesCount = searchData.searchTotals.totalLines;
		blockLinesCount = searchData.searchTotals.blockLines;
		indexLinesModulo = searchData.searchTotals.indexLinesModulo;
		
		let indexesLines = await loadIndexes(searchData);
		console.log("after loadIndexes");
		console.log(indexesLines);
		
		let startMs = (new Date()).getTime();
		cl();
		
//		totalLinesCount = searchData.searchTotals.totalLines;
//		blockLinesCount = searchData.searchTotals.blockLines;
//		indexLinesModulo = searchData.searchTotals.indexLinesModulo;

		//c(createUnion([1, 3, 5], [2, 4]));
		
		let searchFilters = searchData.searchFieldsValues;
		
		let indexTypeFiles = [];
		for (let searchFilter of searchFilters) {
			
//			let searchFilterName = searchFilter.field;
//			let searchFilterNameIndex = filterNameIndex[searchFilterName];
			let fieldIndex = searchFilter.fieldIndex;
			let indexFiles = [];
			indexTypeFiles.push(indexFiles);

			for (let filterIndex of searchFilter.filterIndexes) {
				//let searchFilterValueIndex = filterNameValueIndex[searchFilterName][searchFilterValue];
				//indexFiles.push("static-catalog-index-value-" + searchFilterNameIndex + "-" + searchFilterValueIndex + ".json");
				indexFiles.push("static-catalog-index-" + fieldIndex + "-" + filterIndex + ".json");
			}
		}
		c("indexTypeFiles", indexTypeFiles);
		
		/* Indexes */
		var indexLines = null;
		new Promise( (loadIndexResolve, loadIndexReject) => {

			if (indexTypeFiles.length === 0) {
				indexLines = [];
				indexLines.push(indexLinesModulo + totalLinesCount);
				loadIndexResolve(indexLines);
			}
			else {
				loadIndex(indexTypeFiles, 0, searchData, indexLines, loadIndexResolve);	
			}
		}).then((indexLines) => {

			c("Indexes done in " + ((new Date()).getTime() - startMs), indexLines);
			/* Sort */
			new Promise( (loadSortResolve, loadSortReject) => {

				let searchSort = searchData.searchSort;
				if (searchSort.sortFieldIndex === "-1") {
					loadSortResolve(null);
				}
				else {
					loadSort(searchSort, loadSortResolve);	
				}
			}).then((sortLines) => {
	
				c("Sort loaded done in " + ((new Date()).getTime() - startMs), sortLines);
				
				/* Blocks */
				var resultLines = [];
				new Promise( (loadBlocksResolve, loadBlocksReject) => {
	
					loadBlocks(indexLines, sortLines, searchData, resultLines, loadBlocksResolve);
				}).then((resultLines) => {
					
					c("Blocks done in " + ((new Date()).getTime() - startMs), resultLines);
					let foundLinesCount = getLinesCount(indexLines);
					resultsCallback(searchData, resultLines, foundLinesCount, (new Date()).getTime() - startMs);
				});
			});
		});
	}

	/* Constructor */
//	initialize();
	
	/* Publish public */
	return {
		applyFilters: applyFilters
	}
})();
