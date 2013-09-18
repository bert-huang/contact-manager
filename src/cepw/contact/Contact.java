package cepw.contact;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable{
	private Name name;
	private List<Phone> phones;
	private List<Email> emails;
	private List<Address> addresses;
	private DateOfBirth dateOfBirth;

	public Contact(Name name, List<Phone> phones,
			List<Email> emails, List<Address> addresses,
			DateOfBirth dateOfBirth) {
		
		this.name = name;
		this.phones = phones;
		this.emails = emails;
		this.addresses = addresses;
		this.dateOfBirth = dateOfBirth;

	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public String getDateOfBirth() {
		return dateOfBirth.getDateOfBirth();
	}

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

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeValue(this.name);
		out.writeList(this.phones);
		out.writeList(this.emails);
		out.writeList(this.addresses);
		out.writeValue(this.dateOfBirth);
		
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
		in.readList(phones, Phone.class.getClassLoader());
		in.readList(emails, Email.class.getClassLoader());
		in.readList(addresses, Address.class.getClassLoader());
		this.dateOfBirth = (DateOfBirth)in.readValue(DateOfBirth.class.getClassLoader());
    }
}
