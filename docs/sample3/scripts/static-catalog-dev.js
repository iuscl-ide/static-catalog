/*	
╭──────────────────────────────────────────────────────────────────────────────╮
│ static-catalog development/demo                    static-catalog.org - 2019 │
╰──────────────────────────────────────────────────────────────────────────────╯
*/

"use strict";

/* Particular to the page */
const StaticCatalogDev = (() => {

	var pageFieldsFilters;
	var pageFields = {};
	var pageFilters = {};
	var pageFieldLabels = [];
	var pageFieldIndexes = [];
	var pageFieldTotalFilters = [];

	var searchTotals = {};
	
	var $filterCheckboxes;
	var $filters_count;
	var $filter_count_template;
	var $tilesOrList;
	var $no_results_panel;
	var $results_panel;
	var $tileTemplate;
	var $tileFieldTemplate; 
	
	var $sortDropdown;
	
	var $messageArea;
	var $welcomeMessage;
	var $searchingMessage;
	var $noResultsMessage;
	var $successMessage;

	var $resultsPanel;

	var $paginationPages;
	var $paginationResultsPerPage;
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
	
	
	var $filter_accordions;
	var $expand_collapse_menu;
	var areFiltersExpanded;
	
	var filterCountClickEvent;
	var filterCountLabelClickEvent;

	/* On jQuery document loaded completely */
	const init = () => {
		
		/* semantic-ui stuff */
		$paginationResultsPerPage = $("#scp-id--pagination-results-per-page");
//		$('.ui.dropdown').dropdown({
		$paginationResultsPerPage.dropdown({
			 onChange: function(value, text, $selectedItem) {
				 
				 apply(findSearchPagination());
			}
		});
		$sortDropdown = $("#scp-id--sort");
		$sortDropdown.dropdown({
			 onChange: function(value, text, $selectedItem) {
				 
				 apply(findSearchPagination());
			}
		});
		
		$(".overlay").visibility({
			type: "fixed"
		});
		//$("ui.table").tablesort();
		$(".ui.accordion").accordion();

		/* load variables */
		$filterCheckboxes = $("input[id*='sc_filter__']");
		$filters_count = $("#scp-id--filters-count");
		$filter_count_template = $("#scp-id--filter-count-template");
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
		
		
		$filter_accordions = $(".ui.vertical.fluid.accordion.menu");
		$expand_collapse_menu = $("#sc-id--expand-collapse-menu");
		areFiltersExpanded = true;
		
		/* events */
		$("#sc-id--search-button").click( clickEvent => {
			
			apply(findSearchPagination());
        });
		
		$("#sc-id--filter-menu").click( clickEvent => {
			
			window.scrollTo(0, 0);
			apply(findSearchPagination());
        });
		
		$("#sc-id--clear-menu").click( clickEvent => {
			
			window.scrollTo(0, 0);
			$filters_count.empty();
			$filterCheckboxes.each( (index, element) => {
				element.checked = false;
			});
        });
		
		/* Page click */
		let paginationPageClickEvent = ($paginationPage) => {
			
			return (clickEvent) => {
				
				if ($paginationPage.hasClass("active")) {
					return;
				}
				let searchPagination = findSearchPagination();
				searchPagination.paginationPage = parseInt($paginationPage.prop("data-page"), 10); 
				apply(searchPagination);
		    }
		}
		
		$paginationPage1.click(paginationPageClickEvent($paginationPage1));
		$paginationPage2.click(paginationPageClickEvent($paginationPage2));
		$paginationPage3.click(paginationPageClickEvent($paginationPage3));
		
		$paginationFirst.click( clickEvent => {
			
			apply(findSearchPagination());
        });

		$paginationPrevious.click( clickEvent => {
			
			let searchPagination = findSearchPagination();
			searchPagination.paginationPage = pageIndex - 1;
			apply(searchPagination);
        });

		$paginationNext.click( clickEvent => {

			let searchPagination = findSearchPagination();
			searchPagination.paginationPage = pageIndex + 1;
			apply(searchPagination);
        });

		$paginationLast.click( clickEvent => {

			let searchPagination = findSearchPagination();
			searchPagination.paginationPage = lastPageIndex;
			apply(searchPagination);
        });

		$("#sc-id--debug-button").click( clickEvent => {
			
			window.scrollTo(0, 0);
			$filterCheckboxes.each( (index, element) => {
				element.checked = true;
				element.value = element.value.toLocaleString();
			});
        });
		
		$expand_collapse_menu.click( clickEvent => {
			
			if (areFiltersExpanded) {
				$filter_accordions.accordion("close", 0);
				areFiltersExpanded = false;
				$expand_collapse_menu.html("<i class='expand icon'></i> Expand ")
				
			}
			else {
				$filter_accordions.accordion("open", 0);
				areFiltersExpanded = true;
				$expand_collapse_menu.html("<i class='compress icon'></i> Collapse ")
				
			}
			window.scrollTo(0, 0);
        });
		
		$("#sc-id--top-menu").click( clickEvent => {
			window.scrollTo(0, 0);
        });

		$filterCheckboxes.click( clickEvent => {
			
			displayFiltersCount();
        });

		const $see_as_tiles = $("#scp-id--see-as-tiles");
		const $see_as_list = $("#scp-id--see-as-list");
		
		$see_as_tiles.click( clickEvent => {
			const $tile_grid = $("div[data-name=scp-name--tile-grid]");
			
			$see_as_list.removeClass("active");
			$tilesOrList.removeClass().addClass("ui three column grid");
			$tile_grid.removeClass().addClass("ui one column grid");
			$see_as_tiles.removeClass("item").addClass("active item");
        });
		
		$see_as_list.click( clickEvent => {
			const $tile_grid = $("div[data-name=scp-name--tile-grid]");
			
			$see_as_tiles.removeClass("active");
			$tilesOrList.removeClass().addClass("ui one column grid");
			$tile_grid.removeClass().addClass("ui four column grid");
			$see_as_list.removeClass("item").addClass("active item");
        });

		/* Filter name clear */
		filterCountClickEvent = (fieldName) => {
			
			return (clickEvent) => {
				
				for (let checkbox of $filterCheckboxes) {
					if (pageFilters[checkbox.id].fieldName === fieldName) {
						checkbox.checked = false;
					}
				}
				displayFiltersCount();
		    }
		}
		
		/* Filter name display */
		filterCountLabelClickEvent = (fieldName) => {
			
			return (clickEvent) => {
				
				for (let checkbox of $filterCheckboxes) {
					if (pageFilters[checkbox.id].fieldName === fieldName) {
						let $filterAccordion = $($(checkbox).parents(".ui.vertical.fluid.accordion.menu")[0]);
						$filterAccordion.accordion("open", 0);
						$([document.documentElement, document.body]).animate({
					        scrollTop: $filterAccordion.offset().top
					    }, 100);
						break;
					}
				}
		    }
		}
		
		/* The four ones */
		const loadFilterValue = (pageFieldValue, pageField) => {
			
			let pageFilter = {};
			pageFilter.fieldIndex = pageField.index;
			pageFilter.filterIndex = pageFieldValue.index;
			pageFilter.fieldName = pageField.name;
			
			pageFilters[pageFieldValue.identifier] = pageFilter;
		};
		
		/* static-catalog-fields.json */
		$.ajax({
			dataType: "json",
			url: "static-catalog-fields.json",
			mimeType: "application/json",
			success: result => {
				pageFieldsFilters = result;
				let fieldIndex = 0;
				pageFieldsFilters.fields.map( (pageField) => {
					
					pageFields[pageField.index] = pageField;
					pageFieldLabels[fieldIndex] = pageField.label;
					pageFieldIndexes[fieldIndex] = pageField.indexInLine;
					pageFieldTotalFilters[fieldIndex] = pageField.exception_values.length + pageField.more_exception_values.length +
						pageField.values.length + pageField.more_values.length;
					fieldIndex++;

					pageField.exception_values.map((pageFieldValue) => {
						loadFilterValue(pageFieldValue, pageField);
					});
					pageField.more_exception_values.map((pageFieldValue) => {
						loadFilterValue(pageFieldValue, pageField);
					});
					pageField.values.map((pageFieldValue) => {
						loadFilterValue(pageFieldValue, pageField);
					});
					pageField.more_values.map((pageFieldValue) => {
						loadFilterValue(pageFieldValue, pageField);
					});
					
					let totals = pageFieldsFilters.totals;
					searchTotals.totalLines = totals.totalLines;
					searchTotals.blockLines = totals.blockLines;
					searchTotals.indexLinesModulo = totals.indexLinesModulo;
				});
				
				//console.log(pageFieldsFilters);
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
				let pageFilter = pageFilters[checkbox.id];
				
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
		}
		
		
//		let keys = [];
//		let searchFieldsValues = [];
//		
//		let filters = pageFieldsFilters.filters;
//		for (let checkbox of $filterCheckboxes) {
//			if (checkbox.checked) {
//				let filterFieldValue = filters[checkbox.id];
//				
//				let filterField = filterFieldValue.field;
//				let filterValue = filterFieldValue.value;
//				
//				if (!keys.includes(filterField)) {
//					keys.push(filterField);
//					searchFieldsValues.push({
//						"field": filterField,
//						"values": []
//					});
//				}
//
//				let index = keys.indexOf(filterField, 0);
//				searchFieldsValues[index].values.push(filterValue);
//			}
//		}
		//console.log(searchFieldsValues);
		
		return searchFieldsValues;
	}

	/* Filter counts */
	const displayFiltersCount = () => {
		
		$filters_count.empty();
		
		let searchFieldsValues = findSearchFieldValues();
		for (let searchFieldsValue of searchFieldsValues) {
			let $filter_count = $filter_count_template.clone();
			$filter_count.appendTo($filters_count);
			
			let fieldIndex = searchFieldsValue.fieldIndex;
			let pageField = pageFields[fieldIndex];
			$filter_count.find("a[data-name=scp-name--filter-count-name]").html(pageField.label).click(filterCountLabelClickEvent(pageField.name));
			let sumDetail = searchFieldsValue.filterIndexes.length + " (" + pageFieldTotalFilters[fieldIndex] + ")";
			$filter_count.find("div[data-name=scp-name--filter-count-sum]").html(sumDetail);
			$filter_count.find("i[data-name=scp-name--filter-count-close]").click(filterCountClickEvent(pageField.name));
		}
	}

	/* New pagination */
	const findSearchPagination = () => {

		let paginationResultsPerPage = parseInt($paginationResultsPerPage.text(), 10);
		return {
			"paginationPage": 1,
			"paginationResultsPerPage": paginationResultsPerPage
		};
	}
	
	/* Collect selected filter values and send them to the engine */
	const apply = (searchPagination) => {

		$welcomeMessage.hide();
		$noResultsMessage.hide();
		$successMessage.hide();
		$searchingMessage.show();
		$resultsPanel.hide()
		$tilesOrList.empty();
		
		let searchFieldsValues = findSearchFieldValues();
		let searchData = {
			"searchFieldsValues": searchFieldsValues,
			"searchPagination": searchPagination,
			"searchTotals": searchTotals
		};

		StaticCatalog.applyFilters(searchData, resultsCallback);
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
		let linesPerPage = searchData.searchPagination.paginationResultsPerPage;
		pageIndex = searchData.searchPagination.paginationPage;
		lastPageIndex = Math.trunc(foundLinesCount / linesPerPage) + ((foundLinesCount % linesPerPage) > 0 ? 1 : 0);
		
		$paginationPages.html(lastPageIndex.toLocaleString());
		//$("#scp-id--seconds").html(lastPageIndex.toLocaleString());
		
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
		
		/* Result lines */
		
		for (let line of lines) {
			let $tile = $tileTemplate.clone();
			$tile.appendTo($tilesOrList);
			
			let $tileFields = $tile.find("div[data-name=scp-name--tile-grid]")[0];
			
			for (let lineField in line) {
				let $tileField = $tileFieldTemplate.clone();
				$tileField.appendTo($tileFields);
				$tileField.find("span[data-name=scp-name--tile-field-name]").html(pageFieldLabels[lineField]);
				$tileField.find("span[data-name=scp-name--tile-field-value]").html(line[pageFieldIndexes[lineField] - 1]);
			}
//			let $map = $('<div class="mapouter"><div class="gmap_canvas"><iframe width="600" height="500" id="gmap_canvas" src="https://maps.google.com/maps?q=' + line[43] + '%2C%20%20' + line[44] + '&t=&z=7&ie=UTF8&iwloc=&output=embed" frameborder="0" scrolling="no" marginheight="0" marginwidth="0"></iframe>Google Maps Generator by <a href="https://www.embedgooglemap.net">embedgooglemap.net</a></div><style>.mapouter{position:relative;text-align:right;height:500px;width:600px;}.gmap_canvas {overflow:hidden;background:none!important;height:500px;width:600px;}</style></div>');
//			$map.appendTo($tile);
		}
		$resultsPanel.show();
	}

	return {
		init: init,
		apply: apply
	}
})();
