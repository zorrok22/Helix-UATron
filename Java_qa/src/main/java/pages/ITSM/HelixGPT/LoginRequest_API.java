package pages.ITSM.HelixGPT;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static org.testng.Assert.assertEquals;

import framework.Launcher;

public class LoginRequest_API extends Launcher{

    Map<String, String> config;
    public Map<String, String> cookies = new HashMap<>();
    private static final Logger logger = Logger.getLogger(LoginRequest_API.class.getName());
    private String loginUrl, userName, password;

    public LoginRequest_API(){
        config = configProp;
    }

    public Map<String, String> loginAction()
    {
        loginUrl = config.get("IS_URL") + "/api/rx/authentication/loginrequest";
        userName = config.get("adminUserName");
        password = config.get("adminPassword");
        String loginJSON = "{\"userName\":\""+userName+"\",\"password\":\""+password+"\"}";

          // Prepare request
        RequestSpecification loginRequest = RestAssured.given()
                .relaxedHTTPSValidation() // to skip SSL verification like curl -k
                .header("Content-Type", "application/json")
                .header("X-Requested-By", "XMLHttpRequest")
                .body(loginJSON);

        // Send POST request
        Response loginResponse = loginRequest.post(loginUrl);
        assertEquals(200,  loginResponse.getStatusCode(), "The Login is not successful");

        //get cookie values
        Map<String, String> cookies = new HashMap<>();
        cookies.putAll(loginResponse.getCookies());
        logger.info("cookies received");

        return cookies;
    }
    
}
