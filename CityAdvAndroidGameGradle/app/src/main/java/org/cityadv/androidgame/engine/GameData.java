package org.cityadv.androidgame.engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.HashSet;
import java.util.Set;

public class GameData implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//TODO: map id
	//TODO: story id

	/**
	 * Remind: key is name, not id!
	 */
	private Set<String> visitedEventPoints;
	private Set<String> doingTasks;
	private Set<String> completedTasks;
	
	private GameData() { }
		
	
	public static GameData create() {
		GameData result = new GameData();
		result.visitedEventPoints = new HashSet<String>();
		result.doingTasks = new HashSet<String>();
		result.completedTasks = new HashSet<String>();
		return result;
	}
	
	public static GameData load(String filePath) throws StreamCorruptedException, FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream objInput = new ObjectInputStream(new FileInputStream(filePath));
		Object obj = objInput.readObject();
		objInput.close();
		
		GameData result = (GameData) obj;
		return result;
	}
	
	public void save(String filePath) throws FileNotFoundException, IOException {
		ObjectOutputStream objOutput = new ObjectOutputStream(new FileOutputStream(filePath));
		objOutput.writeObject(this);
		objOutput.close();
	}
	
	public Set<String> getVisitedEventPoints() {
		return visitedEventPoints;
	}
	public Set<String> getDoingTasks() {
		return doingTasks;
	}
	public Set<String> getCompletedTasks() {
		return completedTasks;
	}
	
	
}
