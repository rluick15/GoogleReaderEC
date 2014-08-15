package com.example.googlereaderec;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainListActivity extends ListActivity {
	
	//Strings from strings.xml
	protected String mException;
	protected String mNetUnavailable;
	
	protected String[] mGoogleResultTitles;
	public static final int NUMBER_OF_POSTS = 20;
	public static final String TAG = MainListActivity.class.getSimpleName();
	
	private final String KEY_TITLE = "title";
	private final String KEY_URL = "url";
	
	Button mSearchGoogleButton;
	EditText mSearchText;
	protected ProgressBar mProgressBar;
	public String mUserInput = "";
	
	protected JSONObject mSearchData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);
		
		mSearchGoogleButton = (Button) findViewById(R.id.searchButton);
		mSearchText = (EditText) findViewById(R.id.searchQuery);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		mProgressBar.setVisibility(View.INVISIBLE);
		
		//Get String resources from strings.xml
		mException = getString(R.string.exception);
		mNetUnavailable = getString(R.string.network_unavailable);
		
		//This if block checks if the network is available using a method. If not, it displays an error message
		if(isNetworkAvailable()) {
			searchGoogleClickButton();
		}
		else {
			Toast.makeText(this, mNetUnavailable, Toast.LENGTH_LONG).show();
		}

	}

	//Takes in the user input and executes the search when the button is clicked
	private void searchGoogleClickButton() {
		mSearchGoogleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mProgressBar.setVisibility(View.VISIBLE);
				mUserInput = mSearchText.getText().toString();
				Log.v("SearchTextInside", mUserInput);
				
				GetGoogleResultsTask getGoogleResultsTask = new GetGoogleResultsTask();
				getGoogleResultsTask.execute();
				
				mSearchText.setText("");
			}
		});
	}

	//Checks if the network is available for use
	private boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		
		boolean isAvailable = false;
		if(networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		}
		return isAvailable;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_list, menu);
		return true;
	}
	
	public void updateList() {
		mProgressBar.setVisibility(View.INVISIBLE);
		
		if(mSearchData == null) {
			updateDisplayForError();
		}
		else {
			JSONArray jsonResults;
			try {
				jsonResults = mSearchData.getJSONArray("results");
//				ArrayList<HashMap<String, String>> googleResults = new ArrayList<HashMap<String, String>>();
				
				for(int i = 0; i < jsonResults.length(); i++) {
					SearchResult searchResult = new SearchResult();
					JSONObject result = jsonResults.getJSONObject(i);
					
					searchResult.setTitle(result.getString(KEY_TITLE));
					String title = searchResult.getTitle();
					title = Html.fromHtml(title).toString();
					Log.v(TAG, "Result " + i + ": " + title);
					
//					JSONObject result = jsonResults.getJSONObject(i);
//					String title = result.getString(KEY_TITLE);
//					title = Html.fromHtml(title).toString();
//					String url = result.getString(KEY_URL);
//					url = Html.fromHtml(url).toString();
//					
//					Log.v(TAG, "Result " + i + ": " + title);
//					Log.v(TAG, "Result " + i + ": " + url);
//					
//					HashMap<String, String> googleResultHash = new HashMap<String, String>();
//					googleResultHash.put(KEY_TITLE, title);
//					googleResultHash.put(KEY_URL, url);
//					
//					googleResults.add(googleResultHash);
				}
				
//				String[] keys = {KEY_TITLE, KEY_URL};
//				int[] ids = {android.R.id.text1, android.R.id.text2};
//				SimpleAdapter adapter = new SimpleAdapter(this, googleResults, android.R.layout.simple_expandable_list_item_2, keys, ids);
//				setListAdapter(adapter);
			} 
			catch (JSONException e) {
				Log.e(TAG, "Exception Caught", e);
			}					
			//TODO: format into list and add loading bar
		}
	}
	
	private void updateDisplayForError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.error_title));
		builder.setMessage(getString(R.string.error_message));
		builder.setPositiveButton(android.R.string.ok, null);
		AlertDialog dialog = builder.create();
		dialog.show();
		
		TextView emptyTextView = (TextView) getListView().getEmptyView();
		emptyTextView.setText(getString(R.string.no_items));
	}


	//Async task
	public class GetGoogleResultsTask extends AsyncTask<Object, Void, JSONObject> {
	
		@Override
		protected JSONObject doInBackground(Object... params) {
			int responseCode = -1;
			JSONObject jsonResponseData = null;
			
			try {
				jsonResponseData = connectToServer(jsonResponseData);
					
			} 
			catch(MalformedURLException e) {
				Log.e(TAG, mException, e);
			} 
			catch(IOException e) {
				Log.e(TAG, mException, e);
			}
			catch(Exception e) {
				Log.e(TAG, mException, e);
			}
			
			return jsonResponseData;
		}
		
		//Attempts to connect to the google API search result server based on the users input
		private JSONObject connectToServer(JSONObject jsonResponseData) throws MalformedURLException, IOException, JSONException {
			int responseCode;
			
			URL blogFeedUrl = new URL("https://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + mUserInput);
			HttpURLConnection connection = (HttpURLConnection) blogFeedUrl.openConnection();
			connection.connect();
			
			responseCode = connection.getResponseCode();
			jsonResponseData = responseCodeCheck(jsonResponseData, responseCode, connection);
			
			return jsonResponseData;
		}
		
		//Checks if the response code is valid. If so, it executes the the code to get JSON results. If not, it logs an error
		private JSONObject responseCodeCheck(JSONObject jsonResponseData, int responseCode, HttpURLConnection connection) throws IOException, JSONException {
			
			if(responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = connection.getInputStream();
				Reader reader = new InputStreamReader(inputStream);
				
				jsonResponseData = getJSONResults(reader);
			}
			else {
				Log.i(TAG, "Unsuccessful HTTP Response Code: " + responseCode);
			}
			
			return jsonResponseData;
		}
		
		//Reads the JSON results into a string and extracts the url/other data from each result
		private JSONObject getJSONResults(Reader reader) throws IOException,JSONException {
			JSONObject jsonResponseData;
			int nextCharacter;
			String responseData = "";
			
			while(true) { 
			    nextCharacter = reader.read(); 	// read() without parameters returns one character
			    if(nextCharacter == -1) 		// A return value of -1 means that we reached the end
			        break;
			    responseData += (char) nextCharacter;
			}
			
			JSONObject jsonResponse = new JSONObject(responseData);
			jsonResponseData = jsonResponse.getJSONObject("responseData");
			
			return jsonResponseData;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			mSearchData = result;
			updateList();
		}
	}


}
