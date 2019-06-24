/*	
╭──────────────────────────────────────────────────────────────────────────────╮
│ static-catalog development/demo                    static-catalog.org - 2019 │
╰──────────────────────────────────────────────────────────────────────────────╯
*/

"use strict";

/* Particular to the page */
const StaticCatalogDev = (() => {

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
	
	/* 8<---------------------------------------- */
	
	/* Catalog */
	var pageFields = {};
	var pageFieldsProp = [];
	var pageValueFilterValues = {};
	var pageKeywordFields = {};

	var pageKeywordFieldPrefixValues = {};

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
	var $tilesOrList;
	var $tileTemplate;
	var $tileFieldTemplate; 
	
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

//		$.fn.search.settings.templates : {
//			escape: function(string) {
//				// returns escaped string for injected results
//			},
//			message: function(message, type) {
//				// returns html for message with given message and type
//			},
//			category: function(response) {
//				// returns results html for category results
//			},
//			standard: function(response) {
//				// returns results html for standard results
//			}
//		}
		
		$.fn.search.settings.templates.staticCatalogSearch = function(response) {

			console.log(response);
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
					console.log(pageKeywordField);
		    		console.log(searchTerm);

					let results = [];
					if (searchTerm.length === 1) {
						$this.removeAttr("data-prefix");
						for (let prefix of Object.keys(pageKeywordField.prefixes)) {
							if (prefix.startsWith(searchTerm)) {
								results.push({
									"title": prefix,
									"description": pageKeywordField.prefixes[prefix].count
								});
							}
						}
					}
					else {
						let currentPrefix = $this.attr("data-prefix");
						let prefix = searchTerm.substr(0, 2).toLowerCase();
						let pageKeywordFieldPrefix = pageKeywordField.prefixes[prefix];
						if (!pageKeywordFieldPrefix) {
//							callback([]);
//							return;
//							return {
//								"results": []
//							};
						}
						let pageKeywordPrefixValues = pageKeywordFieldPrefixValues[id];
						if (!pageKeywordPrefixValues) {
							pageKeywordFieldPrefixValues[id] = {};
						}
						let pageKeywordValues = pageKeywordFieldPrefixValues[id][prefix];
						if (!pageKeywordValues) {
							/* static-catalog-page-keywords-field-value.json */
							$.ajax({
								dataType: "json",
								url: "_catalog-page/static-catalog-page-keywords-" + pageKeywordFieldPrefix.fieldIndex + "-" + pageKeywordFieldPrefix.filterIndex + ".json",
								mimeType: "application/json",
//								async: false,
								success: result => {
									console.log(result);
//										console.log($this);
									let results = [];
									for (let keyword of Object.keys(result)) {
										if (keyword.toLowerCase().startsWith(searchTerm)) {
											results.push({
												"title": keyword,
												"description": result[keyword]
											});
										}
									};
									callback({
										"results": results
									});
								}
							});
//							console.log("dupa");
//							while (results.length === 0) {
//								setTimeout(()=>{}, 1000);
//							};
//							return {
//								"results": results
//							};
						}
					};
					setTimeout(function() {
				        callback({
							"results": results
							});
				      }, 1);
					
//					callback({
//						"results": results
//					});
//					return {
//						"results": results
//					};
		    	}
		    },
		    onResponse : function(resp) {
		    	console.log("resp");
//		    	return {
//		    		results: response.myresults
//		    	}
		    },
		    maxResults: 100,
		    type: 'staticCatalogSearch'
		});

		/* Sort */
		$("#scp-id--sort").dropdown({
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
		$tilesOrList = $("#scp-id--tiles-or-list");
		$tileTemplate = $("#scp-id--tile-template");
		$tileFieldTemplate = $("#scp-id--tile-field-template");
		
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
			$filterCheckboxes.each( (index, element) => {
				element.checked = false;
			});
			$filterDropdowns.each( (index, element) => {
				let $filterDropdown = $(element);
				$filterDropdown.dropdown('set selected', '');
				$filterDropdown.removeAttr("data-value");
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
		$("#sc-id--debug-button").click( clickEvent => {
			
			window.scrollTo(0, 0);
			$filterCheckboxes.each( (index, element) => {
				element.checked = true;
				element.value = element.value.toLocaleString();
			});
        });

		/* Results list / tiles */
		const $seeAsTiles = $("#scp-id--see-as-tiles");
		const $seeAsList = $("#scp-id--see-as-list");
		
		$seeAsTiles.click( clickEvent => {
			
			const $tileGrid = $("div[data-name=scp-name--tile-grid]");
			
			$seeAsList.removeClass("active");
			$tilesOrList.removeClass().addClass("ui three column grid");
			$tileGrid.removeClass().addClass("ui one column grid");
			$seeAsTiles.removeClass("item").addClass("active item");
        });
		
		$seeAsList.click( clickEvent => {
			
			const $tileGrid = $("div[data-name=scp-name--tile-grid]");
			
			$seeAsTiles.removeClass("active");
			$tilesOrList.removeClass().addClass("ui one column grid");
			$tileGrid.removeClass().addClass("ui four column grid");
			$seeAsList.removeClass("item").addClass("active item");
        });

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
				//console.log(result);
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
							
							pageKeywordFieldPrefixes[pageFieldValue.name] = {
								"fieldIndex": pageField.index,
								"filterIndex": pageFieldValue.index,
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
			let $tile = $tileTemplate.clone();
			$tile.appendTo($tilesOrList);
			
			let $tileFields = $tile.find("div[data-name=scp-name--tile-grid]")[0];
			
			for (let lineField in line) {
				let $tileField = $tileFieldTemplate.clone();
				$tileField.appendTo($tileFields);
				$tileField.find("span[data-name=scp-name--tile-field-name]").html(pageFieldsProp[lineField].label);
				$tileField.find("span[data-name=scp-name--tile-field-value]").html(line[pageFieldsProp[lineField].csvIndex - 1]);
			}
//			let $map = $('<div class="mapouter"><div class="gmap_canvas"><iframe width="600" height="500" id="gmap_canvas" src="https://maps.google.com/maps?q=' + line[43] + '%2C%20%20' + line[44] + '&t=&z=7&ie=UTF8&iwloc=&output=embed" frameborder="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>Google Maps Generator by <a href="https://www.embedgooglemap.net">embedgooglemap.net</a></div><style>.mapouter{position:relative;text-align:right;height:500px;width:600px;}.gmap_canvas {overflow:hidden;background:none!important;height:500px;width:600px;}</style></div>');
//			$map.appendTo($tile);
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
		$tilesOrList.empty();
		
		let searchFieldsValues = findSearchFieldValues();
		let searchData = {
			"searchSort": searchSort,
			"searchFieldsValues": searchFieldsValues,
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
