/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.dest;

/** Page JSON filter field-value */
public class StaticCatalogPageTotals {

	private long totalLines;
	
	private long totalCsvFileSize;

	private long totalFields;
	
	private long totalFilters;

	public long getTotalLines() {
		return totalLines;
	}

	public void setTotalLines(long totalLines) {
		this.totalLines = totalLines;
	}

	public long getTotalCsvFileSize() {
		return totalCsvFileSize;
	}

	public void setTotalCsvFileSize(long totalCsvFileSize) {
		this.totalCsvFileSize = totalCsvFileSize;
	}

	public long getTotalFields() {
		return totalFields;
	}

	public void setTotalFields(long totalFields) {
		this.totalFields = totalFields;
	}

	public long getTotalFilters() {
		return totalFilters;
	}

	public void setTotalFilters(long totalFilters) {
		this.totalFilters = totalFilters;
	}
}