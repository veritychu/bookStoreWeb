package db.mysql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.DBConnection;
import entity.Author;
import entity.Book;
import entity.Book.BookBuilder;
import entity.Tag;

public class MySQLConnection implements DBConnection{
	private Connection conn;

	public MySQLConnection() {
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@Override
	public void addBook(Book book) {
		if (conn == null) {
			return;
		}
		try {
			// 1. Insert into books table
			String sql = "INSERT IGNORE INTO books(book_id,title,price) VALUES (?,?,?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, book.getBookId());
			statement.setString(2, book.getTitle());
			statement.setDouble(3, book.getPrice());
			statement.execute();

			// 2. Update authors table for each author.
			sql = "INSERT IGNORE INTO authors VALUES (?,?)";
			for (Author author : book.getAuthors()) {
				statement = conn.prepareStatement(sql);
				statement.setString(1, author.getAuthorId());
				statement.setString(2, author.getAuthorName());
				statement.execute();
			}
			
			// 3. Update tags table for each tag.
			sql = "INSERT IGNORE INTO tags VALUES (?,?)";
			for (Tag tag : book.getTags()) {
				statement = conn.prepareStatement(sql);
				statement.setString(1, tag.getTagId());
				statement.setString(2, tag.getTagName());
				statement.execute();
			}
			
			// 4. Update books_tags table for each book_tag.
			sql = "INSERT IGNORE INTO books_tags VALUES (?,?)";
			for (Tag tag : book.getTags()) {
				statement = conn.prepareStatement(sql);
				statement.setString(1, book.getBookId());
				statement.setString(2, tag.getTagId());
				statement.execute();
			}
			
			// 5. Update books_authors table for each book_author.
			sql = "INSERT IGNORE INTO books_authors VALUES (?,?)";
			for (Author author : book.getAuthors()) {
				statement = conn.prepareStatement(sql);
				statement.setString(1, book.getBookId());
				statement.setString(2, author.getAuthorId());
				statement.execute();
			}
			
			// 6. Update book_file table for book_file.
			String filePath = book.getFilePath();
			try {
				System.out.println(filePath);
				InputStream is = new FileInputStream(filePath);
				if (is != null) {
					sql = "INSERT IGNORE INTO book_file (book_id,file) VALUES (?,?)";
					statement = conn.prepareStatement(sql);
					statement.setString(1, book.getBookId());
					statement.setBlob(2, is);
					statement.execute();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteBook(String bookId) {
		if (conn == null) {
			return;
		}
		try {
			String sql = "DELETE FROM books_tags WHERE book_id = " + bookId;
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.execute();
			
			sql = "DELETE FROM books_authors WHERE book_id = " + bookId;
			statement = conn.prepareStatement(sql);
			statement.execute();
			
			sql = "DELETE FROM book_file WHERE book_id = " + bookId;
			statement = conn.prepareStatement(sql);
			statement.execute();
			
			sql = "DELETE FROM books WHERE book_id = " + bookId + " LIMIT 1";
			statement = conn.prepareStatement(sql);
			statement.execute();		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setBookTag(String bookId, String tagId) {
		if (conn == null) {
			return;
		}
		try {
			// Add (book_id, tag_id) into books_tags table
			String sql = "INSERT IGNORE INTO books_tags VALUES (?,?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, bookId);
			statement.setString(2, tagId);
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unsetBookTag(String bookId, String tagId) {
		if (conn == null) {
			return;
		}
		try {
			String sql = "DELETE FROM books_tags WHERE book_id = " + bookId +" AND tag_id = " + tagId + " LIMIT 1";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void renameTag(String tagId, String newTagName) {
		if (conn == null) {
			return;
		}
		try {
			String checkIfTagExist = "SELECT * from tags WHERE tag_id = " + tagId;
			PreparedStatement statement = conn.prepareStatement(checkIfTagExist);
			ResultSet rs = statement.executeQuery();
			if (!rs.isBeforeFirst() ) {    
			    System.out.println("you can only rename a tag when it exists"); 
			} 
			String sql = "UPDATE tags SET tag_name = " + newTagName + " WHERE tag_id = " + tagId;
			statement = conn.prepareStatement(sql);
			statement.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Set<Book> getBooksByNumberFilter(int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Book> getBooksByTagFilter(String tagId) {
		if (conn == null) {
			return null;
		}
		Set<Book> res = new HashSet<Book>();
		Set<Tag> mTags = new HashSet<Tag>();
		try {
			// TODO: can further modify into input as List<String>
			String getTagName = "SELECT tag_name FROM tags WHERE tag_id = " + tagId;
        	Statement stmt1 = conn.createStatement();
			ResultSet rs1 = stmt1.executeQuery(getTagName);
			while (rs1.next()) {
				String tagName = rs1.getString("tag_name");
				Tag tag = new Tag(tagId, tagName);
				mTags.add(tag);
			}
			stmt1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {		
			String query = "SELECT b.* FROM books b INNER JOIN books_tags b2t ON b.book_id = b2t.book_id WHERE tag_id = " + tagId;
			Statement stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery(query);
			while (rs2.next()){
		        String mbook_id = rs2.getString("book_id");
		        String mbook_title = rs2.getString("title");
		        Float mprice = rs2.getFloat("price");
		        String authorIdsQuery = "SELECT author_id FROM books_authors WHERE book_id = " + mbook_id;
		        Statement stmt3 = conn.createStatement();
				ResultSet rsAuthoIds = stmt3.executeQuery(authorIdsQuery);
				Set<Author> mAuthors = new HashSet<Author>();
				while (rsAuthoIds.next()) {
					String iauthorId =  rsAuthoIds.getString("author_id");
					String authorsQuery = "SELECT author_name FROM authors WHERE author_id = " + iauthorId;
					Statement stmt4 = conn.createStatement();
					ResultSet rsAuthors = stmt4.executeQuery(authorsQuery);
					while (rsAuthors.next()) {
						String iauthorName = rsAuthors.getString("author_name");
						Author imauthor = new Author(iauthorId, iauthorName);
						mAuthors.add(imauthor);
					}
					stmt4.close();
				}
				stmt3.close();
				// print the result's key features for debug
//		        System.out.format("%s, %s, %.2f\n", mbook_id, mbook_title, mprice);
		        BookBuilder builder = new BookBuilder().setBookId(mbook_id).setTitle(mbook_title).setPrice(mprice).setAuthors(mAuthors).setTags(mTags);
		        Book book = builder.build();
		        res.add(book);
			}
			stmt2.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public Set<Book> getBooksByAuthorFilter(String authorId) {
		if (conn == null) {
			return null;
		}
		Set<Book> res = new HashSet<Book>();
		Set<Author> mAuthors = new HashSet<Author>();
		try {
			// TODO: can further modify into input as List<String>
			String getauthorName = "SELECT author_name FROM authors WHERE author_id = " + authorId;
        	Statement stmt1 = conn.createStatement();
			ResultSet rs1 = stmt1.executeQuery(getauthorName);
			while (rs1.next()) {
				String authorName = rs1.getString("author_name");
				Author author = new Author(authorId, authorName);
				mAuthors.add(author);
			}
			stmt1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {		
			String query = "SELECT b.* FROM books b INNER JOIN books_authors b2a ON b.book_id = b2a.book_id WHERE author_id = " + authorId;
			Statement stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery(query);
			while (rs2.next())
		      {
		        String mbook_id = rs2.getString("book_id");
		        String mbook_title = rs2.getString("title");
		        Float mprice = rs2.getFloat("price");
		        String tagIdsQuery = "SELECT tag_id FROM books_tags WHERE book_id = " + mbook_id;
		        Statement stmt3 = conn.createStatement();
				ResultSet rsTagIds = stmt3.executeQuery(tagIdsQuery);
				Set<Tag> mTags = new HashSet<Tag>();
				while (rsTagIds.next()) {
					String itagId =  rsTagIds.getString("tag_id");
					String tagsQuery = "SELECT tag_name FROM tags WHERE tag_id = " + itagId;
					Statement stmt4 = conn.createStatement();
					ResultSet rsTags = stmt4.executeQuery(tagsQuery);
					while (rsTags.next()) {
						String itagName = rsTags.getString("tag_name");
						Tag imtag = new Tag(itagId, itagName);
						mTags.add(imtag);
					}
					stmt4.close();
				}
				stmt3.close();
		        // print the result's key features for debug
//		        System.out.format("%s, %s, %.2f, %s\n", mbook_id, mbook_title, mprice);
		        BookBuilder builder = new BookBuilder().setBookId(mbook_id).setTitle(mbook_title).setPrice(mprice).setAuthors(mAuthors).setTags(mTags);
		        Book book = builder.build();
		        res.add(book);
		      }
			stmt2.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public Set<Tag> getBookTags(String bookId) {
		if (conn == null) {
			return null;
		}
		Set<Tag> res = new HashSet<>();
		try {
			String query = "SELECT t.* FROM tags t INNER JOIN books_tags b2t ON t.tag_id = b2t.tag_id WHERE book_id = " + bookId; 
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				String mtag_id = rs.getString("tag_id");
				String mtag_name = rs.getString("tag_name");
				Tag mtag = new Tag(mtag_id, mtag_name);
				res.add(mtag);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	@Override
	public Book getBook(String bookId) {
		if (conn == null) {
			return null;
		}
		List<Book> res = new ArrayList<>();
		try {
			String query = "SELECT * FROM books WHERE book_id = " + bookId; 
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
		        String mbook_id = rs.getString("book_id");
		        String mbook_title = rs.getString("title");
		        Float mprice = rs.getFloat("price");
		        Set<Author> mAuthors = new HashSet<Author>();
		        Set<Tag> mTags = new HashSet<Tag>();
		        // get book's authors
		        String authorIdsquery = "SELECT author_id FROM books_authors WHERE book_id = " + mbook_id;
				Statement stmt2 = conn.createStatement();
				ResultSet rsAuthoIds = stmt2.executeQuery(authorIdsquery);
				while (rsAuthoIds.next()) {
					String iauthorId =  rsAuthoIds.getString("author_id");
					String authorsQuery = "SELECT author_name FROM authors WHERE author_id = " + iauthorId;
					Statement stmt4 = conn.createStatement();
					ResultSet rsAuthors = stmt4.executeQuery(authorsQuery);
					while (rsAuthors.next()) {
						String iauthorName = rsAuthors.getString("author_name");
						Author imauthor = new Author(iauthorId, iauthorName);
						mAuthors.add(imauthor);
					}
					stmt4.close();
				}
				stmt2.close();
				// get book's tags
				String tagIdsquery = "SELECT tag_id FROM books_tags WHERE book_id = " + mbook_id;
				Statement stmt5 = conn.createStatement();
				ResultSet rsTagIds = stmt5.executeQuery(tagIdsquery);
				while (rsTagIds.next()) {
					String itagId =  rsTagIds.getString("tag_id");
					String tagsQuery = "SELECT tag_name FROM tags WHERE tag_id = " + itagId;
					Statement stmt6 = conn.createStatement();
					ResultSet rsTags = stmt6.executeQuery(tagsQuery);
					while (rsTags.next()) {
						String itagName = rsTags.getString("tag_name");
						Tag imtag = new Tag(itagId, itagName);
						mTags.add(imtag);
					}
					stmt6.close();
				}
				stmt5.close();
		        // print the results
//		        System.out.format("%s, %s, %.2f, %s\n", mbook_id, mbook_title, mprice);
		        BookBuilder builder = new BookBuilder().setBookId(mbook_id).setTitle(mbook_title).setPrice(mprice).setAuthors(mAuthors).setTags(mTags);
		        Book book = builder.build();
		        res.add(book);
			}
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res.get(0);	
	}
	
	@Override
	public List<Book> getBooks() {
		if (conn == null) {
			return null;
		}
		List<Book> res = new ArrayList<>();
		try {
			String query = "SELECT * FROM books"; 
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
		        String mbook_id = rs.getString("book_id");
		        String mbook_title = rs.getString("title");
		        Float mprice = rs.getFloat("price");
		        Set<Author> mAuthors = new HashSet<Author>();
		        Set<Tag> mTags = new HashSet<Tag>();
		        // get book's authors
		        String authorIdsquery = "SELECT author_id FROM books_authors WHERE book_id = " + mbook_id;
				Statement stmt2 = conn.createStatement();
				ResultSet rsAuthoIds = stmt2.executeQuery(authorIdsquery);
				while (rsAuthoIds.next()) {
					String iauthorId =  rsAuthoIds.getString("author_id");
					// 
					String authorsQuery = "SELECT author_name FROM authors WHERE author_id = " + iauthorId;
					Statement stmt4 = conn.createStatement();
					ResultSet rsAuthors = stmt4.executeQuery(authorsQuery);
					while (rsAuthors.next()) {
						String iauthorName = rsAuthors.getString("author_name");
						Author imauthor = new Author(iauthorId, iauthorName);
						
						
						mAuthors.add(imauthor);
					}
					// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					stmt4.close();
				}
				stmt2.close();
				// get book's tags
				String tagIdsquery = "SELECT tag_id FROM books_tags WHERE book_id = " + mbook_id;
				Statement stmt5 = conn.createStatement();
				ResultSet rsTagIds = stmt5.executeQuery(tagIdsquery);
				while (rsTagIds.next()) {
					String itagId =  rsTagIds.getString("tag_id");
					String tagsQuery = "SELECT tag_name FROM tags WHERE tag_id = " + itagId;
					Statement stmt6 = conn.createStatement();
					ResultSet rsTags = stmt6.executeQuery(tagsQuery);
					while (rsTags.next()) {
						String itagName = rsTags.getString("tag_name");
						Tag imtag = new Tag(itagId, itagName);
						mTags.add(imtag);
					}
					stmt6.close();
				}
				stmt5.close();
		        
		        // print the results
		        System.out.format("%s, %s, %.2f\n", mbook_id, mbook_title, mprice);
		        BookBuilder builder = new BookBuilder().setBookId(mbook_id).setTitle(mbook_title).setPrice(mprice).setAuthors(mAuthors).setTags(mTags);
		        Book book = builder.build();
		        res.add(book);
		      }
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	

	@Override
	public Tag getTag(String tagId) {
		if (conn == null) {
			return null;
		}
		List<Tag> res = new ArrayList<>();
		try {
			String query = "SELECT * FROM tags WHERE tag_id = " + tagId; 
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				String tagName = rs.getString("tag_name");
				Tag tag = new Tag(tagId, tagName);
				res.add(tag);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res.get(0);
	}

	@Override
	public void addTag(String tagId, String tagName) {
		if (conn == null) {
			return;
		}
		try {
			// Add (tag_id, tag_name) into tags table
			String sql = "INSERT IGNORE INTO tags VALUES (?,?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, tagId);
			statement.setString(2, tagName);
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void uplaodFile(String bookId, InputStream is) {
		 try {
			String sql = "INSERT INTO book_file values (?, ?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, bookId);
			if (is != null) {
				statement.setBlob(2, is);
			}
			statement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	@Override
	public Author getAuthor(String authorId) {
		List<Author> mAuthors = new ArrayList<Author>();
		 try {
			String authorsQuery = "SELECT author_name FROM authors WHERE author_id = " + authorId;
			Statement stmt4 = conn.createStatement();
			ResultSet rsAuthors = stmt4.executeQuery(authorsQuery);
			while (rsAuthors.next()) {
				String iauthorName = rsAuthors.getString("author_name");
				Author imauthor = new Author(authorId, iauthorName);
				mAuthors.add(imauthor);
			}
			stmt4.close();
		 } catch (SQLException e) {
				e.printStackTrace();
		}
		 return mAuthors.get(0);
	}

}
