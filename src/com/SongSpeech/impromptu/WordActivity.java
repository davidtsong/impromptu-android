package com.SongSpeech.impromptu;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class WordActivity extends Activity {
	private ArrayList<String> wordarray;
	AssetManager am;
	int wordnum = 0;
	BufferedReader dict = null;
	ArrayAdapter<String> adapter;
	ArrayList<String> activewords;
	ListView list;
	AlertDialog alertDialog;
	AlertDialog.Builder builder;
	TextView textView;
	CountDownTimer count;
	int wordtime;
	Button refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_word);

		Button refresh = (Button) findViewById(R.id.word_refreshbutton);
		builder = new AlertDialog.Builder(this);
		list = (ListView) findViewById(R.id.word_list_view);
		wordarray = new ArrayList<String>();
		activewords = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(WordActivity.this,
				R.layout.settings_listview_item, activewords);
		list.setAdapter(adapter);
		am = this.getAssets();

		readSettings();
		createDictionary();
		addListViews();
		final CountDownTimer count = new CountDownTimer((60000 * wordtime),
				1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				int minutes = (int) (millisUntilFinished / 60000);
				int difference = (int) (millisUntilFinished % 60000);
				int seconds = difference / 1000;
				alertDialog.setMessage(minutes + " : " + seconds);
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				alertDialog.setMessage("Time is up!");
			}
		};

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				textView = (TextView) view;

				builder.setTitle(textView.getText())
						.setMessage(textView.getText())
						.setPositiveButton("Close",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										alertDialog.cancel();
										count.cancel();
									}

								}).setCancelable(false);
						
				alertDialog = builder.create();
				alertDialog.show();
				count.start();
			}

		});
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activewords.clear();
				addListViews();
			}
		});
	}

	private void addListViews() {
		// TODO Auto-generated method stub

		for (int i = 0; i < wordnum; i++) {
			activewords.add(getRandomWord());
		}
		adapter.notifyDataSetChanged();
	}
	private void createDictionary() {
		try {
			dict = new BufferedReader(
					new InputStreamReader(am.open("web2.txt")));
			String word;

			while ((word = dict.readLine()) != null) {
				wordarray.add(word);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			dict.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String getRandomWord() {
		return wordarray.get((int) (Math.random() * wordarray.size()));
	}

	private void readSettings() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		wordnum = Integer.parseInt(sharedPref.getString("wordnumkey", ""));
		wordtime = Integer.parseInt(sharedPref.getString("wordtime", ""));
		/* wordnum = Integer.parseInt(sharedPref.getString("wordnumkey", "")); */
	}
}
