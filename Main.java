import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	@SuppressWarnings({ "resource", "deprecation" })
	public static void main(String[] args) throws SQLException, IOException, InterruptedException, ParseException {
		
		//enter the seeds in the database to be crawled later
		String[] seed= {"http://dmoztools.net","http://edition.cnn.com","https://www.wikipedia.org","http://www.dictionary.com","http://www.webopedia.com"};
		int seedlen=seed.length;
		String sql = "INSERT INTO `Urlsforcrawling`(`URL`,`Depth`,`Selected`,`BaseURL`) VALUES " + "(?,?,?,?)" ;
		PreparedStatement stmt = Crawler.db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		for(int i=0;i<seedlen;i++)
		{
			stmt.setString(1, seed[i]);
			stmt.setString(2, "0");
			stmt.setString(3, "0");
			URL urlObj = new URL(seed[i]);
			String strHost = urlObj.getHost();
			stmt.setString(4, strHost);
			stmt.execute();
		}
		
		sql = "UPDATE Urlsforcrawling set selected = 0";
		stmt = Crawler.db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.execute();
		
		System.out.println("---------------Please enter the number of threads to run crawling---------------");
		Scanner sc = new Scanner(System.in);
		int nthreads=sc.nextInt();
		ExecutorService executor = Executors.newFixedThreadPool(nthreads);
		
		sql = "select * from Urlsforcrawling where selected=0 Limit 1 ;";
		ResultSet rs = Crawler.db.runSql(sql);
		
		Thread index = new Thread(new Indexer());
		index.start();
		
		
		
		while(true)
		{
			if(rs.next())
			{
			sql = "UPDATE Urlsforcrawling set selected = 1 where RecordID = ?";
			stmt = Crawler.db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, rs.getString(1));
			stmt.execute();
			Runnable c = new Crawler(rs.getString(2),rs.getInt(3));
			executor.execute(c);
			}
			
			Date d= new Date();
			
			sql = "select * from Crawler ;";
			rs = Crawler.db.runSql(sql);
			while(rs.next())
			{
				DateFormat format = new SimpleDateFormat("HH:mm:ss");
				Date d1 = format.parse(rs.getString(7));
				if(d.getHours()*60 - d1.getHours()*60 >rs.getInt(6)||d.getMinutes() - d1.getMinutes()>rs.getInt(6)||((d.getHours()*60 - d1.getHours()*60)+(d.getMinutes() - d1.getMinutes()))>rs.getInt(6)){
					int z=1;
				sql = "UPDATE Crawler set ToUpdate ='"+z+"' WHERE URL='"+rs.getString(2)+"';";
				stmt = Crawler.db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.execute();
				
				sql = "INSERT INTO `Urlsforcrawling`(`URL`,`Depth`,`Selected`,`BaseURL`) VALUES " + "(?,?,?,?)" ;
				stmt = Crawler.db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				System.out.println("URL: " + rs.getString(2) + " will be re-crawled (updated).");
				URL urlObj = new URL(rs.getString(2));
				String strHost = urlObj.getHost();	
				stmt.setString(1, rs.getString(2));
				stmt.setString(2, String.valueOf(rs.getString(5)));
				stmt.setString(3, "0");
				stmt.setString(4, strHost);
				stmt.execute();
				}
			}
			sql = "select * from Urlsforcrawling where selected=0 Limit 1 ;";				
			rs = Crawler.db.runSql(sql);
			
			
		}
	}
}
