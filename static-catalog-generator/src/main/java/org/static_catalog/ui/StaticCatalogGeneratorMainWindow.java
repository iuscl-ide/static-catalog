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

/** Viewer main window */
public class StaticCatalogGeneratorMainWindow {

	/** Main display */
	private Display display;

	/** Main Window instance */
	private static StaticCatalogGeneratorMainWindow mainWindowInstance;

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
//	    MenuItem removeMenuItem = new MenuItem(fileMenu, SWT.CASCADE);
//	    removeMenuItem.setText("Remove");
//	    
//	    removeMenuItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				
//				if (contentsTree.getSelectionCount() == 0) {
//					
//					return;
//				}
//				
//				TreeItem treeItem = contentsTree.getSelection()[0];
//				
//				while (treeItem.getParentItem() != null) {
//					
//					treeItem = treeItem.getParentItem();
//				}
//				
//				XMLPlugin removePlugin = plugins.get(contentsTree.indexOf(treeItem));
//				
//				MessageBox messageBox = new MessageBox(mainShell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
//				messageBox.setMessage("Do you want to remove \"" + treeItem.getText() + "\"?");
//				messageBox.setText("Remove plugin \"" + removePlugin.getPluginName() + "\"");
//				int response = messageBox.open();
//				
//				if (response == SWT.YES) {
//					
//					plugins.remove(removePlugin);
//					treeItem.dispose();
//					loadStartHTML();
//
//					int index = -1;
//					
//					for (EHAViewerCache ehaViewerCache : EHAViewerCache.getEhaCaches()) {
//						
//						if (ehaViewerCache.getPluginId().equalsIgnoreCase(removePlugin.getPluginID())) {
//							
//							index = EHAViewerCache.getEhaCaches().indexOf(ehaViewerCache);
//						}
//					}
//					
//					EHAViewerCache.getEhaCaches().remove(index);
//					EHAViewerCache.save();
//
//					S.deleteFolder(new File(removePlugin.getCacheFolderPluginPath()));
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
	    mainShell.setLayout(createGridLayout(0, 0, 0, 0, 1));

	    createViewCsvTab();

	    
//	    Composite mainComposite = new Composite(mainShell, SWT.NONE);
//	    mainComposite.setBackground(new Color(display, 0, 0, 255));
//	    mainComposite.setLayoutData(createGridData(GridData.FILL, GridData.FILL, true, true, -1));
//	    mainComposite.setLayout(createGridLayout(8, 8, 0, 0, 1));
	    
//	    Composite topComposite = new Composite(mainShell, SWT.NONE);
//	    //topComposite.setBackground(new Color(display, 0, 0, 255));
//	    gridData = new GridData();
//	    //gridData.heightHint = 32;
//	    gridData.horizontalAlignment = GridData.FILL;
//	    gridData.grabExcessHorizontalSpace = true;
//	    topComposite.setLayoutData(gridData);
//	    
//	    GridLayout topGridLayout = new GridLayout();
//	    topGridLayout.marginWidth = 4;
//	    topGridLayout.marginHeight = 8;
//	    topGridLayout.verticalSpacing = 0;
//	    topGridLayout.horizontalSpacing = 4;
//	    topGridLayout.numColumns = 5;
//	    topComposite.setLayout(topGridLayout);
//	    
//	    Label topSearchLabel = new Label(topComposite, SWT.NONE);
//	    topSearchLabel.setText("Search:");
//	    
//	    final Text topSearchText = new Text(topComposite, SWT.SINGLE | SWT.BORDER);
//	    topSearchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//	    ((GridData) topSearchText.getLayoutData()).widthHint = 200;
//	    topSearchText.setText("");
////	    L.p(" " + topSearchText.getFont().getFontData()[0].getHeight());
//	    topSearchText.addKeyListener(new KeyAdapter() {
//
//			@Override
//			public void keyPressed(KeyEvent keyEvent) {
//				
//				if (keyEvent.keyCode == SWT.CR) {
//
//					keyEvent.doit = false;
//					lastSearchPhrase = topSearchText.getText();
//					loadSearchResults();
//				}
//			}
//		});
//
//	    Button topSearchButton = newGrayButton(topComposite, "Go");
//	    topSearchButton.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//
//				lastSearchPhrase = topSearchText.getText();
//				loadSearchResults();
//			}
//		});
//	    
////	    ((GridData) topSearchButton.getLayoutData()).heightHint = ((GridData) topSearchText.getLayoutData()).heightHint;
////	    ((GridData) topSearchText.getLayoutData()).heightHint = ((GridData) topSearchButton.getLayoutData()).heightHint - (3 * topSearchText.getBorderWidth());
//	    
//	    topComposite.pack();
//	    
//	    Composite middleComposite = new Composite(mainShell, SWT.NONE);
//	    //middleComposite.setBackground(new Color(display, 0, 255, 0));
//	    gridData = new GridData();
//	    gridData.horizontalAlignment = GridData.FILL;
//	    gridData.grabExcessHorizontalSpace = true;
//	    gridData.verticalAlignment = GridData.FILL;
//	    gridData.grabExcessVerticalSpace = true;
//	    middleComposite.setLayoutData(gridData);
//	    middleComposite.setLayout(new FillLayout());
//	    
//	    middleSashForm = new SashFormMin(middleComposite, SWT.HORIZONTAL | SWT.SMOOTH);
//	    middleSashForm.SASH_WIDTH = 5;
//	    middleSashForm.DRAG_MINIMUM_1 = 200;
//	    middleSashForm.DRAG_MINIMUM_2 = 400;
//	    
//	    final Composite leftComposite = new Composite(middleSashForm, SWT.NONE | SWT.DOUBLE_BUFFERED);
//	    
//	    //leftComposite.setBackground(new Color(display, 255, 255, 0));
//	    GridLayout leftGridLayout = new GridLayout();
//	    leftGridLayout.marginWidth = 2;
//	    leftGridLayout.marginHeight = 2;
//	    leftGridLayout.verticalSpacing = 2;
//	    leftGridLayout.numColumns = 1;
//	    leftComposite.setLayout(leftGridLayout);
//
//	    //Composite leftToolbarComposite = new Composite(leftComposite, SWT.BORDER);
//	    final Composite leftHeaderComposite = new Composite(leftComposite, SWT.NONE);
//	    gridData = new GridData();
//	    gridData.horizontalAlignment = GridData.FILL;
//	    gridData.grabExcessHorizontalSpace = true;
//	    leftHeaderComposite.setLayoutData(gridData);
//	    
//	    GridLayout leftHeaderGridLayout = new GridLayout();
//	    leftHeaderGridLayout.marginWidth = 4;
//	    leftHeaderGridLayout.marginHeight = 4;
//	    leftHeaderGridLayout.verticalSpacing = 0;
//	    leftHeaderGridLayout.horizontalSpacing = 0;
//	    leftHeaderGridLayout.numColumns = 2;
//	    leftHeaderComposite.setLayout(leftHeaderGridLayout);
//
//	    tabNameLabel = new Label(leftHeaderComposite, SWT.NONE);
//		tabNameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));
//		changeFont(tabNameLabel, SWT.BOLD);
//		tabNameLabel.setText("Contents");
//		
//		
//		ToolItem toolItem;
//		
//		final ToolBar contentToolbar = new ToolBar(leftHeaderComposite, SWT.FLAT | SWT.WRAP | SWT. HORIZONTAL);
//		contentToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
//		activeTabToolbar = contentToolbar;
//		
////		toolItem = newToolItem(contentToolbar, SWT.DROP_DOWN, "e_print_topic", "Print Topics");
////
////		toolItem = newToolItem(contentToolbar, SWT.DROP_DOWN, "e_search_results_view", "Search Topics");
//		
//		toolItem = newToolItem(contentToolbar, "e_collapseall", "Collapse All");
//		toolItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//
//				for (TreeItem treeItem : contentsTree.getItems()) {
//					
//					collapseAll(treeItem);
//				}
//			}
//		});
//		
//		toolItem = newToolItem(contentToolbar, SWT.CHECK, "e_auto_synch_toc", "Link with Contents");
//		toolItem.setSelection(false);
//		toolItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				
//				alwaysSyncContents = !alwaysSyncContents;
//				
//				ToolItem toolItem = (ToolItem) selectionEvent.widget;
//				toolItem.setSelection(alwaysSyncContents);
//				
//				if (alwaysSyncContents) {
//					
//					syncContents(false);
//				}
//			}
//		});
//
//		toolItem = newToolItem(contentToolbar, "maximize", "Maximize");
//		toolItem.addSelectionListener(newMaximizeSelectionAdapter(leftComposite));
//
//		leftHeaderComposite.pack();
//
//		
//		final ToolBar indexToolbar = new ToolBar(leftHeaderComposite, SWT.FLAT | SWT.WRAP | SWT. HORIZONTAL);
//		indexToolbar.setVisible(false);
//		indexToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
//		((GridData) indexToolbar.getLayoutData()).exclude = true;
//
//		toolItem = newToolItem(indexToolbar, "maximize", "Maximize");
//		toolItem.addSelectionListener(newMaximizeSelectionAdapter(leftComposite));
//		
//		
//		searchResultsToolbar = new ToolBar(leftHeaderComposite, SWT.FLAT | SWT.WRAP | SWT. HORIZONTAL);
//		searchResultsToolbar.setVisible(false);
//		searchResultsToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
//		((GridData) searchResultsToolbar.getLayoutData()).exclude = true;
//		
//		toolItem = newToolItem(searchResultsToolbar, SWT.CHECK, "e_show_categories", "Group by Categories");
//		toolItem.setSelection(true);
//		toolItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				
//				groupSearchByCategories = !groupSearchByCategories;
//
//				ToolItem toolItem = (ToolItem) selectionEvent.widget;
//				toolItem.setSelection(groupSearchByCategories);
//
//				loadSearchResults();
//			}
//		});
//		
//		toolItem = newToolItem(searchResultsToolbar, SWT.CHECK, "e_show_descriptions", "Show Descriptions");
//		toolItem.setSelection(true);
//		toolItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				
//				showSearchDescriptions = !showSearchDescriptions;
//
//				ToolItem toolItem = (ToolItem) selectionEvent.widget;
//				toolItem.setSelection(showSearchDescriptions);
//
//				loadSearchResults();
//			}
//		});
//		
//		toolItem = newToolItem(searchResultsToolbar, "maximize", "Maximize");
//		toolItem.addSelectionListener(newMaximizeSelectionAdapter(leftComposite));
//
//		
//		final ToolBar bookmarksToolbar = new ToolBar(leftHeaderComposite, SWT.FLAT | SWT.WRAP | SWT. HORIZONTAL);
//		bookmarksToolbar.setVisible(false);
//		bookmarksToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
//		((GridData) bookmarksToolbar.getLayoutData()).exclude = true;
//		
//		toolItem = newToolItem(bookmarksToolbar, "e_bookmark_rem", "Delete Selected Bookmark");
//		toolItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//
//				int index = bookmarksGrid.getSelectionIndex();
//				
//				if (index > -1) {
//					
//					GridItem gridItem = bookmarksGrid.getItem(index);
//					UIBookmarkItem uiBookmarkItem = (UIBookmarkItem) gridItem.getData();
//					
//					EHAViewerProperties.deleteBookmark(uiBookmarkItem);
//					
//					uiBookmarkItems.clear();
//					for (XMLPlugin plugin : plugins) {
//						
//						plugin.loadUIBookmarkItems(uiBookmarkItems);
//					}
//					loadBookmarksGrid();
//				}
//			}
//		});
//		
//		toolItem = newToolItem(bookmarksToolbar, "e_bookmark_remall", "Delete All Bookmarks");
//		toolItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//
//				ArrayList<String> pluginIDs = new ArrayList<>();
//				
//				for (XMLPlugin plugin : plugins) {
//					
//					pluginIDs.add(plugin.getPluginID());
//				}
//				
//				EHAViewerProperties.deleteAllBookmarks(pluginIDs);
//					
//				uiBookmarkItems.clear();
//				loadBookmarksGrid();
//			}
//		});
//		
//		toolItem = newToolItem(bookmarksToolbar, "maximize", "Maximize");
//		toolItem.addSelectionListener(newMaximizeSelectionAdapter(leftComposite));
//		
//
//	    final Composite leftContentComposite = new Composite(leftComposite, SWT.NONE);
//	    leftContentComposite.setLayoutData(newGridDataFILLFILLtruetrue());
//	    leftContentComposite.setLayout(newGridLayout00001());
//
//	    
//	    final Composite contentComposite = new Composite(leftContentComposite, SWT.NONE);
//	    contentComposite.setLayoutData(newGridDataFILLFILLtruetrue());
//	    contentComposite.setLayout(newGridLayout00001());
//	    activeComposite = contentComposite;
//	    
//		contentsTree = new Tree(contentComposite, SWT.NONE);
//		contentsTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		contentsTree.addTreeListener(new TreeListener() {
//			
//			@Override
//			public void treeExpanded(TreeEvent treeEvent) {
//				
//				 TreeItem treeItem = (TreeItem) treeEvent.item;
//				 if (treeItem.getParentItem() == null) {
//					 
//					 treeItem.setImage(uiImages.get("toc_open"));
//				 }
//			}
//			
//			@Override
//			public void treeCollapsed(TreeEvent treeEvent) {
//
//				 TreeItem treeItem = (TreeItem) treeEvent.item;
//				 if (treeItem.getParentItem() == null) {
//					 
//					 treeItem.setImage(uiImages.get("toc_closed"));
//				 }
//				 
//				 if (osNameProperty.contains("mac")) {
//					 
//					 contentsTree.setSelection(treeItem);
//				 }
//			}
//		});
//		
//		contentsTree.addMouseTrackListener(new MouseTrackAdapter() {
//			
//			@Override
//			public void mouseHover(MouseEvent mouseEvent) {
//
//				TreeItem treeItem = contentsTree.getItem(new Point(mouseEvent.x, mouseEvent.y));
//				
//				if (treeItem != null) {
//				
//					UITocItem tocItem = (UITocItem) treeItem.getData();
//					String ref = tocItem.getRef();
//					//String ref = tocItem.getBaseUrl();
//					if (ref != null) {
//						
//						bottomLeftLabel.setText(ref);
//					}
//					else {
//						
//						bottomLeftLabel.setText("");
//					}
//					
//					//bottomLeftLabel.setText(tocItem.getBaseUrl());
//				}
//				else {
//					
//					bottomLeftLabel.setText("");
//				}
//			}
//			
//			@Override
//			public void mouseExit(MouseEvent mouseEvent) {
//				
//				bottomLeftLabel.setText("");
//			}
//		});
//		
//		contentsTree.addSelectionListener(new SelectionAdapter() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//				
//				TreeItem treeItem = (TreeItem) selectionEvent.item;
//				UITocItem tocItem = (UITocItem) treeItem.getData();
//
//				String baseUrl = tocItem.getBaseUrl();
//				
//				loadHTML(baseUrl);
//			}
//		});
//
//		/* goto index */
//		
//	    final Composite indexComposite = new Composite(leftContentComposite, SWT.NONE);
//	    indexComposite.setBackground(colorWhite);
//	    indexComposite.setVisible(false);
//	    indexComposite.setLayoutData(newGridDataFILLFILLtruetrue());
//	    ((GridData) indexComposite.getLayoutData()).exclude = true;
//	    indexComposite.setLayout(newGridLayout00001());
//
//	    Composite indexHeaderComposite = new Composite(indexComposite, SWT.NONE);
//	    gridData = new GridData();
//	    gridData.horizontalAlignment = GridData.FILL;
//	    gridData.grabExcessHorizontalSpace = true;
//	    indexHeaderComposite.setLayoutData(gridData);
//	    
//	    GridLayout indexHeaderGridLayout = new GridLayout();
//	    indexHeaderGridLayout.marginWidth = 4;
//	    indexHeaderGridLayout.marginHeight = 4;
//	    indexHeaderGridLayout.verticalSpacing = 4;
//	    indexHeaderGridLayout.horizontalSpacing = 0;
//	    indexHeaderGridLayout.numColumns = 1;
//	    indexHeaderComposite.setLayout(indexHeaderGridLayout);
//
//	    Label indexHeaderLabel = new Label(indexHeaderComposite, SWT.NONE);
//	    indexHeaderLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));
//	    indexHeaderLabel.setText("Type in the word to find:");
//
//	    
//	    Composite indexHeaderSearchComposite = new Composite(indexHeaderComposite, SWT.NONE);
//	    gridData = new GridData();
//	    gridData.horizontalAlignment = GridData.FILL;
//	    gridData.grabExcessHorizontalSpace = true;
//	    indexHeaderSearchComposite.setLayoutData(gridData);
//	    
//	    GridLayout indexHeaderSearchGridLayout = new GridLayout();
//	    indexHeaderSearchGridLayout.marginWidth = 0;
//	    indexHeaderSearchGridLayout.marginHeight = 0;
//	    indexHeaderSearchGridLayout.verticalSpacing = 0;
//	    indexHeaderSearchGridLayout.horizontalSpacing = 4;
//	    indexHeaderSearchGridLayout.numColumns = 2;
//	    indexHeaderSearchComposite.setLayout(indexHeaderSearchGridLayout);
//
//	    final Text indexHeaderDisplayWordText = new Text(indexHeaderSearchComposite, SWT.SINGLE | SWT.BORDER);
//		indexHeaderDisplayWordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//		indexHeaderDisplayWordText.setText("");
//
//		indexHeaderDisplayWordText.addKeyListener(new KeyAdapter() {
//
//			@Override
//			public void keyPressed(KeyEvent keyEvent) {
//				
//				if (keyEvent.keyCode == SWT.CR) {
//
//					keyEvent.doit = false;
//					lastIndexDisplayWord = indexHeaderDisplayWordText.getText();
//					loadIndexGrid();
//				}
//			}
//		});
//
//		
//	    Button indexHeaderSearchButton = newGrayButton(indexHeaderSearchComposite, "Display");
//	    indexHeaderSearchButton.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent selectionEvent) {
//
//				lastIndexDisplayWord = indexHeaderDisplayWordText.getText();
//				loadIndexGrid();
//			}
//		});
//	    
//	    indexHeaderSearchComposite.pack();
//	    
//	    indexComposite.addControlListener(new ControlAdapter() {
//
//			@Override
//			public void controlResized(ControlEvent controlEvent) {
//
//				indexGrid.getColumn(0).setWidth(indexComposite.getClientArea().width - indexGrid.getVerticalBar().getSize().x);
//			}
//		});
//
//	    
//	    indexGrid = new Grid(indexComposite, SWT.V_SCROLL | SWT.H_SCROLL);
//	    indexGrid.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//	    indexGrid.setAutoHeight(true);
//	    indexGrid.setLinesVisible(false);
//
//	    indexGrid.addMouseListener(new MouseAdapter() {
//
//			@Override
//			public void mouseDown(MouseEvent mouseEvent) {
//
//				GridItem gridItem = indexGrid.getItem(new Point(mouseEvent.x, mouseEvent.y));
//				
//				if ((gridItem != null) && (gridItem.getData() != null)) {
//
//					UIIndexItem uiIndexItem = (UIIndexItem) gridItem.getData();
//					
//					String baseUrl = uiIndexItem.getBaseUrl();
//					
//					if (baseUrl != null) {
//						
//						loadHTML(baseUrl);
//					}
//				}
//			}
//		});
//	    
//	    indexGrid.addMouseMoveListener(new MouseMoveListener() {
//			
//			@Override
//			public void mouseMove(MouseEvent mouseEvent) {
//				
//				GridItem gridItem = indexGrid.getItem(new Point(mouseEvent.x, mouseEvent.y));
//
//				if ((gridItem == null) || (gridItem.getData() == null)) {
//					
//					indexGrid.setCursor(null);
//					bottomLeftLabel.setText("");
//					return;
//				}
//				
//				UIIndexItem uiIndexItem = (UIIndexItem) gridItem.getData();
//				
//				if (uiIndexItem.getRef() != null) {
//
//					bottomLeftLabel.setText(uiIndexItem.getRef());
//				}
//				else {
//
//					bottomLeftLabel.setText("");
//				}
//				
//				if (uiIndexItem.getBaseUrl() != null) {
//
//					indexGrid.setCursor(cursorHand);
//				}
//				else {
//
//					indexGrid.setCursor(null);
//				}
//
//			}
//		});
//	    
//	    indexGrid.addMouseTrackListener(new MouseTrackAdapter() {
//
//			@Override
//			public void mouseEnter(MouseEvent mouseEvent) {
//
//				indexGrid.setCursor(null);
//			}
//
//			@Override
//			public void mouseExit(MouseEvent mouseEvent) {
//
//				indexGrid.setCursor(null);
//				bottomLeftLabel.setText("");
//			}
//		});
//
//	    GridColumn indexGridColumn = new GridColumn(indexGrid, SWT.NONE);
//	    indexGridColumn.setWidth(300);
//	    indexGridColumn.setWordWrap(true);
//	    
//		
//	    /* goto search results */
//	    
//	    searchResultsComposite = new Composite(leftContentComposite, SWT.NONE);
//	    searchResultsComposite.setVisible(false);
//	    searchResultsComposite.setLayoutData(newGridDataFILLFILLtruetrue());
//	    ((GridData) searchResultsComposite.getLayoutData()).exclude = true;
//	    searchResultsComposite.setLayout(newGridLayout00001());
//
//	    searchResultsComposite.addControlListener(new ControlAdapter() {
//
//			@Override
//			public void controlResized(ControlEvent controlEvent) {
//
//				searchResultsGrid.getColumn(1).setWidth(searchResultsComposite.getClientArea().width - 
//						(searchResultsGrid.getColumn(0).getWidth() +  searchResultsGrid.getVerticalBar().getSize().x));
//			}
//		});
//
//	    
//	    searchResultsGrid = new Grid(searchResultsComposite, SWT.V_SCROLL | SWT.H_SCROLL);
//	    searchResultsGrid.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//	    searchResultsGrid.setAutoHeight(true);
//	    searchResultsGrid.setLinesVisible(false);
////	    searchResultsGrid.setSelectionEnabled(false);
//	    
//	    searchResultsGrid.addMouseListener(new MouseAdapter() {
//
//			@Override
//			public void mouseDown(MouseEvent mouseEvent) {
//
//				GridItem gridItem = searchResultsGrid.getItem(new Point(mouseEvent.x, mouseEvent.y));
//				
//				if ((gridItem != null) && (gridItem.getData("baseUrl") != null)) {
//
//					loadHTML((String) gridItem.getData("baseUrl"));
//				}
//			}
//		});
//	    
//	    searchResultsGrid.addMouseMoveListener(new MouseMoveListener() {
//			
//			@Override
//			public void mouseMove(MouseEvent mouseEvent) {
//				
//				GridItem gridItem = searchResultsGrid.getItem(new Point(mouseEvent.x, mouseEvent.y));
//
//				if (gridItem == null) {
//					
//					searchResultsGrid.setCursor(null);
//					bottomLeftLabel.setText("");
//					return;
//				}
//				
//				if (gridItem.getData("ref") != null) {
//
//					bottomLeftLabel.setText((String) gridItem.getData("ref"));
//				}
//				else {
//
//					bottomLeftLabel.setText("");
//				}
//				
//				if (gridItem.getData("baseUrl") != null) {
//
//					searchResultsGrid.setCursor(cursorHand);
//				}
//				else {
//
//					searchResultsGrid.setCursor(null);
//				}
//
//			}
//		});
//	    
//	    searchResultsGrid.addMouseTrackListener(new MouseTrackAdapter() {
//
//			@Override
//			public void mouseEnter(MouseEvent mouseEvent) {
//
//				searchResultsGrid.setCursor(null);
//			}
//
//			@Override
//			public void mouseExit(MouseEvent mouseEvent) {
//
//				searchResultsGrid.setCursor(null);
//				bottomLeftLabel.setText("");
//			}
//		});
//	    
//	    GridColumn columnImage = new GridColumn(searchResultsGrid, SWT.NONE);
//	    columnImage.setWidth(32);
//	    GridColumn columnText = new GridColumn(searchResultsGrid, SWT.NONE);
//	    columnText.setWordWrap(true);
//	    columnText.setWidth(100);
//	    
//
//	    /* goto bookmarks */
//	    
//	    final Composite bookmarksComposite = new Composite(leftContentComposite, SWT.NONE);
//	    bookmarksComposite.setVisible(false);
//	    bookmarksComposite.setLayoutData(newGridDataFILLFILLtruetrue());
//	    ((GridData) bookmarksComposite.getLayoutData()).exclude = true;
//	    bookmarksComposite.setLayout(newGridLayout00001());
//
//	    bookmarksComposite.addControlListener(new ControlAdapter() {
//
//			@Override
//			public void controlResized(ControlEvent controlEvent) {
//
//				bookmarksGrid.getColumn(1).setWidth(bookmarksComposite.getClientArea().width - 
//						(bookmarksGrid.getColumn(0).getWidth() +  bookmarksGrid.getVerticalBar().getSize().x));
//			}
//		});
//	    
//	    bookmarksGrid = new Grid(bookmarksComposite, SWT.V_SCROLL | SWT.H_SCROLL);
//	    bookmarksGrid.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//	    bookmarksGrid.setAutoHeight(true);
//	    bookmarksGrid.setLinesVisible(false);
//
//	    bookmarksGrid.addMouseListener(new MouseAdapter() {
//
//			@Override
//			public void mouseDown(MouseEvent mouseEvent) {
//
//				GridItem gridItem = bookmarksGrid.getItem(new Point(mouseEvent.x, mouseEvent.y));
//				
//				if ((gridItem != null) && (gridItem.getData() != null)) {
//
//					UIBookmarkItem uiBookmarkItem = (UIBookmarkItem) gridItem.getData();
//					
//					String baseUrl = uiBookmarkItem.getBaseUrl();
//					
//					if (baseUrl != null) {
//						
//						loadHTML(baseUrl);
//					}
//				}
//			}
//		});
//	    
//	    bookmarksGrid.addMouseMoveListener(new MouseMoveListener() {
//			
//			@Override
//			public void mouseMove(MouseEvent mouseEvent) {
//				
//				GridItem gridItem = bookmarksGrid.getItem(new Point(mouseEvent.x, mouseEvent.y));
//
//				if ((gridItem == null) || (gridItem.getData() == null)) {
//					
//					bookmarksGrid.setCursor(null);
//					bottomLeftLabel.setText("");
//					return;
//				}
//				
//				UIBookmarkItem uiBookmarkItem = (UIBookmarkItem) gridItem.getData();
//				
//				if (uiBookmarkItem.getRef() != null) {
//
//					bottomLeftLabel.setText(uiBookmarkItem.getRef());
//				}
//				else {
//
//					bottomLeftLabel.setText("");
//				}
//				
//				if (uiBookmarkItem.getBaseUrl() != null) {
//
//					bookmarksGrid.setCursor(cursorHand);
//				}
//				else {
//
//					bookmarksGrid.setCursor(null);
//				}
//
//			}
//		});
//	    
//	    bookmarksGrid.addMouseTrackListener(new MouseTrackAdapter() {
//
//			@Override
//			public void mouseEnter(MouseEvent mouseEvent) {
//
//				bookmarksGrid.setCursor(null);
//			}
//
//			@Override
//			public void mouseExit(MouseEvent mouseEvent) {
//
//				bookmarksGrid.setCursor(null);
//				bottomLeftLabel.setText("");
//			}
//		});
//
//	    GridColumn bookmarksGridColumnImage = new GridColumn(bookmarksGrid, SWT.NONE);
//	    bookmarksGridColumnImage.setWidth(32);
//	    GridColumn bookmarksGridColumnText = new GridColumn(bookmarksGrid, SWT.NONE);
//	    bookmarksGridColumnText.setWordWrap(true);
//	    bookmarksGridColumnText.setWidth(100);
//	    
//	    final Composite leftTabsComposite = new Composite(leftComposite, SWT.NONE);
//	    gridData = new GridData();
//	    gridData.heightHint = 24;
//	    gridData.horizontalAlignment = GridData.FILL;
//	    gridData.grabExcessHorizontalSpace = true;
//	    leftTabsComposite.setLayoutData(gridData);
//	    //leftTabsComposite.setBackground(EHAViewerUtils.getShadowColor(leftTabsComposite.getBackground()));
//	    
//	    GridLayout leftTabsGridLayout = new GridLayout();
//	    leftTabsGridLayout.marginWidth = 0;
//	    leftTabsGridLayout.marginHeight = 0;
//	    //leftTabsGridLayout.marginTop = 1;
//	    leftTabsGridLayout.verticalSpacing = 0;
//	    leftTabsGridLayout.horizontalSpacing = 0;
//	    leftTabsGridLayout.numColumns = 4;
//	    leftTabsGridLayout.makeColumnsEqualWidth = true;
//	    leftTabsComposite.setLayout(leftTabsGridLayout);
//
//	    
//	    final Composite contentsTabComposite = new Composite(leftTabsComposite, SWT.DOUBLE_BUFFERED);
//	    contentsTabComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//	    contentsTabComposite.setBackground(colorGrayHighlight);
//	    contentsTabComposite.addPaintListener(drawTabImagePaintListener(contentsTabComposite, "e_contents_view"));
//	    contentsTabComposite.setToolTipText("Contents");
//	    activeTabComposite = contentsTabComposite;
//	    
//	    final Composite indexTabComposite = new Composite(leftTabsComposite, SWT.DOUBLE_BUFFERED);
//	    indexTabComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//	    indexTabComposite.addPaintListener(newMarginLeftPaintListener(indexTabComposite));
//	    indexTabComposite.addPaintListener(drawTabImagePaintListener(contentsTabComposite, "e_index_view"));
//	    indexTabComposite.setToolTipText("Index");
//	    
//	    searchResultsTabComposite = new Composite(leftTabsComposite, SWT.DOUBLE_BUFFERED);
//	    searchResultsTabComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//	    searchResultsTabComposite.addPaintListener(newMarginLeftPaintListener(searchResultsTabComposite));
//	    searchResultsTabComposite.addPaintListener(drawTabImagePaintListener(contentsTabComposite, "e_search_results_view"));
//	    searchResultsTabComposite.setToolTipText("Search Results");
//	    
//	    final Composite bookmarksTabComposite = new Composite(leftTabsComposite, SWT.DOUBLE_BUFFERED);
//	    bookmarksTabComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//	    bookmarksTabComposite.addPaintListener(newMarginLeftPaintListener(bookmarksTabComposite));
//	    bookmarksTabComposite.addPaintListener(drawTabImagePaintListener(contentsTabComposite, "e_bookmarks_view"));
//	    bookmarksTabComposite.setToolTipText("Bookmarks");
//
//	    contentsTabComposite.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseDown(MouseEvent e) {
//				
//				changeTabComposite("Contents", contentsTabComposite, contentComposite, contentToolbar);
//			}
//		});
//
//	    indexTabComposite.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseDown(MouseEvent e) {
//
//				changeTabComposite("Index", indexTabComposite, indexComposite, indexToolbar);
//			}
//		});
//
//	    searchResultsTabComposite.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseDown(MouseEvent e) {
//
//				changeTabComposite("Search Results", searchResultsTabComposite, searchResultsComposite, searchResultsToolbar);
//			}
//		});
//
//	    bookmarksTabComposite.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseDown(MouseEvent e) {
//
//				changeTabComposite("Bookmarks", bookmarksTabComposite, bookmarksComposite, bookmarksToolbar);
//			}
//		});
//
//	    leftComposite.addPaintListener(drawMarginPaintListener(leftComposite, leftHeaderComposite, leftTabsComposite));
//
//
//	    /* Right part */
//	    final Composite rightComposite = new Composite(middleSashForm, SWT.NONE);
//
//	    //rightComposite.setBackground(new Color(display, 0, 255, 255));
//	    rightComposite.setLayout(leftGridLayout);	    
//	    
//	    final Composite rightToolbarComposite = new Composite(rightComposite, SWT.NONE);
//	    gridData = new GridData();
//	    gridData.horizontalAlignment = GridData.FILL;
//	    gridData.grabExcessHorizontalSpace = true;
//	    rightToolbarComposite.setLayoutData(gridData);
//
//	    GridLayout rightToolbarGridLayout = new GridLayout();
//	    rightToolbarGridLayout.marginWidth = 4;
//	    rightToolbarGridLayout.marginHeight = 4;
//	    rightToolbarGridLayout.verticalSpacing = 0;
//	    rightToolbarGridLayout.horizontalSpacing = 0;
//	    rightToolbarGridLayout.numColumns = 1;
//	    rightToolbarComposite.setLayout(rightToolbarGridLayout);
//	    
//		ToolBar rightToolbar = new ToolBar(rightToolbarComposite, SWT.FLAT | SWT.WRAP | SWT. HORIZONTAL);
//		rightToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
//		
//		// "e_forward", "e_home", "e_synch_toc_nav", "e_add_bkmrk", "e_print_topic", "maximize"
//
//		toolItem = newToolItem(rightToolbar, "e_back", "Go Back");
//		toolItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//
//				if (baseUrlHistoryIndex > 0) {
//					
//					baseUrlHistoryIndex--;
//					loadHTML(baseUrlHistory.get(baseUrlHistoryIndex));
//				}
//			}
//		});
//
//		toolItem = newToolItem(rightToolbar, "e_forward", "Go Forward");
//		toolItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//
//				if (baseUrlHistoryIndex < baseUrlHistory.size() - 1) {
//					
//					baseUrlHistoryIndex++;
//					loadHTML(baseUrlHistory.get(baseUrlHistoryIndex));
//				}
//			}
//		});
//
//		toolItem = newToolItem(rightToolbar, "e_home", "Home");
//		toolItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//
//				if (contentsTree.getSelectionCount() == 0) {
//					
//					return;
//				}
//				
//				TreeItem treeItem = contentsTree.getSelection()[0];
//				
//				while (treeItem.getParentItem() != null) {
//					
//					treeItem = treeItem.getParentItem();
//				}
//				
//				contentsTree.setSelection(treeItem);
//				UITocItem tocItem = (UITocItem) treeItem.getData();
//				loadHTML(tocItem.getBaseUrl());
//			}
//		});
//
//	    toolItem = new ToolItem(rightToolbar, SWT.SEPARATOR);
//		
//	    toolItem = newToolItem(rightToolbar, "e_synch_toc_nav", "Show in Table of Contents");
//		toolItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//
//				syncContents(true);
//			}
//		});
//
//	    toolItem = newToolItem(rightToolbar, "e_add_bkmrk", "Bookmark Document");
//		toolItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//
//				for (XMLPlugin plugin : plugins) {
//					
//					String cacheFolderPluginPath = plugin.getCacheFolderPluginPath().replace("\\", "/");
//					
//					if (activeBaseUrl.contains(cacheFolderPluginPath)) {
//						
//						String theRef = activeBaseUrl.substring(activeBaseUrl.indexOf(cacheFolderPluginPath) + cacheFolderPluginPath.length() + 1);
//
//						String theName = "Title not found";
//						
//						URL url = getUrl(activeBaseUrl);
//						if (url == null) {
//							
//							return;
//						}
//
//						TreeItem treeItem = findToc(url.toString());
//						
//						if (treeItem != null) {
//							
//							UITocItem uiTocItem = (UITocItem) treeItem.getData();
//							theName = uiTocItem.getName();
//						}
//						else {
//							
//							String theHtml = browser.getText();
//							
//							int indexStart = theHtml.indexOf("<title>");
//							int indexEnd = theHtml.indexOf("</title>");
//
//							if (indexStart == -1) {
//								
//								indexStart = theHtml.indexOf("<TITLE>");
//								indexEnd = theHtml.indexOf("</TITLE>");
//							}
//							
//							if (indexStart != -1) {
//								
//								theName = theHtml.substring(indexStart + 7, indexEnd);
//							}
//						}
//						
//						EHAViewerProperties.addBookmark(theName, plugin.getPluginID() + "/" + theRef);
//						
//						uiBookmarkItems.clear();
//						for (XMLPlugin thePlugin : plugins) {
//							
//							thePlugin.loadUIBookmarkItems(uiBookmarkItems);
//						}
//						loadBookmarksGrid();
//					}
//				}
//
//			}
//		});
//
//	    toolItem = newToolItem(rightToolbar, "e_print_topic", "Print Page");
//	    toolItem.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//
//				browser.execute("javascript:window.print();");
//			}
//		});
//	    
//	    toolItem = newToolItem(rightToolbar, "maximize", "Maximize");
//		toolItem.addSelectionListener(newMaximizeSelectionAdapter(rightComposite));
//		
//		rightToolbarComposite.pack();
//
//	    rightComposite.addPaintListener(drawMarginPaintListener(rightComposite, rightToolbarComposite, null));
//
//
//		browser = new Browser(rightComposite, SWT.NONE);
//		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		
//		browser.addLocationListener(new LocationAdapter() {
//
//			@Override
//			public void changing(LocationEvent locationEvent) {
//
//				String location = locationEvent.location;
////				L.p("changing = " + location);
////				L.p("changingu = " + browser.getUrl());
//				
//				if (location.startsWith("javascript:")) {
//
//					browser.execute(location);
//					locationEvent.doit = false;
//					return;
//				}
//				
//				URL url = getUrl(location);
//
//				if (url != null) {
//					
//					if (location.startsWith("http:/") || location.startsWith("https:/")) {
//						
//						locationEvent.doit = false;
//						location = "file:/" + location;
//					}
//
//					loadHTML(location);
//				}
//				else {
//
//					if (!location.startsWith("about:blank")) {
//					
//						locationEvent.doit = false;
//					}
//				}
//			}
//		});
//		
//		browser.addOpenWindowListener(new OpenWindowListener() {
//			
//			@Override
//			public void open(WindowEvent windowEvent) {
//
////				L.p("OpenWindowListener");
//
//				windowEvent.browser = browser;
//			}
//		});
//		
//		browser.addStatusTextListener(new StatusTextListener() {
//			
//			@Override
//			public void changed(StatusTextEvent statusTextEvent) {
//
//				URL url = getUrl(statusTextEvent.text);
//
//				if (url != null) {
//					
//					String strUrl = url.toString();
//					
//					for (XMLPlugin xmlPlugin : plugins) {
//						
//						String prefix = "file:/" + xmlPlugin.getCacheFolderPluginPath().replace("\\", "/") + "/";
//						
//						if (strUrl.startsWith(prefix)) {
//							
//							strUrl = strUrl.replace(prefix, "");
//							
//							if (strUrl.startsWith("help/nav/")) {
//								
//								strUrl = "";
//							}
//						}
//					}
//					
//					bottomLeftLabel.setText(strUrl);	
//				}
//				else {
//					
//					bottomLeftLabel.setText("");
//				}
//			}
//		});
//		
//	    middleSashForm.setWeights(new int[] {10, 25});
//
//	    Composite bottomComposite = new Composite(mainShell, SWT.NONE);
//	    //bottomComposite.setBackground(new Color(display, 255, 0, 0));
//	    gridData = new GridData();
//	    gridData.heightHint = 24;
//	    gridData.horizontalAlignment = GridData.FILL;
//	    gridData.grabExcessHorizontalSpace = true;
//	    bottomComposite.setLayoutData(gridData);
//	    
//	    GridLayout bottomGridLayout = new GridLayout();
//	    bottomGridLayout.marginWidth = 4;
//	    bottomGridLayout.marginHeight = 4;
//	    bottomGridLayout.verticalSpacing = 0;
//	    bottomGridLayout.horizontalSpacing = 4;
//	    bottomGridLayout.numColumns = 2;
//	    bottomComposite.setLayout(bottomGridLayout);
//	    
//	    bottomLeftLabel = new Label(bottomComposite, SWT.NONE);
//	    //bottomLeftLabel.setText("Glkmn sqmogj lgjeaipgj eoig");
//	    //bottomLeftLabel.setBackground(new Color(display, 255, 0, 0));
//	    gridData = new GridData();
//	    gridData.heightHint = 16;
//	    gridData.horizontalAlignment = GridData.FILL;
//	    gridData.grabExcessHorizontalSpace = true;
//	    bottomLeftLabel.setLayoutData(gridData);
//	    
////		browser.setUrl("about:blank");
//		loadStartHTML();
//	    
//	    /* Load all cache */
//		EHAViewerCache.load(LoadSortType.firstUsedAsc);
//		
//		ArrayList<String> pluginPaths = new ArrayList<>();
//		
//		for (EHAViewerCache ehaViewerCache : EHAViewerCache.getEhaCaches()) {
//
//			pluginPaths.add(ehaViewerCache.getCompleteFilePath());
//		}
//		
//		for (String pluginPath : pluginPaths) {
//
//			XMLPlugin xmlPlugin = EHAViewerEngine.loadPluginFile(pluginPath);
//			
//			plugins.add(xmlPlugin);
//			
//			/* TOC */
//			UITocItem primaryTocItem = xmlPlugin.loadUITocItems();
//			loadContentsTree(primaryTocItem);
//			
//			/* Index */
//			xmlPlugin.loadUIIndexItems(uiIndexItems);
//
//			/* Bookmarks */
//			xmlPlugin.loadUIBookmarkItems(uiBookmarkItems);
//			loadBookmarksGrid();
//		}
		
	    
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

	/** View a CSV file in a grid */
	private void createViewCsvTab() {
		
		viewCsvTabComposite = new Composite(mainShell, SWT.NONE);
		//viewCsvTabComposite.setBackground(new Color(display, 0, 255, 255));
		viewCsvTabComposite.setLayoutData(createGridData(GridData.FILL, GridData.FILL, true, true, -1, -1));
		viewCsvTabComposite.setLayout(createGridLayout(8, 8, 0, 8, 1));

		final Label viewCsvTabLabel = new Label(viewCsvTabComposite, SWT.NONE);
		viewCsvTabLabel.setText("View .csv");
		
		
		final Composite csvFileComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		//csvFileComposite.setBackground(new Color(display, 255, 255, 0));
		csvFileComposite.setLayoutData(createGridData(GridData.FILL, GridData.CENTER, true, false, -1, -1));
		csvFileComposite.setLayout(createGridLayout(0, 0, 8, 0, 3));
		
		final Label csvFileLabel = new Label(csvFileComposite, SWT.NONE);
		csvFileLabel.setText("File");
		csvFileLabel.setLayoutData(createGridData(GridData.BEGINNING, GridData.CENTER, false, false, 120, -1));
		
		final Text csvFileText = new Text(csvFileComposite, SWT.SINGLE | SWT.BORDER);
		csvFileText.setText("C:\\Iustin\\Programming\\_static-catalog\\tools\\datas\\big.csv");
		csvFileText.setLayoutData(createGridData(GridData.FILL, GridData.CENTER, true, false, -1, -1));
		
		final Button csvFileButton = new Button(csvFileComposite, SWT.NONE);
		csvFileButton.setText("Browse");
		csvFileButton.setLayoutData(createGridData(GridData.BEGINNING, GridData.CENTER, false, false, 120, -1));

		
		final Composite csvButtonsComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		//csvButtonsComposite.setBackground(new Color(display, 255, 255, 0));
		csvButtonsComposite.setLayoutData(createGridData(GridData.FILL, GridData.CENTER, true, false, -1, -1));
		csvButtonsComposite.setLayout(createGridLayout(0, 0, 8, 0, 5));
		
		final Button csvLoadButton = new Button(csvButtonsComposite, SWT.NONE);
		csvLoadButton.setText("Load");
		csvLoadButton.setLayoutData(createGridData(GridData.BEGINNING, GridData.CENTER, false, false, 120, -1));

		final Text csvLoadLinesText = new Text(csvButtonsComposite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		csvLoadLinesText.setText("2000000");
		csvLoadLinesText.setLayoutData(createGridData(GridData.BEGINNING, GridData.CENTER, false, false, 120, -1));

		final Button csvExtractButton = new Button(csvButtonsComposite, SWT.NONE);
		csvExtractButton.setText("Extract");
		csvExtractButton.setLayoutData(createGridData(GridData.BEGINNING, GridData.CENTER, false, false, 120, -1));

		
		final Composite csvStatusComposite = new Composite(viewCsvTabComposite, SWT.NONE);
		csvStatusComposite.setBackground(new Color(display, 255, 255, 255));
		csvStatusComposite.setLayoutData(createGridData(GridData.FILL, GridData.CENTER, true, false, -1, -1));
		csvStatusComposite.setLayout(createGridLayout(0, 0, 8, 0, 5));

		final Label csvStatusLabel = new Label(csvStatusComposite, SWT.NONE);
		csvStatusLabel.setBackground(new Color(display, 255, 255, 255));
		csvStatusLabel.setLayoutData(createGridData(GridData.FILL, GridData.BEGINNING, true, false, -1, -1));
		csvStatusLabel.setText("Status");

		
		final Grid csvFileGrid = new Grid(viewCsvTabComposite, SWT.V_SCROLL | SWT.H_SCROLL);
		csvFileGrid.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
		
		csvLoadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				csvFileGrid.clearItems();
				
				long start = System.currentTimeMillis();
				
				//ArrayList<String> arrayList = new ArrayList<>(500_000);
				ArrayList<String> arrayList = new ArrayList<>();
				
				long maxLines = Long.parseLong(csvLoadLinesText.getText());

//				GridColumn fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
//				fieldGridColumn.setWordWrap(true);
//				fieldGridColumn.setWidth(500);
				
//				try {
//					Scanner scanner = new Scanner(new File(csvFileText.getText()));
//					
//					long csvLineIndex = 0;
////					while (scanner.hasNextLine()) {
//					String line = scanner.nextLine();					
//					while (line != null) {
//						
////						String line = scanner.nextLine();
//						
//						csvLineIndex++;
//						if (csvLineIndex % 10000 == 0) {
//							//L.p("csvLineIndex = " + csvLineIndex);
//							csvStatusLabel.setText(csvLineIndex + " lines...");
//						}
//						
////						GridItem csvGridItem = new GridItem(csvFileGrid, SWT.NONE);
////						csvGridItem.setText(0, line);
//						
//						line = scanner.nextLine();
//					}
//					
//					scanner.close();
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				catch (NoSuchElementException noSuchElementException) {
//					// TODO: handle exception
//				}
				
				// vendezvotrevoiture.fr

				CsvParser csvParser = new CsvParser(new CsvParserSettings());
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
					
					csvLine = csvParser.parseNext();
				}
				
				
//				CsvParser csvParser = new CsvParser(new CsvParserSettings());
//				csvParser.beginParsing(new File(csvFileText.getText()));
//				
//				String[] csvLine = csvParser.parseNext();
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
////					String s = "";
////					StringBuilder sb = new StringBuilder();
////					for (int index1 = 0; index1 < 5; index1++) {
////						s = s + String.join(",", csvLine);
////						//s = s + csvLineIndex + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
////						//arrayList.add(String.join(",", csvLine));
////					}
//
//					for (int index1 = 0; index1 < 10; index1++) {
////						s = s + String.join(",", csvLine);
////						s = s + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
//						//arrayList.add(String.join(",", csvLine));
//						arrayList.add(csvLineIndex + index1 + "qsfq");
//						//arrayList.add("qsfq");
//					}
////					arrayList.add(s);
//
////					arrayList.add("aa");
//					
//					
//					//arrayList.add(String.join(",", csvLine));
//					
////					if (csvLineIndex == 1) {
////						for (int index = 0; index < lineLength + 1; index++) {
////							GridColumn fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
////						    fieldGridColumn.setWordWrap(true);
////						    fieldGridColumn.setWidth(150);
////						}
////					}
////					
////					GridItem csvGridItem = new GridItem(csvFileGrid, SWT.NONE);
////					csvGridItem.setText(0, csvLineIndex + "");
////					for (int index = 0; index < lineLength; index++) {
//////					for (int index = 0; index < 1; index++) {						
////						csvGridItem.setText(index + 1, csvLine[index] + "");
////					}
//
//					if (csvLineIndex == maxLines) {
//						csvStatusLabel.setText("Load done in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
//						return;
//					}
//
//					csvLine = csvParser.parseNext();
//				}
				 
				csvStatusLabel.setText("Done load in " + ((System.currentTimeMillis() - start) / 1000) + " seconds. al = " + arrayList.size());
				//L.p("Done = " + ((System.currentTimeMillis() - start) / 1000));
			}
		});
		
		
		
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
//				CsvParser csvParser = new CsvParser(new CsvParserSettings());
//				csvParser.beginParsing(new File(csvFileText.getText()));
//				
//				String[] csvLine = csvParser.parseNext();
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
////					String s = "";
////					StringBuilder sb = new StringBuilder();
////					for (int index1 = 0; index1 < 5; index1++) {
////						s = s + String.join(",", csvLine);
////						//s = s + csvLineIndex + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
////						//arrayList.add(String.join(",", csvLine));
////					}
//
//					for (int index1 = 0; index1 < 10; index1++) {
////						s = s + String.join(",", csvLine);
////						s = s + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
//						//arrayList.add(String.join(",", csvLine));
//						arrayList.add(csvLineIndex + index1 + "qsfq");
//						//arrayList.add("qsfq");
//					}
////					arrayList.add(s);
//
////					arrayList.add("aa");
//					
//					
//					//arrayList.add(String.join(",", csvLine));
//					
////					if (csvLineIndex == 1) {
////						for (int index = 0; index < lineLength + 1; index++) {
////							GridColumn fieldGridColumn = new GridColumn(csvFileGrid, SWT.NONE);
////						    fieldGridColumn.setWordWrap(true);
////						    fieldGridColumn.setWidth(150);
////						}
////					}
////					
////					GridItem csvGridItem = new GridItem(csvFileGrid, SWT.NONE);
////					csvGridItem.setText(0, csvLineIndex + "");
////					for (int index = 0; index < lineLength; index++) {
//////					for (int index = 0; index < 1; index++) {						
////						csvGridItem.setText(index + 1, csvLine[index] + "");
////					}
//
//					if (csvLineIndex == maxLines) {
//						csvStatusLabel.setText("Load done in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
//						return;
//					}
//
//					csvLine = csvParser.parseNext();
//				}
//				 
//				csvStatusLabel.setText("Done load in " + ((System.currentTimeMillis() - start) / 1000) + " seconds. al = " + arrayList.size());
//				//L.p("Done = " + ((System.currentTimeMillis() - start) / 1000));
//			}
//		});
		
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
				CsvParser csvParser = new CsvParser(new CsvParserSettings());
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
							
							if (diff < 100) {
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
	
	/** Create grid layout */
	public static GridLayout createGridLayout(int marginWidth, int marginHeight, int horizontalSpacing, int verticalSpacing, int numColumns) {
		
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.marginWidth = marginWidth;
	    gridLayout.marginHeight = marginHeight;
	    gridLayout.horizontalSpacing = horizontalSpacing;
	    gridLayout.verticalSpacing = verticalSpacing;
	    gridLayout.numColumns = numColumns;
	    
	    return gridLayout;
	}

	/** Create grid data */
	public static GridData createGridData(int horizontalAlignment, int verticalAlignment,
			boolean grabExcessHorizontalSpace, boolean grabExcessVerticalSpace, int widthHint, int heightHint) {
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = horizontalAlignment;
		gridData.verticalAlignment = verticalAlignment;
		gridData.grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		gridData.grabExcessVerticalSpace = grabExcessVerticalSpace;
		gridData.heightHint = heightHint;
		gridData.widthHint = widthHint;
	    
	    return gridData;
	}

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
