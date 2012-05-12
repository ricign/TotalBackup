/**
 *
 * RestoreTask.java
 * May 18, 2011 - 10:29:06 AM
 *
 */
package mobi.durian.tbackup;

import java.io.BufferedReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.Contacts;
import android.util.Log;

public class RestoreContactTask {
	private static final String TAG = "RestoreContactTask";
	private ContentResolver contentResolver;
	
	public RestoreContactTask(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}
	
	private void insertRow(JSONObject jsonObject) {
		
	}
	
	public void run() {
		
	}
}
