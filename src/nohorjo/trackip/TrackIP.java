package nohorjo.trackip;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nohorjo.dbservices.ip.IPDAO;
import nohorjo.dbservices.url.URLDAO;
import nohorjo.http.HttpOperation;

public class TrackIP extends HttpServlet {

	private static int numberOfErrorPages = 4;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2872416220225245253L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String ip = req.getRemoteAddr();
		HttpSession session = req.getSession(true);
		Cookie newCookie = new Cookie("cid", "C" + new Random().nextLong());
		int urlid = -1;
		boolean visitedBefore = false;
		String userAgent = req.getHeader("User-Agent");
		String isMobile = isMobile(userAgent);
		String redirectUrl;

		try {
			urlid = Integer.parseInt(req.getParameter("urlid"));
			String[] urlData = new URLDAO().getUrl(urlid);
			if (urlData == null) {
				urlData = new String[] { "/web/content", "Loading..." };
			}
			if (isFaceBook(ip)) {
				returnTitle(resp, urlData[1]);
				return;
			}
			redirectUrl = urlData[0];
		} catch (Exception e) {
			redirectUrl = "/error#.html".replace("#", "" + new Random().nextInt(numberOfErrorPages + 1));
		}

		newCookie.setMaxAge(315360000);
		visitedBefore = hasVisitedBefore(req, session, newCookie, visitedBefore);
		try {
			new IPDAO().recordAccess(ip, session.getId(), newCookie.getValue(), visitedBefore, urlid, isMobile);
		} catch (Exception e) {
			session.removeAttribute("visited");
			newCookie.setMaxAge(0);
			e.printStackTrace();
		}
		resp.addCookie(newCookie);
		if (isMobile.equals("false")) {
			if (urlid == -1) {
				getServletContext().getRequestDispatcher(redirectUrl).forward(req, resp);
			} else {
				resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
				resp.setHeader("Location", redirectUrl);

			}
		} else {
			resp.setContentType("text/html");
			resp.getWriter().write("<!DOCTYPE html><html><head></head><body><script>"
					+ "if((window.outerHeight/window.outerWidth)>1.3)alert('Try using a desktop. Mobile not supported');"
					+ "window.location.replace(window.location.origin+'/web'+'" + redirectUrl + "');"
					+ "</script></body></html>");
		}
	}

	private String isMobile(String userAgent) {
		String isMobile = userAgent == null ? "unknown"
				: Boolean.toString(userAgent.toLowerCase().matches(
						".*(android|blackberry|bolt|symbian|dorothy|fennec|gobrowser|iemobile|iris|maemo|mot|maemo|mot-l|minimo|netfront|opera mini|opera mobi|semc|skyfire|teashark|teleca|iphone).*"));
		return isMobile;
	}

	private boolean hasVisitedBefore(HttpServletRequest req, HttpSession session, Cookie newCookie,
			boolean visitedBefore) {
		if (session.getAttribute("visited") != null) {
			visitedBefore = true;
		} else {
			session.setAttribute("visited", new Object());
			for (Cookie cookie : req.getCookies()) {
				if (cookie.getName().equals("cid")) {
					visitedBefore = true;
					newCookie.setValue(cookie.getValue());
					break;
				}
			}
		}
		return visitedBefore;
	}

	private void returnTitle(HttpServletResponse resp, String title) throws IOException {
		resp.setContentType("text/html");
		resp.getWriter().write("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>" + title
				+ "</title></head><body></body></html>");
	}

	private boolean isFaceBook(String ip) throws IOException {
		return new HttpOperation().doGet("http://whatismyipaddress.com/ip/" + ip)
				.contains("<tr><th>Organization:</th><td>Facebook</td></tr>");
	}

}
