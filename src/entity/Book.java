package entity;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class Book {
	private String bookId;
	private String title;
	private double price;
	private String filePath;
	private Set<Author> authors;
	private Set<Tag> tags;
	
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("book_id", bookId);
			obj.put("title", title);
			obj.put("price", price);
			obj.put("filePath", filePath);
			obj.put("authors", authors);
			obj.put("tags", tags);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public String getBookId() {
		return bookId;
	}
	public void setBookId(String id) {
		this.bookId = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Set<Author> getAuthors() {
		return authors;
	}
	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public Set<Tag> getTags() {
		return tags;
	}
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getfileDirectory() {
		return null;
	}
	
	public String getfileName() {
		return null;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	// Builder Pattern
	private Book(BookBuilder builder) {
		this.bookId = builder.id;
		this.title = builder.title;
		this.authors = builder.authors;
		this.price = builder.price;
		this.tags = builder.tags;
		this.filePath = builder.filePath;
	}
	
	public static class BookBuilder {
		private String id;
		private String title;
		private Set<Author> authors;
		private double price;
		private Set<Tag> tags;
		private String fileDirectory;
		private String fileName;
		private String filePath;
		
		public BookBuilder setBookId(String id) {
			this.id = id;
			return this;
		}
		public BookBuilder setTitle(String title) {
			this.title = title;
			return this;
		}
		
		public BookBuilder setAuthors(Set<Author> authors) {
			this.authors = authors;
			return this;
		}

		public BookBuilder setPrice(double price) {
			this.price = price;
			return this;
		}

		public BookBuilder setTags(Set<Tag> tags) {
			this.tags = tags;
			return this;
		}
		
		public Book build() {
			return new Book(this);
		}
		
		public void setFileDirectory(String fileDirectory) {
			this.fileDirectory = fileDirectory;
		}
		
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		
		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}
	}
}
