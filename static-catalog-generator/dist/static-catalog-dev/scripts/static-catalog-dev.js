/**	
╭──────────────────────────────────────╮
│ static-catalog-development-demo      │
╰──────────────────────────────────────╯
*/

"use strict";

/** Particular to the page */
const StaticCatalogDev = (() => {

	var pageFieldsFilters;
	var pageFields = {};

	var $filterCheckboxes;
	var $filters_count;
	var $filter_count_template;
	var $tiles_or_list;
	var $no_results_panel;
	var $results_panel;
	var $tile_template;
	var $tile_field_template; 
	
	var filterCountClickEvent;
	var filterCountLabelClickEvent;
	
	/** On jQuery document loaded completely */
	const init = () => {
		
		/* static-catalog-fields.json */
		$.ajax({
			dataType: "json",
			url: "static-catalog-fields.json",
			mimeType: "application/json",
			success: result => {
				pageFieldsFilters = result;
				pageFieldsFilters.fields.map( (pageField) => {
					pageFields[pageField.name] = pageField;
				});
//				console.log(pageFieldsFilters);
			}
		});

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
		$tiles_or_list = $("#scp-id--tiles-or-list");
		$no_results_panel = $("#scp-id--no-results-panel");
		$results_panel = $("#scp-id--results-panel");
		$tile_template = $("#scp-id--tile-template");
		$tile_field_template = $("#scp-id--tile-field-template");
		
		/* events */
		$("#sc-id--search-button").click( clickEvent => {
			
			apply();
        });
		
		$("#sc-id--filter-menu").click( clickEvent => {
			
			window.scrollTo(0, 0);
//			apply();
			displayFiltersCount();

        });
		
		$("#sc-id--clear-menu").click( clickEvent => {
			
			window.scrollTo(0, 0);
			$filters_count.empty();
			$filterCheckboxes.each( (index, element) => {
				element.checked = false;
			});
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
			const $tile_grid = $("div[name=scp-name--tile-grid]");
			
			$see_as_list.removeClass("active");
			$tiles_or_list.removeClass().addClass("ui three column grid");
			$tile_grid.removeClass().addClass("ui one column grid");
			$see_as_tiles.removeClass("item").addClass("active item");
        });
		
		$see_as_list.click( clickEvent => {
			const $tile_grid = $("div[name=scp-name--tile-grid]");
			
			$see_as_tiles.removeClass("active");
			$tiles_or_list.removeClass().addClass("ui one column grid");
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
						let scrollElement = $(checkbox).parents(".ui.vertical.fluid.accordion.menu")[0];
						$([document.documentElement, document.body]).animate({
					        scrollTop: $(scrollElement).offset().top
					    }, 100);
						break;
					}
				}
		    }
		}
	}

	/** Back in page from search */
	const resultsCallback = (results) => {
		
		$no_results_panel.hide();
		
		for (let index = 0; index < 10; index++) {
			
			$tile = $tile_template.clone();
			$tile.appendTo($tiles_or_list);
			
			$tile_fields = $tile.find("div[name=scp-name--tile-grid]")[0];
			
			let result = results[index];
			for (let resultField in result) {
				//console.log(resultField + " " + result[resultField]);
				
				$tile_field = $tile_field_template.clone();
				$tile_field.appendTo($tile_fields);
				$tile_field.find("span[name=scp-name--tile-field-name]").html(resultField);
				$tile_field.find("span[name=scp-name--tile-field-value]").html(result[resultField]);
			}
		}
		
		$results_panel.show();
	}

	/** Collect selected filter values */
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

	/** Filter counts */
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

	/** Collect selected filter values and send them to the engine */
	const apply = () => {

		$tiles_or_list.empty();
		
		let searchFieldsValues = findSearchFieldValues();
		let searchData = {
			"searchFieldsValues": searchFieldsValues
		};

		StaticCatalog.applyFilters(searchData, resultsCallback);
	}

	return {
		init: init,
		apply: apply
	}
})();
