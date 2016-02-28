package org.cityadv.androidgame.engine;

import java.util.List;

import org.cityadv.androidgame.R;
import org.codeidiot.cityadvstory.Conditions;
import org.codeidiot.cityadvstory.Conditions.EventPointCondition;
import org.codeidiot.cityadvstory.Conditions.InputCondition;
import org.codeidiot.cityadvstory.Conditions.SelectionCondition;
import org.codeidiot.cityadvstory.Conditions.TaskCondition;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;

public class ConditionChecker {
	
	private GameData gameData;
	private Activity activity;
	
	public ConditionChecker(Activity activity, GameData gameData) {
		this.activity = activity;
		this.gameData = gameData;
	}
	
	public void check(Conditions conditions, OnConditionCheckedListener listener) {
		check(conditions, 0, listener);
	}
	
	private void check(Conditions conditions, int currentPos, OnConditionCheckedListener listener) {
		if(conditions == null || currentPos >= conditions.getTaskConditionOrEventPointConditionOrInputCondition().size()) {
			listener.onConditionChecked(true);
			return;
		}
		
		List<Object> objList = conditions.getTaskConditionOrEventPointConditionOrInputCondition();
		
		boolean result = true;
		for(int i = currentPos; i < objList.size(); i++) {
			Object obj = objList.get(i);
			if(obj instanceof TaskCondition) {
				TaskCondition taskCondition = (TaskCondition) obj;
				switch(taskCondition.getType()) {
				case COMPLETED:
					result = gameData.getCompletedTasks().contains(taskCondition.getTaskId());
					break;
				case DOING:
					result = gameData.getDoingTasks().contains(taskCondition.getTaskId());
					break;
				case NOT_TAKEN:
					result = !(gameData.getCompletedTasks().contains(taskCondition.getTaskId()))
							&& !(gameData.getDoingTasks().contains(taskCondition.getTaskId()));
					break;
				}
			} else if (obj instanceof EventPointCondition) {
				EventPointCondition eventPointCondition = (EventPointCondition) obj;
				result = gameData.getVisitedEventPoints().contains(eventPointCondition.getName());
			} else if (obj instanceof InputCondition) {
				showInputDialogAndContinue(conditions, listener, i);
				return;
			} else if (obj instanceof SelectionCondition) {
				showSelectionDialogAndContinue(conditions, listener, i);
				return;
			}
			
			if(!result) {
				break;
			}
		}
		
		listener.onConditionChecked(result);
	}
	
	private void showSelectionDialogAndContinue(Conditions conditions, OnConditionCheckedListener listener, int currentPos) {
		//TODO:
	}

	private void showInputDialogAndContinue(final Conditions conditions, final OnConditionCheckedListener listener, final int currentPos) {
		
		Object obj = conditions.getTaskConditionOrEventPointConditionOrInputCondition().get(currentPos);
		if(!(obj instanceof InputCondition)) {
			throw new IllegalArgumentException();
		}
		
		final InputCondition inputCondition = (InputCondition) obj;
		
		AlertDialog.Builder builder = new Builder(activity);
		builder.setTitle(R.string.task_input_answer_title);
		builder.setMessage(inputCondition.getMessage());
		
		final EditText editText = new EditText(activity);
		builder.setView(editText);
		
		builder.setPositiveButton(activity.getString(R.string.task_input_answer_ok), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String trial = editText.getText().toString();
				if(trial.equals(inputCondition.getAnswer())) {
					check(conditions, currentPos + 1, listener);
				} else {
					listener.onConditionChecked(false);
				}
			}
		});
		
		builder.show();
	}
	
	public static interface OnConditionCheckedListener {
		void onConditionChecked(boolean result);
	}
}
