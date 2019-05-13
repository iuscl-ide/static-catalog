/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.dest;

/** Search JSON root */
public class StaticCatalogIndexes {

	private final StaticCatalogIndexesValueIndex valueIndexes = new StaticCatalogIndexesValueIndex();
	// TODO intervals
	
	public StaticCatalogIndexesValueIndex getValueIndexes() {
		return valueIndexes;
	}
}
