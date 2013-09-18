package cepw.contact;

import android.os.Parcel;
import android.os.Parcelable;

public class Phone implements Parcelable {

	private String type;
	private String number;

	/**
	 * Constructor of a phone object
	 * @param type
	 * @param number
	 * @throws InvalidPhoneException 
	 */
	public Phone(String type, String number) {
		this.type = type;
		String temp = number.replaceAll("[^0-9]", "");
		this.number = temp;
	}
	
	/**
	 * Get the type of this phone object
	 * @return a string representing the type of phone
	 */
	public String getType() {
		return type;
	}

	/**
	 * Setter of phone type
	 * @param type desired type for this phone object
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the number of this phone object
	 * @return the number of this phone object
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Setter of phone number
	 * @param number desired number for this phone object
	 */
	public void setNumber(String number) {
		this.number = number;
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
		out.writeString(type);
		out.writeString(number);

	}

	/**
	 * @see android.os.Parcelable.Creator
	 */
	public static final Parcelable.Creator<Phone> CREATOR = new Parcelable.Creator<Phone>() {
		public Phone createFromParcel(Parcel in) {
			return new Phone(in);
		}

		public Phone[] newArray(int size) {
			return new Phone[size];
		}
	};
	
	/**
	 * Private constructor for Parcelable.Creater
	 * @param in Parcel that contains data
	 */
	private Phone(Parcel in) {
        this.type = in.readString();
        this.number = in.readString();
    }
	
	/**
	 * Exception for when invalid phone number is detected
	 */
	class InvalidPhoneException extends Exception {
		private static final long serialVersionUID = -6647400545705033613L;

		public InvalidPhoneException (String msg) {
			super(msg);
		}
	}
}
