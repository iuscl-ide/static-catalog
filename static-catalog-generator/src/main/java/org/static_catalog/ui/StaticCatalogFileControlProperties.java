/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.ui;

import java.util.ArrayList;

import org.static_catalog.main.P;

/** File control properties */
public class StaticCatalogFileControlProperties {
	
	private P p;
	
	public static final String NO_RECENT_FILES = "-- No recent files --";
	
	private final ArrayList<String> recentFileNames = new ArrayList<String>();
	
	private String fileName = "";

	
	/** Save with all properties */
	public void save() {
		p.save();
	}

	public void setP(P p) {
		this.p = p;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ArrayList<String> getRecentFileNames() {
		return recentFileNames;
	}
}