package cepw.contact;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is an object that represents a Photo object for a Contact Manager
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class Photo implements Parcelable {
	
	public static final int IMAGE_SIZE = 250;
	
	private Bitmap image;
	
	/**
	 * Constructor of a Photo object
	 * @param type
	 */
	public Photo(Bitmap img) {
		// Scale the image to avoid FAILED BINDER TRANSACTION error
		this.image = Bitmap.createScaledBitmap(img, IMAGE_SIZE, IMAGE_SIZE, false);
	}
	
	/**
	 * Constructor of a Photo object
	 * @param type
	 */
	public Photo(byte[] byteArray) {
		this.image = BitmapFactory.decodeByteArray(byteArray , 0, byteArray.length);
	}

	/**
	 * Get the bitmap of Photo
	 * @return the bitmap of Photo
	 */
	public Bitmap getImage() {
		return image;
	}
	
	public byte[] getByteArray() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		this.image.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
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
