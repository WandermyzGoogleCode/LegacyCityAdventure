package org.codeidiot.cityadvstory.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.codeidiot.cityadvstory.CityAdvStory;
import org.codeidiot.cityadvstory.TaskTriggerPoint;
import org.codeidiot.cityadvstory.CityAdvStory.NPCs.NPC;
import org.codeidiot.cityadvstory.CityAdvStory.NPCs.NPC.Dialog;
import org.codeidiot.cityadvstory.CityAdvStory.Tasks.Task;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.widgets.Canvas;

public class ClientMain {
	String storyNameString;
	String storyDescription;
	private Label labelCount;
	private Button checkDoneEnd;
	private Button checkDoneStart;
	private Label labelSysMsg;
	Task currentTask = null;
	NPC currentNPC = null;
	CityAdvManager manager = new CityAdvManager();
	private Text textnpcEvent;
	private Text textnpcName;
	private Text textInfo;
	private Text textTaskName;
	private Text textTaskID;
	private List listNpc;
	private List listTasks;
	protected Shell shell;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ClientMain window = new ClientMain();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window
	 */
	public void open() {
		final Display display = Display.getDefault();
		createContents();
		currentNPC = new NPC();
		currentTask = new Task();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	
	}
	int selectionNpc = -1, selectionTask = -1;
	/**
	 * Create contents of the window
	 */
	protected void createContents() {
		shell = new Shell(SWT.TITLE | SWT.BORDER | SWT.CLOSE);
		shell.setDragDetect(false);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);
		shell.setSize(743, 633);
		shell.setText("City Advanture Client");
		{
			Canvas canvas = new Canvas(shell, SWT.NONE);
			canvas.setBackgroundImage(SWTResourceManager.getImage(ClientMain.class, "gui_logo.PNG"));
			final GridData gd_canvas = new GridData(SWT.FILL, SWT.FILL, false, false);
			gd_canvas.widthHint = 399;
			gd_canvas.heightHint = 118;
			canvas.setLayoutData(gd_canvas);
		}

		final Composite composite_3 = new Composite(shell, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		composite_3.setLayout(new GridLayout());

		final Group instructionsGroup = new Group(composite_3, SWT.NONE);
		instructionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		instructionsGroup.setLayout(new GridLayout());
		instructionsGroup.setText("Instructions");

		textInfo = new Text(instructionsGroup, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
		textInfo.setBackground(SWTResourceManager.getColor(246, 246, 246));
		textInfo.setEditable(false);
		textInfo.setFont(SWTResourceManager.getFont("Lucida Sans Unicode", 10, SWT.NONE));
		textInfo.setForeground(SWTResourceManager.getColor(128, 128, 128));
		textInfo.setText("City Adventure");
		textInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		labelSysMsg = new Label(instructionsGroup, SWT.BORDER);
		labelSysMsg.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		labelSysMsg.setText("Information");

		final Composite composite_2 = new Composite(shell, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		composite_2.setLayout(gridLayout_1);

		final Group tasksGroup = new Group(composite_2, SWT.NONE);
		final GridData gd_tasksGroup = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd_tasksGroup.widthHint = 205;
		tasksGroup.setLayoutData(gd_tasksGroup);
		tasksGroup.setLayout(new GridLayout());
		tasksGroup.setText("Tasks");

		listTasks = new List(tasksGroup, SWT.V_SCROLL | SWT.BORDER);
		listTasks.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(final MouseEvent e) {
				changeTaskSelection();
			}
		});
		listTasks.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				
			}
		});
		listTasks.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

		final Composite composite_7 = new Composite(tasksGroup, SWT.NONE);
		composite_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_8 = new GridLayout();
		gridLayout_8.numColumns = 2;
		composite_7.setLayout(gridLayout_8);

		final Button modifyButton = new Button(composite_7, SWT.NONE);
		modifyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				// TODO: add task event
				if (currentTask == null){
					currentTask = new Task();
				}
				if (textTaskID.getText().length() == 0 || textTaskName.getText().length() < 3){
					labelSysMsg.setText("Modify Task Error: Please Check ID and Name.");
					return;
				}
				if (checkDoneEnd.getSelection() == false || checkDoneStart.getSelection() == false){
					labelSysMsg.setText("Modify Task Error: Please Fill Check Point.");
					return;
				}
				currentTask.setId(textTaskID.getText());
				currentTask.setTitle(textTaskName.getText());
				String msg = manager.modifyTask(listTasks.getSelectionIndex(),manager.copyTask(currentTask));
				if (msg == null) labelSysMsg.setText("Successful");
				else labelSysMsg.setText(msg);
				refreshLists();
				refreshInfo();
			}
		});
		modifyButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		modifyButton.setText("Modify");

		final Button deleteButton = new Button(composite_7, SWT.NONE);
		deleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				String msg = manager.deleteTask(listTasks.getSelectionIndex());
				if (msg == null) labelSysMsg.setText("Successful");
				else labelSysMsg.setText(msg);
				refreshLists();
				
			}
		});
		deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		deleteButton.setText("Delete");

		final Group taskEditorGroup = new Group(composite_2, SWT.NONE);
		taskEditorGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		taskEditorGroup.setLayout(new GridLayout());
		taskEditorGroup.setText("Task Editor");

		final Composite composite = new Composite(taskEditorGroup, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		composite.setLayout(gridLayout_2);

		final Label taskIdLabel = new Label(composite, SWT.NONE);
		taskIdLabel.setText("ID");

		textTaskID = new Text(composite, SWT.BORDER);
		textTaskID.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				refreshInfo();
			}
		});
		textTaskID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label nameLabel = new Label(composite, SWT.NONE);
		nameLabel.setText("Name");

		textTaskName = new Text(composite, SWT.BORDER);
		textTaskName.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				refreshInfo();
			}
		});
		textTaskName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Group startPointGroup = new Group(taskEditorGroup, SWT.NONE);
		startPointGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout gridLayout_3 = new GridLayout();
		gridLayout_3.numColumns = 2;
		startPointGroup.setLayout(gridLayout_3);
		startPointGroup.setText("Check Point");

		final Button startPointButton = new Button(startPointGroup, SWT.NONE);
		startPointButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		startPointButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				//TODO
				TaskTriggerPoint triggerPoint = currentTask.getStart();
				if (triggerPoint == null) triggerPoint = new TaskTriggerPoint();
				DialogCheckPoint checkPoint = new DialogCheckPoint(shell, triggerPoint);
				int returnNum = checkPoint.open();
				if (returnNum == 1){
					labelSysMsg.setText("Add TP Cancelled");
				}
				else {
					labelSysMsg.setText("Add Start TP: @" + triggerPoint.getEventPoint());
					currentTask.setStart(triggerPoint);
					checkDoneStart.setSelection(true);
					refreshLists();
				}
				System.out.println("check: " + returnNum);
				refreshInfo();
			}
		});
		startPointButton.setText("Start Point");

		checkDoneStart = new Button(startPointGroup, SWT.CHECK);
		checkDoneStart.setEnabled(false);
		checkDoneStart.setText("Done");

		final Button startPointButton_1 = new Button(startPointGroup, SWT.NONE);
		startPointButton_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				//TODO
				TaskTriggerPoint triggerPoint = currentTask.getEnd();
				if (triggerPoint == null) triggerPoint = new TaskTriggerPoint();
				DialogCheckPoint checkPoint = new DialogCheckPoint(shell, triggerPoint);
				int returnNum = checkPoint.open();
				if (returnNum == 1){
					labelSysMsg.setText("Add TP Cancelled");
				}
				else {
					labelSysMsg.setText("Add End TP: @" + triggerPoint.getEventPoint());
					currentTask.setEnd(triggerPoint);
					checkDoneEnd.setSelection(true);
					refreshLists();
				}
				System.out.println("check: " + returnNum);
				refreshInfo();
			}
		});
		startPointButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		startPointButton_1.setText("End Point");

		checkDoneEnd = new Button(startPointGroup, SWT.CHECK);
		checkDoneEnd.setEnabled(false);
		checkDoneEnd.setText("Done");

		final Composite composite_1 = new Composite(taskEditorGroup, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_4 = new GridLayout();
		gridLayout_4.numColumns = 2;
		composite_1.setLayout(gridLayout_4);

		final Button addTaskButton = new Button(composite_1, SWT.NONE);
		addTaskButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				// TODO: add task event
				
				if (textTaskID.getText().length() == 0 || textTaskName.getText().length() < 2){
					labelSysMsg.setText("Add Task Error: Please Check ID and Name.");
					return;
				}
				if (checkDoneEnd.getSelection() == false || checkDoneStart.getSelection() == false){
					labelSysMsg.setText("Add Task Error: Please Fill Check Point.");
					return;
				}
				currentTask.setId(textTaskID.getText());
				currentTask.setTitle(textTaskName.getText());
				
				manager.addTask(listTasks.getSelectionIndex(), currentTask);
				refreshLists();
			}
		});
		addTaskButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		addTaskButton.setText("Add");

		final Button deleteButton_2 = new Button(composite_1, SWT.NONE);
		deleteButton_2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				textTaskID.setText("");
				textTaskName.setText("");
				checkDoneEnd.setSelection(false);
				checkDoneStart.setSelection(false);
			}
		});
		deleteButton_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		deleteButton_2.setText("Clear");

		final Group npcsGroup = new Group(composite_2, SWT.NONE);
		final GridData gd_npcsGroup = new GridData(SWT.FILL, SWT.FILL, false, true);
		gd_npcsGroup.widthHint = 169;
		npcsGroup.setLayoutData(gd_npcsGroup);
		npcsGroup.setLayout(new GridLayout());
		npcsGroup.setText("NPCs");

		listNpc = new List(npcsGroup, SWT.V_SCROLL | SWT.BORDER);
		listNpc.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(final MouseEvent e) {
				changeNPCSelection();
			}
		});
		listNpc.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
//				if (selectionNpc < 0 || listNpc.getSelectionIndex() < 0){
//					selectionNpc = listNpc.getSelectionIndex();
//				}
//				else {
//					DialogConfirmation confirmation = new DialogConfirmation(shell, "Will you modify this entry?");
//					int select = confirmation.open();
//					if (select == 0){
//						currentNPC.setName(textnpcName.getText());
//						currentNPC.setEventPoint(textnpcEvent.getText());
//						manager.modifyNpc(listNpc.getSelectionIndex(), manager.copyNPC(currentNPC));
//						refreshLists();
//						labelSysMsg.setText("Modification Confirmed");
//					}
//					else {
//						labelSysMsg.setText("Modification Cancelled");
//					}
//				}
			}
		});
		listNpc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

		final Composite composite_7_1 = new Composite(npcsGroup, SWT.NONE);
		composite_7_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_9 = new GridLayout();
		gridLayout_9.numColumns = 2;
		composite_7_1.setLayout(gridLayout_9);

		final Button buttonNpcMod = new Button(composite_7_1, SWT.NONE);
		buttonNpcMod.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (listNpc.getSelectionIndex() < 0) return;
				currentNPC.setName(textnpcName.getText());
				currentNPC.setEventPoint(textnpcEvent.getText());
				manager.modifyNpc(listNpc.getSelectionIndex(), manager.copyNPC(currentNPC));
				refreshLists();
			}
		});
		buttonNpcMod.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		buttonNpcMod.setText("Modify");

		final Button buttonNpcDel = new Button(composite_7_1, SWT.NONE);
		buttonNpcDel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (listNpc.getSelectionIndex() < 0) return;
				manager.deteleNPC(listNpc.getSelectionIndex());
				refreshLists();
			}
		});
		buttonNpcDel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		buttonNpcDel.setText("Delete");

		final Group npcEditorGroup = new Group(composite_2, SWT.NONE);
		npcEditorGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		npcEditorGroup.setText("NPC Editor");
		npcEditorGroup.setLayout(new GridLayout());

		final Composite composite_4 = new Composite(npcEditorGroup, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite_4.setLayout(new GridLayout());

		final Composite composite_5 = new Composite(composite_4, SWT.NONE);
		composite_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		final GridLayout gridLayout_5 = new GridLayout();
		gridLayout_5.numColumns = 2;
		composite_5.setLayout(gridLayout_5);

		final Label taskIdLabel_1 = new Label(composite_5, SWT.NONE);
		taskIdLabel_1.setText("Name");

		textnpcName = new Text(composite_5, SWT.BORDER);
		textnpcName.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				refreshNpcInfo();
			}
		});
		textnpcName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Label nameLabel_1 = new Label(composite_5, SWT.NONE);
		nameLabel_1.setText("Event");

		textnpcEvent = new Text(composite_5, SWT.BORDER);
		textnpcEvent.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				refreshNpcInfo();
			}
		});
		textnpcEvent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Composite composite_6 = new Composite(composite_4, SWT.NONE);
		composite_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_7 = new GridLayout();
		gridLayout_7.numColumns = 2;
		composite_6.setLayout(gridLayout_7);

		final Button buttonEditDialog = new Button(composite_6, SWT.NONE);
		buttonEditDialog.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				java.util.List<Dialog> dialog = currentNPC.getDialog();
				if (dialog == null){
					dialog = new ArrayList<Dialog>();
				}
				DialogNPCDialog dialogNPCDialog = new DialogNPCDialog(shell, dialog);
				int selection = dialogNPCDialog.open();
				if (selection == 0){
					labelSysMsg.setText("Modified Dialogs");
					labelCount.setText("." + dialog.size());
				}
				refreshNpcInfo();
			}
		});
		buttonEditDialog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		buttonEditDialog.setText("Edit Dialogs");

		labelCount = new Label(composite_6, SWT.NONE);
		labelCount.setText("Label");

		final Composite composite_1_1 = new Composite(composite_4, SWT.NONE);
		composite_1_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_6 = new GridLayout();
		gridLayout_6.numColumns = 2;
		composite_1_1.setLayout(gridLayout_6);

		final Button buttonNpcAdd = new Button(composite_1_1, SWT.NONE);
		buttonNpcAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (textnpcEvent.getText().length() < 2){
					labelSysMsg.setText("npc name wrong");
					return;
				}
				if (currentNPC.getDialog().size() < 1){
					labelSysMsg.setText("npc dialog empty");
					return;
				}
				if (currentNPC == null){
					currentNPC = new NPC();
				}
				currentNPC.setEventPoint(textnpcEvent.getText());
				currentNPC.setName(textnpcName.getText());
				manager.addNPC(listNpc.getSelectionIndex(), manager.copyNPC(currentNPC));
				refreshLists();
			}
		});
		buttonNpcAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		buttonNpcAdd.setText("Add");

		final Button buttonNpcClear = new Button(composite_1_1, SWT.NONE);
		buttonNpcClear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				textnpcEvent.setText("");
				textnpcName.setText("");
				labelCount.setText(".0");
			}
		});
		buttonNpcClear.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		buttonNpcClear.setText("Clear");
		new Label(shell, SWT.NONE);

		final Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		final MenuItem newSubmenuMenuItem = new MenuItem(menu, SWT.CASCADE);
		newSubmenuMenuItem.setText("Adv Files");

		final Menu menu_1 = new Menu(newSubmenuMenuItem);
		newSubmenuMenuItem.setMenu(menu_1);

		final MenuItem newItemMenuItem = new MenuItem(menu_1, SWT.NONE);
		newItemMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				manager.clear();
				refreshLists();
				refreshInfo();
				textTaskID.setText("");
				textTaskName.setText("");
				checkDoneEnd.setSelection(false);
				checkDoneStart.setSelection(false);
			}
		});
		newItemMenuItem.setText("New");

		new MenuItem(menu_1, SWT.SEPARATOR);

		final MenuItem newItemMenuItem_3 = new MenuItem(menu_1, SWT.NONE);
		newItemMenuItem_3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(shell);
				String fn = fileDialog.open();
				if (fn == null) return;
				int ret = manager.loadTo(fn);
				if (ret == 0){
					labelSysMsg.setText("Load successful");
					refreshLists();
					refreshInfo();
				}
				else {
					labelSysMsg.setText("Error Occured During Loading...");
				}
			}
		});
		newItemMenuItem_3.setText("Load");

		final MenuItem newItemMenuItem_4 = new MenuItem(menu_1, SWT.NONE);
		newItemMenuItem_4.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(shell);
				String fn = fileDialog.open();
				int ret = manager.saveAs(fn);
				if (ret == 0){
					labelSysMsg.setText("Save successful");
				}
				else {
					labelSysMsg.setText("Error Occured During Saving...");
				}
			}
		});
		newItemMenuItem_4.setText("Save As");

		new MenuItem(menu_1, SWT.SEPARATOR);

		final MenuItem newItemMenuItem_5 = new MenuItem(menu_1, SWT.NONE);
		newItemMenuItem_5.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				shell.close();
			}
		});
		newItemMenuItem_5.setText("Exit");

		final MenuItem newSubmenuMenuItem_1 = new MenuItem(menu, SWT.CASCADE);
		newSubmenuMenuItem_1.setText("Summary");

		final Menu menu_2 = new Menu(newSubmenuMenuItem_1);
		newSubmenuMenuItem_1.setMenu(menu_2);

		final MenuItem newItemMenuItem_6 = new MenuItem(menu_2, SWT.NONE);
		newItemMenuItem_6.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				String[] infoString = manager.checkCoherence();
				if (infoString == null || infoString.length == 0){
					infoString = new String[1];
					infoString[0] = "Check successful";
				}
				DialogDocView docView = new DialogDocView(shell, "Check Cohenrence Error Report", infoString);
				docView.open();
			}
		});
		newItemMenuItem_6.setText("Check Coherency");

		new MenuItem(menu_2, SWT.SEPARATOR);

		final MenuItem newItemMenuItem_7 = new MenuItem(menu_2, SWT.NONE);
		newItemMenuItem_7.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				try {
					manager.getXMLtoFile("temp.xml");
					Scanner scanner =  new Scanner(new FileInputStream("temp.xml"));
					String infoString[] = new String[1];
					infoString[0] = "XML viewer\n";
					while(scanner.hasNext()){
						infoString[0] += scanner.nextLine() + "\n";
					}
					
					DialogDocView docView = new DialogDocView(shell, "XML view report", infoString);
					docView.open();
					scanner.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		newItemMenuItem_7.setText("XML report View");

		final MenuItem newItemMenuItem_8 = new MenuItem(menu_2, SWT.NONE);
		newItemMenuItem_8.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell);
				dialog.setFilterExtensions(new String[] {"xml"});
				String fn = dialog.open();
				if (fn == null || fn.length() == 0) return;
				try {
					if (!fn.endsWith(".xml")){
						fn += ".xml";
					}
					manager.getXMLtoFile(fn);
					labelSysMsg.setText("XML save successful");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					labelSysMsg.setText("XML save failed");
				}
			}
		});
		newItemMenuItem_8.setText("XML save");

		final MenuItem newItemMenuItem_1 = new MenuItem(menu_2, SWT.NONE);
		newItemMenuItem_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				
				StoryInfo info = new StoryInfo();
				DialogLogin login = new DialogLogin(shell, info);
				login.open();
				manager.sendToServer(info.nameString, info.descString);
				String msg = String.format("Name: %s\nDescription: \n%s\n", info.nameString, info.descString);
				DialogDone done = new DialogDone(shell, msg);
				done.open();
			}
		});
		newItemMenuItem_1.setText("Send to Server");

		new MenuItem(menu_2, SWT.SEPARATOR);

		final MenuItem newItemMenuItem_9 = new MenuItem(menu_2, SWT.NONE);
		newItemMenuItem_9.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				StoryInfo info = new StoryInfo();
				FileDialog dialog = new FileDialog(shell);
				String fn = dialog.open();
				if (fn == null || fn.length() == 0) return;
				DialogLogin login = new DialogLogin(shell, info);
				login.open();
				manager.openSend(fn, info.nameString, info.descString);
				String msg = String.format("File: %s\nName: %s\nDescription: \n%s\n", fn, info.nameString, info.descString);
				DialogDone done = new DialogDone(shell, msg);
				done.open();
			}
		});
		newItemMenuItem_9.setText("Open Send XML");

		final MenuItem newItemMenuItem_2 = new MenuItem(menu, SWT.NONE);
		newItemMenuItem_2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				DialogAbout about = new DialogAbout(shell);
				about.open();
			}
		});
		newItemMenuItem_2.setText("About");
		//
	}
	
	class StoryInfo{
		String nameString;
		String descString;
	}
	void refreshLists(){
		//TODO: refresh all lists according to [manager]
		String[] taskListItem = manager.getTaskNameList();
		listTasks.setItems(taskListItem);
		String[] npcListItem = manager.getNPCNameList();
		listNpc.setItems(npcListItem);
	}
	void refreshInfo(){
		textInfo.setText(manager.getInfoTask(currentTask));
	}
	void refreshNpcInfo(){
		textInfo.setText(manager.getInfoNPCs(currentNPC));
	}
	void changeTaskSelection(){
		/*
		 * for: Task Editor
		 */
		int selection = listTasks.getSelectionIndex();
		if (selection < 0) return;
		currentTask = manager.getTaskCopy(selection);
		textTaskID.setText(currentTask.getId());
		textTaskName.setText(currentTask.getTitle());
		checkDoneEnd.setSelection(true);
		checkDoneStart.setSelection(true);
		refreshInfo();
	}
	void changeNPCSelection(){
		/*
		 * for: Task Editor
		 */
		int selection = listNpc.getSelectionIndex();
		if (selection < 0) return;
		currentNPC = manager.getNPCCopy(selection);
		textnpcEvent.setText(currentNPC.getEventPoint());
		textnpcName.setText(currentNPC.getName());
		labelCount.setText("." + currentNPC.getDialog().size());
		refreshNpcInfo();
	}
}
