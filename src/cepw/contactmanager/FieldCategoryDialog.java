package cepw.contactmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class FieldCategoryDialog extends DialogFragment {

	/*
	 * private CharSequence[] items = {"Address", "Email", "Date of Birth",
	 * "Notes", "Website", "Organisation"};
	 */

	private List<String> item = new ArrayList<String>();

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		if (getActivity().findViewById(R.id.layout_emailfields).getVisibility() == View.GONE) {
			item.add("E-mail");
		}

		if (getActivity().findViewById(R.id.layout_addressfields)
				.getVisibility() == View.GONE) {
			item.add("Address");
		}

		if (getActivity().findViewById(R.id.layout_dobfields).getVisibility() == View.GONE) {
			item.add("Date of Birth");
		}

		if (item.isEmpty()) {
			View view2rm = getActivity().findViewById(
					R.id.button_new_field_category);
			((ViewGroup) view2rm.getParent()).removeView(view2rm);
			dismiss();
		}

		final String[] items;
		items = item.toArray(new String[] {});

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.action_new_field)
				.setNeutralButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dismiss();

							}

						})
				.setItems(items, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (items[which].equals("E-mail")) {
							getActivity().findViewById(R.id.layout_emailfields)
									.setVisibility(View.VISIBLE);
							item.remove(which);
						}

						if (items[which].equals("Address")) {
							getActivity().findViewById(
									R.id.layout_addressfields).setVisibility(
									View.VISIBLE);
							item.remove(which);
						}

						if (items[which].equals("Date of Birth")) {
							getActivity().findViewById(R.id.layout_dobfields)
									.setVisibility(View.VISIBLE);
							item.remove(which);
						}

						if (item.isEmpty()) {
							View view2rm = getActivity().findViewById(
									R.id.button_new_field_category);
							((ViewGroup) view2rm.getParent())
									.removeView(view2rm);
						}
					}
				});
		return builder.create();
	}

}
