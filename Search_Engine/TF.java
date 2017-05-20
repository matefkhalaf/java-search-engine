
import java.io.*;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;

public class TF implements Runnable{
	public static DB db = new DB();

	public void CalculateTF() throws SQLException, IOException{
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.CEILING);
		String sql = "SELECT * FROM `Crawler` where indexed =1";
		ResultSet rs = db.runSql(sql);
		//calculate tf
		while(rs.next()) {
			String sql2 = "SELECT KeyWord FROM `indexer` where URL = '" + rs.getString("URL") + "'";
			ResultSet rs2 = db.runSql(sql2);
			while(rs2.next()) {
				sql = "DELETE FROM `term_frequency` WHERE URL = ? AND KeyWord=?" ;
				PreparedStatement stmt4 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt4.setString(1, rs.getString("URL"));
				stmt4.setString(2, rs2.getString("KeyWord"));
				stmt4.execute();
				sql = "SELECT Count(*) FROM `indexer` WHERE URL = ? AND KeyWord=? AND Position='Header'" ;
				PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1, rs.getString("URL"));
				stmt.setString(2, rs2.getString("KeyWord"));
				ResultSet header=stmt.executeQuery();
				sql = "SELECT Count(*) FROM `indexer` WHERE URL = ? AND Position='Header' ";
				PreparedStatement stmt2 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt2.setString(1, rs.getString("URL"));
				ResultSet allheader=stmt2.executeQuery();

				header.next();
				int hf=header.getInt(1);

				allheader.next();
				int head_number=allheader.getInt(1);
				sql = "SELECT Count(*) FROM `indexer` WHERE URL = ? AND KeyWord=? AND Position='Title'" ;
				stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1, rs.getString("URL"));
				stmt.setString(2, rs2.getString("KeyWord"));
				ResultSet title=stmt.executeQuery();
				sql = "SELECT Count(*) FROM `indexer` WHERE URL = ? AND Position='Title' " ;
				stmt2 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt2.setString(1, rs.getString("URL"));
				ResultSet alltitle=stmt2.executeQuery();

				title.next();
				int titf=title.getInt(1);

				alltitle.next();
				int title_number=alltitle.getInt(1);
				sql = "SELECT Count(*) FROM `indexer` WHERE URL = ? AND KeyWord=? AND Position='Description'" ;
				stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1, rs.getString("URL"));
				stmt.setString(2, rs2.getString("KeyWord"));
				ResultSet desc=stmt.executeQuery();
				sql = "SELECT Count(*) FROM `indexer` WHERE URL = ? AND Position='Description' " ;
				stmt2 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt2.setString(1, rs.getString("URL"));
				ResultSet alldesc=stmt2.executeQuery();

				desc.next();
				int descf=desc.getInt(1);

				alldesc.next();
				int desc_number=alldesc.getInt(1);
				sql = "SELECT Count(*) FROM `indexer` WHERE URL = ? AND KeyWord=? AND Position='Body'" ;
				stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1, rs.getString("URL"));
				stmt.setString(2, rs2.getString("KeyWord"));
				ResultSet body=stmt.executeQuery();
				sql = "SELECT Count(*) FROM `indexer` WHERE URL = ? AND Position='Body' " ;
				stmt2 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt2.setString(1, rs.getString("URL"));
				ResultSet allbody=stmt2.executeQuery();

				body.next();
				int bf=body.getInt(1);

				allbody.next();
				int body_number=allbody.getInt(1);
				int frequency=descf*7+hf*6+titf*3+bf;
				int word_number=head_number*6+title_number*3+body_number+desc_number*7;
				float tf=(float)frequency/word_number;

				sql="INSERT INTO `term_frequency`( `KeyWord`,`URL`, `TF`) VALUES " + "(?,?,?)";
				PreparedStatement stmt3 = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt3.setString(1, rs2.getString("KeyWord"));
				stmt3.setString(2, rs.getString("URL"));
				stmt3.setFloat(3,tf);
				stmt3.execute();
			}
		}
	}
	@Override
	public void run() {
		try {
			while(true) {
				CalculateTF();
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

}


