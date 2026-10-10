package pages.ITSM.HelixGPT;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.*;
import static org.testng.Assert.assertEquals;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;


import framework.Launcher;

public class Chatbot_API extends Launcher{

    public Map<String, String> config;
    private static final Logger logger = Logger.getLogger(Chatbot_API.class.getName());

    public Chatbot_API(){
        config = configProp;
    }

    public void CreateChatbot(String chatbotName, Map<String, String> cookies)
    {
        try{
            String chatbotUrl = config.get("IS_URL") + "/api/rx/application/record/recordinstance";
            String payloadPath = "Java_qa/resources/ITSM/HelixGPT/Create_Chatbot_request.json";
            String bodyJson = new String(Files.readAllBytes(Paths.get(payloadPath)));
            bodyJson=bodyJson.replace("{0}", chatbotName);
            logger.info(bodyJson);

            RequestSpecification chatbotRequest = RestAssured.given()
                .relaxedHTTPSValidation() // to skip SSL verification like curl -k
                .header("Content-Type", "application/json")
                .header("X-Requested-By", "XMLHttpRequest")
                .cookies(cookies)
                .body(bodyJson);

            // Send POST request
            Response chatbotResponse = chatbotRequest.post(chatbotUrl);
            assertEquals(201,  chatbotResponse.getStatusCode(), "The Chatbot creation is not successful");
        }
        catch (Exception e) {
            logger.severe("Exception in Chatbot Creation: " + e.getMessage());
            e.printStackTrace();
        }
       
    }
    
}
