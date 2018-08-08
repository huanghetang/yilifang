package cn.e3mal.common;

import java.io.Serializable;

public class EasyUIDateTreeResutl implements Serializable{
	private Long id;
	private String state;
	private String text;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	

}
