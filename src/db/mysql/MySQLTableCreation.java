package db.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

/**
 * Automatically reset our tables in our database when it messed up!
 * @author Verity Bing Chu
 *
 */
public class MySQLTableCreation {
	// Run this as Java application to reset db schema.
	public static void main(String[] args) {
		try {
			// Ensure the driver is imported.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = null;

			// Step 1 Connect to MySQL.
			try {
				System.out.println("Connecting to \n" + MySQLDBUtil.URL);
				conn = DriverManager.getConnection(MySQLDBUtil.URL);
			} catch (SQLException e) {
				System.out.println("SQLException " + e.getMessage());
				System.out.println("SQLState " + e.getSQLState());
				System.out.println("VendorError " + e.getErrorCode());
			}
			if (conn == null) {
				return;
			}
			
			// Step 2 Drop tables in case they exist.
			Statement stmt = conn.createStatement();
			
			String sql = "DROP TABLE IF EXISTS books_tags";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS books_authors";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS tags";
			stmt.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS authors";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS book_file";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS books";
			stmt.executeUpdate(sql);
			
			
			
			

			
			
			// Step 3. Create new tables.
			
			sql = "CREATE TABLE books " + "(book_id VARCHAR(255) NOT NULL, " + " title VARCHAR(255) NOT NULL, "
					+ " price FLOAT, " + " PRIMARY KEY ( book_id ))";
			// +" file BLOB NOT NULL, "
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE authors " + "(author_id VARCHAR(255) NOT NULL, " + " author_name VARCHAR(255) NOT NULL, "
					+ " PRIMARY KEY ( author_id ))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE tags " + "(tag_id VARCHAR(255) NOT NULL, " + " tag_name VARCHAR(255) NOT NULL, "
					+ " PRIMARY KEY ( tag_id ))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE books_tags " + "(book_id VARCHAR(255) NOT NULL, "
					+ " tag_id VARCHAR(255) NOT NULL, "
					+ " PRIMARY KEY (book_id,tag_id ),"
					+ "FOREIGN KEY (book_id) REFERENCES books(book_id),"
					+ "FOREIGN KEY (tag_id) REFERENCES tags(tag_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE books_authors " + "(book_id VARCHAR(255) NOT NULL, "
					+ " author_id VARCHAR(255) NOT NULL, "
					+ " PRIMARY KEY (book_id,author_id ),"
					+ "FOREIGN KEY (book_id) REFERENCES books(book_id),"
					+ "FOREIGN KEY (author_id) REFERENCES authors(author_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE book_file " + "(book_id VARCHAR(255) NOT NULL, "
					+ " file BLOB NOT NULL, "
					+ " PRIMARY KEY (book_id),"
					+ "FOREIGN KEY (book_id) REFERENCES books(book_id))";
			stmt.executeUpdate(sql);

			// Step 4: insert data
			// Create some fake data
			// tag id 0...
			// book id 1...
			// author id 2...
			sql = "INSERT INTO books " + "VALUES (\"1111\", \"bookTitleTesting1\", \"100.00\")";
			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO books " + "VALUES (\"1112\", \"bookTitleTesting2\", \"100.00\")";
			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO books " + "VALUES (\"1113\", \"bookTitleTesting3\", \"100.00\")";
			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
			
			
			sql = "INSERT INTO tags " + "VALUES (\"0000\", \"tagNameTesting1\")";
			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO tags " + "VALUES (\"0001\", \"tagNameTesting2\")";
			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO authors " + "VALUES (\"2222\", \"authorNameTesting1\")";
			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO authors " + "VALUES (\"2223\", \"authorNameTesting2\")";
			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO books_tags " + "VALUES (\"1111\", \"0000\")";
			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO books_tags " + "VALUES (\"1112\", \"0000\")";
			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO books_tags " + "VALUES (\"1112\", \"0001\")";
			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO books_authors " + "VALUES (\"1111\", \"2222\")";
			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
			
			sql = "INSERT INTO books_authors " + "VALUES (\"1112\", \"2223\")";
			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
						
			System.out.println("Import is done successfully.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
