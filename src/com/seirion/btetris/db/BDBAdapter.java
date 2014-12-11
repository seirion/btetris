/**
 * Copyright (C) 2010 BalSoft (http://seirion.com)
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seirion.btetris.db;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BDBAdapter {
	private final Context _context;
	/** string id for the row id */
	public static final String KEY_ROWID = "id";
	/** string id for the name field */
	public static final String KEY_NAME = "name";
	/** string id for the date field */
	public static final String KEY_DATE = "date";
	/** string id for the score field */
	public static final String KEY_SCORE = "score";
	
	private static final String TAG = "BDBAdapter";
	private DatabaseHelper _DBHelper;
	private SQLiteDatabase _DB;
	
	private static final String DATABASE_NAME = "datum.db";
	private static final String DATABASE_TABLE = "data_table";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE =
		"create table " + DATABASE_TABLE + " (" + 
		KEY_ROWID + " integer primary key autoincrement, " +
		KEY_NAME + " text not null, " +
		KEY_DATE + " integer, " +
		KEY_SCORE + " integer);";
	
	private class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		public void onCreate(SQLiteDatabase db){
			db.execSQL(DATABASE_CREATE);
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			Log.w(TAG, "Upgrading db from version" + oldVersion + " to" +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
		
	}
	
	public BDBAdapter(Context context){
		this._context = context;
	}
	
	/**
	 * open the DB table.
	 * @return SQLiteOpenHelper handle.
	 * @throws SQLException
	 */
	public BDBAdapter open() throws SQLException {
		_DBHelper = new DatabaseHelper(_context);
		_DB = _DBHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * close the DB table
	 */
	public void close(){
		_DBHelper.close();
	}
	
	/**
	 * insert a new record.
	 * @param name the user name.
	 * @param date the date that the record is created.
	 * @param score the game score.
	 * @return
	 */
	public long insertRecord(String name, Date date, int score) {
		ContentValues initialValues = new ContentValues();
		long ms = date.getTime();
		
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_DATE, ms);
		initialValues.put(KEY_SCORE, score);
		
		return _DB.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * delete a record by ID
	 * @param rowID record's ID
	 * @return true if the request is successful.
	 */
	public boolean deleteRecord(long rowID){
		return _DB.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowID, null) > 0;
	}
	
	/**
	 * getting all records from the table. 
	 * @return the data.
	 */
	public Cursor getAllRecords() {
		return _DB.query(DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_NAME, KEY_DATE, KEY_SCORE},
				null, null, null, null, null);
	}
	
	/**
	 * getting all records from the table and sort as descending order. 
	 * @return the data.
	 */
	public Cursor getAllRecordsAndSortDescendant() {
		return _DB.query(DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_NAME, KEY_DATE, KEY_SCORE},
				null, null, null, null, KEY_SCORE + " desc");
	}
	
	/**
	 * getting a record by ID.
	 * @param rowID the record's ID
	 * @return the data.
	 * @throws SQLException
	 */
	public Cursor getRecordById(long rowID) throws SQLException{
		Cursor cursor =
			_DB.query(true, DATABASE_TABLE, new String[] {
					KEY_ROWID, KEY_NAME, KEY_DATE, KEY_SCORE}, 
					KEY_ROWID + "=" + rowID,
					null, null, null, null, null);
		if(cursor != null)
			cursor.moveToFirst();
		return cursor;
	}
		
	/**
	 * update existed data.
	 * @param rowID the ID (key).
	 * @param name new name.
	 * @param date new date that the record is created.
	 * @param score new score.
	 * @return
	 */
	public boolean updateRecord(long rowID, String name, Date date, int score){
		ContentValues args = new ContentValues();
		long ms = date.getTime();
		
		args.put(KEY_NAME, name);
		args.put(KEY_DATE, ms);
		args.put(KEY_SCORE, score);
		
		return _DB.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowID, null) > 0;
	}
}
