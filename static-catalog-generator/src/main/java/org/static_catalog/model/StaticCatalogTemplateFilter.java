package org.static_catalog.model;

import java.util.ArrayList;

public class StaticCatalogTemplateFilter {

	private String name;
	
	private String type;

	private String label;
	
	private final ArrayList<String> exceptions = new ArrayList<>();

	private final ArrayList<StaticCatalogTemplateFilterValue> values = new ArrayList<>();

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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ArrayList<String> getExceptions() {
		return exceptions;
	}

	public ArrayList<StaticCatalogTemplateFilterValue> getValues() {
		return values;
	}
}
