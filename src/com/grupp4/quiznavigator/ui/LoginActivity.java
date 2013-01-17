package com.grupp4.quiznavigator.ui;

import java.util.List;

import com.grupp4.quiznavigator.R;
import com.grupp4.quiznavigator.database.QuizNavigatorDataSource;
import com.grupp4.quiznavigator.model.Account;
import com.grupp4.quiznavigator.model.LoggedUser;
import com.grupp4.quiznavigator.util.HuggUtils;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Robert Bagge
 * @author Johan Hugg
 * @author Alexander Bostr�m
 */
public class LoginActivity extends Activity 
{
	
	String userName, password;
	QuizNavigatorDataSource dbSrc;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		dbSrc = QuizNavigatorDataSource.getInstance(this);
		dbSrc.open();
		try
		{
			LoggedUser loggedUser = dbSrc.getLoggedInUser();
			if(loggedUser != null)
			{
				if (checkCredentials(loggedUser.getUsername(), loggedUser.getPassword()))
				{
					Intent i = new Intent(this, MainMenuActivity.class);
					dbSrc.close();
					startActivity(i);
				}
			}
		}
		catch(Exception e)
		{
			
		}
		
	}

	/**
	 * Called when Sign Up label is clicked.
	 * @param caller
	 */
	public void signUpDialog(View caller) 
	{
		
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		final View dialog = inflater.inflate(R.layout.dialog_login_signup, null);
		
		final EditText eMailText = (EditText) dialog.findViewById(R.id.emailText);
		final EditText passwordText = (EditText) dialog.findViewById(R.id.passwordText);
		
		helpBuilder.setTitle("Sign Up");
		helpBuilder.setView(dialog);
		helpBuilder.setPositiveButton(android.R.string.ok, null);
		helpBuilder.setNegativeButton(android.R.string.cancel, null);
		
		final AlertDialog signUpDialog = helpBuilder.create();
		
		signUpDialog.setOnShowListener(new OnShowListener()
		{

			@Override
			public void onShow(DialogInterface dialog) 
			{
				
				Button positiveButton = signUpDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						
						if(!HuggUtils.checkIfEmpty(eMailText.getText().toString()) 
								&& !HuggUtils.checkIfEmpty(passwordText.getText().toString()))
						{
							if (HuggUtils.validEmail(eMailText.getText().toString()))
							{
								dbSrc.createAccount(eMailText.getText().toString(), passwordText.getText().toString(),
										true);
								signUpDialog.dismiss();
							}
							else
								Toast.makeText(LoginActivity.this, "Email address is not valid", Toast.LENGTH_LONG).show();
						}
						else
							Toast.makeText(LoginActivity.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
						
					}
				});
				
			}
			
		});
		
		eMailText.setOnFocusChangeListener(new OnFocusChangeListener() 
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus) 
			{
				
				if(hasFocus)
					signUpDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			}
		});
		signUpDialog.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		return true;
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return True if the credentials are valid and match something in the database
	 */
	private boolean checkCredentials(String username, String password)
	{
		
		List<Account> accounts = dbSrc.getAllAccounts(); 
		for (Account a : accounts)
		{
			if (a.getName().equals(username) && a.getPassword().equals(password))
				return true;
		}
		return false;
	}
	
	/**
	 * This method checks if the two textboxes have the right username and password and if so,
	 * you are send to the MainMenuActivity. If the user input is not correct a Toast will notify the user. 
	 */
	public void tryLogin(View caller)
	{
		dbSrc.open();
		String email;
		String password;
		List<Account> accounts = dbSrc.getAllAccounts(); 

		TextView eMailField = (TextView) findViewById(R.id.eMailField);
		TextView passwordField = (TextView) findViewById(R.id.passwordField);
		
		for (Account a : accounts)
		{
			if (a.getName().equals(eMailField.getText().toString()) && a.getPassword().equals(passwordField.getText().toString()))
			{
				email = a.getName();
				password = a.getPassword();

				if(email.equals(eMailField.getText().toString()) && password.equals(passwordField.getText().toString()))
				{
					Intent i = new Intent(this, MainMenuActivity.class);
					dbSrc.createLoggedUser(email, password);
					dbSrc.close();
					startActivity(i);
					
					return;
				}
			}
		}
		Toast.makeText(this, "Username or password is incorrect, please try again.", Toast.LENGTH_LONG).show();
	}
	
}

