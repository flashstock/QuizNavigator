package com.grupp4.quiznavigator.ui;

import java.util.List;

import com.grupp4.quiznavigator.R;
import com.grupp4.quiznavigator.database.QuizNavigatorDataSource;
import com.grupp4.quiznavigator.model.Course;
import com.grupp4.quiznavigator.util.HuggUtils;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Johan Hugg
 * @author Alexander Boström
 * @author Robert Bagge
 */
public class WalkCourseActivity extends ListActivity
{
	String courseNames[];
	List<Course> coursesList;
	float refLength;
	int refRating;
	int refNumberOQ;
	String refCreator;
	String refDate;
	QuizNavigatorDataSource dbSrc;
	HuggUtils strUtils;
	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		dbSrc = QuizNavigatorDataSource.getInstance(this);
		dbSrc.open();
		try
		{
			coursesList = dbSrc.getAllCourses(dbSrc.getLoggedInUser().getUsername());
			if (coursesList.size() == 0)
				noFoundCoursesDialog();
			this.courseNames = strUtils.getCourseNames(coursesList);
		}
		catch(Exception e){ }
		
		setListAdapter(new ArrayAdapter<String>(WalkCourseActivity.this,
				android.R.layout.simple_list_item_1, courseNames));
	}
	
	/**
	 * Dialog that is created when no courses associated with the current user has been found.
	 */
	private void noFoundCoursesDialog()
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Error");
		dialog.setMessage("No courses found for this user!");
		dialog.setPositiveButton("OK", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Intent i = new Intent(WalkCourseActivity.this, MainMenuActivity.class);
				startActivity(i);
				return;
			}
		});
		dialog.show();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int index, long id)
	{
		super.onListItemClick(l, v, index, id);
		final int position = index;
		Course course = coursesList.get(index);
		refLength = course.getLength();
		refRating = course.getAverageRating();
		refNumberOQ = course.getNoOfQuestions();
		refCreator = course.getCreator();
		refDate = course.getCreationDate();
		
		String courseName = courseNames[index];
		String length = "Length of course: " + refLength + "km";
		String rating = "Rating: " + refRating;
		String numberOQ = "Number of questions: " + refNumberOQ;
		String creator = "Creator: " + refCreator;
		String date = "Created: " + refDate;
		
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle(courseName);
		LayoutInflater inflater = getLayoutInflater();
		final View dialogLayout = inflater.inflate(R.layout.dialog_course_walkcourseinfo, null);

		final TextView sLength = (TextView) dialogLayout.findViewById(R.id.lengthTV);
		final TextView sNumberOQ = (TextView) dialogLayout
				.findViewById(R.id.numberOQTV);
		final TextView sRating = (TextView) dialogLayout.findViewById(R.id.ratingTV);
		final TextView sCreator = (TextView) dialogLayout
				.findViewById(R.id.creatorTV);
		final TextView sDate = (TextView) dialogLayout.findViewById(R.id.dateTV);
		
		sLength.setText(length);
		sNumberOQ.setText(numberOQ);
		sRating.setText(rating);
		sCreator.setText(creator);
		sDate.setText(date);

		helpBuilder.setView(dialogLayout);
		helpBuilder.setPositiveButton("Go", null);
		helpBuilder.setNegativeButton(android.R.string.cancel, null);
		
		final AlertDialog dialogCourseInfo = helpBuilder.create();
		
		dialogCourseInfo.setOnShowListener(new OnShowListener()
		{
			
			@Override
			public void onShow(DialogInterface dialog)
			{
				Button positiveButton = dialogCourseInfo.getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new View.OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
						goMapWalkCourse(coursesList.get(position));
						dialogCourseInfo.dismiss();
					}
				});
				
			}
		});
		dialogCourseInfo.show();
	}
	
	/**
	 * Sends intent to the MapActivity with relevant information about the course and starts it.
	 * @param course
	 */
	public void goMapWalkCourse(Course course)
	{
		Intent i = new Intent(this, MyMapActivity.class);
		i.putExtra("course_settingorwalking", "walking");
		i.putExtra("course_name", course.getName());
		i.putExtra("course_id", course.getId());
		startActivity(i);
	}
	
}