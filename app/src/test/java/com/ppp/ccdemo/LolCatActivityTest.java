package com.ppp.ccdemo;

import android.widget.ImageView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = "src/main/AndroidManifest.xml")
public class LolCatActivityTest
{

	private static final int TIMEOUT = 30;

	@Test
	public void clickMe() throws Throwable
	{
		LolCatActivity activity = Robolectric.setupActivity(LolCatActivity.class);
		final ImageView iv = (ImageView)activity.findViewById(R.id.lolcat_image);

		// Validate that the initial view is still there
		int imageTag = (int)iv.getTag();
		if (imageTag != 1)
			throw new IllegalArgumentException("invalid initial image tag");

		// Click on the image and make sure we get something back
		iv.performClick();
		CountDownLatch loadedLatch = new CountDownLatch(1);
		loadedLatch.await(TIMEOUT, TimeUnit.SECONDS);
		imageTag = (int)iv.getTag();
		if (imageTag != 0)
			throw new IllegalArgumentException("image not loaded after click");
	}
}
