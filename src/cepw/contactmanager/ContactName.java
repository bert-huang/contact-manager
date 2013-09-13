package cepw.contactmanager;

/**
 * @author cookie-paw
 * 
 */
public class ContactName {

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
		
		if (full.equals("") || full == null) {
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
			int lastSpaceIndex = fullName.lastIndexOf(" ");
			
			if (lastSpaceIndex > lastCommaIndex) { // Name without suffix
				lastName = fullName.substring(lastSpaceIndex+1); // Assign last name
				fullName = fullName.substring(0, lastSpaceIndex);
				String[] split = fullName.split("[\\W+]");
				if (split.length > 1) { //Name with middle name
					middleName = split[split.length-1]; //Assign middle name
					
					for (int i = 0; i < split.length-1; i++) {
						firstName += split[i] + " "; 
					}
					firstName = firstName.trim(); //Assign first name
				} else {
					firstName = split[0]; //Assign first name
				}
			}else if (lastCommaIndex > lastSpaceIndex) { // Name with suffix
				nameSuffix = fullName.substring(lastCommaIndex+1);
				fullName = fullName.substring(0, lastCommaIndex);
				
				String[] split = fullName.split("[\\W+]");
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
			}else { // If no space and no comma found, assign it to first name
				firstName = fullName;
			}
			return new String[] { firstName, middleName, lastName, nameSuffix };
		}

	}

	public ContactName(String firstName, String middleName,
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

}
