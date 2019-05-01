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
	
	/** init */
	const init = () => {
		
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

	/** */
	const resultsCallback = (results) => {
		
		console.log(results);
		
		console.log(_$tile_template);
		
		_$no_results_panel.hide();
		
		for (let index = 0; index < 10; index++) {
			
			$tile = _$tile_template.clone();
			$tile.appendTo(_$tiles_or_list);
			
			$tile_fields = $tile.find('div[name=scp_name__tile_grid]')[0];
			
			let result = results.data[index];
			
			for (let resultField in result) {
				console.log(resultField + " " + result[resultField]);
				
				$tile_field = _$tile_field_template.clone();
				$tile_field.appendTo($tile_fields);
				$tile_field.find('span[name=scp_name__tile_field_name]').html(resultField);
				$tile_field.find('span[name=scp_name__tile_field_value]').html(result[resultField]);
			}
			
			console.log(result);
		}
		
		_$results_panel.show();
		
	}
	
	/** */
	const apply = () => {
		
		StaticCatalog.applyFilters(resultsCallback);
		
	}

	return {
		init: init,
		apply: apply
	}
})();
