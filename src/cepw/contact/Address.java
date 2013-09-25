package cepw.contact;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is an object that represents a single address for a Contact Manager
 * @author cookie-paw
 */
public class Address implements Parcelable {
	private String type;
	private String address;

	/**
	 * Constructor of an address object
	 * @param type
	 * @param address
	 */
	public Address(String type, String address) {
		this.type = type;
		this.address = address;
	}
	
	/**
	 * Get the type of this address object
	 * Can be { "Home", "Work", "Other" }
	 * @return a string representing the type of address
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the value of this address object
	 * @return the value of this address object
	 */
	public String getAddress() {
		return address;
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
	public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
		public Address createFromParcel(Parcel in) {
			return new Address(in);
		}

		public Address[] newArray(int size) {
			return new Address[size];
		}
	};
	
	/**
	 * Private constructor for Parcelable.Creater
	 * @param in Parcel that contains data
	 */
	private Address(Parcel in) {
        this.type = in.readString();
        this.address = in.readString();
    }
}
