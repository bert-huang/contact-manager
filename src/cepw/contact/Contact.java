package cepw.contact;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
	private Name name;
	private Photo photo;
	private List<Phone> phones;
	private List<Email> emails;
	private List<Address> addresses;
	private DateOfBirth dateOfBirth;

	public Contact(Name name, Photo photo, List<Phone> phones,
			List<Email> emails, List<Address> addresses,
			DateOfBirth dateOfBirth) {
		
		this.name = name;
		this.photo = photo;
		this.phones = phones;
		this.emails = emails;
		this.addresses = addresses;
		this.dateOfBirth = dateOfBirth;

	}



	// Direct getters setters
	public void setPhoto(Photo photo) {
		this.photo = photo;
	}
	
	public Name getName() {
		return name;
	}
	
	public void setName(Name name) {
		this.name = name;
	}
	
	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}
	
	public void setEmail(List<Email> emails) {
		this.emails = emails;
	}
	
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
	
	public void setDateOfBirth(DateOfBirth dob) {
		this.dateOfBirth = dob;
	}

	
	//Indirect Getters and setters
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth.setDateOfBirth(dateOfBirth);
	}
	
	public List<Phone> getPhones() {
		return phones;
	}
	
	public void addPhone(Phone phone) {
		this.phones.add(phone);
	}
	
	public void removePhone(Phone phone) {
		this.phones.remove(phone);
	}
	
	public List<Email> getEmails() {
		return emails;
	}
	
	public void addEmail(Email email) {
		this.emails.add(email);
	}
	
	public void removeEmail(Email email) {
		this.emails.remove(email);
	}
	
	public List<Address> getAddresses() {
		return addresses;
	}
	
	public void addAddress(Address address) {
		this.addresses.add(address);
	}
	
	public void removeAddress(Address address) {
		this.addresses.remove(address);
	}
	
	public Bitmap getImage() {
		return photo.getImage();
	}

	public void setImage(Bitmap photo) {
		this.photo.setImage(photo);
	}
	
	public String getDateOfBirth() {
		return dateOfBirth.getDateOfBirth();
	}

	
	//Parcelable
	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
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
		
		this.name = (Name)in.readValue(Name.class.getClassLoader());
		this.photo = (Photo)in.readValue(Photo.class.getClassLoader());
		this.dateOfBirth = (DateOfBirth)in.readValue(DateOfBirth.class.getClassLoader());
		in.readList(phones, Phone.class.getClassLoader());
		in.readList(emails, Email.class.getClassLoader());
		in.readList(addresses, Address.class.getClassLoader());
    }

	public static class Comparators {

		public static class CompareByFirstName implements Comparator<Contact> {

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
		
		public static class CompareByLastName implements Comparator<Contact> {

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
		
		public static class CompareByPhone implements Comparator<Contact> {

			@Override
			public int compare(Contact lhs, Contact rhs) {
				Phone lhsPh = null;
				Phone rhsPh = null;

				for (Phone p : lhs.phones){
					if (p.isDefault()){
						lhsPh = p;
						break;
					}
				}
				
				for (Phone p : rhs.phones){
					if (p.isDefault()){
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
					return lhsPh.compareTo(rhsPh);
				}
				
				
			}
		}
	}
}
