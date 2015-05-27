package com.bigdrum.metronomemate.export;

public class Contact {
	private String name;
	private String email;
	private boolean selected;
	
	/**
	 * 
	 * @param name
	 * @param email
	 */
	public Contact(String name, String email) {
		this.name = name;
		this.email = email;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean isSelected() {
		return selected;
	}


	public void setSelected(boolean selected) {
		this.selected = selected;
	}


	public String toString() {
		return name + ": " + email;
	}
	
}
