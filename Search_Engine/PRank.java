
import java.io.*;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;
 
public class PRank implements Runnable{
	public static DB db = new DB();
        
	public void CalculateRank() throws SQLException, IOException{
		DecimalFormat df = new DecimalFormat("#.####");
                df.setRoundingMode(RoundingMode.CEILING);
		String sql = "SELECT DISTINCT URL FROM `Rank`";
		ResultSet rs = db.runSql(sql);
               //calculate tf
		while(rs.next()) {
                    sql = "SELECT Count(*) FROM `Page_Rank` WHERE URL='" + rs.getString("URL") +"'";
                    ResultSet count=db.runSql(sql);
                    count.next();
                    if (count.getInt(1)==0){
				sql = "INSERT INTO `Page_Rank` (`URL`, `Rank`) VALUES " + "(?,?)" ;
					PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					stmt.setString(1, rs.getString("URL"));
                                        stmt.setFloat(2, (float) 0.1);
					stmt.execute();
                    }
		}
                sql = "SELECT * FROM `Page_Rank`";
		//ResultSet rs1 = db.runSql(sql);
                while(rs.next()) {
                    sql = "SELECT REFERENCE FROM `RANK` WHERE URL='"+rs.getString("URL")+"'";
                    ResultSet rs2 = db.runSql(sql);
                    float rank=rs.getFloat("Rank");
				while (rs2.next())
                                {
                                    sql="SELECT Count(*) FROM `Rank` WHERE Reference='"+rs2.getString("Reference")+"'";
                                    ResultSet rs3=db.runSql(sql);
                                    float outlinks=rs3.getFloat(1);
                                    sql="SELECT Rank FROM `Page_Rank` WHERE URL='"+rs2.getString("Reference")+"'";
                                    ResultSet rs4=db.runSql(sql);
                                    float reference_rank=rs4.getFloat(1);
                                    rank+=(float)reference_rank/outlinks;
                                }
                               sql = "UPDATE Page_Rank set Rank = '"+rank+"' where URL ='"+rs.getString("URL")+"'";
				db.runSql2(sql);
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


