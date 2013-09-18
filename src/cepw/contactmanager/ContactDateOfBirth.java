package cepw.contactmanager;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactDateOfBirth implements Parcelable {
	
	private String dateOfBirth;

	/**
	 * Constructor of a DateOfBirth object
	 * @param type
	 */
	public ContactDateOfBirth(String dob) {
		this.dateOfBirth = dob;
	}

	/**
	 * Get the value of dateOfBirth
	 * @return the value of dateOfBirth
	 */
	public String getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * Setter of dateOfBirth
	 * @param dob desired value for this date of birth
	 */
	public void setDateOfBirth(String dob) {
		this.dateOfBirth = dob;
	}
	
	/**
	 * A description of this Parcelable object 
	 */
	@Override
	public int describeContents() {
		return hashCode();
	}

	/**
	 * @see android.os.Parcelable.writeToParcel
	 */
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(dateOfBirth);
	}

	/**
	 * @see android.os.Parcelable.Creator
	 */
	public static final Parcelable.Creator<ContactDateOfBirth> CREATOR = new Parcelable.Creator<ContactDateOfBirth>() {
		public ContactDateOfBirth createFromParcel(Parcel in) {
			return new ContactDateOfBirth(in);
		}

		public ContactDateOfBirth[] newArray(int size) {
			return new ContactDateOfBirth[size];
		}
	};
	
	/**
	 * Private constructor for Parcelable.Creater
	 * @param in Parcel that contains data
	 */
	private ContactDateOfBirth(Parcel in) {
        this.dateOfBirth = in.readString();
    }
}
