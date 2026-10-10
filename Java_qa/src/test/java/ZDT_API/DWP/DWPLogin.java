package ZDT_API.DWP;

import static org.testng.Assert.assertEquals;

import java.util.*;
import java.util.logging.*;

import framework.Launcher;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


public class DWPLogin extends Launcher{

    private static final Logger logger = Logger.getLogger(DWPLogin.class.getName());
    private Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private String loginUrl, userName, password;

    public DWPLogin()
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

    public void updateUrl()
    {
        config.put("DWP_URL", config.get("DWPCatalog_URL").replace("/myitsbe", ""));
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
        updateUrl();
        String baseUrl = config.get("DWP_URL");
        String endpoint = "/api/rx/authentication/loginrequest";
        userName = config.get("API_USERNAME");
        password = config.get("API_PASSWORD");
        
        // Validate that base URL is not null or placeholder
        if (baseUrl == null || baseUrl.contains("__") || baseUrl.isEmpty()) {
            logger.severe("DWP_URL is not properly configured. Current value: " + baseUrl);
            throw new IllegalStateException("DWP_URL configuration is missing or invalid");
        }
        
        String loginJSON = "{\"userName\":\""+userName+"\",\"password\":\""+password+"\"}";

        headers.put("Content-Type", "application/json");
        headers.put("X-Requested-By","XMLHttpRequest");

        logger.info("Attempting login to: " + baseUrl + endpoint + " with user: " + userName);
        Response loginResponse = RestAssured.given()
            .baseUri(baseUrl)
            .headers(headers)
            .body(loginJSON)
            .contentType(ContentType.JSON)
            .when()
            .post(endpoint)
            .then()
            .extract()
            .response();
        logger.info("Login response Code: " + loginResponse.getStatusCode());

        assertEquals(200,  loginResponse.getStatusCode(), "The Login is not successful for user: " + userName);

        //get cookie values
        cookies.putAll(loginResponse.getCookies());
        logger.info("Login Successful and cookies received for user: " + userName);

        return cookies;
    }
    
}
