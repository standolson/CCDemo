package com.ppp.ccdemo;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ppp.ccdemo.model.FlickrPhotoMetaData;
import com.ppp.ccdemo.model.PhotoSearchResult;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LolCatLoader extends AsyncTaskLoader<Object> {

	// API Key
	private static final String FLICKR_API_KEY = "a406dee197a52009af02ca8732ff799a";

	// Geolocation of US for "woe_id" arg
	private static final String US_WOE_ID = "23424977";

	// URL arguments
	private static final String API_KEY_ARG = "api_key";
	private static final String API_FORMAT_ARG = "format";
	private static final String API_NOJSONCALLBACK_ARG = "nojsoncallback";
	private static final String API_WOE_ID_ARG = "woe_id";
	public static final String API_METHOD_ARG = "method";
	public static final String SEARCH_TAGS_ARG = "tags";
	public static final String LOAD_PHOTO_ARGS = "loadPhotoArgs";

	// Flickr API names
	public static final String PHOTO_SEARCH_METHOD_NAME = "flickr.photos.search";

	// Endpoints
	private static final String FLICKR_API_ENDPOINT = "https://api.flickr.com/services/rest";
	private static final String FLICKR_PHOTO_ENDPOINT = "https://farm%d.staticflickr.com/%s/%s_%s.jpg";

	private Bundle args;

	public LolCatLoader(Context context, Bundle args)
	{

		super(context);

		// First part of a workaround to cause instantiation of this Loader to
		// perform a load.
		//
		// See: http://stackoverflow.com/a/19481256
		onContentChanged();

		this.args = args;

	}

	@Override
	public Object loadInBackground()
	{

		// Generate the URL based on parameters at instantiation
		String url = getUrlFromArgs();

		// Build the request
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();

		try {

			// Execute the request to get a response
			Response response = client.newCall(request).execute();

			// Get the response as a stream
			InputStream stream = response.body().byteStream();

			// Process the response
			Object result = parseResultFromArgs(stream);
			return result;

		}
		catch (IOException e) {
			Log.e(getClass().getSimpleName(), "loadInBackground exception: " + e.getClass().getSimpleName());
			return null;
		}

	}

	@Override
	public void deliverResult(Object result)
	{
		super.deliverResult(result);
	}

	@Override
	public void onCanceled(Object arg) {}

	@Override
	public void onStartLoading()
	{
		// Second part of the fix outlined in http://stackoverflow.com/a/19481256
		if (takeContentChanged())
			forceLoad();
	}

	@Override
	protected void onStopLoading() { cancelLoad(); }

	@Override
	protected void onReset() {}

	/**
	 * Given the arguments we are currently working with, create the URL for the
	 * specified API.
	 *
	 * @return The URL if arguments are proper, null otherwise.
	 */
	private String getUrlFromArgs()
	{

		StringBuilder sb = new StringBuilder();
		String method = args.getString(API_METHOD_ARG);

		// Photo search
		if (PHOTO_SEARCH_METHOD_NAME.equals(method)) {
			String tags = args.getString(SEARCH_TAGS_ARG, "");
			sb.append(FLICKR_API_ENDPOINT).append("?");
			sb.append(API_KEY_ARG).append("=").append(FLICKR_API_KEY).append("&");
			sb.append(API_WOE_ID_ARG).append("=").append(US_WOE_ID).append("&");
			sb.append(API_FORMAT_ARG).append("=json").append("&");
			sb.append(API_NOJSONCALLBACK_ARG).append("=1").append("&");
			sb.append(API_METHOD_ARG).append("=").append(method).append("&");
			sb.append(SEARCH_TAGS_ARG).append("=").append(tags);
		}

		// Load photo
		else if (LOAD_PHOTO_ARGS.equals(method)) {
			FlickrPhotoMetaData metaData = args.getParcelable(LOAD_PHOTO_ARGS);
			return String.format(FLICKR_PHOTO_ENDPOINT, metaData.farm, metaData.server, metaData.id, metaData.secret);
		}

		// If we got bad input, return null so the caller can act appropriately
		return sb.length() > 0 ? sb.toString() : null;

	}

	/**
	 * Parses the given input stream and returns a response object for the
	 * specified API.
	 *
	 * @param stream The stream
	 * @return An object representing the response for the API
	 */
	private Object parseResultFromArgs(InputStream stream) throws IOException
	{

		Object result = null;
		String method = args.getString(API_METHOD_ARG);

		// Photo search
		if (PHOTO_SEARCH_METHOD_NAME.equals(method)) {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			InputStreamReader reader = new InputStreamReader(stream);
			result = gson.fromJson(reader, PhotoSearchResult.class);
			reader.close();
		}

		else if (LOAD_PHOTO_ARGS.equals(method)) {
			byte[] bytes = streamToBytes(stream);
			result = boxBytes(bytes);
		}

		return result;

	}

	/**
	 * Reads the given stream and converts its contents to an array of bytes
	 *
	 * @param is The stream
	 * @return The full content of the stream as a byte array
	 */
	private byte[] streamToBytes(InputStream is)
	{

		byte byteData[] = new byte[1024];
		byte returnBytes[] = null;
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();

		try {

			int c;
			int totalBytes = 0;

			// Read the data a chunk at a time accumulating
			// the bytes in the stream
			while ((c = is.read(byteData, 0, byteData.length)) != -1)  {
				bytes.write(byteData, 0, c);
				totalBytes += c;
			}

			// If we read anything, convert the stream to a
			// byte array
			if (totalBytes > 0)
				returnBytes = bytes.toByteArray();

			bytes.close();

		}
		catch (IOException e) {}

		return returnBytes;

	}

	private Byte[] boxBytes(byte[] bytes)
	{
		Byte[] retval = new Byte[bytes.length];
		for (int i = 0; i < bytes.length; i += 1)
			retval[i] = bytes[i];
		return retval;
	}

}
