import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.tartarus.martin.Stemmer;


public class QueryProcessing {
	
	public static DB db = new DB();
	String filename="D:\\CUFE_CHS\\Senior-1-2\\Advanced Programming Techniques\\Project\\Interface\\NonIndexedWords.txt";
	BufferedReader br = null;
	FileReader fr = null;
	ArrayList<String> parts = new ArrayList<String>();
	
	private ArrayList<ArrayList<String>> EachWord(String query) throws IOException, SQLException
	{
		ArrayList<ArrayList<String>> urldoc = new ArrayList<ArrayList<String>>();
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
				String sql = "select DISTINCT URL from indexer where KeyWord = '"+parts.get(i)+"';";
				ResultSet rs = db.runSql(sql);
				while(rs.next())
				{
					ArrayList<String> xurldoc = new ArrayList<String>();
					sql = "select URL,Document from Crawler where URL = '"+rs.getString(1)+"';";						
					ResultSet rs2 = db.runSql(sql);
					if(rs2.next())
					{
						xurldoc.add(rs2.getString(1));
						xurldoc.add(rs2.getString(2));
						urldoc.add(xurldoc);
					}
				}
			}
		return urldoc;
	}
	
	private ArrayList<ArrayList<String>> PhraseSearch(String query) throws SQLException, IOException
	{
		ArrayList<ArrayList<String>> urldoc = new ArrayList<ArrayList<String>>();
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
					ArrayList<String> xurldoc = new ArrayList<String>();
					String sql = "select URL,Document from Crawler where URL = '"+Rs.get(0).getString(4)+"';";
					ResultSet rs2 = db.runSql(sql);
					if(rs2.next())
					{
						xurldoc.add(rs2.getString(1));
						xurldoc.add(rs2.getString(2));
						urldoc.add(xurldoc);
					}
				}
				else
				{
					tempbool=false;
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