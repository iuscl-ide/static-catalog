/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.stream.IntStream;

import org.eclipse.swt.program.Program;
import org.pojava.datetime.DateTime;
import org.static_catalog.main.E;
import org.static_catalog.main.L;
import org.static_catalog.main.S;
import org.static_catalog.main.U;
import org.static_catalog.model.dest.StaticCatalogPage;
import org.static_catalog.model.dest.StaticCatalogPageField;
import org.static_catalog.model.dest.StaticCatalogPageFieldValue;
import org.static_catalog.model.dest.StaticCatalogPageTotals;
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
import liqp.filters.Filter;

/** Generator engine */
public class StaticCatalogEngine {

	/** Path file separator */
	public static String fs = File.separator;
	
	/** Format long in liquid */
	static {
		Filter.registerFilter(new Filter("w") {
			@Override
			public Object apply(Object value, Object... params) {
				return U.w(new Long(value.toString()));
			}
		});

		Filter.registerFilter(new Filter("ws") {
			@Override
			public Object apply(Object value, Object... params) {
				return S.formatSize(new Long(value.toString()));
			}
		});
	}
	
	/** Types */
	public static final String TYPE_DATE = "date"; 
	public static final String TYPE_LONG = "long"; 
	public static final String TYPE_DOUBLE= "double"; 
	public static final String TYPE_TEXT = "text";
	
	private static final String TYPE_STRINGS = "strings"; 

	private static final String[] TYPE_NAMES = { TYPE_DATE, TYPE_LONG, TYPE_DOUBLE, TYPE_TEXT };
	public static final ArrayList<String> TYPES = new ArrayList<>(Arrays.asList(TYPE_NAMES));

	/** Filter types */
	public static final String FILTER_TYPE_NONE = "none";
	public static final String FILTER_TYPE_VALUES = "values"; 
	public static final String FILTER_TYPE_VALUE_INTERVALS = "value_intervals"; 
	public static final String FILTER_TYPE_LENGTH_INTERVALS = "length_intervals"; 
	public static final String FILTER_TYPE_KEYWORDS = "keywords"; 

	/** Display types */
	public static final String DISPLAY_TYPE_NONE = "none";
	public static final String DISPLAY_TYPE_CHECKBOXES = "checkboxes"; 
	public static final String DISPLAY_TYPE_DROPDOWN = "dropdown"; 
	public static final String DISPLAY_TYPE_RADIOBUTTONS = "radiobuttons";
	public static final String DISPLAY_TYPE_SEARCHBOX = "searchbox";

	/** Index split types */
	public static final String INDEX_SPLIT_TYPE_NONE = "none"; 
	public static final String INDEX_SPLIT_TYPE_NAMES = "names"; 
	public static final String INDEX_SPLIT_TYPE_VALUES = "values"; 
	
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
	public static long loadViewCsv(String csvCompleteFileName,
			ArrayList<String[]> csvFileGridLines, ArrayList<String> csvFileGridHeader,
			long maxLines, boolean useFirstLineAsHeader, LoopProgress loopProgress) {
	
		if (maxLines > 100001) {
			maxLines = 100001;
		}
		String wMaxLines = U.w(maxLines);
	
		CsvParserSettings csvParserSettings = new CsvParserSettings();
		csvParserSettings.setLineSeparatorDetectionEnabled(true);
		CsvParser csvParser = new CsvParser(csvParserSettings);
		csvParser.beginParsing(new File(csvCompleteFileName));
		
		String[] csvLine = csvParser.parseNext();
		long csvLineIndex = 0;
		boolean load = true;
		while (csvLine != null) {
			if (load) {
				int lineLength = csvLine.length;
				if (useFirstLineAsHeader && (csvLineIndex == 0)) {
					csvFileGridHeader.addAll(new ArrayList<String>(Arrays.asList(csvLine)));
				}
				else {
					if (!useFirstLineAsHeader) {
						int fieldsSize = csvFileGridHeader.size();
						if (fieldsSize < lineLength) {
							for (int index = fieldsSize; index < lineLength; index++) {
								csvFileGridHeader.add(index, "Field " + (index + 1));
							}
						}
					}
					csvFileGridLines.add(csvLine);
				}
			}
			csvLineIndex++;
			if (csvLineIndex % 100000 == 0) {
				loopProgress.doProgress(U.w(csvLineIndex) + " lines read (" + wMaxLines + " loaded)...");
			}
			if (csvLineIndex == maxLines) {
				load = false;
			}
			csvLine = csvParser.parseNext();
		}
		csvParser.stopParsing();
		
		return csvLineIndex;
	}
	
	/** Load examine CSV */
	public static void loadExamineCsv(String csvCompleteFileName, StaticCatalogExamineFields staticCatalogExamine,
			long maxUniqueValues, int maxExceptions, boolean useFirstLineAsHeader, LoopProgress loopProgress) {

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
			if (csvLineIndex % 100000 == 0) {
				loopProgress.doProgress(U.w(csvLineIndex) + " lines examined...");
			}

			csvLine = csvParser.parseNext();
		}
		
		loopProgress.doProgress(U.w(csvLineIndex) + " lines done examining, now try to find the types...");
		
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
						}
						catch (NumberFormatException numberFormatException) {
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
						}
						catch (NumberFormatException numberFormatException) {
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
						}
						catch (Exception exception) {
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
		
		loopProgress.doProgress("Examine and group " + U.w(csvLineIndex) + " lines done in " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
	}

	/** Generate */
	public static void generate(String sourceCsvFileName, String filtersFileName, String templateFilename, String destinationFolderName,	
			boolean useFirstLineAsHeader, LoopProgress loopProgress) {

		/* Total time */
		long start = System.currentTimeMillis();
		
		S.deleteFolder(destinationFolderName);
		
		/* Structure */
		loopProgress.doProgress("Start generation...");
		
		S.createFoldersIfNotExists(destinationFolderName);
		S.copyFolders(S.getFileFolderName(templateFilename), destinationFolderName, "gitignore; liquid");
		
		/* Generate fields */
		StaticCatalogPage page = new StaticCatalogPage();

		final HashMap<String, HashMap<String, String>> fieldNameValueIntervals = new HashMap<>();
		final LinkedHashMap<String, ArrayList<String>> fieldNameSortAscValues = new LinkedHashMap<>();
		final LinkedHashMap<String, ArrayList<String>> fieldNameSortDescValues = new LinkedHashMap<>();
		
		generateFields(page, sourceCsvFileName, filtersFileName, destinationFolderName, fieldNameValueIntervals, fieldNameSortAscValues, fieldNameSortDescValues, useFirstLineAsHeader, loopProgress);
		
		/* HTML */
		String templateString = S.loadFileInString(templateFilename);
		String templateBaseFileNameNoExtension = Paths.get(templateFilename).getFileName().toString();
		String ext = S.getExtension(templateBaseFileNameNoExtension);
		if (ext.trim().length() > 0) {
			templateBaseFileNameNoExtension = templateBaseFileNameNoExtension.substring(0, templateBaseFileNameNoExtension.length() - (ext.length() + 1));
		}
		String indexHtmlFileName = destinationFolderName + fs + templateBaseFileNameNoExtension + ".html";
		Template templateLiquid = Template.parse(templateString);
		String filtersJson = S.saveObjectToJsonString(page);
		String rendered = templateLiquid.render(filtersJson);
		S.saveStringToFile(rendered, indexHtmlFileName);
		
		/* Generate catalog */
		generateCatalog(sourceCsvFileName, page, destinationFolderName, fieldNameValueIntervals, fieldNameSortAscValues, fieldNameSortDescValues, useFirstLineAsHeader, loopProgress);
		
		String generationDone = "Generate completed in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.";
		loopProgress.doProgress(generationDone, generationDone);
		
		Program.launch(indexHtmlFileName);
	}

	/** Generate filters */
	public static void generateFields(StaticCatalogPage page, String sourceCsvFileName, String fieldsFiltersFileName, String destinationFolderName,
			HashMap<String, HashMap<String, String>> fieldNameValueIntervals, LinkedHashMap<String, ArrayList<String>> fieldNameSortAscValues, LinkedHashMap<String, ArrayList<String>> fieldNameSortDescValues,
			boolean useFirstLineAsHeader, LoopProgress loopProgress) {

		/* Fields and filters time */
		long start = System.currentTimeMillis();
		loopProgress.doProgress("Start fields and filters generation...");
		
		String filterFieldsFolderName = destinationFolderName + fs + "_catalog-page";
		S.createFoldersIfNotExists(filterFieldsFolderName);
		
		String catalogPageKeywordsFolderName = filterFieldsFolderName + fs + "keywords";
		S.createFoldersIfNotExists(catalogPageKeywordsFolderName);
		S.deleteFolderContentsOnly(catalogPageKeywordsFolderName);

		/* Filters */
		boolean dev = false;
		if (dev) {
			//StaticCatalogPage template = S.loadObjectFromJsonFileName(filterFieldsFileName, StaticCatalogPage.class);
			//return template;
		}
		
		/* Fields */
		ArrayList<StaticCatalogPageField> pageFields = page.getFields();
		
		/* Generate */
		StaticCatalogConfigurationFields configurationFieldsList = S.loadObjectFromJsonFileName(fieldsFiltersFileName, StaticCatalogConfigurationFields.class);
		page.setDescription(configurationFieldsList.getDescription());
		/* All fields will be exposed (!) as filters */
		ArrayList<StaticCatalogConfigurationField> configurationFields = configurationFieldsList.getFields();
		int lineLength = configurationFields.size();
		/* Filters field names */
		LinkedHashMap<String, StaticCatalogConfigurationField> fieldNames = new LinkedHashMap<>();
		int configurationFieldIndex = 0;
		int filtersCnt = 0;
		for (StaticCatalogConfigurationField configurationField : configurationFields) {
			if (configurationField.getIsFilter()) {
				filtersCnt++;
			}
			String configurationFieldName = configurationField.getName();
			
			StaticCatalogPageField newPageField = new StaticCatalogPageField();
			newPageField.setCsvIndex(configurationField.getCsvIndex());
			newPageField.setLabel(configurationField.getLabel());
			newPageField.setName(configurationFieldName);
			newPageField.setIndex(configurationFieldIndex);
			newPageField.setIdentifier("sc_field__" + configurationFieldIndex + "__" + U.makeIdentifier(configurationFieldName));
			configurationFieldIndex++;
			newPageField.setType(configurationField.getType());
			newPageField.setFilter(configurationField.getIsFilter());
			newPageField.setFilterType(configurationField.getFilterType());
			newPageField.setFilterDisplayType(configurationField.getDisplayType());
			newPageField.setSortAsc(configurationField.getIsSortAsc());
			newPageField.setSortDesc(configurationField.getIsSortDesc());
			newPageField.setSortAscLabel(configurationField.getSortAscLabel());
			newPageField.setSortDescLabel(configurationField.getSortDescLabel());

			fieldNames.put(configurationFieldName, configurationField);
			pageFields.add(newPageField);
		}
		
		/* Examine closely */
		ArrayList<String> csvFieldNames = new ArrayList<>();
		
		CsvParserSettings csvParserSettings = new CsvParserSettings();
		csvParserSettings.setLineSeparatorDetectionEnabled(true);
		CsvParser csvParser = new CsvParser(csvParserSettings);
		csvParser.beginParsing(new File(sourceCsvFileName));
		
		String[] csvLine = csvParser.parseNext();
		
		LinkedHashMap<String, HashMap<String, Long>> uniqueExceptionValuesWithCount = new LinkedHashMap<>();
		LinkedHashMap<String, HashMap<String, Long>> uniqueValuesWithCount = new LinkedHashMap<>();
		for (String fieldName : fieldNames.keySet()) {
			uniqueExceptionValuesWithCount.put(fieldName, new HashMap<>());
			uniqueValuesWithCount.put(fieldName, new HashMap<>());
		}
		
		long csvLineIndex = 0;
		while (csvLine != null) {

			/* CSV field names */
			if ((csvLineIndex == 0) && (csvFieldNames.size() == 0)) {
				if (useFirstLineAsHeader) {
					for (int index = 0; index < lineLength; index++) {
						csvFieldNames.add(csvLine[index]);
					}
					csvLine = csvParser.parseNext();
					csvLineIndex++;
					continue;
				}
				else {
					for (int index = 0; index < lineLength; index++) {
						csvFieldNames.add("Field " + (index + 1));
					}
				}
			}
			
			for (int index = 0; index < lineLength; index++) {
				
				String csvFieldName = csvFieldNames.get(index);
				if (!fieldNames.containsKey(csvFieldName)) {
					/* Is not a field filter */
					Exception exception = new Exception("Is not a field filter, filters inconsistent with the file");
					L.e("generateFields => csvFieldName: " + csvFieldName, exception);
					throw new E(exception);
				}

				StaticCatalogConfigurationField configurationField = fieldNames.get(csvFieldName);

				/* Defined filter */
				if (configurationField.getIsFilter() || configurationField.getIsSortAsc() || configurationField.getIsSortDesc()) {

					String csvFieldValue = csvLine[index];
					
					if (csvFieldValue == null) {
						HashMap<String, Long> exceptions = uniqueExceptionValuesWithCount.get(csvFieldName);
						long cnt = 0;
						if (exceptions.containsKey("NULL")) {
							cnt = exceptions.get("NULL");
						}
						cnt++;
						exceptions.put("NULL", cnt);
						continue;
					}
					try {
						if (configurationField.getType().equals(TYPE_DATE)) {
							DateTime.parse(csvFieldValue);
						}
						if (configurationField.getType().equals(TYPE_LONG)) {
							Long.parseLong(csvFieldValue);
						}
						if (configurationField.getType().equals(TYPE_DOUBLE)) {
							Double.parseDouble(csvFieldValue);
						}
					}
					catch (Exception exception) {
						HashMap<String, Long> exceptions = uniqueExceptionValuesWithCount.get(csvFieldName);
						long cnt = 0;
						if (exceptions.containsKey(csvFieldValue)) {
							cnt = exceptions.get(csvFieldValue);
						}
						cnt++;
						exceptions.put(csvFieldValue, cnt);
						continue;
					}

					HashMap<String, Long> values = uniqueValuesWithCount.get(csvFieldName);
					long cnt = 0;
					if (values.containsKey(csvFieldValue)) {
						cnt = values.get(csvFieldValue);
					}
					cnt++;
					values.put(csvFieldValue, cnt);
				}
				
//				if (configurationField.getIsSortAsc()) {
//					
//					
//				}
			}

			csvLineIndex++;
			if (csvLineIndex % 100000 == 0) {
				loopProgress.doProgress("Fields and filters " + U.w(csvLineIndex) + " lines closely examined...");
			}
			csvLine = csvParser.parseNext();
		}
		
		long totalLinesCount = csvLineIndex - (useFirstLineAsHeader ? 1 : 0);

		int totalLinesDigitsCount = (new Long(totalLinesCount)).toString().length();
		final StringBuilder indexLinesModuloDigits = new StringBuilder();
		indexLinesModuloDigits.append("1");
		IntStream.range(0, totalLinesDigitsCount).forEach(index -> indexLinesModuloDigits.append("0"));
		long indexLinesModulo = Long.parseLong(indexLinesModuloDigits.toString());
//		L.p(totalLines + " " + indexLinesModulo);
		
		long csvFileSize = S.findFileSizeInBytes(sourceCsvFileName);
		long lineSize = csvFileSize / totalLinesCount;
		long blockLinesCount = 1000000 / lineSize;
		blockLinesCount = (blockLinesCount / 100) * 100;
		
		/* Filters */
//		LinkedHashMap<String, StaticCatalogPageFilter> pageFilters = page.getFilters();
		
		/* Sorting */
		for (StaticCatalogPageField pageField : pageFields) {
			
			if (pageField.getFilter() || pageField.getSortAsc() || pageField.getSortDesc()) {
			
				String fieldName = pageField.getName();
				StaticCatalogConfigurationField configurationField = fieldNames.get(fieldName);
				String transformFormat = configurationField.getTransformFormat();
				String transformValues = configurationField.getTransformValues();
				HashMap<String, String> transformValuesLabels = new HashMap<>(); 
				if (transformValues != null) {
					String[] transformValuesKeysLabels = transformValues.split(";");
					for (String transformValuesKeyLabel : transformValuesKeysLabels) {
						transformValuesKeyLabel = transformValuesKeyLabel.trim();
						String[] transformValueKeyLabel = transformValuesKeyLabel.split("="); 
						transformValuesLabels.put(transformValueKeyLabel[0].trim(), transformValueKeyLabel[1].trim());
					}
				}
				
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
				
				pageField.setTotalValuesCount(totalSize);
				pageField.setTotalMoreValuesCount(totalSize > maxDisplayValues ? totalSize - minDisplayValues : 0);

				/* Sort values */
				ArrayList<String> exceptionKeys = new ArrayList<>(exceptions.keySet());
				sortTypeKey(TYPE_TEXT, exceptionKeys);
				
				String fieldType = pageField.getType();
				ArrayList<String> valueKeys = new ArrayList<>(values.keySet());
				sortTypeKey(fieldType, valueKeys);
				
				/* Filter */
				if (pageField.getFilter()) {
					
					int keyIndex = 0;
					String filterIdentifierPrefix = pageField.getIdentifier().replace("sc_field__", "sc_filter__");
					ArrayList<StaticCatalogPageFieldValue> pageFieldValues = pageField.getValues();
					
					String filterType = configurationField.getFilterType();
					
					/* Exceptions, common */
					if (!filterType.equals(FILTER_TYPE_KEYWORDS)) {
						
						for (String exceptionKey : exceptionKeys) {
							StaticCatalogPageFieldValue filterValue = new StaticCatalogPageFieldValue();
							filterValue.setIndex(keyIndex);
							filterValue.setIsException(true);
							filterValue.setIdentifier(filterIdentifierPrefix + "__e_" + keyIndex + "__" + U.makeIdentifier(exceptionKey));
							filterValue.setName(exceptionKey);
							filterValue.setLabel(transformValues != null ? transformValuesLabels.get(exceptionKey) : exceptionKey);
							filterValue.setCount(exceptions.get(exceptionKey));
							
							pageFieldValues.add(filterValue);
							keyIndex++;
						}
					}
					
					/* Values */
					if (filterType.equals(FILTER_TYPE_VALUES)) {
						for (String valueKey : valueKeys) {
			
							StaticCatalogPageFieldValue filterValue = new StaticCatalogPageFieldValue();
							filterValue.setIndex(keyIndex);
							filterValue.setIsException(false);
							filterValue.setIdentifier(filterIdentifierPrefix + "__" + keyIndex + "__" + U.makeIdentifier(valueKey));
							filterValue.setName(valueKey);
							filterValue.setLabel(createFormatTransformValue(valueKey, fieldType, transformFormat, transformValues, transformValuesLabels));
							filterValue.setCount(values.get(valueKey));

							pageFieldValues.add(filterValue);
							keyIndex++;
						}
					}
					/* Value interval */
					else if (filterType.equals(FILTER_TYPE_VALUE_INTERVALS)) {

						HashMap<String, String> valueIntervals = new HashMap<String, String>();
						fieldNameValueIntervals.put(fieldName, valueIntervals);
						
						String startInterval = null;
						String endInterval = null;
						int i1 = -1;
						int i2 = -1;
						
						Long markModulo = Long.parseLong(configurationField.getIntervalValue());
						Long mark = 0L;
						
						int valueKeysIndex = 0;
						int valueKeysCount = valueKeys.size();
						String intervalLabel;
						
						for (int valueKeyIndex = 0; valueKeyIndex < valueKeys.size(); valueKeyIndex++) {

							String valueKey = valueKeys.get(valueKeyIndex);
							
							if (configurationField.getType().equals(TYPE_LONG)) {
								Long value = Long.parseLong(valueKey);
								valueKeysIndex++;

								if (startInterval == null) {
									startInterval = valueKey;
									i1 = valueKeyIndex;
									mark = value / markModulo;
									continue;
								}
								
								if (value < (mark + 1) * markModulo) {
									/* Still in interval */
									endInterval = valueKey;
									i2 = valueKeyIndex;
									
									if (valueKeysIndex == valueKeysCount) {
										intervalLabel = createFormatTransformValue(startInterval, fieldType, transformFormat, transformValues, transformValuesLabels) + 
												" - " + createFormatTransformValue(endInterval, fieldType, transformFormat, transformValues, transformValuesLabels);
										pageFieldValues.add(createFilterInterval(keyIndex, filterIdentifierPrefix,
												startInterval, endInterval, intervalLabel,
												values, valueIntervals, i1, i2, valueKeys));
									}
								}
								else {
									if (endInterval == null) {
										endInterval = startInterval;
										i2 = i1;
									}
									intervalLabel = createFormatTransformValue(startInterval, fieldType, transformFormat, transformValues, transformValuesLabels) + 
											" - " + createFormatTransformValue(endInterval, fieldType, transformFormat, transformValues, transformValuesLabels);
									pageFieldValues.add(createFilterInterval(keyIndex, filterIdentifierPrefix,
											startInterval, endInterval, intervalLabel,
											values, valueIntervals, i1, i2, valueKeys));
									keyIndex++;

									startInterval = valueKey;
									i1 = valueKeyIndex;
									mark = value / markModulo;
									endInterval = null;
									i2 = -1;
										
									if (valueKeysIndex == valueKeysCount) {
										endInterval = startInterval;
										i2 = i1;
										intervalLabel = createFormatTransformValue(startInterval, fieldType, transformFormat, transformValues, transformValuesLabels) + 
												" - " + createFormatTransformValue(endInterval, fieldType, transformFormat, transformValues, transformValuesLabels);
										pageFieldValues.add(createFilterInterval(keyIndex, filterIdentifierPrefix,
												startInterval, endInterval, intervalLabel,
												values, valueIntervals, i1, i2, valueKeys));
									}
								}
							}
							else if (configurationField.getType().equals(TYPE_DOUBLE)) {
								Double value = Double.parseDouble(valueKey);
								valueKeysIndex++;

								if (startInterval == null) {
									startInterval = valueKey; 
									i1 = valueKeyIndex;
									mark = Math.round(value) / markModulo;
									continue;
								}
								
								if (value < (mark + 1) * markModulo) {
									/* Still in interval */
									endInterval = valueKey;
									i2 = valueKeyIndex;
									
									if (valueKeysIndex == valueKeysCount) {
										intervalLabel = createFormatTransformValue(startInterval, fieldType, transformFormat, transformValues, transformValuesLabels) + 
												" - " + createFormatTransformValue(endInterval, fieldType, transformFormat, transformValues, transformValuesLabels);
										pageFieldValues.add(createFilterInterval(keyIndex, filterIdentifierPrefix,
												startInterval, endInterval, intervalLabel,
												values, valueIntervals, i1, i2, valueKeys));
									}
								}
								else {
									if (endInterval == null) {
										endInterval = startInterval;
										i2 = i1;
									}
									intervalLabel = createFormatTransformValue(startInterval, fieldType, transformFormat, transformValues, transformValuesLabels) + 
											" - " + createFormatTransformValue(endInterval, fieldType, transformFormat, transformValues, transformValuesLabels);
									pageFieldValues.add(createFilterInterval(keyIndex, filterIdentifierPrefix,
											startInterval, endInterval, intervalLabel,
											values, valueIntervals, i1, i2, valueKeys));
									keyIndex++;

									startInterval = valueKey;
									i1 = valueKeyIndex;
									mark = Math.round(value) / markModulo;
									endInterval = null;
									i2 = -1;
										
									if (valueKeysIndex == valueKeysCount) {
										endInterval = startInterval;
										i2 = i1;
										intervalLabel = createFormatTransformValue(startInterval, fieldType, transformFormat, transformValues, transformValuesLabels) + 
												" - " + createFormatTransformValue(endInterval, fieldType, transformFormat, transformValues, transformValuesLabels);
										pageFieldValues.add(createFilterInterval(keyIndex, filterIdentifierPrefix,
												startInterval, endInterval, intervalLabel,
												values, valueIntervals, i1, i2, valueKeys));
									}
								}
							}
							else {
								/* Is not an integer or double */
								Exception exception = new Exception("The field is not integer and not double, cannot create value intervals filter on it");
								L.e("generateFields => configurationField: " + configurationField.getName(), exception);
								throw new E(exception);
							}
						}
						pageField.setTotalValuesCount(pageFieldValues.size());
						pageField.setTotalMoreValuesCount(0);
					}
					/* Length */
					else if (filterType.equals(FILTER_TYPE_LENGTH_INTERVALS)) {

						HashMap<String, String> valueIntervals = new HashMap<String, String>();
						fieldNameValueIntervals.put(fieldName, valueIntervals);

						String startInterval = null;
						String endInterval = null;
						
						int intervalLength = Integer.parseInt(configurationField.getIntervalValue());
						int valueKeysCount = valueKeys.size();
						String intervalLabel;
						
						int intervalIndex = 0;
						while (((intervalIndex + 1) * intervalLength - 1) < valueKeysCount) {
							int i1 = intervalIndex * intervalLength;
							startInterval = valueKeys.get(i1);
							intervalIndex++;
							int i2 = intervalIndex * intervalLength - 1;
							endInterval = valueKeys.get(i2);
							intervalLabel = createFormatTransformValue(startInterval, fieldType, transformFormat, transformValues, transformValuesLabels) + 
									" - " + createFormatTransformValue(endInterval, fieldType, transformFormat, transformValues, transformValuesLabels);
							pageFieldValues.add(createFilterInterval(keyIndex, filterIdentifierPrefix,
									startInterval, endInterval, intervalLabel,
									values, valueIntervals, i1, i2, valueKeys));
							keyIndex++;
						}
						int lastValuesCount = intervalIndex * intervalLength;
						if (lastValuesCount < valueKeysCount) {
							startInterval = valueKeys.get(lastValuesCount);
							endInterval = valueKeys.get(valueKeysCount - 1);
							intervalLabel = createFormatTransformValue(startInterval, fieldType, transformFormat, transformValues, transformValuesLabels) + 
									" - " + createFormatTransformValue(endInterval, fieldType, transformFormat, transformValues, transformValuesLabels);
							pageFieldValues.add(createFilterInterval(keyIndex, filterIdentifierPrefix,
									startInterval, endInterval, intervalLabel,
									values, valueIntervals, lastValuesCount, valueKeysCount - 1, valueKeys));
						}
						pageField.setTotalValuesCount(pageFieldValues.size());
						pageField.setTotalMoreValuesCount(0);
					}
					/* Keywords */
					else if (filterType.equals(FILTER_TYPE_KEYWORDS)) {
						
						sortTypeKey(TYPE_STRINGS, valueKeys);
						
						HashMap<String, String> valueIntervals = new HashMap<String, String>();
						fieldNameValueIntervals.put(fieldName, valueIntervals);
						
						String startInterval = null;
						String endInterval = null;
						int i1 = -1;
						int i2 = -1;
						
						int valueKeysIndex = 0;
						int valueKeysCount = valueKeys.size();
						
						ArrayList<LinkedHashMap<String, Long>> keywordIndexValueCounts = new ArrayList<>();
						
						for (int valueKeyIndex = 0; valueKeyIndex < valueKeys.size(); valueKeyIndex++) {

							String valueKey = valueKeys.get(valueKeyIndex);
							
							valueKeysIndex++;
							if (valueKey.length() < 2) {
								continue;
							}
							String value = valueKey.substring(0, 2);

							if (startInterval == null) {
								startInterval = value;
								i1 = valueKeyIndex;
								continue;
							}
							
							if (value.equals(startInterval)) {
								/* Still in interval */
								endInterval = value;
								i2 = valueKeyIndex;
								
								if (valueKeysIndex == valueKeysCount) {
									pageFieldValues.add(createKeywordsFilterInterval(keyIndex, filterIdentifierPrefix,
											startInterval, startInterval,
											values, valueIntervals, i1, i2, valueKeys, keywordIndexValueCounts));
								}
							}
							else {
								if (endInterval == null) {
									endInterval = startInterval;
									i2 = i1;
								}
								pageFieldValues.add(createKeywordsFilterInterval(keyIndex, filterIdentifierPrefix,
										startInterval, startInterval,
										values, valueIntervals, i1, i2, valueKeys, keywordIndexValueCounts));
								keyIndex++;

								startInterval = value;
								i1 = valueKeyIndex;
								endInterval = null;
								i2 = -1;
									
								if (valueKeysIndex == valueKeysCount) {
									endInterval = startInterval;
									i2 = i1;
									pageFieldValues.add(createKeywordsFilterInterval(keyIndex, filterIdentifierPrefix,
											startInterval, startInterval,
											values, valueIntervals, i1, i2, valueKeys, keywordIndexValueCounts));
								}
							}
						}
						
						int keywordsNameCnt = (new ArrayList<>(fieldNames.keySet())).indexOf(fieldName);
						String pageKeywordsFolder = catalogPageKeywordsFolderName + fs + "keywords-" + keywordsNameCnt;
						S.createFoldersIfNotExists(pageKeywordsFolder);

						int keywordsValueCnt = 0;
						for (LinkedHashMap<String, Long> keywordValueCounts : keywordIndexValueCounts) {
							
							ArrayList<String> keywordValueKeys = new ArrayList<>(keywordValueCounts.keySet());
							sortTypeKey(TYPE_TEXT, keywordValueKeys);
							
							LinkedHashMap<String, Long> sortedKeywordValueCounts = new LinkedHashMap<>();
							for (String keywordValueKey : keywordValueKeys) {
								sortedKeywordValueCounts.put(keywordValueKey, keywordValueCounts.get(keywordValueKey));
							}
							S.saveObjectToJsonFileName(sortedKeywordValueCounts,
								pageKeywordsFolder + fs + "static-catalog-page-keywords-" + keywordsNameCnt + "-" + keywordsValueCnt + ".json");
							keywordsValueCnt++;
						}
					}
				}
				
				/* SortAsc */
				if (pageField.getSortAsc()) {
					ArrayList<String> sortAscValues = new ArrayList<>();
					fieldNameSortAscValues.put(fieldName, sortAscValues);
					/* Exceptions */
					for (String exceptionKey : exceptionKeys) {
						sortAscValues.add(exceptionKey);
					}
					/* Values */
					for (String valueKey : valueKeys) {
						sortAscValues.add(valueKey);
					}
				}
				
				/* SortDesc */
				if (pageField.getSortDesc()) {
					ArrayList<String> sortDescValues = new ArrayList<>();
					fieldNameSortDescValues.put(fieldName, sortDescValues);
					/* Values */
					for (int indexDesc = valueKeys.size() - 1; indexDesc >= 0; indexDesc--) {
						sortDescValues.add(valueKeys.get(indexDesc));
					}
					/* Exceptions */
					for (int indexDesc = exceptionKeys.size() - 1; indexDesc >= 0; indexDesc--) {
						sortDescValues.add(exceptionKeys.get(indexDesc));
					}
				}
			}
		}

		/* Totals */
		StaticCatalogPageTotals totals = page.getTotals();
		
		totals.setTotalLines(totalLinesCount);
		totals.setTotalCsvFileSize(S.findFileSizeInBytes(sourceCsvFileName));
		totals.setTotalFields(lineLength);
		totals.setTotalFilters(filtersCnt);
		totals.setBlockLines(blockLinesCount);
		totals.setIndexLinesModulo(indexLinesModulo);
		
		String filterFieldsFileName = filterFieldsFolderName + fs + "static-catalog-page.json";
		S.saveObjectToJsonFileName(page, filterFieldsFileName);
		
		String filtersDone = "For " + U.w(csvLineIndex) + " lines, fields and filters generated in " + ((System.currentTimeMillis() - start) / 1000) + " seconds";
		loopProgress.doProgress(filtersDone, filtersDone);
	}
	
	/** Format, transform */
	private static String createFormatTransformValue(String value, String type, String transformFormat, String transformValues, HashMap<String, String> transformValuesLabels) {
		
		String resValue = value;
		
		if (transformFormat != null) {
			if (type.equals(TYPE_DATE)) {
				DateTime dateTime = DateTime.parse(value);
				resValue = dateTime.toString(transformFormat);
			}
		}
		else {
			if (transformValues != null) {
				resValue = transformValuesLabels.get(value);
			}
		}
		
		return resValue;
	}

	/** Create filter interval */
	private static StaticCatalogPageFieldValue createFilterInterval(int keyIndex, String filterIdentifierPrefix, String startInterval, String endInterval, String label,
			HashMap<String, Long> values, HashMap<String, String> valueIntervals, int i1, int i2, ArrayList<String> valueKeys) {

		StaticCatalogPageFieldValue filterValue = new StaticCatalogPageFieldValue();
		filterValue.setIndex(keyIndex);
		filterValue.setIsException(false);
		String intervalName = startInterval;
		if (startInterval.equals(endInterval)) {
			filterValue.setIdentifier(filterIdentifierPrefix + "__" + keyIndex + "__" + U.makeIdentifier(intervalName));
		}
		else {
			intervalName = startInterval + " to " + endInterval;
			filterValue.setIdentifier(filterIdentifierPrefix + "__" + keyIndex + "__" + U.makeIdentifier(startInterval) + "_to_" + U.makeIdentifier(endInterval));
		}
		filterValue.setName(intervalName);
		filterValue.setLabel(label);

		long intervalCount = 0;
		for (int index = i1; index <= i2; index++) {
			valueIntervals.put(valueKeys.get(index), intervalName);
			intervalCount = intervalCount + values.get(valueKeys.get(index));
		}
		filterValue.setCount(intervalCount);
		
		return filterValue;
	}

	/** Create filter interval */
	private static StaticCatalogPageFieldValue createKeywordsFilterInterval(int keyIndex, String filterIdentifierPrefix, String startInterval, String label,
			HashMap<String, Long> values, HashMap<String, String> valueIntervals, int i1, int i2, ArrayList<String> valueKeys,
			ArrayList<LinkedHashMap<String, Long>> keywordIndexValueCounts) {

		StaticCatalogPageFieldValue filterValue = new StaticCatalogPageFieldValue();
		filterValue.setIndex(keyIndex);
		filterValue.setIsException(false);
		String intervalName = startInterval;
		filterValue.setIdentifier(filterIdentifierPrefix + "__" + keyIndex + "__" + U.makeIdentifier(intervalName));
		filterValue.setName(intervalName);
		filterValue.setLabel(label);

		LinkedHashMap<String, Long> keywordValueCounts = new LinkedHashMap<>(); 
		
		long intervalCount = 0;
		for (int index = i1; index <= i2; index++) {
			String keywordValue = valueKeys.get(index); 
			valueIntervals.put(keywordValue, intervalName);
			long keywordCount = values.get(keywordValue);
			intervalCount = intervalCount + keywordCount;
			
			keywordValueCounts.put(keywordValue, keywordCount);
		}
		filterValue.setCount(intervalCount);
		keywordIndexValueCounts.add(keywordValueCounts);
		
		return filterValue;
	}

	/** Generate catalog */
	public static void generateCatalog(String sourceCsvFileName, StaticCatalogPage page, String destinationFolderName,
			HashMap<String, HashMap<String, String>> fieldNameValueIntervals, LinkedHashMap<String, ArrayList<String>> fieldNameSortAscValues, LinkedHashMap<String, ArrayList<String>> fieldNameSortDescValues,
			boolean useFirstLineAsHeader, LoopProgress loopProgress) {

		/* Indexes and blocks generation time */ 
		long start = System.currentTimeMillis();
		loopProgress.doProgress("Start catalog files generation...");
		
		/* Files */
		String catalogFolderName = destinationFolderName + fs + "_catalog";
		S.createFoldersIfNotExists(catalogFolderName);
		
		String catalogBlocksFolderName = catalogFolderName + fs + "data";
		S.createFoldersIfNotExists(catalogBlocksFolderName);
		S.deleteFolderContentsOnly(catalogBlocksFolderName);
		String blockFilePrefix = catalogBlocksFolderName + fs + "block-";
		
		String catalogIndexesFolderName = catalogFolderName + fs + "indexes";
		S.createFoldersIfNotExists(catalogIndexesFolderName);
		S.deleteFolderContentsOnly(catalogIndexesFolderName);
		String indexesValueFileNamePrefix = catalogIndexesFolderName + fs + "static-catalog-index";

		String catalogKeywordsFolderName = catalogFolderName + fs + "keywords";
		S.createFoldersIfNotExists(catalogKeywordsFolderName);
		S.deleteFolderContentsOnly(catalogKeywordsFolderName);

		String catalogSortFolderName = catalogFolderName + fs + "sort";
		S.createFoldersIfNotExists(catalogSortFolderName);
		S.deleteFolderContentsOnly(catalogSortFolderName);
		String sortFileNamePrefix = catalogSortFolderName + fs + "static-catalog-sort";

		LinkedHashMap<String, LinkedHashMap<String, ArrayList<Long>>> fieldNameIndexValueLines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, ArrayList<Long>>>> fieldNameIndexValueCsvValueLines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, ArrayList<Long>>> fieldNameSortAscValueLines = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, ArrayList<Long>>> fieldNameSortDescValueLines = new LinkedHashMap<>();
		
		/* Filters */
		ArrayList<StaticCatalogPageField> pageFields = page.getFields();
		
		int lineLength = pageFields.size();
		ArrayList<Integer> filterIndexes = new ArrayList<>();
		ArrayList<Integer> sortAscIndexes = new ArrayList<>();
		ArrayList<Integer> sortDescIndexes = new ArrayList<>();
		ArrayList<String> fieldNames = new ArrayList<>();

		for (int index = 0; index < lineLength; index++) {
			StaticCatalogPageField pageField = pageFields.get(index);
			String fieldName = pageField.getName();
			fieldNames.add(fieldName);
			
			if (pageField.getFilter()) {
				filterIndexes.add(index);
				/* All values */
				if (pageField.getFilterType().equals(FILTER_TYPE_KEYWORDS)) {
					LinkedHashMap<String, LinkedHashMap<String, ArrayList<Long>>> filterValuesCsvValues = new LinkedHashMap<>();
					for (StaticCatalogPageFieldValue value : pageField.getValues()) {
						filterValuesCsvValues.put(value.getName(), new LinkedHashMap<>());
					}
					fieldNameIndexValueCsvValueLines.put(fieldName, filterValuesCsvValues);
				}
				else {
					LinkedHashMap<String, ArrayList<Long>> filterValues = new LinkedHashMap<>();
					for (StaticCatalogPageFieldValue value : pageField.getValues()) {
						filterValues.put(value.getName(), new ArrayList<>());
					}
					fieldNameIndexValueLines.put(fieldName, filterValues);
				}
			}
			if (pageField.getSortAsc()) {
				sortAscIndexes.add(index);
				/* All values */
				LinkedHashMap<String, ArrayList<Long>> sortAscValueLines = new LinkedHashMap<>();
				ArrayList<String> sortAscValues = fieldNameSortAscValues.get(fieldName);
				for (String value : sortAscValues) {
					sortAscValueLines.put(value, new ArrayList<>());
				}
				fieldNameSortAscValueLines.put(fieldName, sortAscValueLines);
			}
			if (pageField.getSortDesc()) {
				sortDescIndexes.add(index);
				/* All values */
				LinkedHashMap<String, ArrayList<Long>> sortDescValueLines = new LinkedHashMap<>();
				ArrayList<String> sortDescValues = fieldNameSortDescValues.get(fieldName);
				for (String value : sortDescValues) {
					sortDescValueLines.put(value, new ArrayList<>());
				}
				fieldNameSortDescValueLines.put(fieldName, sortDescValueLines);
			}
		}
		
		/* Generate */
		CsvParserSettings csvParserSettings = new CsvParserSettings();
		csvParserSettings.setLineSeparatorDetectionEnabled(true);
		CsvParser csvParser = new CsvParser(csvParserSettings);
		csvParser.beginParsing(new File(sourceCsvFileName));
		
		CsvWriter csvWriter = null;
		
		long csvLineIndex = 0;
		int blockIndex = -1;
		long blockLineIndex = 0;
		
		long blockLinesCount = page.getTotals().getBlockLines();
		long indexLinesModulo = page.getTotals().getIndexLinesModulo();
		
		String[] csvLine = csvParser.parseNext();
		while (csvLine != null) {

			/* CSV field names */
			if (csvLineIndex == 0) {
				if (useFirstLineAsHeader) {
					csvLine = csvParser.parseNext();
					csvLineIndex++;
					continue;
				}
				else {
					csvLineIndex++;
				}
			}
			
			/* Blocks */
			if (blockLineIndex % blockLinesCount == 0) {
				
				blockIndex++;
				String blockFileName = blockFilePrefix + blockIndex + ".csv";
				blockLineIndex = 0;
				
				if (csvWriter != null) {
					csvWriter.close();
				}
				try {
					FileOutputStream blockOutputStream = new FileOutputStream(blockFileName);
					csvWriter = new CsvWriter(blockOutputStream, "UTF-8", new CsvWriterSettings());
				}
				catch (IOException ioException) {
					L.e("generateCatalog => blockIndex: " + blockIndex + ", blockFileName: " + blockFileName, ioException);
					throw new E(ioException);
				}
				//csvWriter.writeRow(headerLine);
			}

			csvWriter.writeRow(csvLine);
			blockLineIndex++;
			
			/* Indexes */
			for (int index : filterIndexes) {
				createLineInterval(pageFields, index, csvLine, fieldNameValueIntervals, fieldNameIndexValueLines, fieldNameIndexValueCsvValueLines, csvLineIndex, indexLinesModulo, false);
			}
			/* SortAsc */
			for (int index : sortAscIndexes) {
				createLineInterval(pageFields, index, csvLine, fieldNameValueIntervals, fieldNameSortAscValueLines, fieldNameIndexValueCsvValueLines, csvLineIndex, indexLinesModulo, true);
			}
			/* SortDesc */
			for (int index : sortDescIndexes) {
				createLineInterval(pageFields, index, csvLine, fieldNameValueIntervals, fieldNameSortDescValueLines, fieldNameIndexValueCsvValueLines, csvLineIndex, indexLinesModulo, true);
			}
			
			csvLineIndex++;
			if (csvLineIndex % 100000 == 0) {
				loopProgress.doProgress("Catalog files generation " + U.w(csvLineIndex) + " lines taken into account...");
			}
			csvLine = csvParser.parseNext();
		}
		csvParser.stopParsing();
		if (csvWriter != null) {
			csvWriter.close();
		}
		
		/* INDEX_SPLIT_TYPE_VALUES */
		/* Indexes */
//		int indexCnt = 0;
//		for (String fieldName : fieldNameIndexValueLines.keySet()) {
//			LinkedHashMap<String, ArrayList<Long>> valueLines = fieldNameIndexValueLines.get(fieldName);
//			int indexNameCnt = fieldNames.indexOf(fieldName);
//			int indexValueCnt = 0;
//			for (ArrayList<Long> lines : valueLines.values()) {
//				S.saveObjectToJsonFileName(lines, indexesValueFileNamePrefix + "-" + indexNameCnt + "-" + indexValueCnt + ".json");
//				indexCnt++;
//				indexValueCnt++;
//			}
//		}

		/* INDEX_SPLIT_TYPE_NAMES */
		/* Indexes */
		int indexCnt = 0;
		for (String fieldName : fieldNameIndexValueLines.keySet()) {
			LinkedHashMap<String, ArrayList<Long>> valueLines = fieldNameIndexValueLines.get(fieldName);
			int indexNameCnt = fieldNames.indexOf(fieldName);
			int indexValueCnt = 0;
			LinkedHashMap<Integer, ArrayList<Long>> indexValueCntLines = new LinkedHashMap<>();
			for (ArrayList<Long> lines : valueLines.values()) {
				indexValueCntLines.put(indexValueCnt, lines);
				indexValueCnt++;
			}
			S.saveObjectToJsonFileName(indexValueCntLines, indexesValueFileNamePrefix + "-" + indexNameCnt + ".json");
		}

		/* Keywords */
		int keywordsCnt = 0;
		for (String fieldName : fieldNameIndexValueCsvValueLines.keySet()) {
			LinkedHashMap<String, LinkedHashMap<String, ArrayList<Long>>> indexValueCsvValueLines = fieldNameIndexValueCsvValueLines.get(fieldName);
			int keywordsNameCnt = fieldNames.indexOf(fieldName);
			int keywordsValueCnt = 0;
			
			if (indexValueCsvValueLines.size() == 0) {
				S.deleteFolder(catalogKeywordsFolderName);
			}
			for (LinkedHashMap<String, ArrayList<Long>> csvValueLines : indexValueCsvValueLines.values()) {
				
				ArrayList<String> csvValueKeys = new ArrayList<>(csvValueLines.keySet());
				sortTypeKey(TYPE_TEXT, csvValueKeys);
				
				LinkedHashMap<String, ArrayList<Long>> sortedCsvValueLines = new LinkedHashMap<>();
				for (String csvValueKey : csvValueKeys) {
					ArrayList<Long> lines = csvValueLines.get(csvValueKey);
					sortedCsvValueLines.put(csvValueKey, lines);
				}
				String keywordsFolder = catalogKeywordsFolderName + fs + "keywords-" + keywordsNameCnt;
				S.createFoldersIfNotExists(keywordsFolder);
				S.saveObjectToJsonFileName(sortedCsvValueLines, keywordsFolder + fs + "static-catalog-keywords-" + keywordsNameCnt + "-" + keywordsValueCnt + ".json");
				keywordsCnt++;
				keywordsValueCnt++;
			}
		}

		/* Sort */
		int sortCnt = 0;
		for (String fieldName : fieldNameSortAscValueLines.keySet()) {
			LinkedHashMap<String, ArrayList<Long>> valueLines = fieldNameSortAscValueLines.get(fieldName);
			int indexNameCnt = fieldNames.indexOf(fieldName);
			ArrayList<Long> ascSortedLines = new ArrayList<>();
			for (ArrayList<Long> lines : valueLines.values()) {
				ascSortedLines.addAll(lines);
			}
//			ascSortedLines = compactLines(ascSortedLines, indexLinesModulo);
			S.saveObjectToJsonFileName(ascSortedLines, sortFileNamePrefix + "-" + indexNameCnt + "-asc.json");
			sortCnt++;
		}
		for (String fieldName : fieldNameSortDescValueLines.keySet()) {
			LinkedHashMap<String, ArrayList<Long>> valueLines = fieldNameSortDescValueLines.get(fieldName);
			int indexNameCnt = fieldNames.indexOf(fieldName);
			ArrayList<Long> descSortedLines = new ArrayList<>();
			for (ArrayList<Long> lines : valueLines.values()) {
				descSortedLines.addAll(lines);
			}
			S.saveObjectToJsonFileName(descSortedLines, sortFileNamePrefix + "-" + indexNameCnt + "-desc.json");
			sortCnt++;
		}

		String catalogFilesDone = "For " + U.w(csvLineIndex) + " lines: " + (blockIndex + 1) + " data block, " + indexCnt + " index, " + keywordsCnt + " keywords, " + sortCnt + " sort catalog files generated in " + ((System.currentTimeMillis() - start) / 1000) + " seconds";
		loopProgress.doProgress(catalogFilesDone, catalogFilesDone);
	}

	/* Lines interval */
	private static void createLineInterval(ArrayList<StaticCatalogPageField> pageFields, int fieldIndex, String[] csvLine,
			HashMap<String, HashMap<String, String>> fieldNameValueIntervals,
			LinkedHashMap<String, LinkedHashMap<String, ArrayList<Long>>> fieldNameIndexValueLines,
			LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, ArrayList<Long>>>> fieldNameIndexValueCsvValueLines,
			long csvLineIndex, long indexLinesModulo, boolean isSort) {
		
		StaticCatalogPageField pageField = pageFields.get(fieldIndex);
		String csvFieldValue = csvLine[pageField.getCsvIndex() - 1];
		if (csvFieldValue == null) {
			csvFieldValue = "NULL";
		}
		String fieldName = pageField.getName();
		
		/* Find the lines */
		ArrayList<Long> lines = null;
		if (isSort) {
			lines = fieldNameIndexValueLines.get(fieldName).get(csvFieldValue);
		}
		else {
			String filterType = pageField.getFilterType();
			if (filterType.equals(FILTER_TYPE_VALUES)) {
				lines = fieldNameIndexValueLines.get(fieldName).get(csvFieldValue);
			}
			else if (filterType.equals(FILTER_TYPE_VALUE_INTERVALS) || filterType.equals(FILTER_TYPE_LENGTH_INTERVALS)) {
				lines = fieldNameIndexValueLines.get(fieldName).get(fieldNameValueIntervals.get(fieldName).get(csvFieldValue));
			}
			else if (filterType.equals(FILTER_TYPE_KEYWORDS)) {
				LinkedHashMap<String, ArrayList<Long>> csvValueLines = fieldNameIndexValueCsvValueLines.get(fieldName).get(fieldNameValueIntervals.get(fieldName).get(csvFieldValue));
				if (csvValueLines == null) {
					return;
				}	
				if (csvValueLines.containsKey(csvFieldValue)) {
					lines = csvValueLines.get(csvFieldValue);
				}
				else {
					lines = new ArrayList<>();
					csvValueLines.put(csvFieldValue, lines);
				}
			}
		}

		int linesSize = lines.size();
		if (linesSize == 0) {
			lines.add(csvLineIndex);	
		}
		else {
			long lastLine = lines.get(linesSize - 1);
			long intervalFirstLine = lastLine / indexLinesModulo;
			long intervalSecondLine = lastLine % indexLinesModulo;
			
			if (intervalFirstLine == 0) {
				/* One line */
				if (csvLineIndex - intervalSecondLine == 1) {
					/* New interval */
					long newLastLine = intervalSecondLine * indexLinesModulo + csvLineIndex;
					lines.set(linesSize - 1, newLastLine);
				}
				else {
					/* New line */
					lines.add(csvLineIndex);
				}
			}
			else {
				/* Interval */
				if (csvLineIndex - intervalSecondLine == 1) {
					/* Add to interval */
					long newLastLine = intervalFirstLine * indexLinesModulo + csvLineIndex;
					lines.set(linesSize - 1, newLastLine);
				}
				else {
					/* New line */
					lines.add(csvLineIndex);
				}
			}
		}
	}
	
	/** Sort type value */
	public static void sortTypeKey(String type, ArrayList<String> keys) {

		switch (type) {
		case TYPE_DATE:
			Collections.sort(keys, datetimeComparator);
			break;
		case TYPE_LONG:
			Collections.sort(keys, longComparator);
			break;
		case TYPE_DOUBLE:
			Collections.sort(keys, doubleComparator);
			break;
		case TYPE_TEXT:
			Collections.sort(keys, stringAsNumberComparator);
			break;
		case TYPE_STRINGS:
			Collections.sort(keys);
			break;
		}
	}
}
