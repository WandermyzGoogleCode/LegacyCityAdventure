package cityadvgaeserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Upload extends HttpServlet {
	String name;
	String description;
	byte[] content;
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
				
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		
		name = req.getParameter("name");
		description = req.getParameter("description");
		if(req.getContentType().equals("application/octet-stream")) {
			
			InputStream inputStream = req.getInputStream();
			byte[] content = new byte[req.getContentLength()];
			inputStream.read(content);
			ByteStore byteStore = new ByteStore(content);
			Record record = new Record( name, description, byteStore );
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				pm.makePersistent( record );
				res.setContentType("text/plain");
				res.getWriter().println(record.getKey().getId());
			} catch ( Exception e)
			{
				e.printStackTrace();
			}
			finally {
				pm.close();
			}
		}else
		{
			res.setContentType("text/plain");
			res.getWriter().println("-1 ContentType Error!");
		}
	}
}
