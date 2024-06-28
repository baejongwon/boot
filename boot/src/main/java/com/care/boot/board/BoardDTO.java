package com.care.boot.board;
/*
CREATE TABLE board_quiz(
	no number,
	title varchar2(100),
	content varchar2(200),
	id varchar2(20),
	write_date varchar2(15),
	hits number,
	file_name varchar2(255),
	primary key(no)
);
COMMIT;
 */
public class BoardDTO {
	private int no;
	private String title;
	private String content;
	private String id;
	private String writeDate;
	private int hits;
	private String fileName;
	
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	public int getHits() {
		return hits;
	}
	public void setHits(int hits) {
		this.hits = hits;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
}
