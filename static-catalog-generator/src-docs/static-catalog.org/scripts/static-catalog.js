/*	
╭──────────────────────────────────────────────────────────────────────────────╮
│ static-catalog.org                                 static-catalog.org - 2019 │
╰──────────────────────────────────────────────────────────────────────────────╯
*/

"use strict";

/* Particular to the page */
const StaticCatalog = (() => {

	/* On jQuery document loaded completely */
	const init = () => {
		
		/* SEMANTIC UI initializations */
		
		$(".overlay").visibility({
			type: "fixed"
		});
		//$("ui.table").tablesort();
		$(".ui.accordion").accordion();
	}
	
	return {
		init: init
	}
})();
