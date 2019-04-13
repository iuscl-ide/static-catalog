/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.ui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.static_catalog.engine.StaticCatalogEngine;
import org.static_catalog.engine.StringAsNumberComparator;
import org.static_catalog.main.L;
import org.static_catalog.main.P;
import org.static_catalog.model.StaticCatalogField;
import org.static_catalog.model.StaticCatalogFilters;

import com.alibaba.fastjson.JSON;

/** Generator main window */
public class StaticCatalogGeneratorMainWindow {

	/** Type names */
	public static final LinkedHashMap<String, String> typeNames = new LinkedHashMap<>();
	public static final LinkedHashMap<String, String> nameTypes = new LinkedHashMap<>();
	static {
		typeNames.put("long", "Integer");
		typeNames.put("double", "Real");
		typeNames.put("date", "Date");
		typeNames.put("text", "Text");
		
		for (Entry<String, String> entry : typeNames.entrySet()) {
			nameTypes.put(entry.getValue(), entry.getKey());
		}
	}
	public static final String[] typeNameValues = typeNames.values().toArray(new String[typeNames.values().size()]);
	


	
	/** File control */
	private interface FileControl {
		
		public String getCompleteFileName();
	}

	
	/** Progress */
	public interface LoopProgress {
		
		public void doProgress(String progressMessage);
	}

	/** Change tab */
	private interface TabButtons {
		
		public void changeTab(int selectedButtonIndex);
	}

	/** Load filters */
	private interface LoadFilters {
		
		public void loadFilters(StaticCatalogFilters staticCatalogFilters);
	}

	/** Root folder */
	private String applicationRootFolder;

	/** Properties */
	private P p;

	/** New */
	public StaticCatalogGeneratorMainWindow(String applicationRootFolder) {
		super();
		this.applicationRootFolder = applicationRootFolder;
		
		p = P.load(this, applicationRootFolder + "/static-catalog.config.json");
	}

	/** Natural order */
	private final StringAsNumberComparator stringAsNumberComparator = new StringAsNumberComparator();
	
	/** Concurrent */
	private AtomicBoolean doLoop = new AtomicBoolean(true);
	
	/** Main display */
	private Display display;

	/** UI (SWT) */
	private UI ui;
	
	/* Fonts */
	private Font fontNormal;
	private Font fontBold;
	private Font fontBigger;

	/* Colors */
	private Color whiteColor;
//	private Color gridBackgroundColor;
//	private Color gridTextColor;
//	private Color gridSelectedBackgroundColor;
//	private Color gridSelectedTextColor;

	
	/** Change */
	private TabButtons tabButtons;
	
	/** Load filters */
	private LoadFilters loadFilters;
	
	/** Main application loop in the window */
	public void runMainWindow() {

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
		
		/* Display */
		Display.setAppName("static-catalog");
		display = new Display();
		ui = new UI(false, display);

		/* Main window */
		Shell mainShell = new Shell(display);
		mainShell.setText("static-catalog Generator");
	    mainShell.setLayout(ui.createMarginsVerticalSpacingGridLayout(UI.sep8, UI.sep8));

		/* Icon */
		Image[] iconImages = new Image[9];
		String[] rez = { "16", "24", "32", "48", "64", "96", "128", "256", "512" };
		for (int index = 0; index < 9; index++) {
			String rezimg = rez[index];
			iconImages[index] = ui.getResourceAsImage("org/static_catalog/res/icon/" + rezimg + "x" + rezimg + ".png");
		}
		mainShell.setImages(iconImages);
		
		/* Location */
		mainShell.setLocation(250, 0);
		mainShell.setSize(display.getClientArea().width - 600, display.getClientArea().height);

		/* Fonts */
		fontNormal = mainShell.getFont();
		fontBold = ui.newFontAttributes(fontNormal, SWT.BOLD);
		fontBigger = ui.newFontSize(fontBold, 14);

		/* Colors */
		whiteColor = new Color(display, 255, 255, 255);
//		gridBackgroundColor = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
//		gridTextColor = display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
//		gridSelectedBackgroundColor = display.getSystemColor(SWT.COLOR_LIST_SELECTION);
//		gridSelectedTextColor = display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);
		
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
	    /* Create Filters */
		createCreateFiltersTab(mainComposites.get(2));
		
		
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

	/** Create file control */
	private FileControl addFileControl(Composite parentComposite, String labelText, FileControlProperties p) {
		
		return addFileControl(parentComposite, labelText, false, p);
	}
	
	/** Create file control */
	private FileControl addFileControl(Composite parentComposite, String labelText, boolean isFolder, FileControlProperties p) {
		
		/*
		 * TODO 
		 * 1/ The Browse button
		 * 2/ The text to be a combo with recent files
		 * 
		 */
		
		final Composite fileComposite = new Composite(parentComposite, SWT.NONE);
		ui.addDebug(fileComposite);
	    fileComposite.setLayoutData(ui.createFillHorizontalGridData());
		fileComposite.setLayout(ui.createColumnsSpacingGridLayout(4, UI.sep8));
		
		final Label fileLabel = new Label(fileComposite, SWT.NONE);
		fileLabel.setText(labelText);
		fileLabel.setLayoutData(ui.createWidth120GridData());
		
		final CComboNoText recentFilesCCombo = new CComboNoText(fileComposite, SWT.BORDER | SWT.READ_ONLY);
		recentFilesCCombo.setLayoutData(ui.createWidthGridData(recentFilesCCombo.findButtonWidth()));
		recentFilesCCombo.setEditable(false);

		final Text fileText = new Text(fileComposite, SWT.SINGLE | SWT.BORDER);
		fileText.setText(p.getFileName());
		fileText.setLayoutData(ui.createFillHorizontalGridData());

		final Button fileButton = new Button(fileComposite, SWT.NONE);
		fileButton.setText("Browse");
		fileButton.setLayoutData(ui.createWidth120GridData());

		
		int recentFileNamesSize = p.getRecentFileNames().size(); 
		if (recentFileNamesSize == 0) {
			recentFilesCCombo.add(FileControlProperties.NO_RECENT_FILES);
		}
		else {
			recentFilesCCombo.setItems(p.getRecentFileNames().toArray(new String[recentFileNamesSize]));
		}
		
		recentFilesCCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
	
				CComboNoText cComboNoText = (CComboNoText) selectionEvent.widget;
				String recentFileName = cComboNoText.getText();
				
				if (recentFileName.equals(FileControlProperties.NO_RECENT_FILES)) {
					return;
				}
				
				p.setFileName(recentFileName);
				fileText.setText(recentFileName);
				
				ArrayList<String> recentFileNames = p.getRecentFileNames(); 
				recentFileNames.remove(recentFileName);
				recentFileNames.add(0, recentFileName);
				recentFilesCCombo.setItems(p.getRecentFileNames().toArray(new String[recentFileNames.size()]));
				
				p.save();
			}
		});

		fileText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent focusEvent) {
				
				String focusLostFileName = fileText.getText().trim();
				if (!focusLostFileName.equalsIgnoreCase(p.getFileName())) {
					
					p.setFileName(focusLostFileName);

					ArrayList<String> recentFileNames = p.getRecentFileNames(); 
					if (recentFileNames.contains(focusLostFileName)) {
						recentFileNames.remove(focusLostFileName);
					}
					recentFileNames.add(0, focusLostFileName);
					recentFilesCCombo.setItems(p.getRecentFileNames().toArray(new String[recentFileNames.size()]));
					
					p.save();
				}
			}
		});
		
		fileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {

				String oldFileName = fileText.getText();
				String newFileName = null;
				if (isFolder) {
					
				}
				else {
					FileDialog fileDialog = new FileDialog(parentComposite.getShell(), SWT.NONE);
					fileDialog.setFileName(oldFileName);
					newFileName = fileDialog.open();
				}
				
				if ((newFileName != null) && (!newFileName.equalsIgnoreCase(oldFileName))) {

					p.setFileName(newFileName);
					fileText.setText(newFileName);
					
					ArrayList<String> recentFileNames = p.getRecentFileNames(); 
					if (recentFileNames.contains(newFileName)) {
						recentFileNames.remove(newFileName);
					}
					recentFileNames.add(0, newFileName);
					recentFilesCCombo.setItems(p.getRecentFileNames().toArray(new String[recentFileNames.size()]));
					
					p.save();
				}
			}
		});
		
		return new FileControl() {
			@Override
			public String getCompleteFileName() {

				return fileText.getText();
			}
		};
	}

	/** Main tabs */
	private ArrayList<Composite> createMainTabs(Shell mainShell) {
		
	    final Composite topComposite = new Composite(mainShell, SWT.NONE);
	    ui.addDebug(topComposite);
	    topComposite.setLayoutData(ui.createFillHorizontalGridData());
	    topComposite.setLayout(ui.createGridLayout());

	    final Composite topButtonsComposite = new Composite(topComposite, SWT.NONE);
	    ui.addDebug(topButtonsComposite);
	    GridData topButtonsCompositeGridData = ui.createGridData();
	    topButtonsCompositeGridData.horizontalAlignment = SWT.CENTER;
	    topButtonsCompositeGridData.grabExcessHorizontalSpace = true;
	    topButtonsComposite.setLayoutData(topButtonsCompositeGridData);
	    topButtonsComposite.setLayout(ui.createColumnsSpacingGridLayout(4, UI.sep8));
	    
	    final String[] topButtonTexts = { "View CSV", "Analyse CSV", "Filters", "Generate" };
	    
	    ArrayList<Button> topButtons = new ArrayList<>(); 

	    for (String topButtonText : topButtonTexts) {

		    Button button = new Button(topButtonsComposite, SWT.TOGGLE);
		    button.setText(topButtonText);
		    button.setLayoutData(ui.createWidth120GridData());
		    
		    topButtons.add(button);
	    }
	    
	    final Label separator = new Label(mainShell, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(ui.createFillHorizontalGridData());
	    
	    final ArrayList<Composite> mainComposites = new ArrayList<>(); 
	    
	    for (String topButtonText : topButtonTexts) {

	    	final Composite composite = new Composite(mainShell, SWT.NONE);
	    	ui.addDebug(composite);
		    composite.setLayoutData(ui.createFillBothGridData());
		    composite.setLayout(ui.createVerticalSpacingGridLayout(UI.sep8));
	    	
		    mainComposites.add(composite);
		    
		    final Label label = new Label(composite, SWT.NONE);
		    label.setText(topButtonText);
		    label.setFont(fontBigger);
		    label.setLayoutData(ui.createFillHorizontalGridData());
	    }

	    final int[] activeButtonIndex = new int[1];
	    
	    tabButtons = new TabButtons() {
			@Override
			public void changeTab(int selectedButtonIndex) {

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
		};
	    
	    
	    SelectionAdapter topButtonSelectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				int selectedButtonIndex = topButtons.indexOf(selectionEvent.widget);
				tabButtons.changeTab(selectedButtonIndex);
			}
		};
	    
		for (Button button : topButtons) {
			button.addSelectionListener(topButtonSelectionAdapter);
		}

		activeButtonIndex[0] = -1;
		tabButtons.changeTab(0);
		
		return mainComposites;
	}
	
	/** View a CSV file in a grid */
	private void createViewCsvTab(Composite viewCsvTabComposite) {
		
		final FileControl viewCsvFileControl = addFileControl(viewCsvTabComposite, "CSV file", p.getViewCsvFileControl());
		
		final Composite csvButtonsComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		ui.addDebug(csvButtonsComposite);
		csvButtonsComposite.setLayoutData(ui.createFillHorizontalGridData());
		csvButtonsComposite.setLayout(ui.createColumnsSpacingGridLayout(5, UI.sep8));
		
		final Button csvLoadButton = new Button(csvButtonsComposite, SWT.NONE);
		csvLoadButton.setText("Load");
		csvLoadButton.setLayoutData(ui.createWidth120GridData());
		
		final Text csvLoadLinesText = new Text(csvButtonsComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		csvLoadLinesText.setText("2000000");
		csvLoadLinesText.setLayoutData(ui.createWidth120GridData());

		final Label csvMaxLinesLabel = new Label(csvButtonsComposite, SWT.NONE);
		csvMaxLinesLabel.setLayoutData(ui.createWidth120GridData());
		csvMaxLinesLabel.setText("max lines");

		final Button useFirstLineAsHeaderCheckBox = new Button(csvButtonsComposite, SWT.CHECK);
		GridData useFirstLineAsHeaderCheckBoxGridData = ui.createWidthGridData(200);
		useFirstLineAsHeaderCheckBoxGridData.verticalIndent = 1; // Perfectionist
		useFirstLineAsHeaderCheckBox.setLayoutData(useFirstLineAsHeaderCheckBoxGridData);
		useFirstLineAsHeaderCheckBox.setText("Use first line as header");
		useFirstLineAsHeaderCheckBox.setSelection(true);
		
		final Button csvStopLoadButton = new Button(csvButtonsComposite, SWT.NONE);
		csvStopLoadButton.setText("Stop");
		csvStopLoadButton.setEnabled(false);
		csvStopLoadButton.setLayoutData(ui.createWidth120GridData());

		
//		final Button csvExtractButton = new Button(csvButtonsComposite, SWT.NONE);
//		csvExtractButton.setText("Extract");
//		csvExtractButton.setLayoutData(createWidth120GridData());

		
		final Composite csvStatusComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		csvStatusComposite.setBackground(whiteColor);
		csvStatusComposite.setLayoutData(ui.createFillHorizontalGridData());
		GridLayout csvStatusCompositeGridLayout = ui.createColumnsGridLayout(2);
		csvStatusCompositeGridLayout.marginWidth = UI.sep8;
		csvStatusCompositeGridLayout.marginHeight = UI.sep8;
		csvStatusComposite.setLayoutData(ui.createFillHorizontalGridData());
		csvStatusComposite.setLayout(csvStatusCompositeGridLayout);

		final Label csvStatusLabel = new Label(csvStatusComposite, SWT.NONE);
		csvStatusLabel.setBackground(whiteColor);
		csvStatusLabel.setLayoutData(ui.createFillHorizontalGridData());
		csvStatusLabel.setText("Status");

		
		final Grid csvFileGrid = new Grid(viewCsvTabComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		csvFileGrid.setLayoutData(ui.createFillBothGridData());
		csvFileGrid.setHeaderVisible(true);
		//csvFileGrid.setAutoHeight(true);
		csvFileGrid.setLinesVisible(true);
		
		/* Events */
		final ArrayList<String[]> csvFileGridLines = new ArrayList<>();
		final ArrayList<String> csvFileGridHeader = new ArrayList<>();
		
		/* Virtual load line */
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
			}
		});
		
		/* Load */
		csvLoadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				csvLoadButton.setEnabled(false);
				csvStopLoadButton.setEnabled(true);
				
				csvFileGrid.clearItems();
				csvFileGrid.disposeAllItems();
				csvFileGridLines.clear();
				while (csvFileGrid.getColumnCount() > 0) {
					csvFileGrid.getColumns()[0].dispose();
				}
				csvFileGridHeader.clear();
				
				long maxLines = Long.parseLong(csvLoadLinesText.getText());
				boolean useFirstLineAsHeader = useFirstLineAsHeaderCheckBox.getSelection();
				
				String csvCompleteFileName = viewCsvFileControl.getCompleteFileName();
				
				Thread thread = new Thread() {
				    public void run() {
				        
				    	long start = System.currentTimeMillis();
				    	
				    	doLoop.set(true);
						StaticCatalogEngine.loadViewCsv(csvCompleteFileName, csvFileGridLines, csvFileGridHeader,
								maxLines, useFirstLineAsHeader,
								doLoop,
						new LoopProgress() {
							@Override
							public void doProgress(String progressMessage) {
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										csvStatusLabel.setText(progressMessage);
										Display.getDefault().readAndDispatch();
									}
								});
							}
						});

						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								
								csvStopLoadButton.setEnabled(false);
								
								GridColumn fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
							    fieldGridColumn.setWidth(50);
							    fieldGridColumn.setText("Index");
							    fieldGridColumn.setAlignment(SWT.RIGHT);
								for (String columnName : csvFileGridHeader) {
									fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
								    fieldGridColumn.setWordWrap(true);
								    fieldGridColumn.setText(columnName);
								}
								for (GridColumn gridColumn : csvFileGrid.getColumns()) {
									gridColumn.pack();
									gridColumn.setWidth(gridColumn.getWidth() + 24);
								}
								
								int linesCount = csvFileGridLines.size();
								csvFileGrid.setItemCount(linesCount);
								csvStatusLabel.setText(linesCount + " lines done load in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
								
								csvLoadButton.setEnabled(true);
							}
						});
				    }
				};
				thread.start();
			}
		});

		/* Stop loading */
		csvStopLoadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				doLoop.set(false);
			}
		});
	}

	/** Analyze a CSV file for generation rules */
	private void createAnalyzeCsvTab(Composite parentComposite) {
		
		/*
		 * TODO 
		 * 
		 */
	    
	    final FileControl analyzeCsvFileControl = addFileControl(parentComposite, "Analyze file", p.getAnalizeCsvFileControl());
		
		final Composite csvButtonsComposite = new Composite(parentComposite, SWT.NONE);
		ui.addDebug(csvButtonsComposite);
		csvButtonsComposite.setLayoutData(ui.createFillHorizontalGridData());
		csvButtonsComposite.setLayout(ui.createColumnsSpacingGridLayout(7, UI.sep8));
		
		final Button csvAnalyzeButton = new Button(csvButtonsComposite, SWT.NONE);
		csvAnalyzeButton.setText("Analyze");
		csvAnalyzeButton.setLayoutData(ui.createWidth120GridData());
		
		final Text typeMaxExceptionsText = new Text(csvButtonsComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		typeMaxExceptionsText.setText("1");
		typeMaxExceptionsText.setLayoutData(ui.createWidthGridData(30));

		final Label typeMaxExceptionsLabel = new Label(csvButtonsComposite, SWT.NONE);
		typeMaxExceptionsLabel.setLayoutData(ui.createWidthGridData(210));
		typeMaxExceptionsLabel.setText("maximum field type exception values");

		final Text filterElementsMaxDisplayText = new Text(csvButtonsComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		filterElementsMaxDisplayText.setText("500");
		filterElementsMaxDisplayText.setLayoutData(ui.createWidthGridData(40));

		final Label uniqueElementsMaxDisplayLabel = new Label(csvButtonsComposite, SWT.NONE);
		uniqueElementsMaxDisplayLabel.setLayoutData(ui.createWidthGridData(200));
		uniqueElementsMaxDisplayLabel.setText("maximum filter elements to display");

		
		final Button useFirstLineasHeaderCheckBox = new Button(csvButtonsComposite, SWT.CHECK);
		GridData useFirstLineasHeaderCheckBoxGridData = ui.createWidthGridData(200);
		useFirstLineasHeaderCheckBoxGridData.verticalIndent = 1; // Perfectionist
		useFirstLineasHeaderCheckBox.setLayoutData(useFirstLineasHeaderCheckBoxGridData);
		useFirstLineasHeaderCheckBox.setText("Use first line as header");
		useFirstLineasHeaderCheckBox.setSelection(true);

		final Button createFiltersButton = new Button(csvButtonsComposite, SWT.NONE);
		GridData createFiltersButtonGridData = ui.createWidth120GridData();
		createFiltersButtonGridData.horizontalAlignment = SWT.END;
		createFiltersButtonGridData.grabExcessHorizontalSpace = true;
		createFiltersButton.setLayoutData(createFiltersButtonGridData);
		createFiltersButton.setText("Create New Filters");
		
//		final Button csvStopLoadButton = new Button(csvButtonsComposite, SWT.NONE);
//		csvStopLoadButton.setText("Stop");
//		csvStopLoadButton.setEnabled(false);
//		csvStopLoadButton.setLayoutData(createWidth120GridData());
		
		final Composite csvStatusComposite = new Composite(parentComposite, SWT.NONE);
		csvStatusComposite.setBackground(whiteColor);
		csvStatusComposite.setLayoutData(ui.createFillHorizontalGridData());
		GridLayout csvStatusCompositeGridLayout = ui.createColumnsGridLayout(2);
		csvStatusCompositeGridLayout.marginWidth = UI.sep8;
		csvStatusCompositeGridLayout.marginHeight = UI.sep8;
		csvStatusComposite.setLayout(csvStatusCompositeGridLayout);

		final Label csvStatusLabel = new Label(csvStatusComposite, SWT.NONE);
		csvStatusLabel.setBackground(whiteColor);
		csvStatusLabel.setLayoutData(ui.createFillHorizontalGridData());
		csvStatusLabel.setText("Status");

		
		final Grid csvAnalyzeGrid = new Grid(parentComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		csvAnalyzeGrid.setLayoutData(ui.createFillBothGridData());
		csvAnalyzeGrid.setHeaderVisible(true);
		csvAnalyzeGrid.setLinesVisible(true);
		
		GridColumn fieldGridColumn = new GridColumn(csvAnalyzeGrid, SWT.NONE);
		fieldGridColumn.setText("Field");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(250);

		fieldGridColumn = new GridColumn(csvAnalyzeGrid, SWT.NONE);
		fieldGridColumn.setText("Type");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(150);

	    fieldGridColumn = new GridColumn(csvAnalyzeGrid, SWT.NONE);
		fieldGridColumn.setText("Unique elements count");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(150);

	    fieldGridColumn = new GridColumn(csvAnalyzeGrid, SWT.NONE);
		fieldGridColumn.setText("Exceptions");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(100);

	    fieldGridColumn = new GridColumn(csvAnalyzeGrid, SWT.NONE);
		fieldGridColumn.setText("Unique elements");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(300);

	    fieldGridColumn = new GridColumn(csvAnalyzeGrid, SWT.NONE);
		fieldGridColumn.setText("Unique elements distribution");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(300);

		/* Events */
		final ArrayList<String[]> csvAnalyzeGridLines = new ArrayList<String[]>();
		
		csvAnalyzeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				csvAnalyzeGrid.clearItems();
				csvAnalyzeGrid.disposeAllItems();
				csvAnalyzeGridLines.clear();
				
				ArrayList<HashMap<String, Long>> fields = new ArrayList<HashMap<String,Long>>();
				ArrayList<String> fieldNames = new ArrayList<>();
				ArrayList<String> fieldTypes = new ArrayList<>();
				ArrayList<HashMap<String, ArrayList<String>>> fieldTypesExceptionValues = new ArrayList<>();
				 
				StaticCatalogEngine.loadAnalyzeCsv(analyzeCsvFileControl.getCompleteFileName(),
				fields, fieldNames, fieldTypes, fieldTypesExceptionValues,
				500,
				Integer.parseInt(typeMaxExceptionsText.getText()),
				useFirstLineasHeaderCheckBox.getSelection(),
				doLoop,
				new LoopProgress() {
					@Override
					public void doProgress(String progressMessage) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								csvStatusLabel.setText(progressMessage);
								Display.getDefault().readAndDispatch();
							}
						});
					}
				});
				
				int maxDiff = Integer.parseInt(filterElementsMaxDisplayText.getText());
				
				for (int index = 0; index < fieldNames.size(); index++) {
					
					GridItem csvGridItem = new GridItem(csvAnalyzeGrid, SWT.NONE);
					
					csvGridItem.setText(0, fieldNames.get(index));

					String fieldType = fieldTypes.get(index);
					csvGridItem.setData("type", fieldType);
					csvGridItem.setText(1, typeNames.get(fieldType));
					
					int diff = fields.get(index).keySet().size(); 
					csvGridItem.setText(2, diff + "");
					
					if (diff < maxDiff) {
						
						HashMap<String, Long> groups = fields.get(index); 

						if (!fieldType.equals("text")) {
							ArrayList<String> exceps = new ArrayList<>(fieldTypesExceptionValues.get(index).get(fieldType));
							Collections.sort(exceps);

							csvGridItem.setText(3, String.join(", ", exceps));
						}
						
						ArrayList<String> keys = new ArrayList<>(groups.keySet());
						Collections.sort(keys, stringAsNumberComparator);

						csvGridItem.setText(4, String.join(", ", keys));

						
						ArrayList<String> keysValues = new ArrayList<>();
						for (String key : keys) {
							keysValues.add(key + " (" + groups.get(key) + ") ");
						}
						
						csvGridItem.setText(5, String.join(", ", keysValues));
					}
				}
			}
		});
		
		/** Create and open new filters */
		createFiltersButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {

				/* Create filter fields */
				StaticCatalogFilters staticCatalogFilters = new StaticCatalogFilters();
				for (GridItem gridItem : csvAnalyzeGrid.getItems()) {
					
					StaticCatalogField staticCatalogField = new StaticCatalogField();
					String name = gridItem.getText(0);
					staticCatalogField.setName(name);
					staticCatalogField.setType((String) gridItem.getData("type"));
					
					staticCatalogField.setIsFilter(false);
					staticCatalogField.setLabel(StaticCatalogEngine.makeLabel(name));
					
					staticCatalogFilters.getFields().add(staticCatalogField);
				}
				
				loadFilters.loadFilters(staticCatalogFilters);
				tabButtons.changeTab(2);
			}
		});
	}
	
	/** Create filters generation file based on the analysis */
	private void createCreateFiltersTab(Composite parentComposite) {
		
		/*
		 * TODO 
		 * 
		 */
	    
	    final FileControl filtersFileControl = addFileControl(parentComposite, "Filters file", p.getFiltersFileControl());

		
		final Composite csvButtonsComposite = new Composite(parentComposite, SWT.NONE);
		ui.addDebug(csvButtonsComposite);
		csvButtonsComposite.setLayoutData(ui.createFillHorizontalGridData());
		csvButtonsComposite.setLayout(ui.createColumnsSpacingGridLayout(5, UI.sep8));

		final Button loadFiltersButton = new Button(csvButtonsComposite, SWT.NONE);
		loadFiltersButton.setText("Load");
		loadFiltersButton.setLayoutData(ui.createWidth120GridData());

		final Button saveFiltersButton = new Button(csvButtonsComposite, SWT.NONE);
		saveFiltersButton.setText("Save");
		saveFiltersButton.setLayoutData(ui.createWidth120GridData());
		
		
		final Grid filtersGrid = new Grid(parentComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		filtersGrid.setLayoutData(ui.createFillBothGridData());
		filtersGrid.setHeaderVisible(true);
		filtersGrid.setLinesVisible(true);
		filtersGrid.setSelectionEnabled(false);
		
		GridColumn fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
		fieldGridColumn.setText("Index");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setAlignment(SWT.RIGHT);
	    fieldGridColumn.setWidth(75);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
		fieldGridColumn.setText("Field");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(250);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
		fieldGridColumn.setText("Type");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(150);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.CHECK | SWT.CENTER);
		fieldGridColumn.setText("Use as Filter");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(100);
	    fieldGridColumn.setCheckable(true);
	    
		fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
		fieldGridColumn.setText("Label");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(250);

	    loadFilters = new LoadFilters() {
			@Override
			public void loadFilters(StaticCatalogFilters staticCatalogFilters) {

				for (GridItem gridItem : filtersGrid.getItems()) {
					((CCombo) gridItem.getData("typeCCombo")).dispose();
					((Text) gridItem.getData("labelText")).dispose();
				}
				filtersGrid.clearItems();
				filtersGrid.disposeAllItems();
				
				int index = 0;
				for (StaticCatalogField staticCatalogField : staticCatalogFilters.getFields()) {
					
					GridItem gridItem = new GridItem(filtersGrid, SWT.NONE);
					
					index++;
					gridItem.setText(0, "" + index);
					
					gridItem.setText(1, staticCatalogField.getName());
					//gridItem.setText(2, staticCatalogField.getType());
					
					CCombo cCombo = new CCombo(filtersGrid, SWT.NONE);
					cCombo.setEditable(false);
					cCombo.setBackground(whiteColor);
					cCombo.setItems(typeNameValues);
					cCombo.setText(typeNames.get(staticCatalogField.getType()));
					
				    GridEditor cComboGridEditor = new GridEditor(filtersGrid);
				    cComboGridEditor.minimumWidth = 50;
				    cComboGridEditor.grabHorizontal = true;
				    cComboGridEditor.setEditor(cCombo, gridItem, 2);
				    gridItem.setData("typeCCombo", cCombo);
				    
					gridItem.setChecked(3, staticCatalogField.getIsFilter());
					
					Text labelText = new Text(filtersGrid, SWT.NONE);
					labelText.setText(staticCatalogField.getLabel());

					GridEditor labelTextGridEditor = new GridEditor(filtersGrid);
					labelTextGridEditor.minimumWidth = 50;
					labelTextGridEditor.grabHorizontal = true;
					labelTextGridEditor.setEditor(labelText, gridItem, 4);
					gridItem.setData("labelText", labelText);
				}
			}
		};
		
		
		/* Events */
		loadFiltersButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
			
				String jsonSer = null;
				try {
					jsonSer = new String(Files.readAllBytes(Paths.get(filtersFileControl.getCompleteFileName())), StandardCharsets.UTF_8);
				}
				catch (IOException ioException) {
					L.e("JSON load", ioException);
				}
				
				StaticCatalogFilters loadedFilters = JSON.parseObject(jsonSer, StaticCatalogFilters.class);
				loadFilters.loadFilters(loadedFilters);
			}
		});
		
		saveFiltersButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				StaticCatalogFilters filtersDefinition = new StaticCatalogFilters();
				
				for (GridItem gridItem : filtersGrid.getItems()) {

					StaticCatalogField field = new StaticCatalogField();
					field.setName(gridItem.getText(1));
					field.setType(nameTypes.get(((CCombo) gridItem.getData("typeCCombo")).getText()));
					field.setIsFilter(gridItem.getChecked(3));
					field.setLabel(((Text) gridItem.getData("labelText")).getText());
					filtersDefinition.getFields().add(field);
				}
					
				String jsonSer = JSON.toJSONString(filtersDefinition, true);
				
				try {
					Files.write(Paths.get(filtersFileControl.getCompleteFileName()), jsonSer.getBytes(StandardCharsets.UTF_8));
				} catch (IOException ioException) {
					L.e("Error writing file", ioException);
				}
			}
		});
	}
}
