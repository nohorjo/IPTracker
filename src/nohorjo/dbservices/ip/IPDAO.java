package nohorjo.dbservices.ip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import nohorjo.dbservices.ConnectionFactory;

public class IPDAO {

	public void recordAccess(String remote_address, String session_id, String cookie_id, boolean visitedBefore,
			int urlid, String isMobile) throws SQLException {
		String vb = "NO";
		if (!visitedBefore) {
			String sql = "SELECT IF(COUNT(*)>0, 'YES', 'NO') AS visited_before FROM ip_addresses WHERE remote_address=?";
			try (Connection conn = ConnectionFactory.getConnection();
					PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, remote_address);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						vb = rs.getString("visited_before");
					}
				}
			}
		} else {
			vb = "YES";
		}

		String sql = "INSERT INTO ip_addresses (remote_address, session_id, cookie_id, visited_before, urlid, is_mobile) VALUES (?,?,?,?,?,?)";
		try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, remote_address);
			ps.setString(2, session_id);
			ps.setString(3, cookie_id);
			ps.setString(4, vb);
			ps.setInt(5, urlid);
			ps.setString(6, isMobile);
			ps.execute();
		}
	}
}
