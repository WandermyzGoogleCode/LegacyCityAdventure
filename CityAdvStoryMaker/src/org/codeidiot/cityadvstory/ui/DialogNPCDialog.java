package org.codeidiot.cityadvstory.ui;

import org.codeidiot.cityadvstory.Conditions;
import org.codeidiot.cityadvstory.DialogType;
import org.codeidiot.cityadvstory.CityAdvStory.NPCs.NPC.Dialog;
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

public class DialogNPCDialog extends TitleAreaDialog {
	private List listDialog;
	java.util.List<Dialog> dialogList;
	Dialog currentDialog;
	Shell parentShell;
	private Text textCondition;
	private Text textContents;
	private List comboType;
	/**
	 * Create the dialog
	 * @param parentShell
	 */
	public DialogNPCDialog(Shell parentShell, java.util.List<Dialog> dialogs) {
		super(parentShell);
		dialogList = dialogs;
		this.parentShell = parentShell;
	
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

		final Composite composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		composite_1.setLayout(new GridLayout());

		final Button contentsButton = new Button(composite_1, SWT.NONE);
		contentsButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		contentsButton.setText("View All Dialog");

		listDialog = new List(composite_1, SWT.BORDER);
		listDialog.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (listDialog.getSelectionIndex() < 0) return;
				currentDialog = CityAdvManager.copyDialog(dialogList.get(listDialog.getSelectionIndex()));
				int typeIndex = currentDialog.getType().ordinal();
				comboType.setSelection(typeIndex);
				textContents.setText(currentDialog.getContent());
			}
		});
		final GridData gd_listDialog = new GridData(SWT.FILL, SWT.FILL, false, true);
		listDialog.setLayoutData(gd_listDialog);

		final Composite composite_2 = new Composite(composite_1, SWT.NONE);
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 3;
		composite_2.setLayout(gridLayout_2);

		final Button addButton = new Button(composite_2, SWT.NONE);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (comboType.getSelectionIndex() < 0 || textContents.getText().length() < 1) {
					setMessage("Failed to Add Dialog entry...");
					return;
				}
				if (currentDialog == null) currentDialog = new Dialog();
				currentDialog.setContent(textContents.getText());
				currentDialog.setType(DialogType.values()[comboType.getSelectionIndex()]);
				dialogList.add(CityAdvManager.copyDialog(currentDialog));
				refreshList();
			}
		});
		addButton.setText("Add");

		final Button modifyButton = new Button(composite_2, SWT.NONE);
		modifyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (listDialog.getSelectionIndex() < 0) return;
				if (comboType.getSelectionIndex() < 0 || textContents.getText().length() < 1) {
					setMessage("Failed to Add Dialog entry...");
					return;
				}
				if (currentDialog == null) currentDialog = new Dialog();
				currentDialog.setContent(textContents.getText());
				currentDialog.setType(DialogType.values()[comboType.getSelectionIndex()]);
				dialogList.set(listDialog.getSelectionIndex(), CityAdvManager.copyDialog(currentDialog));
				refreshList();
			}
		});
		modifyButton.setText("Modify");

		final Button deleteButton = new Button(composite_2, SWT.NONE);
		deleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (listDialog.getSelectionIndex() < 0) return;
				dialogList.remove(listDialog.getSelectionIndex());
				refreshList();
			}
		});
		deleteButton.setText("Delete");

		final Composite informaitonGroup = new Composite(container, SWT.NONE);
		informaitonGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		informaitonGroup.setLayout(gridLayout_1);

		final Label dialogTyleLabel = new Label(informaitonGroup, SWT.NONE);
		dialogTyleLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		dialogTyleLabel.setText("Dialog Type");

		comboType = new List(informaitonGroup, SWT.BORDER);
		comboType.select(0);
		comboType.setItems(new String[] {"Trigger", "Near"});
		final GridData gd_comboType = new GridData(SWT.FILL, SWT.CENTER, false, false);
		comboType.setLayoutData(gd_comboType);

		final Composite composite = new Composite(informaitonGroup, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, true));
		composite.setLayout(new GridLayout());

		final Label contentsLabel = new Label(composite, SWT.NONE);
		contentsLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		contentsLabel.setText("Contents");

		textContents = new Text(informaitonGroup, SWT.MULTI | SWT.BORDER);
		final GridData gd_textContents = new GridData(SWT.FILL, SWT.FILL, true, true);
		textContents.setLayoutData(gd_textContents);

		final Button editConditionButton = new Button(informaitonGroup, SWT.NONE);
		editConditionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (currentDialog == null) currentDialog = new Dialog();
				Conditions conditions = currentDialog.getConditions();
				if (conditions == null){
					conditions = new Conditions();
				}
				DialogConditionBuilder conditionsEditor = new DialogConditionBuilder(parentShell, conditions);
				int ret = conditionsEditor.open();
				if (ret == 0){
					textCondition.setText(CityAdvManager.getConditionArgument(conditions, 0));
					setMessage("Condition Edited");
					currentDialog.setConditions(conditions);
				}
				else {
					setMessage("Condition Editor Cancelled");
				}
			}
		});
		final GridData gd_editConditionButton = new GridData(SWT.FILL, SWT.FILL, false, true);
		editConditionButton.setLayoutData(gd_editConditionButton);
		editConditionButton.setText("Conditions");

		textCondition = new Text(informaitonGroup, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER);
		final GridData gd_textCondition = new GridData(SWT.FILL, SWT.FILL, true, true);
		textCondition.setLayoutData(gd_textCondition);
		setTitle("Dialog Editor");
		setMessage("Here Display Dialog object");
		refreshList();
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
		newShell.setText("Dialog Editor");
	}
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			if (dialogList.size()< 1){
				setMessage("Empty Dialog, please press Cancel");
				return;
			}
		}
		super.buttonPressed(buttonId);
	}
	void refreshList(){
		if (dialogList == null) return;
		String items[] = new String[dialogList.size()];
		for (int a =0 ; a < dialogList.size(); ++a){
			Dialog oneDialog = dialogList.get(a);
			int mlen = oneDialog.getContent().length();
			if (mlen > 16) mlen = 16;
			items[a] = a + "." + oneDialog.getType().name() + ": " + oneDialog.getContent().substring(0,mlen);
		}
		listDialog.setItems(items);
	}
}
