package entity;

import org.json.JSONException;
import org.json.JSONObject;

import entity.Book.BookBuilder;

public class Tag {
	private String tagName;
	private String tagId;
	
	public Tag(String tagId, String tagName) {
		this.tagId = tagId;
		this.tagName = tagName;
	}
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("tagId", tagId);
			obj.put("tagName", tagName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public String getTagName() {
		return tagName;
	}
	
	public String getTagId() {
		return tagId;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
}
