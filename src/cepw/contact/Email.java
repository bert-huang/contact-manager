package cepw.contact;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is an object that represents a Email object for a Contact Manager
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class Email implements Parcelable {
	
	private int id;
	private int contactId;
	private String type;
	private String email;

	/**
	 * Constructor of an email object
	 * @param type type of email, can be Home, Work, Other
	 * @param email address of the email
	 * @throws InvalidEmailException when invalid character token is detected
	 */
	public Email(String type, String email) throws InvalidEmailException {
		this.type = type;
		if (!email.isEmpty() && !email.matches("(.+)[@]([A-Za-z]+[\\.])+[A-Za-z]+"))
			throw new InvalidEmailException("Not a valid E-mail address");
		this.email = email;
	}
	
	/**
	 * 
	 * @param id id of this email object
	 * @param type type of email, can be Home, Work, Other
	 * @param email address of the email
	 * @throws InvalidEmailException when invalid character token is detected
	 */
	public Email(int id, String type, String email) throws InvalidEmailException {
		this(type, email);
		this.id = id;
	}
	
	/**
	 * 
	 * @param id id of this email object
	 * @param type type of email, can be Home, Work, Other
	 * @param email address of the email
	 * @throws InvalidEmailException when invalid character token is detected
	 */
	public Email(int id, int contactId, String type, String email) throws InvalidEmailException {
		this(id, type, email);
		this.contactId = contactId;
	}
	
	/**
	 * Getter of ID
	 * @return the id of this phone object
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Setter of ID
	 * @param id set the id of this object
	 */
	public void setID(int id) {
		this.id = id;
	}
	
	/**
	 * Getter of contact ID
	 * @return the id of contact this object belongs to
	 */
	public int getContactID() {
		return contactId;
	}
	
	/**
	 * Setter of contact ID
	 * @param id set the id of the contact this object belongs to
	 */
	public void setContactID(int id) {
		this.contactId = id;
	}
	
	/**
	 * Get the type of this email object
	 * Can be { "Home", "Work", "Other" }
	 * @return a string representing the type of email
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the value of this email object
	 * @return the value of this email object
	 */
	public String getEmail() {
		return email;
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
		out.writeInt(id);
		out.writeInt(contactId);
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
		this.id = in.readInt();
		this.contactId = in.readInt();
        this.type = in.readString();
        this.email = in.readString();
    }
	
	/**
	 * Exception for when invalid email number is detected
	 */
	public class InvalidEmailException extends Exception {
		private static final long serialVersionUID = 705798288431081538L;

		public InvalidEmailException (String msg) {
			super(msg);
		}
	}
}
