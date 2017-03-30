import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

 
public class Crawler implements Runnable{
	public static DB db = new DB();
	private static final int crawler_Depth = 10;
	private int current_Depth;
	private int delay;
	private String seed ;

	Crawler(String seed,int depth)
	{
		this.seed=seed;
		this.current_Depth=depth; 
		delay=0;
	}
	@SuppressWarnings({ "deprecation"})
	public void processPage(String URL,int depth)throws SQLException, IOException, InterruptedException, ParseException{
		String sql = "DELETE FROM Urlsforcrawling LIMIT 1;";
		PreparedStatement stmt = Crawler.db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.execute();
		//check if the given URL is already in database
		sql = "select * from Crawler where URL = '"+URL+"' Limit 1 ;";
		ResultSet rs = db.runSql(sql);
		stmt = Crawler.db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		if(rs.next()){
			// if at least one row of URL don't add again
		}else{
			DateFormat df = new SimpleDateFormat("HH:mm:ss");
			
			if(!isAllowed(URL))
				return;		
			URL urlObj = new URL(URL);
			String strHost = urlObj.getHost();
			sql = "select Lastvisited from Delay where Host = '"+strHost+"' ;";
			rs = db.runSql(sql);
			if(rs.next())
			{
				DateFormat format = new SimpleDateFormat("HH:mm:ss");
				Date d = format.parse(rs.getString(1));
				if(delay!=0){								
					Date d2=new Date();
					if(d.getMinutes()-d2.getMinutes()>rs.getInt(1)||d.getHours()*60-d2.getHours()*60>rs.getInt(1)||((d.getMinutes()-d2.getMinutes())+(d.getHours()*60-d2.getHours()))>rs.getInt(1))
						{
						    System.out.println("Host: "+strHost+" delaying me now for "+rs.getString(1)+" second/s");
							Thread.sleep(rs.getInt(1));
							Date d3= new Date();
							sql = "UPDATE Crawler set Last visited = '"+df.format(d3)+"' where URL = ?";
							stmt = Crawler.db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
							stmt.setString(1, strHost);
							stmt.execute();
						}
				}
			}
			else
			{
				Date d= new Date();
				sql = "INSERT INTO `Delay`(`Host`, `LastVisited`) VALUES " + "(?,?)" ;
				stmt = Crawler.db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1, strHost);
				stmt.setString(2, df.format(d));
				stmt.execute();
			}
			
			//get useful information
			Document doc = Jsoup.connect(URL).ignoreHttpErrors(true).timeout(70000).get();
			
			//store the URL to database to avoid parsing again
			sql = "INSERT INTO `Crawler`(`URL`, `Document`, `indexed`,`freq`,`depth`,`CrawledTime`) VALUES " + "(?,?,?,?,?,?)" ;
			stmt = Crawler.db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			int freq = (current_Depth+1)*10; 
			Date d1= new Date();
			stmt.setString(1, URL);
			stmt.setString(2, doc.toString());
			stmt.setString(3, "0");
			stmt.setInt(4,freq);
			stmt.setInt(5,depth);
			stmt.setString(6,df.format(d1));
			stmt.execute();

			
			Elements questions = doc.select("a[href]");
			for(Element link: questions){
				if(link.attr("href").contains(".html") && depth<crawler_Depth) // only crawl .html websites (Project purpose)
				{
					urlObj = new URL(link.attr("abs:href"));
					strHost = urlObj.getHost();
						
						sql = "INSERT INTO `Urlsforcrawling`(`URL`,`Depth`,`Selected`,`BaseURL`) VALUES " + "(?,?,?,?)" ;
						stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
						stmt.setString(1, link.attr("abs:href"));
						stmt.setString(2, String.valueOf(depth+1));
						stmt.setString(3, "0");
						stmt.setString(4, strHost);
						stmt.execute();
						System.out.println("URL: " + (link.attr("abs:href")) +" will be crawled later.");
				}
			}
			System.out.println("URL: " + URL + " was crawled successfully.");
		}
		
	}
	@Override
	
	
	public void run() {
		try {

			processPage(seed,current_Depth);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	public boolean isAllowed(String URL) throws MalformedURLException
	{
			boolean takerules=false;
			URL urlObj = new URL(URL);
			String strHost = urlObj.getHost();
			 try(BufferedReader in = new BufferedReader(
			            new InputStreamReader(new URL("http://"+strHost+"/robots.txt").openStream()))) {
			        String line = null;
			        while((line = in.readLine()) != null) {
			            if(line.contains("User-agent: *"))
			            	takerules=true;
			            else if (line.contains("User-agent:")&&takerules)
			            	break;
			            else if(line.contains("Disallow:") && takerules)
			            {
			            	//take the rules for disallowing				            	
			            	String ss=line.substring(10, line.length());
			            	if(ruleMatches(URL,ss))
			            	{
			            		System.out.println(URL+" is disallowed I can't crawl");
			            		return false;
			            	}
			            }
			            else if(line.contains("Allow:") && takerules)
			            {
			            	//take the rules for disallowing				            	
			            	String ss=line.substring(7, line.length());
			            	if(ruleMatches(URL,ss))
			            		return true;
			            	
			            	
			            }
			        	if(line.contains("Crawl-delay:"))
			        	{
			        		String ss=line.substring(14,15);
			        		delay=Integer.parseInt(ss);
			        	}
			        }
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			 return true;
		
	}
	public static boolean ruleMatches(String s1,String disallowed) throws MalformedURLException
	{
		String path = new URL(s1).getPath();
		disallowed.replace("*", "[a-zA-Z0-9]*");
		return Pattern.matches(disallowed,path);	
	}

}
