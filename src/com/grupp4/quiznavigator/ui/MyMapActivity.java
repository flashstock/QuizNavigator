package com.grupp4.quiznavigator.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import com.grupp4.quiznavigator.R;
import com.grupp4.quiznavigator.database.QuizNavigatorDataSource;
import com.grupp4.quiznavigator.model.Course;
import com.grupp4.quiznavigator.model.MyItemizedOverlay;
import com.grupp4.quiznavigator.model.PathMapOverlay;
import com.grupp4.quiznavigator.model.Question;
import com.grupp4.quiznavigator.model.QuestionManager;
import com.grupp4.quiznavigator.util.HuggUtils;

/**
 * 
 * @author Johan Hugg
 *
 */
public class MyMapActivity extends MapActivity
{

	MyItemizedOverlay myItemizedOverlay;
	MyLocationOverlay myLocationOverlay;
	MapController mapController;
	MapView mapView;
	List<Overlay> mapOverlays;
	
	String mode;
	String courseName;
	String courseDate;
	String courseCreator;
	int courseId;
	PathMapOverlay pthMapOverlay;
	QuestionManager questionManager;
	float distanceWalked;
	TextView txtView;
	
	QuizNavigatorDataSource dbSrc;
	
	GeoPoint myLocation;
		
	boolean dialogOpen = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_map);
		dbSrc = QuizNavigatorDataSource.getInstance(this);
		dbSrc.open();
		handleIntents();
		initMapView();
		initLocation();
		txtView = (TextView) findViewById(R.id.clickBtn);
		if (mode.equals("walking"))
			populateMapWithQuestions();
		mapOverlays.add(myLocationOverlay);
	}

	/**
	 * Handles the intents received from other activities. 
	 */
	private void handleIntents()
	{
		Bundle extras = getIntent().getExtras();
		String value = extras.getString("course_settingorwalking");
		courseName = extras.getString("course_name");
		courseCreator = extras.getString("course_creator");
		courseDate = extras.getString("course_date");
		courseId = extras.getInt("course_id");
		
		if (value.equals("walking"))
		{
			mode = "walking";
			questionManager = new QuestionManager(this, courseId);
		}
		else if (value.equals("setting"))
		{
			mode = "setting";
			questionManager = new QuestionManager(this, courseId);
		}
	}

	/**
	 * Initializes the location components, such as the MyLocationOverlay and MapController. When MyLocationOverlay gets the users
	 * location, it will move and zoom in to that point. 
	 */
	private void initLocation()
	{
		myLocationOverlay = new MyLocationOverlay(this, mapView)
		{
			@Override
			public synchronized void onLocationChanged(Location location)
			{
				super.onLocationChanged(location);
				locationChanged(location);
			}
		};

		myLocationOverlay.enableMyLocation();
		myLocationOverlay.runOnFirstFix(new Runnable()
		{
			public void run()
			{
				mapController.setZoom(20);
				mapController.animateTo(myLocationOverlay.getMyLocation());
			}
		});

	}
	/**
	 * Initializes the MapView, the mapController and adds the relevant overlays
	 */
	private void initMapView()
	{
		mapView = (MapView)findViewById(R.id.mapview);
		mapOverlays = mapView.getOverlays();

		pthMapOverlay = new PathMapOverlay(this);
		mapOverlays.add(pthMapOverlay);
		
		myItemizedOverlay = new MyItemizedOverlay(this.getResources().getDrawable(questionManager.getCurrentRedFlag()), this, this);
		mapOverlays.add(myItemizedOverlay);

		mapController = mapView.getController();
		mapView.setBuiltInZoomControls(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_my_map, menu);
		if (mode.equals("walking")) 
			menu.removeItem(R.id.map_placequestion); 
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		myLocationOverlay.disableMyLocation();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		myLocationOverlay.enableMyLocation();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		myLocationOverlay.disableMyLocation();
	}
	
	/**
	 * Called when the user presses the back button on the device.
	 * A confirmation dialog will appear to make sure the user wants to go back.
	 */
	@Override
	public void onBackPressed()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Are you sure you want to quit?");
		
		if (mode.equals("walking"))
			dialogBuilder.setMessage("You haven't answered all the questions. \n\nAll answers will be lost!");
		else if (mode.equals("setting"))
			if(!questionManager.hasSetQuestions())
				dialogBuilder.setMessage("You haven't placed any questions!");
			else
				dialogBuilder.setMessage("All set questions will be lost!");
			
		dialogBuilder.setNegativeButton(android.R.string.no, null);
		dialogBuilder.setPositiveButton(android.R.string.yes, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				goBackToMainMenu();
			}
		});
		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.setOnShowListener(new OnShowListener()
		{
			
			@Override
			public void onShow(DialogInterface dialog)
			{
				dialogOpen = true;
				
			}
		});
		alertDialog.setOnDismissListener(new OnDismissListener()
		{
			
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				dialogOpen = false;
				
			}
		});
		alertDialog.show();
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		String caption = item.getTitle().toString();
		
		if(caption.equals(getString(R.string.map_placequestion)))
			placeQuestion();
		else if (caption.equals(getString(R.string.map_satellitemode)))
			setSatelliteMapMode();
		else if (caption.equals(getString(R.string.map_traditionalmode)))
			setTraditionalMapMode();
		else if (caption.equals(getString(R.string.map_done)))
			doneWithCourse();
		else if (caption.equals(getString(R.string.map_showdistancemoved)))
		{
			
			if (txtView.getVisibility() == View.VISIBLE)
			{
				txtView.setVisibility(View.INVISIBLE);
				txtView.invalidate();
			}
			else
			{
				txtView.setVisibility(View.VISIBLE);
				txtView.invalidate();
			}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void setSatelliteMapMode()
	{
		mapView.setSatellite(true);
	}
	
	/**
	 * <p>Called when the user clicks on the Done button on the actionbar. If the user is in the
	 * setting mode a check will be made to make sure the user has placed any questions, if true
	 * the course will be created. If false a confirmation dialog to exit will appear.</p>
	 * <p> If the mode is walking, a check will be made to make sure the user has answered all
	 * the questions. If true a dialog with the results of the course will appear. If false a 
	 * confirmation dialog to exit will appear.
	 */
	private void doneWithCourse()
	{
		
		if(mode.equals("setting"))
		{
			if (questionManager.hasSetQuestions())
			{
				Course course = dbSrc.createCourse(courseName, questionManager.getDistanceMoved(), courseCreator, courseDate, questionManager.getNumberOfQuestionsSet());
				questionManager.registerCourse(course);
				Toast.makeText(this, "Course created!", Toast.LENGTH_LONG).show();
				goBackToMainMenu();
			}
			else
				onBackPressed();
		}
		else if (mode.equals("walking"))
		{
			//Done, all questions answered
			if(questionManager.isLastQuestion())
				resultDialog();
			else
				onBackPressed(); //
		}
	}
	
	/**
	 * Sends an intent to the Main menu and starts it.
	 */
	private void goBackToMainMenu()
	{
		Intent i = new Intent(MyMapActivity.this, MainMenuActivity.class);
		dbSrc.close();
		startActivity(i);
	}
	
	private void setTraditionalMapMode()
	{
		mapView.setSatellite(false);
	}
	
	/**
	 * A dialog that shows the result of the course i.e score.
	 */
	private void resultDialog()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Your result");
		StringBuilder sb = new StringBuilder();
		int numberOfCorrectAnswers = 0;
		int numberOfQuestions = questionManager.getCorrectAnswers().size();
		
		for(int i = 0; i < numberOfQuestions; i++)
		{
			sb.append("\nQuestion " + (i + 1) + ": " + questionManager.getQuestion(i).getCaption() + "\n"
					+ "You answered: " + questionManager.getUserAnswers().get(i) + "\nCorrect answer is: " + questionManager.getCorrectAnswers().get(i) + "\n");
			
			if (questionManager.getCorrectAnswers().get(i).equals(questionManager.getUserAnswers().get(i)))
				numberOfCorrectAnswers++;
		}
		
		sb.append("\nYou answered " + numberOfCorrectAnswers + " questions correct out of " + numberOfQuestions);
		
		
		dialogBuilder.setMessage(sb);
		dialogBuilder.setPositiveButton("Done", new OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				goBackToMainMenu();
			}
		});
		
		AlertDialog dialog = dialogBuilder.create();
		
		dialog.setOnShowListener(new OnShowListener()
		{
			
			@Override
			public void onShow(DialogInterface dialog)
			{
				dialogOpen = true;
				
			}
		});
		
		dialog.setOnDismissListener(new OnDismissListener()
		{
			
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				dialogOpen = false;
				
			}
		});
		
		dialog.show();
		
	}
	
	/**
	 * Places a question on the MapView. This method should only be used if the user is walking a course. 
	 */
	private void populateMapWithQuestions()
	{
		int i = 0;
		for (OverlayItem item : questionManager.getQuestionOverlayItems())
		{
			myItemizedOverlay.addOverlay(item);
			Drawable marker = MyMapActivity.this.getResources().getDrawable(questionManager.getRedFlag(i));
			myItemizedOverlay.replaceItemMarker(marker, item, i);
			i++;
		}
	}
	/**
	 * Places a question on the map, this method should only be used if the user is setting a course.
	 */
	private void placeQuestion()
	{
		if (myLocation != null)
			placeQuestionDialog(myLocation);
	}
	
	/**
	 * Places a question on the location
	 * @param location
	 */
	public void placeQuestionDialog(GeoPoint location)
	{
		if (!this.mode.equals("setting"))
			return;
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle("Set Question Content");
		LayoutInflater inflater = getLayoutInflater();
		View dialog = inflater.inflate(R.layout.dialog_question_setquestion, null);

		final GeoPoint myLocation = location;
		final EditText questionCaption = (EditText) dialog.findViewById(R.id.questionCaption);
		final EditText answer1 = (EditText) dialog.findViewById(R.id.answer1);
		final EditText answer2 = (EditText) dialog.findViewById(R.id.answer2);
		final EditText answer3 = (EditText) dialog.findViewById(R.id.answer3);
		final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup1);

		helpBuilder.setView(dialog);
		helpBuilder.setPositiveButton(android.R.string.ok, null);
		helpBuilder.setNegativeButton(android.R.string.cancel, null);

		final AlertDialog questionDialog = helpBuilder.create();

		questionDialog.setOnShowListener(new OnShowListener()
		{
			@Override
			public void onShow(DialogInterface arg0)
			{
				Button positiveButton = questionDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new View.OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
						int rId = radioGroup.getCheckedRadioButtonId();
						if (!HuggUtils.checkIfEmpty(answer1.getText().toString()) && !HuggUtils.checkIfEmpty(answer2.getText().toString()) 
								&& !HuggUtils.checkIfEmpty(answer3.getText().toString()) && !HuggUtils.checkIfEmpty(questionCaption.getText().toString()) && rId != -1) 
						{
							switch(rId)
							{
							case R.id.radioButton1:
								addOverlayItem();
								questionManager.addSetQuestion(new Question(questionCaption.getText().toString(), answer1.getText().toString(), answer2.getText().toString(), 
										answer3.getText().toString(), myLocation.getLatitudeE6(), myLocation.getLongitudeE6(), courseId, "1X2"));
								
								break;
							case R.id.radioButton2:
								addOverlayItem();
								questionManager.addSetQuestion(new Question(questionCaption.getText().toString(), answer2.getText().toString(), answer1.getText().toString(), 
										answer3.getText().toString(), myLocation.getLatitudeE6(), myLocation.getLongitudeE6(), courseId, "X12"));
								
								break;
							case R.id.radioButton3:
								addOverlayItem();
								questionManager.addSetQuestion(new Question(questionCaption.getText().toString(), answer3.getText().toString(), answer1.getText().toString(), answer2.getText().toString(),
										myLocation.getLatitudeE6(), myLocation.getLongitudeE6(), courseId, "21X"));
																
								break;
							}
							
						}
						else
						{
							Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_LONG).show();
						}
					}
					public void addOverlayItem()
					{
						OverlayItem item = new OverlayItem(myLocation, questionCaption.getText().toString(), "1: " + answer1.getText().toString()
								+ "\n" + "X: " + answer2.getText().toString() + "\n" + "2: " + answer3.getText().toString());
						
						Drawable marker = MyMapActivity.this.getResources().getDrawable(questionManager.getCurrentRedFlag());
						marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
						myItemizedOverlay.addOverlay(item, marker);
						
						questionDialog.dismiss();
					}
				});
			}
		});

		questionDialog.show();

	}

	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}

	/**
	 * This method adds the overlays that were destroyed when the application changed orientation.
	 * @param newConfig
	 */
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mapOverlays.add(myItemizedOverlay);
	}

	/**
	 * Called when the location of the user is changed. In this method we check if the user
	 * is near a question and execute the appropriate action.
	 * @param location
	 */
	public void locationChanged(Location location)
	{
		
		if(mode.equals("walking"))
		{
			if(!questionManager.isLastQuestion())
				if(questionManager.nearNextQuestion(myLocation) && !dialogOpen)
					answerQuestionDialog();
		}
		if (questionManager.hasAnsweredQuestion() || questionManager.hasSetQuestions())
		{
			
			pthMapOverlay.setCanDraw(false);
			//pthMapOverlay.addPoint(location, mapView);
			distanceWalked = questionManager.getDistanceMoved();
			txtView.setText(Float.toString(distanceWalked));
		}
		
		this.myLocation = HuggUtils.locationToGeoPoint(location);
		
	}
	
	/**
	 * A dialog that takes user input to define a question to be placed on the map.
	 */
	private void answerQuestionDialog()
	{
		OverlayItem item = myItemizedOverlay.getOverlays().get(questionManager.getCurrentQuestionIndex());
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle(item.getTitle());
		
		LayoutInflater inflater = getLayoutInflater();
		View dialog = inflater.inflate(R.layout.dialog_question_answerquestion, null);
		
		final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup1);
		final RadioButton radioButton1 = (RadioButton) dialog.findViewById(R.id.sheetAnswer1Radio);
		final RadioButton radioButton2 = (RadioButton) dialog.findViewById(R.id.sheetAnswer2Radio);
		final RadioButton radioButton3 = (RadioButton) dialog.findViewById(R.id.sheetAnswer3Radio);
		final Question q = questionManager.getCurrentQuestion();
		String order = q.getQuestionOrder();

		if (order.equals("1X2"))
		{
			radioButton1.setText(q.getCorrectAnswer());
			radioButton2.setText(q.getWrongAnswer1());
			radioButton3.setText(q.getWrongAnswer2());
		}
		else if (order.equals("X12"))
		{
			radioButton1.setText(q.getWrongAnswer1());
			radioButton2.setText(q.getCorrectAnswer());
			radioButton3.setText(q.getWrongAnswer2());
		}
		else if (order.equals("21X"))
		{
			radioButton1.setText(q.getWrongAnswer1());
			radioButton2.setText(q.getWrongAnswer2());
			radioButton3.setText(q.getCorrectAnswer());
		}
		
		helpBuilder.setPositiveButton(android.R.string.ok, null);
		helpBuilder.setNegativeButton(android.R.string.cancel, null);

		helpBuilder.setView(dialog);
		final AlertDialog questionAnswerdialog = helpBuilder.create();
		
		questionAnswerdialog.setOnDismissListener(new OnDismissListener()
		{
			
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				if(questionManager.isLastQuestion())
					Toast.makeText(MyMapActivity.this, "You have answered all the questions in this course, press done to exit", Toast.LENGTH_LONG).show();
				dialogOpen = false;
			}
		});
		questionAnswerdialog.setOnShowListener(new OnShowListener()
		{

			@Override
			public void onShow(DialogInterface dialog)
			{
				Button positiveButton = questionAnswerdialog.getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new View.OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
						switch(radioGroup.getCheckedRadioButtonId())
						{
						case R.id.sheetAnswer1Radio:
							addAnswer(radioButton1.getText().toString());
							break;
						case R.id.sheetAnswer2Radio:
							addAnswer(radioButton2.getText().toString());
							break;
							
						case R.id.sheetAnswer3Radio:
							addAnswer(radioButton3.getText().toString());
							break;
						}
						
					}
					public void addAnswer(String answer)
					{
						//Get marker to replace the old one on map
						Drawable marker = MyMapActivity.this.getResources().getDrawable(questionManager.getCurrentGreenFlag());
						marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
						
						//Get the old map item
						OverlayItem oldItem = myItemizedOverlay.getItem(questionManager.getCurrentQuestionIndex());
						//Create a new map item
						OverlayItem newItem = new OverlayItem(oldItem.getPoint(), "Question " + questionManager.getCurrentQuestionIndex() + ": " + q.getCaption(), "1: " + radioButton1.getText().toString()
								+ "\n" + "X: " + radioButton2.getText().toString() + "\n" + "2: " + radioButton3.getText().toString() + "\n\n" + "You answered: " + answer );
						//Replace item with new drawable
						myItemizedOverlay.replaceItemMarker(marker, oldItem, newItem);
						questionManager.addUserAnswer(answer);
						questionAnswerdialog.dismiss();
						dialogOpen = false;
					}
				});
			}
		});
		
		questionAnswerdialog.show();
		dialogOpen = true;
	}
	
	public void getDistance(View caller)
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(Float.toString(distanceWalked));
		dialog.show();
		
	}
}
