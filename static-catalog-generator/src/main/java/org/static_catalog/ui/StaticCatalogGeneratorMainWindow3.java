/* EHA Viewer Desktop Application - Copyright 2016 ehaviewer.com */
package org.static_catalog.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.static_catalog.main.S;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

/** Viewer main window */
public class StaticCatalogGeneratorMainWindow3 {

	/** Main display */
	private Display display;

	/** Main Window instance */
	private static StaticCatalogGeneratorMainWindow3 mainWindowInstance;

	/** The background shell */
	private Shell mainShell;

	/* Colors */
	private Color colorWhite;
	private Color colorBlue;
	private Color colorGray;
	private Color colorGrayHighlight;
	private Color colorGrayShadow;
	private Color colorGrayShadower;

	/* Cursor */
	//private Cursor cursorArrow;
	private Cursor cursorHand;
	private Cursor cursorWait;

	/* Fonts */
	private Font fontNormal;
	private Font fontBold;

	/* Images */
	private final String[] uiImagesNames = new String[] { "container_obj", "container_topic", "toc_closed", "toc_open", "topic",
			"e_contents_view", "e_index_view", "e_search_results_view", "e_bookmarks_view",
			"e_back", "e_forward", "e_home", "e_synch_toc_nav", "e_add_bkmrk", "e_print_topic", "maximize", "e_restore",
			"e_print_toc", "e_quick_search_multi", "e_collapseall", "e_auto_synch_toc",
			"e_show_categories", "e_show_descriptions",
			"e_bookmark_rem", "e_bookmark_remall" };
	
	private final HashMap<String, Image> uiImages = new HashMap<>();

	private String osNameProperty;
	private final String htmlFont = getHtmlFont();
	
	/* Plug-ins */
//	private final ArrayList<XMLPlugin> plugins = new ArrayList<>();
	
	private String activeBaseUrl;
	private final HashMap<String, TreeItem> baseUrlSyncs = new HashMap<>();
	private Boolean alwaysSyncContents = false;
	
	private Boolean groupSearchByCategories = true;
	private Boolean showSearchDescriptions = true;
	
	private final ArrayList<String> baseUrlHistory = new ArrayList<>();
	private Integer baseUrlHistoryIndex = -1;
	
//	private final ArrayList<UIIndexItem> uiIndexItems = new ArrayList<>();

//	private final ArrayList<UIBookmarkItem> uiBookmarkItems = new ArrayList<>();

	private String containerHtml;
	private String containerNavItem;
	private String blockedHtml;
	
	
	private String lastIndexDisplayWord;
	private String lastSearchPhrase;
	
	/* Components */
	private Composite activeTabComposite;
	private Composite activeComposite;
	private ToolBar activeTabToolbar;
	private Label tabNameLabel;
	
	private Composite searchResultsTabComposite;
	private Composite searchResultsComposite;
	private ToolBar searchResultsToolbar;
	
//	private SashFormMin middleSashForm;
	private Tree contentsTree;

	private Grid indexGrid;

	private Grid searchResultsGrid;
	private Label bottomLeftLabel;
	private Browser browser;

	private Grid bookmarksGrid;

	private Composite viewCsvTabComposite;
	
	
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
		
		Display.setAppName("static-catalog");
		display = new Display();
		

		/* Main window */
		mainShell = new Shell(display);
		mainShell.setText("static-catalog Generator");
		
		
		colorWhite = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		colorBlue = display.getSystemColor(SWT.COLOR_BLUE);
		colorGray = mainShell.getBackground();
//		colorGrayHighlight = getHighlightColor(colorGray);
//		colorGrayShadow = getShadowColor(colorGray);
//		colorGrayShadower = getShadowColor(getShadowColor(colorGrayShadow));
		
		cursorHand = display.getSystemCursor(SWT.CURSOR_HAND);
		cursorWait = display.getSystemCursor(SWT.CURSOR_WAIT);

		fontNormal = mainShell.getFont();
//		fontBold = newFont(mainShell.getFont(), SWT.BOLD);
	
		
		
		
		
		/* Images */
//		putUiGifs();

		/* Templates */
//		containerHtml = S.getResourceAsText("com/ehaviewer/model/res/html/container.html");
//		containerNavItem = containerHtml.substring(containerHtml.indexOf("<!-- NavItemStart -->") + 21, containerHtml.indexOf("<!-- NavItemEnd -->"));
//		blockedHtml = S.getResourceAsText("com/ehaviewer/model/res/html/blocked.html");
				
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

		/* Menu */
	    Menu mainMenuBar = new Menu(mainShell, SWT.BAR);
	    
	    MenuItem fileMenuHeader = new MenuItem(mainMenuBar, SWT.CASCADE);
	    fileMenuHeader.setText("File");
	    
	    Menu fileMenu = new Menu(mainShell, SWT.DROP_DOWN);
	    fileMenuHeader.setMenu(fileMenu);
	    
	    MenuItem openMenuItem = new MenuItem(fileMenu, SWT.CASCADE);
	    openMenuItem.setText("Open");
	    
//	    openMenuItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				
//				FileDialog fileDialog = new FileDialog(mainShell, SWT.MULTI);
//
//				String selectedFileName = fileDialog.open();
//				
//				if (selectedFileName != null) {
//
//					String filePath = fileDialog.getFilterPath(); 
//
//					mainShell.setCursor(cursorWait);
//					
//					for (String shortFileName : fileDialog.getFileNames()) {
//						
//						String fileName = (new File(filePath, shortFileName)).getAbsolutePath();
//						
//						bottomLeftLabel.setText("Loading plugin: " + fileName + "...");
//						
//						XMLPlugin plugin = EHAViewerEngine.loadPluginFile(fileName);
//						
//						if (plugin == null) {
//
//							MessageBox messageBox = new MessageBox(mainShell, SWT.ICON_WARNING);
//							messageBox.setText("Loading plugin failed");
//							messageBox.setMessage("Plugin:\n" + fileName + "\nis not valid");
//							messageBox.open();
//						}
//						else {
//							
//							String pluginID = plugin.getPluginID();
//							boolean alreadyLoaded = false;
//							
//							for (XMLPlugin existentPlugin : plugins) {
//								
//								if (existentPlugin.getPluginID().equalsIgnoreCase(pluginID)) {
//									
//									alreadyLoaded = true;
//								}
//							}
//
//							if (!alreadyLoaded) {
//								
//								plugins.add(plugin);
//								
//								/* TOC */
//								UITocItem primaryTocItem = plugin.loadUITocItems();
//								loadContentsTree(primaryTocItem);
//								
//								/* Index */
//								plugin.loadUIIndexItems(uiIndexItems);
//
//								/* Bookmarks */
//								plugin.loadUIBookmarkItems(uiBookmarkItems);
//								loadBookmarksGrid();
//							}
//						}
//					}
//					
//					mainShell.setCursor(null);
//					bottomLeftLabel.setText("");
//				}
//			}
//		});
//
//	    
//	    @SuppressWarnings("unused")
//		MenuItem fileSeparatorMenuItem = new MenuItem(fileMenu, SWT.SEPARATOR);
//
//	    MenuItem exitMenuItem = new MenuItem(fileMenu, SWT.CASCADE);
//	    exitMenuItem.setText("Exit");
//
//	    exitMenuItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//
//				mainShell.dispose();
//				
//				/* JVM bug */
//				System.exit(0);
//			}
//		});
	    
	    mainShell.setMenuBar(mainMenuBar);
	    mainShell.setLayout(createNoMarginsColumns(1));

	    Composite topComposite = new Composite(mainShell, SWT.NONE);
	    topComposite.setBackground(colorBlue);
	    topComposite.setLayoutData(createFillHorizontal());
	    topComposite.setLayout(createMarginsColumns(1));
	    
	    Composite topButtonsComposite = new Composite(topComposite, SWT.NONE);
	    //topButtonsComposite.setBackground(colorBlue);
	    topButtonsComposite.setLayoutData(createCenterHorizontal());
	    topButtonsComposite.setLayout(createNoMarginsColumns(2));

	    Button buttonViewCsvTab = new Button(topButtonsComposite, SWT.TOGGLE);
	    buttonViewCsvTab.setText("View CSV");
	    buttonViewCsvTab.setLayoutData(createWidth(200));
	    
	    Button buttonAnalyzeCsvTab = new Button(topButtonsComposite, SWT.TOGGLE);
	    buttonAnalyzeCsvTab.setText("Analyze CSV");
	    buttonAnalyzeCsvTab.setLayoutData(createWidth(200));

	    Label separator = new Label(topComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
	    topComposite.setBackground(colorWhite);
	    separator.setLayoutData(createFillHorizontal());

	    
	    
	    createViewCsvTab();

	    
		
	    
		/* Run */
		mainShell.open();
		
		/* Application loop */
		while (!mainShell.isDisposed()) {
			
			if (!display.readAndDispatch()) {
				
				display.sleep();
			}
		}
		
		display.dispose();
	}

	/** */
	private GridData createWidth(int widthHint) {
		
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gridData.widthHint = widthHint;
		return gridData;
	}
	
	private GridData createFillHorizontal() {
		
		return new GridData(SWT.FILL, SWT.CENTER, true, false);
	}

	private GridData createCenterHorizontal() {
		
		return new GridData(SWT.CENTER, SWT.CENTER, false, false);
	}

	private GridData createFillBoth() {
		
		return new GridData(SWT.FILL, SWT.FILL, true, true);
	}
	
	private GridLayout createNoMarginsColumns(int numColumns) {
		
		GridLayout gridLayout = new GridLayout();
		
		gridLayout.marginBottom = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		gridLayout.marginTop = 0;
		gridLayout.marginWidth = 0;
		
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		
		gridLayout.numColumns = numColumns;
		
		return gridLayout;
	}

	private GridLayout createMarginsColumns(int numColumns) {
		
		GridLayout gridLayout = new GridLayout();
		
		gridLayout.marginBottom = 8;
		gridLayout.marginHeight = 8;
		gridLayout.marginLeft = 8;
		gridLayout.marginRight = 8;
		gridLayout.marginTop = 8;
		gridLayout.marginWidth = 8;
		
		gridLayout.horizontalSpacing = 8;
		gridLayout.verticalSpacing = 8;
		
		gridLayout.numColumns = numColumns;
		
		return gridLayout;
	}

	/** View a CSV file in a grid */
	private void createViewCsvTab() {
		
		viewCsvTabComposite = new Composite(mainShell, SWT.NONE);
		//viewCsvTabComposite.setBackground(new Color(display, 0, 255, 255));
		viewCsvTabComposite.setLayoutData(createFillBoth());
		viewCsvTabComposite.setLayout(createMarginsColumns(1));

		final Label viewCsvTabLabel = new Label(viewCsvTabComposite, SWT.NONE);
		viewCsvTabLabel.setText("View .csv");
		
		
		final Composite csvFileComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		//csvFileComposite.setBackground(new Color(display, 255, 255, 0));
		csvFileComposite.setLayoutData(createFillHorizontal());
		csvFileComposite.setLayout(createNoMarginsColumns(3));
		
		final Label csvFileLabel = new Label(csvFileComposite, SWT.NONE);
		csvFileLabel.setText("File");
		csvFileLabel.setLayoutData(createWidth(120));
		
		final Text csvFileText = new Text(csvFileComposite, SWT.SINGLE | SWT.BORDER);
		csvFileText.setText("C:\\Iustin\\Programming\\_static-catalog\\tools\\datas\\big.csv");
		csvFileText.setLayoutData(createFillHorizontal());
		
		final Button csvFileButton = new Button(csvFileComposite, SWT.NONE);
		csvFileButton.setText("Browse");
		//csvFileButton.setLayoutData(createGridData(GridData.BEGINNING, GridData.CENTER, false, false, 120, -1));
		csvFileButton.setLayoutData(createWidth(120));
		
		final Composite csvButtonsComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		//csvButtonsComposite.setBackground(new Color(display, 255, 255, 0));
		csvButtonsComposite.setLayoutData(createFillHorizontal());
		csvButtonsComposite.setLayout(createNoMarginsColumns(5));
		
		final Button csvLoadButton = new Button(csvButtonsComposite, SWT.NONE);
		csvLoadButton.setText("Load");
		csvLoadButton.setLayoutData(createWidth(120));

		final Text csvLoadLinesText = new Text(csvButtonsComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		csvLoadLinesText.setText("100");
		csvLoadLinesText.setLayoutData(createWidth(120));

		final Button csvExtractButton = new Button(csvButtonsComposite, SWT.NONE);
		csvExtractButton.setText("Extract");
		csvExtractButton.setLayoutData(createWidth(120));

		
		final Composite csvStatusComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		csvStatusComposite.setBackground(colorWhite);
		csvStatusComposite.setLayoutData(createFillHorizontal());
		csvStatusComposite.setLayout(createNoMarginsColumns(5));

		final Label csvStatusLabel = new Label(csvStatusComposite, SWT.NONE);
		csvStatusLabel.setBackground(new Color(display, 255, 255, 255));
		csvStatusLabel.setLayoutData(createFillHorizontal());
		csvStatusLabel.setText("Status");

		
		final Grid csvFileGrid = new Grid(viewCsvTabComposite, SWT.V_SCROLL | SWT.H_SCROLL);
		csvFileGrid.setLayoutData(createFillBoth());
		//csvFileGrid.setAutoHeight(true);
		csvFileGrid.setLinesVisible(true);

//		NatTable natTable = new NatTable(viewCsvTabComposite);
//		natTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		
		//csvFileGrid.setAutoWidth(true);
		
		//csvFileGrid.
		
//	    GridColumn columnImage = new GridColumn(searchResultsGrid, SWT.NONE);
//	    columnImage.setWidth(32);
//	    GridColumn columnText = new GridColumn(csvFileGrid, SWT.NONE);
//	    columnText.setWordWrap(true);
//	    columnText.setWidth(500);
		
		/* Events */
		
//		csvLoadButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				
//				csvFileGrid.clearItems();
//				
//				long start = System.currentTimeMillis();
//				
//				//ArrayList<String> arrayList = new ArrayList<>(500_000);
//				ArrayList<String> arrayList = new ArrayList<>();
//				
//				long maxLines = Long.parseLong(csvLoadLinesText.getText());
//
//				GridColumn fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
//				fieldGridColumn.setWordWrap(true);
//				fieldGridColumn.setWidth(500);
//				
////				try {
////					Scanner scanner = new Scanner(new File(csvFileText.getText()));
////					
////					long csvLineIndex = 0;
//////					while (scanner.hasNextLine()) {
////					String line = scanner.nextLine();					
////					while (line != null) {
////						
//////						String line = scanner.nextLine();
////						
////						csvLineIndex++;
////						if (csvLineIndex % 10000 == 0) {
////							//L.p("csvLineIndex = " + csvLineIndex);
////							csvStatusLabel.setText(csvLineIndex + " lines...");
////						}
////						
//////						GridItem csvGridItem = new GridItem(csvFileGrid, SWT.NONE);
//////						csvGridItem.setText(0, line);
////						
////						line = scanner.nextLine();
////					}
////					
////					scanner.close();
////				} catch (FileNotFoundException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////				catch (NoSuchElementException noSuchElementException) {
////					// TODO: handle exception
////				}
//				
//				// vendezvotrevoiture.fr
//
//				
//				TsvParser tsvParser = new TsvParser(new TsvParserSettings());
//				tsvParser.beginParsing(new File(csvFileText.getText()));
//				
//				String buf = "";
//				
//				String[] csvLine = tsvParser.parseNext();
//				long csvLineIndex = 0;
//				while (csvLine != null) {
//					
//					int lineLength = csvLine.length;
//					
//					csvLineIndex++;
//					if (csvLineIndex % 10000 == 0) {
//						//L.p("csvLineIndex = " + csvLineIndex);
//						csvStatusLabel.setText(csvLineIndex + " lines...");
//					}
//
//					buf = buf + csvLine[0];
////					if (csvLineIndex % 100 == 1) {
//					for (int indexu = 0; indexu < 10; indexu++) {
//						arrayList.add(buf);
//						
//					}
//					buf = "";
////						arrayList.add(buf);
////						buf = "";
//						
//						//L.p("csvLineIndex = " + csvLineIndex);
//						//csvStatusLabel.setText(csvLineIndex + " lines...");
////					}
//
//					
//					
////					GridItem csvGridItem = new GridItem(csvFileGrid, SWT.NONE);
////					csvGridItem.setText(0, csvLine[0]);
//
//					
//					csvLine = tsvParser.parseNext();
//				}
//				
//				
////				CsvParser csvParser = new CsvParser(new CsvParserSettings());
////				csvParser.beginParsing(new File(csvFileText.getText()));
////				
////				String[] csvLine = csvParser.parseNext();
////				long csvLineIndex = 0;
////				while (csvLine != null) {
////					
////					int lineLength = csvLine.length;
////					
////					csvLineIndex++;
////					if (csvLineIndex % 10000 == 0) {
////						//L.p("csvLineIndex = " + csvLineIndex);
////						csvStatusLabel.setText(csvLineIndex + " lines...");
////					}
////					
////					csvLine = csvParser.parseNext();
////				}
//				
//				
////				CsvParser csvParser = new CsvParser(new CsvParserSettings());
////				csvParser.beginParsing(new File(csvFileText.getText()));
////				
////				String[] csvLine = csvParser.parseNext();
////				long csvLineIndex = 0;
////				while (csvLine != null) {
////					
////					int lineLength = csvLine.length;
////					
////					csvLineIndex++;
////					if (csvLineIndex % 10000 == 0) {
////						//L.p("csvLineIndex = " + csvLineIndex);
////						csvStatusLabel.setText(csvLineIndex + " lines...");
////					}
////					
//////					String s = "";
//////					StringBuilder sb = new StringBuilder();
//////					for (int index1 = 0; index1 < 5; index1++) {
//////						s = s + String.join(",", csvLine);
//////						//s = s + csvLineIndex + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
//////						//arrayList.add(String.join(",", csvLine));
//////					}
////
////					for (int index1 = 0; index1 < 10; index1++) {
//////						s = s + String.join(",", csvLine);
//////						s = s + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
////						//arrayList.add(String.join(",", csvLine));
////						arrayList.add(csvLineIndex + index1 + "qsfq");
////						//arrayList.add("qsfq");
////					}
//////					arrayList.add(s);
////
//////					arrayList.add("aa");
////					
////					
////					//arrayList.add(String.join(",", csvLine));
////					
//////					if (csvLineIndex == 1) {
//////						for (int index = 0; index < lineLength + 1; index++) {
//////							GridColumn fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
//////						    fieldGridColumn.setWordWrap(true);
//////						    fieldGridColumn.setWidth(150);
//////						}
//////					}
//////					
//////					GridItem csvGridItem = new GridItem(csvFileGrid, SWT.NONE);
//////					csvGridItem.setText(0, csvLineIndex + "");
//////					for (int index = 0; index < lineLength; index++) {
////////					for (int index = 0; index < 1; index++) {						
//////						csvGridItem.setText(index + 1, csvLine[index] + "");
//////					}
////
////					if (csvLineIndex == maxLines) {
////						csvStatusLabel.setText("Load done in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
////						return;
////					}
////
////					csvLine = csvParser.parseNext();
////				}
//				 
//				csvStatusLabel.setText("Done load in " + ((System.currentTimeMillis() - start) / 1000) + " seconds. al = " + arrayList.size());
//				//L.p("Done = " + ((System.currentTimeMillis() - start) / 1000));
//			}
//		});
		
		
		
		csvLoadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				csvFileGrid.clearItems();
				csvFileGrid.disposeAllItems();
				
				long start = System.currentTimeMillis();
				
				//ArrayList<String> arrayList = new ArrayList<>(500_000);
				//ArrayList<String> arrayList = new ArrayList<>();
				
				long maxLines = Long.parseLong(csvLoadLinesText.getText());
				CsvParserSettings csvParserSettings = new CsvParserSettings();
				csvParserSettings.setLineSeparatorDetectionEnabled(true);
				CsvParser csvParser = new CsvParser(csvParserSettings);
				csvParser.beginParsing(new File(csvFileText.getText()));
				
				String[] csvLine = csvParser.parseNext();
				long csvLineIndex = 0;
				while (csvLine != null) {
					
					int lineLength = csvLine.length;
					
					csvLineIndex++;
					if (csvLineIndex % 10000 == 0) {
						//L.p("csvLineIndex = " + csvLineIndex);
						csvStatusLabel.setText(csvLineIndex + " lines...");
					}
					
//					String s = "";
//					StringBuilder sb = new StringBuilder();
//					for (int index1 = 0; index1 < 5; index1++) {
//						s = s + String.join(",", csvLine);
//						//s = s + csvLineIndex + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
//						//arrayList.add(String.join(",", csvLine));
//					}

//					for (int index1 = 0; index1 < 10; index1++) {
////						s = s + String.join(",", csvLine);
////						s = s + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
//						//arrayList.add(String.join(",", csvLine));
//						arrayList.add(csvLineIndex + index1 + "qsfq");
//						//arrayList.add("qsfq");
//					}
//					arrayList.add(s);

//					arrayList.add("aa");
					
					
					//arrayList.add(String.join(",", csvLine));
					
					if (csvLineIndex == 1) {
						for (int index = 0; index < lineLength + 1; index++) {
							GridColumn fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
						    fieldGridColumn.setWordWrap(true);
						    fieldGridColumn.setWidth(150);
						}
					}
					
					GridItem csvGridItem = new GridItem(csvFileGrid, SWT.NONE);
					csvGridItem.setText(0, csvLineIndex + "");
					for (int index = 0; index < lineLength; index++) {
//					for (int index = 0; index < 1; index++) {						
						csvGridItem.setText(index + 1, csvLine[index] + "");
					}

					if (csvLineIndex == maxLines) {
						csvStatusLabel.setText("Load done in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
						return;
					}

					csvLine = csvParser.parseNext();
				}
				 
				csvStatusLabel.setText("Done load in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
				//L.p("Done = " + ((System.currentTimeMillis() - start) / 1000));
			}
		});
		
		csvExtractButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				csvFileGrid.clearItems();
				
				long start = System.currentTimeMillis();
				
				ArrayList<HashMap<String, Long>> fields = new ArrayList<HashMap<String,Long>>();
				ArrayList<String> fieldNames = new ArrayList<>();
				
				GridColumn fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
			    fieldGridColumn.setWordWrap(true);
			    fieldGridColumn.setWidth(250);

			    fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
			    fieldGridColumn.setWordWrap(true);
			    fieldGridColumn.setWidth(150);

			    fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
			    fieldGridColumn.setWordWrap(true);
			    fieldGridColumn.setWidth(300);

			    fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
			    fieldGridColumn.setWordWrap(true);
			    fieldGridColumn.setWidth(300);

				
				long maxLines = Long.parseLong(csvLoadLinesText.getText());
				CsvParserSettings csvParserSettings = new CsvParserSettings();
				csvParserSettings.setLineSeparatorDetectionEnabled(true);
				CsvParser csvParser = new CsvParser(csvParserSettings);
				csvParser.beginParsing(new File(csvFileText.getText()));
				
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
					
					if ((csvLineIndex == maxLines) || (csvLine == null)) {
						csvStatusLabel.setText("Load done in " + ((System.currentTimeMillis() - start) / 1000) + " seconds, for " + csvLineIndex + " lines.");
						
						for (int index = 0; index < lineLength; index++) {
							GridItem csvGridItem = new GridItem(csvFileGrid, SWT.NONE);
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
	
//	/** Create grid layout */
//	public static GridLayout createGridLayout(int marginWidth, int marginHeight, int horizontalSpacing, int verticalSpacing, int numColumns) {
//		
//	    GridLayout gridLayout = new GridLayout();
//	    gridLayout.marginWidth = marginWidth;
//	    gridLayout.marginHeight = marginHeight;
//	    gridLayout.horizontalSpacing = horizontalSpacing;
//	    gridLayout.verticalSpacing = verticalSpacing;
//	    gridLayout.numColumns = numColumns;
//	    
//	    return gridLayout;
//	}
//
//	/** Create grid data */
//	public static GridData createGridData(int horizontalAlignment, int verticalAlignment,
//			boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace, int widthHint, int heightHint) {
//		
//		GridData gridData = new GridData();
//		gridData.horizontalAlignment = horizontalAlignment;
//		gridData.verticalAlignment = verticalAlignment;
//		gridData.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
//		gridData.grabExcessVerticalSpace = grabExcessVerticalSpace;
//		gridData.heightHint = heightHint;
//		gridData.widthHint = widthHint;
//	    
//	    return gridData;
//	}

//    gridData = new GridData();
//    //gridData.heightHint = 32;
//    gridData.horizontalAlignment = GridData.FILL;
//    gridData.grabExcessHorizontalSpace = true;
//    topComposite.setLayoutData(gridData);
	
	
//	/** Draw margin rectangle */
//	private PaintListener drawMarginPaintListener(final Composite composite, final Composite topComposite, final Composite bottomComposite) {
//		
//		return new PaintListener() {
//			
//			@Override
//			public void paintControl(PaintEvent paintEvent) {
//
//				GC gc = paintEvent.gc;
//				int width = composite.getClientArea().width;
//				int height = composite.getClientArea().height;
//				
//				gc.setForeground(colorGrayShadow);
//				gc.drawLine(0, 0, width - 1, 0);
//				gc.drawLine(0, 0, 0, height - 1);
//
//				int topHeight = topComposite.getBounds().height;
//				
//				if (topComposite != null) {
//					
//					gc.drawLine(0, topHeight + 2, width - 1, topHeight + 2);
//				}
//
//				if (bottomComposite != null) {
//					
//					int bottomHeight = bottomComposite.getBounds().height;
//					gc.drawLine(0, height - bottomHeight - 3, width - 1, height - bottomHeight - 3);
//				}
//
//				gc.setForeground(colorGrayHighlight);
//				gc.drawLine(width - 1, 0, width - 1, height);
//				gc.drawLine(0, height - 1, width - 1, height - 1);
//			}
//		};
//	}
//
//	/** Draw tab image */
//	private PaintListener drawTabImagePaintListener(final Composite composite, final String imageName) {
//
//		return new PaintListener() {
//			
//			@Override
//			public void paintControl(PaintEvent paintEvent) {
//
//				Image image = uiImages.get(imageName);
//				paintEvent.gc.drawImage(image,
//						(composite.getClientArea().width - image.getBounds().width) / 2,
//						(composite.getClientArea().height - image.getBounds().height) / 2);
//			}
//		};
//	}
//	
//	/** Draw margin rectangle */
//	private PaintListener newMarginLeftPaintListener(final Composite composite) {
//		
//		return new PaintListener() {
//			@Override
//			public void paintControl(PaintEvent paintEvent) {
//				
//				GC gc = paintEvent.gc;
//				int height = composite.getClientArea().height;
//				
//				gc.setForeground(colorGrayShadow);
//				gc.drawLine(0, 0, 0, height - 1);
//			}
//		};
//	}
//
//	/** Only one instance */
//	public static EHAViewerMainWindow getInstance() {
//		
//		return mainWindowInstance;
//	}
//	
//	/** Close application */
//	public void closeMainWindow() {
//		
//		mainShell.dispose();
//	}
//
//	/** Load TOC tree */
//	private void loadContentsTree(UITocItem primaryTocItem) {
//
//		loadTreeItem(null, primaryTocItem);
//	}
//
//	/** Load actual tree item from model TOC item */
//	private void loadTreeItem(TreeItem parentTreeItem, UITocItem tocItem) {
//		
//		final TreeItem treeItem;
//		
//		if (parentTreeItem == null) {
//			
//			treeItem = new TreeItem(contentsTree, SWT.NONE);
//			treeItem.setImage(uiImages.get("toc_closed"));
//			treeItem.setFont(fontBold);
//					//newFont(treeItem.getFont(), SWT.BOLD));
//		}
//		else {
//			
//			treeItem = new TreeItem(parentTreeItem, SWT.NONE);
//			treeItem.setFont(fontNormal);
//			
//			if (tocItem.getTocItems().size() == 0) {
//				
//				treeItem.setImage(uiImages.get("topic"));	
//			}
//			else if (tocItem.getRef() != null) {
//				
//				treeItem.setImage(uiImages.get("container_topic"));
//			}
//			else {
//				
//				treeItem.setImage(uiImages.get("container_obj"));
//			}
//		}
//
//		treeItem.setText(tocItem.getName());
//		treeItem.setData(tocItem);
//		
//		baseUrlSyncs.put(tocItem.getBaseUrl(), treeItem);
//		
//		
//		for (UITocItem childTocItem : tocItem.getTocItems()) {
//			
//			loadTreeItem(treeItem, childTocItem);
//		}
//	}
//
//	/** Reload index list */
//	private void loadIndexGrid() {
//		
//		indexGrid.clearItems();
//		indexGrid.disposeAllItems();
//
//		String filter = lastIndexDisplayWord.toLowerCase();
//		
//		ArrayList<Integer> filterUIIndexItems = new ArrayList<>();
//		
//		int index = -1;
//		for (UIIndexItem uiIndexItem : uiIndexItems) {
//			
//			index++;
//			
//			if (filter.trim().length() == 0) {
//				
//				filterUIIndexItems.add(1);
//			}
//			else {
//				
//				if ((uiIndexItem.getBaseUrl() !=  null) && (uiIndexItem.getName().toLowerCase().contains(filter))) {
//				
//					filterUIIndexItems.add(1);
//					
//					int backIndent = uiIndexItem.getIndent() - 1;
//					int backIndex = index;
//					while (backIndent > -1) {
//						
//						backIndex--;
//						if (backIndent == uiIndexItems.get(backIndex).getIndent()) {
//	
//							filterUIIndexItems.set(backIndex, 1);
//							backIndent--;
//						}
//					}
//				}
//				else {
//					
//					filterUIIndexItems.add(0);
//				}
//			}	
//		}
//		
//		index = -1;
//		for (UIIndexItem uiIndexItem : uiIndexItems) {
//
//			index++;
//			
//			if (filterUIIndexItems.get(index) == 1) {
//
//				GridItem gridItem = new GridItem(indexGrid, SWT.NONE);
//				
//				gridItem.setText("                              ".substring(0, uiIndexItem.getIndent() * 4) + uiIndexItem.getName());
//				gridItem.setData(uiIndexItem);
//				
//				if (uiIndexItem.getBaseUrl() == null) {
//				
//					gridItem.setForeground(colorGrayShadower);
//					gridItem.setFont(0, fontBold);				
//				}
//			}
//		}
//	}
//
//	/** Reload bookmarks list */
//	private void loadBookmarksGrid() {
//		
//		bookmarksGrid.clearItems();
//		bookmarksGrid.disposeAllItems();
//
//		for (UIBookmarkItem uiBookmarkItem : uiBookmarkItems) {
//			
//			GridItem bookmarksGridItem = new GridItem(bookmarksGrid, SWT.NONE);
//			bookmarksGridItem.setImage(0, uiImages.get("topic"));
//			bookmarksGridItem.setText(1, uiBookmarkItem.getName());
//			bookmarksGridItem.setForeground(1, colorBlue);
//
//			bookmarksGridItem.setData(uiBookmarkItem);
//		}
//	}
//
//	/** Cache images */
//	private void putUiGifs() {
//		
//		for (String uiImageName : uiImagesNames) {
//		
//			uiImages.put(uiImageName, getResourceAsImage("com/ehaviewer/res/img/" + uiImageName + ".gif"));
//		}
//	}
//
//	/** Often used grid layout */
//	private GridLayout newGridLayout00001() {
//		
//	    GridLayout gridLayout = new GridLayout();
//	    gridLayout.marginWidth = 0;
//	    gridLayout.marginHeight = 0;
//	    gridLayout.verticalSpacing = 0;
//	    gridLayout.horizontalSpacing = 0;
//	    gridLayout.numColumns = 1;
//
//	    return gridLayout;
//	}
//
//	/** Often used grid data */
//	private GridData newGridDataFILLFILLtruetrue() {
//		
//		return new GridData(SWT.FILL, SWT.FILL, true, true);
//	}
//
//	/** Change left tab */
//	private void changeTabComposite(String tabName, Composite tabComposite, Composite tabContentComposite, ToolBar tabToolbar) {
//
//		tabNameLabel.setText(tabName);
//		tabNameLabel.pack();
//
//		activeTabComposite.setBackground(colorGray);
//		activeTabComposite = tabComposite;
//		activeTabComposite.setBackground(colorGrayHighlight);
//		
//		activeComposite.setVisible(false);
//		((GridData) activeComposite.getLayoutData()).exclude = true;
//		activeComposite = tabContentComposite;
//		activeComposite.setVisible(true);
//		((GridData) activeComposite.getLayoutData()).exclude = false;
//		activeComposite.getParent().layout();
//		
//		activeTabToolbar.setVisible(false);
//		((GridData) activeTabToolbar.getLayoutData()).exclude = true;
//		activeTabToolbar = tabToolbar;
//		activeTabToolbar.setVisible(true);
//		((GridData) activeTabToolbar.getLayoutData()).exclude = false;
//		tabToolbar.getParent().layout();
//	}
//
//	/** Maximize buttons */
//	private SelectionAdapter newMaximizeSelectionAdapter(final Composite control) {
//		
//		return new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				
//				ToolItem eventToolItem = (ToolItem) selectionEvent.widget;
//				
//				if (middleSashForm.getMaximizedControl() == control) {
//					
//					middleSashForm.setMaximizedControl(null);
//					eventToolItem.setImage(uiImages.get("maximize"));
//					eventToolItem.setToolTipText("Maximize");
//				}
//				else {
//					
//					middleSashForm.setMaximizedControl(control);
//					eventToolItem.setImage(uiImages.get("e_restore"));
//					eventToolItem.setToolTipText("Restore");
//				}
//				
//				super.widgetSelected(selectionEvent);
//			}
//		};
//	}
//
//	/** Load HTML in browser */
//	private void loadStartHTML() {
////		L.p("baseUrl = " + baseUrl);
//		
//		String html = S.getResourceAsText("com/ehaviewer/model/res/html/start.html");
//		
//		String startHead = "<head>";
//		String endHead = "</head>";
//		
//		if (html.indexOf(startHead) == -1) {
//			
//			startHead = "<HEAD>";
//		}
//
//		if (html.indexOf(endHead) == -1) {
//			
//			endHead = "</HEAD>";
//		}
//
//		html = html.replace(endHead, "<style> body { margin: 0; padding: 0; font-family: " + htmlFont + "; } </style>" + endHead);
//		
//		String endBody = "</body>";
//		
//		if (html.indexOf(endBody) == -1) {
//			
//			endBody = "</BODY>";
//		}
//		
//		
////		browser.setUrl("about:blank");
//		browser.setText(html);
//	}
//
//	/** Load HTML in browser */
//	private void loadHTML(String baseUrl) {
////		L.p("loadHTML baseUrl = " + baseUrl);
//		
//		URL url = getUrl(baseUrl);
//		
//		/* about:blank */
//		if (url == null) {
//			//L.p("exit");
//			return;
//		}
//
//		TreeItem treeItem = findToc(url.toString());
//		String html = containerHtml;
//		
//		if (baseUrl.indexOf("help/nav") > -1) {
//			
//			html = html.replace("<%NavTitle%/>", treeItem.getText());
//			
//			String containerNavItemContent = "\n";
//			
//			for (TreeItem childTreeItem : treeItem.getItems()) {
//				
//				UITocItem uiTocItem = (UITocItem) childTreeItem.getData();
//				containerNavItemContent = containerNavItemContent + containerNavItem.replace("<%NavItemHref%/>",
//						uiTocItem.getBaseUrl()).replace("<%NavItemTitle%/>", childTreeItem.getText()) + "\n";
//			}
//			
//			html = html.replace(containerNavItem, containerNavItemContent);
//		}
//		else {
//			
//			String filePath = url.getPath();
//			
//			//filePath = filePath.replace("%23", "#");
//			int indexDiez = filePath.indexOf("#");
//			if (indexDiez > -1) {
//				
//				filePath = filePath.substring(0, indexDiez);
//			}
//
//			int indexQuestionMark = filePath.indexOf("?");
//			if (indexQuestionMark > -1) {
//				
//				filePath = filePath.substring(0, indexQuestionMark);
//			}
//
////			L.p(filePath);
//			
//			File htmlFile = new File(filePath);
//			
//			if (htmlFile.exists()) {
//			
//				html = S.loadFileInString(htmlFile);
//			}
//			else {
//				
//				if (filePath.indexOf("http:/") > -1) {
//					
//					/* Is link */
//					String link = filePath.substring(filePath.indexOf("http:/"));
//
//					if (!(link.startsWith("http://"))) {
//						
//						link = link.replace("http:/", "http://");	
//					}
//					//org.eclipse.swt.program.Program.launch(link);
//					//return;
//					html = blockedHtml;
//					html = html.replace("<%BlockedTitle%/>", "External links are blocked");
//					html = html.replace("<%BlockedMessage%/>", "External links are blocked");
//					html = html.replace("<%BlockedLink%/>", link);
//				}
//				else if (filePath.indexOf("https:/") > -1) {
//					
//					/* Is link */
//					String link = filePath.substring(filePath.indexOf("https:/"));
//
//					if (!(link.startsWith("https://"))) {
//						
//						link = link.replace("https:/", "https://");	
//					}
//					//org.eclipse.swt.program.Program.launch(link);
//					//return;
//					html = blockedHtml;
//					html = html.replace("<%BlockedTitle%/>", "External (secure) links are blocked");
//					html = html.replace("<%BlockedMessage%/>", "External (secure) links are blocked");
//					html = html.replace("<%BlockedLink%/>", link);
//				}
//				else {
//					
//					/* From another plugin */
//					html = blockedHtml;
//					html = html.replace("<%BlockedTitle%/>", "File not found");
//					html = html.replace("<%BlockedMessage%/>", "File not found (is from another plugin?)");
//					html = html.replace("<%BlockedLink%/>", filePath);
//				}
//			}
//		}
//		
//		String startHead = "<head>";
//		String endHead = "</head>";
//		
//		if (html.indexOf(startHead) == -1) {
//			
//			startHead = "<HEAD>";
//		}
//
//		if (html.indexOf(endHead) == -1) {
//			
//			endHead = "</HEAD>";
//		}
//
//		html = html.replace(startHead, startHead + "<base href=\"" + baseUrl + "\">");
//		html = html.replace(endHead, "<style> body { font-family: " + htmlFont + "; } </style>" + endHead);
//		
//		String endBody = "</body>";
//		
//		if (html.indexOf(endBody) == -1) {
//			
//			endBody = "</BODY>";
//		}
//		
//		/* Anchor */
//		int qmIndex = baseUrl.indexOf("?");
//		if (qmIndex == -1) {
//
//			int diezIndex = baseUrl.indexOf("#");
//			if (diezIndex > -1) {
//
//				html = html.replace(endBody, "<script> location.hash = \"" + baseUrl.substring(diezIndex + 1) + "\"; </script>\n" + endBody);
//			}
//		}
//
//		
//		
//		/* Bread crumbs */
//		if (treeItem != null) {
//			
//			int bodyStartIndex = html.indexOf("<body");
//			
//			if (bodyStartIndex == -1) {
//				
//				bodyStartIndex = html.indexOf("<BODY");
//			}
//			
//			int bodyEndIndex = html.indexOf(">", bodyStartIndex);
//			
//			
//			String breadCrumbs = "";
//			treeItem = treeItem.getParentItem();
//			
//			while (treeItem != null) {
//				
//				String anchor = "<a href=\"" + ((UITocItem) treeItem.getData()).getBaseUrl() + "\">" + treeItem.getText() + "</a>"; 
//				
//				if (breadCrumbs.length() == 0) {
//					
//					breadCrumbs = anchor;
//				}
//				else {
//					
//					breadCrumbs = anchor + " > " + breadCrumbs;	
//				}
//				
//				treeItem = treeItem.getParentItem();
//			}
//			
//			breadCrumbs = "\n<div>" + breadCrumbs + "</div>\n";
//			
//			html = html.substring(0, bodyEndIndex + 1) + breadCrumbs + html.substring(bodyEndIndex + 1);
//		}
//		
////		L.p(html);
//		
//		browser.setUrl("about:blank");
//		browser.setText(html);
//		
//		activeBaseUrl = baseUrl;
//		
//		/* Sync TOC */
//		if (alwaysSyncContents) {
//			
//			syncContents(false);
//		}
//		
//		/* History */
//		if (baseUrlHistory.size() > 0) {
//		
//			if (baseUrlHistory.get(baseUrlHistoryIndex).equalsIgnoreCase(baseUrl)) {
//				
//				return;
//			}
//		}
//		
//		baseUrlHistoryIndex++;
//		baseUrlHistory.add(baseUrlHistoryIndex, baseUrl);
//	}
//
//	/** URL from string */
//	private URL getUrl(String url) {
//		
//		if (url.indexOf("PLUGINS_ROOT") > -1) {
//			
//			return null;
//		}
//		
//		try {
//			
//			return new URL(url);
//			
//		}
//		catch (MalformedURLException malformedURLException) {
//			
//			return null;
//		}
//	}
//
//	/** Synchronize with content */
//	private void syncContents(boolean showMessage) {
//		
//		URL url = getUrl(activeBaseUrl);
//		if (url == null) {
//			
//			return;
//		}
//		
//		TreeItem treeItem = findToc(url.toString());
//		
//		if (treeItem != null) {
//			
//			contentsTree.setSelection(treeItem);
//		}
//		else if (showMessage) {
//				
//			MessageBox messageBox = new MessageBox(mainShell, SWT.ICON_WARNING);
//			messageBox.setText("Message from Eclipse Help Viewer");
//			messageBox.setMessage("The current document displayed does not exist in the table of contents.");
//			messageBox.open();
//		}
//	}
//
//	/** Find TOC from URL */
//	private TreeItem findToc(String stdUrl) {
//		
//		TreeItem treeItem = baseUrlSyncs.get(stdUrl);
//		
//		if (treeItem == null) {
//			
//			int diezIndex = stdUrl.indexOf("#");
//
//			if (diezIndex > -1) {
//				
//				treeItem = baseUrlSyncs.get(stdUrl.substring(0, diezIndex));
//			}
//		}
//
//		return treeItem;
//	}
//	
//	/** Collapse all tree items */
//	private void collapseAll(TreeItem treeItem) {
//		
//		for (TreeItem childTreeItem : treeItem.getItems()) {
//			
//			collapseAll(childTreeItem);
//		}
//
//		if (treeItem.getExpanded()) {
//			
//			treeItem.setExpanded(false);
//			
//			 if (treeItem.getParentItem() == null) {
//				 
//				 treeItem.setImage(uiImages.get("toc_closed"));
//			 }
//		}
//	}
//
//	/** Gray button */
//	private Button newGrayButton(Composite parentComposite, final String caption) {
//
//		Boolean pressed = false;
//	
//		final Button button = new Button(parentComposite, SWT.PUSH);
//		button.setData(pressed);
//		
//		GC gc = new GC(button);
//		final Font fontGC = newFont(button.getFont(), SWT.BOLD);
//		gc.setFont(fontGC);
//		Point pointGC = gc.textExtent(caption);
//
//		
//		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
//		((GridData) button.getLayoutData()).heightHint = pointGC.y + 6;
//		((GridData) button.getLayoutData()).widthHint = pointGC.x + 8;
//		
//		button.addPaintListener(new PaintListener() {
//			@Override
//			public void paintControl(PaintEvent paintEvent) {
//				
//				GC gc = paintEvent.gc;
//				
//				gc.setBackground(colorGrayShadow);
//				gc.setForeground(colorWhite);
//				gc.fillRectangle(paintEvent.x, paintEvent.y, paintEvent.width, paintEvent.height);
//				gc.setFont(fontGC);
//				
//				Boolean pressed = (Boolean) button.getData();
//				
//				if (pressed) {
//				
//					gc.drawText(caption, 5, 4, true);
//				}
//				else {
//					
//					gc.drawText(caption, 4, 3, true);
//				}
//			}
//		});
//		
//		button.addMouseListener(new MouseAdapter() {
//
//			@Override
//			public void mouseDown(MouseEvent mouseEvent) {
//				
//				Button button = (Button) mouseEvent.widget;
//				button.setData(new Boolean(true));
//				button.redraw();
//			}
//			
//			@Override
//			public void mouseUp(MouseEvent mouseEvent) {
//				
//				Button button = (Button) mouseEvent.widget;
//				button.setData(new Boolean(false));
//				button.redraw();
//			}
//			
//		});
//		
//		return button;
//	}
//
//	/** Search results */
//	private void loadSearchResultsList() throws IOException, ParseException {
//
//		if (activeTabComposite != searchResultsTabComposite) {
//			
//			changeTabComposite("Search Results", searchResultsTabComposite, searchResultsComposite, searchResultsToolbar);
//			
//		}
//		
//		searchResultsGrid.clearItems();
//		searchResultsGrid.disposeAllItems();
//		
//		String searchWords = lastSearchPhrase.trim().toLowerCase().replace(" ", " AND ");
//		
//		for (XMLPlugin plugin : plugins) {
//		
//			if (plugin.getPluginSearchResultsPaths().size() == 0) {
//				
//				continue;
//			}
//			
//			for (String path : plugin.getPluginSearchResultsPaths()) {
//			
//				FSDirectory fsDirectory = FSDirectory.open(new File(plugin.getCacheFolderPluginPath(), path));
//				IndexReader indexReader = IndexReader.open(fsDirectory);	
//				IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//		
//			    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
//			    QueryParser queryParser = new QueryParser(Version.LUCENE_35, "contents", analyzer);
//			    Query query = queryParser.parse(searchWords);
//			    //ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
//			    ScoreDoc[] scoreDocs = indexSearcher.search(query, 10000).scoreDocs;
//				    
//			    if ((scoreDocs.length > 0) && groupSearchByCategories) {
//	
//					GridItem searchGridCategoryItem = new GridItem(searchResultsGrid, SWT.NONE);
//					searchGridCategoryItem.setColumnSpan(0, 2);
//					searchGridCategoryItem.setText(0, plugin.getPluginName());
//					searchGridCategoryItem.setForeground(colorBlue);
//					searchGridCategoryItem.setFont(0, fontBold);
//					
//					TreeItem treeItem = contentsTree.getItem(plugins.indexOf(plugin));
//					UITocItem tocItem = (UITocItem) treeItem.getData();
////					L.p("tocItem.getRef()   " + tocItem.getRef());
//					searchGridCategoryItem.setData("ref", tocItem.getRef());
//					searchGridCategoryItem.setData("baseUrl", tocItem.getBaseUrl());
//			    }
//	
//				for (int i = 0; i < scoreDocs.length; i++) {
//					
//					Document scoreDoc = indexSearcher.doc(scoreDocs[i].doc);
////					L.p("    " + scoreDoc.get("raw_title"));
////					L.p("        " + scoreDoc.get("summary"));
//	
//					GridItem searchGridTitleItem = new GridItem(searchResultsGrid, SWT.NONE);
//					searchGridTitleItem.setImage(0, uiImages.get("topic"));
//					searchGridTitleItem.setText(1, scoreDoc.get("raw_title").trim());
//					searchGridTitleItem.setForeground(1, colorBlue);
//					//searchGridTitleItem.setToolTipText(1, plugin.getPluginName());
//					
//					String name = scoreDoc.get("name");
//					String ref = name.substring(1);
//					String baseUrl = new File(plugin.getCacheFolderPluginPath(), name.replace("/" + plugin.getPluginID(), "")).toURI().toURL().toString().replace("%23", "#").replace("%3F", "?");
//		
//					searchGridTitleItem.setData("ref", ref);
//					searchGridTitleItem.setData("baseUrl", baseUrl);
//					
//					if (showSearchDescriptions) {
//	
//						GridItem searchGridSummaryItem = new GridItem(searchResultsGrid, SWT.NONE);
//						searchGridSummaryItem.setText(1, scoreDoc.get("summary").trim().replaceAll("(\\s)+", "$1"));
//					}
//				}
//	
//				indexSearcher.close();
//			}
//		}
//	}
//	
//	/** Search results wrapper */
//	private void loadSearchResults() {
//		
//		try {
//			
//			loadSearchResultsList();
//		} 
//		catch (IOException ioException) {
//
//			L.e("Load search results", ioException);
//		}
//		catch (ParseException parseException) {
//
//			L.e("Load search results", parseException);
//		}
//	}
//
//	/** New push tool item helper */
//	private ToolItem newToolItem(ToolBar toolBar, String imageName, String toolTipText) {
//		
//		return newToolItem(toolBar, SWT.PUSH, imageName, toolTipText);
//	}
//
//	/** New tool item helper */
//	private ToolItem newToolItem(ToolBar toolBar, int params, String imageName, String toolTipText) {
//		
//		ToolItem toolItem = new ToolItem(toolBar, params);
//		toolItem.setText("");
//		toolItem.setImage(uiImages.get(imageName));
//		toolItem.setToolTipText(toolTipText);
//		
//		return toolItem;
//	}
//	
//	/** Change font attributes */
//	private void changeFont(Control control, int attr) {
//		
//		control.setFont(newFont(control.getFont(), attr));
//	}
//
//	/** New font attributes */
//	private Font newFont(Font font, int attr) {
//		
//		FontData fontData = font.getFontData()[0];
//		fontData = new FontData(fontData.getName(), fontData.getHeight(), attr);
//		//fontData.data.lfUnderline = 1;
//		
//		return new Font(display, fontData);
//	}
//
//	/** Highlight color */
//	private Color getHighlightColor(Color color) {
//		
//		float[] hsb = color.getRGB().getHSB();
//		float brightness = hsb[2];
//		
//		if (brightness * 1.5f > 1) {
//			
//			brightness = 0.99f;
//		}
//		
//		RGB rgb = new RGB(hsb[0], hsb[1], brightness);
//		
//		return new Color(Display.getDefault(), rgb.red, rgb.green, rgb.blue);
//	}
//
//	/** Shadow color */
//	private Color getShadowColor(Color color) {
//
//		float[] hsb = color.getRGB().getHSB();
//		float brightness = hsb[2];
//		
//		RGB rgb = new RGB(hsb[0], hsb[1], brightness * 0.8f);
//		
//		return new Color(Display.getDefault(), rgb.red, rgb.green, rgb.blue);
//	}
	
	/** Load image resource */
	public Image getResourceAsImage(String imageResourceName) {
		
		return new Image(display, S.getResourceAsInputStream(imageResourceName));
	}

	/** win-64, mac-64, linux-64 */
//	private String getArchFilename(String prefix) {
//		
//	   return prefix + "_" + getOSName() + "_" + getArchName() + ".jar";
//	}

	/** win, mac, linux */
//	private String getOSName() {
//		
//	   String osNameProperty = System.getProperty("os.name");
//
//	   if (osNameProperty == null) {
//		   
//	       throw new RuntimeException("os.name property is not set");
//	   }
//	   else {
//		   
//	       osNameProperty = osNameProperty.toLowerCase();
//	   }
//
//	   if (osNameProperty.contains("win")) {
//		   
//	       return "win";
//	   }
//	   else if (osNameProperty.contains("mac")) {
//		   
//	       return "mac";
//	   }
//	   else if (osNameProperty.contains("linux") || osNameProperty.contains("nix")) {
//		   
//	       return "linux";
//	   }
//	   else {
//		   
//	       throw new RuntimeException("Unknown OS name: " + osNameProperty);
//	   }
//	}

	/** Calibri, Lucida Grande, FreeSans */
	private String getHtmlFont() {
		
	   osNameProperty = System.getProperty("os.name");

	   if (osNameProperty == null) {
		   
	       throw new RuntimeException("os.name property is not set");
	   }
	   else {
		   
	       osNameProperty = osNameProperty.toLowerCase();
	   }

	   if (osNameProperty.contains("win")) {
		   
	       return "Calibri";
	   }
	   else if (osNameProperty.contains("mac")) {
		   
	       return "Lucida Grande";
	   }
	   else if (osNameProperty.contains("linux") || osNameProperty.contains("nix")) {
		   
	       return "FreeSans";
	   }
	   else {
		   
	       throw new RuntimeException("Unknown OS name: " + osNameProperty);
	   }
	}
	
	/** 64, 32 */
//	private String getArchName() {
//		
//	   String osArch = System.getProperty("os.arch");
//
//	   if (osArch != null && osArch.contains("64")) {
//		   
//	       return "64";
//	   }
//	   else {
//		   
//	       return "32";
//	   }
//	}

	/* Getters and setters */

	public Display getDisplay() {
		return display;
	}

	public Shell getMainShell() {
		return mainShell;
	}
}
