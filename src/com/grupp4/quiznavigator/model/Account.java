package com.grupp4.quiznavigator.model;

/**
 * 
 * @author Johan Hugg
 *
 */
public class Account 
{
	
	private String name;
	private String password;
	private int id;
	
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public String getPassword() 
	{
		return password;
	}
	
	public void setPassword(String password) 
	{
		this.password = password;
	}
	@Override
	public String toString()
	{
		return new String(getName() + ", " + getPassword());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}