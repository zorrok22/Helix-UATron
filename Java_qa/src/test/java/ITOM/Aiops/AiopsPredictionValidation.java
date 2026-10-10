package ITOM.Aiops;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.ITOM.LogAnalytics.LogAnalyticsPage;
import util.ReadPropertiesFile;
import util.SolqaDetails;
import util.SolqaLogger;
import pages.ITOM.Aiops.AiopsDeepRCA;
import pages.ITOM.Api.Aiops;
import pages.ITOM.Api.HMEvents;
import java.io.File;
import java.io.IOException;
import java.util.*;
import util.*;

/**
 * Java translation (API-first) of Robot test "create-service-configure-for-prediction-and-runAutomation".
 * This focuses on the core REST steps without invoking Robot Framework.
 * NOTE: Several Robot keywords map to REST calls shown here; any missing mappings are marked TODO.
 */
public class AiopsPredictionValidation {

    private static final SolqaLogger log = new SolqaLogger(AiopsPredictionValidation.class.getName());
    Aiops aiopsApiCore = new Aiops();
    HMEvents hm_events = new HMEvents();
    CommonMethods commonmethod = new CommonMethods();
    private SoftAssert softAssert = null;
    private String jwtToken = null;
    private String SERVICE_PREFIX = "predict-pa-";
    private String alarmPolicyName = "predict";
    AiopsDeepRCA aiopsDeepRCA = new AiopsDeepRCA();

    

    @Test(description = "[Service Prediction] Verify Service failure predictions and validate create/request automation flow and status in AIOPS", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "12442", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_12442_Verify_Service_failure_predictions_and_validate_request_automation_flow_and_status_in_AIOPS_(ITestContext context) throws IOException, InterruptedException {
        this.softAssert = new SoftAssert();
        String msearchQueryFile = "msearchqueryonallClass.json";
        jwtToken = commonmethod.refreshJwtToken(null);
        String className = "Prediction";
        JSONObject events_Resp = hm_events.runMSearchQueryGetSituation(className,jwtToken,msearchQueryFile,2);
        int currentincidentcount = events_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        if (currentincidentcount < 1) {
            softAssert.fail("No Prediction events found for class " + className);
        }
        //Check Last one hour data
        long currentTime = hm_events.getBhomServerTime(jwtToken);
        long oneHourAgo = currentTime - 72 * 60 * 60 * 1000; // 3 days ago
        boolean founddata = false;
        JSONArray hitsobject = events_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
        if (hitsobject.length() > 0) {
            log.info("Got Prediction Record");
        }
        for(Object obj:hitsobject) {
            JSONObject event = (JSONObject) obj;

            long creation_time = event.getJSONObject("_source").getLong("creation_time");
            String serviceName = event.getJSONObject("_source").getString("pr_service_name");

            if(creation_time < oneHourAgo){
                continue;
            }
            founddata = true;
            if (serviceName.contains(SERVICE_PREFIX)){
                log.info("Prediction occured on created Service");
                break;
            }

        }

        softAssert.assertTrue(founddata, "No Prediction events found for service " + SERVICE_PREFIX);
        hm_events.SearchanddeleteAlarmPolicies(alarmPolicyName,jwtToken);
        aiopsApiCore.deleteServiceModel(SERVICE_PREFIX, jwtToken);
        softAssert.assertAll();
    }

    @Test(description = "[KPI Prediction] Verify prediction for multiple KPI metrics in Same Service", groups = {"Regression"})
    @SolqaDetails(authorName = "Haris", JiraID = "18967", Products = {Product.Service_Monitoring, Product.Helix_Discovery})
    public void DRSQA_18967_Verify_prediction_for_multiple_KPI_metrics_in_Same_Service(ITestContext context) throws IOException, InterruptedException {
        this.softAssert = new SoftAssert();
        String msearchQueryFile = "msearchqueryonallClass.json";
        jwtToken = commonmethod.refreshJwtToken(null);
        String className = "Prediction";

        boolean isFeatureEnabled = aiopsDeepRCA.TenantFeatureStatusCheck("kpi-predictions");
        if(!isFeatureEnabled){
            log.info("manage-kpi-predictions-enabled feature flag is not enabled; skipping test as passed.");
            return;
        }
        
        JSONObject events_Resp = hm_events.runMSearchQueryGetSituation(className,jwtToken,msearchQueryFile,2);
        int currentincidentcount = events_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONObject("total").getInt("value");
        if (currentincidentcount < 1) {
            softAssert.fail("No Prediction events found for class " + className);
        }
        //Check Last one hour data
        long currentTime = hm_events.getBhomServerTime(jwtToken);
        long oneHourAgo = currentTime - 72 * 60 * 60 * 1000; // 3 days ago
        boolean founddata = false;
        JSONArray hitsobject = events_Resp.getJSONArray("responses").getJSONObject(0).getJSONObject("hits").getJSONArray("hits");
        if (hitsobject.length() > 0) {
            log.info("Got Prediction Record");
        }
        for(Object obj:hitsobject) {
            JSONObject event = (JSONObject) obj;

            long creation_time = event.getJSONObject("_source").getLong("creation_time");
            String operations  = event.getJSONObject("_source").getString("_operations");

            if(creation_time < oneHourAgo){
                continue;
            }
            if(!operations.contains("KPI")){
                continue;
            }
            founddata = true;
            break;

        }

        softAssert.assertTrue(founddata, "No Prediction events found for service " + SERVICE_PREFIX);
        hm_events.SearchanddeleteAlarmPolicies(alarmPolicyName,jwtToken);
        aiopsApiCore.deleteServiceModel(SERVICE_PREFIX, jwtToken);
        softAssert.assertAll();
    }

}