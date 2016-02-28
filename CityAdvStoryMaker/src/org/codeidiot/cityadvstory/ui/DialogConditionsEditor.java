package org.codeidiot.cityadvstory.ui;

import java.util.ArrayList;

import org.codeidiot.cityadvstory.Conditions;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.swtdesigner.SWTResourceManager;

public class DialogConditionsEditor extends TitleAreaDialog {

	private Label label;
	private Text text;
	Conditions conditions;
	/**
	 * Create the dialog
	 * @param parentShell
	 */
	public DialogConditionsEditor(Shell parentShell, Conditions conditions) {
		super(parentShell);
		this.conditions = conditions;
		
	}

	/**
	 * Create contents of the dialog
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Composite composite = new Composite(container, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);

		final Label pleaseEnterConditionLabel = new Label(composite, SWT.NONE);
		pleaseEnterConditionLabel.setText("Please enter condition code here ");

		final Button viewExampleButton = new Button(composite, SWT.NONE);
		viewExampleButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				text.setText("# enter comments here\n" +
						"# [EventPoint]:[Name of TriggerPoint]\n"+
						"EventPoint: trigger point name\n" +
						"# [TaskCondition]:[TaskID]:[Type{non,start,end}]\n" +
						"TaskCondition: taskID: 0\n");
			}
		});
		viewExampleButton.setText("View Example");

		text = new Text(container, SWT.MULTI | SWT.BORDER);
		text.setForeground(SWTResourceManager.getColor(0, 128, 0));
		text.setFont(SWTResourceManager.getFont("Lucida Console", 10, SWT.NONE));
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		label = new Label(container, SWT.SHADOW_IN | SWT.BORDER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		label.setText("Label");
		setTitle("Conditions Editor");
		setMessage("Please Edit Conditions for current quest or NPCs");
		if (conditions != null)
			text.setText(CityAdvManager.getConditionArgument(conditions, 0));
		//
		return area;
	}

	/**
	 * Create contents of the button bar
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
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
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("City Advanture");
	}
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			ArrayList<String> errorList = new ArrayList<String>();
			conditions = CityAdvManager.compileCondition(text.getText(), errorList,conditions);
			if (errorList.size() > 0){
				label.setText(errorList.get(0));
				return;
			}
		}
		super.buttonPressed(buttonId);
	}

}
