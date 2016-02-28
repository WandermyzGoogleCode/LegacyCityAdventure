package org.cityadv.androidgame.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cityadv.androidgame.EventDialogActivity;
import org.cityadv.androidgame.EventDialogActivity.EventDialogTaskType;
import org.cityadv.androidgame.GameEngineActivity;
import org.cityadv.androidgame.R;
import org.cityadv.androidgame.engine.ConditionChecker.OnConditionCheckedListener;
import org.cityadv.androidgame.jni.JniLibrary;
import org.codeidiot.cityadvstory.CityAdvStory;
import org.codeidiot.cityadvstory.Conditions;
import org.codeidiot.cityadvstory.CityAdvStory.NPCs.NPC;
import org.codeidiot.cityadvstory.CityAdvStory.NPCs.NPC.Dialog;
import org.codeidiot.cityadvstory.CityAdvStory.Tasks.Task;
import org.codeidiot.cityadvstory.DialogType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * non-graphic logic for the game
 * @author Wander
 *
 */
public class GameEngine {
	
	private static final String TAG = "GameEngine";
	private static final String FILE_EXT_EVENT_HINT = "evt";
	
	private static final Pattern qrContentPattern = Pattern.compile("(\\d+) ([0-9a-f]{40})");
	
	private Activity activity;
	private HashMap<Integer, String> eventIdToName;
	private HashMap<String, Integer> eventNameToId;
	
	private CityAdvStory story;
	private HashMap<Integer, NPC> eventNpcMap;
	private HashMap<Integer, ArrayList<Task>> eventTaskStartMap, eventTaskEndMap;
	private HashMap<String, Task> taskMap;
	private GameData gameData;
	private ConditionChecker conditionChecker;
	
	public GameEngine(Activity activity) {
		this.activity = activity;
		
		eventIdToName = new HashMap<Integer, String>();
		eventNameToId = new HashMap<String, Integer>();
		eventNpcMap = new HashMap<Integer, NPC>();
		eventTaskStartMap = new HashMap<Integer, ArrayList<Task>>();
		eventTaskEndMap = new HashMap<Integer, ArrayList<Task>>();
		taskMap = new HashMap<String, CityAdvStory.Tasks.Task>();
		
		gameData = GameData.create();	//TODO: S/L
		conditionChecker = new ConditionChecker(activity, gameData);
	}
	
	/**
	 * Load map from a file. will call JNI
	 * @param filePath
	 * @throws IOException 
	 */
	public void loadMapFromFile(String filePath) throws IOException {
		boolean result = JniLibrary.gameEngineLoadMapFromFile(filePath);
		if(!result) {
			throw new IOException("Failed to load map in native code");
		}
		
		loadEventHint(filePath + "." + FILE_EXT_EVENT_HINT);
	}
	
	public void loadStoryFromFile(String filePath) throws StreamCorruptedException, FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(filePath));
		story = (CityAdvStory) objIn.readObject();
		objIn.close();
		
		for(Task t : story.getTasks().getTask()) {
			
			taskMap.put(t.getId(), t);
			
			String start = t.getStart().getEventPoint();
			if(!eventNameToId.containsKey(start)) {
				Log.e(TAG, "Event not exist: " + start);
			}
			int startId = eventNameToId.get(start).intValue();
			if(!eventTaskStartMap.containsKey(startId)) {
				eventTaskStartMap.put(startId, new ArrayList<Task>());
			}
			eventTaskStartMap.get(startId).add(t);
			
			String end = t.getEnd().getEventPoint();
			if(!eventNameToId.containsKey(start)) {
				Log.e(TAG, "Event not exist: " + start);
			}
			int endId = eventNameToId.get(end);
			if(!eventTaskEndMap.containsKey(endId)) {
				eventTaskEndMap.put(endId, new ArrayList<Task>());
			}
			eventTaskEndMap.get(endId).add(t);
		}
		
		for(NPC npc : story.getNPCs().getNPC()) {
			String npcEvent = npc.getEventPoint();
			if(!eventNameToId.containsKey(npcEvent)) {
				Log.e(TAG, "Event not exist: " + npcEvent);
			}
			int npcEventId = eventNameToId.get(npcEvent);
			if(eventNpcMap.containsKey(npcEventId)) {
				Log.e(TAG, "NPC at this event already exist: " + npcEvent);
			} else {
				eventNpcMap.put(npcEventId, npc);
			}
		}
		
		Log.d(TAG, "Story loaded: " + filePath);
		//Toast.makeText(activity, story.getNPCs().getNPC().get(0).getDialog().get(0).getContent(), Toast.LENGTH_LONG).show();
	}
	
	public void parseQrCode(String content) throws InvalidQrContentException {
		Matcher m = qrContentPattern.matcher(content);
		if(!m.matches()) {
			throw new InvalidQrContentException(content, InvalidQrContentErrorType.FormatError);
		}
		
		int id = Integer.parseInt(m.group(1));
		String sha1 = m.group(2).trim();
		
		if(!eventIdToName.containsKey(id)) {
			throw new InvalidQrContentException(content, InvalidQrContentErrorType.IdNotExist);
		}
		
		//check illegal
		byte[] eventRawData = JniLibrary.gameEngineGetEventRawData(id);	//id range has checked
	    MessageDigest md;
	    try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "sha1 error", e);
			return;
		}
	    md.update(eventRawData);
	    byte[] sha1hash = md.digest();
	    
	    StringBuilder sb = new StringBuilder();
	    for(int i = 0; i < sha1hash.length; i++) {
	    	sb.append(String.format("%02x", sha1hash[i]));
	    }
	    
	    if(!sha1.equals(sb.toString())) {
	    	throw new InvalidQrContentException(content, InvalidQrContentErrorType.IdNotExist);
	    }
	    
	    JniLibrary.gameEngineGotoEventPoint(id);
	    
	    //record arrival
	    gameData.getVisitedEventPoints().add(eventIdToName.get(id));
	    
	    //show dialog
	    showNpcDialog(id);
	}
	
	/**
	 * Occured when a user select a task to start/finish in the dialog, or accept/finish a task
	 * @param taskId
	 * @param type
	 */
	public void handleEventDialogResult(int resultCode, final String taskId, final int eventId) {
		switch(resultCode) {
			case EventDialogActivity.RESULT_CODE_OK:
			case EventDialogActivity.RESULT_CODE_CANCEL:
				//nothing to do
				break;
			case EventDialogActivity.RESULT_CODE_TASK_CANCEL:
				if(eventId > 0) {
					showNpcDialog(eventId);
				}
				break;
			case EventDialogActivity.RESULT_CODE_TASK_START_SELECTED: {
				final Task task = taskMap.get(taskId);
				conditionChecker.check(task.getStart().getConditions(), new OnConditionCheckedListener() {
					@Override
					public void onConditionChecked(boolean result) {
						EventDialogTaskType type = result ? EventDialogTaskType.Assign : EventDialogTaskType.AssignNotEnabled;
						Intent intent = new Intent(activity, EventDialogActivity.class);
						intent.putExtra(EventDialogActivity.EXTRA_KEY_TASK_TYPE, type);
						intent.putExtra(EventDialogActivity.EXTRA_KEY_EVENT_ID, eventId);
						intent.putExtra(EventDialogActivity.EXTRA_KEY_TASK_ID, taskId);
						intent.putExtra(EventDialogActivity.EXTRA_KEY_DIALOG_CONTENT, 
								result ? task.getStart().getPositiveDialog() : task.getStart().getNegativeDialog());
						intent.putExtra(EventDialogActivity.EXTRA_KEY_TITLE, task.getTitle());
						activity.startActivityForResult(intent, GameEngineActivity.REQUEST_EVENT_DIALOG);
					}
				});
				break;
			}
			case EventDialogActivity.RESULT_CODE_TASK_END_SELECTED: {
				final Task task = taskMap.get(taskId);
				conditionChecker.check(task.getEnd().getConditions(), new OnConditionCheckedListener() {
					@Override
					public void onConditionChecked(boolean result) {
						EventDialogTaskType type = result ? EventDialogTaskType.Finish : EventDialogTaskType.FinishNotEnabled;
						Intent intent = new Intent(activity, EventDialogActivity.class);
						intent.putExtra(EventDialogActivity.EXTRA_KEY_TASK_TYPE, type);
						intent.putExtra(EventDialogActivity.EXTRA_KEY_EVENT_ID, eventId);
						intent.putExtra(EventDialogActivity.EXTRA_KEY_TASK_ID, taskId);
						intent.putExtra(EventDialogActivity.EXTRA_KEY_DIALOG_CONTENT, 
								result ? task.getEnd().getPositiveDialog() : task.getEnd().getNegativeDialog());
						intent.putExtra(EventDialogActivity.EXTRA_KEY_TITLE, task.getTitle());
						activity.startActivityForResult(intent, GameEngineActivity.REQUEST_EVENT_DIALOG);
					}
				});
				break;
			}
			case EventDialogActivity.RESULT_CODE_TASK_ACCEPTED: {
				gameData.getDoingTasks().add(taskId);
				Toast.makeText(activity, 
						activity.getString(R.string.task_accepted, taskMap.get(taskId).getTitle()),
						Toast.LENGTH_SHORT).show();
				if(eventId > 0) {
					showNpcDialog(eventId);
				}
				break;
			}
			case EventDialogActivity.RESULT_CODE_TASK_FINISHED: {
				gameData.getDoingTasks().remove(taskId);
				gameData.getCompletedTasks().add(taskId);
				Toast.makeText(activity, 
						activity.getString(R.string.task_finished, taskMap.get(taskId).getTitle()),
						Toast.LENGTH_SHORT).show();
				if(eventId > 0) {
					showNpcDialog(eventId);
				}
				break;
			}
			
		}
	}


	private void loadEventHint(String evtFilePath) throws IOException {
		//load event hint
		BufferedReader hintReader;

		hintReader = new BufferedReader(new InputStreamReader(new FileInputStream(evtFilePath)));
		hintReader.readLine();	//skip header;
		hintReader.readLine();	//skip event size
		
		String line;
		
		while((line = hintReader.readLine()) != null) {
			Scanner scanner = new Scanner(line);
			int id = scanner.nextInt();
			scanner.nextLong();	//skip length
			String name = scanner.nextLine().trim();
			eventIdToName.put(id, name);
			eventNameToId.put(name, id);
		}
		
		hintReader.close();
	}
	

	private void showNpcDialog(int eventId) {
		final Intent intent = new Intent(activity, EventDialogActivity.class);
		intent.putExtra(EventDialogActivity.EXTRA_KEY_EVENT_ID, eventId);

		boolean hasEvent = false, hasNpc = false;
		
		if(eventTaskStartMap.containsKey(eventId)) { //check start tasks
			hasEvent = true;
			ArrayList<Task> taskList = eventTaskStartMap.get(eventId);
			ArrayList<String> taskNames = new ArrayList<String>();
			ArrayList<String> taskIds = new ArrayList<String>();
			for(Task t : taskList) {
				if(!gameData.getDoingTasks().contains(t.getId()) 
						&& !gameData.getCompletedTasks().contains(t.getId())) {
					taskNames.add(t.getTitle());
					taskIds.add(t.getId());
				}
			}
			intent.putStringArrayListExtra(EventDialogActivity.EXTRA_KEY_START_TASKS_NAME, taskNames);
			intent.putStringArrayListExtra(EventDialogActivity.EXTRA_KEY_START_TASKS_ID, taskIds);
		}
		
		if(eventTaskEndMap.containsKey(eventId)) { //check end tasks
			hasEvent = true;
			ArrayList<Task> taskList = eventTaskEndMap.get(eventId);
			ArrayList<String> taskNames = new ArrayList<String>();
			ArrayList<String> taskIds = new ArrayList<String>();
			for(Task t : taskList) {
				if(gameData.getDoingTasks().contains(t.getId())) {
					taskNames.add(t.getTitle());
					taskIds.add(t.getId());
				}
			}
			intent.putStringArrayListExtra(EventDialogActivity.EXTRA_KEY_END_TASKS_NAME, taskNames);
			intent.putStringArrayListExtra(EventDialogActivity.EXTRA_KEY_END_TASKS_ID, taskIds);
		}
		
		if(eventNpcMap.containsKey(eventId)) {	//check npc
			hasNpc = true;
			NPC npc = eventNpcMap.get(eventId);
			intent.putExtra(EventDialogActivity.EXTRA_KEY_TITLE, npc.getName());
			findNpcDialog(npc, 0, new OnFindNpcDialogFinished() {
				@Override
				public void onFindNpcDialogFinished(Dialog dialog) {
					if(dialog != null) {
						intent.putExtra(EventDialogActivity.EXTRA_KEY_DIALOG_CONTENT, dialog.getContent());
					}
					activity.startActivityForResult(intent, GameEngineActivity.REQUEST_EVENT_DIALOG);					
				}
			});		
		}
		
		if(!hasEvent && !hasNpc) {
			Toast.makeText(activity, R.string.event_dialog_nothing, Toast.LENGTH_SHORT).show();
		} else if (hasEvent && !hasNpc) {
			activity.startActivityForResult(intent, GameEngineActivity.REQUEST_EVENT_DIALOG);
		} else {
			//do nothing because findNpcDialog will show the activity
		}
	}
	
	private void findNpcDialog(final NPC npc, final int startId, final OnFindNpcDialogFinished onFindNpcDialogFinished) {
		if(startId >= npc.getDialog().size()) {
			//reach the end, find nothing
			onFindNpcDialogFinished.onFindNpcDialogFinished(null);
			return;
		}
		
		final Dialog dialog = npc.getDialog().get(startId);
		if(dialog.getType() != DialogType.TRIGGER) {	//not trigger dialog, find next
			findNpcDialog(npc, startId + 1, onFindNpcDialogFinished);
		}
		
		conditionChecker.check(dialog.getConditions(), new OnConditionCheckedListener() {
			@Override
			public void onConditionChecked(boolean result) {
				if(!result) {
					//not satisfy, check next dialog
					findNpcDialog(npc, startId + 1, onFindNpcDialogFinished);
				} else {
					//satisfied
					onFindNpcDialogFinished.onFindNpcDialogFinished(dialog);
				}
			}
		});
	}
	
	public static enum InvalidQrContentErrorType {
		FormatError,
		IdNotExist,
		WrongMap,
	}
	
	public static class InvalidQrContentException extends Exception {
		private static final long serialVersionUID = 1L;
		private InvalidQrContentErrorType type;
		public InvalidQrContentException(String content, InvalidQrContentErrorType errorType) {
			super(content);
			type = errorType;
		}
		
		public InvalidQrContentErrorType getErrorType() {
			return type;
		}
	}
	
	private static interface OnFindNpcDialogFinished {
		void onFindNpcDialogFinished(Dialog dialog);
	}
}
