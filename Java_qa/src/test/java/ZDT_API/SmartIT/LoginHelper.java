package ZDT_API.SmartIT;

import static org.testng.Assert.assertEquals;

import java.net.*;
import java.util.*;
import java.util.logging.*;

import org.json.JSONException;
import org.json.JSONObject;

import framework.Launcher;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class LoginHelper extends Launcher{

    private static final Logger logger = Logger.getLogger(LoginHelper.class.getName());
    private Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private String csrfToken;
    private String overrideUser = null;
    private String overridePassword = null;

    public LoginHelper() {
        config = configProp;
        this.updateUrl();
        this.getTenant();
    }

    public LoginHelper(String username) {
        config = configProp;
        this.overrideUser = username;
        this.updateUrl();
        this.getTenant();
    }


    public LoginHelper(String username, String password) {
        config = configProp;
        this.overrideUser = username;
        this.overridePassword = password;
        this.updateUrl();
        this.getTenant();
        
    }

    private void updateUrl()
    {
        config.put("RSSO_URL", config.get("RSSO_URL").replace("/rsso", ""));
        config.put("SmartIT_URL", config.get("SmartIT_URL").replace("/smartit/app/#/", ""));
    }

    private void getTenant()
    {
        try {
            URL urlObj = new URL(config.get("SmartIT_URL"));
            String domain = urlObj.getHost();
            String prefix = domain.substring(0, domain.indexOf("-"));
            String tenant = domain + "@" + prefix;
            config.put("tenant", tenant);
        } catch (MalformedURLException e) {
            // Fallback to string manipulation
            e.getStackTrace();
        }
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setUserDetails(String userName, String password)
    {
        config.put("API_USERNAME", userName);
        config.put("API_PASSWORD", password);
    }

    public String getUserName()
    {
        return config.get("API_USERNAME");
    }

    public boolean performLogin()
    {
        try{
            cookies.clear();
            String userToUse = overrideUser != null ? overrideUser : config.get("API_USERNAME");
            String passwordToUse = overridePassword != null ? overridePassword : config.get("API_PASSWORD");

            // Step 2: RSSO Login
            String loginUrl = config.get("RSSO_URL") + "/rsso/start";
            headers.put("Referer", loginUrl);
            headers.put("Script-Header", "javascript-request");
            headers.put("Origin", config.get("RSSO_url"));
            headers.put("Accept", "*/*");
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            String loginPayload = "msp_defined_realm=&captcha=&reauth=false&authorizationRequest=&allow-from-domain=&authorize-referer=&rsso-agent-referer=&bypass-referer=&cross-sso-orig-domains=&rsso_preauth=" +
                    "&tenant=" + URLEncoder.encode(config.get("tenant"), "UTF-8") +
                    "&goto=" + URLEncoder.encode(config.get("SmartIT_URL"), "UTF-8") +
                    "&bypass-auth=&url_hash_handler=true" +
                    "&password=" + URLEncoder.encode(passwordToUse, "UTF-8") +
                    "&user-name=" + URLEncoder.encode(userToUse, "UTF-8") +
                    "&redirect=0";
            Response loginResponse = RestAssured.given().baseUri(loginUrl).headers(headers).contentType(ContentType.URLENC).cookies(cookies).body(loginPayload).when().post().then().extract().response();
            System.out.println(loginResponse.getBody().asString());
            assertEquals(loginResponse.getStatusCode(), 200, "The RSSO Login process is not successful for user: " + userToUse);
            logger.info("RSSO Login Validation Successful for user: " + userToUse);
            updateCookies(loginResponse);

            // Step 3: GET SmartIT Rest Server States
            String serverStatesUrl = config.get("SmartIT_URL") + "/smartit/rest/serverstates";
            headers.put("Referer", config.get("SmartIT_URL") + "/smartit/app/");
            headers.put("Origin", config.get("SmartIT_URL"));
            headers.put("Accept", "application/json, text/plain, */*");
            headers.put("Content-Type", "application/json;charset=UTF-8");
            Response serverStatesResp = RestAssured.given().baseUri(serverStatesUrl).headers(headers).cookies(cookies).contentType(ContentType.JSON).when().get().then().extract().response();
            assertEquals(serverStatesResp.getStatusCode(), 200, "The Smart It Server State Operation is not successful for user: " + userToUse);
            logger.info("Smart IT Rest Server response validation successful for user: " + userToUse);
            updateCookies(serverStatesResp);

            // Step 4: Create User session
            String userSessionUrl =  config.get("SmartIT_URL") + "/smartit/rest/users/sessions";
            logger.info(userSessionUrl);
            JSONObject userSessionPayload = new JSONObject();
            userSessionPayload.put("appName", "Galileo");
            userSessionPayload.put("appVersion", "9.1.06.000");
            userSessionPayload.put("apiVersion", 9010600);
            userSessionPayload.put("locale", "en");
            userSessionPayload.put("deviceToken", "dummyToken");
            userSessionPayload.put("os", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
            userSessionPayload.put("model", "Web Client");
            Response userSessionResp = RestAssured.given().baseUri(userSessionUrl).headers(headers).cookies(cookies).body(userSessionPayload.toString()).when().post().then().extract().response();
            assertEquals(userSessionResp.getStatusCode(), 200, "The User Session CReation process is not successful for user: " + userToUse);
            logger.info("User Session creation Validation successful for user: " + userToUse);
            updateCookies(userSessionResp);
            try {
                JSONObject userSessionJson = new JSONObject(userSessionResp.getBody().asString());
                csrfToken = userSessionJson.optString("csrf", null);
                logger.info("Extracted CSRF token: " + csrfToken + " for user: " + userToUse);
            } catch (JSONException e) {
                logger.severe("Failed to extract CSRF token: " + e.getMessage() + " for user: " + userToUse);
                return false;
            }
            return true;
        }
        catch (Exception e) {
            logger.severe("Exception in CreateIncident: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void updateCookies(Response response) {
        List<String> setCookieHeaders = response.getHeaders().getValues("Set-Cookie");
        for (String header : setCookieHeaders) {
            // Split only on the first ";" to separate cookie from attributes
            String[] parts = header.split(";", 2);  // cookie=value; Path=/; HttpOnly...

            if (parts.length > 0) {
                String[] keyValue = parts[0].split("=", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    cookies.put(key, value);
                }
            }
        }
        cookies.putAll(response.cookies());
    }
}
