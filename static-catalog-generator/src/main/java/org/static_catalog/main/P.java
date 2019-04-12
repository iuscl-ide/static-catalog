/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.alibaba.fastjson.JSON;

/** Properties */
public class P {
	
	/** Load, just once */
	public static P load(String propertiesFileName) {
		
		String jsonSer = null;
		try {
			jsonSer = new String(Files.readAllBytes(Paths.get(propertiesFileName)), StandardCharsets.UTF_8);
		}
		catch (IOException ioException) {
//			L.e("JSON load", ioException);
		}
		
		P p = new P();
		if (jsonSer != null) {
			p = JSON.parseObject(jsonSer, P.class);	
		}
		p.propertiesFileName = propertiesFileName;
		
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

	private String viewCsvFileName = "";
	private int viewCsvMaxLines = 100;
	private boolean viewCsvUseFirstLineAsHeader = true;
	
	private String analizeCsvFileName = "";
	
	private String filtersFileName = "";

	
	public String getViewCsvFileName() {
		return viewCsvFileName;
	}

	public void setViewCsvFileName(String viewCsvFileName) {
		this.viewCsvFileName = viewCsvFileName;
	}

	public int getViewCsvMaxLines() {
		return viewCsvMaxLines;
	}

	public void setViewCsvMaxLines(int viewCsvMaxLines) {
		this.viewCsvMaxLines = viewCsvMaxLines;
	}

	public boolean isViewCsvUseFirstLineAsHeader() {
		return viewCsvUseFirstLineAsHeader;
	}

	public void setViewCsvUseFirstLineAsHeader(boolean viewCsvUseFirstLineAsHeader) {
		this.viewCsvUseFirstLineAsHeader = viewCsvUseFirstLineAsHeader;
	}

	public String getAnalizeCsvFileName() {
		return analizeCsvFileName;
	}

	public void setAnalizeCsvFileName(String analizeCsvFileName) {
		this.analizeCsvFileName = analizeCsvFileName;
	}

	public String getFiltersFileName() {
		return filtersFileName;
	}

	public void setFiltersFileName(String filtersFileName) {
		this.filtersFileName = filtersFileName;
	}
	
	
	
}
