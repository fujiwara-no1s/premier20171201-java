package premier20170701;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TwitterRequest {
  private static String METHOD = "GET";
  private static String ENDPOINT;
  private static String CONSUMER_KEY;
  private static String CONSUMER_KEY_SECRET;
  private static String ACCESS_TOKEN;
  private static String ACCESS_TOKEN_SECRET;

  private Map<String, String> apiParamMap = new HashMap<String, String>();
  private Map<String, String> signatureMap = new HashMap<String, String>();
  private Map<String, String> allParamMap = new TreeMap<String, String>();

  public TwitterRequest() {
    setProperty();
    initilize();
  }

  private void setProperty() {
    // Propertyファイルから設定を反映
    ENDPOINT = PropertyUtil.getProperty("ENDPOINT");
    CONSUMER_KEY = PropertyUtil.getProperty("CONSUMER_KEY");
    CONSUMER_KEY_SECRET = PropertyUtil.getProperty("CONSUMER_KEY_SECRET");
    ACCESS_TOKEN = PropertyUtil.getProperty("ACCESS_TOKEN");
    ACCESS_TOKEN_SECRET = PropertyUtil.getProperty("ACCESS_TOKEN_SECRET");
  }

  private void initilize() {
    // APIに付随するパラメータのMAP
    apiParamMap.put("screen_name", "@realDonaldTrump");
    apiParamMap.put("count", "10");
    apiParamMap.put("tweet_mode", "extended");

    // 署名作成用パラメータのMAP
    signatureMap.put("oauth_token", ACCESS_TOKEN);
    signatureMap.put("oauth_consumer_key", CONSUMER_KEY);
    signatureMap.put("oauth_signature_method", "HMAC-SHA1");
    signatureMap.put("oauth_timestamp", TwitterDateUtil.getTimestamp());
    signatureMap.put("oauth_nonce", TwitterDateUtil.getNonce());
    signatureMap.put("oauth_version", "1.0");

    // ハッシュ値作成用パラメータのMAP
    allParamMap.putAll(apiParamMap);
    allParamMap.putAll(signatureMap);
  }
  
  public String execute() {
    // OAUTH署名を作成
    String signature = createSignature();
    // ヘッダ用認証テキスト
    String authorizationHeaderText = createAuthorizationHeaderText(signature);
    // リクエストURL
    String requestURL = createRequestURL();
    // 実際のリクエスト発行
    return makeRequest(signature,authorizationHeaderText,requestURL);
  }

  private String createSignature() {
    String requestParam = createRequestParam();
    String signatureText = createSignatureText(requestParam);

    // HMAC-SHA1方式のハッシュ値に変換
    String signature = "";
    try {
      String signatureKey = CONSUMER_KEY_SECRET + "&" + ACCESS_TOKEN_SECRET;
      byte[] keyBytes = signatureKey.getBytes();
      SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");
      Mac mac = Mac.getInstance("HmacSHA1");
      mac.init(secretKey);
      byte[] text = signatureText.getBytes();
      signature = new String(Base64.getEncoder().encodeToString((mac.doFinal(text))).trim());
      signature = URLEncoder.encode(signature, "UTF-8");
    } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return signature;
  }

  private String createRequestParam() {
    String requestParam = "";
    for (Map.Entry<String, String> entry : allParamMap.entrySet()) {
      requestParam += entry.getKey() + "=" + entry.getValue() + "&";
    }

    requestParam = requestParam.substring(0, requestParam.length() - 1);
    return requestParam;
  }

  private String createSignatureText(String requestParam) {
    String signatureText = "";
    try {
      // リクエストパラメータをURLエンコードする
      requestParam = URLEncoder.encode(requestParam, "UTF-8");
      // リクエストメソッドをURLエンコードする
      String encodedMETHOD = URLEncoder.encode(METHOD);
      // リクエストURLをURLエンコードする
      String encodedEndpoint = URLEncoder.encode(ENDPOINT);
      // リクエストメソッド、リクエストURL、パラメータを[&]で繋ぐ
      signatureText = encodedMETHOD + "&" + encodedEndpoint + "&"
          + requestParam;
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return signatureText;
  }
  
  private String createAuthorizationHeaderText(String signature) {
    // パラメータに署名を追加
    apiParamMap.put("oauth_signature", signature);
    // HTTPヘッダ付与のパラメータを作成
    String authorizationHeaderText = "";
    for (Map.Entry<String, String> entry : allParamMap.entrySet()) {
      authorizationHeaderText += entry.getKey() + "=" + entry.getValue() + ",";
    }
    return authorizationHeaderText;
  }
  
  private String createRequestURL() {
    // エンドポイントに付与するGETパラメータを末尾に追加
    String requestURL = ENDPOINT + "?";
    for (Map.Entry<String, String> entry : apiParamMap.entrySet()) {
      requestURL += entry.getKey() + "=" + entry.getValue() + "&";
    }
    return requestURL.substring(0, requestURL.length() - 1);
  }
  
  private String makeRequest(String signature, String authorizationHeaderText,String requestURL) {
    BufferedReader reader = null;
    String html = "";
    try {
      URLConnection connection = new URL(requestURL).openConnection();
      connection.setDoInput(true);
      connection.setRequestProperty("Authorization", "OAuth " + authorizationHeaderText);
      reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line = "";
      StringBuilder sb = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      html = sb.toString();
    } catch (Exception ex) {
      System.out.println(ex.toString());
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ioe) {
          System.out.println(ioe.toString());
        }
      }
    }
    return html;
  }
}
