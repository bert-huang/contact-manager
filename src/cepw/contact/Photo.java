package cepw.contact;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
	
	private Bitmap image;

	/**
	 * Constructor of a Photo object
	 * @param type
	 */
	public Photo(Bitmap img) {
		this.image = img;
	}

	/**
	 * Get the bitmap of Photo
	 * @return the bitmap of Photo
	 */
	public Bitmap getImage() {
		return image;
	}

	/**
	 * Setter of bitmap
	 * @param img desired bitmap for Photo
	 */
	public void setImage(Bitmap img) {
		this.image = img;
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
		out.writeValue(image);
	}

	/**
	 * @see android.os.Parcelable.Creator
	 */
	public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
		public Photo createFromParcel(Parcel in) {
			return new Photo(in);
		}

		public Photo[] newArray(int size) {
			return new Photo[size];
		}
	};
	
	/**
	 * Private constructor for Parcelable.Creater
	 * @param in Parcel that contains data
	 */
	private Photo(Parcel in) {
        this.image = (Bitmap)in.readValue(Bitmap.class.getClassLoader());
    }
}
