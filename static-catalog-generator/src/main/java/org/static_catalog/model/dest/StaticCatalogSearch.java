/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.dest;

/** Search JSON root */
public class StaticCatalogSearch {

	private final StaticCatalogSearchFields searchCatalog = new StaticCatalogSearchFields();

	public StaticCatalogSearchFields getSearchCatalog() {
		return searchCatalog;
	}
}
