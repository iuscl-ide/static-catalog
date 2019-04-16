/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model;

import java.util.ArrayList;
import java.util.HashMap;

/** Examine field structure */
public class StaticCatalogExamineField {

	private String name;
	
	private String type;
	
	private final HashMap<String, ArrayList<String>> fieldTypesExceptionValues = new HashMap<>();

	private final HashMap<String, Long> uniqueValueCounts = new HashMap<>();

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public HashMap<String, Long> getUniqueValueCounts() {
		return uniqueValueCounts;
	}

	public HashMap<String, ArrayList<String>> getFieldTypesExceptionValues() {
		return fieldTypesExceptionValues;
	};
}
