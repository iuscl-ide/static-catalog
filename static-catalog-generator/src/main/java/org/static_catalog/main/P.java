/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.main;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.static_catalog.ui.StaticCatalogFileControlProperties;
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
		p.viewCsvFileControl = new StaticCatalogFileControlProperties();
		
		p.examineCsvFileControl = new StaticCatalogFileControlProperties();
		
		p.filtersFileControl = new StaticCatalogFileControlProperties();

		p.generateSourceCsvFileControl = new StaticCatalogFileControlProperties();
		p.generateFiltersFileControl = new StaticCatalogFileControlProperties();
		p.generateDestinationFolderFileControl = new StaticCatalogFileControlProperties();
		p.generateTemplateFileControl = new StaticCatalogFileControlProperties();
		
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

	private StaticCatalogFileControlProperties viewCsvFileControl;
	private int viewCsvMaxLines = 1001;
	private boolean viewCsvUseFirstLineAsHeader = true;

	private StaticCatalogFileControlProperties examineCsvFileControl;
	private int examineCsvTypeMaxExceptions = 1;
	private int examineCsvFilterElementsMax = 50;
	private int examineCsvFilterElementsMaxDisplay = 35;
	private int examineCsvFilterElementsMinDisplay = 10;
	private boolean examineCsvUseFirstLineasHeader = true;
	
	private StaticCatalogFileControlProperties filtersFileControl;
	
	private StaticCatalogFileControlProperties generateSourceCsvFileControl;
	private StaticCatalogFileControlProperties generateFiltersFileControl;
	private StaticCatalogFileControlProperties generateDestinationFolderFileControl;
	private StaticCatalogFileControlProperties generateTemplateFileControl;
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

	public StaticCatalogFileControlProperties getViewCsvFileControl() {
		return viewCsvFileControl;
	}

	public void setViewCsvFileControl(StaticCatalogFileControlProperties viewCsvFileControl) {
		this.viewCsvFileControl = viewCsvFileControl;
	}

	public StaticCatalogFileControlProperties getExamineCsvFileControl() {
		return examineCsvFileControl;
	}

	public void setExamineCsvFileControl(StaticCatalogFileControlProperties examineCsvFileControl) {
		this.examineCsvFileControl = examineCsvFileControl;
	}

	public StaticCatalogFileControlProperties getFiltersFileControl() {
		return filtersFileControl;
	}

	public void setFiltersFileControl(StaticCatalogFileControlProperties filtersFileControl) {
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

	public StaticCatalogFileControlProperties getGenerateSourceCsvFileControl() {
		return generateSourceCsvFileControl;
	}

	public void setGenerateSourceCsvFileControl(StaticCatalogFileControlProperties generateSourceCsvFileControl) {
		this.generateSourceCsvFileControl = generateSourceCsvFileControl;
	}

	public StaticCatalogFileControlProperties getGenerateFiltersFileControl() {
		return generateFiltersFileControl;
	}

	public void setGenerateFiltersFileControl(StaticCatalogFileControlProperties generateFiltersFileControl) {
		this.generateFiltersFileControl = generateFiltersFileControl;
	}

	public StaticCatalogFileControlProperties getGenerateDestinationFolderFileControl() {
		return generateDestinationFolderFileControl;
	}

	public void setGenerateDestinationFolderFileControl(StaticCatalogFileControlProperties generateDestinationFolderFileControl) {
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

	public StaticCatalogFileControlProperties getGenerateTemplateFileControl() {
		return generateTemplateFileControl;
	}

	public void setGenerateTemplateFileControl(StaticCatalogFileControlProperties generateTemplateFileControl) {
		this.generateTemplateFileControl = generateTemplateFileControl;
	}

	public int getExamineCsvFilterElementsMax() {
		return examineCsvFilterElementsMax;
	}

	public void setExamineCsvFilterElementsMax(int examineCsvFilterElementsMax) {
		this.examineCsvFilterElementsMax = examineCsvFilterElementsMax;
	}

	public int getExamineCsvFilterElementsMinDisplay() {
		return examineCsvFilterElementsMinDisplay;
	}

	public void setExamineCsvFilterElementsMinDisplay(int examineCsvFilterElementsMinDisplay) {
		this.examineCsvFilterElementsMinDisplay = examineCsvFilterElementsMinDisplay;
	}
	
}
