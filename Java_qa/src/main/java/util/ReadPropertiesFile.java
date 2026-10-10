	package util;
	
	/** *********************************************************************
	Purpose:		Read config properties file
	File Name:		ReadPropertiesFile.Java
	Suite Name:		Framework File
	Module Name:	NA
	@author			akisore
	@version		1.0
	@since			2025-04-15
	Comments:		
	 ********************************************************************** */
	
	import org.apache.log4j.Logger;
	
	import java.io.*;
	import java.util.LinkedHashMap;
	import java.util.Map;
	import java.util.Properties;
	
	public class ReadPropertiesFile {
		static Logger log = Logger.getLogger(ReadPropertiesFile.class.getName());
	
	public static Map<String, String> readProperties(String fileLocation) {
		log.debug("Parsing properties file ...");
		Map<String, String> properties = new LinkedHashMap<String, String>();
		try {
			FileReader reader = new FileReader(fileLocation);
			Properties p = new Properties();
			p.load(reader);
			reader.close();
			
			// First pass: Load all properties as-is
			for (String key : p.stringPropertyNames()) {
				properties.put(key, p.getProperty(key));
			}
			
			// Second pass: Resolve variables (${variable_name}) in property values
			Map<String, String> resolvedProperties = new LinkedHashMap<String, String>();
			boolean hasChanges = false;
			
			for (String key : properties.keySet()) {
				String originalValue = properties.get(key);
				if (originalValue != null) {
					String resolvedValue = resolveVariables(originalValue, properties);
					resolvedProperties.put(key, resolvedValue);
					
					// Check if value was actually resolved (changed)
					if (!originalValue.equals(resolvedValue)) {
						hasChanges = true;
					}
				}
			}
			
			// Third pass: If variables were resolved, write back to file
			if (hasChanges) {
				writeResolvedPropertiesToFile(fileLocation, resolvedProperties);
			}
			
			return resolvedProperties;
		} catch (FileNotFoundException e) {
			log.error("File not found" + e.getMessage());
			log.debug(e);
		} catch (IOException e) {
			log.error("Failed to read properties file" + e.getMessage());
			log.debug(e);
		} catch (Exception e) {
			log.error("Failed to read properties file" + e.getMessage());
			log.debug(e);
		}
		return properties;
	}
	
	/**
	 * Resolves variables in the format ${variable_name} using values from the properties map
	 * @param value The string that may contain variables
	 * @param properties The map of property key-value pairs
	 * @return The resolved string with variables replaced
	 */
	private static String resolveVariables(String value, Map<String, String> properties) {
		if (value == null || !value.contains("${")) {
			return value;
		}
		
		String result = value;
		int maxIterations = 10; // Prevent infinite loops
		int iteration = 0;
		
		// Keep resolving until no more variables are found or max iterations reached
		while (result.contains("${") && iteration < maxIterations) {
			int startIdx = result.indexOf("${");
			int endIdx = result.indexOf("}", startIdx);
			
			if (endIdx == -1) {
				break; // Malformed variable, stop resolving
			}
			
			String varName = result.substring(startIdx + 2, endIdx);
			String varValue = properties.get(varName);
			
			// Check for circular reference (variable references itself)
			if (varValue != null && varValue.equals("${" + varName + "}")) {
				log.warn("⚠ Circular reference detected for variable: " + varName);
				log.warn("  Value is: " + varValue);
				log.warn("  This variable references itself! Please set an actual value.");
				// Leave it as-is to avoid infinite loop
				break;
			}
			
			if (varValue != null) {
				result = result.substring(0, startIdx) + varValue + result.substring(endIdx + 1);
			} else {
				// Variable not found, leave it as-is and move past it
				log.warn("⚠ Variable not found: " + varName + " in value: " + value);
				break;
			}
			
			iteration++;
		}
		
		// Warn if we hit max iterations (possible infinite loop)
		if (iteration >= maxIterations && result.contains("${")) {
			log.warn("⚠ Maximum iteration limit reached while resolving: " + value);
			log.warn("  This might indicate a circular reference or complex nested variables.");
		}
		
		return result;
	}
	
	/**
	 * Writes resolved properties back to the properties file, replacing variables with actual values
	 * @param fileLocation The path to the properties file
	 * @param resolvedProperties The resolved properties with actual values
	 */
	private static void writeResolvedPropertiesToFile(String fileLocation, Map<String, String> resolvedProperties) {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		File tempFile = null;
		
		try {
			// Create a temporary file
			File originalFile = new File(fileLocation);
			tempFile = new File(fileLocation + ".tmp");
			
			reader = new BufferedReader(new FileReader(originalFile));
			writer = new BufferedWriter(new FileWriter(tempFile));
			
			String line;
			while ((line = reader.readLine()) != null) {
				String trimmedLine = line.trim();
				
				// Check if this is a property line (not a comment or empty line)
				if (!trimmedLine.isEmpty() && !trimmedLine.startsWith("#") && trimmedLine.contains("=")) {
					// Parse the property key
					int equalIndex = trimmedLine.indexOf("=");
					String key = trimmedLine.substring(0, equalIndex).trim();
					
					// Check if we have a resolved value for this key
					if (resolvedProperties.containsKey(key)) {
						String resolvedValue = resolvedProperties.get(key);
						// Write the resolved value
						writer.write(key + "=" + resolvedValue);
						writer.newLine();
					} else {
						// Keep the original line
						writer.write(line);
						writer.newLine();
					}
				} else {
					// Keep comments, empty lines, and section headers as-is
					writer.write(line);
					writer.newLine();
				}
			}
			
			reader.close();
			writer.close();
			
			// Replace original file with temp file
			if (originalFile.delete()) {
				if (tempFile.renameTo(originalFile)) {
					log.info("✓ Properties file updated with resolved URLs: " + fileLocation);
					log.info("  STACK_NAME: " + resolvedProperties.get("STACK_NAME"));
					log.info("  HOST_DOMAIN: " + resolvedProperties.get("HOST_DOMAIN"));
					log.info("  DWPCatalog_URL: " + resolvedProperties.get("DWPCatalog_URL"));
					log.info("  adminUserName: " + resolvedProperties.get("adminUserName"));
				} else {
					log.warn("Could not rename temp file to original file");
					tempFile.delete();
				}
			} else {
				log.warn("Could not delete original file for replacement");
				tempFile.delete();
			}
			
		} catch (IOException e) {
			log.warn("Could not write resolved properties to file: " + e.getMessage());
			// Clean up temp file if it exists
			if (tempFile != null && tempFile.exists()) {
				tempFile.delete();
			}
		} finally {
			try {
				if (reader != null) reader.close();
				if (writer != null) writer.close();
			} catch (IOException e) {
				log.warn("Error closing file streams: " + e.getMessage());
			}
		}
	}
	}
