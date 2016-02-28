package org.cityadv.androidgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codeidiot.cityadvstory.TaskConditionType;

import android.app.Activity;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class EventDialogActivity extends Activity {
	
	public static final String EXTRA_KEY_TASK_TYPE = "TASK_TYPE";
	public static final String EXTRA_KEY_TITLE = "TITLE";
	public static final String EXTRA_KEY_TASK_ID = "TASK_ID";	//when the dialog show a task, record its id
	public static final String EXTRA_KEY_EVENT_ID = "EVENT_ID";
	public static final String EXTRA_KEY_DIALOG_CONTENT = "DIALOG_CONTENT";
	public static final String EXTRA_KEY_START_TASKS_NAME = "START_TASKS_NAME";
	public static final String EXTRA_KEY_START_TASKS_ID = "START_TASKS_ID";
	public static final String EXTRA_KEY_END_TASKS_NAME = "END_TASKS_NAME";
	public static final String EXTRA_KEY_END_TASKS_ID = "END_TASKS_ID";
	
	public static final String RESULT_EXTRA_KEY_TASK_ID = "TASK_ID";
	public static final String RESULT_EXTRA_KEY_EVENT_ID = "EVENT_ID";
	
	public static final int RESULT_CODE_OK = -1;
	public static final int RESULT_CODE_CANCEL = 0;
	public static final int RESULT_CODE_TASK_ACCEPTED = 1;
	public static final int RESULT_CODE_TASK_FINISHED = 2;
	public static final int RESULT_CODE_TASK_START_SELECTED = 3;
	public static final int RESULT_CODE_TASK_END_SELECTED = 4;
	public static final int RESULT_CODE_TASK_CANCEL = 5;
	
	private ArrayList<String> startTasksId, endTasksId;
	private EventDialogTaskType taskType;
	private String taskId;
	private int eventId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.event_dialog);
		
		Button okButton = (Button) findViewById(R.id.eventDialogOkButton);
		okButton.setOnClickListener(onOkButtonClick);
		Button cancelButton = (Button) findViewById(R.id.eventDialogCancelButton);
		cancelButton.setOnClickListener(onCancelButtonClick);
		
		Intent intent = getIntent();
		String title = intent.getStringExtra(EXTRA_KEY_TITLE);
		String content = intent.getStringExtra(EXTRA_KEY_DIALOG_CONTENT);
		ArrayList<String> startTasks = intent.getStringArrayListExtra(EXTRA_KEY_START_TASKS_NAME);
		ArrayList<String> endTasks = intent.getStringArrayListExtra(EXTRA_KEY_END_TASKS_NAME);
		startTasksId = intent.getStringArrayListExtra(EXTRA_KEY_START_TASKS_ID);
		endTasksId = intent.getStringArrayListExtra(EXTRA_KEY_END_TASKS_ID);
		eventId = intent.getIntExtra(EXTRA_KEY_EVENT_ID, -1);
		
		taskId = intent.getStringExtra(EXTRA_KEY_TASK_ID);
		
		if(intent.hasExtra(EXTRA_KEY_TASK_TYPE)) {
			taskType = (EventDialogTaskType) intent.getSerializableExtra(EXTRA_KEY_TASK_TYPE);
		} else {
			taskType = EventDialogTaskType.NpcDialog;
		}

		switch(taskType) {
		case Assign:	//assign task
		case AssignNotEnabled:
			okButton.setText(R.string.event_dialog_accept);
			cancelButton.setText(R.string.event_dialog_reject);
			cancelButton.setVisibility(View.VISIBLE);
			break;
		case Query: //query task
			okButton.setText(R.string.event_dialog_ok);
			cancelButton.setVisibility(View.GONE);
			break;
		case Finish:
		case FinishNotEnabled:
			okButton.setText(R.string.event_dialog_finish);
			cancelButton.setText(R.string.event_dialog_cancel);
			cancelButton.setVisibility(View.VISIBLE);
			break;
		case NpcDialog:
			okButton.setText(R.string.event_dialog_ok);
			cancelButton.setVisibility(View.GONE);
			okButton.setEnabled(true);
			break;
		} 
		
		okButton.setEnabled(taskType != EventDialogTaskType.AssignNotEnabled
				&& taskType != EventDialogTaskType.FinishNotEnabled);
		
		if(title != null) {
			setTitle(title);
		}
		
		ArrayList<String> tasks = new ArrayList<String>();
		//TextView textView = (TextView) findViewById(R.id.eventDialogContent);
		if(content != null && content.length() > 0) {
			//textView.setText(content);
			tasks.add(content);
		} else {
			//textView.setText(R.string.event_dialog_nothing);
			tasks.add(getString(R.string.event_dialog_no_response));
		}
		
		if(endTasks != null && endTasks.size() != 0) {
			tasks.addAll(endTasks);
		}
		if(startTasks != null && startTasks.size() != 0) {
			tasks.addAll(startTasks);
		}

		
		if(tasks.size() != 0) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tasks);
			ListView listView = (ListView) findViewById(R.id.eventDialogList);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(onTaskItemClickListener);
		}
		
	}
	
	private OnItemClickListener onTaskItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			int startTasksSize = startTasksId != null ? startTasksId.size() : 0;
			int endTasksSize = endTasksId != null ? endTasksId.size() : 0;
			if(position < 1) {
				//do nothing
			}
			else if(position < endTasksSize + 1) {	//1 for dialog
				//doing task to finish it
				Intent data = new Intent();
				data.putExtra(RESULT_EXTRA_KEY_TASK_ID, endTasksId.get(position - 1));
				data.putExtra(RESULT_EXTRA_KEY_EVENT_ID, eventId);
				setResult(RESULT_CODE_TASK_END_SELECTED, data);
				finish();
			} else if(position < endTasksSize + startTasksSize + 1) {
				//new task to accept
				Intent data = new Intent();
				data.putExtra(RESULT_EXTRA_KEY_TASK_ID, startTasksId.get(position - endTasksSize - 1));
				data.putExtra(RESULT_EXTRA_KEY_EVENT_ID, eventId);
				setResult(RESULT_CODE_TASK_START_SELECTED, data);
				finish();
			}
		}
	};
	
	private OnClickListener onOkButtonClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent data = new Intent();
			data.putExtra(RESULT_EXTRA_KEY_EVENT_ID, eventId);
			
			switch(taskType) {
			case NpcDialog:
				setResult(RESULT_CODE_OK, data);
				break;
			case Assign:
				data.putExtra(RESULT_EXTRA_KEY_TASK_ID, taskId);
				setResult(RESULT_CODE_TASK_ACCEPTED, data);
				break;
			case Finish:
				data.putExtra(RESULT_EXTRA_KEY_TASK_ID, taskId);
				setResult(RESULT_CODE_TASK_FINISHED, data);
				break;
			case AssignNotEnabled:
			case FinishNotEnabled:
				setResult(RESULT_CODE_TASK_CANCEL, data);
				break;
			default:
				setResult(RESULT_CODE_CANCEL, data);
			}
			
			finish();
		}
	};
	
	private OnClickListener onCancelButtonClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent data = new Intent();
			data.putExtra(RESULT_EXTRA_KEY_EVENT_ID, eventId);
			switch(taskType) {
				case NpcDialog:
					setResult(RESULT_CODE_CANCEL, data);
					break;
				case Assign:
				case Finish:
				case AssignNotEnabled:
				case FinishNotEnabled:
					setResult(RESULT_CODE_TASK_CANCEL, data);
					break;
			}
			
			finish();
		}
	};
	
	public enum EventDialogTaskType {
		NpcDialog,
		Assign,
		AssignNotEnabled,
		Query,
		Finish,
		FinishNotEnabled,
	}
}
