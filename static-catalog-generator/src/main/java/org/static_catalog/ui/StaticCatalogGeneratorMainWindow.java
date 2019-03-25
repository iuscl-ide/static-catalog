/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.ui;

import java.util.ArrayList;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.static_catalog.main.S;

/** Generator main window */
public class StaticCatalogGeneratorMainWindow {

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

		/* Display */
		Display.setAppName("static-catalog");
		display = new Display();

		/* Main window */
		Shell mainShell = new Shell(display);
		mainShell.setText("static-catalog Generator");

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

	    
	    /* Layout */
	    int sep = 8;
	    isDebug = true;
	    
	    GridData gridData;
	    GridLayout gridLayout;
	    
	    gridLayout = createGridLayout();
	    gridLayout.verticalSpacing = sep;
	    gridLayout.marginTop = sep;
	    gridLayout.marginBottom = sep;
	    gridLayout.marginLeft = sep;
	    gridLayout.marginRight = sep;
	    mainShell.setLayout(gridLayout);

	    Composite topComposite = new Composite(mainShell, SWT.NONE);
	    addDebug(topComposite);
	    gridData = createGridData();
	    gridData.horizontalAlignment = SWT.FILL;
	    gridData.grabExcessHorizontalSpace = true;
	    topComposite.setLayoutData(gridData);
	    gridLayout = createGridLayout();
	    topComposite.setLayout(gridLayout);

	    Composite topButtonsComposite = new Composite(topComposite, SWT.NONE);
	    addDebug(topButtonsComposite);
	    gridData = createGridData();
	    gridData.horizontalAlignment = SWT.CENTER;
	    gridData.grabExcessHorizontalSpace = true;
	    topButtonsComposite.setLayoutData(gridData);
	    gridLayout = createGridLayout();
	    gridLayout.horizontalSpacing = sep;
	    gridLayout.numColumns = 3;
	    topButtonsComposite.setLayout(gridLayout);
	    
	    String[] topButtonTexts = { "View CSV", "Analyse CSV", "Generate" };
	    
	    ArrayList<Button> topButtons = new ArrayList<>(); 

	    for (String topButtonText : topButtonTexts) {

		    Button button = new Button(topButtonsComposite, SWT.TOGGLE);
		    button.setText(topButtonText);
		    gridData = createGridData();
		    gridData.widthHint = 120;
		    button.setLayoutData(gridData);
		    
		    topButtons.add(button);
	    }
	    
	    Label separator = new Label(mainShell, SWT.HORIZONTAL | SWT.SEPARATOR);
	    gridData = createGridData();
	    gridData.horizontalAlignment = SWT.FILL;
	    gridData.grabExcessHorizontalSpace = true;
	    separator.setLayoutData(gridData);
	    
	    
	    ArrayList<Composite> mainComposites = new ArrayList<>(); 
	    
	    for (String topButtonText : topButtonTexts) {

	    	Composite composite = new Composite(mainShell, SWT.NONE);
		    addDebug(composite);
		    gridData = createGridData();
		    gridData.horizontalAlignment = SWT.FILL;
		    gridData.grabExcessHorizontalSpace = true;
		    gridData.verticalAlignment = SWT.FILL;
		    gridData.grabExcessVerticalSpace = true;
		    composite.setLayoutData(gridData);
		    gridLayout = createGridLayout();
		    gridLayout.horizontalSpacing = sep;
		    composite.setLayout(gridLayout);
	    	
		    mainComposites.add(composite);
		    
		    Label label = new Label(composite, SWT.NONE);
		    label.setText(topButtonText);
		    label.setFont(fontBigger);
		    gridData = createGridData();
		    gridData.horizontalAlignment = SWT.FILL;
		    gridData.grabExcessHorizontalSpace = true;
		    label.setLayoutData(gridData);
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
