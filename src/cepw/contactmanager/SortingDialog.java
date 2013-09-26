package cepw.contactmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SortingDialog extends DialogFragment {

	protected enum SortType { SORT_BY_FIRST_NAME, SORT_BY_LAST_NAME, SORT_BY_PHONE }
	
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
	
	public static interface OnCompleteListener {
		public abstract void onComplete(SortType sortType);
	}

	class OnOptionSelected implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				mListener.onComplete(SortType.SORT_BY_FIRST_NAME);
				break;
			case 1:
				mListener.onComplete(SortType.SORT_BY_LAST_NAME);
				break;
			case 2:
				mListener.onComplete(SortType.SORT_BY_PHONE);
				break;
			}

		}

	}

}
