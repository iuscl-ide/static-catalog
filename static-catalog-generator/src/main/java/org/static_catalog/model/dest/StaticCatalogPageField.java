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

	private String filterType;

	private String filterDisplayType;

	private boolean sortAsc;
	
	private boolean sortDesc;

	private String sortAscLabel;

	private String sortDescLabel;

	private int totalValuesCount = 0;
	
	private int totalMoreValuesCount = 0;

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

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getFilterDisplayType() {
		return filterDisplayType;
	}

	public void setFilterDisplayType(String filterDisplayType) {
		this.filterDisplayType = filterDisplayType;
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

	public String getSortAscLabel() {
		return sortAscLabel;
	}

	public void setSortAscLabel(String sortAscLabel) {
		this.sortAscLabel = sortAscLabel;
	}

	public String getSortDescLabel() {
		return sortDescLabel;
	}

	public void setSortDescLabel(String sortDescLabel) {
		this.sortDescLabel = sortDescLabel;
	}

	public int getTotalValuesCount() {
		return totalValuesCount;
	}

	public void setTotalValuesCount(int totalValuesCount) {
		this.totalValuesCount = totalValuesCount;
	}

	public int getTotalMoreValuesCount() {
		return totalMoreValuesCount;
	}

	public void setTotalMoreValuesCount(int totalMoreValuesCount) {
		this.totalMoreValuesCount = totalMoreValuesCount;
	}

	public ArrayList<StaticCatalogPageFieldValue> getValues() {
		return values;
	}
}
