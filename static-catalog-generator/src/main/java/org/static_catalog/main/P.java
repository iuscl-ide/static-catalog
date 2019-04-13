/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.static_catalog.ui.FileControlProperties;
import org.static_catalog.ui.StaticCatalogGeneratorMainWindow;

import com.alibaba.fastjson.JSON;

/** Properties */
public class P {
	
	/** Load, just once */
	public static P load(StaticCatalogGeneratorMainWindow staticCatalogGeneratorMainWindow, String propertiesFileName) {
		
		String jsonSer = null;
		try {
			jsonSer = new String(Files.readAllBytes(Paths.get(propertiesFileName)), StandardCharsets.UTF_8);
		}
		catch (IOException ioException) {
//			L.e("JSON load", ioException);
		}
		
		P p = new P();
		p.viewCsvFileControl = new FileControlProperties();
		p.analizeCsvFileControl = new FileControlProperties();
		p.filtersFileControl = new FileControlProperties();

		if (jsonSer != null) {
			p = JSON.parseObject(jsonSer, P.class);	
		}
		p.propertiesFileName = propertiesFileName;

		p.viewCsvFileControl.setP(p);
		p.analizeCsvFileControl.setP(p);
		p.filtersFileControl.setP(p);
		
		return p;
	}
	
	/** Save, the instance */
	public void save() {
		
		String jsonSer = JSON.toJSONString(this, true);
		
		try {
			Files.write(Paths.get(propertiesFileName), jsonSer.getBytes(StandardCharsets.UTF_8));
		} catch (IOException ioException) {
			L.e("Error writing file", ioException);
		}
	}
	
	private String propertiesFileName = "";

	private FileControlProperties viewCsvFileControl;
	private int viewCsvMaxLines = 100;
	private boolean viewCsvUseFirstLineAsHeader = true;

	private FileControlProperties analizeCsvFileControl;
	
	private FileControlProperties filtersFileControl;

	
	public int getViewCsvMaxLines() {
		return viewCsvMaxLines;
	}

	public void setViewCsvMaxLines(int viewCsvMaxLines) {
		this.viewCsvMaxLines = viewCsvMaxLines;
	}

	public boolean isViewCsvUseFirstLineAsHeader() {
		return viewCsvUseFirstLineAsHeader;
	}

	public FileControlProperties getViewCsvFileControl() {
		return viewCsvFileControl;
	}

	public void setViewCsvFileControl(FileControlProperties viewCsvFileControl) {
		this.viewCsvFileControl = viewCsvFileControl;
	}

	public FileControlProperties getAnalizeCsvFileControl() {
		return analizeCsvFileControl;
	}

	public void setAnalizeCsvFileControl(FileControlProperties analizeCsvFileControl) {
		this.analizeCsvFileControl = analizeCsvFileControl;
	}

	public FileControlProperties getFiltersFileControl() {
		return filtersFileControl;
	}

	public void setFiltersFileControl(FileControlProperties filtersFileControl) {
		this.filtersFileControl = filtersFileControl;
	}
}
