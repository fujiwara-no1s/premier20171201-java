package premier20170701;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

// Date Utility
public class TwitterDateUtil {
  private static final String TWITTER_FORMAT ="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
  private static final String DISPLAY_FORMAT = "yyyy年MM月dd日 HH時mm分ss秒";
  
  public static String getTimestamp() {
    return String.valueOf(System.currentTimeMillis() / 1000);
  }
  
  public static String parseDate(String dateString) {
    String parsedString = "";
    SimpleDateFormat twitterFormat = new SimpleDateFormat(TWITTER_FORMAT,Locale.US);
    SimpleDateFormat displayFormat = new SimpleDateFormat(DISPLAY_FORMAT);
    displayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    try {
      parsedString = displayFormat.format(twitterFormat.parse(dateString));
    } catch (ParseException e) {
      parsedString = "error parsing date";
    }
    return parsedString;
  }
  
  public static String getNonce() {
    return String.valueOf(System.currentTimeMillis());
  }
}
