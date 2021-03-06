package util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import setting.LogController;

public class CommonUtil {
	
	//Get property    
	public static String getProperty(Properties properties, String key) {
        final String tmepVal = properties.getProperty(key);
        if (ckValueNull(tmepVal)) {
        	LogController.writeMessage("GetProperty Fail  : " + key);
            return null;
        }
        return tmepVal.trim();
    }
	
	public static String getProperty(Properties properties, String key, String defaultValue) {
		String val = getProperty(properties, key);
		return (val == null) ? defaultValue : val.trim();
	}
	
	public static int getProperty(Properties properties, String key, int defaultValue) {
		String val = getProperty(properties, key);
		return (val == null) ? defaultValue : (Integer.valueOf(val)).intValue();
	}
	
	public static boolean getProperty(Properties properties, String key, boolean defaultValue) {
		String val = getProperty(properties, key);
		return (val == null) ? defaultValue : (Boolean.valueOf(val)).booleanValue();
	}
	
	/*Check value NULL or not
	 * true = NULL
	 * false = Not NULL
	 */
	public static Boolean ckValueNull(String val) {
        return (val == null || "".equals(val) || val.length() < 0) ? true : false;
    }
	
	//Get Key' Name property
	public static ArrayList<String> getLoopingProperty(String keyName, Properties properties) {
		ArrayList<String> nameList = new ArrayList<String>();
		int i = 1;
		while(ckValueNull(properties.getProperty(keyName+i)) == false) {
			nameList.add(properties.getProperty(keyName+i));
			i++;
		}
		return nameList;
	}
	//Get Day
	public static String getyesterday(DateFormat dateFormat){
		Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -1);
		return dateFormat.format(c.getTime());
	}
	
	//Set prepareStmt dynamic
	public static PreparedStatement setPreparedStatment(Object obj, int step, PreparedStatement ps) throws SQLException{
		if (obj instanceof String) {
			ps.setString(step, (String)obj);
		} else if (obj instanceof Timestamp) {
			ps.setTimestamp(step, (Timestamp)obj);
		} else if (obj instanceof Number) {
			ps.setInt(step, (int)obj);
		} else {
			ps.setString(step, null);
		}
		return ps;
	}
	
	
	//Get ArrayList for single column
	public static ArrayList<String> getArrayListFromRS(ResultSet rs, String key) throws SQLException {
		ArrayList<String> tempArray = new ArrayList<String>();
		ArrayList<HashMap<String, String>> tempArray1 = getHashMapFromRS(rs);
		for(HashMap<String, String> map: tempArray1) {
			tempArray.add(map.get(key));
		}
		LogController.writeMessage("tempArray ?"+tempArray.size());
		return tempArray;
	}
	
	//Get HashMap for mutable column
	public static ArrayList<HashMap<String, String>> getHashMapFromRS(ResultSet rs) throws SQLException {
		ArrayList<HashMap<String, String>> rsArrayList = new ArrayList<HashMap<String, String>>();

		ResultSetMetaData metadata = rs.getMetaData();
		int columnCount = metadata.getColumnCount();
		ArrayList<String> columnNames = new ArrayList<>();
		for (int i = 1; i <= columnCount; i++) {
			String columnName = metadata.getColumnName(i);
			columnNames.add(columnName);
		}

		while (rs.next()) {
			HashMap<String, String> rsHashMap = new HashMap<String, String>();
			for (String columnName : columnNames) {
				String value = rs.getString(columnName);
				rsHashMap.put(columnName, value);
			}
			rsArrayList.add(rsHashMap);
		}
		return rsArrayList;
	}
	
	//SQL Results Checking
	public static int ckSQLExecute(int[] results) {
    	int result = 0;
    	for (int i = 0; i < results.length; i++) {
            if (results[i] >= 0) {
            	result += results[i];
            } else if (results[i] == Statement.SUCCESS_NO_INFO) {
            	LogController.writeMessage(LogController.DEBUG, "SQL SUCCESS_NO_INFO : " + results[i]);
            } else if (results[i] == Statement.EXECUTE_FAILED) {
            	LogController.writeMessage(LogController.ERROR, "SQL EXECUTE_FAILED : " + results[i]);
            }
        }
    	return result;
    }
}
