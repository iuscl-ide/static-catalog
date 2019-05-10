/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.dest;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/** Search JSON fields */
public class StaticCatalogSearchFields {

	private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<Long>>> nameValuesBlocks = new LinkedHashMap<>();

	public LinkedHashMap<String, LinkedHashMap<String, ArrayList<Long>>> getNameValuesBlocks() {
		return nameValuesBlocks;
	}
}
