package ZDT_API.ITSM;

import static org.testng.Assert.assertEquals;

import java.util.*;
import java.util.logging.*;

import framework.Launcher;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ITSMLogin extends Launcher {

    private static final Logger logger = Logger.getLogger(ITSMLogin.class.getName());
    private Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private String loginUrl, userName, password;


    public ITSMLogin()
    {
        config = configProp;
    }

    public Map<String, String> getConfig()
    {
        return config;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public Map<String, String> getCookies()
    {
        return cookies;
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

    public Map<String, String> performLogin()
    {
        loginUrl = config.get("IS_URL") + "/api/rx/authentication/loginrequest";
        userName = config.get("API_USERNAME");
        password = config.get("API_PASSWORD");
        logger.info("Starting Test Execution for user: " + userName);
        String loginJSON = "{\"userName\":\""+userName+"\",\"password\":\""+password+"\"}";

        headers.put("Content-Type", "application/json");
        headers.put("X-Requested-By","XMLHttpRequest");

        Response loginResponse = RestAssured.given().baseUri(loginUrl).headers(headers).body(loginJSON).contentType(ContentType.JSON).when().post().then().extract().response();
        logger.info("Login response Code: " + loginResponse.getStatusCode());

        assertEquals(loginResponse.getStatusCode(), 200, "The Login is not successful for user: " + userName);

        //get cookie values
        cookies.putAll(loginResponse.getCookies());
        logger.info("Login Successful and cookies received for user: " + userName);

        return cookies;
    }
    
}
