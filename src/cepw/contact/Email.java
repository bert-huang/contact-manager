package cepw.contact;

import android.os.Parcel;
import android.os.Parcelable;

public class Email implements Parcelable {
	private String type;
	private String email;

	/**
	 * Constructor of an email object
	 * @param type
	 * @param email
	 * @throws InvalidEmailException 
	 */
	public Email(String type, String email) throws InvalidEmailException {
		this.type = type;
		if (!email.isEmpty() && !email.matches("(.+)[@]([A-Za-z]+[\\.])+[A-Za-z]+"))
			throw new InvalidEmailException("Not a valid E-mail address");
		this.email = email;
	}
	
	/**
	 * Get the type of this email object
	 * @return a string representing the type of email
	 */
	public String getType() {
		return type;
	}

	/**
	 * Setter of email type
	 * @param type desired type for this email object
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the value of this email object
	 * @return the value of this email object
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Setter of email number
	 * @param email desired email address for this email object
	 */
	public void setEmail(String email) {
		this.email = email;
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
		out.writeString(email);

	}

	/**
	 * @see android.os.Parcelable.Creator
	 */
	public static final Parcelable.Creator<Email> CREATOR = new Parcelable.Creator<Email>() {
		public Email createFromParcel(Parcel in) {
			return new Email(in);
		}

		public Email[] newArray(int size) {
			return new Email[size];
		}
	};
	
	/**
	 * Private constructor for Parcelable.Creater
	 * @param in Parcel that contains data
	 */
	private Email(Parcel in) {
        this.type = in.readString();
        this.email = in.readString();
    }
	
	/**
	 * Exception for when invalid phone number is detected
	 */
	public class InvalidEmailException extends Exception {
		private static final long serialVersionUID = 705798288431081538L;

		public InvalidEmailException (String msg) {
			super(msg);
		}
	}
}
