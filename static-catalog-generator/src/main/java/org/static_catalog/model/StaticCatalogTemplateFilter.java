package org.static_catalog.model;

import java.util.ArrayList;

public class StaticCatalogTemplateFilter {

	private String name;
	
	private String type;

	private String label;
	
	private boolean is_displayed;
	
	
	private final ArrayList<StaticCatalogTemplateFilterValue> exception_values = new ArrayList<>();
	
	private int exception_values_count = 0;

	private boolean has_more_exception_values = false;

	private final ArrayList<StaticCatalogTemplateFilterValue> more_exception_values = new ArrayList<>();
	
	private int more_exception_values_count = 0;

	
	private final ArrayList<StaticCatalogTemplateFilterValue> values = new ArrayList<>();
	
	private int values_count = 0;

	private boolean has_more_values = false;

	private final ArrayList<StaticCatalogTemplateFilterValue> more_values = new ArrayList<>();
	
	private int more_values_count = 0;

	
	private int total_values_count = 0;
	
	private int total_more_values_count = 0;

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

	public boolean getIs_displayed() {
		return is_displayed;
	}

	public void setIs_displayed(boolean is_displayed) {
		this.is_displayed = is_displayed;
	}

	public int getException_values_count() {
		return exception_values_count;
	}

	public void setException_values_count(int exception_values_count) {
		this.exception_values_count = exception_values_count;
	}

	public boolean isHas_more_exception_values() {
		return has_more_exception_values;
	}

	public void setHas_more_exception_values(boolean has_more_exception_values) {
		this.has_more_exception_values = has_more_exception_values;
	}

	public int getMore_exception_values_count() {
		return more_exception_values_count;
	}

	public void setMore_exception_values_count(int more_exception_values_count) {
		this.more_exception_values_count = more_exception_values_count;
	}

	public int getValues_count() {
		return values_count;
	}

	public void setValues_count(int values_count) {
		this.values_count = values_count;
	}

	public boolean isHas_more_values() {
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

	public int getTotal_values_count() {
		return total_values_count;
	}

	public void setTotal_values_count(int total_values_count) {
		this.total_values_count = total_values_count;
	}

	public int getTotal_more_values_count() {
		return total_more_values_count;
	}

	public void setTotal_more_values_count(int total_more_values_count) {
		this.total_more_values_count = total_more_values_count;
	}

	public ArrayList<StaticCatalogTemplateFilterValue> getException_values() {
		return exception_values;
	}

	public ArrayList<StaticCatalogTemplateFilterValue> getMore_exception_values() {
		return more_exception_values;
	}

	public ArrayList<StaticCatalogTemplateFilterValue> getValues() {
		return values;
	}

	public ArrayList<StaticCatalogTemplateFilterValue> getMore_values() {
		return more_values;
	}
}
