package com.ppp.ccdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class FlickrPhotos implements Parcelable {

	public int page;
	public int pages;
	public int perpage;
	public int total;
	public ArrayList<FlickrPhotoMetaData> photo;

	//
	// Parcelable
	//

	public static final Parcelable.Creator<FlickrPhotos> CREATOR = new Parcelable.Creator<FlickrPhotos>()
	{
		@Override
		public FlickrPhotos createFromParcel(Parcel in) { return new FlickrPhotos(in); }
		@Override
		public FlickrPhotos[] newArray(int size) { return new FlickrPhotos[size]; }
	};

	@Override
	public int describeContents() { return 0; }

	public FlickrPhotos(Parcel in)
	{
		page = in.readInt();
		pages = in.readInt();
		perpage = in.readInt();
		total = in.readInt();
		int totalPhotos = in.readInt();
		photo = new ArrayList<FlickrPhotoMetaData>(totalPhotos);
		for (int i = 0; i < totalPhotos; i += 1) {
			FlickrPhotoMetaData metaData = new FlickrPhotoMetaData(in);
			photo.add(metaData);
		}
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(page);
		dest.writeInt(pages);
		dest.writeInt(perpage);
		dest.writeInt(total);
		dest.writeInt(photo.size());
		for (int i = 0; i < photo.size(); i += 1) {
			FlickrPhotoMetaData metaData = photo.get(i);
			metaData.writeToParcel(dest, flags);
		}
	}

}
