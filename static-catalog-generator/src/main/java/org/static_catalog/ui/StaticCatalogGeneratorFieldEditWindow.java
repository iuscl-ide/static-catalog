/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.static_catalog.engine.StaticCatalogEngine;
import org.static_catalog.main.L;
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
	private Combo filterTypeCombo;
	private Text intervalValueText;	
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
		fieldEditShell.setLayout(ui.createMarginsVerticalSpacingGridLayout(UI.sep, UI.sep));

		/* Icon */
		//String[] rez = { "16", "24", "32", "48", "64", "96", "128", "256", "512" };
		String[] rez = { "16", "24", "32", "48" };
		Image[] iconImages = new Image[rez.length];
		for (int index = 0; index < rez.length; index++) {
			String rezimg = rez[index];
			iconImages[index] = ui.getResourceAsImage("org/static_catalog/res/icon/neutral/" + rezimg + "x" + rezimg + ".png");
		}
		fieldEditShell.setImages(iconImages);

		/* Location */
		int width = 640;
		int height = 444;

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

		final Group topGroup = new Group(fieldEditShell, SWT.NONE);
		topGroup.setText("Edit Field");
		topGroup.setLayoutData(ui.createFillBothGridData());
		topGroup.setLayout(ui.createColumnsGridLayout(1));
	    
	    final ScrolledComposite scrolledComposite = new ScrolledComposite(topGroup, SWT.V_SCROLL);
	    scrolledComposite.setLayoutData(ui.createFillBothGridData());
	    scrolledComposite.setLayout(ui.createColumnsGridLayout(1));
	    
	    final Composite contentComposite = new Composite(scrolledComposite, SWT.NONE);
	    contentComposite.setLayoutData(ui.createFillBothGridData());
		GridLayout contentCompositeGridLayout = ui.createColumnsSpacingGridLayout(2, UI.sep);
		contentCompositeGridLayout.marginWidth = UI.sep;
		contentCompositeGridLayout.marginHeight = UI.sep;
		contentCompositeGridLayout.verticalSpacing = UI.sep;
		contentComposite.setLayout(contentCompositeGridLayout);
	    
	    final Label indexLabel = new Label(contentComposite, SWT.NONE);
	    indexLabel.setText("Index");
	    indexValueLabel = new Label(contentComposite, SWT.NONE);
	    indexValueLabel.setLayoutData(ui.createFillHorizontalGridData());
	    indexValueLabel.setFont(StaticCatalogGeneratorMainWindow.fontBold);
	    
	    final Label indexInLineLabel = new Label(contentComposite, SWT.NONE);
	    indexInLineLabel.setText("Index in Line");
	    indexInLineValueLabel = new Label(contentComposite, SWT.NONE);
	    indexInLineValueLabel.setLayoutData(ui.createFillHorizontalGridData());
	    indexInLineValueLabel.setFont(StaticCatalogGeneratorMainWindow.fontBold);
	    
	    final Label nameLabel = new Label(contentComposite, SWT.NONE);
	    nameLabel.setText("Field Name");
	    nameValueLabel = new Label(contentComposite, SWT.NONE);
	    nameValueLabel.setLayoutData(ui.createFillHorizontalGridData());
	    nameValueLabel.setFont(StaticCatalogGeneratorMainWindow.fontBold);

	    final Label labelLabel = new Label(contentComposite, SWT.NONE);
	    labelLabel.setText("Label");
	    labelText = new Text(contentComposite, SWT.SINGLE | SWT.BORDER);
	    labelText.setLayoutData(ui.createFillHorizontalGridData());
	    
	    final Label typeLabel = new Label(contentComposite, SWT.NONE);
	    typeLabel.setText("Type");
		typeCombo = new Combo(contentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeCombo.setLayoutData(ui.createWidthButtonGridData());
		typeCombo.setItems(StaticCatalogGeneratorMainWindow.typeNameValues);

	    final Label useAsFilterLabel = new Label(contentComposite, SWT.NONE);
	    useAsFilterLabel.setText("Use as Filter");
		useAsFilterCheckbox = new Button(contentComposite, SWT.CHECK);

	    final Label filterTypeLabel = new Label(contentComposite, SWT.NONE);
	    filterTypeLabel.setText("Filter Type");
	    filterTypeCombo = new Combo(contentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
	    filterTypeCombo.setLayoutData(ui.createWidthButtonGridData());
	    filterTypeCombo.setItems(StaticCatalogGeneratorMainWindow.filterTypeNameValues);

	    final Label intervalValueLabel = new Label(contentComposite, SWT.NONE);
	    intervalValueLabel.setText("Interval Value");
	    intervalValueText = new Text(contentComposite, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
	    intervalValueText.setLayoutData(ui.createWidthButtonGridData());
	    
	    final Label displayTypeLabel = new Label(contentComposite, SWT.NONE);
	    displayTypeLabel.setText("Filter Display Type");
	    displayTypeCombo = new Combo(contentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
	    displayTypeCombo.setLayoutData(ui.createWidthButtonGridData());
	    displayTypeCombo.setItems(StaticCatalogGeneratorMainWindow.displayTypeNameValues);
		
	    final Label maxDisplayValuesLabel = new Label(contentComposite, SWT.NONE);
	    maxDisplayValuesLabel.setText("Filter Max. Display Values");
	    maxDisplayValuesText = new Text(contentComposite, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
	    maxDisplayValuesText.setLayoutData(ui.createWidthGridData(50));

	    final Label minDisplayValuesLabel = new Label(contentComposite, SWT.NONE);
	    minDisplayValuesLabel.setText("Filter Min. Display Values");
	    minDisplayValuesText = new Text(contentComposite, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
	    minDisplayValuesText.setLayoutData(ui.createWidthGridData(50));

	    final Label formatLabel = new Label(contentComposite, SWT.NONE);
	    formatLabel.setText("Filter Format Values");
	    formatText = new Text(contentComposite, SWT.SINGLE | SWT.BORDER);
	    formatText.setLayoutData(ui.createFillHorizontalGridData());
	    
	    final Label transformValuesLabel = new Label(contentComposite, SWT.NONE);
	    transformValuesLabel.setText("Filter Transform Values");
	    transformValuesText = new Text(contentComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
	    GridData transformValuesTextGridData = ui.createFillHorizontalGridData();
	    transformValuesTextGridData.heightHint = 50;
	    transformValuesTextGridData.minimumHeight = 50;
	    //transformValuesText.sets
	    transformValuesText.setLayoutData(transformValuesTextGridData);

	    final Label sortAscLabel = new Label(contentComposite, SWT.NONE);
	    sortAscLabel.setText("Sort Asc.");
	    sortAscCheckbox = new Button(contentComposite, SWT.CHECK);

	    final Label sortAscLabelLabel = new Label(contentComposite, SWT.NONE);
	    sortAscLabelLabel.setText("Sort Asc. Label");
	    sortAscLabelText = new Text(contentComposite, SWT.SINGLE | SWT.BORDER);
	    sortAscLabelText.setLayoutData(ui.createFillHorizontalGridData());
	    
	    final Label sortDescLabel = new Label(contentComposite, SWT.NONE);
	    sortDescLabel.setText("Sort Desc.");
	    sortDescCheckbox = new Button(contentComposite, SWT.CHECK);

	    final Label sortDescLabelLabel = new Label(contentComposite, SWT.NONE);
	    sortDescLabelLabel.setText("Sort Desc. Label");
	    sortDescLabelText = new Text(contentComposite, SWT.SINGLE | SWT.BORDER);
	    sortDescLabelText.setLayoutData(ui.createFillHorizontalGridData());

	    
	    scrolledComposite.setContent(contentComposite);
	    scrolledComposite.setExpandHorizontal(true);
	    scrolledComposite.setExpandVertical(true);
	    scrolledComposite.setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    
//	    final Composite middleComposite = new Composite(fieldEditShell, SWT.NONE);
//	    ui.addDebug(middleComposite);
//	    middleComposite.setLayoutData(ui.createFillBothGridData());
//	    middleComposite.setLayout(ui.createGridLayout());

	    final Label separator = new Label(fieldEditShell, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(ui.createFillHorizontalGridData());

	    final Composite bottomComposite = new Composite(fieldEditShell, SWT.NONE);
	    ui.addDebug(bottomComposite);
	    bottomComposite.setLayoutData(ui.createFillHorizontalGridData());
	    bottomComposite.setLayout(ui.createColumnsSpacingGridLayout(3, UI.sep));

	    final Composite fillLeftBottomComposite = new Composite(bottomComposite, SWT.NONE);
	    ui.addDebug(fillLeftBottomComposite);
	    fillLeftBottomComposite.setLayoutData(ui.createFillHorizontalGridData());
	    fillLeftBottomComposite.setLayout(ui.createColumnsGridLayout(1));
	    
		final Button okButton = new Button(bottomComposite, SWT.NONE);
		okButton.setText("OK");
		okButton.setLayoutData(ui.createWidthButtonGridData());

		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {

				staticCatalogField.setLabel(labelText.getText());

				staticCatalogField.setType(StaticCatalogGeneratorMainWindow.nameTypes.get(typeCombo.getText()));

				staticCatalogField.setIsFilter(useAsFilterCheckbox.getSelection());
				
				String filterTypeComboText = filterTypeCombo.getText();
				String filterType = filterTypeComboText.equals("") ? null : StaticCatalogGeneratorMainWindow.filterNameTypes.get(filterTypeComboText);
				staticCatalogField.setFilterType(filterType);

				String intervalValue = intervalValueText.getText();
				staticCatalogField.setIntervalValue(intervalValue.trim().equals("") ? null : intervalValue);
				
				String displayTypeComboText = displayTypeCombo.getText();
				String displayType = displayTypeComboText.equals("") ? null : StaticCatalogGeneratorMainWindow.displayNameTypes.get(displayTypeComboText);
				if ((filterType != null) && (filterType == StaticCatalogEngine.FILTER_TYPE_KEYWORDS)) {
					if ((displayType == null) || (displayType != StaticCatalogEngine.DISPLAY_TYPE_SEARCHBOX)) {
						L.w("For filter type \"Keywords\" the filter diplay type must be \"Search Box\"");
						return;
					}
				}
				staticCatalogField.setDisplayType(displayType);

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

				String sortAscLabel = sortAscLabelText.getText();
				staticCatalogField.setSortAscLabel(sortAscLabel.trim().equals("") ? null : sortAscLabel);
				String sortDescLabel = sortDescLabelText.getText();
				staticCatalogField.setSortDescLabel(sortDescLabel.trim().equals("") ? null : sortDescLabel);

				fieldEditShell.setVisible(false);
				callback.doCallback();
			}
		});

		final Button cancelButton = new Button(bottomComposite, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(ui.createWidthButtonGridData());
		
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
		String filterType = staticCatalogField.getFilterType();
		filterTypeCombo.setText(filterType == null ? "" : StaticCatalogGeneratorMainWindow.filterTypeNames.get(filterType));
		String intervalValue = staticCatalogField.getIntervalValue();
		intervalValueText.setText(intervalValue == null ? "" : intervalValue);	
		String displayType = staticCatalogField.getDisplayType();
		displayTypeCombo.setText(displayType == null ? "" : StaticCatalogGeneratorMainWindow.displayTypeNames.get(displayType));
		
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
