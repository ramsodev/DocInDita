package net.ramso.docindita.db.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseTest {
	public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	public String protocol = "jdbc:derby:";
//	public String db = "toursdb";
	public String db = "DB/BirtSample";
	
	protected Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String path = Thread.currentThread().getContextClassLoader().getResource(db).getPath();
		Class.forName(driver).newInstance();		
		return DriverManager.getConnection(protocol + path);
	}
	
	protected void disconnect(Connection con) throws SQLException {
		con.close();
	}

}
