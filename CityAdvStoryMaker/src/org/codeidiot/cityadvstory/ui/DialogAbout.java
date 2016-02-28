package org.codeidiot.cityadvstory.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.swtdesigner.SWTResourceManager;

public class DialogAbout extends Dialog {

	private Text wanderWanderwanderwanderText;
	/**
	 * Create the dialog
	 * @param parentShell
	 */
	public DialogAbout(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		final Label codeidiotLabel = new Label(container, SWT.NONE);
		codeidiotLabel.setFont(SWTResourceManager.getFont("Comic Sans MS", 20, SWT.NONE));
		codeidiotLabel.setText("CodeIdiot");

		final Link eclipseorgLink = new Link(container, SWT.NONE);
		eclipseorgLink.setText("<a href=\"http://code.google.com/p/cityadventure/\">code.google.com/p/cityadventure</a>");

		final Label weAreLabel = new Label(container, SWT.WRAP);
		weAreLabel.setText("We Are:");

		wanderWanderwanderwanderText = new Text(container, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER);
		wanderWanderwanderwanderText.setText("wander\nwander\nwander\nwander");
		final GridData gd_wanderWanderwanderwanderText = new GridData(SWT.FILL, SWT.CENTER, true, false);
		wanderWanderwanderwanderText.setLayoutData(gd_wanderWanderwanderwanderText);
		//
		return container;
	}

	/**
	 * Create contents of the button bar
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(304, 297);
	}

}
