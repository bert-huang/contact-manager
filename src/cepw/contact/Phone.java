package cepw.contact;

import java.util.Comparator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is an object that represents a Phone object for a Contact Manager
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class Phone implements Parcelable, Comparable<Phone> {

	private String type;
	private String number;
	private boolean defaultNumber;
	
	/**
	 * Constructor of a phone object
	 * 
	 * @param type
	 * @param number
	 * @throws InvalidPhoneException
	 */
	public Phone(String type, String number, boolean defaultNumber) throws InvalidPhoneException {
		this.type = type;
		if(!number.isEmpty() && !number.matches("(\\+|)[0-9\\- ]+")){
			throw new InvalidPhoneException("Not a valid phone!");
		}
		String temp = number.replaceAll("[^0-9\\+]", "");
		this.number = temp;
		this.defaultNumber = defaultNumber;
	}

	/**
	 * Get the type of this phone object
	 * Can be { "Mobile", "Home", "Work", "Home Fax", "Work Fax", "Other" }
	 * @return a string representing the type of phone
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the number of this phone object
	 * 
	 * @return the number of this phone object
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Return the value of the defaultNumber flag
	 * @return
	 */
	public boolean isDefault() {
		return defaultNumber;
	}

	/**
	 * Make the defaultNumber flag true
	 */
	public void setDefault() {
		defaultNumber = true;
	}

	/**
	 * Make the defaultNumber flag false
	 */
	public void unsetDefault() {
		defaultNumber = false;
	}

	/**
	 * ` A description of this Parcelable object
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
		out.writeByte((byte) (defaultNumber ? 1 : 0));

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
	 * 
	 * @param in
	 *            Parcel that contains data
	 */
	private Phone(Parcel in) {
		this.type = in.readString();
		this.number = in.readString();
		this.defaultNumber = in.readByte() == 1;
	}



	/**
	 * Default comparing method of type Phone
	 * Mobile > Home > Work > Home Fax > Work Fax > Other
	 */
	@Override
	public int compareTo(Phone another) {
		if (this.type.equals("Mobile") && this.type.equals(another.type)) {
			return this.number.compareTo(another.number);
		} else if (this.type.equals("Mobile")) {
			return -1;
		} else if (another.type.equals("Mobile")) {
			return 1;
		} else {
			if (this.type.equals("Home") && this.type.equals(another.type)) {
				return this.number.compareTo(another.number);
			} else if (this.type.equals("Home")) {
				return -1;
			} else if (another.type.equals("Home")) {
				return 1;
			} else {
				if (this.type.equals("Work") && this.type.equals(another.type)) {
					return this.number.compareTo(another.number);
				} else if (this.type.equals("Work")) {
					return -1;
				} else if (another.type.equals("Work")) {
					return 1;
				} else {
					if (this.type.equals("Home Fax")
							&& this.type.equals(another.type)) {
						return this.number.compareTo(another.number);
					} else if (this.type.equals("Home Fax")) {
						return -1;
					} else if (another.type.equals("Home Fax")) {
						return 1;
					} else {
						if (this.type.equals("Work Fax")
								&& this.type.equals(another.type)) {
							return this.number.compareTo(another.number);
						} else if (this.type.equals("Work Fax")) {
							return -1;
						} else if (another.type.equals("Work Fax")) {
							return 1;
						} else {
							if (this.type.equals("Other")
									&& this.type.equals(another.type)) {
								return this.number.compareTo(another.number);
							} else if (this.type.equals("Other")) {
								return -1;
							} else if (another.type.equals("Other")) {
								return 1;
							} else {
								return 0;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * A comparator for sorting phones. 
	 * Default phone will have the highest priority.
	 * Non-Default phone will be sorted by its native way
	 */
	public static class PhoneComparator implements Comparator<Phone> {

		@Override
		public int compare(Phone lhs, Phone rhs) {
			if (lhs.isDefault()) {
				return -1;
			} else if (rhs.isDefault()) {
				return 1;
			} else {
				return lhs.compareTo(rhs);
			}
		}

	}
	
	/**
	 * Exception for when invalid phone number is detected
	 */
	public class InvalidPhoneException extends Exception {
		private static final long serialVersionUID = -6647400545705033613L;

		public InvalidPhoneException(String msg) {
			super(msg);
		}
	}

}
