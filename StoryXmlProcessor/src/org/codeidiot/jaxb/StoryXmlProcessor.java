package org.codeidiot.jaxb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.codeidiot.cityadvstory.CityAdvStory;

public class StoryXmlProcessor {
	
	public static void main(String[] args) {
		if(args.length != 2) {
			printUsage();
			return;
		}
		
		String filePath = args[0];
		String outPath = args[1];
		
		try {
			JAXBContext context = JAXBContext.newInstance(CityAdvStory.class);
			Unmarshaller um = context.createUnmarshaller();
			
			CityAdvStory story = (CityAdvStory) um.unmarshal(new File(filePath));
			
			//System.out.println(story.getTasks().getTask().get(0).getTitle());
			
			ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(outPath));
			objOut.writeObject(story);
			objOut.close();
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void printUsage() {
		System.out.println("Params: xmlPath outPath");
	}
}
