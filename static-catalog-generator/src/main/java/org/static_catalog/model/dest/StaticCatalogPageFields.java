/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.dest;

import java.util.ArrayList;

/** Page JSON fields */
public class StaticCatalogPageFields {

	private final ArrayList<StaticCatalogPageField> fields = new ArrayList<>();

	public ArrayList<StaticCatalogPageField> getFields() {
		return fields;
	}
}
