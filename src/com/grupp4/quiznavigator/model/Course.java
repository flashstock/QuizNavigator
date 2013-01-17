package com.grupp4.quiznavigator.model;

/**
 * 
 * @author Johan Hugg
 *
 */
public class Course
{
	private String name;
	private float length;
	private int noOfQuestions;
	private int averageRating;
	private int noOfRatings;
	private String creator;
	private String creationDate;
	private int id;
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public float getLength()
	{
		return length;
	}
	public void setLength(float f)
	{
		this.length = f;
	}
	public int getNoOfQuestions()
	{
		return noOfQuestions;
	}
	public void setNoOfQuestions(int noOfQuestions)
	{
		this.noOfQuestions = noOfQuestions;
	}
	public int getAverageRating()
	{
		return averageRating;
	}
	public void setAverageRating(int averageRating)
	{
		this.averageRating = averageRating;
	}
	public int getNoOfRatings()
	{
		return noOfRatings;
	}
	public void setNoOfRatings(int noOfRatings)
	{
		this.noOfRatings = noOfRatings;
	}
	public String getCreator()
	{
		return creator;
	}
	public void setCreator(String creator)
	{
		this.creator = creator;
	}
	public String getCreationDate()
	{
		return creationDate;
	}
	public void setCreationDate(String creationDate)
	{
		this.creationDate = creationDate;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	@Override
	public String toString()
	{
		return new String(this.getName() + ", " + this.getLength() + ", "
				+ this.getNoOfQuestions() + ", " + this.getAverageRating() + ", " + this.getNoOfRatings() + 
				", " + this.getCreator() + ", " + this.getCreationDate());
	}
}
