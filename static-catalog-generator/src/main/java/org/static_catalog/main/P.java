/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.main;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.static_catalog.ui.FileControlProperties;
import org.static_catalog.ui.StaticCatalogGeneratorMainWindow;

/** Properties */
public class P {

	/** Load, just once */
	public static P load(StaticCatalogGeneratorMainWindow staticCatalogGeneratorMainWindow, String propertiesFileName) {
		
		String jsonString = null;
		if (Files.exists(Paths.get(propertiesFileName))) {
			jsonString = S.loadFileInString(propertiesFileName);
		}
		
		P p = new P();
		p.viewCsvFileControl = new FileControlProperties();
		
		p.examineCsvFileControl = new FileControlProperties();
		
		p.filtersFileControl = new FileControlProperties();

		p.generateSourceCsvFileControl = new FileControlProperties();
		p.generateFiltersFileControl = new FileControlProperties();
		p.generateDestinationFolderFileControl = new FileControlProperties();
		p.generateTemplateFileControl = new FileControlProperties();
		
		if (jsonString != null) {
			p = S.loadObjectFromJsonString(jsonString, P.class);
		}
		p.propertiesFileName = propertiesFileName;

		p.viewCsvFileControl.setP(p);
		
		p.examineCsvFileControl.setP(p);
		
		p.filtersFileControl.setP(p);
		
		p.generateSourceCsvFileControl.setP(p);
		p.generateFiltersFileControl.setP(p);
		p.generateDestinationFolderFileControl.setP(p);
		p.generateTemplateFileControl.setP(p);
		
		return p;
	}
	
	/** Save, the instance */
	public void save() {
		
		S.saveObjectToJsonFileName(this, propertiesFileName);
	}
	
	private String propertiesFileName = "";

	private FileControlProperties viewCsvFileControl;
	private int viewCsvMaxLines = 1001;
	private boolean viewCsvUseFirstLineAsHeader = true;

	private FileControlProperties examineCsvFileControl;
	private int examineCsvTypeMaxExceptions = 1;
	private int examineCsvFilterElementsMaxDisplay = 500;
	private boolean examineCsvUseFirstLineasHeader = true;
	
	private FileControlProperties filtersFileControl;
	
	private FileControlProperties generateSourceCsvFileControl;
	private FileControlProperties generateFiltersFileControl;
	private FileControlProperties generateDestinationFolderFileControl;
	private FileControlProperties generateTemplateFileControl;
	private int generateTypeMaxExceptions = 1;
	private boolean generateUseFirstLineasHeader = true;

	
	public int getViewCsvMaxLines() {
		return viewCsvMaxLines;
	}

	public void setViewCsvMaxLines(int viewCsvMaxLines) {
		this.viewCsvMaxLines = viewCsvMaxLines;
	}

	public boolean getViewCsvUseFirstLineAsHeader() {
		return viewCsvUseFirstLineAsHeader;
	}
	
	public void setViewCsvUseFirstLineAsHeader(boolean viewCsvUseFirstLineAsHeader) {
		this.viewCsvUseFirstLineAsHeader = viewCsvUseFirstLineAsHeader;
	}

	public FileControlProperties getViewCsvFileControl() {
		return viewCsvFileControl;
	}

	public void setViewCsvFileControl(FileControlProperties viewCsvFileControl) {
		this.viewCsvFileControl = viewCsvFileControl;
	}

	public FileControlProperties getExamineCsvFileControl() {
		return examineCsvFileControl;
	}

	public void setExamineCsvFileControl(FileControlProperties examineCsvFileControl) {
		this.examineCsvFileControl = examineCsvFileControl;
	}

	public FileControlProperties getFiltersFileControl() {
		return filtersFileControl;
	}

	public void setFiltersFileControl(FileControlProperties filtersFileControl) {
		this.filtersFileControl = filtersFileControl;
	}

	public int getExamineCsvTypeMaxExceptions() {
		return examineCsvTypeMaxExceptions;
	}

	public void setExamineCsvTypeMaxExceptions(int examineCsvTypeMaxExceptions) {
		this.examineCsvTypeMaxExceptions = examineCsvTypeMaxExceptions;
	}

	public int getExamineCsvFilterElementsMaxDisplay() {
		return examineCsvFilterElementsMaxDisplay;
	}

	public void setExamineCsvFilterElementsMaxDisplay(int examineCsvFilterElementsMaxDisplay) {
		this.examineCsvFilterElementsMaxDisplay = examineCsvFilterElementsMaxDisplay;
	}

	public boolean getExamineCsvUseFirstLineasHeader() {
		return examineCsvUseFirstLineasHeader;
	}

	public void setExamineCsvUseFirstLineasHeader(boolean examineCsvUseFirstLineasHeader) {
		this.examineCsvUseFirstLineasHeader = examineCsvUseFirstLineasHeader;
	}

	public FileControlProperties getGenerateSourceCsvFileControl() {
		return generateSourceCsvFileControl;
	}

	public void setGenerateSourceCsvFileControl(FileControlProperties generateSourceCsvFileControl) {
		this.generateSourceCsvFileControl = generateSourceCsvFileControl;
	}

	public FileControlProperties getGenerateFiltersFileControl() {
		return generateFiltersFileControl;
	}

	public void setGenerateFiltersFileControl(FileControlProperties generateFiltersFileControl) {
		this.generateFiltersFileControl = generateFiltersFileControl;
	}

	public FileControlProperties getGenerateDestinationFolderFileControl() {
		return generateDestinationFolderFileControl;
	}

	public void setGenerateDestinationFolderFileControl(FileControlProperties generateDestinationFolderFileControl) {
		this.generateDestinationFolderFileControl = generateDestinationFolderFileControl;
	}

	public int getGenerateTypeMaxExceptions() {
		return generateTypeMaxExceptions;
	}

	public void setGenerateTypeMaxExceptions(int generateTypeMaxExceptions) {
		this.generateTypeMaxExceptions = generateTypeMaxExceptions;
	}

	public boolean getGenerateUseFirstLineasHeader() {
		return generateUseFirstLineasHeader;
	}

	public void setGenerateUseFirstLineasHeader(boolean generateUseFirstLineasHeader) {
		this.generateUseFirstLineasHeader = generateUseFirstLineasHeader;
	}

	public FileControlProperties getGenerateTemplateFileControl() {
		return generateTemplateFileControl;
	}

	public void setGenerateTemplateFileControl(FileControlProperties generateTemplateFileControl) {
		this.generateTemplateFileControl = generateTemplateFileControl;
	}

	
}
