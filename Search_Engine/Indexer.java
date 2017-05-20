
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.tartarus.martin.Stemmer;
 
public class Indexer implements Runnable{
	public static DB db = new DB();
	public void IndexDB() throws SQLException, IOException{
		
		String sql = "SELECT * FROM `Crawler` where indexed = 0";
		ResultSet rs = db.runSql(sql);

		File file = new File("NonIndexedWords.txt");
		Scanner scanner = new Scanner(file);
		ArrayList<String> mylist = new ArrayList<String>();
		while (scanner.hasNext()){
		    mylist.add(scanner.next());
		}
		scanner.close();
	 
		while(rs.next()) {
				String sql2 = "SELECT * FROM `indexer` where URL = '" + rs.getString("URL") + "'";
				ResultSet rs2 = db.runSql(sql2);
				if(rs2.next()) {
					sql = "DELETE FROM `indexer` WHERE URL = ?" ;
					PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					stmt.setString(1, rs.getString("URL"));
					stmt.execute();
				}
				
				sql = "UPDATE `Crawler` set indexed = 1 where URL = ?";
				PreparedStatement stmt2 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt2 = Crawler.db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt2.setString(1, rs.getString("URL"));
				stmt2.execute();
				
			Document d;
			String s=rs.getString("Document");
			String title="";
			String [] s1= s.split("(?<=</html>)");
			
			String Desc = "";
			String Description[] = {};
			String Keyw = "";
			String Keywords[] = {};
			Document met = Jsoup.parse(s); // Parse HTML into a Document -- returns sane HTML
			Desc = met.select("meta[name=description]").attr("content");
			Keyw = met.select("meta[name=keywords]").attr("content");
			if(Desc != "" && Desc != null) {
				Description = Desc.split(" ");
			}
			if(Keyw != "" && Keyw != null) {
				Keywords = Keyw.split(" ");
			}
			
				for(String part:s1)
				{
					d=Jsoup.parse(part);
					title+=d.title();	
					
				}
			Document doc1=Jsoup.parse(s);
			Elements e=doc1.select("p");
			String body="";
			for(Element p:e)
			{
				body+=p.text();
			}
			
		  String header="";
		  Elements e1=doc1.select("h1");
		  for(Element p:e1)
			{
				header+=p.text();
			}
			  Elements e2=doc1.select("h2");
		  for(Element p:e2)
			{
				header+=p.text();
			}
			 Elements e3=doc1.select("h3");
		  for(Element p:e3)
			{
				header+=p.text();
			}
			
			 Elements e4=doc1.select("h4");
		  for(Element p:e4)
			{
				header+=p.text();
			}
			 Elements e5=doc1.select("h5");
		  for(Element p:e5)
			{
				header+=p.text();
			}
			 Elements e6=doc1.select("h6");
		  for(Element p:e6)
			{
				header+=p.text();
			}
		
		  
		    String[] head = header.split(" ");
		    String[] tit = title.split(" ");
		    String[] bodyy = body.split(" ");
		    
		    
		    sql = "INSERT INTO `Indexer`(`KeyWord`, `Position`, `URL`) VALUES " + "(?,?,?)" ;
			PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	
			
		    if(head.length != 0) {
		    	for(int i=0; i < head.length; i++) {

			    	head[i] = head[i].toLowerCase();
		    		if(!mylist.contains(head[i])  && !head[i].contains("??") && !head[i].contains(" ")  && head[i] != "") {
		    		if(head[i].endsWith(":") || head[i].endsWith(",") || head[i].endsWith(".")) head[i] = head[i].replace(head[i].substring(head[i].length() - 1), "");
		    			
		    		Stemmer mystem = new Stemmer();
		    		String low = head[i].toLowerCase();
		    		char[] w = new char[501];
		    		for (int c = 0; c < low.length(); c++) 
		    			{
		    			w[c] = low.charAt(c);
		    			mystem.add(w[c]);
		    			}
		    		mystem.stem();
		    		String mys = mystem.toString();
		    		
		    		if(!mys.equals(head[i].toLowerCase())) {
		    		stmt.setString(1, mys);
					stmt.setString(2, "Header");
					stmt.setString(3, rs.getString("URL"));
					stmt.execute();
		    		}
		    			
		    		stmt.setString(1, head[i].toLowerCase());
					stmt.setString(2, "Header");
					stmt.setString(3, rs.getString("URL"));
					stmt.execute();
		    		}
		    		
		    	}
				
		    }
		    if(tit.length != 0) {
		    	for(int i=0; i < tit.length; i++) {
		    		tit[i] = tit[i].toLowerCase();
		    		if(!mylist.contains(tit[i]) && !tit[i].contains("??") && !tit[i].contains(" ") && tit[i] != "") {
		    			if(tit[i].endsWith(":") || tit[i].endsWith(",") || tit[i].endsWith(".")) tit[i] = tit[i].replace(tit[i].substring(tit[i].length() - 1), "");
		    			Stemmer mystem = new Stemmer();
			    		String low = tit[i].toLowerCase();
			    		char[] w = new char[501];
			    		for (int c = 0; c < low.length(); c++) 
			    			{
			    			w[c] = low.charAt(c);
			    			mystem.add(w[c]);
			    			}
			    		mystem.stem();
			    		String mys = mystem.toString();
			    		
			    		if(!mys.equals(tit[i].toLowerCase())) {
			    		stmt.setString(1, mys);
						stmt.setString(2, "Title");
						stmt.setString(3, rs.getString("URL"));
						stmt.execute();
			    		}
						
		    			stmt.setString(1, tit[i].toLowerCase());
					stmt.setString(2, "Title");
					stmt.setString(3, rs.getString("URL"));
					stmt.execute();
		    		}
		    	}
		    }
		    if(bodyy.length != 0) {
		    	
		    	for(int i=0; i < bodyy.length; i++) {
		    		bodyy[i] = bodyy[i].toLowerCase();
		    		if(!mylist.contains(bodyy[i])  && !bodyy[i].contains("??") && !bodyy[i].contains(" ")  && bodyy[i] != "") {
		    			
		    			if(bodyy[i].endsWith(":") || bodyy[i].endsWith(",") || bodyy[i].endsWith(".")) bodyy[i] = bodyy[i].replace(bodyy[i].substring(bodyy[i].length() - 1), "");
		    			
		    			Stemmer mystem = new Stemmer();
			    		String low = bodyy[i].toLowerCase();
			    		char[] w = new char[501];
			    		for (int c = 0; c < low.length(); c++) 
			    			{
			    			w[c] = low.charAt(c);
			    			mystem.add(w[c]);
			    			}
			    		mystem.stem();
			    		String mys = mystem.toString();
			    		
			    		if(!mys.equals(bodyy[i].toLowerCase())) {
			    		stmt.setString(1, mys);
						stmt.setString(2, "Body");
						stmt.setString(3, rs.getString("URL"));
						stmt.execute();
			    		}	
		    			
		    			stmt.setString(1, bodyy[i].toLowerCase());
					stmt.setString(2, "Body");
					stmt.setString(3, rs.getString("URL"));
					stmt.execute();
		    		}
		    	}
		    }
		    
		    if(Description.length != 0) {
		    	
		    	for(int i=0; i < Description.length; i++) {
		    		Description[i] = Description[i].toLowerCase();
		    		if(!mylist.contains(Description[i])  && !Description[i].contains("??") && !Description[i].contains(" ")  && Description[i] != "") {
		    		
		    			if(Description[i].endsWith(":") || Description[i].endsWith(",") || Description[i].endsWith(".")) Description[i] = Description[i].replace(Description[i].substring(Description[i].length() - 1), "");
		    			Stemmer mystem = new Stemmer();
			    		String low = Description[i].toLowerCase();
			    		char[] w = new char[501];
			    		for (int c = 0; c < low.length(); c++) 
			    			{
			    			w[c] = low.charAt(c);
			    			mystem.add(w[c]);
			    			}
			    		mystem.stem();
			    		String mys = mystem.toString();
			    		
			    		if(!mys.equals(Description[i].toLowerCase())) {
			    		stmt.setString(1, mys);
						stmt.setString(2, "Description");
						stmt.setString(3, rs.getString("URL"));
						stmt.execute();
			    		}
		    			
		    			stmt.setString(1, Description[i].toLowerCase());
					stmt.setString(2, "Description");
					stmt.setString(3, rs.getString("URL"));
					stmt.execute();
		    		}
		    	}
		    }
		    
		    if(Keywords.length != 0) {
		    	
		    	for(int i=0; i < Keywords.length; i++) {
		    		Keywords[i] = Keywords[i].toLowerCase();
		    		if(!mylist.contains(Keywords[i])  && !Keywords[i].contains("??") && !Keywords[i].contains(" ")  && Keywords[i] != "") {
		    		
		    			if(Keywords[i].endsWith(":") || Keywords[i].endsWith(",") || Keywords[i].endsWith(".")) Keywords[i] = Keywords[i].replace(Keywords[i].substring(Keywords[i].length() - 1), "");
		    			
		    			Stemmer mystem = new Stemmer();
			    		String low = Keywords[i].toLowerCase();
			    		char[] w = new char[501];
			    		for (int c = 0; c < low.length(); c++) 
			    			{
			    			w[c] = low.charAt(c);
			    			mystem.add(w[c]);
			    			}
			    		mystem.stem();
			    		String mys = mystem.toString();
			    		
			    		if(!mys.equals(Keywords[i].toLowerCase())) {
			    		stmt.setString(1, mys);
						stmt.setString(2, "Keywords");
						stmt.setString(3, rs.getString("URL"));
						stmt.execute();
			    		}
		    			
		    		stmt.setString(1, Keywords[i].toLowerCase());
					stmt.setString(2, "Keywords");
					stmt.setString(3, rs.getString("URL"));
					stmt.execute();
		    		}
		    	}
		    }

		  	System.out.println("URL: " + rs.getString("URL") + " was indexed successfully.");
			}
		
		}
	public void run() {
		try {
			while(true) {
			IndexDB();
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	   
}


