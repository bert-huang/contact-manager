package cepw.contactmanager;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable{
	private ContactName name;
	private List<ContactPhone> phones;
	private List<ContactEmail> emails;
	private List<ContactAddress> addresses;
	private ContactDateOfBirth dateOfBirth;

	public Contact(ContactName name, List<ContactPhone> phones,
			List<ContactEmail> emails, List<ContactAddress> addresses,
			ContactDateOfBirth dateOfBirth) {
		
		this.name = name;
		this.phones = phones;
		this.emails = emails;
		this.addresses = addresses;
		this.dateOfBirth = dateOfBirth;

	}

	public ContactName getName() {
		return name;
	}

	public void setName(ContactName name) {
		this.name = name;
	}

	public String getDateOfBirth() {
		return dateOfBirth.getDateOfBirth();
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth.setDateOfBirth(dateOfBirth);
	}
	
	public List<ContactPhone> getPhones() {
		return phones;
	}
	
	public void addPhone(ContactPhone phone) {
		this.phones.add(phone);
	}
	
	public void removePhone(ContactPhone phone) {
		this.phones.remove(phone);
	}
	
	public List<ContactEmail> getEmails() {
		return emails;
	}
	
	public void addEmail(ContactEmail email) {
		this.emails.add(email);
	}
	
	public void removeEmail(ContactEmail email) {
		this.emails.remove(email);
	}
	
	public List<ContactAddress> getAddresses() {
		return addresses;
	}
	
	public void addAddress(ContactAddress address) {
		this.addresses.add(address);
	}
	
	public void removeAddress(ContactAddress address) {
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
		phones = new ArrayList<ContactPhone>();
		emails = new ArrayList<ContactEmail>();
		addresses = new ArrayList<ContactAddress>();
		
		this.name = (ContactName)in.readValue(ContactName.class.getClassLoader());
		in.readList(phones, ContactPhone.class.getClassLoader());
		in.readList(emails, ContactEmail.class.getClassLoader());
		in.readList(addresses, ContactAddress.class.getClassLoader());
		this.dateOfBirth = (ContactDateOfBirth)in.readValue(ContactDateOfBirth.class.getClassLoader());
    }
}
