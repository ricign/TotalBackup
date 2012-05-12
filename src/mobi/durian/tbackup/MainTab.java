package mobi.durian.tbackup;

import mobi.durian.tbackup.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainTab extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

		TabSpec backupTabSpec = tabHost.newTabSpec("tid1");
		TabSpec restoreTabSpec = tabHost.newTabSpec("tid2");
		TabSpec settingsTabSpec = tabHost.newTabSpec("tid3");

		backupTabSpec.setIndicator("Backup",res.getDrawable(R.drawable.ic_tab_backup)).setContent(new Intent(this,BackupActivity.class));
		restoreTabSpec.setIndicator("Restore",res.getDrawable(R.drawable.ic_tab_restore)).setContent(new Intent(this,RestoreActivity.class));
		settingsTabSpec.setIndicator("Settings",res.getDrawable(R.drawable.ic_tab_settings)).setContent(new Intent(this,SettingsActivity.class));

		tabHost.addTab(backupTabSpec);
		tabHost.addTab(restoreTabSpec);
		tabHost.addTab(settingsTabSpec);
	}
}
