package cepw.contactmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SortingDialog extends DialogFragment {

	public static interface OnCompleteListener {
		public abstract void onComplete(int sortType);
	}

	static final int SORT_BY_FIRST_NAME = 1;
	static final int SORT_BY_LAST_NAME = 2;
	static final int SORT_BY_PHONE = 3;

	private OnCompleteListener mListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String[] sortTypes = getResources().getStringArray(R.array.sort_type);
		onAttach(getActivity());

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.action_sort_options)
				.setItems(sortTypes, new OnOptionSelected())
				.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dismiss();
							}
						});
		return builder.create();
	}

	// make sure the Activity implemented it
	@Override
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

	class OnOptionSelected implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				mListener.onComplete(SORT_BY_FIRST_NAME);
				break;
			case 1:
				mListener.onComplete(SORT_BY_LAST_NAME);
				break;
			case 2:
				mListener.onComplete(SORT_BY_PHONE);
				break;
			}

		}

	}

}
