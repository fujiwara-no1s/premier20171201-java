package premier20170701;

import java.util.List;

public class Main {
  
  public static void main(String[] args) {
    String json = getTimeline();
    List<Tweet> tweets = parse(json);
    displayTweet(tweets);
  }
  
  private static String getTimeline() {
    return new TwitterRequest().execute();
  }
  
  private static List<Tweet> parse(String json) {
    return new JsonParser(json).getTweets();
  }
  
  private static void displayTweet(List<Tweet> tweets) {
    for(Tweet tweet : tweets) {
      System.out.println("<<< " + TwitterDateUtil.parseDate(tweet.getCreatedAt()) + " >>>");
      System.out.println(tweet.getFullText());
      System.out.println("----------------------------");
    }
  }
}
