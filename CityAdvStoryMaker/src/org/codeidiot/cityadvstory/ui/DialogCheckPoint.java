package org.codeidiot.cityadvstory.ui;

import org.codeidiot.cityadvstory.ConditionParser;
import org.codeidiot.cityadvstory.Conditions;
import org.codeidiot.cityadvstory.TaskTriggerPoint;
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

public class DialogCheckPoint extends TitleAreaDialog {
	

	private Text textConditions;
	private Text textNegative;
	private Text textPositive;
	private Text textEventPoint;
	
	private TaskTriggerPoint triggerPoint;
	Shell parentShell;
	/**
	 * Create the dialog
	 * @param parentShell
	 */
	public DialogCheckPoint(Shell parentShell, TaskTriggerPoint triggerPoint) {
		super(parentShell);
		this.parentShell = parentShell;
		this.triggerPoint = triggerPoint;
		
	}

	/**
	 * Create contents of the dialog
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

		final Label eventPointLabel = new Label(container, SWT.NONE);
		eventPointLabel.setText("Event Point");

		textEventPoint = new Text(container, SWT.BORDER);
		final GridData gd_textEventPoint = new GridData(SWT.FILL, SWT.CENTER, true, false);
		textEventPoint.setLayoutData(gd_textEventPoint);

		final Label possitiveMessageLabel = new Label(container, SWT.NONE);
		possitiveMessageLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		possitiveMessageLabel.setText("Possitive msg");

		textPositive = new Text(container, SWT.MULTI | SWT.BORDER);
		final GridData gd_textPositive = new GridData(SWT.FILL, SWT.FILL, true, true);
		textPositive.setLayoutData(gd_textPositive);

		final Label negativeMsgLabel = new Label(container, SWT.NONE);
		negativeMsgLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		negativeMsgLabel.setText("Negative msg");

		textNegative = new Text(container, SWT.MULTI | SWT.BORDER);
		final GridData gd_textNegative = new GridData(SWT.FILL, SWT.FILL, true, true);
		textNegative.setLayoutData(gd_textNegative);

		final Button conditionsButton = new Button(container, SWT.NONE);
		conditionsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				Conditions conditions = triggerPoint.getConditions();
				if (conditions == null) conditions = new Conditions();
				DialogConditionBuilder conditionsEditor = new DialogConditionBuilder(parentShell, conditions);
				conditionsEditor.open();
				if (conditions == null){
					textConditions.setText("null");
				}
				else {
					ConditionParser parser = new ConditionParser();
					textConditions.setText(parser.parseStrConditionList(conditions.getTaskConditionOrEventPointConditionOrInputCondition(), 0));
					triggerPoint.setConditions(conditions);
				}
			}
		});
		conditionsButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		conditionsButton.setText("Conditions");

		textConditions = new Text(container, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER);
		final GridData gd_textConditions = new GridData(SWT.FILL, SWT.FILL, true, true);
		textConditions.setLayoutData(gd_textConditions);
		setTitle("Trigger Point Editor");
		setMessage("Please fill in the form and click the button to edit Condition");
		if (triggerPoint != null && triggerPoint.getEventPoint() != null){
			textEventPoint.setText(triggerPoint.getEventPoint());
			textPositive.setText(triggerPoint.getPositiveDialog());
			textNegative.setText(triggerPoint.getNegativeDialog());
			textConditions.setText(CityAdvManager.getConditionArgument(triggerPoint.getConditions(),0));
		}
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
		return new Point(466, 375);
	}
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Quest Trigger Point Editor");
	}
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			// TODO
			triggerPoint.setEventPoint(textEventPoint.getText());
			triggerPoint.setPositiveDialog(textPositive.getText());
			triggerPoint.setNegativeDialog(textNegative.getText());
			
			if (dialogCheck() == false) return;
		}
		super.buttonPressed(buttonId);
	}
	boolean dialogCheck(){
		if (textEventPoint.getText().length() < 3){
			this.setMessage("Error: Event Point name length < 3");
			return false;
		}
		return true;
		
	}
}
