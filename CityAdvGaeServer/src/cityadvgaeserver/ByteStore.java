package cityadvgaeserver;
import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

public class ByteStore implements Serializable{
	 private byte[] content;
	 public ByteStore(byte[] content) {
	        this.content = content;
	 }
	 public byte[] getContent() {
		 return content;
	 }
	 public void setContent(byte[] content) {
	        this.content = content;
	 }
}
