package premier20170701;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

// parse twitter json to List<Tweet>
public class JsonParser {
  private List<Tweet> tweets;
  
  public JsonParser(String json) {
    Gson gson = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create();
    Type listType = new TypeToken<ArrayList<Tweet>>(){}.getType();
    tweets = gson.fromJson(json, listType);
  }
  
  public List<Tweet> getTweets() {
    return this.tweets;
  }
}
