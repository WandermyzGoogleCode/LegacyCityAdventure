package cityadvgaeserver;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.identity.ObjectIdentity;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class GetRecordContentServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Key key = ( Key )KeyFactory.stringToKey( req.getParameter( "id" ) ) ;
			ObjectIdentity id = new ObjectIdentity( Record.class ,key);
			Record record = null;
			if( pm.getObjectById( id ) instanceof Record )
			{
				record = ( Record ) pm.getObjectById( id );
				byte[] content = record.getContent().getContent();
				resp.setContentType("application/octet-stream");
				resp.getOutputStream().write( content );
			}
		} finally {
			pm.close();
		}
		//resp.getWriter().println("Hello, " );
	}
}
