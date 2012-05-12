package mobi.durian.tbackup;

import mobi.durian.tbackup.R;
import android.app.Dialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements OnPreferenceClickListener{
	static final int DIALOG_HELP = 2;
	static final int DIALOG_ABOUT = 3;
	
	Preference backupPref, restorePref, onlinePref, autoPref, aboutPref;
	
	public enum PrefEnum {
	    Backup, Restore, Online, Auto, About;  
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		//backupPref = findPreference(PrefEnum.Backup.toString());
		//restorePref = findPreference(PrefEnum.Restore.toString());
		onlinePref = findPreference(PrefEnum.Online.toString());
		autoPref = findPreference(PrefEnum.Auto.toString());
		aboutPref = findPreference(PrefEnum.About.toString());
		
		//backupPref.setOnPreferenceClickListener(this);
		//restorePref.setOnPreferenceClickListener(this);
		onlinePref.setOnPreferenceClickListener(this);
		autoPref.setOnPreferenceClickListener(this);
		aboutPref.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference pref) {
        switch(PrefEnum.valueOf(pref.getKey())) {
        	//case Backup: showDialog(DIALOG_HELP); break;
        	//case Restore: showDialog(DIALOG_HELP); break;
        	case Online: showDialog(DIALOG_HELP); break;
        	case Auto: showDialog(DIALOG_HELP); break;
        	case About: showDialog(DIALOG_ABOUT); break;
        }
	
		return false;
	}
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch(id) {
		case DIALOG_HELP:
			dialog = new HelpDialog(this);
			break;
		case DIALOG_ABOUT:
			dialog = new AboutDialogBuilder(this).create();
			break;
		}
		return dialog;
	}
}
