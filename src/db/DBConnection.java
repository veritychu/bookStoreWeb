package db;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import entity.Author;
import entity.Book;
import entity.Tag;

public interface DBConnection {
	/**
	 * Close the connection.
	 */
	public void close();
	
	/**
	 * Upload a book to the database 
	 * 
	 */
	public void addBook(Book book);
	
	/**
	 * Delete a book from db
	 * rpc example: http://localhost:8080/BookStoreWeb/DeleteBook?book_id=1113
	 * @param bookId
	 */
	public void deleteBook(String bookId);
	
	/**
	 * Label a existing book with an existing tag
	 * @param bookId alreay exist 
	 * @param tagId already exist 
	 */
	public void setBookTag(String bookId, String tagId);
	
	/**
	 * Unlabel a book from a tag it already has
	 * rpc example: http://localhost:8080/BookStoreWeb/UnsetBookTag?book_id=1111&tag_id=0001
	 * @param bookId
	 * @param tagId tag_id of the tag to remove from a book
	 */
	public void unsetBookTag(String bookId, String tagId);
	
	/**
	 * Rename an existing tag's tag_name
	 * rpc  example: http://localhost:8080/BookStoreWeb/RenameTag?tag_id=0001&tag_name="aha"
	 * you can only rename a tag when it exists, or else the console will 
	 * output this notice and print exception track
	 * @param tagId
	 */
	public void renameTag(String tagId, String newTagName);
	
	/**
	 * Return limit number of books from db
	 * @param limit
	 * @return
	 */
	public Set<Book> getBooksByNumberFilter(int limit);
	
	/**
	 * Return books labeled with a given tagId
	 * rpc example: http://localhost:8080/BookStoreWeb/searchTag?tag_id=0000
	 * @param tagId
	 * @return
	 */
	public Set<Book> getBooksByTagFilter(String tagId);
	
	/**
	 * Return books written by a given author 
	 * rpc example: http://localhost:8080/BookStoreWeb/searchAuthor?author_id=2222
	 * @param authorId
	 * @return
	 */
	public Set<Book> getBooksByAuthorFilter(String authorId);
	
	/**
	 * Gets all tags of a given book
	 * 
	 * rpc example: http://localhost:8080/BookStoreWeb/FindBookTags?book_id=1111
	 * 
	 * @param bookId
	 * @return set of Tags
	 */
	public Set<Tag> getBookTags(String bookId);
	
	/**
	 * Get tag by its tag_id
	 * rpc example: http://localhost:8080/BookStoreWeb/FindTag?tag_id=0001
	 * @param tagId
	 * @return
	 */
	public Tag getTag(String tagId);
	
	/**
	 * Add a tag into the db
	 * rpc example: http://localhost:8080/BookStoreWeb/AddTag?tag_id=0002&tag_name=tagNameTesting3
	 * @param tagId
	 * @param tagName
	 */
	public void addTag(String tagId, String tagName);
	
	
	/**
	 * Get a book with given bookId 
	 * rpc example: http://localhost:8080/BookStoreWeb/SearchByBookId?book_id=1111
	 * @param bookId
	 * @return
	 */
	public Book getBook(String bookId);
	
	/**
	 * Get all the books from the bookstore
	 * rpc example: http://localhost:8080/BookStoreWeb/search
	 * @return
	 */
	public List<Book> getBooks();

	public void uplaodFile(String bookId, InputStream is);
	
	public Author getAuthor(String authorId);
}
