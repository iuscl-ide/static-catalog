/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.src;

/** Generation structure */
public class StaticCatalogConfigurationField {

	private String name;
	
	private String type;

	private String label;

	private boolean isFilter;

	private String displayType;

	private Integer maxDisplayValues;

	private Integer minDisplayValues;

	private String transformFormat;

	private String transformValues;

	
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

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
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
}
