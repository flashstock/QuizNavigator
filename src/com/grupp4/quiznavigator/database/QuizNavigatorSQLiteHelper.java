	
package com.grupp4.quiznavigator.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * @author Johan Hugg
 * @author Erik Skoog
 *
 */
public class QuizNavigatorSQLiteHelper extends SQLiteOpenHelper
{
	
	public static final String ACCOUNT = "account";
	public static final String ACCOUNT_USERNAME = "username";
	public static final String ACCOUNT_PASSWORD = "password";
	public static final String ACCOUNT_ID = "account_id";
	
	public static final String LOGGED_IN = "logged_in";
	public static final String LOGGED_USER = "logged_username";
	public static final String LOGGED_PASS = "logged_password";
	public static final String LOGGED_ID = "logged_id";
	
	public static final String COURSE = "course";
	public static final String COURSE_NAME = "name";
	public static final String COURSE_LENGTH = "length";
	public static final String COURSE_NUMBEROFQUESTIONS = "number_of_questions";
	public static final String COURSE_AVERAGERATING = "average_rating";
	public static final String COURSE_NUMBEROFRATINGS = "number_of_ratings";
	public static final String COURSE_CREATOR = "creator";
	public static final String COURSE_CREATIONDATE = "creation_date";
	public static final String COURSE_ID = "course_id";
	
	public static final String QUESTION = "question";
	public static final String QUESTION_CAPTION = "caption";
	public static final String QUESTION_CORRECTANSWER = "correct_answer";
	public static final String QUESTION_WRONGANSWER1 = "wrong_answer1";
	public static final String QUESTION_WRONGANSWER2 = "wrong_answer2";
	public static final String QUESTION_LONGITUDE = "longitude";
	public static final String QUESTION_LATITUDE = "latitude";
	public static final String QUESTION_ID = "question_id";
	public static final String QUESTION_COURSE = "question_course";
	public static final String QUESTION_ORDER = "question_order";
	
	private static QuizNavigatorSQLiteHelper myHelper;
	private static final String DATABASE_NAME = "quiznavigator.db";
	private static final int DATABASE_VERSION = 22;

	private static final String DATABASE_CREATE_ACCOUNT = "create table "
			+ ACCOUNT + "(" + ACCOUNT_ID + " integer primary key autoincrement, " + ACCOUNT_USERNAME
			+ " text unique not null, " + ACCOUNT_PASSWORD + " text not null);";
	
	private static final String DATABASE_CREATE_LOGGED_IN = "create table "
			+ LOGGED_IN + "(" + LOGGED_USER + " text not null, " + LOGGED_PASS
			+ " text not null, " + LOGGED_ID + " integer primary key);";
	
	private static final String DATABASE_CREATE_COURSE = "create table "
			+ COURSE + "(" + COURSE_ID
			+ " integer primary key autoincrement, " + COURSE_NAME 
			+ " text not null, " + COURSE_LENGTH + " real not null, " + COURSE_NUMBEROFQUESTIONS 
			+ " integer not null, " + COURSE_AVERAGERATING + " text not null, " + COURSE_NUMBEROFRATINGS
			+ " integer not null, " + COURSE_CREATOR + " text not null, " + COURSE_CREATIONDATE
			+ " text not null);";
	
	private static final String DATABASE_CREATE_QUESTION = "create table "
			+ QUESTION + "(" + QUESTION_ID
			+ " integer primary key autoincrement, " + QUESTION_CAPTION
			+ " text not null, " + QUESTION_CORRECTANSWER + " text not null, " + QUESTION_WRONGANSWER1
			+ " text not null, " + QUESTION_WRONGANSWER2 + " text not null, " + QUESTION_LATITUDE
			+ " real not null, " + QUESTION_LONGITUDE
			+ " real not null, " + QUESTION_COURSE + " integer not null, " + QUESTION_ORDER + " text not null);";

	private QuizNavigatorSQLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/**
	 * Returns the QuizNavigatorSQLiteHelper instance.
	 * @param context
	 * @return QuizNavigatorSQLiteHelper
	 */
	public static QuizNavigatorSQLiteHelper getInstance(Context context)
	{
		if (myHelper == null)
			myHelper = new QuizNavigatorSQLiteHelper(context.getApplicationContext());
		return myHelper;
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) 
	{
		database.execSQL(DATABASE_CREATE_COURSE);
		Log.d("db", DATABASE_CREATE_COURSE);
		database.execSQL(DATABASE_CREATE_ACCOUNT);
		Log.d("db", DATABASE_CREATE_ACCOUNT);
		database.execSQL(DATABASE_CREATE_QUESTION);
		Log.d("db", DATABASE_CREATE_QUESTION);
		database.execSQL(DATABASE_CREATE_LOGGED_IN);
		Log.d("db", DATABASE_CREATE_LOGGED_IN);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		Log.w(QuizNavigatorSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + "to " + newVersion + "which will destroy all old data.");
		db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT);
		db.execSQL("DROP TABLE IF EXISTS " + COURSE);
		db.execSQL("DROP TABLE IF EXISTS " + QUESTION);
		db.execSQL("DROP TABLE IF EXISTS " + LOGGED_IN);
		onCreate(db);
	}

}

