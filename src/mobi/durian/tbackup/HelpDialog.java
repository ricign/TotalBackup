package mobi.durian.tbackup;

import mobi.durian.tbackup.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class HelpDialog extends Dialog implements OnClickListener{
	
	private TextView helpText;
	
	public HelpDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.help_dialog);
		
		helpText = (TextView) findViewById(R.id.helpText);
		helpText.setOnClickListener(this);
		this.setCancelable(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.helpText:
			dismiss();
			break;
		}
	}

}
