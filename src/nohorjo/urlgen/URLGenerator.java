package nohorjo.urlgen;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nohorjo.dbservices.url.URLDAO;
import nohorjo.http.HttpOperation;

public class URLGenerator extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7659278004019337652L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int id = -1;
		try {
			String url = req.getParameter("url");
			HttpOperation http = new HttpOperation();
			String title = req.getParameter("title");
			URLDAO dao = new URLDAO();

			if (!url.startsWith("http")) {
				url = "http://" + url;
			}

			if (title == null || title.equals("")) {
				try {
					Matcher m = Pattern.compile("<title>.*</title>").matcher(http.doGet(url).replace("\n", ""));
					if (m.find()) {
						title = m.group().replace("<title>", "").replace("</title>", "");
					}
				} catch (IOException e) {
					e.printStackTrace();
					title = "Loading...";
				}
			}
			id = dao.setUrl(url, title);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		resp.setContentType("text/plain");
		resp.getWriter().println("http://tinyurl.com/hbf2qp7?urlid=" + id);
	}

}
