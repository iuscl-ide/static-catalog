/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model;

/** Generation structure */
public class StaticCatalogField {

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

	public boolean isFilter() {
		return isFilter;
	}

	public void setFilter(boolean isFilter) {
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
