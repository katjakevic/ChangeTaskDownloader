package ctdownloader.model;

public class CommentModel {
	
	private String content;
	private String author;
	private String date; 
	
	public CommentModel(String commentContent, String commentAuthor, 
			String commentDate){
		this.content = commentContent;
		this.author = commentAuthor;
		this.date = commentDate;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String content){
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
