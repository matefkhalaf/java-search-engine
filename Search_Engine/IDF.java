
import java.io.*;
import static java.lang.Math.log;
import java.sql.*;
 
public class IDF implements Runnable{
	public static DB db = new DB();
	public void CalculateIDF() throws SQLException, IOException{
		
		String sql = "SELECT Distinct KeyWord FROM `indexer`";
		ResultSet rs = db.runSql(sql);
               //calculate tf
               sql = "SELECT Count(Distinct URL) FROM `Crawler` where indexed =1" ;
		ResultSet rs4=db.runSql(sql);
                int crawledsites=0;
               if( rs4.next()){
                 crawledsites=rs4.getInt(1);
               }
		while(rs.next()) {
                    sql = "DELETE FROM `idf` WHERE KeyWord=?" ;
					PreparedStatement stmt4 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                                        stmt4.setString(1, rs.getString("KeyWord"));
					stmt4.execute();
				sql = "SELECT Count(*) FROM `indexer` WHERE KeyWord=?" ;
					PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                                        stmt.setString(1, rs.getString("KeyWord"));
					ResultSet rs3=stmt.executeQuery();
                                        rs3.next();
                                        int hits=rs3.getInt(1);
                                         sql="INSERT INTO `idf`( `KeyWord`,`IDF`) VALUES " + "(?,?)";
                                        PreparedStatement stmt3 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					stmt3.setString(1, rs.getString("KeyWord"));
                                        double x=log((double)crawledsites/hits);
                                        float idf=(float)x;
                                        stmt3.setFloat(2,idf);
                                        stmt3.execute();
				
		}
        }
        @Override
	public void run() {
		try {
			while(true) {
			CalculateIDF();
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	   
}


