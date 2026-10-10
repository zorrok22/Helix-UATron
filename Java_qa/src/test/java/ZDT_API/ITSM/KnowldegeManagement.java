package ZDT_API.ITSM;

import static org.testng.Assert.assertEquals;

import java.util.*;
import java.util.logging.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class KnowldegeManagement {

    private ITSMLogin login;
    private static final Logger logger = Logger.getLogger(IncidentManagement.class.getName());
    private Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    
    public KnowldegeManagement(ITSMLogin login)
    {
        this.login = login;
        config = login.getConfig();
    }

    public void searchArticleDetails(String type, String Id)
    {
        cookies = login.getCookies();
        headers = login.getHeaders();
        String searchArticleDetailsUrl = config.get("IS_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/" + type.toLowerCase() +"/" + Id + "/recommended/knowledge";
        Response searchArticleDetailsResponse = RestAssured.given().baseUri(searchArticleDetailsUrl).headers(headers).cookies(cookies).contentType(ContentType.JSON).when().get().then().extract().response();
        assertEquals(searchArticleDetailsResponse.getStatusCode(), 200, "The Search article details is not successful for user" + login.getUserName());
        logger.info("Search Knowledge Article Process is successful for user" + login.getUserName());
    }
}
