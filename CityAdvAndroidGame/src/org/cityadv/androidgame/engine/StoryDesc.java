package org.cityadv.androidgame.engine;

import org.codeidiot.cityadvstory.CityAdvStory;

public class StoryDesc {
	String id;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	String name;
	String description;
	CityAdvStory storyData;
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public CityAdvStory getStoryData() {
		return storyData;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStoryData(CityAdvStory storyData) {
		this.storyData = storyData;
	}
	
	
}
