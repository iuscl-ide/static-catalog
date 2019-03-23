package org.static_catalog.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.static_catalog.main.L;

public class StaticCatalogGeneratorMainWindow2 {

	/** Main display */
	private Display display;

	/** Main Window instance */
	private static StaticCatalogGeneratorMainWindow2 mainWindowInstance;

	/** The background shell */
	private Shell mainShell;

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

		Display.setAppName("static-catalog");
		display = new Display();
		
	
		/* Main window */
		mainShell = new Shell(display);
		mainShell.setText("static-catalog Generator");
	
		/* Icon */
//		Image[] iconImages = new Image[9];
//		String[] rez = { "16", "24", "32", "48", "64", "96", "128", "256", "512" };
//		for (int index = 0; index < 9; index++) {
//			String rezimg = rez[index];
//			//iconImages[index] = getResourceAsImage("org/static_catalog/res/icon/" + rezimg + "x" + rezimg + ".png");
//		}
//		mainShell.setImages(iconImages);
		
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
	
	    mainShell.setMenuBar(mainMenuBar);
	    
	    
	    UI ui = new UI();
	    
	    ui.setDebug(true);
	    
	    //mainShell.setBackground(ui.randomColor());
	    
	    //GridLayout gridLayout = ui.gl( "horizontalSpacing: 8" );
	    
	    //GridLayout gridLayout = new GridLayout(); 
//	    gridLayout.horizontalSpacing
	    //BeanUtil.declared.setProperty(gridLayout, "horizontalSpacing", "18");
	
	    //L.p("gridLayout.horizontalSpacing = " + gridLayout.horizontalSpacing);
	    
	    //mainShell.setLayout(ui.l("marginHeight:10", "marginWidth:20", "marginLeft:100"));
//	    mainShell.setLayout(ui.l(UI.L.noMargins, UI.L.margins + ":" + 8, UI.L.noSpacing));

	    mainShell.setLayout(ui.l("margins", "spacing"));

	    Composite topComposite = ui.c(mainShell, ui.d("horizontalFill"), ui.l("spacing"));
	    
	    Composite topCenterButtonsComposite = ui.c(topComposite,
	    		ui.d("horizontalAlignment: " + GridData.CENTER, "grabExcessHorizontalSpace: true"),
	    		ui.l("numColumns: 3"));
	    
	    
	    UI.E topCenterButtonsClick = ui.new E() {
			@Override
			public void onClick(SelectionEvent selectionEvent) {
				L.p("is there");
			}
		};
	    
	    String buttonsWidth120 = "widthHint:120";
	    int buttonsStyleToggle = SWT.TOGGLE;
	    Button viewCSVButton = ui.button(topCenterButtonsComposite, buttonsStyleToggle, "View CSV", ui.d(buttonsWidth120), topCenterButtonsClick);
	    Button analyseCSVButton = ui.button(topCenterButtonsComposite, buttonsStyleToggle, "Analyse CSV", ui.d(buttonsWidth120), topCenterButtonsClick);
	    Button extractCSVButton = ui.button(topCenterButtonsComposite, buttonsStyleToggle, "Extract", ui.d(buttonsWidth120), topCenterButtonsClick);

	    ui.label(topComposite, SWT.HORIZONTAL | SWT.SEPARATOR, "", ui.d("horizontalFill"));
	    
	    Composite viewComposite = ui.c(mainShell, ui.d("bothFill"), ui.l());
	    
	    Composite analyseComposite = ui.c(mainShell, ui.d("bothFill"), ui.l());
	    analyseComposite.setVisible(false);
	    Composite extractComposite = ui.c(mainShell, ui.d("bothFill"), ui.l());
	    extractComposite.setVisible(false);
	    
	    mainShell.layout(true, true);
	    
		/* Run */
		mainShell.open();
		
		/* Application loop */
		while (!mainShell.isDisposed()) {
			
			if (!display.readAndDispatch()) {
				
				display.sleep();
			}
		}
	
		/* Finish */
		display.dispose();
	}
}
