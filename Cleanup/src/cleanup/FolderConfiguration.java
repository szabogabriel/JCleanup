package cleanup;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import cleanup.impl.AmountCleanup;
import cleanup.impl.Cleanup;
import cleanup.impl.DateCleanup;
import cleanup.impl.DummyCleanup;

public class FolderConfiguration {
	
	public static final String KEY_TYPE = "type";
	
	private final Properties props = new Properties();
	
	public FolderConfiguration(File file) {
		try (InputStream in = new FileInputStream(file)) {
			props.load(in);
		} catch (Exception e) {
			e.printStackTrace();
			throw (new IllegalArgumentException(e));
		}
	}
	
	public Cleanup createCleanup() {
		Cleanup ret = new DummyCleanup();
		
		String type = props.getProperty("type", "");
		
		if (type.startsWith("date")) {
			ret = new DateCleanup(type);
		} else
		if (type.startsWith("amount")){
			ret = new AmountCleanup(type);
		}
		
		return ret;
	}

	public String getParameter(String key) {
		return props.getProperty(key);
	}
	
	public String getParameter(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}

}
