package mobi.durian.tbackup;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import mobi.durian.tbackup.R;
import mobi.durian.tbackup.UserDataDialog.UserDataDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Browser;
import android.provider.CallLog;
import android.provider.Contacts;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class RestoreActivity extends Activity implements OnClickListener, UserDataDialogListener{
	static final int DIALOG_USER_DATA = 0;
	TextView statusText, reportText;
	Button restoreButton;
	ScrollView reportScrollView;
	
	StringBuffer reportBuffer;
	
    ProgressBar mainProgressBar;
	int maxCounter, restoreCounter;

	private static final String TAG = "RestoreActivity";
    
    private Properties restoreSetting;
    
	String [] strArr = new String[2];
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore);
        
        statusText = (TextView) findViewById(R.id.statusText);
        restoreButton = (Button) findViewById(R.id.restoreButton);
        mainProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);
        reportText = (TextView) findViewById(R.id.reportText);
        reportScrollView = (ScrollView) findViewById(R.id.reportScrollView);

        reportBuffer = new StringBuffer();

        //TODO: load setting
        restoreSetting = new Properties();
        restoreSetting.put("sms", false);
        restoreSetting.put("contact", false);
        restoreSetting.put("callhistory", false);

        restoreButton.setOnClickListener(this);
    }
    
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch(id) {
		case DIALOG_USER_DATA:
			dialog = new UserDataDialog(this, this, restoreSetting);
			break;
		}
		return dialog;
	}

    public void onClose(boolean save) {
    	if(save) {
    		// TODO: save restore setting
    	}
    	// run restore
		try {
			FileManager.checkSdCard();
			new Restoring().execute(null);
		} catch (IOException e) {
		}
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.restoreButton:
			showDialog(DIALOG_USER_DATA);
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Get menu inflater object from context
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		}
		return true;
	}

	class Restoring extends AsyncTask<String, String, String> {
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			statusText.setText(values[0]);
			if(values.length>1) {
				reportText.setText(values[1]);
				
				// Scroll to the bottom to report text
				reportScrollView.post(new Runnable() {
				    public void run() {
				    	reportScrollView.fullScroll(ScrollView.FOCUS_DOWN);
				    }
				});
			}
		}

		private void restoreSMS() {
			StringBuilder status = new StringBuilder();
			// SMS
			BufferedReader sms = FileManager.readFile(FileManager.SMS_FILE);
			try {
				Log.d(TAG, "restoring sms...");
				String line = sms.readLine();
				// get num of record
				int total = Integer.parseInt(line);
				float denominator = total / 100.0F;
				int savedCounter = 1;
				line = sms.readLine();
				while(line != null) {
					JSONObject obj = new JSONObject(line);
					ContentValues val = new ContentValues();
					Iterator<String> keys = obj.keys();
					while(keys.hasNext()) {
						String key = keys.next();
						val.put(key, obj.getString(key));
					}
					getContentResolver().insert(Uri.parse("content://sms"), val);
					line = sms.readLine();
					mainProgressBar.setProgress((int) (savedCounter / denominator));
					status.append(getString(R.string.restore)).append(" ")
						  .append(String.valueOf(savedCounter))
						  .append("/")
						  .append(String.valueOf(total))
						  .append(" ").append(getString(R.string.smses));
					publishProgress(status.toString());
					// Clear status buffer
					status.setLength(0);
					++savedCounter;
				}
				reportBuffer.append(getString(R.string.reportTextRestore)).append(" ").append(total).append(" ").append(getString(R.string.smses)).append("\n");
				publishProgress("", reportBuffer.toString());
				Log.d(TAG, "restore sms finished");
			}catch(Exception e) {
				Log.e(TAG, "failed restore sms", e);
			}finally {
				try {
					sms.close();
				}catch(Exception e) {}
			}
		}
		
		private void restoreCallHistory() {
			StringBuilder status = new StringBuilder();
			// CallHistory
			BufferedReader callHistory = FileManager.readFile(FileManager.CALLHISTORY_FILE);
			try {
				Log.d(TAG, "restoring callHistory...");
				String line = callHistory.readLine();
				// get num of record
				int total = Integer.parseInt(line);
				float denominator = total / 100.0F;
				int savedCounter = 1;
				line = callHistory.readLine();
				while(line != null) {
					JSONObject obj = new JSONObject(line);
					ContentValues val = new ContentValues();
					Iterator<String> keys = obj.keys();
					while(keys.hasNext()) {
						String key = keys.next();
						val.put(key, obj.getString(key));
					}
					getContentResolver().insert(CallLog.Calls.CONTENT_URI, val);
					line = callHistory.readLine();
					mainProgressBar.setProgress((int) (savedCounter / denominator));
					status.append(getString(R.string.restore)).append(" ")
						  .append(String.valueOf(savedCounter))
						  .append("/")
						  .append(String.valueOf(total))
						  .append(" ").append(getString(R.string.calls));
					publishProgress(status.toString());
					// Clear status buffer
					status.setLength(0);
					++savedCounter;
				}
				reportBuffer.append(getString(R.string.reportTextRestore)).append(" ").append(total).append(" ").append(getString(R.string.calls)).append("\n");
				publishProgress("", reportBuffer.toString());
				Log.d(TAG, "restore callHistory finished");
			}catch(Exception e) {
				Log.e(TAG, "failed restore callHistory", e);
			}finally {
				try {
					callHistory.close();
				}catch(Exception e) {}
			}
		}
		
		private void restoreContacts() {
			StringBuilder status = new StringBuilder();
			// Contacts
    		BufferedReader contact = FileManager.readFile(FileManager.CONTACT_FILE);
    		try {
    			Log.d(TAG, "restoring contact...");
    			String line = contact.readLine();
    			// get num of record
    			int total = Integer.parseInt(line);
				float denominator = total / 100.0F;
				int savedCounter = 1;
    			line = contact.readLine();
    			while(line != null) {
    				JSONObject jsonObject = new JSONObject(line);
    				
    				try {
    					String contactName = jsonObject.getString(Contacts.Phones.DISPLAY_NAME);
    					if(contactName != null) {
    						ContentValues values = new ContentValues();
    						values.put(Contacts.People.NAME, contactName);
    						// add it to the database
    						Uri newPerson = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
    						if(jsonObject.has(Contacts.Phones.NUMBER)) {
    							values.clear();
    							Uri mobileUri = Uri.withAppendedPath(newPerson, Contacts.People.Phones.CONTENT_DIRECTORY);
    							values.put(Contacts.Phones.NUMBER, jsonObject.getString(Contacts.Phones.NUMBER));
    							values.put(Contacts.Phones.TYPE, Contacts.Phones.TYPE_MOBILE);
    							getContentResolver().insert(mobileUri, values);
    						}
    						if(jsonObject.has("email")) {
    							// assign an email address for this person
    							values.clear();
    							values.put(Contacts.ContactMethods.PERSON_ID, newPerson.getLastPathSegment());
    							values.put(Contacts.ContactMethods.KIND, Contacts.KIND_EMAIL);
    							values.put(Contacts.ContactMethods.TYPE, Contacts.ContactMethods.TYPE_HOME);
    							values.put(Contacts.ContactMethods.DATA, jsonObject.getString("email"));
    							// insert the new email address in the database
    							Uri email = Uri.withAppendedPath(newPerson, Contacts.People.ContactMethods.CONTENT_DIRECTORY);
    							getContentResolver().insert(email, values);
    						}
    					}
    				}catch(JSONException ex) {
    					Log.e("RestoreContactTask", "", ex);
    				}
    				line = contact.readLine();
					mainProgressBar.setProgress((int) (savedCounter / denominator));
					status.append(getString(R.string.restore)).append(" ")
						  .append(String.valueOf(savedCounter))
						  .append("/")
						  .append(String.valueOf(total))
						  .append(" ").append(getString(R.string.contacts));
					publishProgress(status.toString());
					// Clear status buffer
					status.setLength(0);
					++savedCounter;
    			}
    			reportBuffer.append(getString(R.string.reportTextRestore)).append(" ").append(total).append(" ").append(getString(R.string.contacts)).append("\n");
				publishProgress("", reportBuffer.toString());
    			Log.d(TAG, "restore contact finished");
    		}catch(Exception e) {
    			Log.e(TAG, "failed restore contact", e);
    		}finally {
    			try {
    				contact.close();
    			}catch(Exception e) {}
    		}
		}

		private void restoreBookmark() {
			StringBuilder status = new StringBuilder();
			// CallHistory
			BufferedReader bookmarkFile = FileManager.readFile(FileManager.BOOKMARK_FILE);
			try {
				Log.d(TAG, "restoring bookmark...");
				String line = bookmarkFile.readLine();
				// get num of record
				int total = Integer.parseInt(line);
				float denominator = total / 100.0F;
				int savedCounter = 1;
				line = bookmarkFile.readLine();
				while(line != null) {
					JSONObject obj = new JSONObject(line);
					ContentValues val = new ContentValues();
					Iterator<String> keys = obj.keys();
					while(keys.hasNext()) {
						String key = keys.next();
						val.put(key, obj.getString(key));
					}
					val.put(Browser.BookmarkColumns.BOOKMARK, 1); // 1 = ?? unknown
					getContentResolver().insert(android.provider.Browser.BOOKMARKS_URI, val);
					line = bookmarkFile.readLine();
					mainProgressBar.setProgress((int) (savedCounter / denominator));
					status.append(getString(R.string.restore)).append(" ")
						  .append(String.valueOf(savedCounter))
						  .append("/")
						  .append(String.valueOf(total))
						  .append(" ").append(getString(R.string.bookmarks));
					publishProgress(status.toString());
					// Clear status buffer
					status.setLength(0);
					++savedCounter;
				}
				reportBuffer.append(getString(R.string.reportTextRestore)).append(" ").append(total).append(" ").append(getString(R.string.bookmarks)).append("\n");
				publishProgress("", reportBuffer.toString());
				Log.d(TAG, "restore bookmark finished");
			}catch(Exception e) {
				Log.e(TAG, "failed restore bookmark", e);
			}finally {
				try {
					bookmarkFile.close();
				}catch(Exception e) {}
			}
		}
		
		@Override
		protected String doInBackground(String... params) {
	        if((Boolean)restoreSetting.get("sms")) {
	        	restoreSMS();
	        }
	        if((Boolean)restoreSetting.get("callhistory")) {
	        	restoreCallHistory();
	        }
	        if((Boolean)restoreSetting.get("contact")) {
	        	restoreContacts();
	        }
	        if((Boolean)restoreSetting.get("bookmark")) {
	        	restoreBookmark();
	        }
			return "Restore Success";
		}
		
	}
}