package org.static_catalog.model;

import java.util.ArrayList;

public class StaticCatalogTemplateFilter {

	private String name;
	
	private String type;

	private String label;
	
	private final ArrayList<String> exceptions = new ArrayList<>();

	private int values_count = 0;
	
	private final ArrayList<StaticCatalogTemplateFilterValue> main_values = new ArrayList<>();

	private boolean has_more_values = false;

	private int more_values_count = 0;
	
	private final ArrayList<StaticCatalogTemplateFilterValue> more_values = new ArrayList<>();

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

	public int getValues_count() {
		return values_count;
	}

	public void setValues_count(int values_count) {
		this.values_count = values_count;
	}

	public boolean getHas_more_values() {
		return has_more_values;
	}

	public void setHas_more_values(boolean has_more_values) {
		this.has_more_values = has_more_values;
	}

	public int getMore_values_count() {
		return more_values_count;
	}

	public void setMore_values_count(int more_values_count) {
		this.more_values_count = more_values_count;
	}

	public ArrayList<String> getExceptions() {
		return exceptions;
	}

	public ArrayList<StaticCatalogTemplateFilterValue> getMain_values() {
		return main_values;
	}

	public ArrayList<StaticCatalogTemplateFilterValue> getMore_values() {
		return more_values;
	}
	
}
