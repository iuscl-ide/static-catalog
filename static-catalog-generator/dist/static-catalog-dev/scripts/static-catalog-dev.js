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
	var pageFieldLabels = [];

	var $filterCheckboxes;
	var $filters_count;
	var $filter_count_template;
	var $tilesOrList;
	var $no_results_panel;
	var $results_panel;
	var $tileTemplate;
	var $tileFieldTemplate; 
	
	var $messageArea;
	var $welcomeMessage;
	var $searchingMessage;
	var $noResultsMessage;
	var $successMessage;

	var $resultsPanel;
	
	var $filter_accordions;
	var $expand_collapse_menu;
	var areFiltersExpanded;
	
	var filterCountClickEvent;
	var filterCountLabelClickEvent;

	/* debug */
	var isDebug = true;
	
	/* On jQuery document loaded completely */
	const init = () => {
		
		/* semantic-ui stuff */
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
		
		
		$filter_accordions = $(".ui.vertical.fluid.accordion.menu");
		$expand_collapse_menu = $("#sc-id--expand-collapse-menu");
		areFiltersExpanded = true;
		
		/* events */
		$("#sc-id--search-button").click( clickEvent => {
			
			apply();
        });
		
		$("#sc-id--filter-menu").click( clickEvent => {
			
			window.scrollTo(0, 0);
			apply();
        });
		
		$("#sc-id--clear-menu").click( clickEvent => {
			
			window.scrollTo(0, 0);
			$filters_count.empty();
			$filterCheckboxes.each( (index, element) => {
				element.checked = false;
			});
        });
		
		if (isDebug) {
			$("#sc-id--debug-button").click( clickEvent => {
				
				window.scrollTo(0, 0);
				$filterCheckboxes.each( (index, element) => {
					element.checked = true;
					element.value = element.value.toLocaleString();
				});
	        });
		}

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
					if (pageFieldsFilters.filters[checkbox.id].field === fieldName) {
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
					if (pageFieldsFilters.filters[checkbox.id].field === fieldName) {
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
		
		/* static-catalog-fields.json */
		$.ajax({
			dataType: "json",
			url: "static-catalog-fields.json",
			mimeType: "application/json",
			success: result => {
				pageFieldsFilters = result;
				let fieldIndex = 0;
				pageFieldsFilters.fields.map( (pageField) => {
					pageFields[pageField.name] = pageField;
					pageFieldLabels[fieldIndex++] = pageField.label;
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
		
		let filters = pageFieldsFilters.filters;
		for (let checkbox of $filterCheckboxes) {
			if (checkbox.checked) {
				let filterFieldValue = filters[checkbox.id];
				
				let filterField = filterFieldValue.field;
				let filterValue = filterFieldValue.value;
				
				if (!keys.includes(filterField)) {
					keys.push(filterField);
					searchFieldsValues.push({
						"field": filterField,
						"values": []
					});
				}

				let index = keys.indexOf(filterField, 0);
				searchFieldsValues[index].values.push(filterValue);
			}
		}
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
			
			let fieldName = searchFieldsValue.field;
			let pageField = pageFields[fieldName];
			$filter_count.find("a[name=scp-name--filter-count-name]").html(pageField.label).click(filterCountLabelClickEvent(fieldName));
			let sumDetail = searchFieldsValue.values.length + " (" + pageField.values.length + ")";
			$filter_count.find("div[name=scp-name--filter-count-sum]").html(sumDetail);
			$filter_count.find("i[name=scp-name--filter-count-close]").click(filterCountClickEvent(fieldName));
		}
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
			"searchFieldsValues": searchFieldsValues,
			"paginationFirst": false,
			"paginationPrevious": false,
			"paginationPage": 1,
			"paginationNext": false,
			"paginationLast": false,
			"paginationResultsPerPage": 10
		};

		StaticCatalog.applyFilters(searchData, resultsCallback);
	}

	/* Back in page from search */
	const resultsCallback = (lines, foundLinesCount, totalSearchMs) => {

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
		
		for (let line of lines) {
			let $tile = $tileTemplate.clone();
			$tile.appendTo($tilesOrList);
			
			let $tileFields = $tile.find("div[data-name=scp-name--tile-grid]")[0];
			
			for (let lineField in line) {
				let $tileField = $tileFieldTemplate.clone();
				$tileField.appendTo($tileFields);
				$tileField.find("span[name=scp-name--tile-field-name]").html(pageFieldLabels[lineField]);
				$tileField.find("span[name=scp-name--tile-field-value]").html(line[lineField]);
			}
		}
		$resultsPanel.show();
	}

	return {
		init: init,
		apply: apply
	}
})();
