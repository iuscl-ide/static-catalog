/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.dest;

/** Page JSON field value */
public class StaticCatalogPageFieldValue {

	private int index;
	
	private String identifier;
	
	private String name;
	
	private Long count;

	private String label;
	
	private boolean isException;
	
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean getIsException() {
		return isException;
	}

	public void setIsException(boolean isException) {
		this.isException = isException;
	}
}
