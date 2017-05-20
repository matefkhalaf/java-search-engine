
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class Ranker implements Runnable{
	public static DB db = new DB();
	public void CalculateRank() throws SQLException, IOException{

		String sql = "SELECT Distinct KeyWord FROM `indexer`";
		ResultSet rs4 = db.runSql(sql);
		//calculate tf
		sql = "SELECT Distinct URL FROM `Crawler` where indexed =1" ;
		ResultSet rs=db.runSql(sql);
		ArrayList<String> mylist = new ArrayList<>();
		while (rs.next()){
			mylist.add(rs.getString("URL"));
		}
		while(rs4.next()) {
			sql = "DELETE FROM `Ranker` WHERE Keyword=?" ;
			PreparedStatement stmt4 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt4.setString(1, rs4.getString("KeyWord"));
			stmt4.execute();
			for (String url:mylist)
			{
				sql = "SELECT Rank FROM `Page_Rank` WHERE URL=?" ;
				stmt4 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt4.setString(1,url);
				ResultSet rs2=stmt4.executeQuery();
				float PR=0;
				if (rs2.next())
				{
					PR=rs2.getFloat("Rank");
				}

				sql = "SELECT TF FROM `Term_Frequency` WHERE URL=? AND KeyWord=?" ;
				stmt4 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt4.setString(1, url);
				stmt4.setString(2,rs4.getString("KeyWord") );
				ResultSet rs3=stmt4.executeQuery();
				float TF=0;
				if (rs3.next())
				{
					TF=rs3.getFloat("TF");
				}
				sql = "SELECT IDF FROM `idf` WHERE KeyWord=?" ;
				stmt4 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt4.setString(1, rs4.getString("KeyWord"));
				ResultSet rs5=stmt4.executeQuery();
				float IDF=0;
				if (rs5.next())
				{
					IDF=rs5.getFloat("IDF");
				}
				float Rank=IDF*TF*PR;
				sql="INSERT INTO `Ranker`( `URL`,`Keyword`,`Rank`) VALUES " + "(?,?,?)";
				PreparedStatement stmt3 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt3.setString(1, url);

				stmt3.setString(2,rs4.getString("KeyWord"));
				stmt3.setFloat(3,Rank);
				stmt3.execute();

			}

		}
	}
	@Override
	public void run() {
		try {
			while(true) {
				CalculateRank();
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

}


