package cityadvgaeserver.tester;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class OpenURL  {
	public static void main(String[] args) {
		URL test;
		try {
			if(args.length != 3) {
				System.out.println("name desc path");
				return;
			}
			
			String name = args[0];
			String desc = args[1];
			String filePath = args[2];
			
			test = new URL("http://codeidiotca.appspot.com/upload?name=" 
					+ URLEncoder.encode(name, "utf-8") + "&description="
					+ URLEncoder.encode(desc, "utf-8"));
			//test = new URL("http://localhost:8888/upload?name=wade&desc=neil");

			HttpURLConnection conn = (HttpURLConnection) test.openConnection();

			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("content-type", "application/octet-stream");
			OutputStream oStrm = conn.getOutputStream();
			
			InputStream fileInputStream = new FileInputStream(filePath);
			ArrayList<Byte> arrayList = new ArrayList<Byte>();
			while (fileInputStream.available() > 0){
				arrayList.add(new Byte((byte) fileInputStream.read()));
			}
			System.out.println("Content Len = " + arrayList.size());
			byte[] content = new byte[arrayList.size()];
			for (int a = 0; a < arrayList.size(); ++a){
				content[a] = arrayList.get(a);
			}
			oStrm.write(content);
			//byte[] content = { 0x60, 0x61 };
			//oStrm.write(content);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			reader.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
