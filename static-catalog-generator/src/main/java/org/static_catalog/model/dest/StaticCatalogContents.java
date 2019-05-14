/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.model.dest;

import java.util.LinkedHashMap;

import org.static_catalog.engine.StaticCatalogEngine;

/** Page JSON fields */
public class StaticCatalogContents {

	private final LinkedHashMap<String, Integer> filterNameIndex = new LinkedHashMap<>();

	private final LinkedHashMap<String, LinkedHashMap<String, Integer>> filterNameValueIndex = new LinkedHashMap<>();

	private String indexSplitType = StaticCatalogEngine.INDEX_SPLIT_TYPE_NONE;
	
	private long totalLinesCount;
	
	private long blockLinesCount;
	
	private long indexLinesModulo;

	public LinkedHashMap<String, Integer> getFilterNameIndex() {
		return filterNameIndex;
	}

	public LinkedHashMap<String, LinkedHashMap<String, Integer>> getFilterNameValueIndex() {
		return filterNameValueIndex;
	}

	public String getIndexSplitType() {
		return indexSplitType;
	}

	public void setIndexSplitType(String indexSplitType) {
		this.indexSplitType = indexSplitType;
	}

	public long getTotalLinesCount() {
		return totalLinesCount;
	}

	public void setTotalLinesCount(long totalLinesCount) {
		this.totalLinesCount = totalLinesCount;
	}

	public long getBlockLinesCount() {
		return blockLinesCount;
	}

	public void setBlockLinesCount(long blockLinesCount) {
		this.blockLinesCount = blockLinesCount;
	}

	public long getIndexLinesModulo() {
		return indexLinesModulo;
	}

	public void setIndexLinesModulo(long indexLinesModulo) {
		this.indexLinesModulo = indexLinesModulo;
	}
}
