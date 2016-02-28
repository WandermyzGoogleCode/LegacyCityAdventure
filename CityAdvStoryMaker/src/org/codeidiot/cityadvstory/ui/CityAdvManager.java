package org.codeidiot.cityadvstory.ui;

import java.awt.Event;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.W3CDomHandler;
import javax.xml.parsers.DocumentBuilder;

import org.codeidiot.cityadvstory.CityAdvStory;
import org.codeidiot.cityadvstory.ConditionParser;
import org.codeidiot.cityadvstory.Conditions;
import org.codeidiot.cityadvstory.TaskConditionType;
import org.codeidiot.cityadvstory.TaskTriggerPoint;
import org.codeidiot.cityadvstory.CityAdvStory.NPCs;
import org.codeidiot.cityadvstory.CityAdvStory.Tasks;
import org.codeidiot.cityadvstory.CityAdvStory.NPCs.NPC;
import org.codeidiot.cityadvstory.CityAdvStory.NPCs.NPC.Dialog;
import org.codeidiot.cityadvstory.CityAdvStory.Tasks.Task;
import org.codeidiot.cityadvstory.Conditions.EventPointCondition;
import org.codeidiot.cityadvstory.Conditions.InputCondition;
import org.codeidiot.cityadvstory.Conditions.TaskCondition;
import org.codeidiot.cityadvstory.ui.JaxbPersister.Context;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;




public class CityAdvManager {
	CityAdvStory cityAdvStory;
	ArrayList<TaskTriggerPoint> taskTriggerPointList;
	public CityAdvManager(){
		cityAdvStory = new CityAdvStory();
		taskTriggerPointList = new ArrayList<TaskTriggerPoint>();
	}
	
	public List<Task> getTaskList(){
		return cityAdvStory.getTasks().getTask();
	}
	public List<NPC> getNPCList(){
		return cityAdvStory.getNPCs().getNPC();
	}
	
	public Task getTaskCopy(int index){
		if (cityAdvStory.getTasks() == null){
			return null;
		}
		return copyTask(cityAdvStory.getTasks().getTask().get(index));
	}
	
	/**
	 * Get a copy of the given Task
	 * @param task
	 * @return
	 */
	public Task copyTask(Task task){
		Task newTask = new Task();
		newTask.setStart(copyTaskTriggerPoint(task.getStart()));
		newTask.setEnd(copyTaskTriggerPoint(task.getEnd()));
		newTask.setId(task.getId());
		newTask.setTitle(task.getTitle());
		return newTask;
	}
	
	public NPC copyNPC(NPC npc){
		NPC newNpc = new NPC();
		newNpc.setEventPoint(npc.getEventPoint());
		newNpc.setName(npc.getName());
		for (int a = 0; a < npc.getDialog().size(); ++a){
			newNpc.getDialog().add(npc.getDialog().get(a));
		}
		return newNpc;
	}
	
	/**
	 * Get a copy of given TaskTriggerPoint
	 * @param triggerPoint
	 * @return
	 */
	public TaskTriggerPoint copyTaskTriggerPoint(TaskTriggerPoint triggerPoint){
		TaskTriggerPoint newTriggerPoint = new TaskTriggerPoint();
		newTriggerPoint.setConditions(copyConditions(triggerPoint.getConditions()));
		newTriggerPoint.setEventPoint(triggerPoint.getEventPoint());
		newTriggerPoint.setNegativeDialog(triggerPoint.getNegativeDialog());
		newTriggerPoint.setPositiveDialog(triggerPoint.getPositiveDialog());
		return newTriggerPoint;
	}
	
	/**
	 * Get a copy of given Conditions
	 * @param conditions
	 * @return
	 */
	public static Conditions copyConditions(Conditions conditions){
		if (conditions == null) return new Conditions();
		Conditions newConditions = new Conditions();
		List<Object> conditionList = conditions.getTaskConditionOrEventPointConditionOrInputCondition();
		int condiCount = conditionList.size();
		for (Object object: conditionList){
			if (object instanceof EventPointCondition){
				EventPointCondition condition = new EventPointCondition();
				condition.setName(((EventPointCondition)object).getName());
				newConditions.getTaskConditionOrEventPointConditionOrInputCondition().add(condition);
			}
			else if (object instanceof TaskCondition){
				TaskCondition condition = new TaskCondition();
				condition.setTaskId(((TaskCondition)object).getTaskId());
				condition.setType(((TaskCondition)object).getType());
				newConditions.getTaskConditionOrEventPointConditionOrInputCondition().add(condition);
			}
			else if (object instanceof InputCondition){
				InputCondition condition = new InputCondition();
				condition.setAnswer(((InputCondition)object).getAnswer());
				condition.setMessage(((InputCondition)object).getMessage());
				newConditions.getTaskConditionOrEventPointConditionOrInputCondition().add(condition);
			}
		}
		System.out.println("Condition copied = " + condiCount);
		return newConditions;
	}
	
	public String getTaskInfo(int index){
		//TODO:
		return null;
	}
	
	public String getNPCInfor(int index){
		//TODO:
		return null;
	}
	
	public String addTask(int index, Task task){
		//TODO:
		if (cityAdvStory.getTasks() == null){
			cityAdvStory.setTasks(new Tasks());
		}
		Task newTask = new Task();
		newTask.setStart(copyTaskTriggerPoint(task.getStart()));
		newTask.setEnd(copyTaskTriggerPoint(task.getEnd()));
		newTask.setTitle(task.getTitle());
		newTask.setId(task.getId());
		if (index == -1){
			cityAdvStory.getTasks().getTask().add(newTask);
		}
		else {
			cityAdvStory.getTasks().getTask().add(index, newTask);
		}
		return null;
	}
	
	public String addNPC(int index, NPC npc){
		
		if (cityAdvStory.getNPCs() == null){
			cityAdvStory.setNPCs(new NPCs());
		}
		if (index == -1){
			cityAdvStory.getNPCs().getNPC().add(npc);
			
		}
		else {
			cityAdvStory.getNPCs().getNPC().add(index, npc);
		}
	
		return null;
	}
	
	public String deleteTask(int index){
		//TODO:
		if (cityAdvStory.getTasks() == null){
			cityAdvStory.setTasks(new Tasks());
			return "Nothing to delete";
		}
		if (index < 0 || index >= cityAdvStory.getTasks().getTask().size()){
			return "Invalid selection...";
		}
		cityAdvStory.getTasks().getTask().remove(index);
		return null;
	}
	public String modifyTask(int index, Task task){
		if (cityAdvStory.getTasks() == null){
			cityAdvStory.setTasks(new Tasks());
			return "Nothing to modify";
		}
		if (index < 0 || index >= cityAdvStory.getTasks().getTask().size()){
			return "Invalid selection...";
		}
		cityAdvStory.getTasks().getTask().set(index, task);
		return null;
	}
	public String modifyNpc(int index, NPC npc){
		if (cityAdvStory.getTasks() == null){
			cityAdvStory.setTasks(new Tasks());
			return "Nothing to modify";
		}
		if (index < 0 || index >= cityAdvStory.getNPCs().getNPC().size()){
			return "Invalid selection...";
		}
		cityAdvStory.getNPCs().getNPC().set(index, npc);
		return null;
	}
	
	public String deteleNPC(int index){
		if (cityAdvStory.getTasks() == null){
			cityAdvStory.setTasks(new Tasks());
			return "Nothing to modify";
		}
		if (index < 0 || index >= cityAdvStory.getNPCs().getNPC().size()){
			return "Invalid selection...";
		}
		cityAdvStory.getNPCs().getNPC().remove(index);
		return null;
	}
	public String[] getTaskNameList(){
		//TODO
		if (cityAdvStory.getTasks() == null){
			return new String[0];
		}
		List<Task> tasks = cityAdvStory.getTasks().getTask();
	
		String[]list  = new String[tasks.size()] ;
		if (tasks == null) return list;
		for (int a = 0; a < tasks.size(); ++a){
			list[a] = tasks.get(a).getId() + "." + tasks.get(a).getTitle();
		}
		return list;
	}
	public String[] getNPCNameList(){
		if (cityAdvStory.getNPCs() == null){
			return new String[0];
		}
		String[] strings = new String[cityAdvStory.getNPCs().getNPC().size()];
		for (int a = 0; a < strings.length; ++a){
			strings[a]  = (a+1) + ": " + cityAdvStory.getNPCs().getNPC().get(a).getName();
		}
		return strings;
	}
	public NPC getNPCCopy(int index){
		return copyNPC(cityAdvStory.getNPCs().getNPC().get(index));
	}
	public String getInfoTask(Task task){
		String ret = "[TASK." + task.getId() + "] " + task.getTitle() + "\n";
		ret += "[Start Point]\n";
		ret += getInfoTriggerPoint(task.getStart(), 1);
		ret += "[End Point]\n";
		ret += getInfoTriggerPoint(task.getEnd(), 1);
		return ret;
	}
	public String getInfoNPCs(NPC npc){
		String retString = "[NPC] " + npc.getName() + " Event: " +npc.getEventPoint() + "\n";
		for (int a = 0; a < npc.getDialog().size(); ++a){
			Dialog dialog = npc.getDialog().get(a);
			retString += "\tDialog" + a + ": " + dialog.getContent() + "\n";
			retString += new ConditionParser().parseStrConditionList(dialog.getConditions().getTaskConditionOrEventPointConditionOrInputCondition(), 2);
		}
		return retString;
	}
	public String getInfoTriggerPoint(TaskTriggerPoint point, int level){
		if (point == null){
			String ret = "";
			for (int v = 0; v < level; ++v) ret+="\t";
			return ret +  "Missing: Task Trigger point";
		}
		String ret = "";
		for (int v = 0; v < level; ++v) ret+="\t";
		ret = "P[" + point.getEventPoint() + "]\n";
		for (int v = 0; v < level; ++v) ret+="\t";
		ret += "msg(+): " + point.getPositiveDialog() + "\n";
		for (int v = 0; v < level; ++v) ret+="\t";
		ret += "msg(-): " + point.getNegativeDialog() + "\n";
		for (int v = 0; v < level; ++v) ret+="\t";
		ret += "Conditions: ";
		if (point.getConditions() == null || point.getConditions().getTaskConditionOrEventPointConditionOrInputCondition().size() == 0){
			ret += "empty\n";
		}
		else {
			ret += "\n";
			ret +=  new ConditionParser().parseStrConditionList(point.getConditions().getTaskConditionOrEventPointConditionOrInputCondition(), 2);
		}
		return ret;
	}
	public static String getConditionArgument(Conditions conditions, int level){
		if (conditions == null) return "# empty";
		String ret = "";
		for (int a = 0; a < conditions.getTaskConditionOrEventPointConditionOrInputCondition().size(); ++a){
			Object object = conditions.getTaskConditionOrEventPointConditionOrInputCondition().get(a);
			if (object instanceof EventPointCondition){
				EventPointCondition condition = (EventPointCondition)object;
				for (int v = 0; v < level; ++v) ret+="\t";
				ret += "EventPoint: " + condition.getName();
			}
			else if (object instanceof TaskCondition){
				TaskCondition taskCondition = (TaskCondition)object;
				for (int v = 0; v < level; ++v) ret+="\t";
				ret += "TaskCondition: " + taskCondition.getTaskId() + ": " + taskCondition.getType().name();
			}
			else if (object instanceof InputCondition){
				InputCondition taskCondition = (InputCondition)object;
				for (int v = 0; v < level; ++v) ret+="\t";
				ret += "InputCondition: Ans=" + taskCondition.getAnswer() + ": MSG:" + taskCondition.getMessage();
			}
			else {
				continue;
			}
			ret += "\n";
		}
		return ret;
	}
	public static Conditions compileCondition(String s, ArrayList<String> errorList, Conditions conditions){
		conditions.getTaskConditionOrEventPointConditionOrInputCondition().clear();
		Scanner scanner = new Scanner(s);
		int lineCount = 0;
		while(scanner.hasNext()){
			lineCount++;
			String lineString = scanner.nextLine();
			if (lineString.length() == 0) continue;
			
			String[] arguments = lineString.split(": ");
			for (int a =0 ; a < arguments.length; ++a){
				int loc = 0;
				for (; loc < arguments[a].length(); ++loc){
					if (arguments[a].charAt(loc) != ' ') break;
				}
				int end = arguments[a].length()-1;
				for (; end >= 0; --end){
					if (arguments[a].charAt(loc) != ' ') break;
				}
				arguments[a] = arguments[a].substring(loc, end+1);
			}
			if (arguments[0].charAt(0) == '#') continue;
			if (arguments[0].equalsIgnoreCase("EventPoint")){
				if (arguments.length < 2){
					errorList.add("Error001: argument number invalid. Line." + lineCount);
					return null;
				}
				if (arguments[1].length() > 0){
					EventPointCondition condition = new EventPointCondition();
					condition.setName(arguments[1]);
					conditions.getTaskConditionOrEventPointConditionOrInputCondition().add(condition);
				}
				else {
					errorList.add("Error002: argument 2 length is 0. Line." + lineCount);
					return null;
				}
			}
			else if (arguments[0].equalsIgnoreCase("TaskCondition")){
				if (arguments.length < 3){
					errorList.add("Error001: argument number invalid. Line." + lineCount);
					return null;
				}
				if (arguments[1].length() > 0 && arguments[2].length() > 0){
					TaskCondition condition = new TaskCondition();
					condition.setTaskId(arguments[1]);
					if (arguments[2].equalsIgnoreCase("non") || arguments[2].equalsIgnoreCase("NOT_TAKEN"))
						condition.setType(TaskConditionType.NOT_TAKEN);
					else if (arguments[2].equalsIgnoreCase("start") || arguments[2].equalsIgnoreCase("doing") || arguments[2].equalsIgnoreCase("DOING"))
						condition.setType(TaskConditionType.DOING);
					else if (arguments[2].equalsIgnoreCase("end") || arguments[2].equalsIgnoreCase("finished") || arguments[2].equalsIgnoreCase("COMPLETED"))
						condition.setType(TaskConditionType.COMPLETED);
					else {
						errorList.add("Error003: Task type invalid. Line." + lineCount);
						return null;
					}
					conditions.getTaskConditionOrEventPointConditionOrInputCondition().add(condition);
				}
				else {
					errorList.add("Error002: argument length invalid. Line." + lineCount);
					return null;
				}
			}
		}
		return conditions;
	}
	public int saveAs(String fn){
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fn);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
		ObjectOutputStream objectOutputStream = null;
		try {
			objectOutputStream = new ObjectOutputStream(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
			return -2;
		}
		try {
			objectOutputStream.writeObject(cityAdvStory);
			objectOutputStream.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return -3;
		}
		
		return 0;
	}
	public int loadTo(String fn){
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(fn);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
		ObjectInputStream objectInputStream;
		try {
			objectInputStream = new ObjectInputStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			return -2;
		}
		try {
			cityAdvStory = (CityAdvStory)objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return -3;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return -4;
		}
		return 0;
	}
	public void saveTo(){
		
	}
	public void clear(){
		cityAdvStory = new CityAdvStory();
	}
	public static Dialog copyDialog(Dialog dialog){
		Dialog newDialog = new Dialog();
		newDialog.setConditions(copyConditions(dialog.getConditions()));
		newDialog.setContent(dialog.getContent());
		newDialog.setType(dialog.getType());
		return newDialog;
	}
	public void getXMLtoFile(String fn) throws FileNotFoundException{
		try {
			JaxbPersister.getInstance().save(cityAdvStory, fn);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String[] checkCoherence(){
		int count = 1;
		if (cityAdvStory.getNPCs() == null) {
			String[] kStrings = new String[1];
			kStrings[0] = "no NPC is defind";
			return kStrings;
		}
		if (cityAdvStory.getTasks()== null) {
			String[] kStrings = new String[1];
			kStrings[0] = "no Task is defind";
			return kStrings;
		}
		
		ArrayList<String> errorList = new ArrayList<String>();
		/*
		 * Check triggerpoint
		 */
		HashSet<String> triggerPointTaskList = new HashSet<String>();
		HashSet<String> triggerPointNPCList = new HashSet<String>();
		
		for (NPC npc: cityAdvStory.getNPCs().getNPC()){
			triggerPointNPCList.add(npc.getEventPoint());
		}
		for (Task task: cityAdvStory.getTasks().getTask()){
			triggerPointTaskList.add(task.getStart().getEventPoint());
			triggerPointTaskList.add(task.getEnd().getEventPoint());
		}
		for (String string: triggerPointNPCList){
			if (!triggerPointTaskList.contains(string)){
				errorList.add(count + ": EventPoint [" + string + "] is not defined.");
				count++;
			}
		}
		if (errorList.size() == 0) return null;
		String retString[] = new String[errorList.size() + 1];
		for (int a=  0; a< errorList.size(); ++a){
			retString[a] = errorList.get(a);
		}
		retString[errorList.size()] = "Check failed with " + (count-1) + " errors";
		return retString;
	}
	public void openSend(String fn, String storyName, String msg){
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(CityAdvStory.class);
			Unmarshaller um = context.createUnmarshaller();
			CityAdvStory story = (CityAdvStory) um.unmarshal(new File(fn));
			ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream("temp.xmlbin"));
			objOut.writeObject(story);
			objOut.close();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		URL test;
		try {
			String urlString = "http://codeidiotca.appspot.com/upload?name=" +  URLEncoder.encode(storyName, "utf-8") + "&description=" + URLEncoder.encode(msg, "utf-8") ;
			test = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) test.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("content-type", "application/octet-stream");
			OutputStream oStrm = conn.getOutputStream();
			InputStream fileInputStream = new FileInputStream("temp.xmlbin");
			int len = fileInputStream.available();
			System.out.println("Content Len = " + len);
			byte[] content = new byte[len];
			fileInputStream.read(content);
			fileInputStream.close();
			oStrm.write(content);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			reader.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendToServer(String storyName, String msg){
		JAXBContext context;
		try {
			getXMLtoFile("temp.xml");
			context = JAXBContext.newInstance(CityAdvStory.class);
			Unmarshaller um = context.createUnmarshaller();
			CityAdvStory story = (CityAdvStory) um.unmarshal(new File("temp.xml"));
			ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream("temp.xmlbin"));
			objOut.writeObject(story);
			objOut.close();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		URL test;
		try {
			String urlString = "http://codeidiotca.appspot.com/upload?name=" +  URLEncoder.encode(storyName, "utf-8") + "&description=" + URLEncoder.encode(msg, "utf-8") ;
			test = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) test.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("content-type", "application/octet-stream");
			OutputStream oStrm = conn.getOutputStream();
			InputStream fileInputStream = new FileInputStream("temp.xmlbin");
			int len = fileInputStream.available();
			System.out.println("Content Len = " + len);
			byte[] content = new byte[len];
			fileInputStream.read(content);
			fileInputStream.close();
			oStrm.write(content);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			reader.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
