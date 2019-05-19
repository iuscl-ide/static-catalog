/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.dest;

/** Page JSON filter field-value */
public class StaticCatalogPageTotals {

	private String totalLines;
	
	private String totalCsvFileSize;

	private String totalFields;
	
	private String totalFilters;

	public String getTotalLines() {
		return totalLines;
	}

	public void setTotalLines(String totalLines) {
		this.totalLines = totalLines;
	}

	public String getTotalCsvFileSize() {
		return totalCsvFileSize;
	}

	public void setTotalCsvFileSize(String totalCsvFileSize) {
		this.totalCsvFileSize = totalCsvFileSize;
	}

	public String getTotalFields() {
		return totalFields;
	}

	public void setTotalFields(String totalFields) {
		this.totalFields = totalFields;
	}

	public String getTotalFilters() {
		return totalFilters;
	}

	public void setTotalFilters(String totalFilters) {
		this.totalFilters = totalFilters;
	}
}
