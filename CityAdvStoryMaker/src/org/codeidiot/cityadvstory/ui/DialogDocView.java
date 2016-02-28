package org.codeidiot.cityadvstory.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class DialogDocView extends TitleAreaDialog {

	private StyledText styledText;
	String[] documents;
	String titleMsgString;
	/**
	 * Create the dialog
	 * @param parentShell
	 */
	public DialogDocView(Shell parentShell, String title, String[] documents) {
		super(parentShell);
		this.documents = documents;
		titleMsgString = title;
	}

	/**
	 * Create contents of the dialog
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new FillLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		styledText = new StyledText(container, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.READ_ONLY | SWT.MIRRORED | SWT.H_SCROLL | SWT.BORDER);
		setTitle("CityAdv Document Viewer");
		setMessage("Please Browse document below");
		for (int a = 0; a < documents.length; ++a){
			styledText.append(documents[a]+ "\n");
		}
		setMessage(titleMsgString);
		//
		return area;
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
		return new Point(500, 375);
	}

}
