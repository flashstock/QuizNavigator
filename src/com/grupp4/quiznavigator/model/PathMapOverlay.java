package com.grupp4.quiznavigator.model;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.grupp4.quiznavigator.R;

/**
 * 
 * @author Johan Hugg
 * @author Jonas Boustedt
 *
 */
public class PathMapOverlay extends Overlay
{
	private final Context context;
	private Paint paint;
	private float radius = 6;
	private ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
	private boolean canDraw = true;
	private GeoPoint previousLocation;
	private float distanceWalked = 0;
	private GeoPoint currentLocation;
	
	public PathMapOverlay(Context context, Paint paint)
	{
		this.context = context;
		this.paint = paint;
	}
	
	/**
	 * Gets the distance moved, also updates the distance, so call it when location is changed.
	 * @return
	 */
	/*
	public float getDistanceMoved()
	{
		try
		{
			distanceWalked += getDistance(geoPoints.get(geoPoints.indexOf(currentLocation) - 1),currentLocation);
		}
		catch (Exception e) {}			
		
		DecimalFormat format = new DecimalFormat("#.#");
		float b = Float.parseFloat((format.format((distanceWalked)))) / 1000;
		return b;
		
	}
	*/
	public PathMapOverlay(Context context)
	{
		this.context = context;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.CYAN);
		paint.setStrokeWidth(10);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
	}

	public void addPoint(GeoPoint point, MapView mapView)
	{
		geoPoints.add(point);
		currentLocation = point;
		mapView.invalidate();
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
	}

	public void addPoint(Location loc, MapView mapView)
	{
		addPoint(loc.getLatitude(), loc.getLongitude(), mapView);
	}

	public void addPoint(double lat, double lon, MapView mapView)
	{
		addPoint(new GeoPoint((int) (lat * 1e6), (int) (lon * 1e6)), mapView);
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when)
	{
		
		super.draw(canvas, mapView, shadow);
		if (canDraw)
		{
			Point previousPoint = null;
			for (GeoPoint geoPoint : geoPoints)
			{
				Point screenPoint = new Point();
				mapView.getProjection().toPixels(geoPoint, screenPoint);
				if (previousPoint != null)
				{
					if (getDistance(previousLocation, geoPoint) < 20)
						canvas.drawLine(previousPoint.x, previousPoint.y, screenPoint.x, screenPoint.y, paint);
				}
				previousPoint = screenPoint;
				previousLocation = geoPoint;
			}
			return true;
		}
		return false;
	}

	public float getDistance(GeoPoint p1, GeoPoint p2)
	{
		float[] result = new float[3];
		Location.distanceBetween(p1.getLatitudeE6() / 1.0e6, p1.getLongitudeE6() / 1.0e6, p2.getLatitudeE6() / 1.0e6, p2.getLongitudeE6() / 1.0e6, result);
		return result[0];
	}
	
	public float getDistance(Location l1, Location l2)
	{
		return l1.distanceTo(l2);
	}
	
	
	
	public void setCanDraw(boolean canDraw)
	{
		this.canDraw = canDraw;
	}
	
}
