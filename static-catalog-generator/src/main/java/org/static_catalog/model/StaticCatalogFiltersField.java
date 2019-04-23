/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model;

/** Generation structure */
public class StaticCatalogFiltersField {

	private String name;
	
	private String type;

	private String label;

	private boolean isFilter;

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getIsFilter() {
		return isFilter;
	}

	public void setIsFilter(boolean isFilter) {
		this.isFilter = isFilter;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
