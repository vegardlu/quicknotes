package no.vlh.quicknotes;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListQuicknotesActivity extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int EDIT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int ABOUT_ID = Menu.FIRST + 2;

    private NotesDbAdapter mDbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }

    private void fillData() {
    	Cursor mNotesCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(mNotesCursor);

        String[] from = new String[]{NotesDbAdapter.KEY_NOTE};

        int[] to = new int[]{R.id.line_text1};

        SimpleCursorAdapter notes = 
            new SimpleCursorAdapter(this, R.layout.notes_row, mNotesCursor, from, to);
        setListAdapter(notes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ABOUT_ID, 0, R.string.menu_about);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case ABOUT_ID:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.options);
        menu.add(0, EDIT_ID, 0, R.string.menu_edit);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case DELETE_ID:
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
            case EDIT_ID:
            	Intent i = new Intent(this, QuicknoteEditActivity.class);
                i.putExtra(NotesDbAdapter.KEY_ROWID, info.id);
                startActivityForResult(i, ACTIVITY_EDIT);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, QuicknoteEditActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, QuicknoteEditActivity.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    
	public void onClick(View v) {
		if (v.getId() == R.id.add_note)   {
			createNote();
		}
	}
	
}
