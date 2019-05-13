/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.dest;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/** Page JSON fields */
public class StaticCatalogPage {

	private final ArrayList<StaticCatalogPageField> fields = new ArrayList<>();

	private final LinkedHashMap<String, StaticCatalogPageFilter> filters = new LinkedHashMap<>();
	
	public ArrayList<StaticCatalogPageField> getFields() {
		return fields;
	}

	public LinkedHashMap<String, StaticCatalogPageFilter> getFilters() {
		return filters;
	}
}
