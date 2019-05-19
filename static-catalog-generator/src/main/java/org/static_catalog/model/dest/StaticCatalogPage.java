/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.dest;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/** Page JSON fields */
public class StaticCatalogPage {

	private String description = "";
	
	private final ArrayList<StaticCatalogPageField> fields = new ArrayList<>();

	private final LinkedHashMap<String, StaticCatalogPageFilter> filters = new LinkedHashMap<>();
	
	private final StaticCatalogPageTotals totals = new StaticCatalogPageTotals(); 
	
	public ArrayList<StaticCatalogPageField> getFields() {
		return fields;
	}

	public LinkedHashMap<String, StaticCatalogPageFilter> getFilters() {
		return filters;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public StaticCatalogPageTotals getTotals() {
		return totals;
	}
}
