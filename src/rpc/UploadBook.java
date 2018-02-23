package rpc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Author;
import entity.Book;
import entity.Book.BookBuilder;
import entity.Tag;

/**
 * Servlet implementation class UploadBook
 */
@WebServlet("/UploadBook")
@MultipartConfig(maxFileSize = 16177215) // upload file's size up to 16MB
public class UploadBook extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadBook() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * 
	 * 
	 */
	/**
	 * The folloing should go into the POST request's body
	 * 
	 {
    "price": 100,
    "book_id": "1112",
    "title": "bookTitleTesting2",
    "filePath": "/Users/bingchu/Documents/workspace/BookStore/BookStoreWeb/src/rpc",
    "authors": [
        {
            "authorName": "authorNameTesting2",
            "authorId": "2223"
        }
    ],
    "tags": [
        {
            "tagId": "0000",
            "tagName": "tagNameTesting1"
        },
        {
            "tagId": "0001",
            "tagName": "tagNameTesting2"
        }
    ]
}	 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getDBConnection();
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String mbook_id = input.getString("book_id");
			System.out.println(mbook_id);
			String mbook_title = input.getString("title");
			System.out.println(mbook_title);
			Double mbook_price = input.getDouble("price");
			System.out.println(mbook_price);
			JSONArray mbook_authors = (JSONArray) input.get("authors");
			JSONArray mbook_tags = (JSONArray) input.get("tags");
			String filePath = input.getString("filePath");
//			System.out.println("l142check: " + filePath);
			
			Set<Author> mAuthors = new HashSet<>();
			for (int i = 0; i < mbook_authors.length(); i++) {
				JSONObject authorObj = (JSONObject) mbook_authors.get(i);
				String authorId = authorObj.getString("authorId");
				String authorName = authorObj.getString("authorName");
				Author author = new Author(authorId,authorName );
	        	mAuthors.add(author);
			}
			
			Set<Tag> mTags = new HashSet<>();
			for (int i = 0; i < mbook_tags.length(); i++) {
				JSONObject tagObj = (JSONObject) mbook_tags.get(i);
				String tagId = tagObj.getString("tagId");
				String tagName = tagObj.getString("tagName");
				Tag tag = new Tag(tagId,tagName );
				mTags.add(tag);
			}
			BookBuilder builder = new BookBuilder().setBookId(mbook_id).setTitle(mbook_title).setPrice(mbook_price).setAuthors(mAuthors).setTags(mTags);
			builder.setFilePath(filePath);
			Book book = builder.build();
//			System.out.println(book.getFilePath());
			conn.addBook(book);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
