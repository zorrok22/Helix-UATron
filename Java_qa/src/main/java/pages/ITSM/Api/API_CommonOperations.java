package pages.ITSM.Api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class API_CommonOperations {

    public static final Logger log = Logger.getLogger(API_CommonOperations.class.getName());

    public String getSamlToken(
            String ssoUrl,
            String username,
            String password,
            String tenant,
            String gotoUrl
    ) {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("user-name", username);
        formParams.put("password", password);
        formParams.put("tenant", tenant);
        formParams.put("goto", gotoUrl);
        formParams.put("reauth", "false");
        formParams.put("redirect", "0");
        formParams.put("url_hash_handler", "true");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Accept", "/");
        headers.put("Origin", gotoUrl);
        headers.put("Referer", ssoUrl);

        Response response = RestAssured.given()
                .headers(headers)
                .formParams(formParams)
                .post(ssoUrl);

        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to get SAML token. Status code: " + response.getStatusCode());
        }

        String samlToken = response.getCookie("saml_token");
        if (samlToken == null) {
            throw new RuntimeException("SAML token not found in response cookies.");
        }

        log.info("SAML Token: " + samlToken);
        return samlToken;
    }

    public Map<String, String> getSessionCookies(WebDriver driver) {
        Map<String, String> map = new HashMap<>();

        for (Cookie c : driver.manage().getCookies()) {
            map.put(c.getName(), c.getValue());
        }

        return map;
    }

}
