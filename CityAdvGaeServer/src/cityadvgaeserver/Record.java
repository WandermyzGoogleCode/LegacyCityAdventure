package cityadvgaeserver;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

import java.util.Date;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Record {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private String name;
	@Persistent
	private String description;
	@Persistent(serialized = "true")
	private ByteStore content;
	
    public Record( String name, String description, ByteStore content) {
    	this.name = name;
    	this.description = description;
    	this.content = content;
	}
	public Key getKey() {
	    return key;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public ByteStore getContent() {
		return content;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	public void setDescription( String description ) {
		this.description  = description;
	}
	public void setContent( ByteStore content ) {
		this.content = content;
	}
}
