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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.program.Program;
import org.pojava.datetime.DateTime;
import org.static_catalog.main.L;
import org.static_catalog.main.S;
import org.static_catalog.main.U;
import org.static_catalog.model.dest.StaticCatalogPage;
import org.static_catalog.model.dest.StaticCatalogPageField;
import org.static_catalog.model.dest.StaticCatalogPageFieldValue;
import org.static_catalog.model.dest.StaticCatalogPageFilter;
import org.static_catalog.model.dest.StaticCatalogSearch;
import org.static_catalog.model.src.StaticCatalogConfigurationField;
import org.static_catalog.model.src.StaticCatalogConfigurationFields;
import org.static_catalog.model.src.StaticCatalogExamineField;
import org.static_catalog.model.src.StaticCatalogExamineFields;
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

	private static final String[] TYPE_NAMES = { TYPE_DATE, TYPE_LONG, TYPE_DOUBLE, TYPE_TEXT };
	public static final ArrayList<String> TYPES = new ArrayList<>(Arrays.asList(TYPE_NAMES));
	
	/** Display types */
	public static final String DISPLAY_TYPE_NONE = "none"; 
	public static final String DISPLAY_TYPE_CHECKBOXES = "checkboxes";
	public static final String DISPLAY_TYPE_DROPDOWN = "dropdown"; 
	public static final String DISPLAY_TYPE_RADIOBUTTONS = "radiobuttons"; 

	
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
//	public static void loadViewCsv(String csvCompleteFileName,
//			ArrayList<String[]> csvFileGridLines, ArrayList<String> csvFileGridHeader,
//			long maxLines, boolean useFirstLineAsHeader,
//			AtomicBoolean doLoop, LoopProgress loopProgress) {

public static void loadViewCsv(String csvCompleteFileName,
		ArrayList<String[]> csvFileGridLines, ArrayList<String> csvFileGridHeader,
		long maxLines, boolean useFirstLineAsHeader, LoopProgress loopProgress) {
		
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
			
//			if (!doLoop.get()) {
//				csvParser.stopParsing();
//				return;
//			}
			
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
	public static void loadExamineCsv(String csvCompleteFileName, StaticCatalogExamineFields staticCatalogExamine,
			long maxUniqueValues, int maxExceptions, boolean useFirstLineAsHeader,
			AtomicBoolean doLoop, LoopProgress loopProgress) {

		long start = System.currentTimeMillis();
		
		loopProgress.doProgress("Start lines examine...");
		
		ArrayList<StaticCatalogExamineField> examineFields = staticCatalogExamine.getExamineFields();
		
		CsvParserSettings csvParserSettings = new CsvParserSettings();
		csvParserSettings.setLineSeparatorDetectionEnabled(true);
		CsvParser csvParser = new CsvParser(csvParserSettings);
		csvParser.beginParsing(new File(csvCompleteFileName));
		
		String nullValue = UUID.randomUUID().toString();
		
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
					long cnt = 0;
					String lineFieldValue = csvLine[index];
					if (lineFieldValue == null) {
						lineFieldValue = nullValue;
					}
					if (uniqueValueCounts.containsKey(lineFieldValue)) {
						cnt = uniqueValueCounts.get(lineFieldValue);
					}
					cnt++;
					uniqueValueCounts.put(lineFieldValue, cnt);
					
////					if (uniqueValueCounts.size() < maxUniqueValues) {
//						long cnt = 0;
//						if (uniqueValueCounts.containsKey(csvLine[index])) {
//							cnt = uniqueValueCounts.get(csvLine[index]);
//						}
////						if (cnt < maxUniqueValues) {
//							cnt++;
//							uniqueValueCounts.put(csvLine[index] + "", cnt);
////						}
////					}
				}
			}

			csvLineIndex++;
			if (csvLineIndex % 500000 == 0) {
				loopProgress.doProgress(csvLineIndex + " lines examined...");
			}

			csvLine = csvParser.parseNext();
		}
		
		loopProgress.doProgress(csvLineIndex + " lines done analyzing, try to find the types...");
		
		for (int index = 0; index < examineFields.size(); index++) {
			
			StaticCatalogExamineField examineField = examineFields.get(index);
			
			ArrayList<String> searchTypes = new ArrayList<>(TYPES);
			HashMap<String, ArrayList<String>> typesExceptionValues = examineField.getFieldTypesExceptionValues();
			for (String possibleType : TYPES) {
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
					if (searchTypes.contains("text")) {
						if (key.equals(nullValue)) {
							if (typesExceptionValues.get("text").size() < maxExceptions) {
								typesExceptionValues.get("text").add("NULL");
							}
							else {
								searchTypes.remove("text");
							}
						}
					}
				}
			}
			
			if (uniqueValueCounts.containsKey(nullValue)) {
				long nullCnt = uniqueValueCounts.get(nullValue);
				uniqueValueCounts.remove(nullValue);
				uniqueValueCounts.put("NULL", nullCnt);
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
			
//			else if ((searchTypes.contains("text")) && (size > typesExceptionValues.get("text").size())) {
//				examineField.setType("text");
//			}
//			else {
//				/* No types without exception */
//				L.e("No types without exception", new Exception());
//			}
		}
		
		loopProgress.doProgress("Group examine " + csvLineIndex + " lines done in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
		
	}

	/** Generate */
	public static void generate(String sourceCsvFileName, String filtersFileName, String templateFilename, String destinationFolderName,	
			boolean useFirstLineAsHeader, AtomicBoolean doLoop, LoopProgress loopProgress) {

		/* Generate */
		StaticCatalogPage filtersTemplate = generateFilters(sourceCsvFileName, filtersFileName, destinationFolderName, useFirstLineAsHeader, loopProgress);
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
	public static StaticCatalogPage generateFilters(String sourceCsvFileName, String filtersFileName, String destinationFolderName,	
			boolean useFirstLineAsHeader, LoopProgress loopProgress) {

		/* Filters */
		String filterFieldsFileName = destinationFolderName + File.separator + "site" + File.separator + "static-catalog-fields.json";
		
		boolean dev = false;
		if (dev) {
			StaticCatalogPage template = S.loadObjectFromJsonFileName(filterFieldsFileName, StaticCatalogPage.class);
			return template;
		}
		
		StaticCatalogPage page = new StaticCatalogPage();
		/* Fields */
		ArrayList<StaticCatalogPageField> pageFields = page.getPage().getFields();
		
		/* Generate */
		ArrayList<StaticCatalogConfigurationField> filterFields = S.loadObjectFromJsonFileName(filtersFileName, StaticCatalogConfigurationFields.class).getFields();
		int lineLength = filterFields.size();
		/* Filters field names */
		LinkedHashMap<String, StaticCatalogConfigurationField> nameFilters = new LinkedHashMap<>();
		for (StaticCatalogConfigurationField filtersField : filterFields) {
			String filtersFieldName = filtersField.getName();
			
			StaticCatalogPageField newPageField = new StaticCatalogPageField();
			newPageField.setLabel(filtersField.getLabel());
			newPageField.setName(filtersFieldName);
			newPageField.setIdentifier("sc_filter__" + U.makeIdentifier(filtersFieldName));
			newPageField.setType(filtersField.getType());
			newPageField.setFilter(filtersField.getIsFilter());

			nameFilters.put(filtersFieldName, filtersField);
			pageFields.add(newPageField);
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
				if (!nameFilters.containsKey(fieldName)) {
					/* Is not a field filter */
					L.e("Inconsistent filters with the file", new Exception());
				}

				String fieldValue = csvLine[index];
				if (fieldValue == null) {
					HashMap<String, Long> exceptions = uniqueExceptionValuesWithCount.get(fieldName);
					long cnt = 0;
					if (exceptions.containsKey("NULL")) {
						cnt = exceptions.get("NULL");
					}
					cnt++;
					exceptions.put("NULL", cnt);
					continue;
				}

				StaticCatalogConfigurationField configurationField = nameFilters.get(fieldName);
				if (configurationField.getIsFilter()) {
					/* Defined filter */
					
					try {
						if (configurationField.getType().equals(TYPE_DATE)) {
							DateTime.parse(fieldValue);
						}
						if (configurationField.getType().equals(TYPE_LONG)) {
							Long.parseLong(fieldValue);
						}
						if (configurationField.getType().equals(TYPE_DOUBLE)) {
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
		
		/* Filters */
		LinkedHashMap<String, StaticCatalogPageFilter> pageFilters = page.getPage().getFilters();
		
		/* Sorting */
		for (StaticCatalogPageField pageField : pageFields) {
			
			String fieldName = pageField.getName();
			StaticCatalogConfigurationField configurationField = nameFilters.get(fieldName);
			
			HashMap<String, Long> exceptions = uniqueExceptionValuesWithCount.get(fieldName);
			HashMap<String, Long> values = uniqueValuesWithCount.get(fieldName);

			Integer maxDisplayValues = configurationField.getMaxDisplayValues();
			if (maxDisplayValues == null) {
				maxDisplayValues = 35;
			}
			Integer minDisplayValues = configurationField.getMinDisplayValues();
			if (minDisplayValues == null) {
				minDisplayValues = 5;
			}
			
			int exceptionsSize = exceptions.size();
			int valuesSize = values.size();
			int totalSize = exceptionsSize + valuesSize;
	
			pageField.setTotal_values_count(totalSize);
			
			if (totalSize > maxDisplayValues) {
				if (exceptionsSize > maxDisplayValues) {
					pageField.setHas_more_exception_values(true);
					pageField.setException_values_count(minDisplayValues);
					pageField.setMore_exception_values_count(exceptionsSize - minDisplayValues);
					
					pageField.setMore_values_count(valuesSize);		
				}
				else {
					pageField.setException_values_count(exceptionsSize);
					
					pageField.setHas_more_values(true);
					pageField.setValues_count(minDisplayValues);
					pageField.setMore_values_count(valuesSize - minDisplayValues);
				}
				
				pageField.setTotal_more_values_count(totalSize - minDisplayValues);
			}
			else {
				pageField.setException_values_count(exceptionsSize);
				pageField.setValues_count(valuesSize);
			}
			
			String fieldType = pageField.getType(); 
			ArrayList<String> exceptionKeys = new ArrayList<>(exceptions.keySet());
			sortTypeKey("text", exceptionKeys);
			ArrayList<String> valueKeys = new ArrayList<>(values.keySet());
			sortTypeKey(fieldType, valueKeys);
			
			int exceptionsIndex = 0;
			int moreExceptionsIndex = pageField.getException_values_count();
			String fieldIdentifier = pageField.getIdentifier();
			for (String exceptionKey : exceptionKeys) {

				String valueIdentifier = fieldIdentifier + "__" + U.makeIdentifier(exceptionKey);
				StaticCatalogPageFieldValue filterValue = new StaticCatalogPageFieldValue();
				filterValue.setIdentifier(valueIdentifier);
				filterValue.setName(exceptionKey);
				filterValue.setLabel(exceptionKey); // TODO
				filterValue.setCount(exceptions.get(exceptionKey));
				
				if (exceptionsIndex < moreExceptionsIndex) {
					pageField.getException_values().add(filterValue);
				}
				else {
					pageField.getMore_exception_values().add(filterValue);
				}
				
				StaticCatalogPageFilter pageFilter = new StaticCatalogPageFilter();
				pageFilter.setField(fieldName);
				pageFilter.setValue(exceptionKey);
				pageFilters.put(valueIdentifier, pageFilter);
				
				exceptionsIndex++;
			}
			
			int valuesIndex = 0;
			int moreValuesIndex = pageField.getValues_count();
			for (String valueKey : valueKeys) {

				String valueIdentifier = fieldIdentifier + "__" + U.makeIdentifier(valueKey);
				StaticCatalogPageFieldValue filterValue = new StaticCatalogPageFieldValue();
				filterValue.setIdentifier(valueIdentifier);
				filterValue.setName(valueKey);
				filterValue.setLabel(valueKey); // TODO
				filterValue.setCount(values.get(valueKey));
				
				if (valuesIndex < moreValuesIndex) {
					pageField.getValues().add(filterValue);
				}
				else {
					pageField.getMore_values().add(filterValue);
				}

				StaticCatalogPageFilter pageFilter = new StaticCatalogPageFilter();
				pageFilter.setField(fieldName);
				pageFilter.setValue(valueKey);
				pageFilters.put(valueIdentifier, pageFilter);

				valuesIndex++;
			}
		}
		
		S.saveObjectToJsonFileName(page, filterFieldsFileName);
		
		loopProgress.doProgress(csvLineIndex + "Filters generated in " + ((System.currentTimeMillis() - start) / 1000) + " seconds, generate the catalog...");
		
		return page;
		
		
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
	public static void generateCatalog(String sourceCsvFileName, StaticCatalogPage page,
			String destinationFolderName, boolean useFirstLineAsHeader, LoopProgress loopProgress) {

		/* Filters */
		String catalogBlocksFolderName = destinationFolderName + File.separator + "site" + File.separator + "catalog";
		String catalogFileName = destinationFolderName + File.separator + "site" + File.separator + "static-catalog.json";

		StaticCatalogSearch templateCatalogRoot = new StaticCatalogSearch();
		LinkedHashMap<String, LinkedHashMap<String, ArrayList<Integer>>> nameValuesBlocks = templateCatalogRoot.getSearchCatalog().getNameValuesBlocks();
		
		/* Filters */
		ArrayList<StaticCatalogPageField> pageFields = page.getPage().getFields();
		
		int lineLength = pageFields.size();
		ArrayList<Integer> valuesIndexes = new ArrayList<>();
//		LinkedHashMap<Integer, String> indexIdentifiers = new LinkedHashMap<>();
		for (int index = 0; index < lineLength; index++) {
			StaticCatalogPageField pageField = pageFields.get(index);
			if (pageField.getFilter()) {
				valuesIndexes.add(index);
//				String pageFieldNameIdentifier = U.makeIdentifier(pageField.getName());
//				indexIdentifiers.put(index, pageFieldNameIdentifier);
//				filterIdentifierBlocks.put(pageFieldNameIdentifier, new LinkedHashMap<>());
				
				LinkedHashMap<String, ArrayList<Integer>> valuesBlocks = new LinkedHashMap<>(); 
				nameValuesBlocks.put(pageField.getName(), valuesBlocks);
				for (StaticCatalogPageFieldValue value : pageField.getException_values()) {
					valuesBlocks.put(value.getName(), new ArrayList<>());
				}
				for (StaticCatalogPageFieldValue value : pageField.getMore_exception_values()) {
					valuesBlocks.put(value.getName(), new ArrayList<>());
				}
				for (StaticCatalogPageFieldValue value : pageField.getValues()) {
					valuesBlocks.put(value.getName(), new ArrayList<>());
				}
				for (StaticCatalogPageFieldValue value : pageField.getMore_values()) {
					valuesBlocks.put(value.getName(), new ArrayList<>());
				}
			}
		}
//		for (String fieldName : nameValuesBlocks.keySet()) {
//			LinkedHashMap<String, ArrayList<Integer>> valuesBlocks = nameValuesBlocks.get(fieldName);
//		
//			//ArrayList<StaticCatalogPageField> templateFilters = page.getPage().getFields();
//			
//			for (int index : valuesIndexes) {
//				for (StaticCatalogPageFieldValue value : pageField.getException_values()) {
//					valuesBlocks.put(value.getIdentifier(), new ArrayList<>());
//				}
//				for (StaticCatalogPageFieldValue value : pageField.getMore_exception_values()) {
//					valuesBlocks.put(value.getIdentifier(), new ArrayList<>());
//				}
//				for (StaticCatalogPageFieldValue value : pageField.getValues()) {
//					valuesBlocks.put(value.getIdentifier(), new ArrayList<>());
//				}
//				for (StaticCatalogPageFieldValue value : pageField.getMore_values()) {
//					valuesBlocks.put(value.getIdentifier(), new ArrayList<>());
//				}
//			}
//		}
		
//		/* Generate size blocks */
//		long start = System.currentTimeMillis();
//		loopProgress.doProgress("Start catalog generation...");
//		
//		ArrayList<String> fieldNames = new ArrayList<>();
//
//		LinkedHashMap<String, Long> uniquePathsWithCount = new LinkedHashMap<>();
//		
//		CsvParserSettings csvParserSettings = new CsvParserSettings();
//		csvParserSettings.setLineSeparatorDetectionEnabled(true);
//		CsvParser csvParser = new CsvParser(csvParserSettings);
//		csvParser.beginParsing(new File(sourceCsvFileName));
//		
//		CsvWriter csvWriter = null;
//		
//		long csvLineIndex = 0;
//		int blockIndex = -1;
//		int blockLineIndex = 0;
//		int blockLines = 10000;
//		String blockFilePrefix = catalogBlocksFolderName + File.separator + "block_";
//		String blockFileName = blockFilePrefix;
//		
//		String[] headerLine = null;
//		String[] csvLine = csvParser.parseNext();
//		while (csvLine != null) {
//
//			/* CSV field names */
//			if ((csvLineIndex == 0) && (useFirstLineAsHeader)) {
//				
//				headerLine = csvLine;
//				// TODO
//				csvLine = csvParser.parseNext();
//				csvLineIndex++;
//				continue;
//			}
//			
////			String path = "";
////			String pathSep = "";
////			
////
////			if (!uniquePathsWithCount.containsKey(path)) {
////				uniquePathsWithCount.put(path, 0L);
////			}
//			
//			if (blockLineIndex % blockLines == 0) {
//				
//				blockIndex++;
//				blockFileName = blockFilePrefix + blockIndex + ".csv";
//				blockLineIndex = 0;
//				
//				if (csvWriter != null) {
//					csvWriter.close();
//				}
//				try {
//					csvWriter = new CsvWriter(new FileWriter(blockFileName), new CsvWriterSettings());
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				csvWriter.writeRow(headerLine);
//			}
//
//			for (int index : valuesIndexes) {
//				
//				String fieldValue = csvLine[index];
//				if (fieldValue == null) {
//					fieldValue = "NULL";
//				}
////					path = path + pathSep + fieldValue;
////					pathSep = " / ";
//
//				String fieldName = pageFields.get(index).getName();
//				LinkedHashMap<String, ArrayList<Integer>> filterNameIdentifierBlocks = nameValuesBlocks.get(fieldName);
//				
////				String fieldValueIdentifier = fieldNameIdentifier + "__" + U.makeIdentifier(fieldValue);
////					if (!filterNameIdentifierBlocks.containsKey(fieldValueIdentifier)) {
////						filterNameIdentifierBlocks.put(fieldValueIdentifier, new ArrayList<>());
////					}
//				ArrayList<Integer> blocks = filterNameIdentifierBlocks.get(fieldValue);
//				if (!blocks.contains(blockIndex)) {
//					blocks.add(blockIndex);
//				}
//			}
//			
//			csvWriter.writeRow(csvLine);
//			blockLineIndex++;
//			
//			csvLineIndex++;
//			if (csvLineIndex % 500000 == 0) {
//				loopProgress.doProgress(csvLineIndex + " lines examined...");
//			}
//			csvLine = csvParser.parseNext();
//		}
//		csvParser.stopParsing();
//		if (csvWriter != null) {
//			csvWriter.close();
//		}
//		
//		S.saveObjectToJsonFileName(templateCatalogRoot, catalogFileName);
		
		//L.p(uniquePathsWithCount.size() + "");
		
		/* Generate filter blocks */
		long start = System.currentTimeMillis();
		loopProgress.doProgress("Start catalog blocks generation...");
		
		ArrayList<String> fieldNames = new ArrayList<>();
		LinkedHashMap<String, StaticCatalogPageField> filterNames = new LinkedHashMap<>();
		LinkedHashMap<String, Integer> filterNamesIndex = new LinkedHashMap<>();
		int index = 0;
		for (StaticCatalogPageField field : page.getPage().getFields()) {
			String name = field.getName();
			fieldNames.add(name);
			if (field.getFilter()) {
				filterNames.put(name, field);
				filterNamesIndex.put(name, index);
			}
			index++;
		}

		ArrayList<String> readBlocks = new ArrayList<>();
//		ArrayList<String> prevWriteBlockNames = new ArrayList<>();
		ArrayList<String> writeBlockNames = new ArrayList<>();
		
		readBlocks.add(sourceCsvFileName);
		writeBlockNames.add("Block");
		
		long writeBlockNamesTotal = 0;
		
//		int cnt = 0;
//		for (String filterFieldName : filterNames.keySet()) {
//			
////			if (cnt++ > 2) {
////				break;
////			}
//			int filterIndex = filterNamesIndex.get(filterFieldName);
//			
//			
//			StaticCatalogPageField field = filterNames.get(filterFieldName);
//			
//			ArrayList<StaticCatalogPageFieldValue> fieldValues = new ArrayList<>();
//			fieldValues.addAll(field.getException_values());
//			fieldValues.addAll(field.getMore_exception_values());
//			fieldValues.addAll(field.getValues());
//			fieldValues.addAll(field.getMore_values());
//
//			readBlocks.clear();
//			readBlocks.addAll(writeBlockNames);
//			writeBlockNames.clear();
//			long writeBlockNamesIndex = 0;
			
			
//			for (String prevWriteBlockName : prevWriteBlockNames) {
//				int valueIndex = 0;
//				for (StaticCatalogPageFieldValue fieldValue : fieldValues) {
//					writeBlockNames.add(prevWriteBlockName + "_" + valueIndex++);
//				}
//			}

			//L.p(filterFieldName + ": " + writeBlockNames.size());
			
//			for (String readBlock : readBlocks) {
//
//				CsvParserSettings csvParserSettings = new CsvParserSettings();
//				csvParserSettings.setLineSeparatorDetectionEnabled(true);
//				CsvParser csvParser = new CsvParser(csvParserSettings);
//				csvParser.beginParsing(new File(sourceCsvFileName));
//
//				long csvLineIndex = 0;
//				String[] csvLine = csvParser.parseNext();
//				while (csvLine != null) {
//
//					String filterValue = csvLine[filterIndex];
//					if (filterValue == null) {
//						filterValue = "EMPTY";
//					}
//					int valueIndex = 0;
//					for (StaticCatalogPageFieldValue fieldValue : fieldValues) {
//						String fieldValueName = null;
//						if (fieldValue == null) {
//							fieldValueName = "EMPTY";
//						}
//						else {
//							fieldValueName = fieldValue.getName();
//						}
//						if (filterValue.equals(fieldValueName)) {
//							String blockName = readBlock + "_" + valueIndex;
////							L.p(blockName);
//							if (!writeBlockNames.contains(blockName)) {
//								L.p(filterValue + ": " + blockName + " __ " + writeBlockNamesIndex++ + " ___ " + writeBlockNamesTotal++);
//								writeBlockNames.add(blockName);
//							}
//						}
//						valueIndex++;
//					}
//					
//					csvLineIndex++;
//					if (csvLineIndex % 500000 == 0) {
//						loopProgress.doProgress("For filter: " + filterFieldName + ", " + csvLineIndex + " lines examined...");
//					}
//					csvLine = csvParser.parseNext();
//				}
//				csvParser.stopParsing();
////				if (csvWriter != null) {
////					csvWriter.close();
////				}
//			}
//			for (String writeBlockName : writeBlockNames) {
//				L.p(writeBlockName);
//			}
//		}
		
		
//		LinkedHashMap<String, Long> uniquePathsWithCount = new LinkedHashMap<>();
//		
//		CsvParserSettings csvParserSettings = new CsvParserSettings();
//		csvParserSettings.setLineSeparatorDetectionEnabled(true);
//		CsvParser csvParser = new CsvParser(csvParserSettings);
//		csvParser.beginParsing(new File(sourceCsvFileName));
//		
//		CsvWriter csvWriter = null;
//		
//		long csvLineIndex = 0;
//		int blockIndex = -1;
//		int blockLineIndex = 0;
//		int blockLines = 10000;
//		String blockFilePrefix = catalogBlocksFolderName + File.separator + "block_";
//		String blockFileName = blockFilePrefix;
//		
//		String[] headerLine = null;
//		String[] csvLine = csvParser.parseNext();
//		while (csvLine != null) {
//
//			/* CSV field names */
//			if ((csvLineIndex == 0) && (useFirstLineAsHeader)) {
//				
//				headerLine = csvLine;
//				// TODO
//				csvLine = csvParser.parseNext();
//				csvLineIndex++;
//				continue;
//			}
//			
////			String path = "";
////			String pathSep = "";
////			
////
////			if (!uniquePathsWithCount.containsKey(path)) {
////				uniquePathsWithCount.put(path, 0L);
////			}
//			
//			if (blockLineIndex % blockLines == 0) {
//				
//				blockIndex++;
//				blockFileName = blockFilePrefix + blockIndex + ".csv";
//				blockLineIndex = 0;
//				
//				if (csvWriter != null) {
//					csvWriter.close();
//				}
//				try {
//					csvWriter = new CsvWriter(new FileWriter(blockFileName), new CsvWriterSettings());
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				csvWriter.writeRow(headerLine);
//			}
//
//			for (int index : valuesIndexes) {
//				
//				String fieldValue = csvLine[index];
//				if (fieldValue == null) {
//					fieldValue = "NULL";
//				}
////					path = path + pathSep + fieldValue;
////					pathSep = " / ";
//
//				String fieldName = pageFields.get(index).getName();
//				LinkedHashMap<String, ArrayList<Integer>> filterNameIdentifierBlocks = nameValuesBlocks.get(fieldName);
//				
////				String fieldValueIdentifier = fieldNameIdentifier + "__" + U.makeIdentifier(fieldValue);
////					if (!filterNameIdentifierBlocks.containsKey(fieldValueIdentifier)) {
////						filterNameIdentifierBlocks.put(fieldValueIdentifier, new ArrayList<>());
////					}
//				ArrayList<Integer> blocks = filterNameIdentifierBlocks.get(fieldValue);
//				if (!blocks.contains(blockIndex)) {
//					blocks.add(blockIndex);
//				}
//			}
//			
//			csvWriter.writeRow(csvLine);
//			blockLineIndex++;
//			
//			csvLineIndex++;
//			if (csvLineIndex % 500000 == 0) {
//				loopProgress.doProgress(csvLineIndex + " lines examined...");
//			}
//			csvLine = csvParser.parseNext();
//		}
//		csvParser.stopParsing();
//		if (csvWriter != null) {
//			csvWriter.close();
//		}
//		
//		S.saveObjectToJsonFileName(templateCatalogRoot, catalogFileName);

		
		HashMap<String, Long> keys = new HashMap<String, Long>();
		
		CsvParserSettings csvParserSettings = new CsvParserSettings();
		csvParserSettings.setLineSeparatorDetectionEnabled(true);
		CsvParser csvParser = new CsvParser(csvParserSettings);
		csvParser.beginParsing(new File(sourceCsvFileName));

		long csvLineIndex = 0;
		String[] csvLine = csvParser.parseNext();
		while (csvLine != null) {

			String key = "";
			String sep = "__";
			for (String filterFieldName : filterNames.keySet()) {
				
//				if (cnt++ > 2) {
//					break;
//				}
				int filterIndex = filterNamesIndex.get(filterFieldName);
//				StaticCatalogPageField field = filterNames.get(filterFieldName);

//				if (filterValue == null) {
//					filterValue = "EMPTY";
//				}

				key = key + sep + csvLine[filterIndex];
			}

			if (!keys.containsKey(key)) {
				L.p(key);
				keys.put(key, 0L);
			}
			
			csvLineIndex++;
			if (csvLineIndex % 500000 == 0) {
				loopProgress.doProgress("For keys, lines examined...");
			}
			csvLine = csvParser.parseNext();
		}
		
		L.p(keys.size() + "");
	}

	/** Sort type value */
	public static void sortTypeKey(String type, ArrayList<String> keys) {
		
		if (type.equals(TYPE_DATE)) {
			Collections.sort(keys, datetimeComparator);
		}
		if (type.equals(TYPE_LONG)) {
			Collections.sort(keys, longComparator);
		}
		if (type.equals(TYPE_DOUBLE)) {
			Collections.sort(keys, doubleComparator);
		}
		if (type.equals(TYPE_TEXT)) {
			Collections.sort(keys, stringAsNumberComparator);
		}
	}
}
