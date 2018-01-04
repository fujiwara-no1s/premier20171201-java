package premier20170701;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertyUtil {
  private static final String INIT_FILE_PATH = "setting.properties";
  private static final Properties properties;

  static {
    properties = new Properties();
    try {
      ClassLoader classLoader = PropertyUtil.class.getClassLoader();
      File file = new File(classLoader.getResource(INIT_FILE_PATH).getFile());
      properties.load(Files.newBufferedReader(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("fail to load setting.propertiess");
    }
  }

  public static String getProperty(final String key) {
    return properties.getProperty(key, "");
  }
}
