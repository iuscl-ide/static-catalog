/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/** Edit a field window */
public class StaticCatalogGeneratorFieldEditWindow {

	
	/** The window */
	private Shell fieldEditShell;
	
	/** Create window */
	public void createFieldEditWindow(Shell mainShell) {

//		// DEBUG
//		DeviceData data = new DeviceData();
//	    data.tracking = true;
//	    display = new Display(data);
//	    Sleak sleak = new Sleak();
//	    sleak.open();
//	    // /DEBUG
		
		/* Display */
		Display display = mainShell.getDisplay();
		UI ui = new UI(false, display);

		/* Modal window */
		fieldEditShell = new Shell(mainShell, SWT.NONE | SWT.TITLE | SWT.BORDER | SWT.CLOSE | SWT.APPLICATION_MODAL);
		fieldEditShell.setText("static-catalog Generator Edit Field");
		fieldEditShell.setLayout(ui.createMarginsVerticalSpacingGridLayout(UI.sep8, UI.sep8));

		/* Icon */
		Image[] iconImages = new Image[9];
		String[] rez = { "16", "24", "32", "48", "64", "96", "128", "256", "512" };
		for (int index = 0; index < 9; index++) {
			String rezimg = rez[index];
			iconImages[index] = ui.getResourceAsImage("org/static_catalog/res/icon/" + rezimg + "x" + rezimg + ".png");
		}
		fieldEditShell.setImages(iconImages);

		/* Location */
		int width = 640;
		int height = 640;

		fieldEditShell.setSize(width, height);
		
		Point mainShellLocation = mainShell.getLocation();
		Point mainShellSize = mainShell.getSize();
		
		fieldEditShell.setLocation(mainShellLocation.x + (mainShellSize.x - width) / 2 ,mainShellLocation.y + (mainShellSize.y - height) / 2);
		
		fieldEditShell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent shellEvent) {
				
				shellEvent.doit = false;
				fieldEditShell.setVisible(false);
			}
		});

	    final Composite nameComposite = new Composite(fieldEditShell, SWT.NONE);
	    ui.addDebug(nameComposite);
	    nameComposite.setLayoutData(ui.createFillHorizontalGridData());
	    nameComposite.setLayout(ui.createColumnsSpacingGridLayout(6, UI.sep8));

	    final Label indexLabel = new Label(nameComposite, SWT.NONE);
	    indexLabel.setLayoutData(ui.createWidthGridData(100));
	    indexLabel.setText("Index");

	    final Label indexValueLabel = new Label(nameComposite, SWT.NONE);
	    indexValueLabel.setLayoutData(ui.createWidthGridData(100));
	    indexValueLabel.setText("Index");
	    indexValueLabel.setFont(StaticCatalogGeneratorMainWindow.fontBold);
	    
	    
	    final Label indexInLineLabel = new Label(nameComposite, SWT.NONE);
	    indexInLineLabel.setLayoutData(ui.createWidthGridData(100));
	    indexInLineLabel.setText("Index in Line");

	    final Label indexInLineValueLabel = new Label(nameComposite, SWT.NONE);
	    indexInLineValueLabel.setLayoutData(ui.createWidthGridData(100));
	    indexInLineValueLabel.setText("Index in Line");
	    indexInLineValueLabel.setFont(StaticCatalogGeneratorMainWindow.fontBold);
	    
	    
	    final Label nameLabel = new Label(nameComposite, SWT.NONE);
	    nameLabel.setLayoutData(ui.createWidthGridData(100));
	    nameLabel.setText("Field Name");

	    final Label nameValueLabel = new Label(nameComposite, SWT.NONE);
	    nameValueLabel.setLayoutData(ui.createWidthGridData(100));
	    nameValueLabel.setText("Field Name");
	    nameValueLabel.setFont(StaticCatalogGeneratorMainWindow.fontBold);

	    
	    final Composite middleComposite = new Composite(fieldEditShell, SWT.NONE);
	    ui.addDebug(middleComposite);
	    middleComposite.setLayoutData(ui.createFillBothGridData());
	    middleComposite.setLayout(ui.createGridLayout());

	    final Label separator = new Label(fieldEditShell, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(ui.createFillHorizontalGridData());

	    final Composite bottomComposite = new Composite(fieldEditShell, SWT.NONE);
	    ui.addDebug(bottomComposite);
	    bottomComposite.setLayoutData(ui.createFillHorizontalGridData());
	    bottomComposite.setLayout(ui.createColumnsSpacingGridLayout(3, UI.sep8));

	    final Composite fillLeftBottomComposite = new Composite(bottomComposite, SWT.NONE);
	    ui.addDebug(fillLeftBottomComposite);
	    fillLeftBottomComposite.setLayoutData(ui.createFillHorizontalGridData());
	    fillLeftBottomComposite.setLayout(ui.createColumnsGridLayout(1));
	    
		final Button okButton = new Button(bottomComposite, SWT.NONE);
		okButton.setText("OK");
		okButton.setLayoutData(ui.createWidth120GridData());

		final Button cancelButton = new Button(bottomComposite, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(ui.createWidth120GridData());
		
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				
				fieldEditShell.setVisible(false);
			}
		});
	}
	
	/** Create window */
	public void showFieldEditWindow() {

		fieldEditShell.setVisible(true);
		fieldEditShell.moveAbove(null);
	}
}
