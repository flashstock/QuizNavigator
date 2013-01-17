package com.grupp4.quiznavigator.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.grupp4.quiznavigator.R;
import com.grupp4.quiznavigator.database.QuizNavigatorDataSource;
import com.grupp4.quiznavigator.model.Course;
import com.grupp4.quiznavigator.model.Question;
import com.grupp4.quiznavigator.util.HuggUtils;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

/**
 * 
 * @author Robert Bagge, 
 * @author Alexander Bostr�m, 
 * @author Johan Hugg
 *
 */
public class MainMenuActivity extends Activity 
{
	List<Question> questions = new ArrayList<Question>();
	List<Course> courses = new ArrayList<Course>();

	Calendar calendar = Calendar.getInstance();
	QuizNavigatorDataSource dbSrc;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		dbSrc = QuizNavigatorDataSource.getInstance(this);
		dbSrc.open();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		menu.add(R.string.log_out_menu_item);
		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		String caption = item.getTitle().toString();
		if(caption.equals(getString(R.string.log_out_menu_item)))
			logOut();
		return super.onMenuItemSelected(featureId, item);
	}

	private void logOut() 
	{
		dbSrc.removeLoggedUser();
		Intent i = new Intent(this, LoginActivity.class);
		startActivity(i);

	}
	@Override
	public void onBackPressed()
	{

	}

	/**
	 * This method creates a AlertDialog when you click on "Create Quiz" in the main menu. 
	 * In the AlertDialog you will be asked to name your Quiz, once you press enter you will be 
	 * forwarded to the QuizCreatorActivity. 
	 * If you press Cancel you will be kicked out to the main menu again.
	 * @param View
	 */
	public void createQuizDialog(View caller)

	{
		final EditText edittxt = new EditText(this);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		//Negative
		alertDialogBuilder.setPositiveButton(android.R.string.ok, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Course course = new Course();
				course.setName(edittxt.getText().toString());
				course.setCreator(dbSrc.getLoggedInUser().getUsername());
				course.setCreationDate(HuggUtils.getDate());
				//Course course = dbSrc.createCourse(edittxt.getText().toString(), dbSrc.getLoggedInUser().getUsername(), getDate());
				goMapSetCourse(course);
			}
		});

		//Positive
		alertDialogBuilder.setNegativeButton(android.R.string.cancel, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

			}
		});

		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.setTitle("Please enter a name for your quiz");
		edittxt.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		edittxt.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if(hasFocus)
					alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

			}
		});
		alertDialog.setView(edittxt);

		alertDialog.show();
	}
	
	private void goMapSetCourse(Course course)
	{
		Intent i = new Intent(this, com.grupp4.quiznavigator.ui.MyMapActivity.class);
		i.putExtra("course_settingorwalking", "setting");
		i.putExtra("course_name", course.getName());
		i.putExtra("course_creator", course.getCreator());
		i.putExtra("course_date", course.getCreationDate());
		dbSrc.close();
		startActivity(i);
	}
	
	public void walkQuizInterface(View caller){
		Intent i = new Intent(this, WalkCourseActivity.class);
		startActivity(i);
	}
}


