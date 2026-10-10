package ZDT_API.SmartIT;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class CreateBroadcast {

    private LoginHelper loginHelper;
    Map<String, String> config;
    private Map<String, String> cookies = new HashMap<>();
    Map<String, String> headers = new HashMap<>();
    private String csrfToken;
    private static final Logger logger = Logger.getLogger(CreateBroadcast.class.getName());

    public CreateBroadcast(LoginHelper loginHelper)
    {
        this.loginHelper = loginHelper;
    }

    public void run()
    {
        try{
            logger.info("Starting Create Broadcast Flow");
            boolean loginSuccess = loginHelper.performLogin();
            if (!loginSuccess) {
                logger.warning("Login failed for user: " + loginHelper.getUserName());
                return;
            }
            config = loginHelper.getConfig();
            headers = loginHelper.getHeaders();
            cookies = loginHelper.getCookies();
            csrfToken = loginHelper.getCsrfToken();

            // Calculate start and end time (mimic SCRIPT_Calculate_Start_and_End_Time)
            long currentMillis = System.currentTimeMillis();
            long fiveMinutesInMillis = 5 * 60 * 1000;
            long startMillis = currentMillis;
            long endMillis = currentMillis + fiveMinutesInMillis;
            // Prepare payload
            String payloadPath = "resources" + File.separator + "SMARTIT" + File.separator + "payloads" + File.separator + "SmartIT_create_broadcast_payload.json";
            String payload = new String(Files.readAllBytes(Paths.get(payloadPath)));
            // Replace variables in payload
            String strmsg = "Performance_Broadcast_message_" + (Instant.now().getEpochSecond());
            payload = payload.replace("${strmsg}", strmsg)
                .replace("${StartdatemBaseline}", String.valueOf(startMillis))
                .replace("${EnddatemBaseline}", String.valueOf(endMillis))
                .replace("${LoginUsercompanyname}", "CFG");
            //logger.info(payload);
            // Add more replacements as needed for company, etc.
            // Prepare headers
            headers.put("Referer", config.get("SmartIT_url") + "/smartit/app/");
            headers.put("X-XSRF-TOKEN", csrfToken);
            // POST to create broadcast
            String createUrl = config.get("SmartIT_URL") + "/smartit/rest/broadcast";
            Response createBroadcastResp = RestAssured.given().baseUri(createUrl).headers(headers).cookies(cookies).body(payload).contentType(ContentType.JSON).when().post().then().extract().response();
            assertEquals(createBroadcastResp.getStatusCode(), 200, "The Create Broadcast Operation has failed for user: " + loginHelper.getUserName());
        }
        catch (Exception e) {
            logger.severe("Exception in Create WorkOrder: " + e.getMessage() + " for user: " + loginHelper.getUserName());
            e.printStackTrace();
        }
    }
}