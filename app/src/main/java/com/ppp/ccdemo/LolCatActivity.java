package com.ppp.ccdemo;

import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppp.ccdemo.model.FlickrPhotoMetaData;
import com.ppp.ccdemo.model.PhotoSearchResult;

import java.util.Random;

public class LolCatActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object>
{

	private static final int LOADER_ID = 1;

	private ImageView lolcatImage;
	private TextView lolcatTitle;
	private boolean isInitialized = false;
	private PhotoSearchResult searchResult = null;
	private FlickrPhotoMetaData metaData = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lol_cat);

		lolcatTitle = (TextView)findViewById(R.id.lolcat_title);

		lolcatImage = (ImageView)findViewById(R.id.lolcat_image);
		lolcatImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				// If initialized with the photo list, load a photo.  If not, we
				// still need to get that list.
				if (isInitialized)
					loadRandomPhoto();
				else
					loadPhotoList();
			}
		});
		lolcatImage.setTag(1);

		// Reload state
		if (savedInstanceState != null) {

			searchResult = savedInstanceState.getParcelable("searchResult");
			metaData = savedInstanceState.getParcelable("metaData");
			isInitialized = savedInstanceState.getBoolean("isInitialized", false);

			// If there was no search result, we still have to get one
			if (searchResult == null)
				loadPhotoList();

			// If there was a photo previously displayed, reload it
			else if (metaData != null) {
				showProgress();
				getLoaderManager().restartLoader(LOADER_ID, getLoadPhotoArgs(metaData), this);
			}

		}

		// Initial activity startup requires us to prime the pump by loading the
		// photo list.
		else
			loadPhotoList();

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putParcelable("searchResult", searchResult);
		savedInstanceState.putParcelable("metaData", metaData);
		savedInstanceState.putBoolean("isInitialized", isInitialized);
	}

	@Override
	public Loader<Object> onCreateLoader(int id, Bundle args)
	{
		if (id != LOADER_ID)
			return null;
		return new LolCatLoader(this, args);
	}

	@Override
	public void onLoadFinished(Loader<Object> loader, Object result)
	{

		hideProgress();

		// If there was an exception, we get back a null
		if (result == null)
			displayError();

		// If we got a PhotoSearchResult back, this is the list of photos
		// the user can randomly click into.
		else if (result instanceof PhotoSearchResult) {

			// Make sure that we got a successful result
			searchResult = (PhotoSearchResult)result;
			if (!"ok".equals(searchResult.stat))
				displayError();
			else {
				isInitialized = true;
				lolcatTitle.setText(R.string.default_lolcat_title);
			}

		}

		// We're getting a random photo back
		else {
			byte[] imageBytes = unboxBytes((Byte[])result);
			loadPhoto(imageBytes);
			lolcatTitle.setText(metaData.title);
		}

	}

	@Override
	public void onLoaderReset(Loader<Object> loader) {}

	/**
	 * Shows the cat progress spinner
 	 */
	private void showProgress()
	{
		lolcatImage.setClickable(false);
		findViewById(R.id.lolcat_progress).setVisibility(View.VISIBLE);
		lolcatTitle.setText(R.string.lolcat_waiting_title);
	}

	/**
	 * Hides the cat progress spinner
	 */
	private void hideProgress()
	{
		lolcatImage.setClickable(true);
		findViewById(R.id.lolcat_progress).setVisibility(View.GONE);
	}

	/**
	 * Load the list of photos used to randomly display a photo on user action
	 */
	private void loadPhotoList()
	{
		showProgress();
		getLoaderManager().restartLoader(LOADER_ID, getPhotoListArgs(), this);
	}

	/**
	 * Select a random photo from the PhotoSearchResult we are holding and load it.
	 */
	private void loadRandomPhoto()
	{

		showProgress();

		// Pick a random photo from those we have
		Random random = new Random();
		int index = random.nextInt(searchResult.photos.photo.size());
		metaData = searchResult.photos.photo.get(index);

		// Cause the loader to get the photo
		getLoaderManager().restartLoader(LOADER_ID, getLoadPhotoArgs(metaData), this);

	}

	/**
	 * Returns a Bundle containing the arguments required for the photo search API
	 * which we use to seed the list of images.
	 *
	 * @return The Bunldle with the required arguments
	 */
	private Bundle getPhotoListArgs()
	{
		Bundle args = new Bundle();
		args.putString(LolCatLoader.API_METHOD_ARG, LolCatLoader.PHOTO_SEARCH_METHOD_NAME);
		args.putString(LolCatLoader.SEARCH_TAGS_ARG, "lolcat");
		return args;
	}

	/**
	 * Returns a Bundle containing the arguments required for loading a photo given
	 * the metadata returned from the photo search API.
	 *
	 * @param metaData The metadata
	 * @return The Bundle with the required arguements
	 */
	private Bundle getLoadPhotoArgs(FlickrPhotoMetaData metaData)
	{
		Bundle args = new Bundle();
		args.putString(LolCatLoader.API_METHOD_ARG, LolCatLoader.LOAD_PHOTO_ARGS);
		args.putParcelable(LolCatLoader.LOAD_PHOTO_ARGS, metaData);
		return args;
	}

	/**
	 * Given an array of bytes representng a bitmap image, decode and display
	 * the image.
	 *
	 * @param bytes
	 */
	private void loadPhoto(byte[] bytes)
	{
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		if (bitmap != null) {
			lolcatImage.setImageBitmap(bitmap);
			lolcatImage.setTag(0);
		}
	}

	/**
	 * Indicate to the user that an error has occurred by showing the
	 * error photo and text.
	 */
	private void displayError()
	{
		lolcatTitle.setText(R.string.lolcat_error_title);
		lolcatImage.setImageResource(R.drawable.error_image);
	}

	/**
	 * Unboxes the Byte[] returned by the load photo API into a byte[] that
	 * can be decoded as an image.
	 *
	 * @param bytes The boxed bytes
	 * @return The unboxed bytes
	 */
	private byte[] unboxBytes(Byte[] bytes)
	{
		byte[] retval = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i += 1)
			retval[i] = bytes[i].byteValue();
		return retval;
	}

}
