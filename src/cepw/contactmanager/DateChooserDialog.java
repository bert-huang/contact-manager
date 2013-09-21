package cepw.contactmanager;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

public class DateChooserDialog extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	private TextView v;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		v = (TextView)getActivity().findViewById(R.id.textview_dob);
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		if (!v.getText().toString().equals("")){
			String[] split = v.getText().toString().split("-");			
			year = Integer.parseInt(split[2]);
			month = Integer.parseInt(split[1])-1;
			day = Integer.parseInt(split[0]);
		}

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		String sday = "";
		String smonth = "";
		
		smonth = (month < 9) ? "0" + (month+1) : "" + (month+1);
		sday = (day < 10) ? "0" + day : "" + day;
		
		v.setText(sday + "-" + smonth + "-" + year);
	}
}
