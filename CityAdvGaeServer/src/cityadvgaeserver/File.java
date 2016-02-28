package cityadvgaeserver;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

import java.util.Date;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class File {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private byte[] file;
    public File(byte[] file) {
    	this.file = file;
    }
	public Key getKey() {
        return key;
    }
    public byte[] getFile() {
    	return file;
    }
    public void setFile( byte[] file ) {
    	this.file = file;
    }
}
