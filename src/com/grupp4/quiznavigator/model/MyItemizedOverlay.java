package com.grupp4.quiznavigator.model;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.grupp4.quiznavigator.ui.MyMapActivity;

/**
 * 
 * @author Johan Hugg
 *
 */

@SuppressWarnings("rawtypes")
public class MyItemizedOverlay extends ItemizedOverlay
{
	private MyMapActivity myMapActivity;
	private Context mContext;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private int index = 0;
	private boolean isPinch= false;
	
	public MyItemizedOverlay(Drawable arg0) 
	{
		super(boundCenterBottom(arg0));
	}
	
	public void addOverlay(OverlayItem overlay)
	{
		mOverlays.add(overlay);
		populate();
	}
	
	public void addOverlay(OverlayItem overlay, Drawable drawable)
	{
		overlay.setMarker(drawable);
		boundCenterBottom(drawable);
		mOverlays.add(overlay);
		populate();
		
	}
	
	@Override
	protected OverlayItem createItem(int i) 
	{
		return mOverlays.get(i);
	}
	
	public ArrayList<OverlayItem> getOverlays()
	{
		return mOverlays;
	}
	
	@Override
	public int size() 
	{
		return mOverlays.size();
	}
	
	public MyItemizedOverlay(Drawable defaultMarker, Context context, MyMapActivity myMapActivity)
	{
		super(boundCenterBottom(defaultMarker));
		populate();
		mContext = context;
		this.myMapActivity = myMapActivity;
	}
	
	/**
	 * Replace an OverlayItem on the map with another
	 * @param marker
	 * @param item
	 * @param itemIndex
	 */
	public void replaceItemMarker(Drawable marker, OverlayItem item, int itemIndex)
	{
		item.setMarker(marker);
		mOverlays.set(itemIndex, item);
		
		populate();
		boundCenterBottom(marker);
	}
	
	/**
	 * Replaces an OverlayItem on the map with another
	 * @param marker
	 * @param oldItem
	 * @param newItem
	 */
	public void replaceItemMarker(Drawable marker, OverlayItem oldItem, OverlayItem newItem)
	{
		newItem.setMarker(marker);
		mOverlays.set(mOverlays.indexOf(oldItem), newItem);
		populate();
		boundCenterBottom(marker);
		
	}
	
	@Override
	protected boolean onTap(int index)
	{
		this.index = index;
		return true;
	}
	
	@Override
	public boolean onTap (final GeoPoint p, final MapView mapView)
	{
		boolean tapped = super.onTap(p, mapView);
		
		if (isPinch)
			return false;
		else if (tapped)
		{
			
			OverlayItem item = this.getOverlays().get(index);
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(item.getTitle());
			if (item.getSnippet().isEmpty())
				dialog.setMessage("Move closer to answer this question!");
			else
				dialog.setMessage(item.getSnippet());
			dialog.show();
			
			//Toast.makeText(mContext, "Move closer to answer this question!", Toast.LENGTH_LONG).show();
			return true;
		}
		else
		{
			myMapActivity.placeQuestionDialog(p);
			return true;
		}
	}
  
	@Override
	public boolean onTouchEvent(MotionEvent e, MapView mapView)
	{
	    int fingers = e.getPointerCount();
	    
	    if( e.getAction() == MotionEvent.ACTION_DOWN )
	    {
	        isPinch=false;  // Touch DOWN, don't know if it's a pinch yet
	    }
	    if( e.getAction() == MotionEvent.ACTION_MOVE && fingers==2 )
	    {
	        isPinch=true;   // Two fingers, def a pinch
	    }
	    return super.onTouchEvent(e, mapView);
	}
}
