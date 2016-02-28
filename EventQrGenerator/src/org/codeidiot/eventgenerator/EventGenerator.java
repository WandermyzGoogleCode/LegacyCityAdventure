package org.codeidiot.eventgenerator;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.ByteMatrix;

public class EventGenerator {
	
	private static final String FILE_EXT_EVENT_HINT = "evt";
	private static final String EVENT_DIR_NAME = "qr_events";
	private static final int QR_SIZE = 350;
	private static final String ASCII_NAME = "ISO-8859-1";
	
	public static void main(String[] args) {
		if(args.length != 1) {
			printUsage();
			return;
		}
		
		String filePath = args[0];
		String hintFilePath = filePath + "." + FILE_EXT_EVENT_HINT;
		
		File mapFile = new File(filePath);
		File qrDir = new File(mapFile.getParent(), EVENT_DIR_NAME);
		if(! qrDir.exists()) {
			qrDir.mkdir();
		}
		
		RandomAccessFile randomAccess;
		BufferedReader hintReader;
		try {
			randomAccess = new RandomAccessFile(filePath, "rw");
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + filePath);
			return;
		}
		try {
			hintReader = new BufferedReader(new InputStreamReader(new FileInputStream(hintFilePath)));
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + hintFilePath);
			return;
		}
		
		try {
			// check header;
			String headerExpectedStr = hintReader.readLine();
			byte[] headerExpected = headerExpectedStr.getBytes(ASCII_NAME);
			byte[] header = new byte[headerExpected.length];
			randomAccess.seek(0);
			randomAccess.read(header);

			String headerStr;
			headerStr = new String(header, ASCII_NAME);
			if(!(headerExpectedStr.equals(headerStr))) {
				System.out.println("Unexpected file header");
				return;
			}
			
			//read event size
			String sizeStr = hintReader.readLine();
			int size = Integer.parseInt(sizeStr);
			
			// read each event
			String line;
			while((line = hintReader.readLine()) != null) {
				Scanner scanner = new Scanner(line);
				int id = scanner.nextInt();
				long offset = scanner.nextLong();
				String name = scanner.nextLine();
				String checksum = generateChecksum(randomAccess, offset, size);
				generateQrCode(id, checksum, name, qrDir);
			}
			
			randomAccess.close();
			hintReader.close();
			
		} catch (Exception e) {
			try {
				randomAccess.close();
			} catch (IOException e1) {
			}
			e.printStackTrace();
		}
		
	}
	
	private static String generateChecksum(RandomAccessFile randomAccess, long offset, int size) throws IOException {
		//TODO: map id, salt?
		randomAccess.seek(offset);
		byte[] eventContent = new byte[size];
		randomAccess.read(eventContent);
		
		
		
	    MessageDigest md;
	    try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	    md.update(eventContent);
	    byte[] sha1hash = md.digest();
	    
	    StringBuilder sb = new StringBuilder();
	    for(int i = 0; i < sha1hash.length; i++) {
	    	sb.append(String.format("%02x", sha1hash[i]));
	    }
	    
	    return sb.toString();
	}
	
	private static void generateQrCode(int id, String checkSum, String name, File qrDir) throws IOException {
		//TODO: map id

		String content = String.valueOf(id) + " " + checkSum;

		// get a byte matrix for the data
		BitMatrix matrix;
		com.google.zxing.Writer writer = new QRCodeWriter();
		try {
			matrix = writer.encode(content, com.google.zxing.BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
		} catch (com.google.zxing.WriterException e) {
			// exit the method
			return;
		}

		// generate an image from the byte matrix
		int width = matrix.getWidth();
		int height = matrix.getHeight();

		// create buffered image to draw to
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// iterate through the matrix and draw the pixels to the image
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int grayValue = matrix.get(x, y) ? 0 : 0xFF;
				image.setRGB(x, y, (grayValue == 0 ? 0 : 0xFFFFFF));
			}
		}

		//write the image to the output stream
		FileOutputStream outputStream = new FileOutputStream(new File(qrDir, name + ".png"));
		ImageIO.write(image, "png", outputStream);
		outputStream.close();
	}
	
	private static void printUsage() {
		System.out.println("Params: mapFilePath");
	}

}
