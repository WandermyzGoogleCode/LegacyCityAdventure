package org.codeidiot.cityadvstory.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import com.swtdesigner.SWTResourceManager;

public class DialogDone extends Dialog {

	/**
	 * Create the dialog
	 * @param parentShell
	 */
	String messageString;
	public DialogDone(Shell parentShell, String message) {
		super(parentShell);
		messageString = message;
	}

	/**
	 * Create contents of the dialog
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		final Label label = new Label(container, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Constantia", 12, SWT.BOLD));
		label.setText("Congradulations:");

		final Label yourTaskIsLabel = new Label(container, SWT.NONE);
		yourTaskIsLabel.setText("Your Task is forwarded to Server");

		final Label labelText = new Label(container, SWT.NONE);
		labelText.setText(messageString);
		//
		return container;
	}

	/**
	 * Create contents of the button bar
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	/**
	 * Return the initial size of the dialog
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(404, 277);
	}

}
