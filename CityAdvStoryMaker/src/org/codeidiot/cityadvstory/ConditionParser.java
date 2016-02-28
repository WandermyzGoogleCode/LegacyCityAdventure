package org.codeidiot.cityadvstory;

import java.util.List;

import org.codeidiot.cityadvstory.Conditions.EventPointCondition;
import org.codeidiot.cityadvstory.Conditions.InputCondition;
import org.codeidiot.cityadvstory.Conditions.TaskCondition;

public class ConditionParser {
	public String getStrCondition(Object object){
		if (object instanceof EventPointCondition){
			EventPointCondition condition = (EventPointCondition) object;
			return String.format("@Event: %s", condition.getName());
		}
		else if (object instanceof InputCondition){
			InputCondition condition = (InputCondition) object;
			return String.format("@input = %s (msg: %s)", 
					condition.getAnswer(),
					condition.getMessage());
		}
		else if (object instanceof TaskCondition){
			TaskCondition condition = (TaskCondition)object;
			return String.format("@Task.%s: %s", condition.getType().name(),
					condition.getTaskId());
		}
		else return null;
	}
	
	public String parseStrConditionList(List<Object>conditionList, int level){
		String condString = "";
		for (int b = 0; b < conditionList.size(); ++b){
			for (int a= 0; a < level; ++a) condString += "\t";
			condString += getStrCondition(conditionList.get(b)) + "\n";
		}
		return condString;
	}
}
