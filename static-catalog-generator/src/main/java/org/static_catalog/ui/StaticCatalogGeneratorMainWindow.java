/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.static_catalog.main.S;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

/** Generator main window */
public class StaticCatalogGeneratorMainWindow {

	/** File control */
	private interface FileControl {
		
		public String getCompleteFileName();
	}
	
	/** debug */
	private boolean isDebug = false;

	/** Main display */
	private Display display;

	/** Main Window instance */
	private static StaticCatalogGeneratorMainWindow mainWindowInstance;

	/* Fonts */
	private Font fontNormal;
	private Font fontBold;
	private Font fontBigger;

	/** Main application loop in the window */
	public void runMainWindow() {

		mainWindowInstance = this;

//		// DEBUG
//		DeviceData data = new DeviceData();
//	    data.tracking = true;
//	    display = new Display(data);
//	    Sleak sleak = new Sleak();
//	    sleak.open();
//	    // /DEBUG
	    
//		int iiii = 3 / (1 - 1);
//		URL url = Device.class.getResource(Device.class.getSimpleName() + ".class");
//	    L.p(url);
//	    try {
//	    	L.p(new File(url.toURI()));	
//		} catch (Exception e) {
//			
//		}
		
		/** margin, padding */
		int sep = 8;

		/* Display */
		Display.setAppName("static-catalog");
		display = new Display();

		/* Main window */
		Shell mainShell = new Shell(display);
		mainShell.setText("static-catalog Generator");
	    mainShell.setLayout(createMarginsVerticalSpacingGridLayout(sep, sep));

		/* Icon */
		Image[] iconImages = new Image[9];
		String[] rez = { "16", "24", "32", "48", "64", "96", "128", "256", "512" };
		for (int index = 0; index < 9; index++) {
			String rezimg = rez[index];
			iconImages[index] = getResourceAsImage("org/static_catalog/res/icon/" + rezimg + "x" + rezimg + ".png");
		}
		mainShell.setImages(iconImages);
		
		/* Location */
		mainShell.setLocation(250, 0);
		mainShell.setSize(display.getClientArea().width - 600, display.getClientArea().height);

		/* Fonts */
		fontNormal = mainShell.getFont();
		fontBold = newFontAttributes(fontNormal, SWT.BOLD);
		fontBigger = newFontSize(fontBold, 14);

		
		/* Menu */
	    Menu mainMenuBar = new Menu(mainShell, SWT.BAR);
	    
	    MenuItem fileMenuHeader = new MenuItem(mainMenuBar, SWT.CASCADE);
	    fileMenuHeader.setText("File");
	    
	    Menu fileMenu = new Menu(mainShell, SWT.DROP_DOWN);
	    fileMenuHeader.setMenu(fileMenu);
	    
	    MenuItem openMenuItem = new MenuItem(fileMenu, SWT.CASCADE);
	    openMenuItem.setText("Open");

	    mainShell.setMenuBar(mainMenuBar);

	    /* Tabs */
	    //isDebug = true;
	    
	    ArrayList<Composite> mainComposites = createMainTabs(mainShell);

	    /* View CSV */
		createViewCsvTab(mainComposites.get(0));
	    /* Analyze CSV */
		createAnalyzeCsvTab(mainComposites.get(1));
		
		
		/* Run */
		mainShell.open();
		
		/* Application loop */
		while (!mainShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		/* Terminate */
		display.dispose();
	}
	
	private void mainSelection(int selectedButtonIndex, ArrayList<Button> topButtons, int[] activeButtonIndex, ArrayList<Composite> mainComposites) {

		topButtons.get(selectedButtonIndex).setSelection(true);
		
		if (selectedButtonIndex == activeButtonIndex[0]) {
			return;
		}
		activeButtonIndex[0] = selectedButtonIndex;
		
		for (int index = 0; index < topButtons.size(); index++) {
			if (index != selectedButtonIndex) {
				topButtons.get(index).setSelection(false);
				Composite mainComposite = mainComposites.get(index);
				mainComposite.setVisible(false);
				((GridData) mainComposite.getLayoutData()).exclude = true;
			}
		}
		
		Composite activeComposite = mainComposites.get(selectedButtonIndex);
		activeComposite.setVisible(true);
		((GridData) activeComposite.getLayoutData()).exclude = false;
		activeComposite.requestLayout();
	}
	
	/** Create file control */
	private FileControl addFileControl(Composite parentComposite, String fileControlName) {
		
		/*
		 * TODO 
		 * 1/ The Browse button
		 * 2/ The text to be a combo with recent files
		 * 
		 */

		int sep = 8;
		
		final Composite fileComposite = new Composite(parentComposite, SWT.NONE);
		addDebug(fileComposite);
	    fileComposite.setLayoutData(createFillHorizontalGridData());
		fileComposite.setLayout(createColumnsSpacingGridLayout(3, sep));
		
		final Label fileLabel = new Label(fileComposite, SWT.NONE);
		fileLabel.setText("File");
		fileLabel.setLayoutData(createWidthGridData(120));
		
		final Text fileText = new Text(fileComposite, SWT.SINGLE | SWT.BORDER);
		fileText.setText("C:\\Iustin\\Programming\\_static-catalog\\tools\\datas\\big.csv");
		fileText.setLayoutData(createFillHorizontalGridData());
		
		final Button fileButton = new Button(fileComposite, SWT.NONE);
		fileButton.setText("Browse");
		fileButton.setLayoutData(createWidthGridData(120));
		
		return new FileControl() {
			@Override
			public String getCompleteFileName() {

				return fileText.getText();
			}
		};
	}

	/** Main tabs */
	private ArrayList<Composite> createMainTabs(Shell mainShell) {
		
	    /* Layout */
	    int sep = 8;
	    
	    GridData gridData;
	    GridLayout gridLayout;
	    
	    Composite topComposite = new Composite(mainShell, SWT.NONE);
	    addDebug(topComposite);
	    topComposite.setLayoutData(createFillHorizontalGridData());
	    topComposite.setLayout(createGridLayout());

	    Composite topButtonsComposite = new Composite(topComposite, SWT.NONE);
	    addDebug(topButtonsComposite);
	    gridData = createGridData();
	    gridData.horizontalAlignment = SWT.CENTER;
	    gridData.grabExcessHorizontalSpace = true;
	    topButtonsComposite.setLayoutData(gridData);
	    topButtonsComposite.setLayout(createColumnsSpacingGridLayout(3, sep));
	    
	    String[] topButtonTexts = { "View CSV", "Analyse CSV", "Generate" };
	    
	    ArrayList<Button> topButtons = new ArrayList<>(); 

	    for (String topButtonText : topButtonTexts) {

		    Button button = new Button(topButtonsComposite, SWT.TOGGLE);
		    button.setText(topButtonText);
		    button.setLayoutData(createWidthGridData(120));
		    
		    topButtons.add(button);
	    }
	    
	    Label separator = new Label(mainShell, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(createFillHorizontalGridData());
	    
	    ArrayList<Composite> mainComposites = new ArrayList<>(); 
	    
	    for (String topButtonText : topButtonTexts) {

	    	Composite composite = new Composite(mainShell, SWT.NONE);
		    addDebug(composite);
		    composite.setLayoutData(createFillBothGridData());
		    composite.setLayout(createVerticalSpacingGridLayout(sep));
	    	
		    mainComposites.add(composite);
		    
		    Label label = new Label(composite, SWT.NONE);
		    label.setText(topButtonText);
		    label.setFont(fontBigger);
		    label.setLayoutData(createFillHorizontalGridData());
	    }

	    final int[] activeButtonIndex = new int[1];
	    
	    SelectionAdapter topButtonSelectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				int selectedButtonIndex = topButtons.indexOf(selectionEvent.widget);
				mainSelection(selectedButtonIndex, topButtons, activeButtonIndex, mainComposites);
			}
		};
	    
		for (Button button : topButtons) {
			button.addSelectionListener(topButtonSelectionAdapter);
		}

		activeButtonIndex[0] = -1;
		mainSelection(0, topButtons, activeButtonIndex, mainComposites);
		
		return mainComposites;
	}
	
	/** View a CSV file in a grid */
	private void createViewCsvTab(Composite viewCsvTabComposite) {
		
		/*
		 * TODO 
		 * 1/ Load in memory only string lines and parse on display
		 * 2/ Activate the Stop button when no memory
		 * 
		 */
		
	    /* Layout */
	    int sep = 8;
	    
	    GridData gridData;
	    GridLayout gridLayout;
		
	    
	    final FileControl viewCsvFileControl = addFileControl(viewCsvTabComposite, "view_csv");
	    
		
		final Composite csvButtonsComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		addDebug(csvButtonsComposite);
		csvButtonsComposite.setLayoutData(createFillHorizontalGridData());
		csvButtonsComposite.setLayout(createColumnsSpacingGridLayout(5, sep));
		
		final Button csvLoadButton = new Button(csvButtonsComposite, SWT.NONE);
		csvLoadButton.setText("Load");
		csvLoadButton.setLayoutData(createWidthGridData(120));
		
		final Text csvLoadLinesText = new Text(csvButtonsComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		csvLoadLinesText.setText("2000000");
		csvLoadLinesText.setLayoutData(createWidthGridData(120));

		final Label csvMaxLinesLabel = new Label(csvButtonsComposite, SWT.NONE);
		csvMaxLinesLabel.setLayoutData(createWidthGridData(120));
		csvMaxLinesLabel.setText("max lines");

		final Button csvStopLoadButton = new Button(csvButtonsComposite, SWT.NONE);
		csvStopLoadButton.setText("Stop");
		csvStopLoadButton.setEnabled(false);
		csvStopLoadButton.setLayoutData(createWidthGridData(120));

		
//		final Button csvExtractButton = new Button(csvButtonsComposite, SWT.NONE);
//		csvExtractButton.setText("Extract");
//		csvExtractButton.setLayoutData(createWidthGridData(120));

		
		final Composite csvStatusComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		//csvStatusComposite.setBackground(colorWhite);
		csvStatusComposite.setLayoutData(createFillHorizontalGridData());
		csvStatusComposite.setLayout(createColumnsGridLayout(2));

		final Label csvStatusLabel = new Label(csvStatusComposite, SWT.NONE);
		csvStatusLabel.setBackground(new Color(display, 255, 255, 255));
		csvStatusLabel.setLayoutData(createFillHorizontalGridData());
		csvStatusLabel.setText("Status");

		
		final Grid csvFileGrid = new Grid(viewCsvTabComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		csvFileGrid.setLayoutData(createFillBothGridData());
		csvFileGrid.setHeaderVisible(true);
		//csvFileGrid.setAutoHeight(true);
		csvFileGrid.setLinesVisible(true);
		
		/* Events */
		final ArrayList<String[]> csvFileGridLines = new ArrayList<String[]>();
		
		csvFileGrid.addListener(SWT.SetData, new Listener() {
			@Override
			public void handleEvent(Event setDataEvent) {

				GridItem gridItem = (GridItem) setDataEvent.item;
				int index = setDataEvent.index;
				String[] line = csvFileGridLines.get(index);
				gridItem.setText(0, index + 1 + "");	
				for (int lineIndex = 0; lineIndex < line.length; lineIndex++) {
					gridItem.setText(lineIndex + 1, line[lineIndex] + "");	
				}
//				for (GridColumn gridColumn : csvFileGrid.getColumns()) {
//					gridColumn.pack();
//					gridColumn.setWidth(gridColumn.getWidth() + 24);
//				}
			}
		});
		
		csvLoadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				//L.p(viewCsvFileControl.getCompleteFileName());
				csvFileGrid.clearItems();
				csvFileGrid.disposeAllItems();
				
				while (csvFileGrid.getColumnCount() > 0) {
					csvFileGrid.getColumns()[0].dispose();
				}
				csvFileGridLines.clear();
				
//				csvFileGrid.setRedraw(false);
				
				long start = System.currentTimeMillis();
				
				long maxLines = Long.parseLong(csvLoadLinesText.getText());
				CsvParserSettings csvParserSettings = new CsvParserSettings();
				csvParserSettings.setLineSeparatorDetectionEnabled(true);
				CsvParser csvParser = new CsvParser(csvParserSettings);
				csvParser.beginParsing(new File(viewCsvFileControl.getCompleteFileName()));
				
//				csvStopLoadButton.setEnabled(true);
				
				String[] csvLine = csvParser.parseNext();
				long csvLineIndex = 0;
				while (csvLine != null) {
					
					int lineLength = csvLine.length;
					
					if (csvLineIndex == 0) {
						GridColumn fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
					    fieldGridColumn.setWidth(50);
					    fieldGridColumn.setText("Index");
					    fieldGridColumn.setAlignment(SWT.RIGHT);
						for (int index = 0; index < lineLength; index++) {
							fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
						    fieldGridColumn.setWordWrap(true);
						    //fieldGridColumn.setWidth(csvLine[index].length() * 10);
						    fieldGridColumn.setText(csvLine[index]);
						    //fieldGridColumn.pack();
						}
						for (GridColumn gridColumn : csvFileGrid.getColumns()) {
							gridColumn.pack();
							gridColumn.setWidth(gridColumn.getWidth() + 24);
						}
					}
					else {
						csvFileGridLines.add(csvLine);
//						GridItem csvGridItem = new GridItem(csvFileGrid, SWT.NONE);
//						csvGridItem.setText(0, csvLineIndex + "");
//						for (int index = 0; index < lineLength; index++) {
//							csvGridItem.setText(index + 1, csvLine[index] + "");
//						}
					}

					csvLineIndex++;
					if (csvLineIndex % 10000 == 0) {
						csvStatusLabel.setText(csvLineIndex + " lines loaded...");
					}

					if (csvLineIndex == maxLines) {
//						csvStopLoadButton.setEnabled(false);
//						for (GridColumn gridColumn : csvFileGrid.getColumns()) {
//							gridColumn.pack();
//							gridColumn.setWidth(gridColumn.getWidth() + 24);
//						}
						csvFileGrid.setItemCount((int) (csvLineIndex - 1));
						csvStatusLabel.setText("Max " + (csvLineIndex - 1) + " lines done load in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
						return;
					}

					csvLine = csvParser.parseNext();
				}

//				csvStopLoadButton.setEnabled(false);
//				for (GridColumn gridColumn : csvFileGrid.getColumns()) {
//					gridColumn.pack();
//					gridColumn.setWidth(gridColumn.getWidth() + 24);
//				}
				csvFileGrid.setItemCount((int) (csvLineIndex - 1));
//				csvFileGrid.setRedraw(true);
				csvStatusLabel.setText("All " + (csvLineIndex - 1) + " lines done load in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
			}
		});
	}

	/** Analyze a CSV file for generation rules */
	private void createAnalyzeCsvTab(Composite parentComposite) {
		
		/*
		 * TODO 
		 * 
		 */
		
	    /* Layout */
	    int sep = 8;
	    
	    GridData gridData;
	    GridLayout gridLayout;
		
	    
	    final FileControl analyzeCsvFileControl = addFileControl(parentComposite, "analyze_csv");
	    
		
		final Composite csvButtonsComposite = new Composite(parentComposite, SWT.NONE);
		addDebug(csvButtonsComposite);
		csvButtonsComposite.setLayoutData(createFillHorizontalGridData());
		csvButtonsComposite.setLayout(createColumnsSpacingGridLayout(5, sep));
		
		final Button csvAnalyzeButton = new Button(csvButtonsComposite, SWT.NONE);
		csvAnalyzeButton.setText("Analyze");
		csvAnalyzeButton.setLayoutData(createWidthGridData(120));
		
//		final Text csvLoadLinesText = new Text(csvButtonsComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
//		csvLoadLinesText.setText("2000000");
//		csvLoadLinesText.setLayoutData(createWidthGridData(120));
//
//		final Label csvMaxLinesLabel = new Label(csvButtonsComposite, SWT.NONE);
//		csvMaxLinesLabel.setLayoutData(createWidthGridData(120));
//		csvMaxLinesLabel.setText("max lines");
//
//		final Button csvStopLoadButton = new Button(csvButtonsComposite, SWT.NONE);
//		csvStopLoadButton.setText("Stop");
//		csvStopLoadButton.setEnabled(false);
//		csvStopLoadButton.setLayoutData(createWidthGridData(120));

		
//		final Button csvExtractButton = new Button(csvButtonsComposite, SWT.NONE);
//		csvExtractButton.setText("Extract");
//		csvExtractButton.setLayoutData(createWidthGridData(120));

		
		final Composite csvStatusComposite = new Composite(parentComposite, SWT.NONE);
		//csvStatusComposite.setBackground(colorWhite);
		csvStatusComposite.setLayoutData(createFillHorizontalGridData());
		csvStatusComposite.setLayout(createColumnsGridLayout(2));

		final Label csvStatusLabel = new Label(csvStatusComposite, SWT.NONE);
		csvStatusLabel.setBackground(new Color(display, 255, 255, 255));
		csvStatusLabel.setLayoutData(createFillHorizontalGridData());
		csvStatusLabel.setText("Status");

		
		final Grid csvAnalyzeGrid = new Grid(parentComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		csvAnalyzeGrid.setLayoutData(createFillBothGridData());
		csvAnalyzeGrid.setHeaderVisible(true);
		csvAnalyzeGrid.setLinesVisible(true);
		
		/* Events */
		final ArrayList<String[]> csvAnalyzeGridLines = new ArrayList<String[]>();
		
		csvAnalyzeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				//L.p(viewCsvFileControl.getCompleteFileName());
				csvAnalyzeGrid.clearItems();
				csvAnalyzeGrid.disposeAllItems();
				
				while (csvAnalyzeGrid.getColumnCount() > 0) {
					csvAnalyzeGrid.getColumns()[0].dispose();
				}
				csvAnalyzeGridLines.clear();
				
//				csvFileGrid.setRedraw(false);
				
				long start = System.currentTimeMillis();
				
				ArrayList<HashMap<String, Long>> fields = new ArrayList<HashMap<String,Long>>();
				ArrayList<String> fieldNames = new ArrayList<>();
				
				GridColumn fieldGridColumn = new GridColumn(csvAnalyzeGrid, SWT.NONE);
			    fieldGridColumn.setWordWrap(true);
			    fieldGridColumn.setWidth(250);

			    fieldGridColumn = new GridColumn(csvAnalyzeGrid, SWT.NONE);
			    fieldGridColumn.setWordWrap(true);
			    fieldGridColumn.setWidth(150);

			    fieldGridColumn = new GridColumn(csvAnalyzeGrid, SWT.NONE);
			    fieldGridColumn.setWordWrap(true);
			    fieldGridColumn.setWidth(300);

			    fieldGridColumn = new GridColumn(csvAnalyzeGrid, SWT.NONE);
			    fieldGridColumn.setWordWrap(true);
			    fieldGridColumn.setWidth(300);

				CsvParserSettings csvParserSettings = new CsvParserSettings();
				csvParserSettings.setLineSeparatorDetectionEnabled(true);
				CsvParser csvParser = new CsvParser(csvParserSettings);
				csvParser.beginParsing(new File(analyzeCsvFileControl.getCompleteFileName()));
				
				String[] csvLine = csvParser.parseNext();
				long csvLineIndex = 0;
				while (csvLine != null) {
					
					int lineLength = csvLine.length;
					
					csvLineIndex++;
					if (csvLineIndex % 500000 == 0) {
						//L.p("csvLineIndex = " + csvLineIndex);
						csvStatusLabel.setText(csvLineIndex + " lines...");
					}
					
					if (csvLineIndex == 1) {
						for (int index = 0; index < lineLength; index++) {
							fields.add(index, new HashMap<String, Long>());
							fieldNames.add(index, csvLine[index]);
						}
					}
					else {
						for (int index = 0; index < lineLength; index++) {
							long cnt = 0;
							if (fields.get(index).containsKey(csvLine[index])) {
								cnt = fields.get(index).get(csvLine[index]);
							}
							cnt++;
							fields.get(index).put(csvLine[index], cnt);
						}
					}

					csvLine = csvParser.parseNext();
					
					if (csvLine == null) {
						csvStatusLabel.setText("Group " + csvLineIndex + " lines done in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
						
						for (int index = 0; index < lineLength; index++) {
							GridItem csvGridItem = new GridItem(csvAnalyzeGrid, SWT.NONE);
							csvGridItem.setText(0, fieldNames.get(index));
							int diff = fields.get(index).keySet().size(); 
							csvGridItem.setText(1, diff + "");
							
							if (diff < 1000) {
								String distrib = "";
								String sep = "";
								for (long lo : fields.get(index).values()) {
									distrib = distrib + sep + lo;
									sep = " - ";
								}
								csvGridItem.setText(2, distrib);
								
								String distribNames = "";
								sep = "";
								for (String key : fields.get(index).keySet()) {
									distribNames = distribNames + sep + key;
									sep = " - ";
								}
								csvGridItem.setText(3, distribNames);
							}
						}

						return;
					}

					//csvLine = csvParser.parseNext();
				}
			}
		});
		
		
	}

	/** Load image resource */
	public Image getResourceAsImage(String imageResourceName) {
		
		return new Image(display, S.getResourceAsInputStream(imageResourceName));
	}

	/** GridData */
	private GridData createGridData() {
		
		/*
		exclude	false	
		grabExcessHorizontalSpace	false	
		grabExcessVerticalSpace	false	
		heightHint	-1	
		horizontalAlignment	1	
		horizontalIndent	0	
		horizontalSpan	1	
		minimumHeight	0	
		minimumWidth	0	
		verticalAlignment	2	
		verticalIndent	0	
		verticalSpan	1	
		widthHint	-1	
		*/

		GridData gridData = new GridData();
		/* TODO ? */
		return gridData;
	}

	/** GridData fill horizontal */
	private GridData createFillHorizontalGridData() {
		
		GridData gridData = createGridData();
	    gridData.horizontalAlignment = SWT.FILL;
	    gridData.grabExcessHorizontalSpace = true;

	    return gridData;
	}

	/** GridData fill horizontal */
	private GridData createFillBothGridData() {
		
		GridData gridData = createGridData();
	    gridData.horizontalAlignment = SWT.FILL;
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.verticalAlignment = SWT.FILL;
	    gridData.grabExcessVerticalSpace = true;
	    
	    return gridData;
	}
	
	/** GridData width */
	private GridData createWidthGridData(int width) {
		
		GridData gridData = createGridData();
	    gridData.widthHint = width;

	    return gridData;
	}
	
	/** GridLayout */
	private GridLayout createGridLayout() {
		
		/*
		horizontalSpacing	5	
		makeColumnsEqualWidth	false	
		marginBottom	0	
		marginHeight	5	
		marginLeft	0	
		marginRight	0	
		marginTop	0	
		marginWidth	5	
		numColumns	1	
		verticalSpacing	5	
		*/

		GridLayout gridLayout = new GridLayout();
		
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		
		return gridLayout;
	}

	/** GridLayout */
	private GridLayout createColumnsGridLayout(int numColumns) {
		
		GridLayout gridLayout = createGridLayout();
		gridLayout.numColumns = numColumns;
		
		return gridLayout;
	}

	/** GridLayout */
	private GridLayout createColumnsSpacingGridLayout(int numColumns, int horizontalSpacing) {
		
		GridLayout gridLayout = createGridLayout();
		gridLayout.numColumns = numColumns;
		gridLayout.horizontalSpacing = horizontalSpacing;
		
		return gridLayout;
	}

	/** GridLayout */
	private GridLayout createMarginsGridLayout(int margin) {
		
		GridLayout gridLayout = createGridLayout();
	    gridLayout.marginTop = margin;
	    gridLayout.marginBottom = margin;
	    gridLayout.marginLeft = margin;
	    gridLayout.marginRight = margin;
		
		return gridLayout;
	}

	/** GridLayout */
	private GridLayout createVerticalSpacingGridLayout(int verticalSpacing) {
		
		GridLayout gridLayout = createGridLayout();
		gridLayout.verticalSpacing = verticalSpacing;
		
		return gridLayout;
	}

	
	/** GridLayout */
	private GridLayout createMarginsVerticalSpacingGridLayout(int margin, int verticalSpacing) {
		
		GridLayout gridLayout = createMarginsGridLayout(margin);
		gridLayout.verticalSpacing = verticalSpacing;
		
		return gridLayout;
	}

	/** Random color component */
	private int random255() {
		
		double d = Math.random() * 255d;
		return (int) d;
	}

	/** Random color */
	public Color randomColor() {
		
		return new Color(Display.getDefault(), random255(), random255(), random255());
	}

	/** New font attributes */
	private Font newFontAttributes(Font font, int attr) {
		
		FontData fontData = font.getFontData()[0];
		fontData = new FontData(fontData.getName(), fontData.getHeight(), attr);
		//fontData.data.lfUnderline = 1;
		
		return new Font(display, fontData);
	}

	/** New font size */
	private Font newFontSize(Font font, int height) {
		
		FontData fontData = font.getFontData()[0];
		fontData = new FontData(fontData.getName(), height, fontData.getStyle());
		
		return new Font(display, fontData);
	}

	/** Debug background */
	private void addDebug(Composite composite) {
		
		if (isDebug) {
			composite.setBackground(randomColor());
		}
	}
}
