package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import setting.LogController;

public class DBManager {
	//private static final String jdbcName = "jdbc/eForm";
//	private final static String DB_DOMAIN = Utility.getProperty("host");
//	private final static String DB_DATABASENAME = Utility.getProperty("DB_DATABASENAME");
//	private final static String DB_USER = Utility.getProperty("db_user");
//	private final static String DB_PASSWORD = Utility.getProperty("db_password");
	private static Connection conn;
	

	
	public static void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			LogController.writeMessage(LogController.ERROR, "DBManager", "closeConnection", e.getMessage());
			LogController.writeExceptionMessage(LogController.DEBUG, e);
		}
	}
	
	
	 public static Connection makeConnection() {
		 
		try {
			LogController.writeMessage(LogController.DEBUG, "Starts to connect");
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			
			//WMG
			String db_name = new String(Utility.getProperty("db_database_name"));
			String url = "jdbc:sqlserver://" + Utility.getProperty("host") + 
	    			";databaseName=" + db_name +";integratedSecurity=true;";
			
			LogController.writeMessage(LogController.DEBUG, url);
			conn = DriverManager.getConnection(url);
			
			LogController.writeMessage(LogController.DEBUG, "DBManager", "makeConnection", "Success to make connection to database");
		} catch (Exception e) {
			LogController.writeMessage(LogController.ERROR, "DBManager", "makeConnection", e.getMessage());
			LogController.writeExceptionMessage(LogController.DEBUG, e);
		}
		return conn;
	}
	/*
	public static Connection makeConnection() {
		try {
			InitialContext context = new InitialContext();
			DataSource dataSource = (DataSource) context.lookup(jdbcName);
			return dataSource.getConnection();
		} catch (Exception e) {
			logger.debug("makeConnection - e: " + e);
			return null;
		}
	}*/

}
