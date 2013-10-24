package cepw.contactmanager;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * This is a DialogFragment that display a DatePicker for user to select a date
 * and will change the contacts birthday field.
 * 
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class DateChooserDialog extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	private TextView v;
	private ImageButton b;

	/**
	 * Creates the dialog. Default date will depend on the value in the birthday
	 * field of a Contact. If no birthday is assigned, start from current date.
	 * Start from the assigned day otherwise.
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the current date as the default date in the picker
		v = (TextView) getActivity().findViewById(R.id.textview_dob);
		b = (ImageButton) getActivity().findViewById(R.id.button_clear_dob);
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Get the date from the birthday field.
		// If the field is NOT empty...
		if (!v.getText().toString().equals("")) {
			// Split the string using "-" as delimiter
			String[] split = v.getText().toString().split("-");

			// Assign the values of year, month and day
			year = Integer.parseInt(split[2]);
			month = Integer.parseInt(split[1]) - 1;
			day = Integer.parseInt(split[0]);
		}

		// Create a new instance of DatePickerDialog and return it
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
		dialog.getDatePicker().setMaxDate(new Date().getTime());
		return dialog;
	}

	/**
	 * @see android.app.DatePickerDialog.OnDateSetListener
	 */
	public void onDateSet(DatePicker view, int year, int month, int day) {
		String sday = "";
		String smonth = "";

		smonth = (month < 9) ? "0" + (month + 1) : "" + (month + 1);
		sday = (day < 10) ? "0" + day : "" + day;

		v.setText(sday + "-" + smonth + "-" + year);
		b.setVisibility(View.VISIBLE);
	}
}
