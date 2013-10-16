package cepw.contactmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * This is a DialogFragment that prompt the user to choose from a list of phone operations
 * such as Call, Message, and Copy to Clipboard etc.
 * 
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class PhonePopupDialog extends DialogFragment {

	// Constants for selection
	protected enum PhoneAction { SELECTED_CALL, SELECTED_MESSAGE, SELECTED_COPY, SELECTED_SET_PRIMARY }
	private OnCompleteListener mListener;
	private int position;
	private String[] selection;
	/**
	 * @see android.app.DialogFragment#onCreate(Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String title = getArguments().getString(InfoActivity.PH_NUMBER);
		String type = getArguments().getString(InfoActivity.PH_TYPE);
		boolean isDefault = getArguments().getBoolean(InfoActivity.IS_DEFAULT);
		position = getArguments().getInt(InfoActivity.SELECTED_POS);
		List<String> list = new ArrayList<String>();
		list.add("Call");
		if(type.equals("Mobile")){ list.add("Message");}
		list.add("Copy to clipboard");
		if(!isDefault) {list.add("Set primary number");}
		
		selection = list.toArray(new String[] {});
		onAttach(getActivity());

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title)
				.setItems(selection, new OnOptionSelected())
				.setNeutralButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dismiss();
							}
						});
		return builder.create();
	}

	/**
	 * @see android.app.DialogFragment#onAttach(Activity)
	 */
	@Override
	// make sure the Activity implemented it
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.mListener = (OnCompleteListener) activity;
		} catch (final ClassCastException e) {
			this.mListener = null;
			throw new ClassCastException(activity.toString()
					+ " must implement OnCompleteListener");
		}
	}

	/**
	 * Interface created for this PhonePopupDialog. Any activity that implements this
	 * interface will onComplete method invoked when one of the option in this dialog is 
	 * clicked.
	 */
	public static interface OnCompleteListener {
		public abstract void onComplete(PhoneAction action, int position);
	}

	/**
	 * The OnClickListener for this list dialog
	 */
	class OnOptionSelected implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				mListener.onComplete(PhoneAction.SELECTED_CALL, position);
				break;
			case 1:
				if(selection[which].equals("Message")){
					mListener.onComplete(PhoneAction.SELECTED_MESSAGE, position);
				}else{
					mListener.onComplete(PhoneAction.SELECTED_COPY, position);
				}
				break;
			case 2:
				if(selection[which].equals("Copy to clipboard")){
					mListener.onComplete(PhoneAction.SELECTED_COPY, position);
				}else{
					mListener.onComplete(PhoneAction.SELECTED_SET_PRIMARY, position);
				}
				break;
			case 3:
				mListener.onComplete(PhoneAction.SELECTED_SET_PRIMARY, position);
				break;
			}

		}

	}
}