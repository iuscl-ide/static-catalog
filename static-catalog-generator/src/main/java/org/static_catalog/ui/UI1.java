/* 2019 */
package org.static_catalog.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;

/** UI */
public class UI1 {

	public abstract class E {
		
		public void onClick(SelectionEvent selectionEvent) {
			
		}
	}
	
	/** GridLayout values */
	public class L {
		
		public static final String noMargins = "noMargins";
		public static final String margins = "margins";

		public static final String noSpacing = "noSpacing";
		public static final String spacing = "spacing";

	}
	
	/** debug */
	private boolean isDebug = false;

	/** Default margin and spacing */
	private String defaultMarginSpacing = "8";
	
	
	/** GridLayout */
	public GridLayout l(String... properties) {

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

		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;

		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		
		for (String property : properties) {
			
			String[] nameValue = property.split(":");
			String name = nameValue[0].trim();
			String value = defaultMarginSpacing;
			if (nameValue.length > 1) {
				value = nameValue[1].trim();	
			}
			
			switch (name) {
			case "numColumns":
				gridLayout.numColumns = Integer.parseInt(value);
				break;
			case "makeColumnsEqualWidth":
				gridLayout.makeColumnsEqualWidth = Boolean.parseBoolean(value);
				break;
			case "marginWidth":
				gridLayout.marginWidth = Integer.parseInt(value);
				break;
			case "marginHeight":
				gridLayout.marginHeight = Integer.parseInt(value);
				break;
			case "marginLeft":
				gridLayout.marginLeft = Integer.parseInt(value);
				break;
			case "marginTop":
				gridLayout.marginTop = Integer.parseInt(value);
				break;
			case "marginRight":
				gridLayout.marginRight = Integer.parseInt(value);
				break;
			case "marginBottom":
				gridLayout.marginBottom = Integer.parseInt(value);
				break;
			case "horizontalSpacing":
				gridLayout.horizontalSpacing = Integer.parseInt(value);
				break;
			case "verticalSpacing":
				gridLayout.verticalSpacing = Integer.parseInt(value);
				break;

			case "noMargins":
				gridLayout.marginWidth = 0;
				gridLayout.marginHeight = 0;
				
				gridLayout.marginLeft = 0;
				gridLayout.marginTop = 0;
				gridLayout.marginRight = 0;
				gridLayout.marginBottom = 0;
				break;
			case "margins":
				int margin = Integer.parseInt(value);
				
				gridLayout.marginWidth = 0;
				gridLayout.marginHeight = 0;
				
				gridLayout.marginLeft = margin;
				gridLayout.marginTop = margin;
				gridLayout.marginRight = margin;
				gridLayout.marginBottom = margin;
				break;

			case L.noSpacing:
				gridLayout.horizontalSpacing = 0;
				gridLayout.verticalSpacing = 0;
				break;
			case L.spacing:
				int spacing = Integer.parseInt(value);
				
				gridLayout.horizontalSpacing = spacing;
				gridLayout.verticalSpacing = spacing;
				break;
			}
		}
		
		return gridLayout;
	}
	
	/** GridData */
	public GridData d(String... properties) {
		
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
		
		for (String property : properties) {
			
			String[] nameValue = property.split(":");
			String name = nameValue[0].trim();
			String value = defaultMarginSpacing;
			if (nameValue.length > 1) {
				value = nameValue[1].trim();	
			}
			
			switch (name) {
			case "verticalAlignment":
				gridData.verticalAlignment = Integer.parseInt(value);
				break;
			case "horizontalAlignment":
				gridData.horizontalAlignment = Integer.parseInt(value);
				break;
			case "widthHint":
				gridData.widthHint = Integer.parseInt(value);
				break;
			case "heightHint":
				gridData.heightHint = Integer.parseInt(value);
				break;
			case "horizontalIndent":
				gridData.horizontalIndent = Integer.parseInt(value);
				break;
			case "verticalIndent":
				gridData.verticalIndent = Integer.parseInt(value);
				break;
			case "horizontalSpan":
				gridData.horizontalSpan = Integer.parseInt(value);
				break;
			case "verticalSpan":
				gridData.verticalSpan = Integer.parseInt(value);
				break;
			case "grabExcessHorizontalSpace":
				gridData.grabExcessHorizontalSpace = Boolean.parseBoolean(value);
				break;
			case "grabExcessVerticalSpace":
				gridData.grabExcessVerticalSpace = Boolean.parseBoolean(value);
				break;
			case "minimumWidth":
				gridData.minimumWidth = Integer.parseInt(value);
				break;
			case "minimumHeight":
				gridData.minimumHeight = Integer.parseInt(value);
				break;
			case "exclude":
				gridData.exclude = Boolean.parseBoolean(value);
				break;

			case "horizontalFill":
				gridData.horizontalAlignment = GridData.FILL;
				gridData.grabExcessHorizontalSpace = true;
				break;

			case "bothFill":
				gridData.horizontalAlignment = GridData.FILL;
				gridData.grabExcessHorizontalSpace = true;

				gridData.verticalAlignment = GridData.FILL;
				gridData.grabExcessVerticalSpace = true;
				break;

			}
		}
		
		return gridData;
	}
	
	/** Composite */
	public Composite c(Composite parent, GridData hisLayoutInHisParent, GridLayout theLayoutForHisChildren) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(hisLayoutInHisParent);
		composite.setLayout(theLayoutForHisChildren);
		
		if (isDebug) {
			composite.setBackground(randomColor());
		}
		
		return composite;
	}

	/** Create control */
	public <T extends Control> T w(T createNew, GridData hisLayoutInHisParent) {
		
		createNew.setLayoutData(hisLayoutInHisParent);
		return createNew;
	}

	/** Create control with caption */
	public Button button(Composite parent, int style, String text, GridData hisLayoutInHisParent) {

		return button(parent, style, text, hisLayoutInHisParent, null);
	}
	
	/** Create control with caption */
	public Button button(Composite parent, int style, String text, GridData hisLayoutInHisParent, E e) {
		
		Button button = new Button(parent, style);
		button.setText(text);
		button.setLayoutData(hisLayoutInHisParent);
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				e.onClick(selectionEvent);
			}
		});
		
		return button;
	}

	/** Label */
	public Label label(Composite parent, int style, String text, GridData hisLayoutInHisParent) {
		
		Label label = new Label(parent, style);
		label.setText(text);
		label.setLayoutData(hisLayoutInHisParent);
		
		if (isDebug) {
			label.setBackground(randomColor());
		}

		return label;
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

	
	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public String getDefaultMarginSpacing() {
		return defaultMarginSpacing;
	}

	public void setDefaultMarginSpacing(String defaultMarginSpacing) {
		this.defaultMarginSpacing = defaultMarginSpacing;
	}
	
	
 }
