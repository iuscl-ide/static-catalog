package org.static_catalog.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class StaticCatalogTemplateCatalog {

	private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<Integer>>> filterIdentifierBlocks = new LinkedHashMap<>();

	public LinkedHashMap<String, LinkedHashMap<String, ArrayList<Integer>>> getFilterIdentifierBlocks() {
		return filterIdentifierBlocks;
	}
}
