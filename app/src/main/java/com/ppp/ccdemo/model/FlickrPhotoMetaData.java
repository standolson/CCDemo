package com.ppp.ccdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FlickrPhotoMetaData implements Parcelable {

	public String id;
	public String owner;
	public String secret;
	public String server;
	public int farm;
	public String title;
	public int ispublic;
	public int isfriend;
	public int isfamily;

	//
	// Parcelable
	//

	public static final Parcelable.Creator<FlickrPhotoMetaData> CREATOR = new Parcelable.Creator<FlickrPhotoMetaData>()
	{
		@Override
		public FlickrPhotoMetaData createFromParcel(Parcel in) { return new FlickrPhotoMetaData(in); }
		@Override
		public FlickrPhotoMetaData[] newArray(int size) { return new FlickrPhotoMetaData[size]; }
	};

	@Override
	public int describeContents() { return 0; }

	public FlickrPhotoMetaData(Parcel in)
	{
		id = in.readString();
		owner = in.readString();
		secret = in.readString();
		server = in.readString();
		farm = in.readInt();
		title = in.readString();
		ispublic = in.readInt();
		isfriend = in.readInt();
		isfamily = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(id == null ? "" : id);
		dest.writeString(owner == null ? "" : owner);
		dest.writeString(secret == null ? "" : secret);
		dest.writeString(server == null ? "" : server);
		dest.writeInt(farm);
		dest.writeString(title == null ? "" : title);
		dest.writeInt(ispublic);
		dest.writeInt(isfriend);
		dest.writeInt(isfamily);
	}

}
