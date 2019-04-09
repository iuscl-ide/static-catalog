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

	/** */
	private UI(Display display) {
		
		this.display = display;
	}
	
	/** Application */
	public static UI createApplication(String applicationName) {
		
		UI ui = new UI(new Display());
		Display.setAppName(applicationName);
		
		return ui;
	}

	/** Events */
	public class Event {
		
		public void onClickOrEnterKey(Component senderComponent) {
			/* ILB */
		}; 
	}
	
	/** Base */
	public class Component {
		
		/** */
		private Control swtControl;

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

				case "fillCenter":
					positionInParentGridData.horizontalAlignment = GridData.CENTER;
					positionInParentGridData.grabExcessHorizontalSpace = true;
					break;

				case "fillHorizontal":
					positionInParentGridData.horizontalAlignment = GridData.FILL;
					positionInParentGridData.grabExcessHorizontalSpace = true;
					break;

				case "fillBoth":
					positionInParentGridData.horizontalAlignment = GridData.FILL;
					positionInParentGridData.grabExcessHorizontalSpace = true;

					positionInParentGridData.verticalAlignment = GridData.FILL;
					positionInParentGridData.grabExcessVerticalSpace = true;
					break;
				}
			}
		}

		public Widget getSwtControl() {
			return swtControl;
		}
		protected void setSwtControl(Control swtControl) {
			this.swtControl = swtControl;
			this.swtControl.setData("ui_control", this);
		}

		public GridData getPositionInParentGridData() {
			return positionInParentGridData;
		}
		
		/** Show and take place */
		public void show() {
			
			swtControl.setVisible(true);
			positionInParentGridData.exclude = false;
			swtControl.requestLayout();
		}

		/** Hide and free place */
		public void hide() {
			
			swtControl.setVisible(false);
			positionInParentGridData.exclude = true;
			swtControl.requestLayout();
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
		private Panel(ParentComponent parentComponent, String positionInParent, String positionForChildren) {
			super(positionInParent, positionForChildren);
			
			Composite swtComposite = new Composite((Composite) parentComponent.getSwtControl(), SWT.NONE);
			swtComposite.setLayoutData(getPositionInParentGridData());
			swtComposite.setLayout(getPositionForChildrenGridLayout());
			
			if (isDebug) {
				swtComposite.setBackground(randomColor());
			}
			
			setSwtControl(swtComposite);
		}

		/** */
		public Composite getComposite() {
			return (Composite) getSwtControl();
		}
	}

	/** */
	public Panel createPanel(Form parentForm, String positionInParent, String positionForChildren) {
		
		return new Panel(parentForm, positionInParent, positionForChildren);
	}

	/** */
	public Panel createPanel(Panel parentPanel, String positionInParent, String positionForChildren) {
		
		return new Panel(parentPanel, positionInParent, positionForChildren);
	}
	
	/** */
	public Panel createPanel(Panel parentPanel, String positionInParent) {
		
		return new Panel(parentPanel, positionInParent, "");
	}

	/** */
	public Panel createPanel(Panel parentPanel) {
		
		return new Panel(parentPanel, "", "");
	}

	
	/** Form */
	public class Form extends ParentComponent {

		/** */
		private Form(String caption, String positionForChildren) {
			super("", positionForChildren);
			
			Shell swtShell = new Shell(display);
			swtShell.setText(caption);
			swtShell.setLayout(getPositionForChildrenGridLayout());
			
			setSwtControl(swtShell);
		}

		/** */
		public Shell getShell() {
			return (Shell) getSwtControl();
		}
	}
	
	/** */
	public Form createForm(String caption, String positionForChildren) {
		
		Form form = new Form(caption, positionForChildren);
		
		return form;
	}
	
	
	/** Button component */
	private class ButtonComponent extends Component {

		/** */
		public ButtonComponent(Panel panel, int swtStyle, String caption, String positionInParent) {
			super(positionInParent);
			
			Button swtButton = new Button((Composite) panel.getSwtControl(), swtStyle);
			swtButton.setLayoutData(getPositionInParentGridData());
			swtButton.setText(caption);
			
			setSwtControl(swtButton);
		}

		/** */
		public Button getButton() {
			return (Button) getSwtControl();
		}
		
		/** */
		public void onClickOrEnterKey(Event event) {
			
			((Button) getSwtControl()).addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent selectionEvent) {
					
					event.onClickOrEnterKey((Component) selectionEvent.widget.getData("ui_control"));
				}
			});
		}
	}
	
	/** Push button */
	public class PushButton extends ButtonComponent {

		/** */
		private PushButton(Panel panel, String caption, String positionInParent) {
			super(panel, SWT.PUSH, caption, positionInParent);
		}
	}
	
	/** */
	public PushButton createPushButton(Panel parentPanel, String caption) {
		
		return new PushButton(parentPanel, caption, "");
	}

	/** */
	public PushButton createPushButton(Panel parentPanel, String caption, String positionInParent) {
		
		return new PushButton(parentPanel, caption, positionInParent);
	}

	
	/** Toggle button */
	public class ToggleButton extends ButtonComponent {

		/** */
		private ToggleButton(Panel panel, String caption, String positionInParent) {
			super(panel, SWT.TOGGLE, caption, positionInParent);
		}
		
		/** */
		public boolean isDown() {
			
			return getButton().getSelection(); 
		}
		
		/** */
		public void setDown(boolean down) {
			
			getButton().setSelection(down); 
		}
	}
	
	/** */
	public ToggleButton createToggleButton(Panel parentPanel, String caption) {
		
		return new ToggleButton(parentPanel, caption, "");
	}

	/** */
	public ToggleButton createToggleButton(Panel parentPanel, String caption, String positionInParent) {
		
		return new ToggleButton(parentPanel, caption, positionInParent);
	}

	/** Label component */
	private class LabelComponent extends Component {

		/** */
		public LabelComponent(Panel panel, int swtStyle, String caption, String positionInParent) {
			super(positionInParent);
			
			Label swtLabel = new Label((Composite) panel.getSwtControl(), swtStyle);
			swtLabel.setLayoutData(getPositionInParentGridData());
			swtLabel.setText(caption);
			
			setSwtControl(swtLabel);
		}

		/** */
		public Label getLabel() {
			return (Label) getSwtControl();
		}
	}

	/** Text label */
	public class TextLabel extends LabelComponent {

		/** */
		private TextLabel(Panel panel, String caption, String positionInParent) {
			super(panel, SWT.NONE, caption, positionInParent);
		}
	}

	/** */
	public TextLabel createTextLabel(Panel parentPanel, String caption) {
		
		return new TextLabel(parentPanel, caption, "");
	}

	/** */
	public TextLabel createTextLabel(Panel parentPanel, String caption, String positionInParent) {
		
		return new TextLabel(parentPanel, caption, positionInParent);
	}
	
	/** Horizontal separator */
	public class HorizontalSeparator extends LabelComponent {

		/** */
		private HorizontalSeparator(Panel panel) {
			super(panel, SWT.HORIZONTAL | SWT.SEPARATOR, "", "fillHorizontal");
		}
	}

	/** */
	public HorizontalSeparator createHorizontalSeparator(Panel parentPanel) {
		
		return new HorizontalSeparator(parentPanel);
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