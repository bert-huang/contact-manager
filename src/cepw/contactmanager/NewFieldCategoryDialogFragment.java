package cepw.contactmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class NewFieldCategoryDialogFragment extends DialogFragment {
    
	private final CharSequence[] items = 
		{"Address", "Email", "Date of Birth", "Notes", "Website", "Organisation"};
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.action_new_field)
			.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch(which) {
					case 0:
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						break;
					case 4:
						break;
					case 5:
						break;
					default:
						break;
					}
				}
			});
        return builder.create();
    }
	
}
