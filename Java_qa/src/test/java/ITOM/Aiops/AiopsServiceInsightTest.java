package ITOM.Aiops;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ITOM.Api.Aiops;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.CommonMethods;
import util.Product;
import util.ReadPropertiesFile;
import util.SolqaDetails;
import util.SolqaLogger;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class AiopsServiceInsightTest {

    private static final SolqaLogger log = new SolqaLogger(AiopsServiceInsightTest.class.getName());
    private static final String SERVICE_NAME = "Ml-TSOM";
    private static final String SERVICE_INSIGHT_ENDPOINT = "/service-health-insight/api/v1.0/health/";

    private final Aiops aiopsApi = new Aiops();
    CommonMethods commonmethod = new CommonMethods();
    
    @Test(description = "[Service Insights] Verify the Pattern and Behaviour of a service is getting populated and shown in Service Insights tab if data points are consistent for more than 2 days", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12434", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12434_Verify_the_Pattern_and_Behaviour_of_a_service_is_getting_populated_and_shown_in_Service_Insights_tab(ITestContext context) throws Exception {
        context.setAttribute("ServiceLevel_Product", Product.Service_Monitoring);
        String jwtToken = commonmethod.refreshJwtToken(null);
        SoftAssert softAssert = new SoftAssert();
        String serviceKey = resolveServiceKey(SERVICE_NAME, jwtToken);

        log.info("Resolved service key for " + SERVICE_NAME + ": " + serviceKey);

        String endpoint = SERVICE_INSIGHT_ENDPOINT + serviceKey + "?type=key&insightDurationInDays=15";
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + jwtToken)
                .get(endpoint);
        Assert.assertEquals(response.getStatusCode(), 200,
                "Failed to fetch service insight: " + response.getBody().asString());

        JSONObject insight = new JSONObject(response.getBody().asString());
        double minConfidence = insight.optDouble("minTrendConfidencePercentage", -1);
        log.info("minTrendConfidencePercentage: " + minConfidence);

        if (minConfidence > 50) {
            JSONArray behaviors = insight.optJSONArray("behaviors");
            Assert.assertNotNull(behaviors, "Behaviors data missing when confidence exceeded threshold");
            Assert.assertTrue(behaviors.length() > 0, "Behaviors array empty despite high confidence");

            for (int i = 0; i < behaviors.length(); i++) {
                JSONObject behavior = behaviors.getJSONObject(i);
                String summary = behavior.optString("summary", "").trim();
                softAssert.assertFalse(summary.isEmpty(), "Behavior summary missing at index " + i);
            }
        } else {
            log.info("Confidence below threshold; skipping behavior summary validation");
        }

        softAssert.assertAll();
    }

    private String resolveServiceKey(String serviceName, String jwtToken) {
        JSONObject services = aiopsApi.searchAndGetServiceDetails(serviceName, jwtToken);
        Iterator<String> iterator = services.keys();
        while (iterator.hasNext()) {
            String id = iterator.next();
            JSONObject service = services.getJSONObject(id);
            if (serviceName.equalsIgnoreCase(service.optString("name"))) {
                return service.optString("key");
            }
        }
        throw new IllegalStateException("Unable to resolve service key for " + serviceName);
    }
}
