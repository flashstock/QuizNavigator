package com.grupp4.quiznavigator.database;

import java.util.ArrayList;
import java.util.List;

import com.grupp4.quiznavigator.model.Account;
import com.grupp4.quiznavigator.model.Course;
import com.grupp4.quiznavigator.model.LoggedUser;
import com.grupp4.quiznavigator.model.Question;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author Johan Hugg
 * @author Erik Skoog
 * 
 */
public class QuizNavigatorDataSource 
{

  // Database fields
  private static SQLiteDatabase database;
  private QuizNavigatorSQLiteHelper dbHelper;
  private String[] accountAllColumns = 
	  	{ 
		  QuizNavigatorSQLiteHelper.ACCOUNT_USERNAME,
		  QuizNavigatorSQLiteHelper.ACCOUNT_PASSWORD,
		  QuizNavigatorSQLiteHelper.ACCOUNT_ID
	  	};
  private String[] questionAllColumns = 
	  	{ 
		  QuizNavigatorSQLiteHelper.QUESTION_ID,
		  QuizNavigatorSQLiteHelper.QUESTION_CAPTION,
		  QuizNavigatorSQLiteHelper.QUESTION_CORRECTANSWER,
		  QuizNavigatorSQLiteHelper.QUESTION_WRONGANSWER1,
		  QuizNavigatorSQLiteHelper.QUESTION_WRONGANSWER2,
		  QuizNavigatorSQLiteHelper.QUESTION_LATITUDE,
		  QuizNavigatorSQLiteHelper.QUESTION_LONGITUDE,
		  QuizNavigatorSQLiteHelper.QUESTION_COURSE,
		  QuizNavigatorSQLiteHelper.QUESTION_ORDER
		};
  private String[] courseAllColumns = 
	  	{ 
		  QuizNavigatorSQLiteHelper.COURSE_ID,
		  QuizNavigatorSQLiteHelper.COURSE_NAME,
		  QuizNavigatorSQLiteHelper.COURSE_LENGTH,
		  QuizNavigatorSQLiteHelper.COURSE_NUMBEROFQUESTIONS,
		  QuizNavigatorSQLiteHelper.COURSE_AVERAGERATING,
		  QuizNavigatorSQLiteHelper.COURSE_NUMBEROFRATINGS,
		  QuizNavigatorSQLiteHelper.COURSE_CREATOR,
		  QuizNavigatorSQLiteHelper.COURSE_CREATIONDATE,
		  
	  	};
  private String[] loggedInAllColumns =
	  {
		  QuizNavigatorSQLiteHelper.LOGGED_USER,
		  QuizNavigatorSQLiteHelper.LOGGED_PASS,
		  QuizNavigatorSQLiteHelper.LOGGED_ID
	  };
  
  private static QuizNavigatorDataSource myDatasrc;

  private  QuizNavigatorDataSource(Context context) 
  {
	  dbHelper = QuizNavigatorSQLiteHelper.getInstance(context);
  }
  
  /**
   * Returns the DataSource instance.
   * @param context
   * @return QuizNavigatorDataSource
   */
  public static QuizNavigatorDataSource getInstance(Context context)
  {
	  if (myDatasrc == null)
	  {
		  myDatasrc = new QuizNavigatorDataSource(context.getApplicationContext());
	  }
	  return myDatasrc;
  }
  
  /**
   * Opens a writable database.
   * @throws SQLException
   */
  public void open() throws SQLException 
  {
	  database = dbHelper.getWritableDatabase();
  }
  
  /**
   * Closes the database.
   */
  public void close() 
  {
	  dbHelper.close();
  }
  
  /**
   * Creates an account in the database and returns the account in java object form.
   * @param username The username to be registered
   * @param password The password of the user
   * @param email Used to specify whether or not the username should be an email adress.
   * @return The created Account
   */
  public Account createAccount(String username, String password, boolean email) 
  {
	  ContentValues values = new ContentValues();
	  values.put(QuizNavigatorSQLiteHelper.ACCOUNT_USERNAME, username);
	  values.put(QuizNavigatorSQLiteHelper.ACCOUNT_PASSWORD, password);
	  long insertId = database.insert(QuizNavigatorSQLiteHelper.ACCOUNT, null,
			  values);
	  Cursor cursor = database.query(QuizNavigatorSQLiteHelper.ACCOUNT,
			  accountAllColumns, QuizNavigatorSQLiteHelper.ACCOUNT_ID + " = " + insertId, null,
			  null, null, null);
	  cursor.moveToFirst();
	  Account newAccount = cursorToAccount(cursor);
	  cursor.close();
	  return newAccount;
  }
  
  /**
   * Creates the user that is logged in.
   * @param username
   * @param password
   * @return The now logged in user
   */
  public LoggedUser createLoggedUser(String username, String password)
  {
	  ContentValues values = new ContentValues();
	  values.put(QuizNavigatorSQLiteHelper.LOGGED_USER, username);
	  values.put(QuizNavigatorSQLiteHelper.LOGGED_PASS, password);
	  values.put(QuizNavigatorSQLiteHelper.LOGGED_ID, "1");
	  long insertId = database.insert(QuizNavigatorSQLiteHelper.LOGGED_IN, null,
	  values);
	  
	  Cursor cursor = database.query(QuizNavigatorSQLiteHelper.LOGGED_IN,
			  loggedInAllColumns, QuizNavigatorSQLiteHelper.LOGGED_ID + " = " + insertId, null,
			  null, null, null);
	  
	  cursor.moveToFirst();
	  LoggedUser newLUser = cursorToLoggedInUser(cursor);
	  cursor.close();
	  return newLUser;
  }
  
  /**
   * Signs out the user, removing the logged in user from the database
   */
  public void removeLoggedUser()
  {
	  database.delete(QuizNavigatorSQLiteHelper.LOGGED_IN, null, null);
  }
  
  /**
   * Gets all accounts in the database.
   * @return List<Account>
   */
  public List<Account> getAllAccounts() 
  {
	  List<Account> accounts = new ArrayList<Account>();

	  Cursor cursor = database.query(QuizNavigatorSQLiteHelper.ACCOUNT,
			  accountAllColumns, null, null, null, null, null);

	  cursor.moveToFirst();
	  while (!cursor.isAfterLast()) {
		  Account account = cursorToAccount(cursor);
		  accounts.add(account);
		  cursor.moveToNext();
	  }
	  // Make sure to close the cursor
	  cursor.close();
	  return accounts;
	  
  }
  
  /**
   * 
   * @return The logged in user
   */
  public LoggedUser getLoggedInUser()
  {
	  LoggedUser user;
	  
	  Cursor cursor = database.query(QuizNavigatorSQLiteHelper.LOGGED_IN,
			  loggedInAllColumns, null, null, null, null, null);
	  
	  cursor.moveToFirst();
	  user = cursorToLoggedInUser(cursor);
	  cursor.close();
	  return user;
  }
  
  /**
   * Creates a new question in the database and return the question in java object form.
   * @param caption The question
   * @param correctAnswer
   * @param wrongAnswer1
   * @param wrongAnswer2
   * @param longitude The longitude of the question on the map
   * @param latitude The longitude of the question on the map
   * @param course The id of the course the question is connected to
   * @return The created Question
   */
  public Question createQuestion
  (String caption, String correctAnswer, 
   String wrongAnswer1, String wrongAnswer2, int longitude,
   int latitude, int course, String order)
  {
	  ContentValues values = new ContentValues();
	  
	  values.put(QuizNavigatorSQLiteHelper.QUESTION_CAPTION,caption);
	  values.put(QuizNavigatorSQLiteHelper.QUESTION_CORRECTANSWER,correctAnswer);
	  values.put(QuizNavigatorSQLiteHelper.QUESTION_WRONGANSWER1,wrongAnswer1);
	  values.put(QuizNavigatorSQLiteHelper.QUESTION_WRONGANSWER2,wrongAnswer2);
	  values.put(QuizNavigatorSQLiteHelper.QUESTION_LONGITUDE,longitude);
	  values.put(QuizNavigatorSQLiteHelper.QUESTION_LATITUDE,latitude);
	  values.put(QuizNavigatorSQLiteHelper.QUESTION_COURSE,course);
	  values.put(QuizNavigatorSQLiteHelper.QUESTION_ORDER, order);

	  long insertId = database.insert(QuizNavigatorSQLiteHelper.QUESTION, null,
			  values);
	  Cursor cursor = database.query(QuizNavigatorSQLiteHelper.QUESTION,
			  questionAllColumns, QuizNavigatorSQLiteHelper.QUESTION_ID + " = " + insertId, null,
			  null, null, null);
	  cursor.moveToFirst();
	  Question newQuestion = cursorToQuestion(cursor);
	  cursor.close();
	  return newQuestion;  
  }
  
  /**
   * Gets all questions relevant to the course id, for example id 1 will get all questions that are associated with that id.
   * @param courseId The course id
   * @return A list of all the Questions
   */
  public List<Question> getAllQuestions(int courseId)
  {
	List<Question> questions = new ArrayList<Question>();
	Cursor cursor = database.rawQuery("select * from " + QuizNavigatorSQLiteHelper.QUESTION + " where " + QuizNavigatorSQLiteHelper.QUESTION_COURSE + "=" + courseId, null);
	
	
	cursor.moveToFirst();
	while (!cursor.isAfterLast()) {
	      Question question = cursorToQuestion(cursor);
	      questions.add(question);
	      cursor.moveToNext();
	}
	    // Make sure to close the cursor
	cursor.close();
	return questions;
  }
  
  /**
   * Creates a new course in the database and returns the course in java object form.
   * @param name The name of the course
   * @param creator The creator of the course
   * @param date The date which the course was made
   * @return The created Course
   */
  public Course createCourse(String name, String creator, String date)
  {
	  return createCourse(name, 0, 0, 0, 0, creator, date);
  }
  
  public Course createCourse(String name, String creator, String date, int numberOfQuestions)
  {
	  return createCourse(name, 0, numberOfQuestions, 0, 0, creator, date);
  }
  
  public Course createCourse(String name, float length, String creator, String date, int numberOfQuestions)
  {
	  return createCourse(name, length, numberOfQuestions, 0, 0, creator, date);
  }
  /**
   * 
   * @param name The name of the course
   * @param length The length of the course in meters
   * @param numberOfQuestions The number of questions in the course
   * @param averageRating The average rating of the course
   * @param numberOfRatings The number of ratings for the course
   * @param creator The creater of the course
   * @param creationDate The date which the course was made
   * @return The created course
   */
  public Course createCourse(String name, float length, int numberOfQuestions,
		  int averageRating, int numberOfRatings, String creator, String creationDate)
  {
	  ContentValues values = new ContentValues();
	  
	  
	  values.put(QuizNavigatorSQLiteHelper.COURSE_NAME,name);
	  values.put(QuizNavigatorSQLiteHelper.COURSE_LENGTH,length);
	  values.put(QuizNavigatorSQLiteHelper.COURSE_NUMBEROFQUESTIONS,numberOfQuestions);
	  values.put(QuizNavigatorSQLiteHelper.COURSE_AVERAGERATING,averageRating);
	  values.put(QuizNavigatorSQLiteHelper.COURSE_NUMBEROFRATINGS,numberOfRatings);
	  values.put(QuizNavigatorSQLiteHelper.COURSE_CREATOR,creator);
	  values.put(QuizNavigatorSQLiteHelper.COURSE_CREATIONDATE,creationDate);
	  

	  long insertId = database.insert(QuizNavigatorSQLiteHelper.COURSE, null,
			  values);
	  
	  Cursor cursor = database.query(QuizNavigatorSQLiteHelper.COURSE,
			  courseAllColumns, QuizNavigatorSQLiteHelper.COURSE_ID + " = " + insertId, null,
			  null, null, null);
	  cursor.moveToFirst();
	  Course newCourse = cursorToCourse(cursor);
	  cursor.close();
	  return newCourse;
  }
  
  /**
   * Returns all courses associated with a username then returns a list with the courses.
   * @param creator The creator of the course
   * @return A list of courses
   */
  public List<Course> getAllCourses(String creator)
  {
	  List<Course> courses = new ArrayList<Course>();
	  Cursor cursor = database.rawQuery("select * from " + QuizNavigatorSQLiteHelper.COURSE + " where " + QuizNavigatorSQLiteHelper.COURSE_CREATOR + "='" + creator + "'", null);
	  cursor.moveToFirst();
	  while (!cursor.isAfterLast()) 
	  {
		  Course course = cursorToCourse(cursor);
		  courses.add(course);
		  cursor.moveToNext();
	  }
	  // Make sure to close the cursor
	  cursor.close();
	  return courses;
  }
  
  /**
   * Gets the information from the database cursor and makes an Account instance from it.
   * @param cursor
   * @return Account made from cursor information
   */
  private Account cursorToAccount(Cursor cursor) 
  {
	  Account account = new Account();
	  account.setName(cursor.getString(0));
	  account.setPassword(cursor.getString(1));
	  account.setId(cursor.getInt(2));
	  return account;
  }
  
  /**
   * @param cursor The database cursor
   * @return LoggedUser made from cursor information
   */
  private LoggedUser cursorToLoggedInUser(Cursor cursor)
  {
	 LoggedUser user = new LoggedUser();
	 user.setUsername(cursor.getString(0));
	 user.setPassword(cursor.getString(1));
	 user.setId(cursor.getInt(2));
	 return user;
  }
  
  /**
   * Gets the information from the database cursor and makes a Course instance from it.
   * @param cursor The database cursor
   * @return Course made from cursor information
   */
  private Course cursorToCourse(Cursor cursor)
  {
	  /*
	   * QuizNavigatorSQLiteHelper.COURSE_NAME,
		  QuizNavigatorSQLiteHelper.COURSE_LENGTH,
		  QuizNavigatorSQLiteHelper.COURSE_NUMBEROFQUESTIONS,
		  QuizNavigatorSQLiteHelper.COURSE_AVERAGERATING,
		  QuizNavigatorSQLiteHelper.COURSE_NUMBEROFRATINGS,
		  QuizNavigatorSQLiteHelper.COURSE_CREATOR,
		  QuizNavigatorSQLiteHelper.COURSE_CREATIONDATE,
		  QuizNavigatorSQLiteHelper.COURSE_ID
	   */
	  Course course = new Course();
	  
	  course.setId(cursor.getInt(0));
	  course.setName(cursor.getString(1));
	  course.setLength(cursor.getFloat(2));
	  course.setNoOfQuestions(cursor.getInt(3));
	  course.setAverageRating(cursor.getInt(4));
	  course.setNoOfRatings(cursor.getInt(5));
	  course.setCreator(cursor.getString(6));
	  course.setCreationDate(cursor.getString(7));
	  
	  return course;
  }
  
  /**
   * Gets the information from the database cursor and makes a Question instance from it.
   * @param cursor The database cursor
   * @return Question made from cursor information
   */
  private Question cursorToQuestion(Cursor cursor)
  {
	  /*
	   * QuizNavigatorSQLiteHelper.QUESTION_ID,
		  QuizNavigatorSQLiteHelper.QUESTION_CAPTION,
		  QuizNavigatorSQLiteHelper.QUESTION_CORRECTANSWER,
		  QuizNavigatorSQLiteHelper.QUESTION_WRONGANSWER1,
		  QuizNavigatorSQLiteHelper.QUESTION_WRONGANSWER2,
		  QuizNavigatorSQLiteHelper.QUESTION_LATITUDE,
		  QuizNavigatorSQLiteHelper.QUESTION_LONGITUDE,
		  QuizNavigatorSQLiteHelper.QUESTION_COURSE,
		  QuizNavigatorSQLiteHelper.QUESTION_ORDER
	   */
	  Question question = new Question();
	  
	  question.setId(cursor.getInt(0));
	  question.setCaption(cursor.getString(1));
	  question.setCorrectAnswer(cursor.getString(2));
	  question.setWrongAnswer1(cursor.getString(3));
	  question.setWrongAnswer2(cursor.getString(4));
	  question.setLatitude(cursor.getInt(5));
	  question.setLongitude(cursor.getInt(6));
	  question.setCourse(cursor.getInt(7));
	  question.setQuestionOrder(cursor.getString(8));
	  
	  return question;
  }
  
} 
