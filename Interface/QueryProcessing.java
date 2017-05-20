import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QueryProcessing {
	
	public static DB db = new DB();
	String filename="D:\\CUFE_CHS\\Senior-1-2\\Advanced Programming Techniques\\Project\\Interface\\NonIndexedWords.txt";
	BufferedReader br = null;
	FileReader fr = null;
	ArrayList<String> parts = new ArrayList<String>();
	
	private ArrayList<ArrayList<String>> EachWord(String query) throws IOException, SQLException
	{
		ArrayList<ArrayList<String>> urldoc = new ArrayList<ArrayList<String>>();
		ArrayList<Integer> rank = new ArrayList<Integer>();
		ArrayList<String> URL = new ArrayList<String>();
		ArrayList<Document> doc = new ArrayList<Document>();
		String[] part=query.split(" ");
		int len=part.length;
			for (int i=0;i<len;i++)
			{
				parts.add(part[i]);
			}
		

			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filename));
			while ((sCurrentLine = br.readLine()) != null) 
			{
				for(int i=0;i<len;i++)
				{
					if(parts.get(i)==sCurrentLine)
						parts.remove(i);
				}
			}
			String temp;
			for(int i=0;i<parts.size();i++)
			{
				parts.set(i, parts.get(i).toLowerCase());
				temp=parts.get(i);
				Stemmer mystem = new Stemmer();
				for(int j=0;j<temp.length();j++)
				{
					mystem.add(temp.charAt(j));
				}
				mystem.stem();
				parts.set(i, mystem.toString());
			}
			
			for (int i=0;i<parts.size();i++)
			{
				String sql = "select URL from indexer where KeyWord = '"+parts.get(i)+"';";
				ResultSet rs = db.runSql(sql);
				while(rs.next())
				{
					URL.add(rs.getString(1));
				}
			}
			for (int i=0;i<URL.size()-1;i++)
			{
				for (int j=i+1;j<URL.size();j++)
				{
					if(URL.get(i).equals(URL.get(j)))
					{
						URL.remove(j);
						j--;
					}
					
				}
			}
			int t=0;
			for(int i=0;i<URL.size();i++)
			{
				t=0;
				for(int j=0;j<parts.size();j++)
				{
					String sql = "select rank from ranker where KeyWord = '"+parts.get(j)+"' and URL = '"+URL.get(i)+"';";
					ResultSet rs = db.runSql(sql);
					while(rs.next())
					{
						t+=rs.getInt(1);
					}
					rank.add(t);
				}
			}
			for(int i=0;i<rank.size()-1;i++)
			{
				String tempst="";
				int tempint=0;
				for(int j=i+1;j<rank.size();j++)
				{
					if(rank.get(i)<rank.get(j))
					{
						tempint=rank.get(i);
						rank.set(i, rank.get(j));
						rank.set(j, tempint);
						tempst=URL.get(i);
						URL.set(i, URL.get(j));
						URL.set(j, tempst);
					}
				}
			}
			for(int i=0;i<URL.size();i++)
			{
				doc.add(Jsoup.connect(URL.get(i)).ignoreHttpErrors(true).timeout(70000).get());
				ArrayList<String> xurldoc = new ArrayList<String>();
				String sql = "select URL,Document from Crawler where URL = '"+URL.get(i)+"';";						
				ResultSet rs2 = db.runSql(sql);
				if(rs2.next())
				{
					xurldoc.add(rs2.getString(1));
					xurldoc.add(rs2.getString(2));
					urldoc.add(xurldoc);
				}
			}
		return urldoc;
	}
	
	private ArrayList<ArrayList<String>> PhraseSearch(String query) throws SQLException, IOException
	{
		ArrayList<ArrayList<String>> urldoc = new ArrayList<ArrayList<String>>();
		ArrayList<Integer> rank = new ArrayList<Integer>();
		ArrayList<String> URL = new ArrayList<String>();
		ArrayList<Document> doc = new ArrayList<Document>();
		String[] part=query.split(" ");
		int len=part.length;
			for (int i=0;i<len;i++)
			{
				parts.add(part[i]);
			}
		

			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filename));
			while ((sCurrentLine = br.readLine()) != null) 
			{
				for(int i=0;i<len;i++)
				{
					if(parts.get(i)==sCurrentLine)
						parts.remove(i);
				}
			}
			String temp;
			for(int i=0;i<parts.size();i++)
			{
				parts.set(i, parts.get(i).toLowerCase());
				temp=parts.get(i);
				Stemmer mystem = new Stemmer();
				for(int j=0;j<temp.length();j++)
				{
					mystem.add(temp.charAt(j));
				}
				mystem.stem();
				parts.set(i, mystem.toString());
			}
			boolean tempbool = true;
			ArrayList<ResultSet> Rs=new ArrayList<ResultSet>();
			for (int i=0;i<parts.size();i++)
			{
				String sql = "select * from indexer where KeyWord = '"+parts.get(i)+"';";
				ResultSet rs = db.runSql(sql);
				Rs.add(rs);	
			}
			while(Rs.get(0).next())
			{
				for(int i=1;i<Rs.size();i++)
				{
					if(tempbool==false)
					{
						break;
					}
					int temp1=0;
					while(Rs.get(i).next())
					{
						if(Rs.get(0).getString(4).equals(Rs.get(i).getString(4))&&Rs.get(0).getInt(1)==Rs.get(i).getInt(1)-(2*i))
						{
							temp1=1;
							tempbool=true;
							break;
						}
					}
					if(temp1==0)
						tempbool=false;
				}
				if(tempbool==true)
				{
					URL.add(Rs.get(0).getString(4));
				}
				else
				{
					tempbool=false;
				}
			}
			
			for (int i=0;i<URL.size()-1;i++)
			{
				for (int j=i+1;j<URL.size();j++)
				{
					if(URL.get(i).equals(URL.get(j))){
						URL.remove(j);
						j--;
					}
						
				}
			}
			int t=0;
			for(int i=0;i<URL.size();i++)
			{
				t=0;
				for(int j=0;j<parts.size();j++)
				{
					String sql = "select rank from ranker where KeyWord = '"+parts.get(j)+"' and URL = '"+URL.get(i)+"';";
					ResultSet rs = db.runSql(sql);
					while(rs.next())
					{
						t+=rs.getInt(1);
					}
					rank.add(t);
				}
			}
			for(int i=0;i<rank.size()-1;i++)
			{
				String tempst="";
				int tempint=0;
				for(int j=i+1;j<rank.size();j++)
				{
					if(rank.get(i)<rank.get(j))
					{
						tempint=rank.get(i);
						rank.set(i, rank.get(j));
						rank.set(j, tempint);
						tempst=URL.get(i);
						URL.set(i, URL.get(j));
						URL.set(j, tempst);
					}
				}
			}
			for(int i=0;i<URL.size();i++)
			{
				doc.add(Jsoup.connect(URL.get(i)).ignoreHttpErrors(true).timeout(70000).get());
				ArrayList<String> xurldoc = new ArrayList<String>();
				String sql = "select URL,Document from Crawler where URL = '"+URL.get(i)+"';";						
				ResultSet rs2 = db.runSql(sql);
				if(rs2.next())
				{
					xurldoc.add(rs2.getString(1));
					xurldoc.add(rs2.getString(2));
					urldoc.add(xurldoc);
				}
			}
	
			
		return urldoc;
	}
	public ArrayList<ArrayList<String>> GetDocument(String s) throws IOException, SQLException
	{
		ArrayList<ArrayList<String>> urldoc = new ArrayList<ArrayList<String>>();
			if(s.charAt(0)!='"')
			{
				 urldoc = EachWord(s);
			}
			else
			{
				urldoc=PhraseSearch(s.substring(1, s.length()-1)); 
			}
		return urldoc;
	}
	
}
