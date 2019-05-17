/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.src;

import java.util.ArrayList;

/** Generation structure */
public class StaticCatalogConfigurationFields {

	private String description = "";
	
	private final ArrayList<StaticCatalogConfigurationField> fields = new ArrayList<>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<StaticCatalogConfigurationField> getFields() {
		return fields;
	}
}
