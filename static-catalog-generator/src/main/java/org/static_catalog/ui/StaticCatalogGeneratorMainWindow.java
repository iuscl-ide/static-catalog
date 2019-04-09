/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridCellRenderer;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.static_catalog.engine.StaticCatalogEngine;
import org.static_catalog.engine.StringAsNumberComparator;
import org.static_catalog.main.L;
import org.static_catalog.main.S;
import org.static_catalog.model.StaticCatalogField;
import org.static_catalog.model.StaticCatalogFilters;

/** Generator main window */
public class StaticCatalogGeneratorMainWindow {

	/** Type names */
	public static final LinkedHashMap<String, String> typeNames = new LinkedHashMap<>();
	static {
		typeNames.put("long", "Integer");
		typeNames.put("double", "Real");
		typeNames.put("date", "Date");
		typeNames.put("text", "Text");
	}
	
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

	
	/** Natural order */
	private final StringAsNumberComparator stringAsNumberComparator = new StringAsNumberComparator();
	
	/** Concurrent */
	private AtomicBoolean doLoop = new AtomicBoolean(true);
	
	/** debug */
	private boolean isDebug = false;

	/** Main display */
	private Display display;

	/* Fonts */
	private Font fontNormal;
	private Font fontBold;
	private Font fontBigger;

	/* Colors */
	private Color whiteColor;
	
	/** Separator, margin, padding */
	private final int sep = 8;
	
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

		/* Colors */
		whiteColor = new Color(display, 255, 255, 255);
		
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
	private FileControl addFileControl(Composite parentComposite, String fileControlName) {
		
		/*
		 * TODO 
		 * 1/ The Browse button
		 * 2/ The text to be a combo with recent files
		 * 
		 */
		
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
		
	    final Composite topComposite = new Composite(mainShell, SWT.NONE);
	    addDebug(topComposite);
	    topComposite.setLayoutData(createFillHorizontalGridData());
	    topComposite.setLayout(createGridLayout());

	    final Composite topButtonsComposite = new Composite(topComposite, SWT.NONE);
	    addDebug(topButtonsComposite);
	    GridData topButtonsCompositeGridData = createGridData();
	    topButtonsCompositeGridData.horizontalAlignment = SWT.CENTER;
	    topButtonsCompositeGridData.grabExcessHorizontalSpace = true;
	    topButtonsComposite.setLayoutData(topButtonsCompositeGridData);
	    topButtonsComposite.setLayout(createColumnsSpacingGridLayout(4, sep));
	    
	    final String[] topButtonTexts = { "View CSV", "Analyse CSV", "Create Filters", "Generate" };
	    
	    ArrayList<Button> topButtons = new ArrayList<>(); 

	    for (String topButtonText : topButtonTexts) {

		    Button button = new Button(topButtonsComposite, SWT.TOGGLE);
		    button.setText(topButtonText);
		    button.setLayoutData(createWidthGridData(120));
		    
		    topButtons.add(button);
	    }
	    
	    final Label separator = new Label(mainShell, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(createFillHorizontalGridData());
	    
	    final ArrayList<Composite> mainComposites = new ArrayList<>(); 
	    
	    for (String topButtonText : topButtonTexts) {

	    	final Composite composite = new Composite(mainShell, SWT.NONE);
		    addDebug(composite);
		    composite.setLayoutData(createFillBothGridData());
		    composite.setLayout(createVerticalSpacingGridLayout(sep));
	    	
		    mainComposites.add(composite);
		    
		    final Label label = new Label(composite, SWT.NONE);
		    label.setText(topButtonText);
		    label.setFont(fontBigger);
		    label.setLayoutData(createFillHorizontalGridData());
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
		
		/*
		 * TODO 
		 * 1/ Load in memory only string lines and parse on display
		 * 2/ Activate the Stop button when no memory
		 * 
		 */

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

		final Button useFirstLineAsHeaderCheckBox = new Button(csvButtonsComposite, SWT.CHECK);
		GridData useFirstLineAsHeaderCheckBoxGridData = createWidthGridData(200);
		useFirstLineAsHeaderCheckBoxGridData.verticalIndent = 1; // Perfectionist
		useFirstLineAsHeaderCheckBox.setLayoutData(useFirstLineAsHeaderCheckBoxGridData);
		useFirstLineAsHeaderCheckBox.setText("Use first line as header");
		useFirstLineAsHeaderCheckBox.setSelection(true);
		
		final Button csvStopLoadButton = new Button(csvButtonsComposite, SWT.NONE);
		csvStopLoadButton.setText("Stop");
		csvStopLoadButton.setEnabled(false);
		csvStopLoadButton.setLayoutData(createWidthGridData(120));

		
//		final Button csvExtractButton = new Button(csvButtonsComposite, SWT.NONE);
//		csvExtractButton.setText("Extract");
//		csvExtractButton.setLayoutData(createWidthGridData(120));

		
		final Composite csvStatusComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		csvStatusComposite.setBackground(whiteColor);
		csvStatusComposite.setLayoutData(createFillHorizontalGridData());
		GridLayout csvStatusCompositeGridLayout = createColumnsGridLayout(2);
		csvStatusCompositeGridLayout.marginWidth = sep;
		csvStatusCompositeGridLayout.marginHeight = sep;
		csvStatusComposite.setLayoutData(createFillHorizontalGridData());
		csvStatusComposite.setLayout(csvStatusCompositeGridLayout);

		final Label csvStatusLabel = new Label(csvStatusComposite, SWT.NONE);
		csvStatusLabel.setBackground(whiteColor);
		csvStatusLabel.setLayoutData(createFillHorizontalGridData());
		csvStatusLabel.setText("Status");

		
		final Grid csvFileGrid = new Grid(viewCsvTabComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		csvFileGrid.setLayoutData(createFillBothGridData());
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
	    
	    final FileControl analyzeCsvFileControl = addFileControl(parentComposite, "analyze_csv");
		
		final Composite csvButtonsComposite = new Composite(parentComposite, SWT.NONE);
		addDebug(csvButtonsComposite);
		csvButtonsComposite.setLayoutData(createFillHorizontalGridData());
		csvButtonsComposite.setLayout(createColumnsSpacingGridLayout(7, sep));
		
		final Button csvAnalyzeButton = new Button(csvButtonsComposite, SWT.NONE);
		csvAnalyzeButton.setText("Analyze");
		csvAnalyzeButton.setLayoutData(createWidthGridData(120));
		
		final Text typeMaxExceptionsText = new Text(csvButtonsComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		typeMaxExceptionsText.setText("1");
		typeMaxExceptionsText.setLayoutData(createWidthGridData(30));

		final Label typeMaxExceptionsLabel = new Label(csvButtonsComposite, SWT.NONE);
		typeMaxExceptionsLabel.setLayoutData(createWidthGridData(210));
		typeMaxExceptionsLabel.setText("maximum field type exception values");

		final Text filterElementsMaxDisplayText = new Text(csvButtonsComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		filterElementsMaxDisplayText.setText("500");
		filterElementsMaxDisplayText.setLayoutData(createWidthGridData(40));

		final Label uniqueElementsMaxDisplayLabel = new Label(csvButtonsComposite, SWT.NONE);
		uniqueElementsMaxDisplayLabel.setLayoutData(createWidthGridData(200));
		uniqueElementsMaxDisplayLabel.setText("maximum filter elements to display");

		
		final Button useFirstLineasHeaderCheckBox = new Button(csvButtonsComposite, SWT.CHECK);
		GridData useFirstLineasHeaderCheckBoxGridData = createWidthGridData(200);
		useFirstLineasHeaderCheckBoxGridData.verticalIndent = 1; // Perfectionist
		useFirstLineasHeaderCheckBox.setLayoutData(useFirstLineasHeaderCheckBoxGridData);
		useFirstLineasHeaderCheckBox.setText("Use first line as header");
		useFirstLineasHeaderCheckBox.setSelection(true);

		final Button createFiltersButton = new Button(csvButtonsComposite, SWT.NONE);
		GridData createFiltersButtonGridData = createWidthGridData(120);
		createFiltersButtonGridData.horizontalAlignment = SWT.END;
		createFiltersButtonGridData.grabExcessHorizontalSpace = true;
		createFiltersButton.setLayoutData(createFiltersButtonGridData);
		createFiltersButton.setText("Create New Filters");
		

		
		
//		final Button csvStopLoadButton = new Button(csvButtonsComposite, SWT.NONE);
//		csvStopLoadButton.setText("Stop");
//		csvStopLoadButton.setEnabled(false);
//		csvStopLoadButton.setLayoutData(createWidthGridData(120));

		
//		final Button csvExtractButton = new Button(csvButtonsComposite, SWT.NONE);
//		csvExtractButton.setText("Extract");
//		csvExtractButton.setLayoutData(createWidthGridData(120));

		
		final Composite csvStatusComposite = new Composite(parentComposite, SWT.NONE);
		csvStatusComposite.setBackground(whiteColor);
		csvStatusComposite.setLayoutData(createFillHorizontalGridData());
		GridLayout csvStatusCompositeGridLayout = createColumnsGridLayout(2);
		csvStatusCompositeGridLayout.marginWidth = sep;
		csvStatusCompositeGridLayout.marginHeight = sep;
		csvStatusComposite.setLayout(csvStatusCompositeGridLayout);

		final Label csvStatusLabel = new Label(csvStatusComposite, SWT.NONE);
		csvStatusLabel.setBackground(whiteColor);
		csvStatusLabel.setLayoutData(createFillHorizontalGridData());
		csvStatusLabel.setText("Status");

		
		final Grid csvAnalyzeGrid = new Grid(parentComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		csvAnalyzeGrid.setLayoutData(createFillBothGridData());
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
				
				//L.p(viewCsvFileControl.getCompleteFileName());
				csvAnalyzeGrid.clearItems();
				csvAnalyzeGrid.disposeAllItems();
				
//				while (csvAnalyzeGrid.getColumnCount() > 0) {
//					csvAnalyzeGrid.getColumns()[0].dispose();
//				}
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
					
					staticCatalogField.setFilter(false);
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
	    
	    final FileControl filtersFileControl = addFileControl(parentComposite, "analyze_csv");
		
		final Composite csvButtonsComposite = new Composite(parentComposite, SWT.NONE);
		addDebug(csvButtonsComposite);
		csvButtonsComposite.setLayoutData(createFillHorizontalGridData());
		csvButtonsComposite.setLayout(createColumnsSpacingGridLayout(5, sep));
		
		final Button saveFiltersButton = new Button(csvButtonsComposite, SWT.NONE);
		saveFiltersButton.setText("Save");
		saveFiltersButton.setLayoutData(createWidthGridData(120));
		
//		final Text typeMaxExceptionsText = new Text(csvButtonsComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
//		typeMaxExceptionsText.setText("1");
//		typeMaxExceptionsText.setLayoutData(createWidthGridData(30));
//
//		final Label typeMaxExceptionsLabel = new Label(csvButtonsComposite, SWT.NONE);
//		typeMaxExceptionsLabel.setLayoutData(createWidthGridData(210));
//		typeMaxExceptionsLabel.setText("maximum field type exception values");
//
//		final Text filterElementsMaxDisplayText = new Text(csvButtonsComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
//		filterElementsMaxDisplayText.setText("500");
//		filterElementsMaxDisplayText.setLayoutData(createWidthGridData(40));
//
//		final Label uniqueElementsMaxDisplayLabel = new Label(csvButtonsComposite, SWT.NONE);
//		uniqueElementsMaxDisplayLabel.setLayoutData(createWidthGridData(200));
//		uniqueElementsMaxDisplayLabel.setText("maximum filter elements to display");

		
//		final Button csvStopLoadButton = new Button(csvButtonsComposite, SWT.NONE);
//		csvStopLoadButton.setText("Stop");
//		csvStopLoadButton.setEnabled(false);
//		csvStopLoadButton.setLayoutData(createWidthGridData(120));

		
//		final Button csvExtractButton = new Button(csvButtonsComposite, SWT.NONE);
//		csvExtractButton.setText("Extract");
//		csvExtractButton.setLayoutData(createWidthGridData(120));

		
//		final Composite csvStatusComposite = new Composite(parentComposite, SWT.NONE);
//		csvStatusComposite.setBackground(whiteColor);
//		csvStatusComposite.setLayoutData(createFillHorizontalGridData());
//		GridLayout csvStatusCompositeGridLayout = createColumnsGridLayout(2);
//		csvStatusCompositeGridLayout.marginWidth = sep;
//		csvStatusCompositeGridLayout.marginHeight = sep;
//		csvStatusComposite.setLayout(csvStatusCompositeGridLayout);
//
//		final Label csvStatusLabel = new Label(csvStatusComposite, SWT.NONE);
//		csvStatusLabel.setBackground(whiteColor);
//		csvStatusLabel.setLayoutData(createFillHorizontalGridData());
//		csvStatusLabel.setText("Status");

		
		final Grid filtersGrid = new Grid(parentComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		filtersGrid.setLayoutData(createFillBothGridData());
		filtersGrid.setHeaderVisible(true);
		filtersGrid.setLinesVisible(true);
		
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

	    fieldGridColumn.setCellRenderer(new GridCellRenderer() {
			
			@Override
			public void paint(GC gc, Object value) {
				// TODO Auto-generated method stub
//				GridItem gridItem = (GridItem) value;
//				gc.drawText(gridItem.getText(2), gridItem.getBounds(2).x, gridItem.getBounds(2).y);
//				L.p(value + "");
				
				Combo combo = new Combo(filtersGrid, SWT.DROP_DOWN);
				combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
				combo.setItems("ef");
				
				
			}
			
			@Override
			public Point computeSize(GC gc, int wHint, int hHint, Object value) {
				// TODO Auto-generated method stub
				return new Point(wHint - 2, hHint - 2);
			}
			
			@Override
			public boolean notify(int event, Point point, Object value) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	    
		fieldGridColumn = new GridColumn(filtersGrid, SWT.CHECK | SWT.CENTER);
		fieldGridColumn.setText("Use as Filter");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(100);
	    fieldGridColumn.setCheckable(true);
	    
	    fieldGridColumn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				L.p("devMessage");
			}
		});
	    

		fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
		fieldGridColumn.setText("Label");
	    fieldGridColumn.setWordWrap(true);
	    fieldGridColumn.setWidth(250);


	    loadFilters = new LoadFilters() {
			@Override
			public void loadFilters(StaticCatalogFilters staticCatalogFilters) {

				filtersGrid.clearItems();
				filtersGrid.disposeAllItems();

				int index = 0;
				for (StaticCatalogField staticCatalogField : staticCatalogFilters.getFields()) {
					
					GridItem gridItem = new GridItem(filtersGrid, SWT.NONE);
					
					index++;
					gridItem.setText(0, "" + index);
					
					gridItem.setText(1, staticCatalogField.getName());
					gridItem.setText(2, staticCatalogField.getType());
					
					gridItem.setChecked(3, staticCatalogField.isFilter());
					
					//gridItem.setText(3, staticCatalogField.isFilter() + "");
					
					gridItem.setText(4, staticCatalogField.getLabel());
					
				}
			}
		};
	    
	    
//	    fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
//		fieldGridColumn.setText("Unique elements count");
//	    fieldGridColumn.setWordWrap(true);
//	    fieldGridColumn.setWidth(150);
//
//	    fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
//		fieldGridColumn.setText("Exceptions");
//	    fieldGridColumn.setWordWrap(true);
//	    fieldGridColumn.setWidth(100);
//
//	    fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
//		fieldGridColumn.setText("Unique elements");
//	    fieldGridColumn.setWordWrap(true);
//	    fieldGridColumn.setWidth(300);
//
//	    fieldGridColumn = new GridColumn(filtersGrid, SWT.NONE);
//		fieldGridColumn.setText("Unique elements distribution");
//	    fieldGridColumn.setWordWrap(true);
//	    fieldGridColumn.setWidth(300);

		
		
		/* Events */
		final ArrayList<String[]> csvAnalyzeGridLines = new ArrayList<String[]>();
		
//		saveFiltersButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				
//				//L.p(viewCsvFileControl.getCompleteFileName());
//				filtersGrid.clearItems();
//				filtersGrid.disposeAllItems();
//				
////				while (csvAnalyzeGrid.getColumnCount() > 0) {
////					csvAnalyzeGrid.getColumns()[0].dispose();
////				}
//				csvAnalyzeGridLines.clear();
//				
//				ArrayList<HashMap<String, Long>> fields = new ArrayList<HashMap<String,Long>>();
//				ArrayList<String> fieldNames = new ArrayList<>();
//				ArrayList<String> fieldTypes = new ArrayList<>();
//				ArrayList<HashMap<String, ArrayList<String>>> fieldTypesExceptionValues = new ArrayList<>();
//				 
//				StaticCatalogEngine.loadAnalyzeCsv(filtersFileControl.getCompleteFileName(), 500,
//				fields, fieldNames, fieldTypes, fieldTypesExceptionValues,
//				Integer.parseInt(typeMaxExceptionsText.getText()),
//				doLoop,
//				new LoopProgress() {
//					@Override
//					public void doProgress(String progressMessage) {
//						Display.getDefault().syncExec(new Runnable() {
//							public void run() {
//								csvStatusLabel.setText(progressMessage);
//								Display.getDefault().readAndDispatch();
//							}
//						});
//					}
//				});
//				
//				int maxDiff = Integer.parseInt(filterElementsMaxDisplayText.getText());
//				
//				for (int index = 0; index < fieldNames.size(); index++) {
//					
//					GridItem csvGridItem = new GridItem(filtersGrid, SWT.NONE);
//					
//					csvGridItem.setText(0, fieldNames.get(index));
//					String fieldType = fieldTypes.get(index);
//					csvGridItem.setText(1, fieldType);
//					
//					
//					int diff = fields.get(index).keySet().size(); 
//					csvGridItem.setText(2, diff + "");
//					
//					if (diff < maxDiff) {
//						
//						HashMap<String, Long> groups = fields.get(index); 
//
//						if (!fieldType.equals("text")) {
//							ArrayList<String> exceps = new ArrayList<>(fieldTypesExceptionValues.get(index).get(fieldType));
//							Collections.sort(exceps);
//
//							csvGridItem.setText(3, String.join(", ", exceps));
//						}
//						
//						ArrayList<String> keys = new ArrayList<>(groups.keySet());
//						Collections.sort(keys, stringAsNumberComparator);
//
//						csvGridItem.setText(4, String.join(", ", keys));
//
//						
//						ArrayList<String> keysValues = new ArrayList<>();
//						for (String key : keys) {
//							keysValues.add(key + " (" + groups.get(key) + ") ");
//						}
//						
//						csvGridItem.setText(5, String.join(", ", keysValues));
//					}
//				}
//			}
//		});
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
