/**	
╭──────────────────────────────────────╮
│ static-catalog-development-demo      │
╰──────────────────────────────────────╯
*/

const StaticCatalogDev = (() => {
	
	var _$tiles_or_list;
	var _$no_results_panel;
	var _$results_panel;
	var _$tile_template;
	var _$tile_field_template; 

	var _pageFieldsFilters;
	var _searchCatalog;
	
	/** init */
	const init = () => {
		
		$.ajax({
			dataType: "json",
			url: "static-catalog-fields.json",
			mimeType: "application/json",
			success: result => {
				_pageFieldsFilters = result;
				console.log(_pageFieldsFilters);
			}
		});

		$.ajax({
			dataType: "json",
			url: "static-catalog.json",
			mimeType: "application/json",
			success: result => {
				_searchCatalog = result;
				console.log(_searchCatalog);
			}
		});

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
		
		$('#buttonApply1').click( clickEvent => {
			
			let arr = [];
			
			console.log("Start");
//			for (let i = 0; i < 1000000; i++) {
//				let arr2 = [];
//				for (let j = 0; j < 100; j++) {
//					arr2.push(i + j);
//				}
//				arr.push(arr2);
//			}

			
			let arr2 = [];
			for (let j = 0; j < 100; j++) {
				arr2.push(j);
			}
			
			let s = "10-20-35-39-47-69-38-47-68-24-1-3-7-6-7-44-6-2-8-67-31-64-9-54-71-6-55-88-4-31-49-5-5-5-6-10"
			let l = 140000000;
			for (let i = 0; i < l; i++) {
				//arr.push(i + " => " + s + i * i + s);
				//arr.push(arr2.clone());
				arr.push(i + i);
			}

			console.log("Done");
			console.log(arr[l - 1]);
        });
	}

	/** */
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
	
	/** */
	const apply = () => {

		_$tiles_or_list.empty();
		
		let keys = [];
		let searchData = {
			"searchFieldsValues": []
		};
		
		let filters = _pageFieldsFilters.page.filters;
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
		searchData.searchCatalog = _searchCatalog.searchCatalog;
		//console.log(searchData);

		StaticCatalog.applyFilters(searchData, resultsCallback);
	}

	return {
		init: init,
		apply: apply
	}
})();
