package no.vlh.quicknotes;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

public class QuicknoteEditActivity extends Activity {

	private EditText mBodyText;
	private Long mRowId;
	private NotesDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.note_edit);
		setTitle(R.string.edit_note);

		mBodyText = (EditText) findViewById(R.id.body);

		mRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
					: null;
		}

		populateFields();

		mBodyText.setOnEditorActionListener(new TextView.OnEditorActionListener()  {

			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				setResult(RESULT_OK);
				finish();
				return true;
			}
		});

	}

	private void populateFields() {
		if (mRowId != null) {
			Cursor note = mDbHelper.fetchNote(mRowId);
			startManagingCursor(note);
			mBodyText.setText(note.getString(note
					.getColumnIndexOrThrow(NotesDbAdapter.KEY_NOTE)));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String note = mBodyText.getText().toString();

		if (note != null && (!note.equals("")) )  {
			if (mRowId == null) {
				long id = mDbHelper.createNote(note);
				if (id > 0) {
					mRowId = id;
				}
			} else {
				mDbHelper.updateNote(mRowId, note);
			}
		}
	}
}
