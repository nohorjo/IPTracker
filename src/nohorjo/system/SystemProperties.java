package nohorjo.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SystemProperties {

	private static final Properties props = new Properties();

	static {
		try (InputStream is = SystemProperties.class.getClassLoader().getResourceAsStream("system.properties")) {
			props.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String get(String property, String defaultVal) {
		return props.getProperty(property, defaultVal);
	}

	public static String get(String property) {
		return get(property, null);
	}
}
