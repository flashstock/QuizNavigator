package com.grupp4.quiznavigator.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.grupp4.quiznavigator.model.Course;

/**
 * 
 * @author Johan Hugg
 *
 */
public class HuggUtils
{

	/**
	 * Checks if a string is empty, ignores spaces
	 * @param string
	 * @return True if empty, false if not
	 */
	public static boolean checkIfEmpty(String string)
	{
		
		string = string.replace(" ", "");
		if (string.equals(""))
			return true;
		else
			return false;
	}

	/**
	 * Gets all the names of a course
	 * @param courses
	 * @return Array with course names
	 */
	public static String[] getCourseNames(List<Course> courses)
	{
		
		List<String> names = new ArrayList<String>();
		for (Course c : courses) {
			names.add(c.getName());
		}
		return names.toArray(new String[0]);
	}

	/**
	 * Checks with regex if the given string is a valid email address
	 * @param email
	 * @return True if valid, false if not
	 */
	public static boolean validEmail(String email)
	{
		
		final Pattern rfc2822 = Pattern.compile(
		        "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
		);

		if (!rfc2822.matcher(email).matches()) 
		{
		    return false;
		}
		else
			return true;
	}
	
	/**
	 * Gets the current date for example "20130110" note that year month and day are separated by a hyphen
	 * @return The current date
	 */
	public static String getDate()
	{
		
		Calendar calendar = Calendar.getInstance();
		String date = Integer.toString(calendar.get(Calendar.YEAR)) + "-" + Integer.toString(calendar.get(Calendar.MONTH) + 1) + "-" + Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		return date;
	}
	
	/**
	 * Converts a Location object to a GeoPoint object
	 * @param location
	 * @return The converted object
	 */
	public static GeoPoint locationToGeoPoint(Location location)
	{
		
		int lat = (int) (location.getLatitude() * 1E6);
		int lng = (int) (location.getLongitude() * 1E6);
		return new GeoPoint(lat, lng);
	}
	
	/**
	 * Gets the distance between two points in meters
	 * @param g1
	 * @param g2
	 * @return The distance in meters
	 */
	public static float getDistanceBetween(GeoPoint g1, GeoPoint g2)
	{
		
		float[] result = new float[3];
		Location.distanceBetween(g1.getLatitudeE6() / 1.0e6, g1.getLongitudeE6() / 1.0e6, g2.getLatitudeE6() / 1.0e6, g2.getLongitudeE6() / 1.0e6, result);
		return result[0];
	}
}
