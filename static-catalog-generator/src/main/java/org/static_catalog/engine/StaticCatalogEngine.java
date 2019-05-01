/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.program.Program;
import org.pojava.datetime.DateTime;
import org.static_catalog.main.L;
import org.static_catalog.main.S;
import org.static_catalog.main.U;
import org.static_catalog.model.StaticCatalogExamine;
import org.static_catalog.model.StaticCatalogExamineField;
import org.static_catalog.model.StaticCatalogFilters;
import org.static_catalog.model.StaticCatalogFiltersField;
import org.static_catalog.model.StaticCatalogTemplate;
import org.static_catalog.model.StaticCatalogTemplateCatalog;
import org.static_catalog.model.StaticCatalogTemplateCatalogRoot;
import org.static_catalog.model.StaticCatalogTemplateFilter;
import org.static_catalog.model.StaticCatalogTemplateFilterValue;
import org.static_catalog.ui.StaticCatalogGeneratorMainWindow.LoopProgress;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

import liqp.Template;

/** Generator engine */
public class StaticCatalogEngine {

	/** Types */
	public static final String TYPE_DATE = "date"; 
	public static final String TYPE_LONG = "long"; 
	public static final String TYPE_DOUBLE= "double"; 
	public static final String TYPE_TEXT = "text"; 

	/** Natural order */
	private final static StringAsNumberComparator stringAsNumberComparator = new StringAsNumberComparator();

	private final static Comparator<String> datetimeComparator = new Comparator<String>() {
        @Override
        public int compare(String object1, String object2) {
        	return DateTime.parse(object1).compareTo(DateTime.parse(object2));
        }
    };
    
	private final static Comparator<String> longComparator = new Comparator<String>() {
        @Override
        public int compare(String object1, String object2) {
        	return Long.valueOf(object1).compareTo(Long.valueOf(object2));
        }
    };
		
	private final static Comparator<String> doubleComparator = new Comparator<String>() {
        @Override
        public int compare(String object1, String object2) {
        	return Double.valueOf(object1).compareTo(Double.valueOf(object2));
        }
    };
	
	/** Load CSV in grid */
	public static void loadViewCsv(String csvCompleteFileName,
			ArrayList<String[]> csvFileGridLines, ArrayList<String> csvFileGridHeader,
			long maxLines, boolean useFirstLineAsHeader,
			AtomicBoolean doLoop, LoopProgress loopProgress) {

		CsvParserSettings csvParserSettings = new CsvParserSettings();
		csvParserSettings.setLineSeparatorDetectionEnabled(true);
		CsvParser csvParser = new CsvParser(csvParserSettings);
		csvParser.beginParsing(new File(csvCompleteFileName));
		
//		csvStopLoadButton.setEnabled(true);
//		csvButtonsComposite.redraw();
//		csvStopLoadButton.requestLayout();
//		Display.getCurrent().readAndDispatch();
		
//		Display.getCurrent().syncExec(new Runnable() {
//		    public void run() {
//		        /** UI update code */
//		    	csvStopLoadButton.setEnabled(true);
//		    }
//		});
		
		String[] csvLine = csvParser.parseNext();
		long csvLineIndex = 0;
		while (csvLine != null) {
			
			if (!doLoop.get()) {
				csvParser.stopParsing();
				return;
			}
			
			int lineLength = csvLine.length;
			
			if (useFirstLineAsHeader && (csvLineIndex == 0)) {
//				GridColumn fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
//			    fieldGridColumn.setWidth(50);
//			    fieldGridColumn.setText("Index");
//			    fieldGridColumn.setAlignment(SWT.RIGHT);
//				for (int index = 0; index < lineLength; index++) {
//					fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
//				    fieldGridColumn.setWordWrap(true);
//				    //fieldGridColumn.setWidth(csvLine[index].length() * 10);
//				    fieldGridColumn.setText(csvLine[index]);
//				    //fieldGridColumn.pack();
//				}
//				for (GridColumn gridColumn : csvFileGrid.getColumns()) {
//					gridColumn.pack();
//					gridColumn.setWidth(gridColumn.getWidth() + 24);
//				}
				csvFileGridHeader.addAll(new ArrayList<String>(Arrays.asList(csvLine)));
			}
			else {
				if (!useFirstLineAsHeader) {
					int fieldsSize = csvFileGridHeader.size();
					if (fieldsSize < lineLength) {
						for (int index = fieldsSize; index < lineLength; index++) {
							csvFileGridHeader.add(index, "Column " + (index + 1));
						}
					}
				}

				csvFileGridLines.add(csvLine);
//				GridItem csvGridItem = new GridItem(csvFileGrid, SWT.NONE);
//				csvGridItem.setText(0, csvLineIndex + "");
//				for (int index = 0; index < lineLength; index++) {
//					csvGridItem.setText(index + 1, csvLine[index] + "");
//				}
			}

			csvLineIndex++;
			if (csvLineIndex % 10000 == 0) {
				loopProgress.doProgress(csvLineIndex + " lines loaded...");
				//csvStopLoadButton.setEnabled(true);
//				csvStatusLabel.setText(csvLineIndex + " lines loaded...");
			}

			if (csvLineIndex == maxLines) {
//				csvStopLoadButton.setEnabled(false);
//				for (GridColumn gridColumn : csvFileGrid.getColumns()) {
//					gridColumn.pack();
//					gridColumn.setWidth(gridColumn.getWidth() + 24);
//				}
//				csvFileGrid.setItemCount((int) (csvLineIndex - 1));
//				csvStatusLabel.setText("Max " + (csvLineIndex - 1) + " lines done load in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
				csvParser.stopParsing();
				return;
			}

			csvLine = csvParser.parseNext();
		}
		
		csvParser.stopParsing();
	}
	
	/** Load examine CSV */
	public static void loadExamineCsv(String csvCompleteFileName, StaticCatalogExamine staticCatalogExamine,
			long maxUniqueValues, int maxExceptions, boolean useFirstLineAsHeader,
			AtomicBoolean doLoop, LoopProgress loopProgress) {

		long start = System.currentTimeMillis();
		
		loopProgress.doProgress("Start lines examine...");
		
		ArrayList<StaticCatalogExamineField> examineFields = staticCatalogExamine.getFields();
		
		CsvParserSettings csvParserSettings = new CsvParserSettings();
		csvParserSettings.setLineSeparatorDetectionEnabled(true);
		CsvParser csvParser = new CsvParser(csvParserSettings);
		csvParser.beginParsing(new File(csvCompleteFileName));
		
		String[] csvLine = csvParser.parseNext();
		int lineLength = 0;
		long csvLineIndex = 0;
		while (csvLine != null) {

			lineLength = csvLine.length;

			if (useFirstLineAsHeader && (csvLineIndex == 0)) {
				for (int index = 0; index < lineLength; index++) {
					StaticCatalogExamineField staticCatalogExamineField = new StaticCatalogExamineField();
					staticCatalogExamineField.setName(csvLine[index]);
					examineFields.add(index, staticCatalogExamineField);
				}
			}
			else {
				if (!useFirstLineAsHeader) {
					int fieldsSize = examineFields.size();
					if (fieldsSize < lineLength) {
						for (int index = fieldsSize; index < lineLength; index++) {
							StaticCatalogExamineField staticCatalogExamineField = new StaticCatalogExamineField();
							staticCatalogExamineField.setName("Field " + (index + 1));
							examineFields.add(index, staticCatalogExamineField);
						}
					}
				}
				for (int index = 0; index < lineLength; index++) {
					HashMap<String, Long> uniqueValueCounts = examineFields.get(index).getUniqueValueCounts();
//					if (uniqueValueCounts.size() < maxUniqueValues) {
						long cnt = 0;
						if (uniqueValueCounts.containsKey(csvLine[index])) {
							cnt = uniqueValueCounts.get(csvLine[index]);
						}
//						if (cnt < maxUniqueValues) {
							cnt++;
							uniqueValueCounts.put(csvLine[index] + "", cnt);
//						}
//					}
				}
			}

			csvLineIndex++;
			if (csvLineIndex % 500000 == 0) {
				loopProgress.doProgress(csvLineIndex + " lines examined...");
			}

			csvLine = csvParser.parseNext();
		}
		
		loopProgress.doProgress(csvLineIndex + " lines done analyzing, try to find the types...");
		
		String[] possibleTypes = { "long", "double", "date" };
		for (int index = 0; index < examineFields.size(); index++) {
			
			StaticCatalogExamineField examineField = examineFields.get(index);
			
			ArrayList<String> searchTypes = new ArrayList<>(Arrays.asList(possibleTypes));
			HashMap<String, ArrayList<String>> typesExceptionValues = examineField.getFieldTypesExceptionValues();
			for (String possibleType : possibleTypes) {
				typesExceptionValues.put(possibleType, new ArrayList<>());
			}
			
			HashMap<String, Long> uniqueValueCounts = examineField.getUniqueValueCounts();
			int size = uniqueValueCounts.size();
			for (String key : uniqueValueCounts.keySet()) {

				if (searchTypes.size() == 0) {
					break;
				}
				else {
					if (searchTypes.contains("long")) {
						try {
							Long.parseLong(key); 
						} catch (NumberFormatException numberFormatException) {
							if (typesExceptionValues.get("long").size() < maxExceptions) {
								typesExceptionValues.get("long").add(key);
							}
							else {
								searchTypes.remove("long");	
							}
						}
					}
					if (searchTypes.contains("double")) {
						try {
							Double.parseDouble(key); 
						} catch (NumberFormatException numberFormatException) {
							if (typesExceptionValues.get("double").size() < maxExceptions) {
								typesExceptionValues.get("double").add(key);
							}
							else {
								searchTypes.remove("double");
							}
						}
					}
					if (searchTypes.contains("date")) {
						try {
							DateTime.parse(key); 
						} catch (Exception exception) {
							if (typesExceptionValues.get("date").size() < maxExceptions) {
								typesExceptionValues.get("date").add(key);
							}
							else {
								searchTypes.remove("date");
							}
						}
					}
				}
			}
			
			if ((searchTypes.contains("long")) && (size > typesExceptionValues.get("long").size())) {
				examineField.setType("long");
			}
			else if ((searchTypes.contains("double")) && (size > typesExceptionValues.get("double").size())) {
				examineField.setType("double");
			}
			else if ((searchTypes.contains("date")) && (size > typesExceptionValues.get("date").size())) {
				examineField.setType("date");
			}
			else {
				examineField.setType("text");
			}

//			else if (searchTypes.contains("long")) {
//				fieldTypes.add("integer number");
//			}
//			else if (searchTypes.contains("double")) {
//				fieldTypes.add("price number");
//			}
//			else if (searchTypes.contains("date")) {
//				fieldTypes.add("date");
//			}
		}
		
		loopProgress.doProgress("Group " + csvLineIndex + " lines done in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
		
	}

	/** Generate */
	public static void generate(String sourceCsvFileName, String filtersFileName, String templateFilename, String destinationFolderName,	
			boolean useFirstLineAsHeader, AtomicBoolean doLoop, LoopProgress loopProgress) {

		/* Generate */
		StaticCatalogTemplate filtersTemplate = generateFilters(sourceCsvFileName, filtersFileName, destinationFolderName, useFirstLineAsHeader, loopProgress);
		String filtersJson = S.saveObjectToJsonString(filtersTemplate);
		
		String templateString = S.loadFileInString(templateFilename);

		/* HTML */
		String indexHtmlFileName = destinationFolderName + File.separator + "site" + File.separator + "static-catalog.html";
		Template templateLiquid = Template.parse(templateString);
		String rendered = templateLiquid.render(filtersJson);
		try {
			Files.write(Paths.get(indexHtmlFileName), rendered.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException ioException) {
			L.e("Error writing 'index.html' file", ioException);
		}
		
		generateCatalog(sourceCsvFileName, filtersTemplate, destinationFolderName, useFirstLineAsHeader, loopProgress);
		
		Program.launch(indexHtmlFileName);
	}

	/** Generate filters */
	public static StaticCatalogTemplate generateFilters(String sourceCsvFileName, String filtersFileName, String destinationFolderName,	
			boolean useFirstLineAsHeader, LoopProgress loopProgress) {

		/* Filters */
		String filterFieldsFileName = destinationFolderName + File.separator + "site" + File.separator + "static-catalog-filters.json";
		
		boolean dev = true;
		if (dev) {
			StaticCatalogTemplate template = S.loadObjectFromJsonFileName(filterFieldsFileName, StaticCatalogTemplate.class);
			return template;
		}
		
		StaticCatalogTemplate template = new StaticCatalogTemplate();
		
		/* Generate */
		ArrayList<StaticCatalogFiltersField> filterFields = S.loadObjectFromJsonFileName(filtersFileName, StaticCatalogFilters.class).getFields();
		int lineLength = filterFields.size();
		/* Filters field names */
		LinkedHashMap<String, StaticCatalogFiltersField> nameFilters = new LinkedHashMap<>();
		for (StaticCatalogFiltersField filtersField : filterFields) {
			nameFilters.put(filtersField.getName(), filtersField);
			
			StaticCatalogTemplateFilter templateFilter = new StaticCatalogTemplateFilter();
			templateFilter.setLabel(filtersField.getLabel());
			String filtersFieldName = filtersField.getName();
			templateFilter.setName(filtersFieldName);
			templateFilter.setIdentifier(U.makeIdentifier(filtersFieldName));
			templateFilter.setType(filtersField.getType());
			templateFilter.setIs_displayed(filtersField.getIsFilter());

			template.getTemplate().getFilters().add(templateFilter);
		}
		
		/* Examine closely */
		long start = System.currentTimeMillis();
		loopProgress.doProgress("Start filters generation...");
		
		ArrayList<String> fieldNames = new ArrayList<>();
		
		CsvParserSettings csvParserSettings = new CsvParserSettings();
		csvParserSettings.setLineSeparatorDetectionEnabled(true);
		CsvParser csvParser = new CsvParser(csvParserSettings);
		csvParser.beginParsing(new File(sourceCsvFileName));
		
		String[] csvLine = csvParser.parseNext();
		
		LinkedHashMap<String, HashMap<String, Long>> uniqueExceptionValuesWithCount = new LinkedHashMap<>();
		LinkedHashMap<String, HashMap<String, Long>> uniqueValuesWithCount = new LinkedHashMap<>();
		for (String fieldName : nameFilters.keySet()) {
			uniqueExceptionValuesWithCount.put(fieldName, new HashMap<>());
			uniqueValuesWithCount.put(fieldName, new HashMap<>());
		}
		
		long csvLineIndex = 0;
		while (csvLine != null) {

			/* CSV field names */
			if ((csvLineIndex == 0) && (fieldNames.size() == 0)) {
				if (useFirstLineAsHeader) {
					for (int index = 0; index < lineLength; index++) {
						fieldNames.add(csvLine[index]);
					}
					csvLine = csvParser.parseNext();
					csvLineIndex++;
					continue;
				}
				else {
					for (int index = 0; index < lineLength; index++) {
						fieldNames.add("Field " + (index + 1));
					}
				}
			}
			
			for (int index = 0; index < lineLength; index++) {
				
				String fieldName = fieldNames.get(index);
				String fieldValue = csvLine[index];
				if (fieldValue == null) {
					fieldValue = "EMPTY";
				}
				
				if (!nameFilters.containsKey(fieldName)) {
					/* Is not a field filter */
					L.e("Inconsistent filters with the file", new Exception());
				}

				StaticCatalogFiltersField filtersField = nameFilters.get(fieldName);
				if (filtersField.getIsFilter()) {
					/* Defined filter */
					
					try {
						if (filtersField.getType().equals(TYPE_DATE)) {
							DateTime.parse(fieldValue);
						}
						if (filtersField.getType().equals(TYPE_LONG)) {
							Long.parseLong(fieldValue);
						}
						if (filtersField.getType().equals(TYPE_DOUBLE)) {
							Double.parseDouble(fieldValue);
						}
					}
					catch (Exception exception) {
						HashMap<String, Long> exceptions = uniqueExceptionValuesWithCount.get(fieldName);
						long cnt = 0;
						if (exceptions.containsKey(fieldValue)) {
							cnt = exceptions.get(fieldValue);
						}
						cnt++;
						exceptions.put(fieldValue, cnt);
						continue;
					}
					
					HashMap<String, Long> values = uniqueValuesWithCount.get(fieldName);
					long cnt = 0;
					if (values.containsKey(fieldValue)) {
						cnt = values.get(fieldValue);
					}
					cnt++;
					values.put(fieldValue, cnt);
				}
				else {
					/* Not defined as filter */
					
				}
			}

			csvLineIndex++;
			if (csvLineIndex % 500000 == 0) {
				loopProgress.doProgress(csvLineIndex + " lines examined...");
			}
			csvLine = csvParser.parseNext();
		}
		
		/* Sorting */
		for (StaticCatalogTemplateFilter templateFilter : template.getTemplate().getFilters()) {
			
			String fieldName = templateFilter.getName();
			HashMap<String, Long> exceptions = uniqueExceptionValuesWithCount.get(fieldName);
			HashMap<String, Long> values = uniqueValuesWithCount.get(fieldName);

			int maxDisplayValues = 20;
			int mainDisplayValues = 5;
			
			int exceptionsSize = exceptions.size();
			int valuesSize = values.size();
			int totalSize = exceptionsSize + valuesSize;

			
			
			templateFilter.setTotal_values_count(totalSize);
			
			if (totalSize > maxDisplayValues) {
				if (exceptionsSize > maxDisplayValues) {
					templateFilter.setHas_more_exception_values(true);
					templateFilter.setException_values_count(mainDisplayValues);
					templateFilter.setMore_exception_values_count(exceptionsSize - mainDisplayValues);
					
					templateFilter.setMore_values_count(valuesSize);		
				}
				else {
					templateFilter.setException_values_count(exceptionsSize);
					
					templateFilter.setHas_more_values(true);
					templateFilter.setValues_count(mainDisplayValues);
					templateFilter.setMore_values_count(valuesSize - mainDisplayValues);
				}
				
				templateFilter.setTotal_more_values_count(totalSize - mainDisplayValues);
			}
			else {
				templateFilter.setException_values_count(exceptionsSize);
				templateFilter.setValues_count(valuesSize);
			}
			
			String fieldType = templateFilter.getType(); 
			ArrayList<String> exceptionKeys = new ArrayList<>(exceptions.keySet());
			sortTypeKey("text", exceptionKeys);
			ArrayList<String> valueKeys = new ArrayList<>(values.keySet());
			sortTypeKey(fieldType, valueKeys);
			
			int exceptionsIndex = 0;
			int moreExceptionsIndex = templateFilter.getException_values_count();
			for (String exceptionKey : exceptionKeys) {

				StaticCatalogTemplateFilterValue filterValue = new StaticCatalogTemplateFilterValue();
				filterValue.setName(exceptionKey);
				filterValue.setCount(exceptions.get(exceptionKey));
				
				if (exceptionsIndex < moreExceptionsIndex) {
					templateFilter.getException_values().add(filterValue);
				}
				else {
					templateFilter.getMore_exception_values().add(filterValue);
				}
				
				exceptionsIndex++;
			}
			
			int valuesIndex = 0;
			int moreValuesIndex = templateFilter.getValues_count();
			for (String valueKey : valueKeys) {

				StaticCatalogTemplateFilterValue filterValue = new StaticCatalogTemplateFilterValue();
				filterValue.setIdentifier(U.makeIdentifier(fieldName) + "__" + U.makeIdentifier(valueKey));
				filterValue.setName(valueKey);
				filterValue.setCount(values.get(valueKey));
				
				if (valuesIndex < moreValuesIndex) {
					templateFilter.getValues().add(filterValue);
				}
				else {
					templateFilter.getMore_values().add(filterValue);
				}
				
				valuesIndex++;
			}
		}
		
		S.saveObjectToJsonFileName(template, filterFieldsFileName);
		
		loopProgress.doProgress(csvLineIndex + "Filters generated in " + ((System.currentTimeMillis() - start) / 1000) + " seconds, generate the catalog...");
		
		return template;
		
		
//		StaticCatalogExamine staticCatalogExamine = new StaticCatalogExamine();
//		
//		loadExamineCsv(sourceCsvFileName, staticCatalogExamine,
//		500,
//		typeMaxExceptions,
//		useFirstLineAsHeader,
//		doLoop, loopProgress);
//
//		S.saveObjectToJsonFileName(staticCatalogExamine, "C:\\Iustin\\Programming\\_static-catalog\\repositories\\static-catalog\\static-catalog-generator\\examine1.csv");

//		StaticCatalogExamine staticCatalogExamine = S.loadObjectFromJsonFileName("C:\\Iustin\\Programming\\_static-catalog\\repositories\\static-catalog\\static-catalog-generator\\examine1.json", StaticCatalogExamine.class);
//		S.saveObjectToJsonFileName(staticCatalogExamine, "C:\\Iustin\\Programming\\_static-catalog\\repositories\\static-catalog\\static-catalog-generator\\examine2.json");
//		LinkedHashMap<String, StaticCatalogExamineField> examineNameFields = new LinkedHashMap<>();
//		for (StaticCatalogExamineField examineFieldValue : staticCatalogExamine.getFields()) {
//			examineNameFields.put(examineFieldValue.getName(), examineFieldValue);	
//		}
		
		
		//String filtersJson = S.loadFileInString(filtersFileName);
		
		/* Catalog */
		
//		StaticCatalogTemplate template = new StaticCatalogTemplate();
//		
//		for (StaticCatalogFiltersField filtersField : filters.getFields()) {
//			
//			if (filtersField.getIsFilter()) {
//				
//				StaticCatalogTemplateFilter templateFilter = new StaticCatalogTemplateFilter();
//				templateFilter.setLabel(filtersField.getLabel());
//				templateFilter.setName(filtersField.getName());
//				templateFilter.setType(filtersField.getType());
//
//				StaticCatalogExamineField examineField = examineNameFields.get(filtersField.getName());
//
//				//templateFilter.getExceptions().addAll(examineField.getFieldTypesExceptionValues().get(examineField.getType()));
//				
//				
//				int valuesCount = examineField.getUniqueValueCounts().size();
//				templateFilter.setValues_count(valuesCount);
//				int moreValuesThreshhold = Integer.MAX_VALUE;
//				
//				if (valuesCount > 10) {
//					templateFilter.setHas_more_values(true);
//					moreValuesThreshhold = 5;
//				}
//				
//				int valueIndex = 0;
//				for (Entry<String, Long> uniqueValueCount : examineField.getUniqueValueCounts().entrySet()) {
//					
//					StaticCatalogTemplateFilterValue filterValue = new StaticCatalogTemplateFilterValue();
//					String filterValueLabel = uniqueValueCount.getKey(); 
//					if (filtersField.getType().equals("date")) {
//						
//						DateTime date = DateTime.parse(filterValueLabel);
//						filterValueLabel = date.toString("MMM dd yyyy");	
//					}
//					
//					filterValue.setName(filterValueLabel);
//					
//					
//					filterValue.setCount(uniqueValueCount.getValue());
//					if (valueIndex < moreValuesThreshhold) {
//						templateFilter.getMain_values().add(filterValue);	
//					}
//					else {
//						templateFilter.getMore_values().add(filterValue);
//					}
//					
//					valueIndex++;
//				}
//				templateFilter.setMore_values_count(templateFilter.getMore_values().size());
//				
//				template.getTemplate().getFilters().add(templateFilter);
//			}
//		}
	}


	/** Generate catalog */
	public static void generateCatalog(String sourceCsvFileName, StaticCatalogTemplate filtersTemplate,
			String destinationFolderName, boolean useFirstLineAsHeader, LoopProgress loopProgress) {

		/* Filters */
		String catalogBlocksFolderName = destinationFolderName + File.separator + "site" + File.separator + "catalog";
		String catalogFileName = destinationFolderName + File.separator + "site" + File.separator + "static-catalog.json";

		StaticCatalogTemplateCatalogRoot templateCatalogRoot = new StaticCatalogTemplateCatalogRoot();
		LinkedHashMap<String, LinkedHashMap<String, ArrayList<Integer>>> filterIdentifierBlocks = templateCatalogRoot.getTemplateCatalog().getFilterIdentifierBlocks();
		
		/* Filters */
		ArrayList<StaticCatalogTemplateFilter> templateFields = filtersTemplate.getTemplate().getFilters(); 
		int lineLength = templateFields.size();
		ArrayList<Integer> valuesIndexes = new ArrayList<>();
		LinkedHashMap<Integer, String> indexIdentifiers = new LinkedHashMap<>();
		for (int index = 0; index < lineLength; index++) {
			StaticCatalogTemplateFilter templateField = templateFields.get(index);
			if (templateField.getIs_displayed()) {
				valuesIndexes.add(index);
				String filterNameIdentifier = U.makeIdentifier(templateField.getName());
				indexIdentifiers.put(index, filterNameIdentifier);
				filterIdentifierBlocks.put(filterNameIdentifier, new LinkedHashMap<>());
			}
		}
		for (String filterNameIdentifier : filterIdentifierBlocks.keySet()) {
			LinkedHashMap<String, ArrayList<Integer>> filterNameIdentifierBlocks = filterIdentifierBlocks.get(filterNameIdentifier);
		
			ArrayList<StaticCatalogTemplateFilter> templateFilters = filtersTemplate.getTemplate().getFilters();
			
			for (StaticCatalogTemplateFilter templateFilter : templateFilters) {
				if (templateFilter.getIdentifier().equals(filterNameIdentifier)) {
					
					for (StaticCatalogTemplateFilterValue value : templateFilter.getException_values()) {
						filterNameIdentifierBlocks.put(value.getIdentifier(), new ArrayList<>());
					}
					for (StaticCatalogTemplateFilterValue value : templateFilter.getMore_exception_values()) {
						filterNameIdentifierBlocks.put(value.getIdentifier(), new ArrayList<>());
					}
					for (StaticCatalogTemplateFilterValue value : templateFilter.getValues()) {
						filterNameIdentifierBlocks.put(value.getIdentifier(), new ArrayList<>());
					}
					for (StaticCatalogTemplateFilterValue value : templateFilter.getMore_values()) {
						filterNameIdentifierBlocks.put(value.getIdentifier(), new ArrayList<>());
					}
					break;
				}
			}
		}
		
		/* Generate */
		long start = System.currentTimeMillis();
		loopProgress.doProgress("Start catalog generation...");
		
		ArrayList<String> fieldNames = new ArrayList<>();

		LinkedHashMap<String, Long> uniquePathsWithCount = new LinkedHashMap<>();
		
		CsvParserSettings csvParserSettings = new CsvParserSettings();
		csvParserSettings.setLineSeparatorDetectionEnabled(true);
		CsvParser csvParser = new CsvParser(csvParserSettings);
		csvParser.beginParsing(new File(sourceCsvFileName));
		
		CsvWriter csvWriter = null;
		
		long csvLineIndex = 0;
		int blockIndex = 0;
		int blockLineIndex = 0;
		int blockLines = 10000;
		String blockFilePrefix = catalogBlocksFolderName + File.separator + "block_";
		String blockFileName = blockFilePrefix;
		
		String[] headerLine = null;
		String[] csvLine = csvParser.parseNext();
		while (csvLine != null) {

			/* CSV field names */
			if ((csvLineIndex == 0) && (useFirstLineAsHeader)) {
				
				headerLine = csvLine;
				
				csvLine = csvParser.parseNext();
				csvLineIndex++;
				continue;
			}
			
			String path = "";
			String pathSep = "";
			
			for (int index = 0; index < lineLength; index++) {
				
				if (valuesIndexes.contains(index)) {
					String fieldValue = csvLine[index];
					if (fieldValue == null) {
						fieldValue = "EMPTY";
					}
//					path = path + pathSep + fieldValue;
//					pathSep = " / ";

					String fieldNameIdentifier = indexIdentifiers.get(index);
					LinkedHashMap<String, ArrayList<Integer>> filterNameIdentifierBlocks = filterIdentifierBlocks.get(fieldNameIdentifier);
					
					String fieldValueIdentifier = fieldNameIdentifier + "__" + U.makeIdentifier(fieldValue);
//					if (!filterNameIdentifierBlocks.containsKey(fieldValueIdentifier)) {
//						filterNameIdentifierBlocks.put(fieldValueIdentifier, new ArrayList<>());
//					}
					ArrayList<Integer> blocks = filterNameIdentifierBlocks.get(fieldValueIdentifier);
					if (!blocks.contains(blockIndex)) {
						blocks.add(blockIndex);
					}
				}
			}

			if (!uniquePathsWithCount.containsKey(path)) {
				uniquePathsWithCount.put(path, 0L);
			}
			
			if (blockLineIndex % blockLines == 0) {
				
				blockFileName = blockFilePrefix + blockIndex + ".csv";
				blockIndex++;
				blockLineIndex = 0;
				
				if (csvWriter != null) {
					csvWriter.close();
				}
				try {
					csvWriter = new CsvWriter(new FileWriter(blockFileName), new CsvWriterSettings());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				csvWriter.writeRow(headerLine);
			}

			csvWriter.writeRow(csvLine);
			blockLineIndex++;
			
			csvLineIndex++;
			if (csvLineIndex % 500000 == 0) {
				loopProgress.doProgress(csvLineIndex + " lines examined...");
			}
			csvLine = csvParser.parseNext();
		}
		csvParser.stopParsing();
		if (csvWriter != null) {
			csvWriter.close();
		}
		
		S.saveObjectToJsonFileName(templateCatalogRoot, catalogFileName);
		
		//L.p(uniquePathsWithCount.size() + "");

	}


	/** Sort type value */
	public static void sortTypeKey(String type, ArrayList<String> keys) {
		
		if (type.equals(TYPE_DATE)) {
			Collections.sort(keys, datetimeComparator);
		}

		if (type.equals(TYPE_LONG)) {
			Collections.sort(keys, longComparator);
//			ArrayList<Long> longKeys = new ArrayList<>();
//			keys.forEach(key -> longKeys.add(Long.parseLong(key)));
//			Collections.sort(longKeys);
//			keys.clear();
//			longKeys.forEach(longKey -> keys.add(longKey.toString()));
		}
				
		if (type.equals(TYPE_DOUBLE)) {
			Collections.sort(keys, doubleComparator);
//			ArrayList<Double> doubleKeys = new ArrayList<>();
//			keys.forEach(key -> doubleKeys.add(Double.parseDouble(key)));
//			Collections.sort(doubleKeys);
//			keys.clear();
//			doubleKeys.forEach(doubleKey -> keys.add(doubleKey.toString()));
		}

		if (type.equals(TYPE_TEXT)) {
			Collections.sort(keys, stringAsNumberComparator);
		}
	}
}
