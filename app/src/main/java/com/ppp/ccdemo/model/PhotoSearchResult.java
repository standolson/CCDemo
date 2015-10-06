package com.ppp.ccdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoSearchResult implements Parcelable {

	public FlickrPhotos photos;
	public String stat;

	//
	// Parcelable
	//

	public static final Parcelable.Creator<PhotoSearchResult> CREATOR = new Parcelable.Creator<PhotoSearchResult>()
	{
		@Override
		public PhotoSearchResult createFromParcel(Parcel in) { return new PhotoSearchResult(in); }
		@Override
		public PhotoSearchResult[] newArray(int size) { return new PhotoSearchResult[size]; }
	};

	@Override
	public int describeContents() { return 0; }

	public PhotoSearchResult(Parcel in)
	{
		photos = new FlickrPhotos(in);
		stat = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		photos.writeToParcel(dest, flags);
		dest.writeString(stat == null ? "" : stat);
	}

}
