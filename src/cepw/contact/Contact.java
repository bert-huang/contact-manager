package cepw.contact;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is an object that represents a Contact object for a Contact Manager
 * It contains all the needed information such as Name, Photo, Phone, Email, Address and Date of Birth
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class Contact implements Parcelable {
	
	private int id;
	private Name name;
	private Photo photo;
	private List<Phone> phones;
	private List<Email> emails;
	private List<Address> addresses;
	private DateOfBirth dateOfBirth;

	{
		id = -2;
	}
	
	/**
	 * Constructor of a Contact object
	 * @param name Name object for this contact
	 * @param photo Photo object for this contact
	 * @param phones Phone list for this contact
	 * @param emails Email list for this contact
	 * @param addresses Address list for this contact
	 * @param dateOfBirth DateOfBirth object for this contact
	 */
	public Contact(Name name, Photo photo, 
			List<Phone> phones, List<Email> emails, List<Address> addresses,
			DateOfBirth dateOfBirth) {
		
		this.name = name;
		this.photo = photo;
		this.phones = phones;
		this.emails = emails;
		this.addresses = addresses;
		this.dateOfBirth = dateOfBirth;

	}
	
	/**
	 * Constructor of a Contact object
	 * @param id ID for this contact object
	 * @param photo Photo object for this contact
	 * @param phones Phone list for this contact
	 * @param emails Email list for this contact
	 * @param addresses Address list for this contact
	 * @param dateOfBirth DateOfBirth object for this contact
	 */
	public Contact(int id, Name name, Photo photo, 
			List<Phone> phones, List<Email> emails, List<Address> addresses,
			DateOfBirth dateOfBirth) {
		this(name, photo, phones, emails, addresses, dateOfBirth);
		this.id = id;
	}

	// Getter
	/**
	 * Getter of ID
	 * @return the id of this phone object
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Returns the contacts Photo object
	 * @return A Photo object for this contact
	 */
	public Photo getPhoto() {
		return photo;
	}
	
	/**
	 * Returns the contacts Name object
	 * @return A Name object for this contact
	 */
	public Name getName() {
		return name;
	}
	
	/**
	 * Returns the contacts Phone List
	 * @return A Phone list for this contact
	 */
	public List<Phone> getPhones() {
		return phones;
	}
	
	/**
	 * Returns the contacts Email List
	 * @return A Email list for this contact
	 */
	public List<Email> getEmails() {
		return emails;
	}
	
	/**
	 * Returns the contacts Address List
	 * @return A Address list for this contact
	 */
	public List<Address> getAddresses() {
		return addresses;
	}
	
	/**
	 * Returns the contacts DateOfBirth object
	 * @return A DateOfBirth object for this contact
	 */
	public DateOfBirth getDateOfBirth() {
		return dateOfBirth;
	}
	
	// Setters
	/**
	 * Setter of ID
	 * @param id set the id of this object
	 */
	public void setID(int id) {
		this.id = id;
	}
	
	/**
	 * Overwrites the current Photo object to another
	 * @param photo
	 */
	public void setPhoto(Photo photo) {
		this.photo = photo;
	}
	
	/**
	 * Overwrites the current Name object to another
	 * @param photo
	 */
	public void setName(Name name) {
		this.name = name;
	}
	
	/**
	 * Overwrites the current Phone List to another
	 * @param photo
	 */
	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}
	
	/**
	 * Overwrites the current Email List to another
	 * @param photo
	 */
	public void setEmail(List<Email> emails) {
		this.emails = emails;
	}
	
	/**
	 * Overwrites the current Address List to another
	 * @param photo
	 */
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
	
	/**
	 * Overwrites the current DateOfBirth object to another
	 * @param photo
	 */
	public void setDateOfBirth(DateOfBirth dob) {
		this.dateOfBirth = dob;
	}

	
	//Parcelable
	/**
	 * A description of this Parcelable object 
	 */
	@Override
	public int describeContents() {
		return this.hashCode();
	}

	/**
	 * @see android.os.Parcelable.writeToParcel
	 */
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.id);
		out.writeValue(this.name);
		out.writeValue(this.photo);
		out.writeValue(this.dateOfBirth);
		out.writeList(this.phones);
		out.writeList(this.emails);
		out.writeList(this.addresses);
		
	}
	
	/**
	 * @see android.os.Parcelable.Creator
	 */
	public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
		public Contact createFromParcel(Parcel in) {
			return new Contact(in);
		}

		public Contact[] newArray(int size) {
			return new Contact[size];
		}
	};
	
	/**
	 * Private constructor for Parcelable.Creater
	 * @param in Parcel that contains data
	 */
	private Contact(Parcel in) {
		phones = new ArrayList<Phone>();
		emails = new ArrayList<Email>();
		addresses = new ArrayList<Address>();
		
		this.id = in.readInt();
		this.name = (Name)in.readValue(Name.class.getClassLoader());
		this.photo = (Photo)in.readValue(Photo.class.getClassLoader());
		this.dateOfBirth = (DateOfBirth)in.readValue(DateOfBirth.class.getClassLoader());
		in.readList(phones, Phone.class.getClassLoader());
		in.readList(emails, Email.class.getClassLoader());
		in.readList(addresses, Address.class.getClassLoader());
    }

	public static class Comparators {

		public static class FirstNameComparator implements Comparator<Contact> {

			@Override
			public int compare(Contact lhs, Contact rhs) {
				
				// Sort by first name first
				if (lhs.name.getFirstName().equals(rhs.name.getFirstName())){
					// Then by middle name
					if (lhs.name.getMiddleName().equals(rhs.name.getMiddleName())){
						// Then by last name
						if (lhs.name.getLastName().equals(rhs.name.getLastName())){
							return 0;
						}else if (lhs.name.getLastName().equals("")){
							return 1;
						}else if (rhs.name.getLastName().equals("")){
							return -1;
						}else {
							return lhs.name.getLastName().compareTo(rhs.name.getLastName());
						}
					}else if (lhs.name.getMiddleName().equals("")){
						return 1;
					}else if (rhs.name.getMiddleName().equals("")){
						return -1;
					}else {
						return lhs.name.getMiddleName().compareTo(rhs.name.getMiddleName());
					}
				}else if (lhs.name.getFirstName().equals("")){
					return 1;
				}else if (rhs.name.getFirstName().equals("")){
					return -1;
				}else {
					return lhs.name.getFirstName().compareTo(rhs.name.getFirstName());
				}
			}
			
		}
		
		public static class LastNameComparator implements Comparator<Contact> {

			@Override
			public int compare(Contact lhs, Contact rhs) {
				// Sort by last name first
				if (lhs.name.getLastName().equals(rhs.name.getLastName())){
					// then by first name
					if (lhs.name.getFirstName().equals(rhs.name.getFirstName())){
						// then by middle name
						if (lhs.name.getMiddleName().equals(rhs.name.getMiddleName())){
							return 0;
						}else if (lhs.name.getMiddleName().equals("")){
							return 1;
						}else if (rhs.name.getMiddleName().equals("")){
							return -1;
						}else {
							return lhs.name.getMiddleName().compareTo(rhs.name.getMiddleName());
						}
					}else if (lhs.name.getFirstName().equals("")){
						return 1;
					}else if (rhs.name.getFirstName().equals("")){
						return -1;
					}else {
						return lhs.name.getFirstName().compareTo(rhs.name.getFirstName());
					}
				}else if (lhs.name.getLastName().equals("")){
					return 1;
				}else if (rhs.name.getLastName().equals("")){
					return -1;
				}else {
					return lhs.name.getLastName().compareTo(rhs.name.getLastName());
				}
			}
		}
		
		public static class NumberComparator implements Comparator<Contact> {

			@Override
			public int compare(Contact lhs, Contact rhs) {
				Phone lhsPh = null;
				Phone rhsPh = null;

				for (Phone p : lhs.phones){
					if (p.isPrimary()){
						lhsPh = p;
						break;
					}
				}
				
				for (Phone p : rhs.phones){
					if (p.isPrimary()){
						rhsPh = p;
						break;
					}
				}
				if (lhsPh == null && rhsPh == null) {
					return 0;
				}else if (lhsPh == null) {
					return 1;
				}else if (rhsPh == null) {
					return -1;
				}else {
					return lhsPh.getNumber().compareTo(rhsPh.getNumber());
				}
				
				
			}
		}
	}
}
