package util;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * LocatorHealer provides self-healing capabilities for locators when they fail.
 * It attempts multiple fallback strategies to find elements when the original locator fails.
 * 
 * Healing Strategies:
 * 1. Partial text match
 * 2. Contains attribute match
 * 3. Similar XPath with relaxed constraints
 * 4. CSS to XPath conversion and vice versa
 * 5. Fuzzy matching on attributes
 * 6. Sibling/Parent element matching
 * 
 * @author SolutionsQA Team
 * @version 1.0
 * @since 2025-01-07
 */
public class LocatorHealer {
    
    private static final SolqaLogger log = new SolqaLogger(LocatorHealer.class.getName());
    private WebDriver driver;
    private int timeout;
    private HealedLocatorManager healingManager;
    
    /**
     * Constructor
     * @param driver WebDriver instance
     * @param timeout Timeout in seconds for finding elements
     */
    public LocatorHealer(WebDriver driver, int timeout) {
        this.driver = driver;
        this.timeout = timeout;
        this.healingManager = HealedLocatorManager.getInstance();
    }
    
    /**
     * Attempts to find element using original locator, falls back to healing strategies if it fails
     * @param by Original locator
     * @param testName Name of the test for tracking
     * @return WebElement if found, null otherwise
     */
    public WebElement findElement(By by, String testName) {
        String originalLocator = by.toString();
        log.info("Attempting to find element with locator: " + originalLocator);
        
        try {
            // Try original locator first
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(timeout))
                    .until(ExpectedConditions.presenceOfElementLocated(by));
            log.info("Element found with original locator");
            return element;
        } catch (TimeoutException | org.openqa.selenium.NoSuchElementException e) {
            log.warn("Original locator failed: " + originalLocator + ". Attempting to heal...");
            
            // Try healing strategies
            HealingResult result = attemptHealing(by, testName);
            
            if (result.isHealed()) {
                log.info("Successfully healed locator! New locator: " + result.getHealedLocator());
                healingManager.recordHealing(testName, originalLocator, result.getHealedLocator(), 
                                            result.getStrategy(), new Date());
                return result.getElement();
            } else {
                log.error("All healing strategies failed for locator: " + originalLocator);
                throw new org.openqa.selenium.NoSuchElementException("Unable to find element even after healing attempts: " + originalLocator);
            }
        }
    }
    
    /**
     * Attempts to find elements using original locator, falls back to healing strategies if it fails
     * @param by Original locator
     * @param testName Name of the test for tracking
     * @return List of WebElements
     */
    public List<WebElement> findElements(By by, String testName) {
        String originalLocator = by.toString();
        log.info("Attempting to find elements with locator: " + originalLocator);
        
        try {
            List<WebElement> elements = new WebDriverWait(driver, Duration.ofSeconds(timeout))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
            
            if (elements != null && !elements.isEmpty()) {
                log.info("Elements found with original locator, count: " + elements.size());
                return elements;
            }
        } catch (TimeoutException | org.openqa.selenium.NoSuchElementException e) {
            log.warn("Original locator failed: " + originalLocator + ". Attempting to heal...");
        }
        
        // Try healing strategies
        HealingResult result = attemptHealing(by, testName);
        
        if (result.isHealed()) {
            log.info("Successfully healed locator! New locator: " + result.getHealedLocator());
            healingManager.recordHealing(testName, originalLocator, result.getHealedLocator(), 
                                        result.getStrategy(), new Date());
            
            // Find all elements with the healed locator
            try {
                return driver.findElements(convertStringToBy(result.getHealedLocator()));
            } catch (Exception e) {
                return Collections.singletonList(result.getElement());
            }
        } else {
            log.error("All healing strategies failed for locator: " + originalLocator);
            return new ArrayList<>();
        }
    }
    
    /**
     * Attempts various healing strategies to find the element
     * @param originalBy Original locator
     * @param testName Test name for tracking
     * @return HealingResult containing healed element and strategy used
     */
    private HealingResult attemptHealing(By originalBy, String testName) {
        String originalLocator = originalBy.toString();
        
        log.info("Starting healing process for: " + originalLocator);
        
        // Strategy 1: Try with relaxed XPath (remove specific indices)
        HealingResult result = tryRelaxedXPath(originalLocator);
        if (result.isHealed()) return result;
        
        // Strategy 2: Try with partial attribute matching
        result = tryPartialAttributeMatch(originalLocator);
        if (result.isHealed()) return result;
        
        // Strategy 3: Try with text content matching
        result = tryTextContentMatch(originalLocator);
        if (result.isHealed()) return result;
        
        // Strategy 4: Try with class name matching
        result = tryClassNameMatch(originalLocator);
        if (result.isHealed()) return result;
        
        // Strategy 5: Try with ID pattern matching
        result = tryIdPatternMatch(originalLocator);
        if (result.isHealed()) return result;
        
        // Strategy 6: Try with tag name and attributes
        result = tryTagAndAttributeMatch(originalLocator);
        if (result.isHealed()) return result;
        
        // Strategy 7: Try with CSS selector variations
        result = tryCssSelectorVariations(originalLocator);
        if (result.isHealed()) return result;
        
        // Strategy 8: Try with parent/child relationship
        result = tryParentChildRelationship(originalLocator);
        if (result.isHealed()) return result;
        
        // Strategy 9: Try finding by attribute name only (NEW - more aggressive)
        result = tryAttributeNameOnly(originalLocator);
        if (result.isHealed()) return result;
        
        // Strategy 10: Try finding similar elements by type (NEW - very aggressive)
        result = trySimilarElementsByType(originalLocator);
        if (result.isHealed()) return result;
        
        // Strategy 11: Try fuzzy attribute value matching (handles typos in attribute values)
        result = tryFuzzyAttributeValueMatch(originalLocator);
        if (result.isHealed()) return result;
        
        // Strategy 12: Try semantic/context-aware matching (super aggressive - finds relevant elements by keywords)
        result = trySemanticContextMatching(originalLocator);
        if (result.isHealed()) return result;
        
        log.warn("All healing strategies exhausted for: " + originalLocator);
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Strategy 1: Try with relaxed XPath by removing specific indices
     */
    private HealingResult tryRelaxedXPath(String locator) {
        if (!locator.contains("xpath")) return new HealingResult(false, null, null, null);
        
        try {
            // Extract XPath value
            String xpath = extractXPathValue(locator);
            
            // Remove array indices like [1], [2] etc
            String relaxedXPath = xpath.replaceAll("\\[\\d+\\]", "");
            
            if (!relaxedXPath.equals(xpath)) {
                By newBy = By.xpath(relaxedXPath);
                WebElement element = driver.findElement(newBy);
                
                if (element.isDisplayed()) {
                    return new HealingResult(true, element, "By.xpath: " + relaxedXPath, 
                                            "Relaxed XPath (removed indices)");
                }
            }
        } catch (Exception e) {
            log.debug("Relaxed XPath strategy failed: " + e.getMessage());
        }
        
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Strategy 2: Try with partial attribute matching
     */
    private HealingResult tryPartialAttributeMatch(String locator) {
        try {
            String xpath = extractXPathValue(locator);
            
            // Extract attribute patterns like @id='value' or @class='value'
            if (xpath.contains("@id=") || xpath.contains("@class=") || xpath.contains("@name=")) {
                // Try with contains instead of exact match
                String partialXPath = xpath
                    .replaceAll("@id=['\"]([^'\"]+)['\"]", "@id and contains(@id, '$1')")
                    .replaceAll("@class=['\"]([^'\"]+)['\"]", "@class and contains(@class, '$1')")
                    .replaceAll("@name=['\"]([^'\"]+)['\"]", "@name and contains(@name, '$1')");
                
                // Fix the XPath syntax
                partialXPath = partialXPath.replaceAll("\\[@id and contains", "[contains");
                
                By newBy = By.xpath(partialXPath);
                List<WebElement> elements = driver.findElements(newBy);
                
                if (!elements.isEmpty()) {
                    WebElement element = elements.get(0);
                    if (element.isDisplayed()) {
                        return new HealingResult(true, element, "By.xpath: " + partialXPath, 
                                                "Partial Attribute Match");
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Partial attribute match strategy failed: " + e.getMessage());
        }
        
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Strategy 3: Try matching by text content
     */
    private HealingResult tryTextContentMatch(String locator) {
        try {
            String xpath = extractXPathValue(locator);
            
            // Extract text content if present
            if (xpath.contains("text()=")) {
                String text = xpath.replaceAll(".*text\\(\\)=['\"]([^'\"]+)['\"].*", "$1");
                
                // Try with contains(text())
                String newXPath = "//*[contains(text(), '" + text + "')]";
                By newBy = By.xpath(newXPath);
                List<WebElement> elements = driver.findElements(newBy);
                
                if (!elements.isEmpty()) {
                    WebElement element = elements.get(0);
                    if (element.isDisplayed()) {
                        return new HealingResult(true, element, "By.xpath: " + newXPath, 
                                                "Text Content Match");
                    }
                }
                
                // Try with normalize-space
                newXPath = "//*[contains(normalize-space(text()), '" + text.trim() + "')]";
                newBy = By.xpath(newXPath);
                elements = driver.findElements(newBy);
                
                if (!elements.isEmpty()) {
                    WebElement element = elements.get(0);
                    if (element.isDisplayed()) {
                        return new HealingResult(true, element, "By.xpath: " + newXPath, 
                                                "Normalized Text Match");
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Text content match strategy failed: " + e.getMessage());
        }
        
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Strategy 4: Try matching by class name
     */
    private HealingResult tryClassNameMatch(String locator) {
        try {
            // Extract class name from locator
            String className = null;
            
            if (locator.contains("class name:")) {
                className = locator.replace("By.className: ", "").trim();
            } else if (locator.contains("@class=")) {
                String xpath = extractXPathValue(locator);
                className = xpath.replaceAll(".*@class=['\"]([^'\"]+)['\"].*", "$1");
            }
            
            if (className != null && !className.isEmpty()) {
                // Try with partial class match
                String[] classes = className.split("\\s+");
                for (String cls : classes) {
                    if (!cls.isEmpty()) {
                        By newBy = By.cssSelector("[class*='" + cls + "']");
                        List<WebElement> elements = driver.findElements(newBy);
                        
                        if (!elements.isEmpty()) {
                            WebElement element = elements.get(0);
                            if (element.isDisplayed()) {
                                return new HealingResult(true, element, "By.cssSelector: [class*='" + cls + "']", 
                                                        "Partial Class Name Match");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Class name match strategy failed: " + e.getMessage());
        }
        
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Strategy 5: Try matching by ID pattern
     */
    private HealingResult tryIdPatternMatch(String locator) {
        try {
            String id = null;
            
            if (locator.contains("By.id:")) {
                id = locator.replace("By.id: ", "").trim();
            } else if (locator.contains("@id=")) {
                String xpath = extractXPathValue(locator);
                id = xpath.replaceAll(".*@id=['\"]([^'\"]+)['\"].*", "$1");
            }
            
            if (id != null && !id.isEmpty()) {
                // Try with partial ID match
                By newBy = By.cssSelector("[id*='" + id + "']");
                List<WebElement> elements = driver.findElements(newBy);
                
                if (!elements.isEmpty()) {
                    WebElement element = elements.get(0);
                    if (element.isDisplayed()) {
                        return new HealingResult(true, element, "By.cssSelector: [id*='" + id + "']", 
                                                "Partial ID Match");
                    }
                }
                
                // Try with XPath contains
                String newXPath = "//*[contains(@id, '" + id + "')]";
                newBy = By.xpath(newXPath);
                elements = driver.findElements(newBy);
                
                if (!elements.isEmpty()) {
                    WebElement element = elements.get(0);
                    if (element.isDisplayed()) {
                        return new HealingResult(true, element, "By.xpath: " + newXPath, 
                                                "Contains ID Match");
                    }
                }
            }
        } catch (Exception e) {
            log.debug("ID pattern match strategy failed: " + e.getMessage());
        }
        
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Strategy 6: Try matching by tag name and attributes
     */
    private HealingResult tryTagAndAttributeMatch(String locator) {
        try {
            String xpath = extractXPathValue(locator);
            
            // Extract tag name
            String tagPattern = "^//([a-zA-Z0-9]+)";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(tagPattern);
            java.util.regex.Matcher matcher = pattern.matcher(xpath);
            
            if (matcher.find()) {
                String tagName = matcher.group(1);
                
                // Find all elements with this tag
                List<WebElement> elements = driver.findElements(By.tagName(tagName));
                
                // Try to match based on visible attributes
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        // Check if any attribute values match parts of the original locator
                        String elementId = element.getAttribute("id");
                        String elementClass = element.getAttribute("class");
                        String elementName = element.getAttribute("name");
                        
                        if ((elementId != null && xpath.contains(elementId)) ||
                            (elementClass != null && xpath.contains(elementClass)) ||
                            (elementName != null && xpath.contains(elementName))) {
                            
                            // Build a new locator based on found attributes
                            String newXPath = buildXPathFromElement(element, tagName);
                            return new HealingResult(true, element, "By.xpath: " + newXPath, 
                                                    "Tag and Attribute Match");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Tag and attribute match strategy failed: " + e.getMessage());
        }
        
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Strategy 7: Try CSS selector variations
     */
    private HealingResult tryCssSelectorVariations(String locator) {
        try {
            if (locator.contains("css selector:") || locator.contains("By.cssSelector:")) {
                String css = locator.replace("By.cssSelector: ", "").replace("By.css selector: ", "").trim();
                
                // Try converting to XPath
                String xpath = convertCssToXPath(css);
                if (xpath != null) {
                    By newBy = By.xpath(xpath);
                    List<WebElement> elements = driver.findElements(newBy);
                    
                    if (!elements.isEmpty()) {
                        WebElement element = elements.get(0);
                        if (element.isDisplayed()) {
                            return new HealingResult(true, element, "By.xpath: " + xpath, 
                                                    "CSS to XPath Conversion");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("CSS selector variations strategy failed: " + e.getMessage());
        }
        
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Strategy 8: Try parent/child relationship
     */
    private HealingResult tryParentChildRelationship(String locator) {
        try {
            String xpath = extractXPathValue(locator);
            
            // If XPath has hierarchy, try relaxing it
            if (xpath.contains("/")) {
                String[] parts = xpath.split("/");
                
                // Try building a more flexible XPath using descendant axis
                if (parts.length > 2) {
                    String lastPart = parts[parts.length - 1];
                    String newXPath = "//" + lastPart;
                    
                    By newBy = By.xpath(newXPath);
                    List<WebElement> elements = driver.findElements(newBy);
                    
                    if (!elements.isEmpty()) {
                        WebElement element = elements.get(0);
                        if (element.isDisplayed()) {
                            return new HealingResult(true, element, "By.xpath: " + newXPath, 
                                                    "Relaxed Parent-Child Relationship");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Parent-child relationship strategy failed: " + e.getMessage());
        }
        
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Strategy 9: Try finding by attribute name only (aggressive search)
     * Extracts the attribute name from the locator and searches for any element with that attribute
     */
    private HealingResult tryAttributeNameOnly(String locator) {
        try {
            log.debug("Trying attribute name only strategy...");
            
            // Extract attribute name from various locator types
            String attributeName = null;
            
            if (locator.contains("By.name:") || locator.contains("name:")) {
                attributeName = "name";
            } else if (locator.contains("By.id:") || locator.contains("id:")) {
                attributeName = "id";
            } else if (locator.contains("@name=")) {
                attributeName = "name";
            } else if (locator.contains("@id=")) {
                attributeName = "id";
            } else if (locator.contains("@class=")) {
                attributeName = "class";
            }
            
            if (attributeName != null) {
                // Search for any visible input element with this attribute
                String xpath = "//input[@" + attributeName + "]";
                log.debug("Searching with: " + xpath);
                
                By newBy = By.xpath(xpath);
                List<WebElement> elements = driver.findElements(newBy);
                
                // Find first visible AND enabled element that we can interact with
                for (WebElement element : elements) {
                    try {
                        if (element.isDisplayed() && element.isEnabled()) {
                            // Additional check: make sure it's a text input we can type into
                            String type = element.getAttribute("type");
                            if (type == null || type.isEmpty() || type.equals("text") || type.equals("search") || type.equals("email")) {
                                String actualValue = element.getAttribute(attributeName);
                                log.info("Found visible and enabled element with " + attributeName + "='" + actualValue + "'");
                                
                                String healedLocator = "By.name: " + actualValue;
                                if (attributeName.equals("id")) {
                                    healedLocator = "By.id: " + actualValue;
                                }
                                
                                return new HealingResult(true, element, healedLocator, 
                                                        "Attribute Name Search (found interactable element)");
                            }
                        }
                    } catch (Exception e) {
                        log.debug("Element not interactable, trying next: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Attribute name only strategy failed: " + e.getMessage());
        }
        
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Strategy 10: Try finding similar elements by type (very aggressive)
     * Searches for common element types (input, button, etc.) and returns first visible match
     */
    private HealingResult trySimilarElementsByType(String locator) {
        try {
            log.debug("Trying similar elements by type strategy...");
            
            // Determine element type from locator
            String[] commonInputSelectors = {
                "//input[@type='text']",
                "//input[@type='search']",
                "//input[not(@type) or @type='']",
                "//textarea",
                "//input[@type='email']",
                "//input[@type='password']"
            };
            
            String[] commonButtonSelectors = {
                "//button",
                "//input[@type='button']",
                "//input[@type='submit']",
                "//a[contains(@class, 'button') or contains(@class, 'btn')]"
            };
            
            // Try input elements first (most common for "name" attributes)
            if (locator.toLowerCase().contains("input") || locator.toLowerCase().contains("name") || locator.toLowerCase().contains("search")) {
                for (String selector : commonInputSelectors) {
                    try {
                        By newBy = By.xpath(selector);
                        List<WebElement> elements = driver.findElements(newBy);
                        
                        for (WebElement element : elements) {
                            try {
                                // Check if element is visible AND enabled AND interactable
                                if (element.isDisplayed() && element.isEnabled()) {
                                    // Try to get element attributes to verify it's usable
                                    String id = element.getAttribute("id");
                                    String name = element.getAttribute("name");
                                    String type = element.getAttribute("type");
                                    
                                    log.info("Found visible and enabled input: " + selector + 
                                            " (name=" + name + ", id=" + id + ", type=" + type + ")");
                                    
                                    // Build locator based on element's attributes
                                    String healedLocator;
                                    if (name != null && !name.isEmpty()) {
                                        healedLocator = "By.name: " + name;
                                    } else if (id != null && !id.isEmpty()) {
                                        healedLocator = "By.id: " + id;
                                    } else {
                                        healedLocator = "By.xpath: " + selector;
                                    }
                                    
                                    return new HealingResult(true, element, healedLocator, 
                                                            "Similar Element Type Search (found interactable)");
                                }
                            } catch (Exception e) {
                                log.debug("Element not interactable, trying next: " + e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        log.debug("Selector failed: " + selector);
                    }
                }
            }
            
            // Try button elements
            if (locator.toLowerCase().contains("button") || locator.toLowerCase().contains("submit")) {
                for (String selector : commonButtonSelectors) {
                    try {
                        By newBy = By.xpath(selector);
                        List<WebElement> elements = driver.findElements(newBy);
                        
                        for (WebElement element : elements) {
                            try {
                                if (element.isDisplayed() && element.isEnabled()) {
                                    log.info("Found visible and enabled button: " + selector);
                                    return new HealingResult(true, element, "By.xpath: " + selector, 
                                                            "Similar Element Type Search (found interactable)");
                                }
                            } catch (Exception e) {
                                log.debug("Element not interactable, trying next: " + e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        log.debug("Selector failed: " + selector);
                    }
                }
            }
            
        } catch (Exception e) {
            log.debug("Similar elements by type strategy failed: " + e.getMessage());
        }
        
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Strategy 11: Fuzzy attribute value matching (handles typos and partial matches)
     * Examples:
     * - //*[@href='#/create/incidentItesmPV'] can match //*[@href='#/create/incidentPV']
     * - //*[@id='submit-buton'] can match //*[@id='submit-button']
     */
    private HealingResult tryFuzzyAttributeValueMatch(String locator) {
        try {
            log.info("=== STRATEGY 11: Fuzzy Attribute Value Matching ===");
            log.info("Trying fuzzy attribute value matching for: " + locator);
            
            String xpath = extractXPathValue(locator);
            if (xpath == null || xpath.isEmpty()) {
                log.warn("⚠️ Could not extract XPath from locator");
                return new HealingResult(false, null, null, null);
            }
            
            log.info("Extracted XPath: " + xpath);
            
            // Extract attribute name and value patterns
            // Supports: @attr='value', @attr="value"
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("@(\\w+)=['\"]([^'\"]+)['\"]");
            java.util.regex.Matcher matcher = pattern.matcher(xpath);
            
            int attributeCount = 0;
            while (matcher.find()) {
                attributeCount++;
                String attributeName = matcher.group(1);
                String expectedValue = matcher.group(2);
                
                log.info("📌 Extracted attribute #" + attributeCount + ": " + attributeName + " = '" + expectedValue + "'");
                
                // Get all elements with this attribute (regardless of value)
                String findAllXPath = "//*[@" + attributeName + "]";
                try {
                    List<WebElement> candidates = driver.findElements(By.xpath(findAllXPath));
                    log.info("🔍 Found " + candidates.size() + " elements with @" + attributeName + " attribute");
                    
                    if (candidates.isEmpty()) {
                        log.warn("⚠️ No elements found with @" + attributeName + " attribute on the page!");
                        return new HealingResult(false, null, null, null);
                    }
                    
                    // Score each candidate by similarity to expected value
                    List<ScoredElement> scoredElements = new ArrayList<>();
                    
                    log.info("📊 Calculating similarity scores for each candidate...");
                    for (WebElement element : candidates) {
                        try {
                            String actualValue = element.getAttribute(attributeName);
                            if (actualValue == null || actualValue.isEmpty()) continue;
                            
                            // Calculate similarity score
                            double score = calculateSimilarity(expectedValue, actualValue);
                            
                            // Only consider elements with similarity > 0.4 (40%)
                            if (score > 0.4) {
                                scoredElements.add(new ScoredElement(element, actualValue, score));
                                log.info("✅ Candidate: " + actualValue + " (score: " + String.format("%.2f", score) + ")");
                            } else {
                                log.info("❌ Rejected: " + actualValue + " (score: " + String.format("%.2f", score) + " - too low)");
                            }
                        } catch (Exception e) {
                            // Skip this element if getAttribute fails
                            log.debug("Failed to get attribute from element: " + e.getMessage());
                            continue;
                        }
                    }
                    
                    // Sort by score (highest first)
                    scoredElements.sort((a, b) -> Double.compare(b.score, a.score));
                    
                    log.info("🎯 Found " + scoredElements.size() + " candidates with > 40% similarity");
                    
                    if (scoredElements.isEmpty()) {
                        log.warn("⚠️ No candidates with sufficient similarity (> 40%) found!");
                        return new HealingResult(false, null, null, null);
                    }
                    
                    // Try the best matches
                    log.info("🔎 Checking if top candidates are visible and enabled...");
                    for (ScoredElement scored : scoredElements) {
                        try {
                            boolean isDisplayed = scored.element.isDisplayed();
                            boolean isEnabled = scored.element.isEnabled();
                            
                            log.info("Checking: " + scored.actualValue + 
                                    " (score: " + String.format("%.2f", scored.score * 100) + "%) - " +
                                    "displayed: " + isDisplayed + ", enabled: " + isEnabled);
                            
                            if (isDisplayed && isEnabled) {
                                log.info("✅ Found fuzzy match! Expected: '" + expectedValue + 
                                        "' → Found: '" + scored.actualValue + 
                                        "' (similarity: " + String.format("%.2f", scored.score * 100) + "%)");
                                
                                String healedXPath = "//*[@" + attributeName + "='" + scored.actualValue + "']";
                                return new HealingResult(true, scored.element, "By.xpath: " + healedXPath, 
                                                        "Fuzzy Attribute Match (similarity: " + 
                                                        String.format("%.0f", scored.score * 100) + "%)");
                            } else {
                                log.info("⚠️ Element not interactable (displayed: " + isDisplayed + ", enabled: " + isEnabled + ")");
                            }
                        } catch (Exception e) {
                            // Element not interactable, try next
                            log.info("⚠️ Error checking element: " + e.getMessage());
                            continue;
                        }
                    }
                    
                    log.warn("⚠️ No interactable elements found among candidates");
                    
                } catch (Exception e) {
                    log.error("❌ Failed to search for attribute " + attributeName + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            log.warn("⚠️ No attributes found in XPath or no matches found");
            
        } catch (Exception e) {
            log.error("❌ Fuzzy attribute value matching failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Calculate similarity between two strings (0.0 = completely different, 1.0 = identical)
     * Uses multiple similarity metrics:
     * - Exact match (1.0)
     * - One string contains the other (0.7-0.9)
     * - Common prefix/suffix matching (0.5-0.8)
     * - Levenshtein distance based similarity (0.0 - 1.0)
     */
    private double calculateSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        
        // Normalize strings (lowercase, trim)
        String str1 = s1.toLowerCase().trim();
        String str2 = s2.toLowerCase().trim();
        
        // Exact match
        if (str1.equals(str2)) return 1.0;
        
        // One contains the other (high score)
        if (str1.contains(str2) || str2.contains(str1)) {
            // Score based on length ratio
            int shorter = Math.min(str1.length(), str2.length());
            int longer = Math.max(str1.length(), str2.length());
            return 0.7 + (0.2 * shorter / (double) longer);
        }
        
        // Check for common prefix (e.g., "inc" in "INCPV" and "incidentPV")
        int commonPrefixLength = 0;
        int minLen = Math.min(str1.length(), str2.length());
        for (int i = 0; i < minLen; i++) {
            if (str1.charAt(i) == str2.charAt(i)) {
                commonPrefixLength++;
            } else {
                break;
            }
        }
        
        // Check for common suffix (e.g., "PV" in both)
        int commonSuffixLength = 0;
        for (int i = 1; i <= minLen; i++) {
            if (str1.charAt(str1.length() - i) == str2.charAt(str2.length() - i)) {
                commonSuffixLength++;
            } else {
                break;
            }
        }
        
        // If we have significant prefix/suffix match, give bonus points
        int totalCommon = commonPrefixLength + commonSuffixLength;
        int maxLength = Math.max(str1.length(), str2.length());
        double prefixSuffixRatio = totalCommon / (double) maxLength;
        
        if (prefixSuffixRatio > 0.3) {  // More than 30% common prefix/suffix
            // Combine with Levenshtein for better accuracy
            int distance = levenshteinDistance(str1, str2);
            double levenshteinSimilarity = 1.0 - (distance / (double) maxLength);
            
            // Weighted average: 40% prefix/suffix, 60% Levenshtein
            double combinedScore = (0.4 * prefixSuffixRatio) + (0.6 * levenshteinSimilarity);
            
            log.debug("Prefix/Suffix match: '" + str1 + "' vs '" + str2 + "' - " +
                     "prefix: " + commonPrefixLength + ", suffix: " + commonSuffixLength + 
                     ", combined score: " + String.format("%.2f", combinedScore));
            
            return combinedScore;
        }
        
        // Fallback to pure Levenshtein distance
        int distance = levenshteinDistance(str1, str2);
        
        if (maxLength == 0) return 1.0;
        
        return 1.0 - (distance / (double) maxLength);
    }
    
    /**
     * Calculate Levenshtein distance (edit distance) between two strings
     */
    private int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        
        int[][] dp = new int[len1 + 1][len2 + 1];
        
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[len1][len2];
    }
    
    /**
     * Helper class to store scored elements
     */
    private static class ScoredElement {
        WebElement element;
        String actualValue;
        double score;
        
        ScoredElement(WebElement element, String actualValue, double score) {
            this.element = element;
            this.actualValue = actualValue;
            this.score = score;
        }
    }
    
    /**
     * Strategy 12: Semantic/Context-aware matching (SUPER AGGRESSIVE)
     * Extracts keywords from the locator and searches for ANY element that matches the intent.
     * Examples:
     * - //*[@href='#/create/INCPV'] → searches for elements containing "create", "inc", "incident"
     * - //*[@id='submit-xyz'] → searches for submit buttons
     */
    private HealingResult trySemanticContextMatching(String locator) {
        try {
            log.info("=== STRATEGY 12: Semantic Context Matching (AGGRESSIVE) ===");
            log.info("Analyzing locator for semantic clues: " + locator);
            
            // Extract all meaningful keywords from the locator
            Set<String> keywords = extractKeywords(locator);
            log.info("📌 Extracted keywords: " + keywords);
            
            if (keywords.isEmpty()) {
                log.warn("⚠️ No keywords extracted from locator");
                return new HealingResult(false, null, null, null);
            }
            
            // Build search patterns for common elements
            List<String> searchPatterns = new ArrayList<>();
            
            // Check if this is for creating something
            boolean isCreateAction = keywords.contains("create") || keywords.contains("new") || keywords.contains("add");
            
            // Check if this is for incident/ticket
            boolean isIncident = keywords.contains("inc") || keywords.contains("incident") || keywords.contains("ticket") || keywords.contains("incpv");
            
            // Check if this is for workorder
            boolean isWorkorder = keywords.contains("work") || keywords.contains("workorder") || keywords.contains("order");
            
            // Check if this is for change
            boolean isChange = keywords.contains("change") || keywords.contains("chg");
            
            // Build intelligent search patterns based on context
            if (isCreateAction && isIncident) {
                log.info("🎯 Detected intent: CREATE INCIDENT");
                searchPatterns.add("//*[contains(translate(@href, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incident')]");
                searchPatterns.add("//*[contains(translate(@href, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'inc')]");
                searchPatterns.add("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incident')]");
                searchPatterns.add("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incident')]");
            } else if (isCreateAction && isWorkorder) {
                log.info("🎯 Detected intent: CREATE WORKORDER");
                searchPatterns.add("//*[contains(translate(@href, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'workorder')]");
                searchPatterns.add("//*[contains(translate(@href, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'work')]");
            } else if (isCreateAction && isChange) {
                log.info("🎯 Detected intent: CREATE CHANGE");
                searchPatterns.add("//*[contains(translate(@href, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'change')]");
            }
            
            // Generic patterns for each keyword (only if specific context not detected)
            if (searchPatterns.isEmpty()) {
                for (String keyword : keywords) {
                    if (keyword.length() >= 3) {  // Skip very short keywords
                        searchPatterns.add("//*[contains(translate(@href, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + keyword.toLowerCase() + "')]");
                        searchPatterns.add("//*[@href and contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + keyword.toLowerCase() + "')]");
                    }
                }
            }
            
            log.info("🔍 Trying " + searchPatterns.size() + " semantic search patterns...");
            
            // Try each pattern
            for (String pattern : searchPatterns) {
                try {
                    By searchBy = By.xpath(pattern);
                    List<WebElement> candidates = driver.findElements(searchBy);
                    
                    log.info("Pattern: " + pattern + " → Found " + candidates.size() + " elements");
                    
                    for (WebElement element : candidates) {
                        try {
                            // Check if element is clickable (visible & enabled)
                            if (element.isDisplayed() && element.isEnabled()) {
                                String tagName = element.getTagName();
                                String href = element.getAttribute("href");
                                String text = element.getText();
                                
                                log.info("✅ Found candidate: tag=" + tagName + 
                                        ", href=" + href + 
                                        ", text=" + (text != null && text.length() > 50 ? text.substring(0, 50) + "..." : text));
                                
                                // Build healed locator from element
                                String healedLocator = buildLocatorFromElement(element);
                                
                                log.info("🎯 SEMANTIC MATCH! Intent matched, healed locator: " + healedLocator);
                                
                                return new HealingResult(true, element, healedLocator, 
                                                        "Semantic Context Match (intent: " + 
                                                        (isCreateAction ? "create " : "") + 
                                                        (isIncident ? "incident" : isWorkorder ? "workorder" : isChange ? "change" : "action") + ")");
                            }
                        } catch (Exception e) {
                            // Element not interactable, try next
                            log.debug("Element not interactable: " + e.getMessage());
                            continue;
                        }
                    }
                } catch (Exception e) {
                    log.debug("Pattern failed: " + pattern + " - " + e.getMessage());
                }
            }
            
            log.warn("⚠️ No elements found matching semantic context");
            
        } catch (Exception e) {
            log.error("❌ Semantic context matching failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return new HealingResult(false, null, null, null);
    }
    
    /**
     * Extract meaningful keywords from a locator string
     */
    private Set<String> extractKeywords(String locator) {
        Set<String> keywords = new HashSet<>();
        
        // Normalize and extract all words
        String normalized = locator.toLowerCase()
                .replaceAll("[^a-z0-9]+", " ")  // Replace non-alphanumeric with spaces
                .trim();
        
        String[] words = normalized.split("\\s+");
        
        // Filter out noise words and very short words
        Set<String> noiseWords = new HashSet<>(Arrays.asList(
            "by", "xpath", "css", "id", "name", "class", "selector", "contains", 
            "and", "or", "not", "ux", "the", "a", "an", "to", "of", "for"
        ));
        
        for (String word : words) {
            if (word.length() >= 2 && !noiseWords.contains(word)) {
                keywords.add(word);
                
                // Also add common abbreviation expansions
                if (word.equals("inc") || word.equals("incpv")) keywords.add("incident");
                if (word.equals("btn")) keywords.add("button");
                if (word.equals("nav")) keywords.add("navigation");
                if (word.equals("chg")) keywords.add("change");
                if (word.equals("wo")) keywords.add("workorder");
            }
        }
        
        return keywords;
    }
    
    /**
     * Build a new locator from an element's attributes
     */
    private String buildLocatorFromElement(WebElement element) {
        try {
            String tagName = element.getTagName();
            String id = element.getAttribute("id");
            String href = element.getAttribute("href");
            String name = element.getAttribute("name");
            String className = element.getAttribute("class");
            
            // Prefer href for links
            if (href != null && !href.isEmpty() && href.contains("#")) {
                // Extract hash part for SPA links
                String hashPart = href.substring(href.indexOf("#"));
                return "By.xpath: //*[@href='" + hashPart + "']";
            }
            
            // Then ID
            if (id != null && !id.isEmpty()) {
                return "By.id: " + id;
            }
            
            // Then name
            if (name != null && !name.isEmpty()) {
                return "By.name: " + name;
            }
            
            // Then class (first class only)
            if (className != null && !className.isEmpty()) {
                String firstClass = className.split("\\s+")[0];
                return "By.cssSelector: " + tagName + "." + firstClass;
            }
            
            // Fallback to tag name
            return "By.tagName: " + tagName;
            
        } catch (Exception e) {
            return "By.xpath: //*";  // Fallback
        }
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Extract XPath value from By locator string
     */
    private String extractXPathValue(String locator) {
        if (locator.startsWith("By.xpath: ")) {
            return locator.replace("By.xpath: ", "").trim();
        } else if (locator.startsWith("xpath: ")) {
            return locator.replace("xpath: ", "").trim();
        }
        return locator;
    }
    
    /**
     * Build XPath from element attributes
     */
    private String buildXPathFromElement(WebElement element, String tagName) {
        String id = element.getAttribute("id");
        String className = element.getAttribute("class");
        String name = element.getAttribute("name");
        
        if (id != null && !id.isEmpty()) {
            return "//" + tagName + "[@id='" + id + "']";
        } else if (name != null && !name.isEmpty()) {
            return "//" + tagName + "[@name='" + name + "']";
        } else if (className != null && !className.isEmpty()) {
            return "//" + tagName + "[contains(@class, '" + className.split(" ")[0] + "')]";
        }
        
        return "//" + tagName;
    }
    
    /**
     * Convert CSS selector to XPath (basic conversion)
     */
    private String convertCssToXPath(String css) {
        try {
            // Simple CSS to XPath conversion
            String xpath = css;
            
            // Replace # (ID) with [@id='']
            xpath = xpath.replaceAll("#([a-zA-Z0-9_-]+)", "*[@id='$1']");
            
            // Replace . (class) with [contains(@class,'')]
            xpath = xpath.replaceAll("\\.([a-zA-Z0-9_-]+)", "*[contains(@class,'$1')]");
            
            // Add // at the beginning if not present
            if (!xpath.startsWith("//")) {
                xpath = "//" + xpath;
            }
            
            return xpath;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Convert string representation back to By object
     */
    private By convertStringToBy(String locatorString) {
        if (locatorString.startsWith("By.xpath: ")) {
            return By.xpath(locatorString.replace("By.xpath: ", ""));
        } else if (locatorString.startsWith("By.id: ")) {
            return By.id(locatorString.replace("By.id: ", ""));
        } else if (locatorString.startsWith("By.cssSelector: ")) {
            return By.cssSelector(locatorString.replace("By.cssSelector: ", ""));
        } else if (locatorString.startsWith("By.className: ")) {
            return By.className(locatorString.replace("By.className: ", ""));
        } else if (locatorString.startsWith("By.name: ")) {
            return By.name(locatorString.replace("By.name: ", ""));
        }
        
        // Default to XPath
        return By.xpath(locatorString);
    }
    
    /**
     * Inner class to hold healing result
     */
    public static class HealingResult {
        private boolean healed;
        private WebElement element;
        private String healedLocator;
        private String strategy;
        
        public HealingResult(boolean healed, WebElement element, String healedLocator, String strategy) {
            this.healed = healed;
            this.element = element;
            this.healedLocator = healedLocator;
            this.strategy = strategy;
        }
        
        public boolean isHealed() {
            return healed;
        }
        
        public WebElement getElement() {
            return element;
        }
        
        public String getHealedLocator() {
            return healedLocator;
        }
        
        public String getStrategy() {
            return strategy;
        }
    }
}

