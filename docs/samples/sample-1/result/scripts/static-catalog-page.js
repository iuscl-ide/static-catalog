/*	
╭──────────────────────────────────────────────────────────────────────────────╮
│ static-catalog-page 500000 people sample           static-catalog.org - 2019 │
╰──────────────────────────────────────────────────────────────────────────────╯
*/

/*

0: "Number", csvIndex: 1
1: "Gender", csvIndex: 2​
2: "Name Set", csvIndex: 3​
3: "Title", csvIndex: 4​
4: "Given Name", csvIndex: 5​
5: "Middle Initial", csvIndex: 6​
6: "Surname", csvIndex: 7​
7: "Street Address", csvIndex: 8​
8: "City", csvIndex: 9​
9: "State", csvIndex: 10​
10: "State Full", csvIndex: 11​
11: "Zip Code", csvIndex: 12​
12: "Country", csvIndex: 13​
13: "Country Full", csvIndex: 14​
14: "Email Address", csvIndex: 15​
15: "Username", csvIndex: 16​
16: "Password", csvIndex: 17​
17: "Browser User Agent", csvIndex: 18​
18: "Telephone Number", csvIndex: 19​
19: "Telephone Country Code", csvIndex: 20​
20: "Mothers Maiden", csvIndex: 21​
21: "Birthday", csvIndex: 22​
22: "Age", csvIndex: 23​
23: "Tropical Zodiac", csvIndex: 24​
24: "CC Type", csvIndex: 25​
25: "CC Number", csvIndex: 26​
26: "CVV2", csvIndex: 27​
27: "CC Expires", csvIndex: 28​
28: "National ID", csvIndex: 29​
29: "UPS", csvIndex: 30​
30: "Western Union MTCN", csvIndex: 31​
31: "Money Gram MTCN", csvIndex: 32​
32: "Color", csvIndex: 33​
33: "Occupation", csvIndex: 34​
34: "Company", csvIndex: 35​
35: "Vehicle", csvIndex: 36​
36: "Domain", csvIndex: 37​
37: "Blood Type", csvIndex: 38​
38: "Pounds", csvIndex: 39​
39: "Kilograms", csvIndex: 40​
40: "Feet Inches", csvIndex: 41​
41: "Centimeters", csvIndex: 42​
42: "GUID", csvIndex: 43​
43: "Latitude", csvIndex: 44​
44: "Longitude", csvIndex: 45​

*/

"use strict";

/* Particular to the page */
const StaticCatalogPage = (() => {

	/*
	Parameters of a search:
	
	searchData
		searchSort
			sortFieldIndex
			sortDirection
		searchPagination
			paginationPage
			paginationResultsPerPage
		searchTotals
			totalLines
			blockLines
			indexLinesModulo
		searchFieldsValues []
			fieldIndex
			filterIndexes []
		searchFieldsKeywords []
			fieldIndex
			filterIndex
			keywordPrefix
	*/
	
	/* 8<---------------------------------------- */
	
	var searchSort = {
		"sortFieldIndex": "-1",
		"sortDirection:": ""
	};
	var searchPagination = {
		"paginationPage": 1,
		"paginationResultsPerPage": 10
	};
	const searchTotals = {
		"totalLines": -1,
		"blockLines": -1,
		"indexLinesModulo": -1
	};
	
	const colorCor = {
		"Black": "black",
		"Blue": "blue",
		"Brown": "brown",
		"Green": "green",
		"Orange": "orange",
		"Purple": "purple",
		"Red": "red",
		"Silver": "grey",
		"White": "",
		"Yellow": "yellow"
	}
	
	/* 8<---------------------------------------- */
	
	/* Catalog */
	var pageFields = {};
	var pageFieldsProp = [];
	var pageValueFilterValues = {};
	var pageKeywordFields = {};

	var pageKeywordFieldPrefixValues = {};

	/* Sort */
	var $sortDropdown;
	
	/* Search */
	var $filterDropdowns;
	var $filterSearchboxes;
	var $filterCheckboxes;
	var $filtersCount;
	var $filterCountTemplate;
	var $filterAccordions;

	var areFiltersExpanded;

	var filterCountCloseClickEvent;
	var filterCountLabelClickEvent;

	/* Results */
	var $resultsPanel;
	var $noResultsPanel;
	var $resultsList;
	var $resultTemplate;
	var $resultFieldTemplate; 
	
	/* Messages */
	var $messageArea;
	var $welcomeMessage;
	var $searchingMessage;
	var $noResultsMessage;
	var $successMessage;

	/* Pagination */
	var $paginationPages;
	var $paginationEllipsis1;
	var $paginationEllipsis2;
	var $paginationPage1;
	var $paginationPage2;
	var $paginationPage3;
	var $paginationFirst;
	var $paginationPrevious;
	var $paginationNext;
	var $paginationLast;
	
	var pageIndex = -1;
	var lastPageIndex = -1;

	/* On jQuery document loaded completely */
	const init = () => {
		
		/* SEMANTIC UI initializations */
		
		$(".overlay").visibility({
			type: "fixed"
		});
		//$("ui.table").tablesort();
		$(".ui.accordion").accordion();

		/* Search item template */
		$.fn.search.settings.templates.staticCatalogSearch = function(response) {

			let html = "";
			for (let responseItem of response.results) {
				
				html = html + '<a class="result">\n';
				html = html + '<div class="content">\n';
				
				html = html + '<div class="right floated content" style="float: right !important;">\n';
				html = html + '<div class="ui basic horizontal label" style="margin-right: 0em !important; margin-left: 1em !important; margin-bottom: -2px;">\n';
				html = html + responseItem.description + '\n';
				html = html + '</div>\n';
				html = html + '</div>\n';
				html = html + '<div class="title" style="padding-top: 2px;">' + responseItem.title +'</div>\n';
				html = html + '</div>\n';
				html = html + '</a>\n';
			}
						
			return html;
		} 
		
		/* Filter values */
		$filterDropdowns = $("[data-filter-display-type=dropdown]");
		$filterDropdowns.dropdown({
			onChange: function(value, text, $selectedItem) {

				let $this = $(this);
				let dataValue = $selectedItem.attr("data-value");
				if (dataValue) {
					$this.attr("data-value", dataValue);
				}
				else {
					$this.removeAttr("data-value");
				}
				displayFiltersCount();
			}
		});
		
		/* Filter keywords */
		// https://embed.plnkr.co/plunk/aITHOT
		$filterSearchboxes = $("[data-filter-display-type=searchbox]");
		$filterSearchboxes.search({
		    apiSettings: {
		    	responseAsync: function (settings, callback) {

		    		let $this = $(this);
		    		let id = this.id;
					let pageKeywordField = pageKeywordFields[id];
		    		let searchTerm = settings.urlData.query;
		    		let searchTermLowerCase = searchTerm.toLowerCase();

					if (searchTerm.length === 1) {
						let results = [];
						for (let prefix of Object.keys(pageKeywordField.prefixes)) {
							if (prefix.startsWith(searchTermLowerCase)) {
								let pageKeywordFieldPrefix = pageKeywordField.prefixes[prefix];
								/* TODO the natural order */
								results.push({
									"title": pageKeywordFieldPrefix.title,
									"description": pageKeywordFieldPrefix.count
								});
							}
						}
						setTimeout(function() {
							callback({
								"results": results
							});
						}, 1);
					}
					else {
						let searchTerm2LowerCase = searchTerm.substr(0, 2).toLowerCase();
						let pageKeywordFieldPrefix = pageKeywordField.prefixes[searchTerm2LowerCase];
						if (!pageKeywordFieldPrefix) {
							setTimeout(function() {
								callback({
									"results": []
								});
							}, 1);
						}
						else {
							let pageKeywordPrefixValues = pageKeywordFieldPrefixValues[id];
							if (!pageKeywordPrefixValues) {
								pageKeywordFieldPrefixValues[id] = {};
							}
							let pageKeywordValues = pageKeywordFieldPrefixValues[id][searchTerm2LowerCase];
							if (!pageKeywordValues) {
								/* static-catalog-page-keywords-field-value.json */
								let fieldIndex = pageKeywordFieldPrefix.fieldIndex;
								$.ajax({
									dataType: "json",
									url: "_catalog-page/keywords/keywords-" + fieldIndex + "/static-catalog-page-keywords-" + fieldIndex + "-" + pageKeywordFieldPrefix.filterIndex + ".json",
									mimeType: "application/json",
									success: result => {

										let pageKeywordValues = [];
										for (let keyword of Object.keys(result)) {
											pageKeywordValues.push({
												"title": keyword,
												"description": result[keyword]
											});
										};
										pageKeywordFieldPrefixValues[id][searchTerm2LowerCase] = pageKeywordValues;
										
										let results = [];
										for (let pageKeywordValue of pageKeywordValues) {
											if (pageKeywordValue.title.toLowerCase().startsWith(searchTermLowerCase)) {
												results.push(pageKeywordValue);
											}
										};
										callback({
											"results": results
										});
									}
								});
							}
							else {
								let results = [];
								for (let pageKeywordValue of pageKeywordValues) {
									if (pageKeywordValue.title.toLowerCase().startsWith(searchTermLowerCase)) {
										results.push(pageKeywordValue);
									}
								};
								setTimeout(function() {
									callback({
										"results": results
									});
								}, 1);
							}
						}
					};
		    	}
		    },
		    maxResults: 100,
		    type: 'staticCatalogSearch'
		});

		/* Sort */
		$sortDropdown = $("#scp-id--sort");
		$sortDropdown.dropdown({
			 onChange: function(value, text, $selectedItem) {
				 
				 searchSort.sortFieldIndex = $selectedItem.attr("data-sort-field-index");
				 searchSort.sortDirection = $selectedItem.attr("data-sort-direction");
				 searchPagination.paginationPage = 1;
				 apply();
			}
		});

		/* Pagination */
		$("#scp-id--pagination-results-per-page").dropdown({
			 onChange: function(value, text, $selectedItem) {
				 
				 searchPagination.paginationPage = 1;
				 let paginationResultsPerPage = parseInt(text, 10);
				 searchPagination.paginationResultsPerPage = paginationResultsPerPage;
				 apply();
			}
		});
		
		/* Load controls variables */
		$filterCheckboxes = $("input[id*='sc_filter__']");
		$filtersCount = $("#scp-id--filters-count");
		$filterCountTemplate = $("#scp-id--filter-count-template");
		$resultsList = $("#scp-id--results-list");
		$resultTemplate = $("#scp-id--result-template");
		$resultFieldTemplate = $("#scp-id--result-field-template");
		
		$messageArea = $("#scp-id--message-area");
		$welcomeMessage = $("#scp-id--welcome-message");
		$searchingMessage = $("#scp-id--searching-message");
		$noResultsMessage = $("#scp-id--no-results-message");
		$successMessage = $("#scp-id--success-message");
		
		$resultsPanel = $("#scp-id--results");
		
		$paginationPages = $("#scp-id--pagination-pages");
		$paginationFirst = $("#scp-id--pagination-first");
		$paginationPrevious = $("#scp-id--pagination-previous");
		$paginationNext = $("#scp-id--pagination-next");
		$paginationLast = $("#scp-id--pagination-last");
		$paginationEllipsis1 = $("#scp-id--pagination-ellipsis-1");
		$paginationEllipsis2 = $("#scp-id--pagination-ellipsis-2");
		$paginationPage1 = $("#scp-id--pagination-page-1");
		$paginationPage2 = $("#scp-id--pagination-page-2");
		$paginationPage3 = $("#scp-id--pagination-page-3");
		
		$filterAccordions = $(".ui.vertical.fluid.accordion.menu");
		areFiltersExpanded = true;
		
		/* "Search" button */
		$("#sc-id--search-button").click( clickEvent => {
			
			searchPagination.paginationPage = 1;
			apply();
        });

		/* Check value */
		$filterCheckboxes.click( clickEvent => {
			
			displayFiltersCount();
        });
		
		/* "Search" lateral menu item */
		$("#sc-id--filter-menu").click( clickEvent => {
			
			window.scrollTo(0, 0);
			searchPagination.paginationPage = 1;
			apply();
        });

		/* "Clear" lateral menu item */
		$("#sc-id--clear-menu").click( clickEvent => {
			
			window.scrollTo(0, 0);
			$filtersCount.empty();
			$sortDropdown.dropdown("set selected", "CSV order");
			$filterCheckboxes.each( (index, element) => {
				element.checked = false;
			});
			$filterDropdowns.each( (index, element) => {
				let $filterDropdown = $(element);
				$filterDropdown.dropdown("set selected", "");
				$filterDropdown.removeAttr("data-value");
			});
			$filterSearchboxes.each( (index, element) => {
				let $filterSearchbox = $(element);
				$filterSearchbox.search("set value", "");
			});
        });

		/* "Expand / Collapse" lateral menu item */
		const $expandCollapseMenu = $("#sc-id--expand-collapse-menu");
		$expandCollapseMenu.click( clickEvent => {
			
			if (areFiltersExpanded) {
				$filterAccordions.accordion("close", 0);
				areFiltersExpanded = false;
				$expandCollapseMenu.html("<i class='expand icon'></i> Expand")
			}
			else {
				$filterAccordions.accordion("open", 0);
				areFiltersExpanded = true;
				$expandCollapseMenu.html("<i class='compress icon'></i> Collapse")
				
			}
			window.scrollTo(0, 0);
        });

		/* "Top" lateral menu item */
		$("#sc-id--top-menu").click( clickEvent => {
			window.scrollTo(0, 0);
        });
		
		/* Pagination clicks */
		let paginationPageClickEvent = ($paginationPage) => {
			
			return (clickEvent) => {
				
				if ($paginationPage.hasClass("active")) {
					return;
				}
				searchPagination.paginationPage = parseInt($paginationPage.prop("data-page"), 10); 
				apply();
		    }
		}
		
		$paginationPage1.click(paginationPageClickEvent($paginationPage1));
		$paginationPage2.click(paginationPageClickEvent($paginationPage2));
		$paginationPage3.click(paginationPageClickEvent($paginationPage3));
		
		$paginationFirst.click( clickEvent => {
			
			searchPagination.paginationPage = 1;
			apply();
        });
		$paginationPrevious.click( clickEvent => {
			
			searchPagination.paginationPage = pageIndex - 1;
			apply();
        });
		$paginationNext.click( clickEvent => {

			searchPagination.paginationPage = pageIndex + 1;
			apply();
        });
		$paginationLast.click( clickEvent => {

			searchPagination.paginationPage = lastPageIndex;
			apply();
        });

		/* Debugs */
		$("#sc-id--debug-all-values-button").click( clickEvent => {
			
			window.scrollTo(0, 0);
			$filterCheckboxes.each( (index, element) => {
				element.checked = true;
				element.value = element.value.toLocaleString();
			});
        });

//		$("#sc-id--debug-first-value-button").click( clickEvent => {
//			
//			window.scrollTo(0, 0);
//			let names = {};
//			$filterCheckboxes.each( (index, element) => {
//				
//				let elementName = element.name;
//				if (!names[elementName]) {
//					names[elementName] = elementName;
//					element.checked = true;
//					element.value = element.value.toLocaleString();
//				}
//			});
//        });

		/* Filter count close - check false all */
		filterCountCloseClickEvent = (fieldName) => {
			
			return (clickEvent) => {
				
				for (let checkbox of $filterCheckboxes) {
					if (pageValueFilterValues[checkbox.id].fieldName === fieldName) {
						checkbox.checked = false;
					}
				}
				for (let dropdown of $filterDropdowns) {
					let $filterDropdown = $(dropdown);
					if (pageValueFilterValues[$filterDropdown.attr("data-value")].fieldName === fieldName) {
						$filterDropdown.dropdown('set selected', '');
						$filterDropdown.removeAttr("data-value");
					}
				}
				displayFiltersCount();
		    }
		}
		
		/* Filter count label - go to filter */
		filterCountLabelClickEvent = (fieldName) => {
			
			return (clickEvent) => {
				
				for (let checkbox of $filterCheckboxes) {
					if (pageValueFilterValues[checkbox.id].fieldName === fieldName) {
						let $filterAccordion = $($(checkbox).parents(".ui.vertical.fluid.accordion.menu")[0]);
						$filterAccordion.accordion("open", 0);
						$([document.documentElement, document.body]).animate({
					        scrollTop: $filterAccordion.offset().top
					    }, 100);
						break;
					}
				}
				for (let dropdown of $filterDropdowns) {
					let $filterDropdown = $(dropdown);
					if (pageValueFilterValues[$filterDropdown.attr("data-value")].fieldName === fieldName) {
						let $filterAccordion = $($filterDropdown.parents(".ui.vertical.fluid.accordion.menu")[0]);
						$filterAccordion.accordion("open", 0);
						$([document.documentElement, document.body]).animate({
					        scrollTop: $filterAccordion.offset().top
					    }, 100);
						break;
					}
				}
		    }
		}
		
		/* static-catalog-fields.json */
		$.ajax({
			dataType: "json",
			url: "_catalog-page/static-catalog-page.json",
			mimeType: "application/json",
			success: result => {

				let fieldIndex = 0;
				result.fields.map( (pageField) => {
					
					/* Field */
					pageFields[pageField.index] = pageField;
					pageFieldsProp[fieldIndex++] = {
						"label": pageField.label,
						"csvIndex": pageField.csvIndex,
						"totalFilters": pageField.values.length
					};
					
					if (pageField.filterType === "keywords") {
						/* Filter keywords */
						
						let pageKeywordFieldPrefixes = {};
						pageField.values.map((pageFieldValue) => {
							
							pageKeywordFieldPrefixes[pageFieldValue.name.toLowerCase()] = {
								"fieldIndex": pageField.index,
								"filterIndex": pageFieldValue.index,
								"title": pageFieldValue.label,
								"count": pageFieldValue.count
							};
						});
						pageKeywordFields[pageField.identifier] = {
							"prefixes": pageKeywordFieldPrefixes
						}
					}
					else {
						/* Filter values */
						pageField.values.map((pageFieldValue) => {
							
							pageValueFilterValues[pageFieldValue.identifier] = {
								"fieldIndex": pageField.index,
								"filterIndex": pageFieldValue.index,
								"fieldName": pageField.name
							};
						});
					}
					
					/* Totals */
					const totals = result.totals;
					searchTotals.totalLines = totals.totalLines;
					searchTotals.blockLines = totals.blockLines;
					searchTotals.indexLinesModulo = totals.indexLinesModulo;
				});
				
				displayFiltersCount();				
			}
		});
	}

	/* Collect selected filter values */
	const findSearchFieldValues = () => {

		let keys = [];
		let searchFieldsValues = [];
		
		for (let checkbox of $filterCheckboxes) {
			if (checkbox.checked) {
				createSearchFieldValue(checkbox.id, pageValueFilterValues, keys, searchFieldsValues);
			}
		}
		for (let dropdown of $filterDropdowns) {
			let dataValue = $(dropdown).attr("data-value");
			if (dataValue) {
				createSearchFieldValue(dataValue, pageValueFilterValues, keys, searchFieldsValues);
			}
		}
		
		return searchFieldsValues;
	}
	
	/* Collect selected filter values */
	const createSearchFieldValue = (valId, pageValueFilterValues, keys, searchFieldsValues) => {

		let pageFilter = pageValueFilterValues[valId];
		
		let fieldIndex = pageFilter.fieldIndex;
		let filterIndex = pageFilter.filterIndex;
		
		if (!keys.includes(fieldIndex)) {
			keys.push(fieldIndex);
			searchFieldsValues.push({
				"fieldIndex": fieldIndex,
				"filterIndexes": []
			});
		}
		let index = keys.indexOf(fieldIndex, 0);
		searchFieldsValues[index].filterIndexes.push(filterIndex);
	}

	/* Collect selected filter keywords */
	const findSearchFieldKeywords = () => {

		let searchFieldsKeywords = [];
		
		for (let searchbox of $filterSearchboxes) {

			let searchTerm = $(searchbox).search("get value");
			let searchTerm2LowerCase = searchTerm.substr(0, 2).toLowerCase();
			if (searchTerm2LowerCase.length >= 2) {
				let pageKeywordField = pageKeywordFields[searchbox.id];
				let prefixFilter = pageKeywordField.prefixes[searchTerm2LowerCase];
				if (prefixFilter) {
					searchFieldsKeywords.push({
						"fieldIndex": prefixFilter.fieldIndex,
						"filterIndex": prefixFilter.filterIndex,
						"keywordPrefix": searchTerm
					})
				}
			}
		}
		
		return searchFieldsKeywords;
	}

	/* Filter counts */
	const displayFiltersCount = () => {
		
		$filtersCount.empty();
		
		let searchFieldsValues = findSearchFieldValues();
		for (let searchFieldsValue of searchFieldsValues) {
			let $filterCount = $filterCountTemplate.clone();
			$filterCount.appendTo($filtersCount);
			
			let fieldIndex = searchFieldsValue.fieldIndex;
			let pageField = pageFields[fieldIndex];
			$filterCount.find("a[data-name=scp-name--filter-count-name]").html(pageField.label).click(filterCountLabelClickEvent(pageField.name));
			let sumDetail = searchFieldsValue.filterIndexes.length + " (" + pageFieldsProp[fieldIndex].totalFilters + ")";
			$filterCount.find("div[data-name=scp-name--filter-count-sum]").html(sumDetail);
			$filterCount.find("i[data-name=scp-name--filter-count-close]").click(filterCountCloseClickEvent(pageField.name));
		}
	}

	/* Pagination */
	const updatePagination = (searchData, foundLinesCount) => {

		/* Pagination */
		let linesPerPage = searchData.searchPagination.paginationResultsPerPage;
		pageIndex = searchData.searchPagination.paginationPage;
		lastPageIndex = Math.trunc(foundLinesCount / linesPerPage) + ((foundLinesCount % linesPerPage) > 0 ? 1 : 0);
		
		$paginationPages.html(lastPageIndex.toLocaleString());
		
		if (pageIndex === 1) {
			$paginationFirst.removeClass().addClass("disabled item");
			$paginationPrevious.removeClass().addClass("disabled item");
		}
		else {
			$paginationFirst.removeClass().addClass("item");
			$paginationPrevious.removeClass().addClass("item");
		}

		if ((pageIndex === lastPageIndex) || (lastPageIndex === 3)) {
			$paginationNext.removeClass().addClass("disabled item");
			$paginationLast.removeClass().addClass("disabled item");
		}
		else {
			$paginationNext.removeClass().addClass("item");
			$paginationLast.removeClass().addClass("item");
		}

		if ((pageIndex === 1) || (pageIndex === 2))  {
			$paginationEllipsis1.hide();
		}
		else {
			$paginationEllipsis1.show();
		}
		
		if ((pageIndex === lastPageIndex) || (pageIndex === (lastPageIndex - 1)) || (lastPageIndex === 3))  {
			$paginationEllipsis2.hide();
		}
		else {
			$paginationEllipsis2.show();
		}

		if (pageIndex === 1) {
			$paginationPage1.removeClass().addClass("active item");
			$paginationPage1.html("1");
			$paginationPage1.prop("data-page", 1);
			if (lastPageIndex >= 2) {
				$paginationPage2.show();
				$paginationPage2.removeClass().addClass("item");
				$paginationPage2.html("2");
				$paginationPage2.prop("data-page", 2);
			}
			else {
				$paginationPage2.hide();
			}
			if (lastPageIndex >= 3) {
				$paginationPage3.show();
				$paginationPage3.removeClass().addClass("item");
				$paginationPage3.html("3");
				$paginationPage3.prop("data-page", 3);
			}
			else {
				$paginationPage3.hide();
			}
		}
		else {
			$paginationPage1.removeClass().addClass("item");
		}

		if (pageIndex === lastPageIndex) {
			if (lastPageIndex >= 3) {
				$paginationPage3.show();
				$paginationPage3.removeClass().addClass("active item");
				$paginationPage3.html((lastPageIndex).toLocaleString());
				$paginationPage3.prop("data-page", lastPageIndex);
				
				$paginationPage2.show();
				$paginationPage2.removeClass().addClass("item");
				$paginationPage2.html((lastPageIndex - 1).toLocaleString());
				$paginationPage2.prop("data-page", lastPageIndex - 1);

				$paginationPage1.show();
				$paginationPage1.removeClass().addClass("item");
				$paginationPage1.html((lastPageIndex - 2).toLocaleString());
				$paginationPage1.prop("data-page", lastPageIndex - 2);
			}
			
			if (lastPageIndex == 2) {
				$paginationPage3.hide();

				$paginationPage2.show();
				$paginationPage2.removeClass().addClass("active item");
				$paginationPage2.html("2");
				$paginationPage2.prop("data-page", 2);

				$paginationPage1.show();
				$paginationPage1.removeClass().addClass("item");
				$paginationPage1.html("1");
				$paginationPage1.prop("data-page", 1);
			}

			if (lastPageIndex == 1) {
				$paginationPage3.hide();
				$paginationPage2.hide();

				$paginationPage1.show();
				$paginationPage1.removeClass().addClass("active item");
				$paginationPage1.html("1");
				$paginationPage1.prop("data-page", 1);
			}
		}
		
		if (!((pageIndex === 1) || (pageIndex === lastPageIndex))) {
			$paginationPage1.show();
			$paginationPage1.removeClass().addClass("item");
			$paginationPage1.html((pageIndex - 1).toLocaleString());
			$paginationPage1.prop("data-page", pageIndex - 1);

			$paginationPage2.show();
			$paginationPage2.removeClass().addClass("active item");
			$paginationPage2.html((pageIndex).toLocaleString());
			$paginationPage2.prop("data-page", pageIndex);

			$paginationPage3.show();
			$paginationPage3.removeClass().addClass("item");
			$paginationPage3.html((pageIndex + 1).toLocaleString());
			$paginationPage3.prop("data-page", pageIndex + 1);
		}
	}
	
	/* Back in page from search */
	const resultsCallback = (searchData, lines, foundLinesCount, totalSearchMs) => {

		/* Message */
		$searchingMessage.hide();
		if (foundLinesCount === 0) {
			$noResultsMessage.show();
			$("#scp-id--no-results-seconds").html((totalSearchMs / 1000).toLocaleString());
			return;
		}
		$successMessage.show();
		$("#scp-id--returned-lines").html(lines.length.toLocaleString());
		$("#scp-id--found-lines").html(foundLinesCount.toLocaleString());
		$("#scp-id--seconds").html((totalSearchMs / 1000).toLocaleString());

		/* Pagination */
		updatePagination(searchData, foundLinesCount);
		
		/* Result lines */
		for (let line of lines) {
			let $result = $resultTemplate.clone();
			$result.appendTo($resultsList);
			
			$($result.find("div[data-name=scp-name--result-segment]")[0]).addClass("ui bottom attached").addClass(colorCor[line[32]]).addClass("segment");
			
			$result.find("span[data-name=scp-name--result-field-title]").html(line[3]);
			$result.find("span[data-name=scp-name--result-field-given-name]").html(line[4]);
			$result.find("span[data-name=scp-name--result-field-middle-initial]").html(line[5]);
			$result.find("span[data-name=scp-name--result-field-surname]").html(line[6]);
			$result.find("span[data-name=scp-name--result-field-national-id]").html(line[28]);

			$result.find("span[data-name=scp-name--result-field-birthday]").html(line[21]);
			$result.find("span[data-name=scp-name--result-field-age]").html(line[22]);
			$result.find("span[data-name=scp-name--result-field-tropical-zodiac]").html(line[23]);
			$result.find("span[data-name=scp-name--result-field--mothers-maiden]").html(line[20]);

			$result.find("span[data-name=scp-name--result-field-occupation]").html(line[33]);
			$result.find("span[data-name=scp-name--result-field-company]").html(line[34]);
			$result.find("span[data-name=scp-name--result-field-domain]").html(line[36]);

			$result.find("span[data-name=scp-name--result-field-blood-type]").html(line[37]);
			$result.find("span[data-name=scp-name--result-field-pounds]").html(line[38]);
			$result.find("span[data-name=scp-name--result-field-kilograms]").html(line[39]);
			$result.find("span[data-name=scp-name--result-field-feet-inches]").html(line[40]);
			$result.find("span[data-name=scp-name--result-field-centimeters]").html(line[41]);
			let vehicle = line[35];
			$result.find("span[data-name=scp-name--result-field-vehicle]").html(vehicle);
			$($result.find("a[data-name=scp-name--result-field-vehicle-href]")[0]).attr("href",
					"https://www.google.com/search?q=" + vehicle.replace(/ /gi, "-") + "&tbm=isch");
			
			$result.find("span[data-name=scp-name--result-field-street-address]").html(line[7]);
			$result.find("span[data-name=scp-name--result-field-city]").html(line[8]);
			$result.find("span[data-name=scp-name--result-field-state-full]").html(line[10]);
			$result.find("span[data-name=scp-name--result-field-zip-code]").html(line[11]);

			$result.find("#gmap_canvas").prop("src", "https://maps.google.com/maps?q=" + line[43] +
					"%2C%20%20" + line[44] + "&t=&z=7&ie=UTF8&iwloc=&output=embed");

			$result.find("span[data-name=scp-name--result-field-email-address]").html(line[14]);
			$result.find("span[data-name=scp-name--result-field-telephone-number]").html(line[18]);
		}
		$resultsPanel.show();
	}
	
	/* Collect selected filter values and send them to the engine */
	const apply = () => {

		$welcomeMessage.hide();
		$noResultsMessage.hide();
		$successMessage.hide();
		$searchingMessage.show();
		$resultsPanel.hide()
		$resultsList.empty();
		
		let searchFieldsValues = findSearchFieldValues();
		let searchFieldKeywords = findSearchFieldKeywords();
		let searchData = {
			"searchSort": searchSort,
			"searchFieldsValues": searchFieldsValues,
			"searchFieldKeywords": searchFieldKeywords,
			"searchPagination": searchPagination,
			"searchTotals": searchTotals
		};

		StaticCatalog.applyFilters(searchData, resultsCallback);
	}
	
	return {
		init: init,
		apply: apply
	}
})();
