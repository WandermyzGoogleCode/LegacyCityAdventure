package org.codeidiot.cityadvstory.ui;

import java.io.InputStream;

import org.codeidiot.cityadvstory.ConditionParser;
import org.codeidiot.cityadvstory.Conditions;
import org.codeidiot.cityadvstory.TaskConditionType;
import org.codeidiot.cityadvstory.Conditions.EventPointCondition;
import org.codeidiot.cityadvstory.Conditions.InputCondition;
import org.codeidiot.cityadvstory.Conditions.TaskCondition;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DialogConditionBuilder extends TitleAreaDialog {
	private Text textInputMsg;
	CityAdvManager manager = new CityAdvManager();
	private Combo combo;
	private Text textTaskID;
	private Text textEvent;
	private Text textInputAns;
	private List list;
	ConditionParser conditionParser = new ConditionParser();
	Conditions currConditions;
	/**
	 * Create the dialog
	 * @param parentShell
	 */
	public DialogConditionBuilder(Shell parentShell, Conditions conditions) {
		super(parentShell);
		currConditions = conditions;
	}
	
	void refreshConditions(){
		list.removeAll();
		java.util.List<Object> condList = currConditions.getTaskConditionOrEventPointConditionOrInputCondition();
		for (int a = 0; a < condList.size(); ++a){
			list.add(conditionParser.getStrCondition(condList.get(a)));
		}
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

		list = new List(container, SWT.BORDER);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Composite composite = new Composite(container, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		composite.setLayout(gridLayout_1);

		final Group inputConditionGroup = new Group(composite, SWT.NONE);
		inputConditionGroup.setText("Input Condition");
		inputConditionGroup.setLayout(new GridLayout());

		final Label messageLabel = new Label(inputConditionGroup, SWT.NONE);
		messageLabel.setText("Message");

		textInputMsg = new Text(inputConditionGroup, SWT.BORDER);
		final GridData gd_textInputMsg = new GridData(SWT.FILL, SWT.CENTER, true, false);
		textInputMsg.setLayoutData(gd_textInputMsg);

		final Label inputAnswerLabel = new Label(inputConditionGroup, SWT.NONE);
		inputAnswerLabel.setLayoutData(new GridData());
		inputAnswerLabel.setText("Input Answer");

		textInputAns = new Text(inputConditionGroup, SWT.BORDER);
		final GridData gd_textInputAns = new GridData(SWT.FILL, SWT.CENTER, true, false);
		textInputAns.setLayoutData(gd_textInputAns);

		final Button addCondButton = new Button(inputConditionGroup, SWT.NONE);
		addCondButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				// TODO
				InputCondition condition = new InputCondition();
				condition.setAnswer(textInputAns.getText());
				condition.setMessage(textInputMsg.getText());
				currConditions.getTaskConditionOrEventPointConditionOrInputCondition().add(condition);
				refreshConditions();
			}
		});
		final GridData gd_addCondButton = new GridData(SWT.FILL, SWT.CENTER, false, false);
		addCondButton.setLayoutData(gd_addCondButton);
		addCondButton.setText("Add Cond.");

		final Group taskConditionGroup = new Group(composite, SWT.NONE);
		taskConditionGroup.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		taskConditionGroup.setText("Task Condition");
		taskConditionGroup.setLayout(new GridLayout());

		final Label taskNameLabel = new Label(taskConditionGroup, SWT.NONE);
		taskNameLabel.setText("Task ID");

		textTaskID = new Text(taskConditionGroup, SWT.BORDER);
		final GridData gd_textTaskID = new GridData(SWT.FILL, SWT.CENTER, true, false);
		textTaskID.setLayoutData(gd_textTaskID);

		final Label taskStatusLabel = new Label(taskConditionGroup, SWT.NONE);
		taskStatusLabel.setText("Task Status");

		combo = new Combo(taskConditionGroup, SWT.NONE);
		combo.setSelection(new Point(0, 0));
		combo.setItems(new String[] {"Not Taken", "Taken", "Done"});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Button addCondButton_2 = new Button(taskConditionGroup, SWT.NONE);
		addCondButton_2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TaskCondition condition = new TaskCondition();
				condition.setTaskId(textTaskID.getText());
				if (combo.getSelectionIndex() < 0) return;
				condition.setType(TaskConditionType.values()[combo.getSelectionIndex()]);
				currConditions.getTaskConditionOrEventPointConditionOrInputCondition().add(condition);
				refreshConditions();
			}
		});
		addCondButton_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		addCondButton_2.setText("Add Cond.");

		final Group eventConditionGroup = new Group(composite, SWT.NONE);
		eventConditionGroup.setText("Event Condition");
		eventConditionGroup.setLayout(new GridLayout());

		final Label eventNameLabel = new Label(eventConditionGroup, SWT.NONE);
		eventNameLabel.setText("Event Name");

		textEvent = new Text(eventConditionGroup, SWT.BORDER);
		final GridData gd_textEvent = new GridData(SWT.FILL, SWT.CENTER, true, false);
		textEvent.setLayoutData(gd_textEvent);

		final Button addCondButton_1 = new Button(eventConditionGroup, SWT.NONE);
		addCondButton_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				EventPointCondition condition = new EventPointCondition();
				condition.setName(textEvent.getText());
				currConditions.getTaskConditionOrEventPointConditionOrInputCondition().add(condition);
				refreshConditions();
			}
		});
		addCondButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		addCondButton_1.setText("Add Cond.");

		final Button clearListButton = new Button(composite, SWT.NONE);
		clearListButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				list.removeAll();
				currConditions.getTaskConditionOrEventPointConditionOrInputCondition().clear();
				refreshConditions();
			}
		});
		final GridData gd_clearListButton = new GridData(SWT.FILL, SWT.CENTER, true, false);
		clearListButton.setLayoutData(gd_clearListButton);
		clearListButton.setText("Clear List");
		setTitle("Condition Builder");
		setMessage("Edit condiction for current event");
		refreshConditions();
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
		return new Point(500, 472);
	}
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Condition Builder");
	}

}
