package cepw.contactmanager;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactAddress implements Parcelable {
	private String type;
	private String address;

	/**
	 * Constructor of an address object
	 * @param type
	 * @param address
	 */
	public ContactAddress(String type, String address) {
		this.type = type;
		this.address = address;
	}
	
	/**
	 * Get the type of this address object
	 * @return a string representing the type of address
	 */
	public String getType() {
		return type;
	}

	/**
	 * Setter of address type
	 * @param type desired type for this address object
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the value of this address object
	 * @return the value of this address object
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Setter of address
	 * @param address desired value for this address object
	 */
	public void setAddress(String address) {
		this.address = address;
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
		out.writeString(address);

	}

	/**
	 * @see android.os.Parcelable.Creator
	 */
	public static final Parcelable.Creator<ContactAddress> CREATOR = new Parcelable.Creator<ContactAddress>() {
		public ContactAddress createFromParcel(Parcel in) {
			return new ContactAddress(in);
		}

		public ContactAddress[] newArray(int size) {
			return new ContactAddress[size];
		}
	};
	
	/**
	 * Private constructor for Parcelable.Creater
	 * @param in Parcel that contains data
	 */
	private ContactAddress(Parcel in) {
        this.type = in.readString();
        this.address = in.readString();
    }
}
