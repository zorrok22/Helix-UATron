package util;

import framework.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.pagefactory.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Custom ElementLocator that provides self-healing capabilities for @FindBy elements.
 * When an element cannot be found with the original locator, it attempts to heal the locator
 * using the LocatorHealer class.
 * 
 * @author SolutionsQA Team
 * @version 1.0
 * @since 2025-01-07
 */
public class HealingElementLocator implements ElementLocator {
    
    private static final SolqaLogger log = new SolqaLogger(HealingElementLocator.class.getName());
    private final SearchContext searchContext;
    private final By by;
    private final boolean shouldCache;
    private WebElement cachedElement;
    private List<WebElement> cachedElementList;
    private final String testName;
    
    /**
     * Constructor
     * @param searchContext WebDriver or WebElement to search within
     * @param field Field with @FindBy annotation
     * @param testName Current test name for tracking healings
     */
    public HealingElementLocator(SearchContext searchContext, Field field, String testName) {
        this.searchContext = searchContext;
        this.by = buildByFromAnnotations(field);
        this.shouldCache = field.getAnnotation(org.openqa.selenium.support.CacheLookup.class) != null;
        this.testName = testName;
    }
    
    /**
     * Find the element, with healing if original locator fails
     */
    @Override
    public WebElement findElement() {
        if (shouldCache && cachedElement != null) {
            return cachedElement;
        }
        
        WebElement element = findElementWithHealing();
        
        if (shouldCache) {
            cachedElement = element;
        }
        
        return element;
    }
    
    /**
     * Find elements, with healing if original locator fails
     */
    @Override
    public List<WebElement> findElements() {
        if (shouldCache && cachedElementList != null) {
            return cachedElementList;
        }
        
        List<WebElement> elements = findElementsWithHealing();
        
        if (shouldCache) {
            cachedElementList = elements;
        }
        
        return elements;
    }
    
    /**
     * Find element with healing capability
     */
    private WebElement findElementWithHealing() {
        try {
            // Try finding with original locator
            return searchContext.findElement(by);
        } catch (NoSuchElementException e) {
            // Original locator failed, attempt healing
            log.info("@FindBy element not found with original locator, attempting to heal: " + by);
            
            if (searchContext instanceof WebDriver) {
                WebDriver driver = (WebDriver) searchContext;
                int timeout = 30; // Default timeout
                
                LocatorHealer healer = new LocatorHealer(driver, timeout);
                return healer.findElement(by, testName);
            } else {
                throw e; // Can't heal if not searching from root
            }
        }
    }
    
    /**
     * Find elements with healing capability
     */
    private List<WebElement> findElementsWithHealing() {
        try {
            // Try finding with original locator
            List<WebElement> elements = searchContext.findElements(by);
            if (elements != null && !elements.isEmpty()) {
                return elements;
            }
            // Empty list, try healing
            throw new NoSuchElementException("No elements found");
        } catch (NoSuchElementException e) {
            // Original locator failed, attempt healing
            log.info("@FindBy elements not found with original locator, attempting to heal: " + by);
            
            if (searchContext instanceof WebDriver) {
                WebDriver driver = (WebDriver) searchContext;
                int timeout = 30; // Default timeout
                
                LocatorHealer healer = new LocatorHealer(driver, timeout);
                return healer.findElements(by, testName);
            } else {
                throw e; // Can't heal if not searching from root
            }
        }
    }
    
    /**
     * Build By locator from @FindBy annotations on the field
     */
    private By buildByFromAnnotations(Field field) {
        // Use Selenium's Annotations class to build the By
        Annotations annotations = new Annotations(field);
        return annotations.buildBy();
    }
}

