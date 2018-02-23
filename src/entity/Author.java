package entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Author {
	private String authorId;
	private String authorName;
	
	public Author(String authorId, String authorName) {
		this.authorId = authorId;
		this.authorName = authorName;
	}
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("authorId", authorId);
			obj.put("authorName", authorName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	public String getAuthorId() {
		return authorId;
	}
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	

}
