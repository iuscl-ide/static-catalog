/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.engine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
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
import org.static_catalog.main.L;
import org.static_catalog.main.S;
import org.static_catalog.main.U;
import org.static_catalog.model.dest.StaticCatalogIndexes;
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

	private static final String[] TYPE_NAMES = { TYPE_DATE, TYPE_LONG, TYPE_DOUBLE, TYPE_TEXT };
	public static final ArrayList<String> TYPES = new ArrayList<>(Arrays.asList(TYPE_NAMES));
	
	/** Display types */
	public static final String DISPLAY_TYPE_NONE = "none"; 
	public static final String DISPLAY_TYPE_CHECKBOXES = "checkboxes"; 
	public static final String DISPLAY_TYPE_DROPDOWN = "dropdown"; 
	public static final String DISPLAY_TYPE_RADIOBUTTONS = "radiobuttons"; 

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
								csvFileGridHeader.add(index, "Column " + (index + 1));
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
			if (csvLineIndex % 500000 == 0) {
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
		
		loopProgress.doProgress("Examine and group " + U.w(csvLineIndex) + " lines done in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
	}

	/** Generate */
	public static void generate(String sourceCsvFileName, String filtersFileName, String templateFilename, String destinationFolderName,	
			boolean useFirstLineAsHeader, LoopProgress loopProgress) {

		/* Total time */
		long start = System.currentTimeMillis();
		
		S.deleteFolder(destinationFolderName);
		
		/* Structure */
		loopProgress.doProgress("Verify template site...");
		Path destinationPath = Paths.get(destinationFolderName);
		if (Files.notExists(destinationPath, LinkOption.NOFOLLOW_LINKS)) {
			try {
				loopProgress.doProgress("Create template site...");
				
				Files.createDirectories(destinationPath);
				Path templateFilePath = Paths.get(templateFilename);
				Path templateFolderPath = templateFilePath.getParent(); 
//				L.p(templateFolderPath.toString());
				S.copyFolders(templateFolderPath, destinationPath, "gitignore; liquid");
			} catch (IOException ioException) {
				L.e("Generation - Can't create (write in ?) destination folder: " + destinationFolderName, ioException);
				return;
			}
		}
		loopProgress.doProgress("Template site OK");
		
		/* Generate fields */
		StaticCatalogPage page = new StaticCatalogPage();
//		StaticCatalogContents contents = new StaticCatalogContents();

		generateFields(page, sourceCsvFileName, filtersFileName, destinationFolderName, useFirstLineAsHeader, loopProgress);
		
		String filterFieldsFileName = destinationFolderName + File.separator + "static-catalog-fields.json";
		S.saveObjectToJsonFileName(page, filterFieldsFileName);
		String catalogFolderName = destinationFolderName + File.separator + "_catalog";
		S.createFoldersIfNotExists(catalogFolderName);
//		String catalogContentsFileName = catalogFolderName + File.separator + "static-catalog.json";
//		S.saveObjectToJsonFileName(contents, catalogContentsFileName);

		/* HTML */
		String templateString = S.loadFileInString(templateFilename);
		String templateBaseFileNameNoExtension = Paths.get(templateFilename).getFileName().toString();
		String ext = S.getExtension(templateBaseFileNameNoExtension);
		if (ext.trim().length() > 0) {
			templateBaseFileNameNoExtension = templateBaseFileNameNoExtension.substring(0, templateBaseFileNameNoExtension.length() - (ext.length() + 1));
		}
		
		String indexHtmlFileName = destinationFolderName + File.separator + templateBaseFileNameNoExtension + ".html";
		Template templateLiquid = Template.parse(templateString);
		String filtersJson = S.saveObjectToJsonString(page);
		String rendered = templateLiquid.render(filtersJson);
		try {
			Files.write(Paths.get(indexHtmlFileName), rendered.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException ioException) {
			L.e("Error writing '" + templateBaseFileNameNoExtension + "' file", ioException);
		}
		
		/* Generate catalog */
		generateCatalog(sourceCsvFileName, page, destinationFolderName, useFirstLineAsHeader, loopProgress);
		
		loopProgress.doProgress("Generate completed in " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
		
		Program.launch(indexHtmlFileName);
	}

	/** Generate filters */
	public static void generateFields(StaticCatalogPage page, String sourceCsvFileName, String fieldsFiltersFileName, String destinationFolderName,	
			boolean useFirstLineAsHeader, LoopProgress loopProgress) {

		/* Fields and filters time */
		long start = System.currentTimeMillis();
		loopProgress.doProgress("Start fields and filters generation...");
		
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
			newPageField.setSortAsc(configurationField.getIsSortAsc());
			newPageField.setSortDesc(configurationField.getIsSortDesc());

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
					L.e("Inconsistent filters with the file", new Exception());
				}

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

				StaticCatalogConfigurationField configurationField = fieldNames.get(csvFieldName);
				if (configurationField.getIsFilter()) {
					/* Defined filter */
					
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
				else {
					/* Not defined as filter */
					
				}
			}

			csvLineIndex++;
			if (csvLineIndex % 100000 == 0) {
				loopProgress.doProgress("Fields and filters " + U.w(csvLineIndex) + " lines examined...");
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
			
			pageField.setTotal_values_count(totalSize);
			
			if (totalSize > maxDisplayValues) {
				pageField.setTotal_more_values_count(totalSize - minDisplayValues);
			}
			else {
				pageField.setTotal_more_values_count(0);
			}
			
			String fieldType = pageField.getType(); 
			ArrayList<String> exceptionKeys = new ArrayList<>(exceptions.keySet());
			sortTypeKey("text", exceptionKeys);
			ArrayList<String> valueKeys = new ArrayList<>(values.keySet());
			sortTypeKey(fieldType, valueKeys);
			
			int keyIndex = 0;
			String fieldIdentifierPrefix = pageField.getIdentifier().replace("sc_field__", "sc_filter__");

			/* Exceptions */
			for (String exceptionKey : exceptionKeys) {
				
				StaticCatalogPageFieldValue filterValue = new StaticCatalogPageFieldValue();
				filterValue.setIndex(keyIndex);
				filterValue.setIsException(true);
				String valueIdentifier = fieldIdentifierPrefix + "__e_" + keyIndex + "__" + U.makeIdentifier(exceptionKey);
				keyIndex++;
				filterValue.setIdentifier(valueIdentifier);
				filterValue.setName(exceptionKey);
				
				if (transformValues != null) {
					filterValue.setLabel(transformValuesLabels.get(exceptionKey));
				}
				else {
					filterValue.setLabel(exceptionKey);
				}

				filterValue.setCount(exceptions.get(exceptionKey));
				pageField.getValues().add(filterValue);
			}

			/* Values */
			for (String valueKey : valueKeys) {

				StaticCatalogPageFieldValue filterValue = new StaticCatalogPageFieldValue();
				filterValue.setIndex(keyIndex);
				filterValue.setIsException(false);
				String valueIdentifier = fieldIdentifierPrefix + "__" + keyIndex + "__" + U.makeIdentifier(valueKey);
				keyIndex++;
				filterValue.setIdentifier(valueIdentifier);
				filterValue.setName(valueKey);

				if (transformFormat != null) {
					if (fieldType.equals(TYPE_DATE)) {
						DateTime dateTime = DateTime.parse(valueKey);
						filterValue.setLabel(dateTime.toString(transformFormat));
					}
					else {
						filterValue.setLabel(valueKey);
					}
				}
				else {
					if (transformValues != null) {
						filterValue.setLabel(transformValuesLabels.get(valueKey));
					}
					else {
						filterValue.setLabel(valueKey);
					}
				}
				
				filterValue.setCount(values.get(valueKey));
				pageField.getValues().add(filterValue);
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
		
		loopProgress.doProgress(U.w(csvLineIndex) + " lines, fields and filters generated in " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
	}

	/** Generate catalog */
	public static void generateCatalog(String sourceCsvFileName, StaticCatalogPage page, String destinationFolderName,
			boolean useFirstLineAsHeader, LoopProgress loopProgress) {

		/* Indexes and blocks generation time */ 
		long start = System.currentTimeMillis();
		loopProgress.doProgress("Start catalog generation...");
		
		/* Filters */
		String fsep = File.separator;
		String catalogBlocksFolderName = destinationFolderName + fsep + "_catalog" + fsep + "data";
		S.createFoldersIfNotExists(catalogBlocksFolderName);
		S.deleteFolderContentsOnly(catalogBlocksFolderName);
		String blockFilePrefix = catalogBlocksFolderName + fsep + "block-";
		
		String catalogIndexesFolderName = destinationFolderName + fsep + "_catalog" + fsep + "indexes";
		S.createFoldersIfNotExists(catalogIndexesFolderName);
		S.deleteFolderContentsOnly(catalogIndexesFolderName);
		String indexesValueFileNamePrefix = catalogIndexesFolderName + fsep + "static-catalog-index-value";

		String catalogSortFolderName = destinationFolderName + fsep + "_catalog" + fsep + "sort";
		S.createFoldersIfNotExists(catalogSortFolderName);
		S.deleteFolderContentsOnly(catalogSortFolderName);
		String sortFileNamePrefix = catalogSortFolderName + fsep + "static-catalog-sort";

		StaticCatalogIndexes templateCatalogRoot = new StaticCatalogIndexes();
		LinkedHashMap<String, LinkedHashMap<String, ArrayList<Long>>> nameValuesLines = templateCatalogRoot.getValueIndexes().getNameValuesLines();
		
		/* Filters */
		ArrayList<StaticCatalogPageField> pageFields = page.getFields();
		
		int lineLength = pageFields.size();
		ArrayList<Integer> filterIndexes = new ArrayList<>();
		ArrayList<String> fieldNames = new ArrayList<>();

		for (int index = 0; index < lineLength; index++) {
			StaticCatalogPageField pageField = pageFields.get(index);
			fieldNames.add(pageField.getName());
			if (pageField.getFilter()) {
				/* Field is index value */
				filterIndexes.add(index);

				/* All filter values */
				LinkedHashMap<String, ArrayList<Long>> filterValues = new LinkedHashMap<>(); 
				for (StaticCatalogPageFieldValue value : pageField.getValues()) {
					filterValues.put(value.getName(), new ArrayList<>());
				}
				nameValuesLines.put(pageField.getName(), filterValues);
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
		
//		String[] headerLine = null;
		String[] csvLine = csvParser.parseNext();
		while (csvLine != null) {

//			/* CSV field names */
//			if ((csvLineIndex == 0) && (fieldNames.size() == 0)) {
//				if (useFirstLineAsHeader) {
//					for (int index = 0; index < lineLength; index++) {
//						fieldNames.add(csvLine[index]);
//					}
//					csvLine = csvParser.parseNext();
//					csvLineIndex++;
//					continue;
//				}
//				else {
//					for (int index = 0; index < lineLength; index++) {
//						fieldNames.add("Field " + (index + 1));
//					}
//				}
//			}

			/* CSV field names */
			if ((csvLineIndex == 0) && (useFirstLineAsHeader)) {
				
//				headerLine = csvLine;
				// TODO
				csvLine = csvParser.parseNext();
				csvLineIndex++;
				continue;
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
					csvWriter = new CsvWriter(new FileWriter(blockFileName), new CsvWriterSettings());
				} catch (IOException ioException) {
					L.e("Cataloog write block " + blockIndex + " error = " + blockFileName, ioException);
				}
				//csvWriter.writeRow(headerLine);
			}

			csvWriter.writeRow(csvLine);
			blockLineIndex++;
			
			/* Indexes */
			for (int index : filterIndexes) {
				
				StaticCatalogPageField pageField = pageFields.get(index);
				String fieldValue = csvLine[pageField.getCsvIndex() - 1];
				if (fieldValue == null) {
					fieldValue = "NULL";
				}
				String fieldName = pageField.getName();
				LinkedHashMap<String, ArrayList<Long>> valueLines = nameValuesLines.get(fieldName);
				ArrayList<Long> lines = valueLines.get(fieldValue);
				
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
			
			csvLineIndex++;
			if (csvLineIndex % 100000 == 0) {
				loopProgress.doProgress("Catalog generation " + U.w(csvLineIndex) + " lines examined...");
			}
			csvLine = csvParser.parseNext();
		}
		csvParser.stopParsing();
		if (csvWriter != null) {
			csvWriter.close();
		}
		
		loopProgress.doProgress("Catalog blocks generated in " + ((System.currentTimeMillis() - start) / 1000) + " seconds, now generate the indexes...");

		/* INDEX_SPLIT_TYPE_VALUES */
		//int indexNameCnt = 0;
		for (String fieldName : nameValuesLines.keySet()) {
			LinkedHashMap<String, ArrayList<Long>> valueLines = nameValuesLines.get(fieldName);
			int indexNameCnt = fieldNames.indexOf(fieldName);
			int indexValueCnt = 0;
			for (ArrayList<Long> lines : valueLines.values()) {
				S.saveObjectToJsonFileName(lines, indexesValueFileNamePrefix + "-" + indexNameCnt + "-" + indexValueCnt + ".json");
				indexValueCnt++;
			}
//			indexNameCnt++;
		}

		loopProgress.doProgress("Catalog indexes generated in " + ((System.currentTimeMillis() - start) / 1000) + " seconds, now generate the sort...");

		ArrayList<Long> sortedLines = new ArrayList<>();
		for (ArrayList<Long> lines : nameValuesLines.get("State").values()) {
			sortedLines.addAll(lines);
		}
		S.saveObjectToJsonFileName(sortedLines, sortFileNamePrefix + "-0" + ".json");		
		
		
//		nameValuesLines.get(key)
		
//		contents.setIndexSplitType(INDEX_SPLIT_TYPE_NAMES);
		
//		String indexSplitType = contents.getIndexSplitType();
//		if (indexSplitType.equals(INDEX_SPLIT_TYPE_VALUES)) {
//			int indexNameCnt = 0;
//			for (LinkedHashMap<String, ArrayList<Long>> valueLines : nameValuesLines.values()) {
//				int indexValueCnt = 0;
//				for (ArrayList<Long> lines : valueLines.values()) {
//					S.saveObjectToJsonFileName(lines, indexesValueFileNamePrefix + "-" + indexNameCnt + "-" + indexValueCnt + ".json");
//					indexValueCnt++;
//				}
//				indexNameCnt++;
//			}
//		}
//		else if (indexSplitType.equals(INDEX_SPLIT_TYPE_NAMES)) {
//			int indexNameCnt = 0;
//			for (LinkedHashMap<String, ArrayList<Long>> valueLines : nameValuesLines.values()) {
//				S.saveObjectToJsonFileName(valueLines, indexesValueFileNamePrefix + "-" + indexNameCnt + ".json");
//				indexNameCnt++;
//			}
//		}
//		else {
//			S.saveObjectToJsonFileName(nameValuesLines, indexesValueFileNamePrefix + ".json");
//		}
 
		loopProgress.doProgress(U.w(csvLineIndex) + " lines catalog generated in " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
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
