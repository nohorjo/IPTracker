package nohorjo.dbservices.url;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import nohorjo.dbservices.ConnectionFactory;

public class URLDAO {

	public int setUrl(String url, String title) throws SQLException {
		String sql = "INSERT INTO urls (url,title) VALUES (?,?)";
		try (Connection conn = ConnectionFactory.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, url);
			ps.setString(2, title);
			if (ps.executeUpdate() != 0) {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						return rs.getInt(1);
					}
				}
			}
		}
		throw new SQLException("Could not perform INSERT");
	}

	public String[] getUrl(int id) throws SQLException {
		String sql = "SELECT url, title FROM urls WHERE id = ?";
		try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				String[] urlData = new String[2];
				if (rs.next()) {
					urlData[0] = rs.getString("url");
					urlData[1] = rs.getString("title");
					return urlData;
				}
			}
		}
		return null;
	}
}
