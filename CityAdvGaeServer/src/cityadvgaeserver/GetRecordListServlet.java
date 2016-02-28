package cityadvgaeserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.identity.ObjectIdentity;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class GetRecordListServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		StringBuffer sb = new StringBuffer();
		try {
			String query = "select from " + Record.class.getName();
			List<Record> queryResult = (List<Record>) pm.newQuery(query).execute();
			if ( ! queryResult.isEmpty() )
			{
				for ( Record record : queryResult )
				{
					ObjectIdentity id = ( ObjectIdentity )pm.getObjectId( record );
					String name = encodeValue( record.getName() );
					String description = encodeValue( record.getDescription() );
					sb.append( KeyFactory.keyToString( ( Key )id.getKey() ) + " " + name + " " + description + '\n');
				}
			}
			resp.setContentType("text/plain");
			resp.getWriter().print( sb.toString() );
		} finally {
			pm.close();
		}
		//resp.getWriter().println("Hello, " );
	}
	
	private String  encodeValue(String value)
	{
		try {
			value = URLEncoder.encode(value, "utf-8");
		} catch (UnsupportedEncodingException e) {
			try {
				value = URLEncoder.encode(value, "iso-88591");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
		return value;
	}
}
