package no.vlh.quicknotes;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		setTitle(R.string.about);
	}
}
