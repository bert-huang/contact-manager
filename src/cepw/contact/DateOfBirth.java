package cepw.contact;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is an object that represents a Date of Birth for a Contact Manager
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class DateOfBirth implements Parcelable {
	
	private String dateOfBirth;

	/**
	 * Constructor of a DateOfBirth object
	 * @param type
	 */
	public DateOfBirth(String dob) {
		this.dateOfBirth = dob;
	}

	/**
	 * Returns the value of dateOfBirth
	 * @return the value of dateOfBirth
	 */
	public String getValue() {
		return dateOfBirth;
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
	public static final Parcelable.Creator<DateOfBirth> CREATOR = new Parcelable.Creator<DateOfBirth>() {
		public DateOfBirth createFromParcel(Parcel in) {
			return new DateOfBirth(in);
		}

		public DateOfBirth[] newArray(int size) {
			return new DateOfBirth[size];
		}
	};
	
	/**
	 * Private constructor for Parcelable.Creater
	 * @param in Parcel that contains data
	 */
	private DateOfBirth(Parcel in) {
        this.dateOfBirth = in.readString();
    }
}
