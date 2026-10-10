package pages.ITSM.DWPC;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.path.json.exception.JsonPathException;
import io.restassured.response.Response;
import pages.ITSM.Api.API_CommonOperations;
import util.CommonMethods;
import java.util.List;
import java.util.Map;

public class DWPC_APICommonOperations extends API_CommonOperations {

    String CONNECTORS_ENDPOINT = "/api/myit-sb/connections";

    public String getRemedyConnectionId() {
        String dwpcCatalogUrl = CommonMethods.readConfig("DWPCatalog_URL");
        String token = getSamlToken(
                CommonMethods.readConfig("RSSO_URL"),
                CommonMethods.readConfig("adminUserName"),
                CommonMethods.readConfig("adminPassword"),
                CommonMethods.readConfig("STACK_NAME"),
                CommonMethods.readConfig("DWPCatalog_URL")
        );

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .get(dwpcCatalogUrl + CONNECTORS_ENDPOINT);

        int statusCode = response.getStatusCode();
        String contentType = response.getContentType();
        String body = response.getBody() == null ? "" : response.getBody().asString();

        if (statusCode != 200) {
            throw new RuntimeException("Failed to fetch connections. Status code: " + statusCode + ", Body: " + body);
        }
        if (contentType == null || !contentType.toLowerCase().contains("json")) {
            throw new RuntimeException("Connections response is not JSON. Content-Type: " + contentType + ", Body: " + body);
        }
        if (body.trim().isEmpty()) {
            throw new RuntimeException("Connections response body is empty.");
        }

        List<Map<String, Object>> list;
        try {
            list = JsonPath.from(body).getList("$");
        } catch (JsonPathException e) {
            throw new RuntimeException("Failed to parse connections response as JSON. Body: " + body, e);
        }

        if (list == null || list.isEmpty()) {
            throw new RuntimeException("Connections list is empty in DWPC response.");
        }

        Map<String, Object> helix = list.stream()
                .filter(x -> "Helix ITSM".equals(x.get("connectorName")) &&
                        "Remedy".equals(x.get("name")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Helix ITSM Remedy not found"));

        Object rxConnectionId = helix.get("rxConnectionId");
        if (rxConnectionId == null || String.valueOf(rxConnectionId).trim().isEmpty()) {
            throw new RuntimeException("rxConnectionId is missing for Helix ITSM Remedy connector.");
        }

        return String.valueOf(rxConnectionId);
    }

}