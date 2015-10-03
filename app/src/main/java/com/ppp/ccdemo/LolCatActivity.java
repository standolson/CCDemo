package com.ppp.ccdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LolCatActivity extends AppCompatActivity
{

	private ImageView lolcatImage;
	private TextView lolcatTitle;

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
				lolcatImage.setClickable(false);
				showProgress();
			}
		});

		hideProgress();
	}

	/**
	 * Shows the cat progress spinner
 	 */
	private void showProgress()
	{
		findViewById(R.id.lolcat_progress).setVisibility(View.VISIBLE);
		lolcatTitle.setText(R.string.lolcat_waiting_title);
	}

	/**
	 * Hides the cat progress spinner
	 */
	private void hideProgress()
	{
		findViewById(R.id.lolcat_progress).setVisibility(View.GONE);
	}
}
