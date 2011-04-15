package uk.co.kalgan.app.notdo;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class NotDoList extends Activity {
	private ArrayList<NotDoItem> notDoItems;
	private EditText myEditText;
	private ListView myListView;
	private NotDoItemAdapter aa;
	private NotDoDBAdapter notDoDBAdapter;
	
	private static final String TEXT_ENTRY_KEY = "TEXT_ENTRY_KEY";
	private static final String ADDING_ITEM_KEY = "ADDING_ITEM_KEY"; 
	private static final String SELECTED_INDEX_KEY ="SELECTED_INDEX_KEY";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inflate your View
        setContentView(R.layout.main);
        
        // Get references to UI widgets
        myListView = (ListView)findViewById(R.id.myListView);
        myEditText = (EditText)findViewById(R.id.myEditText);
        
        // Create the array list of not do items
        notDoItems = new ArrayList<NotDoItem>();
        // Create the array adapter to bind the array to the listview
        int resID = R.layout.notdolist_item;
        aa = new NotDoItemAdapter(this, resID, notDoItems);
        // Bind the array adapter to the listview
        myListView.setAdapter(aa);
        
        myEditText.setOnKeyListener(new OnKeyListener() {
        	public boolean onKey(View v, int keyCode, KeyEvent event) {
        		if (event.getAction() == KeyEvent.ACTION_DOWN)
        			if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
        				NotDoItem newItem = new NotDoItem(myEditText.getText().toString());
        				notDoDBAdapter.insertTask(newItem);
        				updateArray();
        				aa.notifyDataSetChanged();
        				myEditText.setText("");
        				cancelAdd();
        				return true;
        			}
        		return false;
        	}
        });
        
        registerForContextMenu(myListView);
        restoreUIState();
        
        notDoDBAdapter = new NotDoDBAdapter(this);
        notDoDBAdapter.open();
        
        populateNotDoList();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	notDoDBAdapter.close();
    }
    
    static final private int ADD_NEW_NOTDO = Menu.FIRST;
    static final private int REMOVE_NOTDO = Menu.FIRST + 1;
    private boolean addingNew = false;
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
    	
    	int index = myListView.getSelectedItemPosition();
    	String removeTitle = getString(addingNew ? R.string.cancel
    											: R.string.remove);
    	
    	MenuItem removeItem = menu.findItem(REMOVE_NOTDO);
    	removeItem.setTitle(removeTitle);
    	removeItem.setVisible(addingNew || index > -1);
    	
    	return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	// Create and add menu items.
    	MenuItem itemAdd = menu.add(0, ADD_NEW_NOTDO, Menu.NONE,
    			R.string.add_new);
    	MenuItem itemRem = menu.add(0, REMOVE_NOTDO, Menu.NONE,
    			R.string.remove);
    	
    	// Assign Icons
    	itemAdd.setIcon(R.drawable.add_notdo);
    	itemRem.setIcon(R.drawable.del_notdo);
    	
    	// Assign Shortcuts for the menu items.
    	itemAdd.setShortcut('0', 'a');
    	itemRem.setShortcut('1', 'r');
    	
    	return true;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenu.ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	
    	menu.setHeaderTitle("Selected Not Do Item");
    	menu.add(0, REMOVE_NOTDO, Menu.NONE, R.string.remove);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
    	int index = myListView.getSelectedItemPosition();
    	
    	switch (item.getItemId()) {
    	case (REMOVE_NOTDO): {
    		if (addingNew) {
    			cancelAdd();
    		}
    		else {
    			removeItem(index);
    		}
    		return true;
    	}
    	case (ADD_NEW_NOTDO): {
    		addNewItem();
    		return true;
    	}
    	}
    	
    	return false;
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	super.onContextItemSelected(item);
    	
    	switch (item.getItemId()) {
    	case (REMOVE_NOTDO): {
    		AdapterView.AdapterContextMenuInfo menuInfo;
    		menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    		int index = menuInfo.position;
    		
    		removeItem(index);
    		return true;
    	}
    	}
    	return false;
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	// Get the activity preferences object
    	SharedPreferences uiState = getPreferences(Activity.MODE_PRIVATE);
    	// Get preferences editor
    	SharedPreferences.Editor editor = uiState.edit();
    	
    	// Add the UI state preference values
    	editor.putString(TEXT_ENTRY_KEY, myEditText.getText().toString());
    	editor.putBoolean(ADDING_ITEM_KEY, addingNew);
    	
    	editor.commit();    	
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	savedInstanceState.putInt(SELECTED_INDEX_KEY, myListView.getSelectedItemPosition());
    	
    	super.onSaveInstanceState(savedInstanceState);
    }
    
    public void onRestoreInstance(Bundle savedInstanceState) {
    	int pos = -1;
    	
    	if (savedInstanceState != null)
    		if (savedInstanceState.containsKey(SELECTED_INDEX_KEY))
    			pos = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);
    	
    	myListView.setSelection(pos);
    }
    
    private void restoreUIState() {
    	// Get the activity preferences
    	SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
    	
    	// Read the UI state values
    	String text = settings.getString(TEXT_ENTRY_KEY, "");
    	Boolean adding = settings.getBoolean(ADDING_ITEM_KEY, false);
    	
    	// Restore the UI to previous state
    	if (adding) {
    		addNewItem();
    		myEditText.setText(text);
    	}
    }
    
    private void cancelAdd() {
    	addingNew = false;
    	myEditText.setVisibility(View.GONE);
    }
    
    private void addNewItem() {
    	addingNew = true;
    	myEditText.setVisibility(View.VISIBLE);
    	myEditText.requestFocus();
    }
    
    private void removeItem(int _index) {
    	notDoDBAdapter.removeTask(notDoItems.size()-_index);
    	updateArray();
    }
    
    Cursor notDoListCursor;
    
    void populateNotDoList() {
    	// Get all the NotDo list items from the database
    	notDoListCursor = notDoDBAdapter.getAllNotDoItemsCursor();
    	startManagingCursor(notDoListCursor);
    	
    	updateArray();
    }
    
    private void updateArray() {
    	notDoListCursor.requery();
    	
    	notDoItems.clear();
    	
    	if (notDoListCursor.moveToFirst())
    		do {
    			String task = notDoListCursor.getString(notDoListCursor.getColumnIndex(NotDoDBAdapter.KEY_TASK));
    			long created = notDoListCursor.getLong(notDoListCursor.getColumnIndex(NotDoDBAdapter.KEY_CREATION_DATE));
    			
    			NotDoItem newItem = new NotDoItem(task, new Date(created));
    			notDoItems.add(0, newItem);
    		} while (notDoListCursor.moveToNext());
    	
    	aa.notifyDataSetChanged();
    }
} 