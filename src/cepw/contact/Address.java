package cepw.contact;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is an object that represents a single address for a Contact Manager
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class Address implements Parcelable {
	private int id;
	private int contactId;
	private String type;
	private String address;

	/**
	 * Constructor of an address object
	 * @param type type of the address, can be Home, Work, Other
	 * @param address physical address
	 */
	public Address(String type, String address) {
		this.type = type;
		this.address = address;
	}
	
	/**
	 * Constructor of an address object
	 * @param id id of this address
	 * @param type type of the address, can be Home, Work, Other
	 * @param address physical address
	 */
	public Address(int id, String type, String address) {
		this(type, address);
		this.id = id;
	}
	
	/**
	 * Constructor of an address object
	 * @param id id of this address
	 * @param type type of the address, can be Home, Work, Other
	 * @param address physical address
	 */
	public Address(int id, int contactId, String type, String address) {
		this(id, type, address);
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
		out.writeInt(id);
		out.writeInt(contactId);
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
		this.id = in.readInt();
		this.contactId = in.readInt();
        this.type = in.readString();
        this.address = in.readString();
    }
}
