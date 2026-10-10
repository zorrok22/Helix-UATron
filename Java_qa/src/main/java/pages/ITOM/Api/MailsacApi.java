package pages.ITOM.Api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import org.json.JSONArray;
import org.json.JSONObject;

import util.CommonWaits;
import util.SolqaLogger;

import java.util.ArrayList;
import java.util.List;

public class MailsacApi {
    private static final SolqaLogger log = new SolqaLogger(MailsacApi.class.getName());
    CommonWaits commonWaits=new CommonWaits();
    private static final String MAILSAC_BASE_URI = "https://mailsac.com";
    private static final String MESSAGES_ENDPOINT = "/api/addresses/{email}/messages";
    private static final String TEXT_ENDPOINT = "/api/text/{email}/{messageId}";
    private static final String DELETE_MESSAGE_ENDPOINT = "/api/addresses/{email}/messages/{messageId}";

    private final String apiKey;

    public MailsacApi(String apiKey) {
        this.apiKey = apiKey;
    }

    public JSONArray getMessages(String emailAddress) {
        log.info("Fetching messages for: " + emailAddress);
        RestAssured.baseURI = MAILSAC_BASE_URI;
        Response response = given()
                .header("Mailsac-Key", apiKey)
                .get(MESSAGES_ENDPOINT.replace("{email}", emailAddress));

        if (response.getStatusCode() == 200) {
            JSONArray messages = new JSONArray(response.getBody().asString());
            log.info("Found " + messages.length() + " messages for " + emailAddress);
            return messages;
        } else {
            log.error("Failed to fetch messages. Status: " + response.getStatusCode());
            return new JSONArray();
        }
    }

    public String getMessageText(String emailAddress, String messageId) {
        log.info("Reading message text for messageId: " + messageId);
        RestAssured.baseURI = MAILSAC_BASE_URI;
        Response response = given()
                .header("Mailsac-Key", apiKey)
                .get(TEXT_ENDPOINT.replace("{email}", emailAddress).replace("{messageId}", messageId));

        if (response.getStatusCode() == 200) {
            return response.getBody().asString();
        } else {
            log.error("Failed to read message text. Status: " + response.getStatusCode());
            return "";
        }
    }

    public MailsacApi deleteMessage(String emailAddress, String messageId) {
        log.info("Deleting message: " + messageId + " from " + emailAddress);
        RestAssured.baseURI = MAILSAC_BASE_URI;
        Response response = given()
                .header("Mailsac-Key", apiKey)
                .delete(DELETE_MESSAGE_ENDPOINT.replace("{email}", emailAddress).replace("{messageId}", messageId));

        if (response.getStatusCode() == 200) {
            log.info("Message deleted successfully: " + messageId);
        } else {
            log.error("Failed to delete message. Status: " + response.getStatusCode());
        }
		return this;
    }

    public List<JSONObject> getMessagesBySubject(String emailAddress, String subjectContains, int maxAttempts) {
        log.info("Polling for email with subject containing: " + subjectContains);
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            log.info("Attempt " + attempt + "/" + maxAttempts);
            JSONArray messages = getMessages(emailAddress);
            List<JSONObject> matched = new ArrayList<>();
            for (int i = 0; i < messages.length(); i++) {
                JSONObject msg = messages.getJSONObject(i);
                String subject = msg.optString("subject", "");
                if (subject.contains(subjectContains)) {
                    matched.add(msg);
                }
            }
            if (!matched.isEmpty()) {
                log.info("Found " + matched.size() + " email(s) matching subject: " + subjectContains);
                return matched;
            }
            if (attempt < maxAttempts) {
                commonWaits.waitForSometime(2000);
            }    
        }
        log.info("No email found with subject containing: " + subjectContains + " after " + maxAttempts + " attempts");
        return new ArrayList<>();
    }

    public MailsacApi deleteAllMessages(String emailAddress) {
        log.info("Deleting all messages for: " + emailAddress);
        JSONArray messages = getMessages(emailAddress);
        for (int i = 0; i < messages.length(); i++) {
            String messageId = messages.getJSONObject(i).optString("_id", "");
            if (!messageId.isEmpty()) {
                deleteMessage(emailAddress, messageId);
            }
        }
		return this;
    }
}
