package util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/** *********************************************************************
Purpose:		Inserting automation results to postgres db
Suite Name:		Framework File
Module Name:	NA
@author			akarhadk
@version		1.0
@since			2025-04-18
Comments:		
 ********************************************************************** */

public class InsertMultipleRecords {

	public static Logger log = Logger.getLogger(Listener.class.getName());

	protected static Map<String, String> configProp;
	public static final String USERDIR = System.getProperty("user.dir");
	private static String postgresTableName = "";
	private static String postgresUrl = "";
	private static String postgresUser = "";
	private static String postgresPwd = "";
	private static String postgresDatabase = "";

	/**
	 *  Adding static block for adding defect.json into database at one time only.
	 *  Note: We have hard coded postgresTableName=defect, to avoid extra overhead in config file.
	 */
	static {
		configProp = ReadPropertiesFile.readProperties(USERDIR + File.separator + "properties.config");
		postgresTableName = "defect";
		postgresDatabase = configProp.get("postgresDatabase");
		postgresUrl = configProp.get("postgresUrl");
		postgresUser = configProp.get("postgresUser");
		postgresPwd = configProp.get("postgresPwd");
		postgresUrl = postgresUrl + postgresDatabase;
		ReadJson readJson = new ReadJson();
		Map<Object, String> map = readJson.getInputValuesMapIncludingLabelField("DEFECTS", "Defect_List",
				"ActiveDefectList");
		String INSERT_AUTOMATION_DATA_SQL = "INSERT INTO " + postgresTableName + " (key, values) VALUES " + " (?, ? );";
		try (Connection conn = DriverManager.getConnection(postgresUrl, postgresUser, postgresPwd);
				PreparedStatement statement = conn.prepareStatement(INSERT_AUTOMATION_DATA_SQL)) {
			// To delete existing data from table because Defect.json is static and doesn't
			// change at run time.
			// If we don't delete then unnecessarily adding duplicate data each time.
			String sql = "Delete from " + postgresTableName + ";";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.executeUpdate();

			Iterator<Entry<Object, String>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Object, String> pair = it.next();
				System.out.println(pair.getKey() + " = " + pair.getValue());
				System.out.println(pair.getValue().toString().substring(pair.getValue().toString().lastIndexOf("/") + 1));
				statement.setString(1, pair.getKey().toString());
				statement.setString(2, pair.getValue().toString().substring(pair.getValue().toString().lastIndexOf("/") + 1));
				statement.executeUpdate();
			}

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}

	}

	public static void insertData(String[] targetArray) {

		configProp = ReadPropertiesFile.readProperties(USERDIR + File.separator + "properties.config");
		postgresTableName = configProp.get("postgresTableName");
		postgresDatabase = configProp.get("postgresDatabase");
		postgresUrl = configProp.get("postgresUrl");
		postgresUser = configProp.get("postgresUser");
		postgresPwd = configProp.get("postgresPwd");
		postgresUrl = postgresUrl + postgresDatabase;
		String INSERT_AUTOMATION_DATA_SQL = "INSERT INTO " + postgresTableName
				+ " (time, testclass, NAME, description, result, suite, errorMessage, productVersion, productName, jenkinJobId, authorName, jenkinJobName, setupName, serviceName, deployment_job_id, zdtsuite_execution) VALUES "
				+ " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

		// TODO Auto-generated method stub
		try (Connection conn = DriverManager.getConnection(postgresUrl, postgresUser, postgresPwd);
				PreparedStatement statement = conn.prepareStatement(INSERT_AUTOMATION_DATA_SQL);) {

			Calendar cal = Calendar.getInstance();
			Timestamp timestamp = new Timestamp(cal.getTimeInMillis());

			statement.setTimestamp(1, timestamp);
			statement.setString(2, targetArray[1]);
			statement.setString(3, targetArray[2]);
			statement.setString(4, targetArray[3]);
			statement.setString(5, targetArray[4]);
			statement.setString(6, targetArray[5]);
			statement.setString(7, targetArray[6]);
			statement.setString(8, targetArray[7]);
			statement.setString(9, targetArray[8]);
			statement.setString(10, targetArray[9]);
			statement.setString(11, targetArray[10]);
			statement.setString(12, targetArray[11]);
			statement.setString(13, targetArray[12]);
			statement.setString(14, targetArray[13]);
			statement.setString(15, targetArray[14]);
			statement.setString(16, targetArray[15]);
			System.out.println("***Inserting statement***");
			statement.executeUpdate();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}
}
