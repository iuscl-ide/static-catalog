package org.static_catalog.model;

import java.util.ArrayList;

public class StaticCatalogTemplateRoot {

	private final ArrayList<StaticCatalogTemplateFilter> filters = new ArrayList<>();

	public ArrayList<StaticCatalogTemplateFilter> getFilters() {
		return filters;
	}
}
