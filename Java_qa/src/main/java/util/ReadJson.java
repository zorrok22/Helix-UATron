package util;
	
/** *********************************************************************
 	Purpose:		This class will read all data from json file
	File Name:		ReadJson.Java
	Suite Name:		Framework File
	Module Name:	NA
	@author			akarhadk
	@version		1.0
	@since			2025-04-18
	Comments:
 ********************************************************************** */

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.File;

public class ReadJson {

	public Map<Object, String> getInputValuesMapIncludingLabelField(String appName, String fileName, String inputArray) {

		JSONParser parser = new JSONParser();
		Map<Object, String> map = new LinkedHashMap<>();
		String label_Constant = "Label";
		String value_Constant = "Value";

		try {
			String filepath = System.getProperty("user.dir") + File.separator
					+ "resources" + File.separator + appName
					+ File.separator + fileName + ".json";

			Object obj = parser.parse(new FileReader(filepath));
			JSONObject jsonObject = (JSONObject) obj;

			JSONArray filterArray = (JSONArray) jsonObject.get(inputArray);
			Iterator<JSONObject> iterator = filterArray.iterator();

			while (iterator.hasNext()) {
				JSONObject factObj = iterator.next();

				String label = (String) factObj.get(label_Constant);
				Object valueObj = factObj.get(value_Constant);
				String valueStr;

				if (valueObj instanceof JSONArray) {
					JSONArray arr = (JSONArray) valueObj;
					// join array items into one string (customize separator if needed)
					valueStr = String.join(" ", (Iterable<String>) arr);
				} else {
					valueStr = (String) valueObj;
				}

				map.put(label, valueStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	public String getAssignedDefect(String methodName, String appName, String fileName, String inputArray) {
		String defectID = null;
		JSONParser parser = new JSONParser();
		Map<Object, String> map = new LinkedHashMap<Object, String>();
		String label_Constant = "Label";
		String value_Constant = "Value";
		try {
			String filepath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + appName + File.separator + fileName + ".json";
			Object obj = parser.parse(
					new FileReader(filepath));
			JSONObject jsonObject = (JSONObject) obj;
			int fname = jsonObject.size();
			JSONArray filterArray = (JSONArray) jsonObject.get(inputArray);
			Iterator<JSONObject> iterator = filterArray.iterator();
			while (iterator.hasNext()) {
				//  String parseVal = null;
				JSONObject factObj = (JSONObject) iterator.next();
				String label = factObj.get(label_Constant).toString();
				if (methodName.contains(label)) {
					defectID = factObj.get(value_Constant).toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return defectID;

	}

	public JSONObject readSimpleJson(String filePath) {
		JSONObject jsonObject = null;
		try {
			String fullFilepath = System.getProperty("user.dir") + File.separator + filePath;
			JSONParser parser = new JSONParser();
			FileReader reader = new FileReader(fullFilepath);
			Object obj = parser.parse(reader);
			jsonObject = (JSONObject) obj;
			return jsonObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	//it will read json input array and return json content as per Key
	public String readJsonInputArray(String filePath, String inputArray) {
		JSONObject jsonObject = null;
		String output = null;
		try {
			JSONParser parser = new JSONParser();
			FileReader reader = new FileReader(filePath);
			Object obj = parser.parse(reader);
			jsonObject = (JSONObject) obj;
			int fname = jsonObject.size();
			JSONArray filterArray = (JSONArray) jsonObject.get(inputArray);
			Gson gson = new Gson();
			output = gson.toJson(filterArray);
			return output;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

}