package org.codeidiot.cityadvstory.ui;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JaxbPersister {
	// class variables:
	private static JaxbPersister instance = new JaxbPersister();

	// instance variables:
	private Map<Class, Context> contexts = new HashMap<Class, Context>();

	private JaxbPersister() {
	}

	public static JaxbPersister getInstance() {
		return instance;
	}

	/**
	 * Load a object of type 'clazz' from a file.
	 */
	public <T> T load(Class<T> clazz, File file) throws FileNotFoundException, JAXBException {
		return load(clazz, new FileInputStream(file));
	}

	/**
	 * Load a object of type 'clazz' from a file.
	 */
	public <T> T load(Class<T> clazz, InputStream is) throws JAXBException {
		Context context;
		T object;

		context = getContext(clazz);
		synchronized(context) {
			object = (T) context.unmarshal(is);
			return object;
		}
	}

	/**
	 * Save an object to a file.
	 */
	public void save(Object object, String fn) throws JAXBException, IOException {
		OutputStream os;
        File file = new File(fn);
        if (fn == null || fn.length() == 0) return;
		os = new FileOutputStream(file);
		save(object, os);
		os.close();
	}

	/**
	 * Save a object to a outputstream.
	 */
	private void save(Object object, OutputStream os) throws JAXBException, IOException {
		Writer writer;
		Context context;
		writer = new OutputStreamWriter(os, "utf-8");
		context = getContext(object.getClass());
		synchronized(context) {
			context.marshal(object, writer);
		}
	}

	/**
	 * Each class has it's own context to marshal and unmarshal. The context
	 * contains a jaxbcontext.
	 */
	private Context getContext(Class clazz) {
		Context c;

		synchronized(contexts) {
			c = contexts.get(clazz);
			if(c == null) {
				c = new Context(clazz);
				contexts.put(clazz, c);
			}
		}

		return c;
	}

	class Context {
		private JAXBContext jaxbContext;
		private Marshaller marshaller;
		private Unmarshaller unmarshaller;

		Context(Class clazz) {
			try {
				jaxbContext = JAXBContext.newInstance(clazz);

				marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				marshaller.setSchema(null);
				unmarshaller = jaxbContext.createUnmarshaller();
				unmarshaller.setSchema(null);
			} catch(JAXBException e) {
				e.printStackTrace();
			}
		}

		public void marshal(Object object, Writer writer) throws JAXBException {
			marshaller.marshal(object, writer);
		}

		public Object unmarshal(InputStream is) throws JAXBException {
			return unmarshaller.unmarshal(is);
		}
	}
}