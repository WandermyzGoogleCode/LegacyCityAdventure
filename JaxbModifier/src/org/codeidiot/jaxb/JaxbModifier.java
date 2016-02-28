package org.codeidiot.jaxb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JaxbModifier {
	
	public static final Pattern classPattern = Pattern.compile("(((public|private|protected) )?(static )?class (\\S+)) \\{");
	//public static final Pattern nestedAnnotationPattern = Pattern.compile("@\\S+\\s*\\(.*?(@\\S+\\s*\\(.*?\\).*?)+(,\\s*@\\S+\\s*\\(.*?\\).*?)*.*?\\)", Pattern.DOTALL);
	public static final Pattern annotationNoParamPattern = Pattern.compile("^\\s*@\\S+\\s*$", Pattern.MULTILINE);
	public static final Pattern annotationPattern = Pattern.compile("@\\S+\\s*(\\(.*?\\))", Pattern.DOTALL);
	public static final Pattern importPattern = Pattern.compile("import javax\\.xml\\.bind\\S*;");
	public static final Pattern nestedAnnotationCleanPattern = Pattern.compile(",\\s*,\\s*(,\\s)*\\}\\)", Pattern.DOTALL);
	public static final Pattern nestedAnnotationClean2Pattern = Pattern.compile("^\\s*,\\s*$", Pattern.MULTILINE);
	
	public static boolean addSerializable, delAnnotation;
	
	public static void main(String[] args) {
		if(args.length < 1) {
			printUsage();
			return;
		}
		
		String dirPath = args[0];
		File dir = new File(dirPath);
		
		for(int i = 1; i < args.length; i++) {
			if(args[i].equals("-s")) {
				addSerializable = true;
			} else if(args[i].equals("-a")) {
				delAnnotation = true;
			} else {
				printUsage();
				return;
			}
		}
		
		process(dir);
	}
	
	private static void process(File file) {
		if(file.isDirectory()) {
			for(File child : file.listFiles(fileFilter)) {
				process(child);
			}
		} else {
			
			System.out.println(file.getName());
			
			BufferedReader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			
			StringBuilder sb = new StringBuilder();
			String line;
			
			try {
				while((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			String content = sb.toString();
			Matcher m;
			
			if(addSerializable) {
				m = classPattern.matcher(content);
				content = m.replaceAll("$1 implements java.io.Serializable {\n" +
						"private static final long serialVersionUID = 1L;\n");
			}
			
			if(delAnnotation) {
				m = importPattern.matcher(content);
				content = m.replaceAll("");
				
				m = annotationNoParamPattern.matcher(content);
				content = m.replaceAll("");
				
				m = annotationPattern.matcher(content);
				content = m.replaceAll("");
				
				m = nestedAnnotationCleanPattern.matcher(content);
				content = m.replaceAll("");
				
				m = nestedAnnotationClean2Pattern.matcher(content);
				content = m.replaceAll("");
			}
			
			try {
				FileWriter writer = new FileWriter(file);
				writer.write(content);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	private static void printUsage() {
		System.out.println("sourceFolderPath [-s] [-a]\n" +
				"-s: Add Serializable\n" +
				"-a: Delete Annotations");
	}
	
	private static FileFilter fileFilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			if(pathname.isDirectory() || pathname.getName().endsWith(".java")) {
				return true;
			} else {
				return false;
			}
		}
	};
}
