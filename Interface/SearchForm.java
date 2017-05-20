import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


/**
 * Servlet implementation class SearchForm
 */
@WebServlet("/SearchForm")
public class SearchForm extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// Set response content type
			response.setContentType("text/html");
			DB db = new DB();

			String searchInput = request.getParameter("SearchBox");
			System.out.print(searchInput);
			String page = request.getParameter("page");
			if(page == null) { 
				String sql = "INSERT IGNORE INTO `livesearch`(`Query`) VALUES ('"+ searchInput +"')";
				PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.execute();

				page = "1";
			}
			ArrayList<Document> myd = new ArrayList<Document>();
			ArrayList<String> myurls = new ArrayList<String>();
			ArrayList<ArrayList<String>> urldoc = new ArrayList<ArrayList<String>>();
			QueryProcessing myq = new QueryProcessing();
			urldoc = myq.GetDocument(searchInput);

			if(urldoc.size()!=0)
			{
				double count = urldoc.size();
				double limit = 10;
				if(Math.ceil(count/10) == Integer.parseInt(page)) 
					limit = (count - (Math.ceil(count/10)-1)*10);
				for(int i=0;i<limit;i++)
				{
					myurls.add(urldoc.get((Integer.parseInt(page) - 1) * 10 +i).get(0));
					myd.add(Jsoup.parse(urldoc.get((Integer.parseInt(page) - 1) * 10 + i).get(1)));
				}	
				/////////////////////////////////////////////////////////

				PrintWriter out = response.getWriter();
				String title = "Results for \""+searchInput+"\"";
				String docType ="<!doctype html public \"-//w3c//dtd html 5.0 " + "transitional//en\">\n";
				String tout=docType +
						"<html>\n" +" <style>div {font-size: 150%; text-align: center; } body {  background-image: url(\"https://wallpaperbrowse.com/media/images/background-2.jpg\");}</style> "+
						"<head><title>" + title + "</title></head>\n" +
						"<body bgcolor=\"#f0f0f0\">\n" +
						"<h1 align=\"center\">" + title + "</h1>\n";
				for(int i=0; i<myd.size(); i++) {
					String snippets="";
					tout+="<b>";
					tout += "<a href=";
					tout += myurls.get(i);
					tout+=">";

					Elements tit = myd.get(i).select("title");
					for(Element p:tit)
					{
						tout+=p.text();
					}

					tout+= "</a></p>";
					tout+="</b>";
					String Desc = myd.get(i).select("meta[name=description]").attr("content");
					String Description[] = {};
					Description = Desc.split("\\.");

					for(int k = 0; k< Description.length; k++) {
						String [] s = {};
						if(Description[k].contains(" " + searchInput + " ")) {
							s = Description[k].split(" ");
							for(String it:s) {
								if(it.equals(searchInput)) {
									snippets+="<b>";
									snippets += it;
									snippets += " ";
									snippets+="</b>";
								}else {
									snippets += it;
									snippets += " ";
								}
							}


							snippets+="...";

						}
					}

					Elements e=myd.get(i).select("p");
					String body = "";
					for(Element p:e)
					{
						body+=p.text();

					}
					String[] bod = body.split("\\.");
					for(int z= 0; z<bod.length; z++) {
						String [] s = {};
						if(bod[z].contains(" " + searchInput + " ")) {
							s = bod[z].split(" ");
							for(String it:s) {
								if(it.equals(searchInput)) {
									snippets+="<b>";
									snippets += it;
									snippets += " ";
									snippets+="</b>";
								}else {
									snippets += it;
									snippets += " ";
								}
							}


							snippets+="...";

						}
					}

					if(snippets.equals("") || snippets.equals(" ")) {
						String desc = myd.get(i).select("meta[name=description]").attr("content");
						if(desc.length() < 2) {
							
							Elements ee=myd.get(i).select("p");
							int c =0;
							for(Element p:ee)
							{
								if(c<1) {
								tout+=p.text();
								}
								c++;
							}
							tout+="...";
						}else {
							tout += desc;
						}
					}
					else {
						String[] str = snippets.split("\\.\\.\\.");
						int counter = str.length;
						for(int ii =0; ii < 3; ii++) {
							if(ii < counter) {tout += str[ii];
							tout += "...";
							}
						}
					}
					tout+="<br> <br> <br>";
				}


				tout+= "</body></html>";
				
				tout += "<div>";
				for(int i =0; i < Math.ceil(count/10); i++ ) {
					if(i+1 == Integer.parseInt(page)) tout+= "   " + Integer.parseInt(page);
					else {
						
						tout+= "<a href='SearchForm?page=" + (i + 1) + "&SearchBox=" + searchInput + "'>"+ "   " + (i + 1) + "</a> ";
					}

				}
				tout += "</div>";
				out.println(tout);
			}
			else
			{
				PrintWriter out = response.getWriter();
				String title = "No search results found for \""+searchInput+"\"";
				String docType ="<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
				String tout=docType +
						"<html>\n" +
						"<head><title> No search results found</title></head>\n" +
						"<body bgcolor=\"#f0f0f0\">\n" +
						"<h1 align=\"center\">" + title + "</h1>\n";
				tout+= "</body></html>";
				out.println(tout);
			}
		}

		catch(SQLException e){
			e.printStackTrace();
		}
	}
	// Method to handle POST method request.
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		doGet(request, response);
	}

}
