package uk.co.kalgan.app.notdo;

import java.util.ArrayList;

import android.app.Activity;
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
        				notDoItems.add(0, newItem);
        				aa.notifyDataSetChanged();
        				myEditText.setText("");
        				cancelAdd();
        				return true;
        			}
        		return false;
        	}
        });
        
        registerForContextMenu(myListView);
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
    	notDoItems.remove(_index);
    	aa.notifyDataSetChanged();
    }
}