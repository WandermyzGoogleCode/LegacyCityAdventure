package org.codeidiot.cityadvstory.ui;

import org.codeidiot.cityadvstory.ui.ClientMain.StoryInfo;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.swtdesigner.SWTResourceManager;

public class DialogLogin extends TitleAreaDialog {

	private Text text_1;
	StoryInfo storyInfo;
	private Text text;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public DialogLogin(Shell parentShell, StoryInfo storyInfo) {
		super(parentShell);
		this.storyInfo = storyInfo;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Label userNameLabel = new Label(container, SWT.NONE);
		userNameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		userNameLabel.setText("Story name");

		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label pinLabel = new Label(container, SWT.NONE);
		pinLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		pinLabel.setText("Description");

		text_1 = new Text(container, SWT.WRAP | SWT.MULTI | SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setTitle("Welcome");
		setMessage("City Adventure Login");

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Send",
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				"Exit", false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(322, 298);
	}
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("City Adventure ");
	}
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			storyInfo.descString = text_1.getText();
			storyInfo.nameString = text.getText();
		}
		super.buttonPressed(buttonId);
	}

}
