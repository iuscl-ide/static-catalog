/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.pojava.datetime.DateTime;
import org.static_catalog.ui.StaticCatalogGeneratorMainWindow.LoopProgress;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

/** Generator engine */
public class StaticCatalogEngine {

	/** Load CSV in grid */
	public static void loadViewCsv(String csvCompleteFileName, long maxLines,
			ArrayList<String[]> csvFileGridLines, ArrayList<String> csvFileGridHeader,
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
				return;
			}
			
			//int lineLength = csvLine.length;
			
			if (csvLineIndex == 0) {
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
				return;
			}

			csvLine = csvParser.parseNext();
		}
	}
	
	/** Load analyze CSV */
	public static void loadAnalyzeCsv(String csvCompleteFileName, long maxUniqueValues,
			ArrayList<HashMap<String, Long>> fields, ArrayList<String> fieldNames,
			ArrayList<String> fieldTypes, ArrayList<HashMap<String, ArrayList<String>>> fieldTypesExceptionValues,
			int maxExceptions,
			AtomicBoolean doLoop, LoopProgress loopProgress) {

		long start = System.currentTimeMillis();
		
		loopProgress.doProgress("Start lines analyze...");
		
		CsvParserSettings csvParserSettings = new CsvParserSettings();
		csvParserSettings.setLineSeparatorDetectionEnabled(true);
		CsvParser csvParser = new CsvParser(csvParserSettings);
		csvParser.beginParsing(new File(csvCompleteFileName));
		
		String[] csvLine = csvParser.parseNext();
		int lineLength = 0;
		long csvLineIndex = 0;
		while (csvLine != null) {
			
			if (csvLineIndex == 0) {
				lineLength = csvLine.length;
				for (int index = 0; index < lineLength; index++) {
					fields.add(index, new HashMap<String, Long>());
					fieldNames.add(index, csvLine[index]);
				}
			}
			else {
				for (int index = 0; index < lineLength; index++) {
//					if (fields.get(index).size() < 500) {
						long cnt = 0;
						HashMap<String, Long> fieldsIndex = fields.get(index);
						if (fieldsIndex.containsKey(csvLine[index])) {
							cnt = fieldsIndex.get(csvLine[index]);
						}
//						if (cnt < 500) {
							cnt++;
							fieldsIndex.put(csvLine[index] + "", cnt);
//						}
//					}
				}
			}

			csvLineIndex++;
			if (csvLineIndex % 500000 == 0) {
				loopProgress.doProgress(csvLineIndex + " lines analyzed...");
			}

			csvLine = csvParser.parseNext();
		}
		
		loopProgress.doProgress(csvLineIndex + " lines done analyzing, try to find the types...");
		
		String[] possibleTypes = { "long", "double", "date" };
		for (int index = 0; index < lineLength; index++) {
			
			ArrayList<String> searchTypes = new ArrayList<>(Arrays.asList(possibleTypes));
			HashMap<String, ArrayList<String>> typesExceptionValues = new HashMap<>();
			fieldTypesExceptionValues.add(typesExceptionValues);
			for (String possibleType: possibleTypes) {
				typesExceptionValues.put(possibleType, new ArrayList<>());
			}
			
			HashMap<String, Long> fieldsIndex = fields.get(index);
			int size = fieldsIndex.size();
			for (String key : fieldsIndex.keySet()) {

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
				fieldTypes.add("long");
			}
			else if ((searchTypes.contains("double")) && (size > typesExceptionValues.get("double").size())) {
				fieldTypes.add("double");
			}
			else if ((searchTypes.contains("date")) && (size > typesExceptionValues.get("date").size())) {
				fieldTypes.add("date");
			}
			else {
				fieldTypes.add("text");
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
	
}
