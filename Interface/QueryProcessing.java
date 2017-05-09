import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.tartarus.martin.*;

public class QueryProcessing {
	
	public static DB db = new DB();
	String filename="D:\\CUFE_CHS\\Senior-1-2\\Advanced Programming Techniques\\Project\\Interface\\NonIndexedWords.txt";
	BufferedReader br = null;
	FileReader fr = null;
	ArrayList<String> parts = new ArrayList<String>();
	
	private ArrayList<Document> EachWord(String query) throws IOException, SQLException
	{
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
					doc.add(Jsoup.connect(rs.getString(1)).ignoreHttpErrors(true).timeout(70000).get());
				}
			}
		return doc;
	}
	
	private ArrayList<Document> PhraseSearch(String query) throws SQLException, IOException
	{
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
					doc.add(Jsoup.connect(Rs.get(0).getString(4)).ignoreHttpErrors(true).timeout(70000).get());
				}
				else
				{
					tempbool=false;
				}
			}
		return doc;
	}
	public ArrayList<Document> GetDocument(String s) throws IOException, SQLException
	{
		ArrayList<Document> doc=new ArrayList<Document>();
			if(s.charAt(0)!='"')
			{
				 doc = EachWord(s);
			}
			else
			{
				doc=PhraseSearch(s.substring(1, s.length()-1)); 
			}
		return doc;
	}
	
}
