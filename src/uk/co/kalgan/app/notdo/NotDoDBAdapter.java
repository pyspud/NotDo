package uk.co.kalgan.app.notdo;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class NotDoDBAdapter {
	private static final String DATABASE_NAME = "notdoList.db";
	private static final String DATABASE_TABLE = "notDoItems";
	private static final int DATABASE_VERSION = 1;
	
	public static final String KEY_ID = "_id";
	public static final String KEY_TASK = "task";
	public static final String KEY_CREATION_DATE = "creation_date";
	
	private SQLiteDatabase db;
	private final Context context;
	private notDoDBOpenHelper dbHelper;
	
	public NotDoDBAdapter(Context _context) {
		this.context = _context;
		dbHelper = new notDoDBOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void open() throws SQLiteException {
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException e) {
			db = dbHelper.getReadableDatabase();
		}
	}
	
	public void close() {
		db.close();
	}
	
	// Insert a task
	public long insertTask(NotDoItem _task) {
		// Create a new row of values
		ContentValues newTaskValues = new ContentValues();
		
		// Assign Values for each row
		newTaskValues.put(KEY_TASK, _task.getTask());
		newTaskValues.put(KEY_CREATION_DATE, _task.getCreated().getDate());
		
		// Insert row
		return db.insert(DATABASE_TABLE, null, newTaskValues);
	}
	
	public boolean removeTask(long _rowIndex) {
		return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0; 
	}
	
	public boolean updateTask(long _rowIndex, String _task) {
		ContentValues newValue = new ContentValues();
		newValue.put(KEY_TASK, _task);
		
		return db.update(DATABASE_TABLE, newValue, KEY_ID + "=" + _rowIndex, null) > 0;
	}
	
	public Cursor getAllNotDoItemsCursor() {
		return db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_TASK, KEY_CREATION_DATE},
				null, null, null, null, null);
	}
	
	public Cursor setCursorToNotDoItem(long _rowIndex) throws SQLiteException {
		Cursor result = db.query(true, DATABASE_TABLE,
				new String[] {KEY_ID, KEY_TASK, KEY_CREATION_DATE},
				KEY_ID + "=" + _rowIndex, null, null, null, null, null);
		
		if ((result.getCount() == 0) || !result.moveToFirst()) {
			throw new SQLiteException("No items found for row " + _rowIndex);
		}
		
		return result;
	}
	
	public NotDoItem getNotDoItem(long _rowIndex) throws SQLiteException {
		Cursor cursor = db.query(true, DATABASE_TABLE,
				new String[] {KEY_ID, KEY_TASK, KEY_CREATION_DATE},
				KEY_ID + "=" + _rowIndex, null, null, null, null, null);
		
		if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
			throw new SQLiteException("No Not Do item found at " + _rowIndex);
		}
		
		String task = cursor.getString(cursor.getColumnIndex(KEY_TASK));
		long created =  cursor.getLong(cursor.getColumnIndex(KEY_CREATION_DATE));
		
		return new NotDoItem(task, new Date(created));
	}
	
	private static class notDoDBOpenHelper extends SQLiteOpenHelper {
		
		public notDoDBOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		
		private static final String DATABASE_CREATE = "create table " +
		DATABASE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " +
		KEY_TASK + " text not null, " + KEY_CREATION_DATE + " long);";
		
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade( SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion +
					" to " + _newVersion + " ,which will destroy all old data.");
			// Drop old old table
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			// Create new one
			onCreate(_db);
		}
		
	}

}
