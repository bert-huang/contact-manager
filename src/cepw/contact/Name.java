package cepw.contact;

import android.os.Parcel;
import android.os.Parcelable;

public class Name implements Parcelable{

	private String firstName, middleName, lastName, suffix;

	{
		firstName = "";
		middleName = "";
		lastName = "";
		suffix = "";
	}

	/**
	 * Parse Name from either a string of full name or from several parts of name.
	 */
	public static final String[] ParseName(String full,
			String first, String middle, String last, String suffix) {
		
		if (full.equals("")) {
			String fullName = "";
			if (!first.trim().equals("") && first != null)
				fullName += first + " ";
			if (!middle.trim().equals("") && middle != null)
				fullName += middle + " ";
			if (!last.trim().equals("") && last != null)
				fullName += last;
			if (!suffix.trim().equals("") && suffix != null)
				fullName += ", " + suffix;
			return new String[] { fullName };
		} else {
			String fullName = full.trim().replaceAll("([\\s]+|)[,]([\\s]+|)", ",");;
			String firstName = "";
			String middleName = "";
			String lastName = "";
			String nameSuffix = "";
			
			int lastCommaIndex = fullName.lastIndexOf(",");
			
			if (lastCommaIndex == -1) { // Name without suffix
				int lastSpaceIndex = fullName.lastIndexOf(" ");
				if (lastSpaceIndex == -1) {
					firstName = fullName;
				}else {
					lastName = fullName.substring(lastSpaceIndex+1); // Assign last name
					fullName = fullName.substring(0, lastSpaceIndex);
					String[] split = fullName.split("[\\s+]");
					if (split.length > 1) { //Name with middle name
						middleName = split[split.length-1]; //Assign middle name
						
						for (int i = 0; i < split.length-1; i++) {
							firstName += split[i] + " "; 
						}
						firstName = firstName.trim(); //Assign first name
					} else {
						firstName = split[0]; //Assign first name
					}
				}
			}else { // Name with suffix
				nameSuffix = fullName.substring(lastCommaIndex+1);
				fullName = fullName.substring(0, lastCommaIndex);
				
				String[] split = fullName.split("[\\s+]");
				if (split.length >= 3) {
					lastName = split[split.length-1]; //Assign last name
					middleName = split[split.length-2]; //Assign middle name
					
					for (int i = 0; i < split.length-2; i++) {
						firstName += split[i] + " "; 
					}
					firstName = firstName.trim(); //Assign first name
				} else if (split.length == 2) {
					lastName = split[1]; // Assign last name
					firstName = split[0]; //Assign first name
				} else {
					firstName = split[0]; //Assign first name
				}
			}
			return new String[] { firstName, middleName, lastName, nameSuffix };
		}

	}

	public Name(String firstName, String middleName,
			String lastName, String suffix) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.suffix = suffix;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.firstName);
		out.writeString(this.middleName);
		out.writeString(this.lastName);
		out.writeString(this.suffix);
	}
	
	/**
	 * @see android.os.Parcelable.Creator
	 */
	public static final Parcelable.Creator<Name> CREATOR = new Parcelable.Creator<Name>() {
		public Name createFromParcel(Parcel in) {
			return new Name(in);
		}

		public Name[] newArray(int size) {
			return new Name[size];
		}
	};
	
	/**
	 * Private constructor for Parcelable.Creater
	 * @param in Parcel that contains data
	 */
	private Name(Parcel in) {
        this.firstName = in.readString();
        this.middleName = in.readString();
        this.lastName = in.readString();
        this.suffix = in.readString();
    }
	
	

}
