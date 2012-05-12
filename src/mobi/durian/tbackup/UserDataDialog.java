package mobi.durian.tbackup;

import java.util.Properties;

import mobi.durian.tbackup.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

public class UserDataDialog extends Dialog implements OnClickListener{
	public interface UserDataDialogListener {
		public void onClose(boolean save);
	}
	
	private String title;
	private Button okButton, cancelButton;
	private UserDataDialogListener listener;
	private Properties setting;
	
	public UserDataDialog(Context context, UserDataDialogListener listener, Properties setting) {
		super(context);
		this.listener = listener;
		this.setting = setting;
		title = "Choose what to backup";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.userdata_dialog);
		
		okButton = (Button) findViewById(R.id.ok);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		setting.put("contact", ((CheckBox)findViewById(R.id.contactsCheckBox)).isChecked());
		setting.put("sms", ((CheckBox)findViewById(R.id.smsCheckBox)).isChecked());
		setting.put("callhistory", ((CheckBox)findViewById(R.id.callsCheckBox)).isChecked());
		setting.put("bookmark", ((CheckBox)findViewById(R.id.bookmarksCheckBox)).isChecked());

		switch (v.getId()) {
		case R.id.ok:
			// send to caller
			listener.onClose(false);
			dismiss();
			break;
			/*
		case R.id.saveButton:
			// send to caller
			listener.onClose(true);
			*/
		case R.id.cancelButton:
			dismiss();
			break;
		}
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
