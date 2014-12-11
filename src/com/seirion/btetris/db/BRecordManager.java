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

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class BRecordManager {
	private final Context _context;
	/** the number of record to be read */
	private int _recordNum = 0;
	/** if data has been loaded */
	private boolean _isLoaded = false;
	/** the data to be saved in DB */
	private Rank [] _rank = new Rank[RANK_NUM];
	
	public int getRecordNum() {return _recordNum;}
	
	public BRecordManager(Context context) {
		_context = context;
		int i;
		for (i = 0; i < RANK_NUM; i++) {
			_rank[i] = new Rank();
		}
	}
	
	/**
	 * load records from the database
	 */
	public Rank[] loadRecord() {
		_recordNum = 0;
		BDBAdapter adapter = new BDBAdapter(_context);
		try {
			adapter.open();
			Cursor cursor = adapter.getAllRecordsAndSortDescendant();
			if (cursor.moveToFirst()) {
				do {
					readRecord(cursor);
				} while (cursor.moveToNext());
			}
			adapter.close();
		}
		catch (Exception e) {
			Log.e("Load failed", "" + e);
		}
		
		_isLoaded = true;
		return _rank;
	}
	
	/**
	 * read a record from database
	 */
	protected void readRecord(Cursor cursor) {
		int index;
		index = cursor.getColumnIndex(BDBAdapter.KEY_ROWID);
		_rank[_recordNum]._id = cursor.getInt(index);
		index = cursor.getColumnIndex(BDBAdapter.KEY_NAME);
		_rank[_recordNum]._name = cursor.getString(index);
		index = cursor.getColumnIndex(BDBAdapter.KEY_DATE);
		_rank[_recordNum]._date.setTime(cursor.getLong(index));
		index = cursor.getColumnIndex(BDBAdapter.KEY_SCORE);
		_rank[_recordNum]._score = cursor.getInt(index);
		_recordNum++;
	}
	
	/**
	 * insert a new record.
	 * @param name the user name
	 * @param score the game score
	 * @return true if insertion is successful, otherwise return false.
	 */
	public boolean insertRecord(String name, int score) {
		BDBAdapter adapter = new BDBAdapter(_context);
		try {
			if (!_isLoaded) {
				loadRecord();
			}
			
			adapter.open();
			if (_recordNum >= RANK_NUM) {
				adapter.deleteRecord(_rank[RANK_NUM-1]._id);
			}
			adapter.insertRecord(name, new Date(), score);
			adapter.close();
		}
		catch (Exception e) {
			Log.e("Insert failed", "" + e);
			return false;
		}
		return true;
	}

	/**
	 * return the rank (zero-based)
	 * @param score the current score
	 * @return the rank (zero-based)
	 */
	public int getRank(int score) {
		if (!_isLoaded) {
			loadRecord();
		}
		int i = _recordNum;
		while (i > 0) {
			if (_rank[i-1]._score >= score) return i;
			else i--;
		}
		return i;
	}
	
	/** the number of ranking data */
	static final public int RANK_NUM = 5;
	
	/**
	 * the ranking data
	 */
	public class Rank extends Object implements Comparable<Rank> {
		public int _id = 0;
		public String _name = new String();
		public Date _date = new Date();
		public int _score = 0;
		public Rank() {}

		@Override
		public int compareTo(Rank r) {
			if (_score != r._score) {
				return _score - r._score;
			}
			else {
				return -(_date.compareTo(r._date));
			}
		}
		
		public String toString() {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd hh:mm"); 
			String dateString = formatter.format(_date);
			
			String s = String.format("%s (%s)", _name, dateString);
			return s.toString();
		}
	}
}