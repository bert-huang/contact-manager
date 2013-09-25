package cepw.contact;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is an object that represents a Name object for a Contact Manager
 * @author cookie-paw
 */
public class Name implements Parcelable{

	private String firstName = "";
	private String middleName = "";
	private String lastName = "";
	private String suffix = "";

	/**
	 * Parse Name from either a string of full name or from several parts of name.
	 * Put full as NULL if you want to merge.
	 * Put anything except full as NULL if you want to split
	 * 
	 * To convert from full name to individual fields:
	 * 		Suffix will be the string after the LAST occurrence of a comma ","
	 * 		Last name will be the last token of the String, or the token prior to suffix
	 * 		Middle name will be the token prior to Last name
	 * 		First name will be all token prior to Middle name
	 */
	public static final String[] parseName(String full,
			String first, String middle, String last, String suffix) {
		
		
		//Merge
		if (full == null) {
			String fullName = "";
			if (!first.trim().equals("") && first != null)
				fullName += first + " ";
			if (!middle.trim().equals("") && middle != null)
				fullName += middle + " ";
			if (!last.trim().equals("") && last != null)
				fullName += last;
			if (!suffix.trim().equals("") && suffix != null)
				fullName += ", " + suffix;
			return new String[] { fullName.trim() };
		
		//Split
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
			return new String[] { firstName.trim(), middleName.trim(), lastName.trim(), nameSuffix.trim() };
		}

	}

	/**
	 * Constructor of a Name object
	 * @param firstName First name of the object
	 * @param middleName Middle name of the object
	 * @param lastName Last name of the object
	 * @param suffix Suffix of the object
	 */
	public Name(String firstName, String middleName,
			String lastName, String suffix) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.suffix = suffix;
	}

	// Getters
	/**
	 * Get the first name of this object
	 * @return First name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Get the middle name of this object
	 * @return Middle name
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * Get the last name of this object
	 * @return Last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Get the name suffix of this object
	 * @return Name suffix
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * ` A description of this Parcelable object
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
