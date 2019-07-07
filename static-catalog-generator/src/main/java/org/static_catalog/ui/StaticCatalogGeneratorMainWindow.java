/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.FileControlRecentsCombo;
import org.eclipse.swt.FileControlRecentsComboSelectionEvent;
import org.eclipse.swt.PopupComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTFontUtils;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.static_catalog.engine.StaticCatalogEngine;
import org.static_catalog.main.P;
import org.static_catalog.main.S;
import org.static_catalog.main.U;
import org.static_catalog.model.src.StaticCatalogConfigurationField;
import org.static_catalog.model.src.StaticCatalogConfigurationFields;
import org.static_catalog.model.src.StaticCatalogExamineField;
import org.static_catalog.model.src.StaticCatalogExamineFields;

/** Generator main window */
public class StaticCatalogGeneratorMainWindow {

	/** Type names */
	public static final LinkedHashMap<String, String> typeNames = new LinkedHashMap<>();
	public static final LinkedHashMap<String, String> nameTypes = new LinkedHashMap<>();
	static {
		typeNames.put(StaticCatalogEngine.TYPE_LONG, "Integer");
		typeNames.put(StaticCatalogEngine.TYPE_DOUBLE, "Real");
		typeNames.put(StaticCatalogEngine.TYPE_DATE, "Date");
		typeNames.put(StaticCatalogEngine.TYPE_TEXT, "Text");
		
		for (Entry<String, String> entry : typeNames.entrySet()) {
			nameTypes.put(entry.getValue(), entry.getKey());
		}
	}
	public static final String[] typeNameValues = typeNames.values().toArray(new String[typeNames.values().size()]);

	/** Filter type names */
	public static final LinkedHashMap<String, String> filterTypeNames = new LinkedHashMap<>();
	public static final LinkedHashMap<String, String> filterNameTypes = new LinkedHashMap<>();
	static {
		filterTypeNames.put(StaticCatalogEngine.FILTER_TYPE_NONE, "");
		filterTypeNames.put(StaticCatalogEngine.FILTER_TYPE_VALUES, "Values");
		filterTypeNames.put(StaticCatalogEngine.FILTER_TYPE_VALUE_INTERVALS, "Value Intervals");
		filterTypeNames.put(StaticCatalogEngine.FILTER_TYPE_LENGTH_INTERVALS, "Length Intervals");
		filterTypeNames.put(StaticCatalogEngine.FILTER_TYPE_KEYWORDS, "Keywords");
		
		for (Entry<String, String> entry : filterTypeNames.entrySet()) {
			filterNameTypes.put(entry.getValue(), entry.getKey());
		}
	}
	public static final String[] filterTypeNameValues = filterTypeNames.values().toArray(new String[filterTypeNames.values().size()]);

	/** Display type names */
	public static final LinkedHashMap<String, String> displayTypeNames = new LinkedHashMap<>();
	public static final LinkedHashMap<String, String> displayNameTypes = new LinkedHashMap<>();
	static {
		displayTypeNames.put(StaticCatalogEngine.DISPLAY_TYPE_NONE, "");
		displayTypeNames.put(StaticCatalogEngine.DISPLAY_TYPE_CHECKBOXES, "Checkboxes");
		displayTypeNames.put(StaticCatalogEngine.DISPLAY_TYPE_DROPDOWN, "Dropdown");
		displayTypeNames.put(StaticCatalogEngine.DISPLAY_TYPE_RADIOBUTTONS, "Radio Buttons");
		displayTypeNames.put(StaticCatalogEngine.DISPLAY_TYPE_SEARCHBOX, "Search Box");
		
		for (Entry<String, String> entry : displayTypeNames.entrySet()) {
			displayNameTypes.put(entry.getValue(), entry.getKey());
		}
	}
	public static final String[] displayTypeNameValues = displayTypeNames.values().toArray(new String[displayTypeNames.values().size()]);

	
	private static final String[] filterExtensionsCsv = { "*.csv", "*.dat", "*.txt", "*.*" };
	private static final String[] filterExtensionsJson = { "*.json", "*.*" };
	private static final String[] filterExtensionsLiquid = { "*.liquid", "*.html", "*.txt", "*.*" };

	private static final String[] filterNamesCsv = { "Comma separated values (*.csv)", "Comma separated values (*.dat)", "Comma separated values (*.txt)", "All files (*.*)" };
	private static final String[] filterNamesJson = { "Filters file (*.json)", "All files (*.*)" };
	private static final String[] filterNamesLiquid = { "Liquid template (*.liquid)", "Liquid template (*.html)", "Liquid template (*.txt)", "All files (*.*)" };
	
	/** File control */
	private interface FileControl {
		
		public String getCompleteFileName();
	}
	
	/** Progress */
	public class LoopProgress {
		/** Status message */
		public void doProgress(String progressMessage) {
			/* ILB */
		};
		/** Status message and line */
		public void doProgress(String progressMessage, String stepMessage) {
			/* ILB */
		};
	}

	/** Change tab */
	private interface TabButtons {
		
		public void changeTab(int selectedButtonIndex);
	}

	/** Load filters */
	private interface LoadFilters {
		
		public void loadFilters(StaticCatalogConfigurationFields staticCatalogFilters);
	}

	/** Root folder */
	private String applicationRootFolder;

	/** Properties */
	private P p;

	/** New */
	public StaticCatalogGeneratorMainWindow(String applicationRootFolder) {
		super();
		this.applicationRootFolder = applicationRootFolder;
		
		p = P.load(this, applicationRootFolder + "/conf/static-catalog.config.json");
	}

//	/** Concurrent */
//	private AtomicBoolean doLoop = new AtomicBoolean(true);
	
	/** Main display */
	private Display display;

	/** UI (SWT) */
	private UI ui;
	
	/* Fonts */
	public static Font fontNormal;
	public static Font fontBold;
	public static Font fontBigger;
	public static Font fontRecents;
	public static Font fontSmaller;
	public static Font fontMonospaced;
	
	/* Colors */
	public static Color whiteColor;
//	private Color gridBackgroundColor;
//	private Color gridTextColor;
//	private Color gridSelectedBackgroundColor;
//	private Color gridSelectedTextColor;

	
	/** Change */
	private TabButtons tabButtons;
	
	/** Load filters */
	private LoadFilters loadFilters;
	
	/** Field edit window */
	private Grid filtersGrid;
	private StaticCatalogGeneratorFieldEditWindow fieldEditWindow = new StaticCatalogGeneratorFieldEditWindow();
	
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
//		Display.setAppName("static-catalog");
		display = new Display();
		ui = new UI(false, display);

		/* Main window */
		Shell mainShell = new Shell(display);
		mainShell.setText("static-catalog Generator");
	    mainShell.setLayout(ui.createMarginsVerticalSpacingGridLayout(UI.sep, UI.sep));

		/* Icon */
		String[] rez = { "16", "24", "32", "48" };
		Image[] iconImages = new Image[rez.length];
		for (int index = 0; index < rez.length; index++) {
			String rezimg = rez[index];
			iconImages[index] = ui.getResourceAsImage("org/static_catalog/res/icon/neutral/" + rezimg + "x" + rezimg + ".png");
		}
		mainShell.setImages(iconImages);
		
		/* Location */
		mainShell.setLocation(250, 0);
		mainShell.setSize(display.getClientArea().width - 600, display.getClientArea().height);

		/* Fonts */
		fontNormal = mainShell.getFont();
		int fontNormalHeight = fontNormal.getFontData()[0].getHeight();
		fontBold = ui.newFontAttributes(fontNormal, SWT.BOLD);
		fontBigger = ui.newFontSize(fontBold, fontNormalHeight + 5);
		fontRecents = ui.newFontSize(fontNormal,fontNormalHeight  + 1);
		fontSmaller = ui.newFontSize(fontNormal,fontNormalHeight - 3);
		fontMonospaced = SWTFontUtils.getMonospacedFont(display);

		/* Colors */
		whiteColor = new Color(display, 255, 255, 255);
//		gridBackgroundColor = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
//		gridTextColor = display.getSystemColor(SWT.COLOR_LIST_FOREGROUND);
//		gridSelectedBackgroundColor = display.getSystemColor(SWT.COLOR_LIST_SELECTION);
//		gridSelectedTextColor = display.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);
		
		/* Menu */
	    Menu mainMenuBar = new Menu(mainShell, SWT.BAR);
	    
	    MenuItem fileMenuHeader = new MenuItem(mainMenuBar, SWT.CASCADE);
	    fileMenuHeader.setText("\u2630");
	    
	    Menu fileMenu = new Menu(mainShell, SWT.DROP_DOWN);
	    fileMenuHeader.setMenu(fileMenu);
	    
//	    MenuItem settingsMenuItem = new MenuItem(fileMenu, SWT.NONE);
//	    settingsMenuItem.setText("Settings");
//
//	    new MenuItem(fileMenu, SWT.SEPARATOR);

	    MenuItem homeMenuItem = new MenuItem(fileMenu, SWT.NONE);
	    homeMenuItem.setText("Home");
	    homeMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				Program.launch("https://static-catalog.org");
			}
		});

	    MenuItem repoMenuItem = new MenuItem(fileMenu, SWT.NONE);
	    repoMenuItem.setText("GitHub Repo");
	    repoMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				Program.launch("https://github.com/iuscl-ide/static-catalog");
			}
		});

	    new MenuItem(fileMenu, SWT.SEPARATOR);
	    
	    MenuItem exitMenuItem = new MenuItem(fileMenu, SWT.NONE);
	    exitMenuItem.setText("Exit");
	    exitMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				mainShell.close();
			}
		});

	    mainShell.setMenuBar(mainMenuBar);

	    /* Tabs */
	    //isDebug = true;
	    
	    ArrayList<Composite> mainComposites = createMainTabs(mainShell);

	    /* View CSV */
		createViewCsvTab(mainComposites.get(0));
	    /* Examine CSV */
		createExamineCsvTab(mainComposites.get(1));
	    /* Filters */
		createFiltersTab(mainComposites.get(2));
	    /* Generate */
		createGenerateTab(mainComposites.get(3));
		
		/* Field edit */
		fieldEditWindow.createFieldEditWindow(mainShell, new StaticCatalogGeneratorCallback() {
			@Override
			public void doCallback() {

				StaticCatalogConfigurationFields configurationFields = ((StaticCatalogConfigurationFields) filtersGrid.getData("StaticCatalogConfigurationFields"));
				loadFilters.loadFilters(configurationFields);
			}
		});
		
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
	private FileControl addFileControl(Composite parentComposite, String labelText, String fileType, StaticCatalogFileControlProperties p) {
		
		return addFileControl(parentComposite, labelText, fileType, false, p);
	}
	
	/** Create file control */
	private FileControl addFileControl(Composite parentComposite, String labelText, String fileType, boolean isFolder, StaticCatalogFileControlProperties p) {
		
		final Composite fileComposite = new Composite(parentComposite, SWT.NONE);
		ui.addDebug(fileComposite);
	    fileComposite.setLayoutData(ui.createFillHorizontalGridData());
		fileComposite.setLayout(ui.createColumnsSpacingGridLayout(4, UI.sep));
		
		final Label fileLabel = new Label(fileComposite, SWT.NONE);
		fileLabel.setText(labelText);
		fileLabel.setLayoutData(ui.createWidthButtonGridData());
		
		//final FileControlRecentsCombo recentFilesCombo = new FileControlRecentsCombo(fileComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		final FileControlRecentsCombo recentFilesCombo = new FileControlRecentsCombo(fileComposite, SWT.SINGLE | SWT.V_SCROLL);
		GridData recentFilesComboGridData = ui.createWidthGridData(recentFilesCombo.findButtonWidth());
		//recentFilesComboGridData.heightHint = 10;
		recentFilesCombo.setLayoutData(recentFilesComboGridData);
		recentFilesCombo.setEditable(false);
		recentFilesCombo.setVisibleItemCount(8);
		recentFilesCombo.setFont(fontRecents);

		final Text fileText = new Text(fileComposite, SWT.SINGLE | SWT.BORDER);
		fileText.setText(p.getFileName());
		fileText.setLayoutData(ui.createFillHorizontalGridData());

		final Button fileButton = new Button(fileComposite, SWT.NONE);
		fileButton.setText("Browse");
		fileButton.setLayoutData(ui.createWidthButtonGridData());
		
		int recentFileNamesSize = p.getRecentFileNames().size(); 
		if (recentFileNamesSize == 0) {
			recentFilesCombo.add(StaticCatalogFileControlProperties.NO_RECENT_FILES);
		}
		else {
			recentFilesCombo.setItems(p.getRecentFileNames().toArray(new String[recentFileNamesSize]));
		}
		
		/* Recent files */
		recentFilesCombo.addRecentsSelectionEvent(new FileControlRecentsComboSelectionEvent() {
			@Override
			public void doEvent() {

				String recentFileName = recentFilesCombo.getItem(recentFilesCombo.getSelectionIndex());
				if (recentFileName.equals(StaticCatalogFileControlProperties.NO_RECENT_FILES)) {
					return;
				}

				if (!recentFilesCombo.getListVisible()) {
					p.setFileName(recentFileName);
					fileText.setText(recentFileName);
					
					ArrayList<String> recentFileNames = p.getRecentFileNames(); 
					recentFileNames.remove(recentFileName);
					recentFileNames.add(0, recentFileName);
					recentFilesCombo.setItems(p.getRecentFileNames().toArray(new String[recentFileNames.size()]));
					
					p.save();
				}
			}
		});
		
		/* File name */
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
					recentFilesCombo.setItems(p.getRecentFileNames().toArray(new String[recentFileNames.size()]));
					
					p.save();
				}
			}
		});
		
		/* Browse */
		fileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {

				String oldFileName = fileText.getText();
				String newFileName = null;
				if (isFolder) {
					DirectoryDialog directoryDialog = new DirectoryDialog(parentComposite.getShell(), SWT.NONE);
					directoryDialog.setText(labelText);
					if (oldFileName.trim().length() == 0) {
						directoryDialog.setFilterPath(applicationRootFolder);	
					}
					else {
						directoryDialog.setFilterPath(oldFileName);	
					}
					newFileName = directoryDialog.open();
				}
				else {
					FileDialog fileDialog = new FileDialog(parentComposite.getShell(), SWT.NONE);
					fileDialog.setText(labelText);
					if (oldFileName.trim().length() == 0) {
						fileDialog.setFileName(applicationRootFolder);	
					}
					else {
						fileDialog.setFileName(oldFileName);	
					}
					
					if (fileType.equalsIgnoreCase("csv")) {
						fileDialog.setFilterExtensions(filterExtensionsCsv);
						fileDialog.setFilterNames(filterNamesCsv);
					}

					if (fileType.equalsIgnoreCase("json")) {
						fileDialog.setFilterExtensions(filterExtensionsJson);
						fileDialog.setFilterNames(filterNamesJson);
					}

					if (fileType.equalsIgnoreCase("liquid")) {
						fileDialog.setFilterExtensions(filterExtensionsLiquid);
						fileDialog.setFilterNames(filterNamesLiquid);
					}
					
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
					recentFilesCombo.setItems(p.getRecentFileNames().toArray(new String[recentFileNames.size()]));
					
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
	    topButtonsComposite.setLayout(ui.createColumnsSpacingGridLayout(4, UI.sep));
	    
	    final String[] topButtonTexts = { "View CSV", "Examine CSV", "Fields && Filters", "Generate" };
	    
	    ArrayList<Button> topButtons = new ArrayList<>(); 

	    for (String topButtonText : topButtonTexts) {

		    Button button = new Button(topButtonsComposite, SWT.TOGGLE);
		    button.setText(topButtonText);
		    button.setLayoutData(ui.createWidthButtonGridData());
		    
		    topButtons.add(button);
	    }
	    
	    final Label separator = new Label(mainShell, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(ui.createFillHorizontalGridData());
	    
	    final ArrayList<Composite> mainComposites = new ArrayList<>(); 
	    
	    for (String topButtonText : topButtonTexts) {

	    	final Composite composite = new Composite(mainShell, SWT.NONE);
	    	ui.addDebug(composite);
		    composite.setLayoutData(ui.createFillBothGridData());
		    composite.setLayout(ui.createVerticalSpacingGridLayout(UI.sep));
	    	
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
		
		final FileControl viewCsvFileControl = addFileControl(viewCsvTabComposite, "CSV file", "csv", p.getViewCsvFileControl());
		
		final Composite buttonsComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		ui.addDebug(buttonsComposite);
		buttonsComposite.setLayoutData(ui.createFillHorizontalGridData());
		buttonsComposite.setLayout(ui.createColumnsSpacingGridLayout(2, UI.sep));
		
		final Button csvLoadButton = new Button(buttonsComposite, SWT.NONE);
		csvLoadButton.setText("Load");
		csvLoadButton.setLayoutData(ui.createWidthButtonGridData());

		final Composite buttonsMiddleComposite = new Composite(buttonsComposite, SWT.NONE);
		buttonsMiddleComposite.setLayoutData(ui.createFillHorizontalGridData());
		buttonsMiddleComposite.setLayout(ui.createColumnsSpacingGridLayout(3, UI.sep));
		
		final Button useFirstLineAsHeaderCheckBox = new Button(buttonsMiddleComposite, SWT.CHECK);
//		GridData useFirstLineAsHeaderCheckBoxGridData = ui.createWidthGridData(140);
//		useFirstLineAsHeaderCheckBoxGridData.verticalIndent = 1; // Perfectionist
//		useFirstLineAsHeaderCheckBox.setLayoutData(useFirstLineAsHeaderCheckBoxGridData);
		useFirstLineAsHeaderCheckBox.setText("Use first line as header");
		useFirstLineAsHeaderCheckBox.setSelection(p.getViewCsvUseFirstLineAsHeader());
		useFirstLineAsHeaderCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				p.setViewCsvUseFirstLineAsHeader(useFirstLineAsHeaderCheckBox.getSelection());
				p.save();
			}
		});

		final Text loadLinesText = new Text(buttonsMiddleComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		loadLinesText.setLayoutData(ui.createWidthGridData(100));
		loadLinesText.setText(p.getViewCsvMaxLines() + "");
		loadLinesText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent focusEvent) {
				
				int viewCsvMaxLines = p.getViewCsvMaxLines();
				try {
					viewCsvMaxLines = Integer.parseInt(loadLinesText.getText().trim());
				}
				catch (NumberFormatException numberFormatException) {
					loadLinesText.setText(viewCsvMaxLines + "");
				}		
				p.setViewCsvMaxLines(viewCsvMaxLines);		
				p.save();
			}
		});

		final Label maxLinesLabel = new Label(buttonsMiddleComposite, SWT.NONE);
//		csvMaxLinesLabel.setLayoutData(ui.createWidthGridData(60));
		maxLinesLabel.setText("max lines");

		final Composite statusComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		statusComposite.setBackground(whiteColor);
		statusComposite.setLayoutData(ui.createFillHorizontalGridData());
		GridLayout statusCompositeGridLayout = ui.createColumnsGridLayout(2);
		statusCompositeGridLayout.marginWidth = UI.sep;
		statusCompositeGridLayout.marginHeight = UI.sep;
		statusComposite.setLayoutData(ui.createFillHorizontalGridData());
		statusComposite.setLayout(statusCompositeGridLayout);

		final Label statusLabel = new Label(statusComposite, SWT.NONE);
		statusLabel.setBackground(whiteColor);
		statusLabel.setLayoutData(ui.createFillHorizontalGridData());
		statusLabel.setText("Status");

		
//		final Grid csvFileGrid = new Grid(viewCsvTabComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		final Grid fileGrid = new Grid(viewCsvTabComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		fileGrid.setLayoutData(ui.createFillBothGridData());
		fileGrid.setHeaderVisible(true);
		//csvFileGrid.setAutoHeight(true);
		fileGrid.setLinesVisible(true);
		
		/* Events */
		final ArrayList<String[]> fileGridLines = new ArrayList<>();
		final ArrayList<String> fileGridHeader = new ArrayList<>();
		
		/* Load */
		csvLoadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
		    	long start = System.currentTimeMillis();
		    	
				fileGrid.clearItems();
				fileGrid.disposeAllItems();
				fileGridLines.clear();
				while (fileGrid.getColumnCount() > 0) {
					fileGrid.getColumns()[0].dispose();
				}
				fileGridHeader.clear();
				
				long maxLines = Long.parseLong(loadLinesText.getText());
				boolean useFirstLineAsHeader = useFirstLineAsHeaderCheckBox.getSelection();
				
				String csvCompleteFileName = viewCsvFileControl.getCompleteFileName();

				long totalLines = StaticCatalogEngine.loadViewCsv(csvCompleteFileName, fileGridLines, fileGridHeader,
				maxLines, useFirstLineAsHeader,
				new LoopProgress() {
					@Override
					public void doProgress(String progressMessage) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								statusLabel.setText(progressMessage);
								Display.getDefault().readAndDispatch();
							}
						});
					}
				});

				GridColumn fieldGridColumn = new GridColumn(fileGrid, SWT.NONE);
			    fieldGridColumn.setWidth(50);
			    fieldGridColumn.setText("Index");
			    fieldGridColumn.setAlignment(SWT.RIGHT);
				for (String columnName : fileGridHeader) {
					fieldGridColumn = new GridColumn(fileGrid, SWT.NONE);
				    fieldGridColumn.setWordWrap(true);
				    fieldGridColumn.setText(columnName);
				}

				for (GridColumn gridColumn : fileGrid.getColumns()) {
					gridColumn.pack();
					gridColumn.setWidth(gridColumn.getWidth() + 24);
				}

				int linesCount = fileGridLines.size();
				long csvLineIndex = 0;
				for (String[] csvLine : fileGridLines) {
					GridItem csvGridItem = new GridItem(fileGrid, SWT.NONE);
					csvLineIndex++;
					csvGridItem.setText(0, csvLineIndex + "");
					for (int index = 0; index < csvLine.length; index++) {
						csvGridItem.setText(index + 1, csvLine[index] + "");
					}
				}

				if (linesCount < 1500) {
					for (GridColumn gridColumn : fileGrid.getColumns()) {
						gridColumn.pack();
						gridColumn.setWidth(gridColumn.getWidth() + 24);
					}
				}
				
				statusLabel.setText(U.w(linesCount) + " lines (of total " + U.w(totalLines) + ") done loading in " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
			}
		});
	}

	/** Examine a CSV file for generation rules */
	private void createExamineCsvTab(Composite parentComposite) {
		
	    final FileControl examineCsvFileControl = addFileControl(parentComposite, "Examine file", "csv", p.getExamineCsvFileControl());
		
		final Composite buttonsComposite = new Composite(parentComposite, SWT.NONE);
		ui.addDebug(buttonsComposite);
		buttonsComposite.setLayoutData(ui.createFillHorizontalGridData());
		buttonsComposite.setLayout(ui.createColumnsSpacingGridLayout(3, UI.sep));
		
		final Button examineButton = new Button(buttonsComposite, SWT.NONE);
		examineButton.setText("Examine");
		examineButton.setLayoutData(ui.createWidthButtonGridData());

		final Composite buttonsMiddleComposite = new Composite(buttonsComposite, SWT.NONE);
		buttonsMiddleComposite.setLayoutData(ui.createFillHorizontalGridData());
		buttonsMiddleComposite.setLayout(ui.createColumnsSpacingGridLayout(9, UI.sep));
		
		final Button useFirstLineAsHeaderCheckBox = new Button(buttonsMiddleComposite, SWT.CHECK);
//		GridData useFirstLineasHeaderCheckBoxGridData = ui.createWidthGridData(140);
//		useFirstLineasHeaderCheckBoxGridData.verticalIndent = 1; // Perfectionist
//		useFirstLineAsHeaderCheckBox.setLayoutData(useFirstLineasHeaderCheckBoxGridData);
		useFirstLineAsHeaderCheckBox.setText("Use first line as header");
		useFirstLineAsHeaderCheckBox.setSelection(p.getExamineCsvUseFirstLineasHeader());
		useFirstLineAsHeaderCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				p.setExamineCsvUseFirstLineasHeader(useFirstLineAsHeaderCheckBox.getSelection());
				p.save();
			}
		});

		
		final Text typeMaxExceptionsText = new Text(buttonsMiddleComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		typeMaxExceptionsText.setLayoutData(ui.createWidthGridData(25));
		typeMaxExceptionsText.setText(p.getExamineCsvTypeMaxExceptions() + "");
		typeMaxExceptionsText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent focusEvent) {
				
				int typeMaxExceptions = p.getExamineCsvTypeMaxExceptions();
				try {
					typeMaxExceptions = Integer.parseInt(typeMaxExceptionsText.getText().trim());
				}
				catch (NumberFormatException numberFormatException) {
					typeMaxExceptionsText.setText(typeMaxExceptions + "");
				}		
				p.setExamineCsvTypeMaxExceptions(typeMaxExceptions);		
				p.save();
			}
		});
		final Label typeMaxExceptionsLabel = new Label(buttonsMiddleComposite, SWT.NONE);
//		typeMaxExceptionsLabel.setLayoutData(ui.createWidthGridData(170));
		typeMaxExceptionsLabel.setText("max type exception values");

		
		final Text filterElementsMaxText = new Text(buttonsMiddleComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		filterElementsMaxText.setLayoutData(ui.createWidthGridData(25));
		filterElementsMaxText.setText(p.getExamineCsvFilterElementsMax() + "");
		filterElementsMaxText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent focusEvent) {
				
				int filterElementsMax = p.getExamineCsvFilterElementsMax();
				try {
					filterElementsMax = Integer.parseInt(filterElementsMaxText.getText().trim());
				}
				catch (NumberFormatException numberFormatException) {
					filterElementsMaxText.setText(filterElementsMax + "");
				}		
				p.setExamineCsvFilterElementsMax(filterElementsMax);		
				p.save();
			}
		});
		final Label filterElementsMaxLabel = new Label(buttonsMiddleComposite, SWT.NONE);
//		filterElementsMaxLabel.setLayoutData(ui.createWidthGridData(110));
		filterElementsMaxLabel.setText("max filter values");

		
		final Text filterElementsMaxDisplayText = new Text(buttonsMiddleComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		filterElementsMaxDisplayText.setLayoutData(ui.createWidthGridData(25));
		filterElementsMaxDisplayText.setText(p.getExamineCsvFilterElementsMaxDisplay() + "");
		filterElementsMaxDisplayText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent focusEvent) {
				
				int filterElementsMaxDisplay = p.getExamineCsvFilterElementsMaxDisplay();
				try {
					filterElementsMaxDisplay = Integer.parseInt(filterElementsMaxDisplayText.getText().trim());
				}
				catch (NumberFormatException numberFormatException) {
					filterElementsMaxDisplayText.setText(filterElementsMaxDisplay + "");
				}		
				p.setExamineCsvFilterElementsMaxDisplay(filterElementsMaxDisplay);		
				p.save();
			}
		});
		final Label filterElementsMaxDisplayLabel = new Label(buttonsMiddleComposite, SWT.NONE);
//		filterElementsMaxDisplayLabel.setLayoutData(ui.createWidthGridData(150));
		filterElementsMaxDisplayLabel.setText("max display values");

		
		final Text filterElementsMinDisplayText = new Text(buttonsMiddleComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		filterElementsMinDisplayText.setLayoutData(ui.createWidthGridData(25));
		filterElementsMinDisplayText.setText(p.getExamineCsvFilterElementsMinDisplay() + "");
		filterElementsMinDisplayText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent focusEvent) {
				
				int filterElementsMinDisplay = p.getExamineCsvFilterElementsMinDisplay();
				try {
					filterElementsMinDisplay = Integer.parseInt(filterElementsMinDisplayText.getText().trim());
				}
				catch (NumberFormatException numberFormatException) {
					filterElementsMinDisplayText.setText(filterElementsMinDisplay + "");
				}		
				p.setExamineCsvFilterElementsMinDisplay(filterElementsMinDisplay);		
				p.save();
			}
		});
		final Label filterElementsMinDisplayLabel = new Label(buttonsMiddleComposite, SWT.NONE);
//		filterElementsMinDisplayLabel.setLayoutData(ui.createWidthGridData(150));
		filterElementsMinDisplayLabel.setText("min display values");

		
		final Button createFiltersButton = new Button(buttonsComposite, SWT.NONE);
		GridData createFiltersButtonGridData = ui.createWidthButtonGridData();
//		createFiltersButtonGridData.horizontalAlignment = SWT.END;
//		createFiltersButtonGridData.grabExcessHorizontalSpace = true;
		createFiltersButton.setLayoutData(createFiltersButtonGridData);
		createFiltersButton.setText("Create New Filters");

		
		final Composite statusComposite = new Composite(parentComposite, SWT.NONE);
		statusComposite.setBackground(whiteColor);
		statusComposite.setLayoutData(ui.createFillHorizontalGridData());
		GridLayout statusCompositeGridLayout = ui.createColumnsGridLayout(2);
		statusCompositeGridLayout.marginWidth = UI.sep;
		statusCompositeGridLayout.marginHeight = UI.sep;
		statusComposite.setLayout(statusCompositeGridLayout);

		final Label statusLabel = new Label(statusComposite, SWT.NONE);
		statusLabel.setBackground(whiteColor);
		statusLabel.setLayoutData(ui.createFillHorizontalGridData());
		statusLabel.setText("Status");

		final Grid examineGrid = new Grid(parentComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		examineGrid.setLayoutData(ui.createFillBothGridData());
		examineGrid.setHeaderVisible(true);
		examineGrid.setLinesVisible(true);
		
		GridColumn fieldGridColumn = new GridColumn(examineGrid, SWT.NONE);
		fieldGridColumn.setText("Field");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(200);

		fieldGridColumn = new GridColumn(examineGrid, SWT.NONE);
		fieldGridColumn.setText("Type");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(120);

	    fieldGridColumn = new GridColumn(examineGrid, SWT.NONE);
		fieldGridColumn.setText("Unique elements count");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setAlignment(SWT.RIGHT);
	    fieldGridColumn.setWidth(180);

	    fieldGridColumn = new GridColumn(examineGrid, SWT.NONE);
		fieldGridColumn.setText("Exceptions (count)");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(250);

	    fieldGridColumn = new GridColumn(examineGrid, SWT.NONE);
		fieldGridColumn.setText("Unique elements (count)");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(480);

		/* Events */
	    examineGrid.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent mouseEvent) {

				GridItem gridItem = examineGrid.getItem(new Point(mouseEvent.x, mouseEvent.y));
				
				for (int columnIndex = 3; columnIndex <= 4; columnIndex++) {
					if (gridItem.getBounds(columnIndex).contains(mouseEvent.x, mouseEvent.y)) {
						String itemText = gridItem.getText(columnIndex);
						if (itemText.trim().length() == 0) {
							break;
						}
						Point gridP = examineGrid.toDisplay(0, 0);
						final PopupComposite popupComposite = new PopupComposite(parentComposite.getShell(), SWT.NONE, ui);
						popupComposite.setSize(480, 480);
						popupComposite.setLocation(
								gridP.x + ((examineGrid.getSize().x - popupComposite.getSize().x) / 2),
								gridP.y + ((examineGrid.getSize().y - popupComposite.getSize().y) / 2));

						final Text popupCompositeText = new Text(popupComposite, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL);
						popupCompositeText.setBackground(whiteColor);
						String wrap = U.wrap(itemText, 60, ",\n", true, ", ").replace(",\n ", ",\n"); 
						popupCompositeText.setText(wrap);
						popupCompositeText.setLayoutData(ui.createFillBothGridData());
						popupCompositeText.setFont(fontMonospaced);
	
						popupComposite.show(popupComposite.getLocation());
						break;
					}
				}
			}
		});
	    
		final ArrayList<String[]> examineGridLines = new ArrayList<String[]>();
		
		examineButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				examineGrid.clearItems();
				examineGrid.disposeAllItems();
				examineGridLines.clear();
				
				StaticCatalogExamineFields staticCatalogExamine = new StaticCatalogExamineFields();
				
				StaticCatalogEngine.loadExamineCsv(examineCsvFileControl.getCompleteFileName(), staticCatalogExamine,
				Integer.parseInt(filterElementsMaxText.getText()),
				Integer.parseInt(typeMaxExceptionsText.getText()),
				useFirstLineAsHeaderCheckBox.getSelection(),
				new LoopProgress() {
					@Override
					public void doProgress(String progressMessage) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								statusLabel.setText(progressMessage);
								Display.getDefault().readAndDispatch();
							}
						});
					}
				});
				
				int maxDiff = Integer.parseInt(filterElementsMaxText.getText());
				
				ArrayList<StaticCatalogExamineField> examineFields = staticCatalogExamine.getExamineFields();
				
				for (int index = 0; index < examineFields.size(); index++) {
					
					StaticCatalogExamineField  examineField = examineFields.get(index);
					
					GridItem gridItem = new GridItem(examineGrid, SWT.NONE);
					
					gridItem.setText(0, examineField.getName());

					String fieldType = examineField.getType();
					gridItem.setData("type", fieldType);
					gridItem.setText(1, typeNames.get(fieldType));
					
					HashMap<String, Long> uniqueValueCounts = examineField.getUniqueValueCounts();
					
					int diff = uniqueValueCounts.keySet().size(); 
					gridItem.setText(2, diff + "");
					
					if (diff < maxDiff) {
						ArrayList<String> keys = new ArrayList<>(uniqueValueCounts.keySet());
						
						ArrayList<String> exceptionKeys = new ArrayList<>(examineField.getFieldTypesExceptionValues().get(fieldType));
						if (exceptionKeys.size() > 0) {
							StaticCatalogEngine.sortTypeKey("text", keys);
							ArrayList<String> exceptionKeysValues = new ArrayList<>();
							for (String exceptionKey : exceptionKeys) {
								exceptionKeysValues.add(exceptionKey + " (" + uniqueValueCounts.get(exceptionKey) + ")");
							}
							gridItem.setText(3, String.join(", ", exceptionKeysValues));
							
							keys.removeAll(exceptionKeys);
						}

						StaticCatalogEngine.sortTypeKey(fieldType, keys);
						
						ArrayList<String> keysValues = new ArrayList<>();
						for (String key : keys) {
							keysValues.add(key + " (" + uniqueValueCounts.get(key) + ")");
						}
						gridItem.setText(4, String.join(", ", keysValues));
					}
				}
			}
		});
		
		/** Create and open new filters */
		createFiltersButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {

				/* Create filter fields */
				int filterElementsMinDisplay = Integer.parseInt(filterElementsMinDisplayText.getText());
				int filterElementsMaxDisplay = Integer.parseInt(filterElementsMaxDisplayText.getText());
				
				StaticCatalogConfigurationFields staticCatalogFilters = new StaticCatalogConfigurationFields();
				int indexInLine = 0; 
				for (GridItem gridItem : examineGrid.getItems()) {
					
					StaticCatalogConfigurationField staticCatalogField = new StaticCatalogConfigurationField();
					String name = gridItem.getText(0);
					
					staticCatalogField.setCsvIndex(++indexInLine);
					staticCatalogField.setName(name);

					staticCatalogField.setLabel(U.makeLabel(name));

					staticCatalogField.setType((String) gridItem.getData("type"));
					
					String uniqueValuesCount = gridItem.getText(4);
					if ((uniqueValuesCount != null) && (uniqueValuesCount.trim().length() > 0)) {
						staticCatalogField.setIsFilter(true);
						staticCatalogField.setFilterType(StaticCatalogEngine.FILTER_TYPE_VALUES);
						staticCatalogField.setDisplayType(StaticCatalogEngine.DISPLAY_TYPE_CHECKBOXES);
						staticCatalogField.setMinDisplayValues(filterElementsMinDisplay);
						staticCatalogField.setMaxDisplayValues(filterElementsMaxDisplay);
					}
					else {
						staticCatalogField.setIsFilter(false);
						//staticCatalogField.setDisplayType(StaticCatalogEngine.DISPLAY_TYPE_NONE);
					}
					staticCatalogField.setIsSortAsc(false);
					staticCatalogField.setIsSortDesc(false);					
					
					staticCatalogFilters.getFields().add(staticCatalogField);
				}
				
				loadFilters.loadFilters(staticCatalogFilters);
				tabButtons.changeTab(2);
			}
		});
	}
	
	/** Create filters generation file based on the analysis */
	private void createFiltersTab(Composite parentComposite) {
		
	    final FileControl filtersFileControl = addFileControl(parentComposite, "Filters file", "json", p.getFiltersFileControl());
		
		final Composite buttonsComposite = new Composite(parentComposite, SWT.NONE);
		ui.addDebug(buttonsComposite);
		buttonsComposite.setLayoutData(ui.createFillHorizontalGridData());
		buttonsComposite.setLayout(ui.createColumnsSpacingGridLayout(5, UI.sep));

		
		final Button loadFiltersButton = new Button(buttonsComposite, SWT.NONE);
		loadFiltersButton.setText("Load");
		loadFiltersButton.setLayoutData(ui.createWidthButtonGridData());

		final Button saveFiltersButton = new Button(buttonsComposite, SWT.NONE);
		saveFiltersButton.setText("Save");
		saveFiltersButton.setLayoutData(ui.createWidthButtonGridData());

		final Composite fieldsComposite = new Composite(parentComposite, SWT.NONE);
		ui.addDebug(fieldsComposite);
		fieldsComposite.setLayoutData(ui.createFillHorizontalGridData());
		fieldsComposite.setLayout(ui.createColumnsSpacingGridLayout(3, UI.sep));

		final Composite upDownComposite = new Composite(fieldsComposite, SWT.NONE);
		upDownComposite.setLayoutData(ui.createWidthButtonGridData());
		upDownComposite.setLayout(ui.createColumnsSpacingGridLayout(2, UI.sep));

		final Button upButton = new Button(upDownComposite, SWT.NONE);
		upButton.setText("\u25B2");
		upButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {

				int index = filtersGrid.getSelectionIndex();
				if (index < 1) {
					return;
				}
				StaticCatalogConfigurationFields configurationFields = ((StaticCatalogConfigurationFields) filtersGrid.getData("StaticCatalogConfigurationFields"));
				ArrayList<StaticCatalogConfigurationField> fields = configurationFields.getFields();
				StaticCatalogConfigurationField staticCatalogField = fields.get(index);
				fields.remove(index);
				fields.add(index - 1, staticCatalogField);
				loadFilters.loadFilters(configurationFields);
				filtersGrid.setSelection(index - 1);
			}
		});

		final Button downButton = new Button(upDownComposite, SWT.NONE);
		downButton.setText("\u25BC");
		downButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {

				int index = filtersGrid.getSelectionIndex();
				if (index == -1) {
					return;
				}
				StaticCatalogConfigurationFields configurationFields = ((StaticCatalogConfigurationFields) filtersGrid.getData("StaticCatalogConfigurationFields"));
				ArrayList<StaticCatalogConfigurationField> fields = configurationFields.getFields();
				if (index < (fields.size() - 1)) {
					StaticCatalogConfigurationField staticCatalogField = fields.get(index);
					fields.remove(index);
					fields.add(index + 1, staticCatalogField);
					loadFilters.loadFilters(configurationFields);
					filtersGrid.setSelection(index + 1);
				}
			}
		});

		final Label descriptionLabel = new Label(fieldsComposite, SWT.RIGHT);
		descriptionLabel.setText("Description");
		descriptionLabel.setLayoutData(ui.createWidthButtonGridData());
		
		final Text descriptionText = new Text(fieldsComposite, SWT.SINGLE | SWT.BORDER);
		descriptionText.setLayoutData(ui.createFillHorizontalGridData());
		
		filtersGrid = new Grid(parentComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		filtersGrid.setLayoutData(ui.createFillBothGridData());
		filtersGrid.setHeaderVisible(true);
		filtersGrid.setLinesVisible(true);
//		filtersGrid.setSelectionEnabled(false);
		
		GridColumn fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
		fieldGridColumn.setText("Index");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setAlignment(SWT.RIGHT);
	    fieldGridColumn.setWidth(60);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
		fieldGridColumn.setText("CSV Index");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setAlignment(SWT.RIGHT);
	    fieldGridColumn.setWidth(90);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
		fieldGridColumn.setText("Field");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(160);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
		fieldGridColumn.setText("Label");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(170);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
		fieldGridColumn.setText("Type");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(80);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.CENTER);
		fieldGridColumn.setText("Use as Filter");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(110);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
		fieldGridColumn.setText("Filter Type");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(110);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
		fieldGridColumn.setText("Display Type");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(110);
	    
		fieldGridColumn = new GridColumn(filtersGrid, SWT.CENTER);
		fieldGridColumn.setText("Sort Asc.");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(90);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.CENTER);
		fieldGridColumn.setText("Sort Desc.");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(90);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.CENTER);
		fieldGridColumn.setText("Format");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(80);

		fieldGridColumn = new GridColumn(filtersGrid, SWT.CENTER);
		fieldGridColumn.setText("Replace");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(80);

	    filtersGrid.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent mouseEvent) {

				int index = filtersGrid.getSelectionIndex();
				StaticCatalogConfigurationFields configurationFields = ((StaticCatalogConfigurationFields) filtersGrid.getData("StaticCatalogConfigurationFields"));
				fieldEditWindow.showFieldEditWindow(index + 1, configurationFields.getFields().get(index));
			}
	    });
	    
	    loadFilters = new LoadFilters() {
			@Override
			public void loadFilters(StaticCatalogConfigurationFields staticCatalogFilters) {

				filtersGrid.setData("StaticCatalogConfigurationFields", staticCatalogFilters);
				descriptionText.setText(staticCatalogFilters.getDescription());
				
				filtersGrid.clearItems();
				filtersGrid.disposeAllItems();
				
				int index = 0;
				for (StaticCatalogConfigurationField staticCatalogField : staticCatalogFilters.getFields()) {
					
					GridItem gridItem = new GridItem(filtersGrid, SWT.NONE);
					
					int col = 0;
					
					index++;
					gridItem.setText(col++, "" + index);

					gridItem.setText(col++, "" + staticCatalogField.getCsvIndex());
					gridItem.setText(col++, staticCatalogField.getName());
					gridItem.setText(col++, staticCatalogField.getLabel());
					gridItem.setText(col++, typeNames.get(staticCatalogField.getType()));
					
					gridItem.setText(col++, staticCatalogField.getIsFilter() ? "Yes" : "");
					String filterType = staticCatalogField.getFilterType();
					gridItem.setText(col++, filterType == null ? "" : filterTypeNames.get(filterType));
					String displayType = staticCatalogField.getDisplayType();
					gridItem.setText(col++, displayType == null ? "" : displayTypeNames.get(displayType));
					gridItem.setText(col++, staticCatalogField.getIsSortAsc() ? "Yes" : "");
					gridItem.setText(col++, staticCatalogField.getIsSortDesc() ? "Yes" : "");
					gridItem.setText(col++, staticCatalogField.getTransformFormat() == null ? "" : "Yes");
					gridItem.setText(col++, staticCatalogField.getTransformValues() == null ? "" : "Yes");
				}
			}
		};
		
		/* Events */
		loadFiltersButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
			
				StaticCatalogConfigurationFields loadedFilters = S.loadObjectFromJsonFileName(filtersFileControl.getCompleteFileName(), StaticCatalogConfigurationFields.class);
				loadFilters.loadFilters(loadedFilters);
			}
		});
		
		saveFiltersButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				StaticCatalogConfigurationFields filtersDefinition = ((StaticCatalogConfigurationFields) filtersGrid.getData("StaticCatalogConfigurationFields"));
				filtersDefinition.setDescription(descriptionText.getText());
				S.saveObjectToJsonFileName(filtersDefinition, filtersFileControl.getCompleteFileName());
			}
		});
	}

	/** Create generation */
	private void createGenerateTab(Composite parentComposite) {
		
	    final FileControl sourceCsvFileControl = addFileControl(parentComposite, "Source CSV", "csv", p.getGenerateSourceCsvFileControl());

	    final FileControl filtersFileControl = addFileControl(parentComposite, "Filters file", "json", p.getGenerateFiltersFileControl());

	    final FileControl templateFileControl = addFileControl(parentComposite, "Template file", "liquid", p.getGenerateTemplateFileControl());

	    final FileControl destinationFileControl = addFileControl(parentComposite, "Destination folder", "", true, p.getGenerateDestinationFolderFileControl());

		final Composite buttonsComposite = new Composite(parentComposite, SWT.NONE);
		ui.addDebug(buttonsComposite);
		buttonsComposite.setLayoutData(ui.createFillHorizontalGridData());
		buttonsComposite.setLayout(ui.createColumnsSpacingGridLayout(7, UI.sep));
		
		final Button generateButton = new Button(buttonsComposite, SWT.NONE);
		generateButton.setText("Generate");
		generateButton.setLayoutData(ui.createWidthButtonGridData());

		final Button useFirstLineAsHeaderCheckBox = new Button(buttonsComposite, SWT.CHECK);
		GridData useFirstLineasHeaderCheckBoxGridData = ui.createWidthGridData(200);
		useFirstLineasHeaderCheckBoxGridData.verticalIndent = 1; // Perfectionist
		useFirstLineAsHeaderCheckBox.setLayoutData(useFirstLineasHeaderCheckBoxGridData);
		useFirstLineAsHeaderCheckBox.setText("Use first line as header");
		useFirstLineAsHeaderCheckBox.setSelection(p.getGenerateUseFirstLineasHeader());
		useFirstLineAsHeaderCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				p.setGenerateUseFirstLineasHeader(useFirstLineAsHeaderCheckBox.getSelection());
				p.save();
			}
		});
		
		final Composite statusComposite = new Composite(parentComposite, SWT.NONE);
		statusComposite.setBackground(whiteColor);
		statusComposite.setLayoutData(ui.createFillHorizontalGridData());
		GridLayout statusCompositeGridLayout = ui.createColumnsGridLayout(2);
		statusCompositeGridLayout.marginWidth = UI.sep;
		statusCompositeGridLayout.marginHeight = UI.sep;
		statusComposite.setLayout(statusCompositeGridLayout);

		final Label generateStatusLabel = new Label(statusComposite, SWT.NONE);
		generateStatusLabel.setBackground(whiteColor);
		generateStatusLabel.setLayoutData(ui.createFillHorizontalGridData());
		generateStatusLabel.setText("Status");

		final StyledText statusStyledText = new StyledText(parentComposite, SWT.BORDER | SWT.V_SCROLL);
		statusStyledText.setLayoutData(ui.createFillBothGridData());
		statusStyledText.setWordWrap(true);
		statusStyledText.setFont(fontMonospaced);
		
		generateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				StaticCatalogEngine.generate(sourceCsvFileControl.getCompleteFileName(),
					filtersFileControl.getCompleteFileName(), templateFileControl.getCompleteFileName(),
					destinationFileControl.getCompleteFileName(),	
					useFirstLineAsHeaderCheckBox.getSelection(),
					new LoopProgress() {
						@Override
						public void doProgress(String progressMessage) {
							doProgress(progressMessage, null);
						}
						@Override
						public void doProgress(String progressMessage, String stepMessage) {
							
							Display.getDefault().readAndDispatch();
							if (progressMessage != null) {
								generateStatusLabel.setText(progressMessage);	
							}
							if (stepMessage != null) {
								statusStyledText.append(stepMessage + "\n");	
							}
							Display.getDefault().readAndDispatch();
						}
					}
				);
			}
		});
	}
}
