/*	
╭──────────────────────────────────────────────────────────────────────────────╮
│ static-catalog distribution                        static-catalog.org - 2019 │
╰──────────────────────────────────────────────────────────────────────────────╯
*/

"use strict";

/* static-catalog engine */ 
const StaticCatalog = (() => {

	/* _catalog/static-catalog.json */
	var totalLinesCount;
	var blockLinesCount;
	var indexLinesModulo;

	/* binary search from _underscore.js */
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

	/* Union */
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
		sortedUnion = compactLines(sortedUnion);
		
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
			while (index2 < array2Length) {
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

	/* JSON */
	const getJson = async (url) => {
		
		let responseText = await fetch(url);
        let responseJson = await responseText.json();
        
		return responseJson;
	};

	/* CSV */
	const getCsv = async (url) => {

        let responseText = await fetch(url);
        let text = await responseText.text();
        return Papa.parse(text, {
			header: false
		}).data;
	};
	
	/* Indexes */
	const loadIndexes = async (searchData) => {
		
		let searchFieldsValues = searchData.searchFieldsValues;
		
		let indexFieldsFiles = [];
        let indexFieldsIndexValues = [];
		for (let searchFieldValues of searchFieldsValues) {
			let fieldIndex = searchFieldValues.fieldIndex;
            indexFieldsFiles.push("static-catalog-index-" + fieldIndex + ".json");
            let indexFieldIndexValues = [];
            for (let filterIndex of searchFieldValues.filterIndexes) {
            	indexFieldIndexValues.push(filterIndex);
            }
            indexFieldsIndexValues.push(indexFieldIndexValues);
		}
		
		/* Indexes */
		var indexFieldIntersectLines = null;
		if (indexFieldsFiles.length === 0) {
			indexFieldIntersectLines = [];
			indexFieldIntersectLines.push(indexLinesModulo + totalLinesCount);
		}
		else {
			for (let indexFieldsFileIndex = 0; indexFieldsFileIndex < indexFieldsFiles.length; indexFieldsFileIndex++) {
				
                let indexFieldFile = indexFieldsFiles[indexFieldsFileIndex];
                //const startMs11 = (new Date()).getTime();
                let indexFieldAllValuesLines = await getJson("_catalog/indexes/" + indexFieldFile);
                //console.log("File done in " + ((new Date()).getTime() - startMs11) + " _catalog/indexes/" + indexFieldFile);
                let indexFieldIndexValues = indexFieldsIndexValues[indexFieldsFileIndex];
                let indexFieldValuesLines = [];
                for (let indexFieldIndexValue of indexFieldIndexValues) {
                    indexFieldValuesLines.push(indexFieldAllValuesLines[indexFieldIndexValue]);
                }
				
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
		    	//console.log("union"); console.log(indexFieldUnionLines);
		    	
		    	/* Intersection */
		    	if (indexFieldIntersectLines === null) {
		    		indexFieldIntersectLines = indexFieldUnionLines;
		    	}
		    	else {
		    		indexFieldIntersectLines = createIntersection(indexFieldIntersectLines, indexFieldUnionLines);
		    	}
		    	//console.log("intersection"); console.log(indexFieldIntersectLines);
		    	
	    		if (indexFieldIntersectLines.length === 0) {
	    			//console.log("Intersection empty, exit");
	    			return indexFieldIntersectLines;
	    		}
			}
		}

        return indexFieldIntersectLines;
	}

	/* Keywords */
	const loadKeywords = async (searchData) => {
		
		let searchFieldKeywords = searchData.searchFieldKeywords;
		
		var keywordIntersectLines = null;
		for (let searchFieldKeyword of searchFieldKeywords) {
			
			let fieldIndex = searchFieldKeyword.fieldIndex;
			let filterIndex = searchFieldKeyword.filterIndex;
			let keywordPrefix = searchFieldKeyword.keywordPrefix.toLowerCase();

			let keywordValuesLines = await getJson("_catalog/keywords/keywords-" + fieldIndex + "/static-catalog-keywords-" + fieldIndex + "-" + filterIndex + ".json");
			/* Union */
	    	let keywordUnionLines = [];
			for (let keywordValue of Object.keys(keywordValuesLines)) {
				
				if (keywordValue.toLowerCase().startsWith(keywordPrefix)) {
					
					let keywordValueLines = keywordValuesLines[keywordValue];
					
		    		if (keywordUnionLines.length === 0) {
		    			keywordUnionLines = keywordValueLines;
		    		}
		    		else {
		    			keywordUnionLines = createUnion(keywordUnionLines, keywordValueLines);
		    		}
				}
			}
			
	    	/* Intersection */
	    	if (keywordIntersectLines === null) {
	    		keywordIntersectLines = keywordUnionLines;
	    	}
	    	else {
	    		keywordIntersectLines = createIntersection(keywordIntersectLines, keywordUnionLines);
	    	}
		}

		return keywordIntersectLines;
	}

	/* Load sort */
	const loadSort = async (searchData) => {
		
		let searchSort = searchData.searchSort;
		if (searchSort.sortFieldIndex === "-1") {
			return null;
		}

		return await getJson("_catalog/sort/static-catalog-sort-" + searchSort.sortFieldIndex + "-" + searchSort.sortDirection + ".json");
	}
	
	/* Sort the index lines */
	const sortIndexLines = (indexLines, sortLines) => {

		if (sortLines === null) {
			return indexLines;
		}

		let indexLinesLength = indexLines.length;
		let sortedIndexLines = [];
		let indexLinesStarts = [];
		for (let indexLine of indexLines) {
			indexLinesStarts.push(getLine(indexLine));
		}
		
		for (let sortLine of sortLines) {
			
			let sortLineStart = getLine(sortLine);

			let startIndex = findInsertionIndex(indexLinesStarts, sortLineStart);
			if (startIndex >= indexLinesLength) {
				let lastIndexLine = indexLines[indexLinesLength - 1];
				if (sortLineStart <= getEndLine(lastIndexLine)) {
					let found = createIntervalIntersection(sortLine, lastIndexLine);
					if (found !== null) {
						sortedIndexLines.push(found);
					}
				}
			}
			else {
				if (startIndex > 0) {
					let prevIndexLine = indexLines[startIndex - 1];
					let found = createIntervalIntersection(sortLine, prevIndexLine);
					if (found !== null) {
						sortedIndexLines.push(found);
					}
				}
				let indexLine = indexLines[startIndex];
				let found = createIntervalIntersection(sortLine, indexLine);
				while (found !== null) {
					sortedIndexLines.push(found);
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
		
		return sortedIndexLines;
	}
	
	/* Load the CSV blocks */
	const loadBlocks = async (searchData, sortedIndexLines) => {
		
		let startMs1 = (new Date()).getTime();
		
		let searchLinesCount = searchData.searchPagination.paginationResultsPerPage;
		let firstSearchIndexLine = (searchData.searchPagination.paginationPage - 1) * searchLinesCount + 1;
		
		/* Find result lines indexes */
		let resultIndexLines = [];
		let linesCnt = 0;
		break_lines:
		for (let indexLine of sortedIndexLines) {

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

		/* Find result blocks indexes */
		let resultIndexBlocks = [];
		for (let resultIndexLine of resultIndexLines) {
			let resultIndexBlock = Math.trunc(resultIndexLine / blockLinesCount);
			if (!resultIndexBlocks.includes(resultIndexBlock)) {
				resultIndexBlocks.push(resultIndexBlock);
			}
		}
		
		/* Download result blocks */
		let blockResults = {};
		let blockFilePromises = resultIndexBlocks.map( (indexBlock) => {

			return (async () => {
				
				blockResults[indexBlock] = await getCsv("_catalog/data/block-" + indexBlock + ".csv");
			})();
		});  
		
		await Promise.all(blockFilePromises);

		let resultLines = [];
    	for (let resultIndexLine of resultIndexLines) {
    		let resultIndexBlock = Math.trunc(resultIndexLine / blockLinesCount);
    		let resultIndexBlockLine = resultIndexLine - (resultIndexBlock * blockLinesCount + 1);
    		resultLines.push(blockResults[resultIndexBlock][resultIndexBlockLine]);
    	}
    	
    	return resultLines;
	}

	/* Search received */
	const applyFilters = (searchData, resultsCallback) => {

		applyFiltersAsync(searchData, resultsCallback);
	}

	/* Search received */
	const applyFiltersAsync = async (searchData, resultsCallback) => {
		
		const startMs = (new Date()).getTime();
		//console.clear();

		/* Global */
		totalLinesCount = searchData.searchTotals.totalLines;
		blockLinesCount = searchData.searchTotals.blockLines;
		indexLinesModulo = searchData.searchTotals.indexLinesModulo;
		
		/* Indexes */
		//const startMsIndexes = (new Date()).getTime();
		const indexesLines = await loadIndexes(searchData);
		//console.log("Indexes done in " + ((new Date()).getTime() - startMsIndexes)); //console.log(indexesLines);

		/* Keywords */
		//const startMsKeywords = (new Date()).getTime();
		const keywordLines = await loadKeywords(searchData);
		//console.log("Keywords done in " + ((new Date()).getTime() - startMsKeywords)); //console.log(keywordLines);

		let searchedLines = indexesLines;
		if (!(keywordLines === null)) {
			searchedLines = createIntersection(searchedLines, keywordLines);	
		}
		const foundLinesCount = getLinesCount(searchedLines);
		
		/* Sort */
		//const startMsSort = (new Date()).getTime();
		const sortLines = await loadSort(searchData);
		//console.log("Sort done in " + ((new Date()).getTime() - startMsSort)); //console.log(sortLines);
		const sortedIndexLines = sortIndexLines(searchedLines, sortLines);
        //console.log("Sorting done in " + ((new Date()).getTime() - startMsSort)); //console.log(sortedIndexLines);
		
		/* Blocks */
		//const startMsBlocks = (new Date()).getTime();
		const resultLines = await loadBlocks(searchData, sortedIndexLines);
		//console.log("Blocks done in " + ((new Date()).getTime() - startMsBlocks)); //console.log(resultLines);

		/* Return callback */
		resultsCallback(searchData, resultLines, foundLinesCount, (new Date()).getTime() - startMs);
	}

	/* Publish public */
	return {
		applyFilters: applyFilters
	}
})();
