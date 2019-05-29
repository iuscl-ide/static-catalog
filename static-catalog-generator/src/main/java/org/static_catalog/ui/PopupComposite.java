/** http://palea.cgrb.oregonstate.edu/svn/jaiswallab/Annotation/src/ie/dcu/swt/PopupComposite.java */
package org.static_catalog.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/** Pop-up composite in a shell */
public class PopupComposite extends Composite {

	/** Style of the shell that will house the composite */
	private static final int SHELL_STYLE = SWT.MODELESS | SWT.NO_TRIM | SWT.ON_TOP;

	/** Shell that will house the composite */
	private final Shell shell;

	/** Create a Pop-up composite */
	public PopupComposite(Shell parent, int style, UI ui) {
		super(new Shell(parent, SHELL_STYLE), style);
		shell = getShell();
		//shell.setLayout(new FillLayout());
		shell.setLayout(ui.createMarginsGridLayout(1));
		//shell.setLayout(ui.createGridLayout());
		
		//setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		shell.addShellListener(new ActivationListener());
		//setLayout(createLayout());
		setLayoutData(ui.createFillBothGridData());
		setLayout(ui.createGridLayout());
	}

	/** Display the composite in its own shell at the given point */
	public void show(Point pt) {
		// Match shell and component sizes
		shell.setSize(getSize());

		if (pt != null) {
			shell.setLocation(pt);
		}

		shell.open();
	}

	/** Display the pop-up where it was last displayed */
	public void show() {
		show(null);
	}

	/** Hide the pop-up */
	public void hide() {
		shell.setVisible(false);
	}

	/** Returns <code>true</code> if the shell is currently activated */
	public boolean isDisplayed() {
		return shell.isVisible();
	}

	/**
	 * Class that handles shell appearance and disappearance appropriately.
	 * Specifically, it hides the shell when it becomes de-activated (for example,
	 * when the user clicks on the parent shell). Also, there is a minimum delay
	 * which is enforced between showing and hiding the pop-up, to prevent
	 * undesirable behavior such as hiding and immediately re-displaying the pop-up
	 * when the user selects a button responsible for showing the tool item.
	 */
	private final class ActivationListener extends ShellAdapter {
		private static final int TIMEOUT = 500;
		private long time = -1;

		@Override
		public void shellDeactivated(ShellEvent e) {
			// Record time of event
			time = (e.time & 0xFFFFFFFFL);

			// Hide
			hide();
		}

		@Override
		public void shellActivated(ShellEvent e) {
			if (time > 0) {
				// Find elapsed time
				long elapsed = ((e.time & 0xFFFFFFFFL) - time);

				// If less than a timeout, don't activate
				if (elapsed < TIMEOUT) {
					hide();

					// Next activation event is fine
					time = -1;
				}
			}
		}
	};

}