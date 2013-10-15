package cepw.contactmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * This is a DialogFragment that prompt the user to choose from Gallery or Camera. 
 * 
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class EmailPopupDialog extends DialogFragment {

	// Constants for selection
	protected enum EmailAction { SELECTED_MAIL, SELECTED_COPY }
	
	private OnCompleteListener mListener;
	private int position;

	/**
	 * @see android.app.DialogFragment#onCreate(Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String title = getArguments().getString(InfoActivity.EMAIL_ADDRESS);
		position = getArguments().getInt(InfoActivity.SELECTED_POS);
		String[] selection = {"Mail", "Copy to clipboard"};
		
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
	 * Interface created for this ImageChooserDialog. Any activity that implements this
	 * interface will onComplete method invoked when one of the option in this dialog is 
	 * clicked.
	 */
	public static interface OnCompleteListener {
		public abstract void onComplete(EmailAction action, int position);
	}

	/**
	 * The OnClickListener for this list dialog
	 */
	class OnOptionSelected implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				mListener.onComplete(EmailAction.SELECTED_MAIL, position);
				break;
			case 1:
				mListener.onComplete(EmailAction.SELECTED_COPY, position);
				break;
			}

		}

	}
}