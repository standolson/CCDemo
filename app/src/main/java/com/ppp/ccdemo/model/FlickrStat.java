package com.ppp.ccdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FlickrStat implements Parcelable {

	public String stat;
	public int code;
	public String message;

	//
	// Parcelable
	//

	public static final Parcelable.Creator<FlickrStat> CREATOR = new Parcelable.Creator<FlickrStat>()
	{
		@Override
		public FlickrStat createFromParcel(Parcel in) { return new FlickrStat(in); }
		@Override
		public FlickrStat[] newArray(int size) { return new FlickrStat[size]; }
	};

	@Override
	public int describeContents() { return 0; }

	public FlickrStat(Parcel in)
	{
		stat = in.readString();
		code = in.readInt();
		message = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(stat == null ? "" : stat);
		dest.writeInt(code);
		dest.writeString(message == null ? "" : message);
	}

}
