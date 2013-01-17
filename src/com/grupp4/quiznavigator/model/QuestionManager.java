package com.grupp4.quiznavigator.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.grupp4.quiznavigator.R;
import com.grupp4.quiznavigator.database.QuizNavigatorDataSource;
import com.grupp4.quiznavigator.util.HuggUtils;

/**
 * 
 * @author Johan Hugg
 * An attempt to manage questions in the mapactivity more easily, this makes it 
 * so that the mapactivity doesn't have to do everything and can instead rely on 
 * the manager to make sure question indexes and everything else is more easily mantained.
 */
public class QuestionManager
{
	private int questionIndex;
	private int questionIndexMax;
	private int radius = 10; //Radius in meters in which to search for questions to answer
	private boolean hasAnsweredQuestion = false;
	DecimalFormat format = new DecimalFormat("#.##");
	
	//Questions that user has set
	private List<Question> setQuestions = new ArrayList<Question>();
	
	//Question answers by user
	private List<String> userAnswers = new ArrayList<String>();
	
	//Correct question answers
	private List<String> correctAnswers = new ArrayList<String>();
	
	//Questions received from course
	private List<Question> courseQuestions = new ArrayList<Question>();
	
	QuizNavigatorDataSource dbSrc;
	Context mContext;
	
	private static int[] redFlags =
		{
			  R.drawable.r1f,
			  R.drawable.r2f,
			  R.drawable.r3f,
			  R.drawable.r4f,
			  R.drawable.r5f,
			  R.drawable.r6f,
			  R.drawable.r7f,
			  R.drawable.r8f,
			  R.drawable.r9f,
			  R.drawable.r10f,
			  R.drawable.r11f,
			  R.drawable.r12f,
			  R.drawable.r13f,
			  R.drawable.r14f,
			  R.drawable.r15f,
			  R.drawable.r16f,
			  R.drawable.r17f,
			  R.drawable.r18f,
			  R.drawable.r19f,
			  R.drawable.r20f,
			  R.drawable.r21f,
			  R.drawable.r22f,
			  R.drawable.r23f,
			  R.drawable.r24f,
			  R.drawable.r25f,
			  R.drawable.r26f,
			  R.drawable.r27f,
			  R.drawable.r28f,
			  R.drawable.r29f,
			  R.drawable.r30f
		};
	
	private static int[] greenFlags = 
		{
			R.drawable.g1f,
			R.drawable.g2f,
			R.drawable.g3f,
			R.drawable.g4f,
			R.drawable.g5f,
			R.drawable.g6f,
			R.drawable.g7f,
			R.drawable.g8f,
			R.drawable.g9f,
			R.drawable.g10f,
			R.drawable.g11f,
			R.drawable.g12f,
			R.drawable.g13f,
			R.drawable.g14f,
			R.drawable.g15f,
			R.drawable.g16f,
			R.drawable.g17f,
			R.drawable.g18f,
			R.drawable.g19f,
			R.drawable.g20f,
			R.drawable.g21f,
			R.drawable.g22f,
			R.drawable.g23f,
			R.drawable.g24f,
			R.drawable.g25f,
			R.drawable.g26f,
			R.drawable.g27f,
			R.drawable.g28f,
			R.drawable.g29f,
			R.drawable.g30f
		};
	
	public QuestionManager(Context context, int courseId)
	{
		dbSrc = QuizNavigatorDataSource.getInstance(context);
		courseQuestions = dbSrc.getAllQuestions(courseId);
		questionIndex = 0;
		questionIndexMax = courseQuestions.size();
	}
	
	public Question getCurrentQuestion()
	{
		return courseQuestions.get(questionIndex);
	}
	
	/**
	 * Adds an answer to a question
	 * @param answer
	 */
	public void addUserAnswer(String answer)
	{
		userAnswers.add(answer);
		hasAnsweredQuestion = true;
		questionIndex++;
	}
	
	public Question getQuestion(int index)
	{
		return courseQuestions.get(index);
	}
	
	public List<String> getUserAnswers()
	{
		return userAnswers;
	}
	
	/**
	 * Adds a userdefined question to the course
	 * @param question
	 */
	public void addSetQuestion(Question question)
	{
		setQuestions.add(question);
		questionIndex++;
	}
	
	/**
	 * Makes overlayitems out of the list that contains questions to be placed on map.
	 * @return
	 */
	public List<OverlayItem> getQuestionOverlayItems()
	{
		List<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
		int i = 0;
		for (Question q : courseQuestions)
		{
			correctAnswers.add(q.getCorrectAnswer());
			overlayItems.add(new OverlayItem(new GeoPoint(q.getLongitude(), q.getLatitude()), "Question " + (++i) + ": " + q.getCaption(), ""));
		}
		return overlayItems;
	}
	
	public List<String> getCorrectAnswers()
	{
		return correctAnswers;
	}
	
	/**
	 * Checks if the user is near a question
	 * @param myLocation the user location
	 * @return True if near, false if not
	 */
	public boolean nearNextQuestion(GeoPoint myLocation)
	{
		try
		{
			if(myLocation != null && courseQuestions != null)
			{
				float distance = HuggUtils.getDistanceBetween(myLocation, new GeoPoint(courseQuestions.get(questionIndex).getLongitude(), courseQuestions.get(questionIndex).getLatitude()));
				if (distance < radius)
					return true;
				else
					return false;
			}
			else
				return false;
		}
		catch(Exception e)
		{
			Log.d("QuestionManager", e.getMessage());
			return false;
		}
	}
	
	/**
	 * Checks if the user is near a question
	 * @param myLocation the user location
	 * @return True if near, false if not
	 */
	public boolean nearNextQuestion(Location myLocation)
	{
		return nearNextQuestion(HuggUtils.locationToGeoPoint(myLocation));
	}
	
	public int getCurrentQuestionIndex()
	{
		return this.questionIndex;
	}
	
	public void setCurrentQuestionIndex(int index)
	{
		this.questionIndex = index;
	}
	
	public void setCurrentQuestionIndexMax(int index)
	{
		this.questionIndexMax = index;
	}
	
	public int getCurrentQuestionIndexMax(int index)
	{
		return questionIndexMax;
	}
	
	public boolean isLastQuestion()
	{
		if (questionIndex == questionIndexMax)
			return true;
		else
			return false;
	}
	
	/**
	 * Registers the course in the database
	 * @param course The course to be registered
	 */
	public void registerCourse(Course course)
	{
		for (Question q : setQuestions)
		{
			dbSrc.createQuestion(q.getCaption(), q.getCorrectAnswer(), q.getWrongAnswer1(), q.getWrongAnswer2(), q.getLongitude(), q.getLatitude(), course.getId(), q.getQuestionOrder());
		}
	}
	
	/**
	 * Checks if the user has set any questions on the map.
	 * @return Boolean
	 */
	public boolean hasSetQuestions()
	{
		if (this.setQuestions.size() == 0)
			return false;
		else
			return true;
	}

	public int getNumberOfQuestionsSet()
	{
		return setQuestions.size();
	}
	
	/**
	 * Checks if the user has answered a question
	 * @return
	 */
	public boolean hasAnsweredQuestion()
	{
		return hasAnsweredQuestion;
	}
	
	/**
	 * Sets the radius in which to look for questions to be answered
	 * @param radius
	 */
	public void setRadius(int radius)
	{
		this.radius = radius;
	}
	
	public int getCurrentRedFlag()
	{
		return redFlags[this.getCurrentQuestionIndex()];//
	}
	
	public int getCurrentGreenFlag()
	{
		return greenFlags[this.getCurrentQuestionIndex()];
	}
	
	public int getRedFlag(int index)
	{
		return redFlags[index];
	}
	
	public int getGreenFlag(int index)
	{
		return redFlags[index];
	}
	
	public float getDistanceMoved()
	{
		float distanceMoved = 0;
		for (Question q : setQuestions)
		{
			try
			{
				GeoPoint g1 = new GeoPoint(q.getLongitude(), q.getLatitude());
				GeoPoint g2 = new GeoPoint (setQuestions.get(setQuestions.indexOf(q) + 1).getLongitude(),
						setQuestions.get(setQuestions.indexOf(q) + 1).getLatitude());
				distanceMoved += HuggUtils.getDistanceBetween(g1, g2) / 1000;
			}
			catch (Exception e)
			{
				return Float.parseFloat((format.format((distanceMoved))));
			}
			
			
		}
		return distanceMoved / 1000;
		
	}
}
