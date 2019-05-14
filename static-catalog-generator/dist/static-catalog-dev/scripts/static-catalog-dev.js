/**	
╭──────────────────────────────────────╮
│ static-catalog-development-demo      │
╰──────────────────────────────────────╯
*/

/** Particular to the page */
const StaticCatalogDev = (() => {
	
	var _$tiles_or_list;
	var _$no_results_panel;
	var _$results_panel;
	var _$tile_template;
	var _$tile_field_template; 

	var _pageFieldsFilters;
//	var _searchCatalog;
	
	/** On jQuery document loaded completely */
	const init = () => {
		
		/* static-catalog-fields.json */
		$.ajax({
			dataType: "json",
			url: "static-catalog-fields.json",
			mimeType: "application/json",
			success: result => {
				_pageFieldsFilters = result;
				console.log(_pageFieldsFilters);
			}
		});

//		$.ajax({
//			dataType: "json",
//			url: "static-catalog.json",
//			mimeType: "application/json",
//			success: result => {
//				_searchCatalog = result;
//				console.log(_searchCatalog);
//			}
//		});

		//$('ui.table').tablesort();
		$('.ui.accordion').accordion();
		
		$('#buttonApply').click( clickEvent => {
			apply();
        });
		
		const $see_as_tiles = $('#scp_id__see_as_tiles');
		const $see_as_list = $('#scp_id__see_as_list');
		const $tiles_or_list = $('#scp_id__tiles_or_list');
		
		$see_as_tiles.click( clickEvent => {
			const $tile_grid = $('div[name=scp_name__tile_grid]');
			
			$see_as_list.removeClass("active");
			$tiles_or_list.removeClass().addClass("ui three column grid");
			$tile_grid.removeClass().addClass("ui one column grid");
			$see_as_tiles.removeClass("item").addClass("active item");
        });
		
		$see_as_list.click( clickEvent => {
			const $tile_grid = $('div[name=scp_name__tile_grid]');
			
			$see_as_tiles.removeClass("active");
			$tiles_or_list.removeClass().addClass("ui one column grid");
			$tile_grid.removeClass().addClass("ui four column grid");
			$see_as_list.removeClass("item").addClass("active item");
        });
		
		_$tiles_or_list = $('#scp_id__tiles_or_list');
		_$no_results_panel = $('#scp_id__no_results_panel');
		_$results_panel = $('#scp_id__results_panel');
		_$tile_template = $('#scp_id__tile_template');
		_$tile_field_template = $('#scp_id__tile_field_template');
	}

	/** Back in page from search */
	const resultsCallback = (results) => {
		
		//console.log(results);
		
		//console.log(_$tile_template);
		
		_$no_results_panel.hide();
		
		for (let index = 0; index < 10; index++) {
			
			$tile = _$tile_template.clone();
			$tile.appendTo(_$tiles_or_list);
			
			$tile_fields = $tile.find('div[name=scp_name__tile_grid]')[0];
			
			let result = results[index];
			
			for (let resultField in result) {
				//console.log(resultField + " " + result[resultField]);
				
				$tile_field = _$tile_field_template.clone();
				$tile_field.appendTo($tile_fields);
				$tile_field.find('span[name=scp_name__tile_field_name]').html(resultField);
				$tile_field.find('span[name=scp_name__tile_field_value]').html(result[resultField]);
			}
			
			//console.log(result);
		}
		
		_$results_panel.show();
		
	}
	
	/** Collect selected filter values and send them to the engine */
	const apply = () => {

		_$tiles_or_list.empty();
		
		let keys = [];
		let searchData = {
			"searchFieldsValues": []
		};
		
		let filters = _pageFieldsFilters.filters;
		for (checkbox of $("input[id*='sc_filter__']")) {
			if (checkbox.checked) {
				let filterFieldValue = filters[checkbox.id];
				
				let filterField = filterFieldValue.field;
				let filterValue = filterFieldValue.value;
				
				if (!keys.includes(filterField)) {
					keys.push(filterField);
					searchData.searchFieldsValues.push({
						"field": filterField,
						"values": []
					});
				}

				let index = keys.indexOf(filterField, 0);
				searchData.searchFieldsValues[index].values.push(filterValue);
			}
		}
//		searchData.searchCatalog = _searchCatalog.searchCatalog;
//		searchData.filterNameIndex = _pageFieldsFilters.page.filterNameIndex;
		console.log(searchData);

		StaticCatalog.applyFilters(searchData, resultsCallback);
	}

	return {
		init: init,
		apply: apply
	}
})();
