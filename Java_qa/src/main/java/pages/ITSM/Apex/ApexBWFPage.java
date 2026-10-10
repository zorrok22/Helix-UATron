package pages.ITSM.Apex;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import framework.BasePage;
import pages.ITSM.bwf.BWFKnowledge;
import util.CommonWaits;
import util.SolQAConstants;
import util.SolqaLogger;

public class ApexBWFPage extends BasePage {

    static SolqaLogger log;
    CommonWaits commonWaits;
    BWFKnowledge bwfKnowledge;

    public ApexBWFPage() {
        PageFactory.initElements(getDriver(), this);
        log = new SolqaLogger(ApexBWFPage.class.getName());
        commonWaits=new CommonWaits();
        bwfKnowledge = new BWFKnowledge();
    }

    @FindBy(xpath = "//div[contains(@class,'dp-content')]//button[contains(. ,'Save')]")
    WebElement saveUpdatedStatusButton;
    
    //String variables
    String knowledgeSet = "Knowledge Set";
    String category_Tier_1 = "Category Tier 1";
    String category_Tier_2 = "Category Tier 2";
    String category_Tier_3 = "Category Tier 3";
    String category_Tier_4 = "Category Tier 4";
    String siteGroup = "Site Group";

    public String createKnowledgeArticleApex(Map<Object, String> knowledgeData){
        log.info("Creating Knowledge Article in BWF Application");
        bwfKnowledge.selectKnowledgeTemplate(knowledgeData.get("ArticleTemplate"));
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        bwfKnowledge.setArticleTitle(knowledgeData.get("ArticleTitle"));
        bwfKnowledge.selectFieldsInKnowledgeMetadata(knowledgeSet, knowledgeData.get("KnowledgeSet"));
        bwfKnowledge.clickOnAssignToMeButton();
        bwfKnowledge.selectFieldsInKnowledgeMetadata(category_Tier_1, knowledgeData.get("TIER1"));
        bwfKnowledge.selectFieldsInKnowledgeMetadata(category_Tier_2, knowledgeData.get("TIER2"));
        bwfKnowledge.selectFieldsInKnowledgeMetadata(category_Tier_3, knowledgeData.get("TIER3"));
        bwfKnowledge.selectFieldsInKnowledgeMetadata(category_Tier_4, knowledgeData.get("TIER4"));
        bwfKnowledge.selectFieldsInKnowledgeMetadata(SolQAConstants.REGION,knowledgeData.get("REGION"));
        bwfKnowledge.selectFieldsInKnowledgeMetadata(siteGroup, knowledgeData.get("SITE_GROUP"));
        bwfKnowledge.selectFieldsInKnowledgeMetadata(SolQAConstants.SITE, knowledgeData.get("SITE"));
        bwfKnowledge.save_Knowledge();
        String knowledgeArticleID = bwfKnowledge.getKAIDFieldValueOnPreview();
        bwfKnowledge.clickonViewFullArticle();
        return knowledgeArticleID;
    }

    public void updateArticleStatusApex(String status) {
        log.info("Changing Knowledge Article Status to '" + status + "'");
        commonWaits.clickWithoutInterception(bwfKnowledge.txt_articleStatus);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
        commonWaits.waitForByElementToBeVisible(By.xpath(String.format(bwfKnowledge.dropDownKnowledgeMetadata, SolQAConstants.STATUS))).click();
        commonWaits.clickWithoutInterception(By.xpath(String.format(bwfKnowledge.dropdownValue_xpath, status)));
        log.info("Selected '" + status + "' for Status Dropdown");
        commonWaits.clickWithoutInterception(saveUpdatedStatusButton);
        commonWaits.waitForAllSpinnersToHide(SolQAConstants.appBWF);
    }


}
