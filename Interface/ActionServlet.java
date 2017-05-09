import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ActionServlet
 */
@WebServlet("/ActionServlet")
public class ActionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			DB db = new DB();
			String name = request.getParameter("SearchBox");
			if(name != "") {
				String sql = "SELECT DISTINCT * FROM `livesearch` WHERE `Query` LIkE '" + name +"%'";
				ResultSet rs = db.runSql(sql);
				String result = "";
				response.setContentType("text/plain"); 
				response.setCharacterEncoding("UTF-8");
				while(rs.next()) {
					
					
					result += "<a href='SearchForm?SearchBox=" +rs.getString("Query") + "'>" + rs.getString("Query") +"</a>";
					result += " <br>";
				}
				response.getWriter().write(result);
			}
			

		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
