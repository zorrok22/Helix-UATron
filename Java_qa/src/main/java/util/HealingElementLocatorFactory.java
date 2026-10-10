package util;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;

/**
 * Factory for creating HealingElementLocator instances.
 * This factory is used by PageFactory to create locators with self-healing capabilities.
 * 
 * @author SolutionsQA Team
 * @version 1.0
 * @since 2025-01-07
 */
public class HealingElementLocatorFactory implements ElementLocatorFactory {
    
    private final SearchContext searchContext;
    private final String testName;
    
    /**
     * Constructor
     * @param searchContext WebDriver to use for finding elements
     * @param testName Current test name for tracking healings
     */
    public HealingElementLocatorFactory(SearchContext searchContext, String testName) {
        this.searchContext = searchContext;
        this.testName = testName;
    }
    
    /**
     * Create an ElementLocator for the given field
     * @param field Field with @FindBy annotation
     * @return HealingElementLocator that provides self-healing
     */
    @Override
    public ElementLocator createLocator(Field field) {
        return new HealingElementLocator(searchContext, field, testName);
    }
}

