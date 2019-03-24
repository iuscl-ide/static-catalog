/* 2019 */ 
package org.static_catalog.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.static_catalog.ui.UI1.L;

/** UI over SWT */
public class UI {

	/** debug */
	private boolean isDebug = false;

	/** Default margin and spacing */
	private String defaultMarginSpacing = "8";

	/** Own display */
	private final Display display;
	
	/** Application */
	public UI(String applicationName) {
		super();
		
		Display.setAppName(applicationName);
		display = new Display();
	}

	/** Base */
	public class Component {
		
		/** */
		private Widget swtWidget;

		/** */
		private final GridData positionInParentGridData;
		
		/** */
		public Component(String positionInParent) {
			super();
			
			positionInParentGridData = new GridData();

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

			String[] propertiesValues = positionInParent.split(","); 
			for (String propertyValue : propertiesValues) {
				
				String[] nameValue = propertyValue.split(":");
				String name = nameValue[0].trim();
				String value = defaultMarginSpacing;
				if (nameValue.length > 1) {
					value = nameValue[1].trim();	
				}
				
				switch (name) {

				case "verticalAlignment":
					positionInParentGridData.verticalAlignment = Integer.parseInt(value);
					break;
				case "horizontalAlignment":
					positionInParentGridData.horizontalAlignment = Integer.parseInt(value);
					break;
				case "widthHint":
					positionInParentGridData.widthHint = Integer.parseInt(value);
					break;
				case "heightHint":
					positionInParentGridData.heightHint = Integer.parseInt(value);
					break;
				case "horizontalIndent":
					positionInParentGridData.horizontalIndent = Integer.parseInt(value);
					break;
				case "verticalIndent":
					positionInParentGridData.verticalIndent = Integer.parseInt(value);
					break;
				case "horizontalSpan":
					positionInParentGridData.horizontalSpan = Integer.parseInt(value);
					break;
				case "verticalSpan":
					positionInParentGridData.verticalSpan = Integer.parseInt(value);
					break;
				case "grabExcessHorizontalSpace":
					positionInParentGridData.grabExcessHorizontalSpace = Boolean.parseBoolean(value);
					break;
				case "grabExcessVerticalSpace":
					positionInParentGridData.grabExcessVerticalSpace = Boolean.parseBoolean(value);
					break;
				case "minimumWidth":
					positionInParentGridData.minimumWidth = Integer.parseInt(value);
					break;
				case "minimumHeight":
					positionInParentGridData.minimumHeight = Integer.parseInt(value);
					break;
				case "exclude":
					positionInParentGridData.exclude = Boolean.parseBoolean(value);
					break;
				
				case "horizontalFill":
					positionInParentGridData.horizontalAlignment = GridData.FILL;
					positionInParentGridData.grabExcessHorizontalSpace = true;
					break;

				case "bothFill":
					positionInParentGridData.horizontalAlignment = GridData.FILL;
					positionInParentGridData.grabExcessHorizontalSpace = true;

					positionInParentGridData.verticalAlignment = GridData.FILL;
					positionInParentGridData.grabExcessVerticalSpace = true;
					break;
				}
			}
		}

		public Widget getSwtWidget() {
			return swtWidget;
		}
		protected void setSwtWidget(Widget swtWidget) {
			this.swtWidget = swtWidget;
		}

		public GridData getPositionInParentGridData() {
			return positionInParentGridData;
		}
	}
	
	/** Panel */
	public class ParentComponent extends Component {
		
		private final GridLayout positionForChildrenGridLayout;

		/** */
		public ParentComponent(String positionInParent) {
			
			this(positionInParent, "");
		}

		/** */
		public ParentComponent(String positionInParent, String positionForChildren) {
			super(positionInParent);
			
			positionForChildrenGridLayout = new GridLayout();
			
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
			
			positionForChildrenGridLayout.marginWidth = 0;
			positionForChildrenGridLayout.marginHeight = 0;

			positionForChildrenGridLayout.horizontalSpacing = 0;
			positionForChildrenGridLayout.verticalSpacing = 0;

			String[] propertiesValues = positionForChildren.split(","); 
			for (String property : propertiesValues) {
				
				String[] nameValue = property.split(":");
				String name = nameValue[0].trim();
				String value = defaultMarginSpacing;
				if (nameValue.length > 1) {
					value = nameValue[1].trim();	
				}
				
				switch (name) {
				case "numColumns":
					positionForChildrenGridLayout.numColumns = Integer.parseInt(value);
					break;
				case "makeColumnsEqualWidth":
					positionForChildrenGridLayout.makeColumnsEqualWidth = Boolean.parseBoolean(value);
					break;
				case "marginWidth":
					positionForChildrenGridLayout.marginWidth = Integer.parseInt(value);
					break;
				case "marginHeight":
					positionForChildrenGridLayout.marginHeight = Integer.parseInt(value);
					break;
				case "marginLeft":
					positionForChildrenGridLayout.marginLeft = Integer.parseInt(value);
					break;
				case "marginTop":
					positionForChildrenGridLayout.marginTop = Integer.parseInt(value);
					break;
				case "marginRight":
					positionForChildrenGridLayout.marginRight = Integer.parseInt(value);
					break;
				case "marginBottom":
					positionForChildrenGridLayout.marginBottom = Integer.parseInt(value);
					break;
				case "horizontalSpacing":
					positionForChildrenGridLayout.horizontalSpacing = Integer.parseInt(value);
					break;
				case "verticalSpacing":
					positionForChildrenGridLayout.verticalSpacing = Integer.parseInt(value);
					break;

				case "noMargins":
					positionForChildrenGridLayout.marginWidth = 0;
					positionForChildrenGridLayout.marginHeight = 0;
					
					positionForChildrenGridLayout.marginLeft = 0;
					positionForChildrenGridLayout.marginTop = 0;
					positionForChildrenGridLayout.marginRight = 0;
					positionForChildrenGridLayout.marginBottom = 0;
					break;
				case "margins":
					int margin = Integer.parseInt(value);
					
					positionForChildrenGridLayout.marginWidth = 0;
					positionForChildrenGridLayout.marginHeight = 0;
					
					positionForChildrenGridLayout.marginLeft = margin;
					positionForChildrenGridLayout.marginTop = margin;
					positionForChildrenGridLayout.marginRight = margin;
					positionForChildrenGridLayout.marginBottom = margin;
					break;

				case L.noSpacing:
					positionForChildrenGridLayout.horizontalSpacing = 0;
					positionForChildrenGridLayout.verticalSpacing = 0;
					break;
				case L.spacing:
					int spacing = Integer.parseInt(value);
					
					positionForChildrenGridLayout.horizontalSpacing = spacing;
					positionForChildrenGridLayout.verticalSpacing = spacing;
					break;
				}
			}
		}

		public GridLayout getPositionForChildrenGridLayout() {
			return positionForChildrenGridLayout;
		}
	}

	/** Panel */
	public class Panel extends ParentComponent {

		/** */
		public Panel(Form parentForm, String positionInParent, String positionForChildren) {
			this((ParentComponent) parentForm, positionInParent, positionForChildren);
		}

		/** */
		public Panel(Panel parentPanel) {
			this((ParentComponent) parentPanel, "", "");
		}
		
		/** */
		public Panel(Panel parentPanel, String positionInParent) {
			this((ParentComponent) parentPanel, positionInParent, "");
		}
		
		/** */
		public Panel(Panel parentPanel, String positionInParent, String positionForChildren) {
			this((ParentComponent) parentPanel, positionInParent, positionForChildren);
		}
		
		/** */
		private Panel(ParentComponent parentComponent, String positionInParent, String positionForChildren) {
			super(positionInParent, positionForChildren);
			
			Composite swtComposite = new Composite((Composite) parentComponent.getSwtWidget(), SWT.NONE);
			swtComposite.setLayoutData(getPositionInParentGridData());
			swtComposite.setLayout(getPositionForChildrenGridLayout());
			
			if (isDebug) {
				swtComposite.setBackground(randomColor());
			}
			
			setSwtWidget(swtComposite);
		}

		/** */
		public Composite getComposite() {
			return (Composite) getSwtWidget();
		}
	}

	/** Form */
	public class Form extends ParentComponent {

		/** */
		public Form(String caption, String positionForChildren) {
			super("", positionForChildren);
			
			Shell swtShell = new Shell(display);
			swtShell.setText(caption);
			swtShell.setLayout(getPositionForChildrenGridLayout());
			
			setSwtWidget(swtShell);
		}

		/** */
		public Shell getShell() {
			return (Shell) getSwtWidget();
		}
	}
	
	/** Button component */
	public class ButtonComponent extends Component {

		/** */
		public ButtonComponent(Panel panel, int swtStyle, String positionInParent) {
			super(positionInParent);
			
			Button swtButton = new Button((Composite) panel.getSwtWidget(), swtStyle);
			swtButton.setLayoutData(getPositionInParentGridData());
			
			setSwtWidget(swtButton);
		}

		/** */
		public Button getButton() {
			return (Button) getSwtWidget();
		}
	}
	
	/** Push button */
	public class PushButton extends ButtonComponent {

		/** */
		public PushButton(Panel panel) {
			super(panel, SWT.PUSH, "");
		}
		
		/** */
		public PushButton(Panel panel, String positionInParent) {
			super(panel, SWT.PUSH, positionInParent);
		}
	}
	
	/** Toggle button */
	public class ToggleButton extends ButtonComponent {

		/** */
		public ToggleButton(Panel panel) {
			super(panel, SWT.TOGGLE, "");
		}

		/** */
		public ToggleButton(Panel panel, String positionInParent) {
			super(panel, SWT.TOGGLE, positionInParent);
		}
	}
	

//	/** Separator */
//	public class Separator extends Component {
//		
//	}
	

	
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

	public Display getDisplay() {
		return display;
	}
}
