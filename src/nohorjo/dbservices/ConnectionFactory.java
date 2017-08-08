package nohorjo.dbservices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import nohorjo.system.SystemProperties;

public class ConnectionFactory {
	public static Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://"+SystemProperties.get("dburl")+"/"+SystemProperties.get("dbname");
		String user = SystemProperties.get("dbuser");
		String password = SystemProperties.get("dbpassword");
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DriverManager.getConnection(url, user, password);
	}
}
