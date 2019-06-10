/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.dest;

import java.util.ArrayList;

/** Page JSON field */
public class StaticCatalogPageField {

	private int index;
	
	private String identifier;
	
	private int csvIndex;
	
	private String name;
	
	private String type;

	private String label;
	
	private boolean filter;
	
	private boolean sortAsc;
	
	private boolean sortDesc;
	
	private int total_values_count = 0;
	
	private int total_more_values_count = 0;

	private final ArrayList<StaticCatalogPageFieldValue> values = new ArrayList<>();
	
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public int getCsvIndex() {
		return csvIndex;
	}

	public void setCsvIndex(int csvIndex) {
		this.csvIndex = csvIndex;
	}

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

	public boolean getFilter() {
		return filter;
	}

	public void setFilter(boolean filter) {
		this.filter = filter;
	}

	public boolean getSortAsc() {
		return sortAsc;
	}

	public void setSortAsc(boolean sortAsc) {
		this.sortAsc = sortAsc;
	}

	public boolean getSortDesc() {
		return sortDesc;
	}

	public void setSortDesc(boolean sortDesc) {
		this.sortDesc = sortDesc;
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

	public ArrayList<StaticCatalogPageFieldValue> getValues() {
		return values;
	}
}
