package com.grupp4.quiznavigator.model;

/**
 * 
 * @author Johan Hugg
 *
 */
public class Question
{
	private String caption;
	private String correctAnswer;
	private String wrongAnswer1;
	private String wrongAnswer2;
	private int longitude;
	private int latitude;
	private int id;
	private int course;
	private String questionOrder;
	
	public Question()
	{
		
	}
	public Question(String caption, String correctAnswer, String wrongAnswer1, String wrongAnswer2, int longitude, int latitude, int course, String questionOrder)
	{
		this.caption = caption;
		this.correctAnswer = correctAnswer;
		this.wrongAnswer1 = wrongAnswer1;
		this.wrongAnswer2 = wrongAnswer2;
		this.longitude = longitude;
		this.latitude = latitude;
		this.course = course;
		this.questionOrder = questionOrder;
	}
	public int getCourse()
	{
		return course;
	}
	public void setCourse(int course)
	{
		this.course = course;
	}
	public String getCaption()
	{
		return caption;
	}
	public void setCaption(String caption)
	{
		this.caption = caption;
	}
	public String getCorrectAnswer()
	{
		return correctAnswer;
	}
	public void setCorrectAnswer(String correctAnswer)
	{
		this.correctAnswer = correctAnswer;
	}
	public String getWrongAnswer1()
	{
		return wrongAnswer1;
	}
	public void setWrongAnswer1(String wrongAnswer1)
	{
		this.wrongAnswer1 = wrongAnswer1;
	}
	public String getWrongAnswer2()
	{
		return wrongAnswer2;
	}
	public void setWrongAnswer2(String wrongAnswer2)
	{
		this.wrongAnswer2 = wrongAnswer2;
	}
	public int getLongitude()
	{
		return longitude;
	}
	public void setLongitude(int longitude)
	{
		this.longitude = longitude;
	}
	public int getLatitude()
	{
		return latitude;
	}
	public void setLatitude(int latitude)
	{
		this.latitude = latitude;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int i)
	{
		this.id = i;
	}
	@Override
	public String toString()
	{
		return new String(this.getCaption() + ", " + this.getCorrectAnswer() + ", "
				+ this.getWrongAnswer1() + ", " + this.getWrongAnswer2() + ", " + this.getLongitude() + 
				", " + this.getLatitude() + ", " + this.getCourse());
		
	}
	public void setQuestionOrder(String questionOrder)
	{
		this.questionOrder = questionOrder;
	}
	public String getQuestionOrder()
	{
		return this.questionOrder;
	}
}
