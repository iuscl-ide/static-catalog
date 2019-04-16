/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model;

import java.util.ArrayList;

/** Generation structure */
public class StaticCatalogFilters {

	private final ArrayList<StaticCatalogFiltersField> fields = new ArrayList<>();

	public ArrayList<StaticCatalogFiltersField> getFields() {
		return fields;
	}
	
	
}
