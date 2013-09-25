package cepw.contact;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * This is a class that contains all the utility methods 
 * that will be used by this Contact Manager App
 * @author cookie-paw
 *
 */
public class Utilities {
	
	/**
	 * Converts the DP to PX based on the DPI of the phone
	 * @param context the metric is going to based on
	 * @param dp The value you want to convert
	 * @return The px corresponding to the input dp
	 */
	public static final int dpToPx(Context context, int dp) {
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		int px = Math.round(dp
				* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}
	
	/**
	 * Adjust the height of the ListView based on number of elements within the list
	 * @param listView List View you want it to apply on
	 * @return Success/Failure of operation. Return false if this list does not have an adapter
	 */
	public static final boolean setNoCollapseListView(ListView listView) {

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return false;

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		return true;
	}
	
	/**
	 * Scales a photo based on the 
	 * @param photo Bitmap you want to resize
	 * @param newHeight The desired height for the image
	 * @param context the metric is going to based on
	 * @return
	 */
	public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight,
			Context context) {

		final float densityMultiplier = context.getResources()
				.getDisplayMetrics().density;

		int h = (int) (newHeight * densityMultiplier);
		int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

		photo = Bitmap.createScaledBitmap(photo, w, h, true);

		return photo;
	}
	
	/**
	 * Unfocuses an EditText by reset its focusability
	 * @param text The EditText you want to unfocus
	 */
	public static void unFocusEditText(EditText text){
		text.setFocusable(false);
		text.setFocusableInTouchMode(true);
	}
	
	/**
	 * Copy a string to android clipboard
	 * @param clipboard the clipboard object you want the value to be copied to
	 * @param string the string you want to pass to the clipboard
	 */
	public static void copyStringToClipboard(ClipboardManager clipboard, String string) {
		ClipData clip = ClipData.newPlainText("simple text", string);
		clipboard.setPrimaryClip(clip);
	}
}
