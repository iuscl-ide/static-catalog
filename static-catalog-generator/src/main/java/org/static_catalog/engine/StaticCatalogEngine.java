/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Display;
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
			
			int lineLength = csvLine.length;
			
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
				loopProgress.doProgress(csvLineIndex);
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
	

}
