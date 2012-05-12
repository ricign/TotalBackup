package mobi.durian.tbackup;

import mobi.durian.tbackup.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.widget.TextView;

public class AboutDialogBuilder {
	Context context;
	
	protected AboutDialogBuilder(Context context) {
		this.context = context;
	}
	
	public Dialog create() {
		String aboutTitle = context.getString(R.string.aboutTitle, context.getString(R.string.app_name));
		String versionString = context.getString(R.string.aboutVersion, "1.0");
		String aboutText = context.getString(R.string.aboutText);

		// Set up the TextView
		final TextView message = new TextView(context);
		// To make links clickable
		final SpannableString s = new SpannableString(aboutText);

		// Set some padding
		message.setPadding(5, 5, 5, 5);
		// Set up the final string
		message.setText(versionString + "\n\n" + s);
		// Now linkify the text
		Linkify.addLinks(message, Linkify.ALL);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(aboutTitle)
				.setView(message)
		       .setCancelable(false)
		       .setPositiveButton(context.getString(R.string.ok), null);

		return builder.create();
	}
}
