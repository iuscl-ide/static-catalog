package org.static_catalog.ui;

import java.awt.LayoutManager;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.static_catalog.main.L;
import org.static_catalog.ui.UI.Component;
import org.static_catalog.ui.UI.Event;
import org.static_catalog.ui.UI.Form;
import org.static_catalog.ui.UI.HorizontalSeparator;
import org.static_catalog.ui.UI.Panel;
import org.static_catalog.ui.UI.TextLabel;
import org.static_catalog.ui.UI.ToggleButton;



public class StaticCatalogGeneratorMainWindow4 {

	public void runMainWindow() {
		
		UI ui = UI.createApplication("static-catalog");
		Display mainDisplay = ui.getDisplay(); 
		
		//ui.setDebug(true);
		
		Form mainForm = ui.createForm("static-catalog Generator", "margins, spacing");
		
		/* Location */
		Shell mainShell = mainForm.getShell();
		mainShell.setLocation(250, 0);
		mainShell.setSize(mainDisplay.getClientArea().width - 600, mainDisplay.getClientArea().height);

		/* Top panel */
		Panel topPanel = ui.createPanel(mainForm, "fillHorizontal", "spacing");
		
		Panel topButtonsPanel = ui.createPanel(topPanel, "fillCenter", "numColumns: 3");

		boolean[] downButtons = { true, false, false };
		
		ToggleButton viewCsvToggleButton = ui.createToggleButton(topButtonsPanel, "View CSV", "widthHint:120");
		ToggleButton analyseCsvToggleButton = ui.createToggleButton(topButtonsPanel, "Analyse CSV", "widthHint:120");
		ToggleButton generateToggleButton = ui.createToggleButton(topButtonsPanel, "Generate", "widthHint:120");

		HorizontalSeparator topPanelHorizontalSeparator = ui.createHorizontalSeparator(topPanel);
		
		
		/* Panels */
		Panel viewCsvPanel = ui.createPanel(mainForm, "fillBoth", ""); 
		Panel analyseCsvPanel = ui.createPanel(mainForm, "fillBoth", "");
		Panel generatePanel = ui.createPanel(mainForm, "fillBoth", "");
		
		Event topButtons_onClickOrEnterKey = ui.new Event() {
			@Override
			public void onClickOrEnterKey(Component senderComponent) {
				
				ToggleButton toggleButton = (ToggleButton) senderComponent;
				
				if (toggleButton.equals(viewCsvToggleButton)) {
					viewCsvToggleButton.setDown(true);
					if (downButtons[0]) {
						return;
					}
					downButtons[0] = true;
					downButtons[1] = false;
					downButtons[2] = false;
					analyseCsvToggleButton.setDown(false);
					generateToggleButton.setDown(false);
					
					viewCsvPanel.show();
					analyseCsvPanel.hide();
					generatePanel.hide();
				}
				else if (toggleButton.equals(analyseCsvToggleButton)) {
					analyseCsvToggleButton.setDown(true);
					if (downButtons[1]) {
						return;
					}
					downButtons[0] = false;
					downButtons[1] = true;
					downButtons[2] = false;
					viewCsvToggleButton.setDown(false);
					generateToggleButton.setDown(false);
					
					analyseCsvPanel.show();
					viewCsvPanel.hide();
					generatePanel.hide();
				}
				else if (toggleButton.equals(generateToggleButton)) {
					generateToggleButton.setDown(true);
					if (downButtons[2]) {
						return;
					}
					
					downButtons[0] = false;
					downButtons[1] = false;
					downButtons[2] = true;
					viewCsvToggleButton.setDown(false);
					analyseCsvToggleButton.setDown(false);
					
					generatePanel.show();
					viewCsvPanel.hide();
					analyseCsvPanel.hide();
				}
			}
		};
		
		viewCsvToggleButton.onClickOrEnterKey(topButtons_onClickOrEnterKey);
		analyseCsvToggleButton.onClickOrEnterKey(topButtons_onClickOrEnterKey);
		generateToggleButton.onClickOrEnterKey(topButtons_onClickOrEnterKey);

		viewCsvToggleButton.setDown(true);
		analyseCsvPanel.hide();
		generatePanel.hide();

		/* viewCsvPanel */
		
		TextLabel viewCsvTitleTextLabel = ui.createTextLabel(viewCsvPanel, "View CSV");

		/* analyseCsvPanel */
		
		TextLabel analyseCsvTitleTextLabel = ui.createTextLabel(analyseCsvPanel, "Analyse CSV");

		/* generatePanel */
		
		TextLabel generateTitleTextLabel = ui.createTextLabel(generatePanel, "Generate");
		
		
		/* Run */
		mainShell.open();
		
		/* Application loop */
		while (!mainShell.isDisposed()) {
			if (!mainDisplay.readAndDispatch()) {
				mainDisplay.sleep();
			}
		}
		
		/* Finish */
		mainDisplay.dispose();
	}
}
