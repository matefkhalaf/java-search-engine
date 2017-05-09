import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
		String page = request.getParameter("page");
		if(page == null) { 
			String sql = "INSERT IGNORE INTO `livesearch`(`Query`) VALUES ('"+ searchInput +"')";
			PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.execute();
			
			page = "1";
		}
		
		ArrayList<Document> myd = new ArrayList<Document>();
		ArrayList<String> myurls = new ArrayList<String>();
		
//		QueryProcessing myq = new QueryProcessing();
//		myd = myq.GetDocument(searchInput);
//		for(int i =0; i< myd.size(); i++) {
//
//			String sql = "SELECT `Document`, URL FROM crawler where Document = " + myd.get(i).toString();
//			ResultSet rs = db.runSql(sql);
//			while(rs.next()) {
//				myurls.add(rs.getString("URL"));
//			}
//		}

		
		
		double count = 13;
		double limit = 10;
		if(Math.ceil(count/10) == Integer.parseInt(page)) limit = (count - (Math.ceil(count/10)-1)*10);
			String sql = "SELECT `Document`, URL FROM crawler LIMIT "+ (int)limit +" OFFSET " + (((Integer.parseInt(page) - 1) * 10));
			ResultSet rs = db.runSql(sql);
			while(rs.next()) {
					myurls.add(rs.getString("URL"));
					myd.add(Jsoup.parse(rs.getString("Document")));
			}
		
		
		/////////////////////////////////////////////////////////
		
		PrintWriter out = response.getWriter();
		String title = "Results for \""+searchInput+"\"";
		String docType ="<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
		String tout=docType +
				"<html>\n" +
				"<head><title>" + title + "</title></head>\n" +
				"<body bgcolor=\"#f0f0f0\">\n" +
				"<h1 align=\"center\">" + title + "</h1>\n";
		for(int i=0; i<myd.size(); i++) {
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
			String desc = myd.get(i).select("meta[name=description]").attr("content");
			//If no description in document found, display body
			if(desc.length() < 2) {
				
				Elements e=myd.get(i).select("p");
				int c =0;
				for(Element p:e)
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
			tout+="<br> <br> <br>";
		}
		tout+= "</body></html>";

		for(int i =0; i < Math.ceil(count/10); i++ ) {
			if(i+1 == Integer.parseInt(page)) out.print(Integer.parseInt(page));
			else out.print("<a href='SearchForm?page=" + (i + 1) + "&SearchBox=" + searchInput + "'>"+ (i + 1) + "</a> ");

		}
		out.println(tout);
		out.println(searchInput);
		
		
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
