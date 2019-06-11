/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.src;

/** Generation structure */
public class StaticCatalogConfigurationField {

	private Integer csvIndex;
	
	private String name;
	
	private String type;

	private String label;

	private boolean isFilter;

	private String filterType;

	private String displayType;

	private String intervalValue;

	private Integer maxDisplayValues;

	private Integer minDisplayValues;

	private String transformFormat;

	private String transformValues;

	private boolean isSortAsc;

	private boolean isSortDesc;

	private String sortAscLabel;

	private String sortDescLabel;
	
	
	public Integer getCsvIndex() {
		return csvIndex;
	}

	public void setCsvIndex(Integer csvIndex) {
		this.csvIndex = csvIndex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getIsFilter() {
		return isFilter;
	}

	public void setIsFilter(boolean isFilter) {
		this.isFilter = isFilter;
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

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public String getIntervalValue() {
		return intervalValue;
	}

	public void setIntervalValue(String intervalValue) {
		this.intervalValue = intervalValue;
	}

	public Integer getMaxDisplayValues() {
		return maxDisplayValues;
	}

	public void setMaxDisplayValues(Integer maxDisplayValues) {
		this.maxDisplayValues = maxDisplayValues;
	}

	public Integer getMinDisplayValues() {
		return minDisplayValues;
	}

	public void setMinDisplayValues(Integer minDisplayValues) {
		this.minDisplayValues = minDisplayValues;
	}

	public String getTransformFormat() {
		return transformFormat;
	}

	public void setTransformFormat(String transformFormat) {
		this.transformFormat = transformFormat;
	}

	public String getTransformValues() {
		return transformValues;
	}

	public void setTransformValues(String transformValues) {
		this.transformValues = transformValues;
	}

	public boolean getIsSortAsc() {
		return isSortAsc;
	}

	public void setIsSortAsc(boolean isSortAsc) {
		this.isSortAsc = isSortAsc;
	}

	public boolean getIsSortDesc() {
		return isSortDesc;
	}

	public void setIsSortDesc(boolean isSortDesc) {
		this.isSortDesc = isSortDesc;
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
}
