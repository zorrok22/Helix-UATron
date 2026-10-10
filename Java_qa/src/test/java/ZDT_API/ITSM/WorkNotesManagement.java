package ZDT_API.ITSM;

import static org.testng.Assert.assertEquals;

import java.util.*;
import java.util.logging.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class WorkNotesManagement {

    private ITSMLogin login;
    private static final Logger logger = Logger.getLogger(WorkNotesManagement.class.getName());
    private Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();

    public WorkNotesManagement(ITSMLogin login)
    {
        this.login = login;
        config = login.getConfig();
    }

    public void getWorkNotesDetails(String type, String Id)
    {
        cookies = login.getCookies();
        headers = login.getHeaders();
        String getWorkNotesDetailsUrl = config.get("IS_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/worknote/" + type.toLowerCase() + "/" + Id ;
        Response getWorkNotesDetailsResponse = RestAssured.given().baseUri(getWorkNotesDetailsUrl).headers(headers).cookies(cookies).contentType(ContentType.JSON).queryParam("startIndex", 1).queryParam("pageSize", 10).when().get().then().extract().response();
        assertEquals(getWorkNotesDetailsResponse.getStatusCode(), 200, "The Get work notes details is not successful");
        logger.info("Get work notes details process is successful for user: " + login.getUserName());
    }

    public void createWorkNotes(String type, String Id)
    {
        cookies = login.getCookies();
        headers = login.getHeaders();
        
        // Create a copy of headers without Content-Type for multipart request
        Map<String, String> multipartHeaders = new HashMap<>(headers);
        multipartHeaders.remove("Content-Type"); // Remove Content-Type to let RestAssured set multipart/form-data
        
        String createWorkNotesUrl = config.get("IS_URL") + "/api/com.bmc.dsm.itsm.itsm-rest-api/v2/worknote/" + type.toLowerCase() + "/" + Id ;
        String message = "This is a follow up note";
        
        Response createWorkNotesResponse = RestAssured.given()
            .headers(multipartHeaders)
            .cookies(cookies)
            .multiPart("note", message)
            .multiPart("access", "true")
            .multiPart("infoType", "4000")
            .when()
            .post(createWorkNotesUrl)
            .then()
            .extract()
            .response();
            
        logger.info(createWorkNotesResponse.getBody().asString());
        assertEquals(createWorkNotesResponse.getStatusCode(), 200, "The Create work notes is not successful for user: " + login.getUserName());
        logger.info("Create work notes process is successful for user: " + login.getUserName());
    }
    
}
