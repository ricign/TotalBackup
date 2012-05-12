package mobi.durian.tbackup;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import mobi.durian.tbackup.R;
import mobi.durian.tbackup.UserDataDialog.UserDataDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Browser;
import android.provider.CallLog;
import android.provider.Contacts;
import android.provider.Contacts.People;
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

public class BackupActivity extends Activity implements OnClickListener, UserDataDialogListener {
	static final int DIALOG_USER_DATA = 0;
	static final int DIALOG_ABOUT = 1;
	static final int DIALOG_HELP = 2;
	static final int DIALOG_SDCARD_ERROR = 3;
	
	TextView statusText, reportText;
	Button backupButton, restoreButton;
	ScrollView reportScrollView;
	Cursor contacts, calls, smses, bookmarks;
	
    private Columns call = ColumnsFactory.calls();
    private Columns sms = ColumnsFactory.messages();
    private Columns contact = TBackupColumsFactory.contacts();
    private Columns bookmark = TBackupColumsFactory.bookmarks();
    
    ProgressBar mainProgressBar;
	int maxCounter, savedCounter;
	StringBuffer reportBuffer;

	private Properties backupSetting;
	
    private static final String TAG = "BackupActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup);
        
        statusText = (TextView) findViewById(R.id.statusText);
        backupButton = (Button) findViewById(R.id.backupButton);
        mainProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);
        reportText = (TextView) findViewById(R.id.reportText);
        reportScrollView = (ScrollView) findViewById(R.id.reportScrollView);
        
        reportBuffer = new StringBuffer();
        
        //TODO: load setting
        // backupSetting....
        backupSetting = new Properties();
        backupSetting.put("sms", false);
        backupSetting.put("contact", false);
        backupSetting.put("callhistory", false);
        
        backupButton.setOnClickListener(this);
    }
    
    public void onClose(boolean save) {
    	if(save) {
    		// TODO: save backup setting
    	}
    	try {
    	// Check external memory
			FileManager.checkSdCard();
		// run backup
			new BackingUp().execute(null);
    	} catch (IOException e) {
    		Log.e(TAG,"Error accessing SD Card");
    		showDialog(DIALOG_SDCARD_ERROR);
        }
    }
    
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch(id) {
		case DIALOG_USER_DATA:
			dialog = new UserDataDialog(this, this, backupSetting);
			break;
		case DIALOG_ABOUT:
			dialog = new AboutDialogBuilder(this).create();
			break;
		}
		return dialog;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backupButton:
    		showDialog(DIALOG_USER_DATA);
			break;
		}
	}
	
	@Override
	/*
	 * Menu About
	 */
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
			showDialog(DIALOG_ABOUT);
			break;
		}
		return true;
	}

	class BackingUp extends AsyncTask<String, String, String> {
		ContentResolver contentResolver = getContentResolver();

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

		@Override
		protected String doInBackground(String... params) {
			StringBuilder status = new StringBuilder();
			String [] strArr = new String[2];

			float denominator;
			
		// SMS
	        if((Boolean)backupSetting.get("sms")) {
	        	Log.d(TAG, "backup sms");
		        smses 	= contentResolver.query(Uri.parse("content://sms/"),
                			null, null, null, null);
		        
				PrintStream smsFile = FileManager.writeFile(FileManager.SMS_FILE);
		        denominator = smses.getCount() / 100.0F;
				maxCounter = smses.getCount();
				savedCounter = 0;
				// save total record
				smsFile.println(maxCounter);
				while(smses.moveToNext()) {
					smsFile.println(sms.cursorToJSON(smses));
					savedCounter++;
					mainProgressBar.setProgress((int) (savedCounter / denominator));
					status.append(getString(R.string.savingSms, savedCounter, maxCounter));
					publishProgress(status.toString());
					// Clear status buffer
					status.setLength(0);
				}
				reportBuffer.append(getString(R.string.successSaveSms, maxCounter)).append("\n");
				strArr[1] = reportBuffer.toString();
				publishProgress(strArr);
				
				smses.close();
				smsFile.close();
	        }
	        
	     // Calls History
	        if((Boolean)backupSetting.get("callhistory")) {
	        	Log.d(TAG, "backup call");
		        calls 	= contentResolver.query(CallLog.Calls.CONTENT_URI,
        					null, null, null, null);
		        
				PrintStream callHistoryFile = FileManager.writeFile(FileManager.CALLHISTORY_FILE);
				denominator = calls.getCount() / 100.0F;
				maxCounter = calls.getCount();
				savedCounter = 0;
				// save total record
				callHistoryFile.println(maxCounter);
				while(calls.moveToNext()) {
					callHistoryFile.println(call.cursorToJSON(calls));
					savedCounter++;
					mainProgressBar.setProgress((int) (savedCounter / denominator));
					status.append(getString(R.string.savingCalls, savedCounter, maxCounter));
					publishProgress(status.toString());
					status.setLength(0);
				}
				reportBuffer.append(getString(R.string.successSaveCalls, maxCounter)).append("\n");
				strArr[1] = reportBuffer.toString();
				publishProgress(strArr);
				
				calls.close();
				callHistoryFile.close();
	        }
	        
	       // Contacts
	        if((Boolean)backupSetting.get("contact")) {
	        	Log.d(TAG, "backup contact");
				String[] contactsProjection = new String[] { 
						Contacts.Phones._ID,
						Contacts.Phones.DISPLAY_NAME,
						Contacts.Phones.NUMBER
				};
				contacts = contentResolver.query(Contacts.People.CONTENT_URI,
							contactsProjection, null, null, null);
	        	
		        // Contacts
				PrintStream contactFile = FileManager.writeFile(FileManager.CONTACT_FILE);
				denominator = contacts.getCount() / 100.0F;
				maxCounter = contacts.getCount();
				savedCounter = 0;
				// save total record
				contactFile.println(maxCounter);
				while(contacts.moveToNext()) {
					String contactId = contacts.getString(contacts.getColumnIndex(People._ID));
					JSONObject json = contact.cursorToJSON(contacts);
					
				// if phone number empty
					if(!json.has(Contacts.Phones.NUMBER) || json.isNull(Contacts.Phones.NUMBER)) {
						try {
							Cursor c = contentResolver.query(Contacts.Phones.CONTENT_URI, null, Contacts.ContactMethods.PERSON_ID + "=?", new String[]{contactId}, null);
							if(c.moveToNext()) {
								json.put(Contacts.Phones.NUMBER, c.getString(c.getColumnIndex(Contacts.Phones.NUMBER)));
							}
							c.close();
						}catch(JSONException e) {
							
						}
						Log.w(TAG, "Phone number empty");
					}
					
				// Query email address
					Cursor emailCur = contentResolver.query( 
							Contacts.ContactMethods.CONTENT_EMAIL_URI, null,
							Contacts.ContactMethods.PERSON_ID + " = ?", new String[]{contactId}, null); 
					while (emailCur.moveToNext()) { 
							String email = emailCur.getString(emailCur.getColumnIndex(Contacts.ContactMethods.DATA));
							try {
								json.put("email", email);
							} catch (JSONException e) {
								e.printStackTrace();
							}
					} 
					emailCur.close();
					
					contactFile.println(json);
					savedCounter++;
					mainProgressBar.setProgress((int) (savedCounter / denominator));
					status.append(getString(R.string.savingContacts, savedCounter, maxCounter));
					publishProgress(status.toString());
					status.setLength(0);
				}
				reportBuffer.append(getString(R.string.successSaveContacts, maxCounter)).append("\n");
				strArr[1] = reportBuffer.toString();
				publishProgress(strArr);
				
				contacts.close();
				contactFile.close();
	        }
	        
		   // Bookmarks
	        if((Boolean)backupSetting.get("bookmark")) {
	        	Log.d(TAG, "backup bookmark");
		        String[] bookmarksProjection = new String[] {
           				Browser.BookmarkColumns.TITLE, 
           				Browser.BookmarkColumns.URL
		        };
		        bookmarks 	= contentResolver.query(android.provider.Browser.BOOKMARKS_URI,
        					bookmarksProjection, Browser.BookmarkColumns.BOOKMARK + "=?", new String[]{"1"}, null);
        
				PrintStream bookmarkFile = FileManager.writeFile(FileManager.BOOKMARK_FILE);
				denominator = bookmarks.getCount() / 100.0F;
				maxCounter = bookmarks.getCount();
				savedCounter = 0;
				// save total record
				bookmarkFile.println(maxCounter);
				while(bookmarks.moveToNext()) {
					bookmarkFile.println(bookmark.cursorToJSON(bookmarks));
					savedCounter++;
					mainProgressBar.setProgress((int) (savedCounter / denominator));
					status.append(getString(R.string.savingBookmarks, savedCounter, maxCounter));
					publishProgress(status.toString());
					status.setLength(0);
				}
				reportBuffer.append(getString(R.string.successSaveBookmarks, maxCounter)).append("\n");
				publishProgress(strArr);
				
				bookmarks.close();
				bookmarkFile.close();
	        }
	      
			return "Backup Done";
		}
		
	}
}