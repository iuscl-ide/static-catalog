/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.static_catalog.model.src.StaticCatalogConfigurationField;

/** Edit a field window */
public class StaticCatalogGeneratorFieldEditWindow {
	
	/** The window */
	private Shell fieldEditShell;
	private StaticCatalogConfigurationField staticCatalogField;
	
	private Label indexValueLabel;
	private Label indexInLineValueLabel;
	private Label nameValueLabel;
	private Text labelText;
	private Combo typeCombo;
	private Button useAsFilterCheckbox;
	private Combo displayTypeCombo;
	private Text maxDisplayValuesText;	
	private Text minDisplayValuesText;
	private Text formatText;
	private Text transformValuesText;	
	private Button sortAscCheckbox;
	private Button sortDescCheckbox;
	private Text sortAscLabelText;
	private Text sortDescLabelText;
	
	/** Create window */
	public void createFieldEditWindow(Shell mainShell, StaticCatalogGeneratorCallback callback) {

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
		fieldEditShell.setText("static-catalog Generator");
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

		Group topGroup = new Group(fieldEditShell, SWT.NONE);
		topGroup.setText("Edit Field");
		topGroup.setLayoutData(ui.createFillBothGridData());
		GridLayout topGroupGridLayout = ui.createColumnsSpacingGridLayout(2, UI.sep8);
		topGroupGridLayout.marginWidth = UI.sep8;
		topGroupGridLayout.marginHeight = UI.sep8;
		topGroupGridLayout.verticalSpacing = UI.sep8;
	    topGroup.setLayout(topGroupGridLayout);
	    
	    final Label indexLabel = new Label(topGroup, SWT.NONE);
	    indexLabel.setText("Index");
	    indexValueLabel = new Label(topGroup, SWT.NONE);
	    indexValueLabel.setLayoutData(ui.createFillHorizontalGridData());
	    indexValueLabel.setFont(StaticCatalogGeneratorMainWindow.fontBold);
	    
	    final Label indexInLineLabel = new Label(topGroup, SWT.NONE);
	    indexInLineLabel.setText("Index in Line");
	    indexInLineValueLabel = new Label(topGroup, SWT.NONE);
	    indexInLineValueLabel.setLayoutData(ui.createFillHorizontalGridData());
	    indexInLineValueLabel.setFont(StaticCatalogGeneratorMainWindow.fontBold);
	    
	    final Label nameLabel = new Label(topGroup, SWT.NONE);
	    nameLabel.setText("Field Name");
	    nameValueLabel = new Label(topGroup, SWT.NONE);
	    nameValueLabel.setLayoutData(ui.createFillHorizontalGridData());
	    nameValueLabel.setFont(StaticCatalogGeneratorMainWindow.fontBold);

	    final Label labelLabel = new Label(topGroup, SWT.NONE);
	    labelLabel.setText("Label");
	    labelText = new Text(topGroup, SWT.SINGLE | SWT.BORDER);
	    labelText.setLayoutData(ui.createFillHorizontalGridData());
	    
	    final Label typeLabel = new Label(topGroup, SWT.NONE);
	    typeLabel.setText("Type");
		typeCombo = new Combo(topGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeCombo.setLayoutData(ui.createWidth120GridData());
		typeCombo.setItems(StaticCatalogGeneratorMainWindow.typeNameValues);

	    final Label useAsFilterLabel = new Label(topGroup, SWT.NONE);
	    useAsFilterLabel.setText("Use as Filter");
		useAsFilterCheckbox = new Button(topGroup, SWT.CHECK);
		
	    final Label displayTypeLabel = new Label(topGroup, SWT.NONE);
	    displayTypeLabel.setText("Filter Display Type");
	    displayTypeCombo = new Combo(topGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
	    displayTypeCombo.setLayoutData(ui.createWidth120GridData());
	    displayTypeCombo.setItems(StaticCatalogGeneratorMainWindow.displayTypeNameValues);
		
	    final Label maxDisplayValuesLabel = new Label(topGroup, SWT.NONE);
	    maxDisplayValuesLabel.setText("Filter Max. Display Values");
	    maxDisplayValuesText = new Text(topGroup, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
	    maxDisplayValuesText.setLayoutData(ui.createWidthGridData(50));

	    final Label minDisplayValuesLabel = new Label(topGroup, SWT.NONE);
	    minDisplayValuesLabel.setText("Filter Min. Display Values");
	    minDisplayValuesText = new Text(topGroup, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
	    minDisplayValuesText.setLayoutData(ui.createWidthGridData(50));

	    final Label formatLabel = new Label(topGroup, SWT.NONE);
	    formatLabel.setText("Filter Format Values");
	    formatText = new Text(topGroup, SWT.SINGLE | SWT.BORDER);
	    formatText.setLayoutData(ui.createFillHorizontalGridData());
	    
	    final Label transformValuesLabel = new Label(topGroup, SWT.NONE);
	    transformValuesLabel.setText("Filter Transform Values");
	    transformValuesText = new Text(topGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
	    GridData transformValuesTextGridData = ui.createFillHorizontalGridData();
	    transformValuesTextGridData.heightHint = 50;
	    transformValuesTextGridData.minimumHeight = 50;
	    //transformValuesText.sets
	    transformValuesText.setLayoutData(transformValuesTextGridData);

	    final Label sortAscLabel = new Label(topGroup, SWT.NONE);
	    sortAscLabel.setText("Sort Asc.");
	    sortAscCheckbox = new Button(topGroup, SWT.CHECK);

	    final Label sortAscLabelLabel = new Label(topGroup, SWT.NONE);
	    sortAscLabelLabel.setText("Sort Asc. Label");
	    sortAscLabelText = new Text(topGroup, SWT.SINGLE | SWT.BORDER);
	    sortAscLabelText.setLayoutData(ui.createFillHorizontalGridData());
	    
	    final Label sortDescLabel = new Label(topGroup, SWT.NONE);
	    sortDescLabel.setText("Sort Desc.");
	    sortDescCheckbox = new Button(topGroup, SWT.CHECK);

	    final Label sortDescLabelLabel = new Label(topGroup, SWT.NONE);
	    sortDescLabelLabel.setText("Sort Desc. Label");
	    sortDescLabelText = new Text(topGroup, SWT.SINGLE | SWT.BORDER);
	    sortDescLabelText.setLayoutData(ui.createFillHorizontalGridData());

	    
//	    final Composite middleComposite = new Composite(fieldEditShell, SWT.NONE);
//	    ui.addDebug(middleComposite);
//	    middleComposite.setLayoutData(ui.createFillBothGridData());
//	    middleComposite.setLayout(ui.createGridLayout());

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

		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {

				staticCatalogField.setLabel(labelText.getText());

				staticCatalogField.setType(StaticCatalogGeneratorMainWindow.nameTypes.get(typeCombo.getText()));

				staticCatalogField.setIsFilter(useAsFilterCheckbox.getSelection());
				
				staticCatalogField.setDisplayType(StaticCatalogGeneratorMainWindow.displayNameTypes.get(displayTypeCombo.getText()));

				String maxDisplay = maxDisplayValuesText.getText();
				staticCatalogField.setMaxDisplayValues(maxDisplay.trim().equals("") ? null : Integer.parseInt(maxDisplay));

				String minDisplay = minDisplayValuesText.getText();
				staticCatalogField.setMinDisplayValues(minDisplay.trim().equals("") ? null : Integer.parseInt(minDisplay));

				String transformFormat = formatText.getText();
				staticCatalogField.setTransformFormat(transformFormat.trim().equals("") ? null : transformFormat);
				
				String transformValues = transformValuesText.getText();
				staticCatalogField.setTransformValues(transformValues.trim().equals("") ? null : transformValues);
				
				staticCatalogField.setIsSortAsc(sortAscCheckbox.getSelection());
				staticCatalogField.setIsSortDesc(sortDescCheckbox.getSelection());

				staticCatalogField.setSortAscLabel(sortAscLabelText.getText());
				staticCatalogField.setSortDescLabel(sortDescLabelText.getText());

				fieldEditShell.setVisible(false);
				callback.doCallback();
			}
		});

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
	public void showFieldEditWindow(int index, StaticCatalogConfigurationField staticCatalogField) {

		this.staticCatalogField = staticCatalogField;
		
		indexValueLabel.setText("" + index);
		indexInLineValueLabel.setText("" + staticCatalogField.getCsvIndex());
		nameValueLabel.setText(staticCatalogField.getName());

		labelText.setText(staticCatalogField.getLabel());
		typeCombo.setText(StaticCatalogGeneratorMainWindow.typeNames.get(staticCatalogField.getType()));

		useAsFilterCheckbox.setSelection(staticCatalogField.getIsFilter());
		displayTypeCombo.setText(StaticCatalogGeneratorMainWindow.displayTypeNames.get(staticCatalogField.getDisplayType()));
		
		Integer maxDisplayValues = staticCatalogField.getMaxDisplayValues();
		maxDisplayValuesText.setText(maxDisplayValues == null ? "" : "" + maxDisplayValues);	
		Integer minDisplayValues = staticCatalogField.getMinDisplayValues();
		minDisplayValuesText.setText(minDisplayValues == null ? "" : "" + minDisplayValues);	
		
		String transformFormat = staticCatalogField.getTransformFormat();
		formatText.setText(transformFormat == null ? "" : transformFormat);
		String transformValues = staticCatalogField.getTransformValues();
		transformValuesText.setText(transformValues == null ? "" : transformValues);
		
		sortAscCheckbox.setSelection(staticCatalogField.getIsSortAsc());
		sortDescCheckbox.setSelection(staticCatalogField.getIsSortDesc());

		String sortAscLabel = staticCatalogField.getSortAscLabel();
		sortAscLabelText.setText(sortAscLabel == null ? "" : sortAscLabel);
		String sortDescLabel = staticCatalogField.getSortDescLabel();
		sortDescLabelText.setText(sortDescLabel == null ? "" : sortDescLabel);

		fieldEditShell.setVisible(true);
		fieldEditShell.moveAbove(null);
	}
}
