/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.dest;

import java.util.ArrayList;

/** Page JSON field */
public class StaticCatalogPageField {

	private String identifier;
	
	private int indexInLine;
	
	private String name;
	
	private String type;

	private String label;
	
	private boolean filter;
	
	private boolean sortAsc;
	
	private boolean sortDesc;
	
	
	private final ArrayList<StaticCatalogPageFieldValue> exception_values = new ArrayList<>();
	
	private int exception_values_count = 0;

	private boolean has_more_exception_values = false;

	private final ArrayList<StaticCatalogPageFieldValue> more_exception_values = new ArrayList<>();
	
	private int more_exception_values_count = 0;

	
	private final ArrayList<StaticCatalogPageFieldValue> values = new ArrayList<>();
	
	private int values_count = 0;

	private boolean has_more_values = false;

	private final ArrayList<StaticCatalogPageFieldValue> more_values = new ArrayList<>();
	
	private int more_values_count = 0;

	
	private int total_values_count = 0;
	
	private int total_more_values_count = 0;

	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public int getIndexInLine() {
		return indexInLine;
	}

	public void setIndexInLine(int indexInLine) {
		this.indexInLine = indexInLine;
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

	public int getException_values_count() {
		return exception_values_count;
	}

	public void setException_values_count(int exception_values_count) {
		this.exception_values_count = exception_values_count;
	}

	public boolean getHas_more_exception_values() {
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

	public ArrayList<StaticCatalogPageFieldValue> getException_values() {
		return exception_values;
	}

	public ArrayList<StaticCatalogPageFieldValue> getMore_exception_values() {
		return more_exception_values;
	}

	public ArrayList<StaticCatalogPageFieldValue> getValues() {
		return values;
	}

	public ArrayList<StaticCatalogPageFieldValue> getMore_values() {
		return more_values;
	}
}
