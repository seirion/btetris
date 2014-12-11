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

package com.seirion.btetris;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class BControl extends Object {
	/** */
	Context _context;
	/** current score */
	private int _score;
	/** current level */
	private int _level;
	/** current game speed in milli seconds */
	private int _speed;
	/** game control mode */
	private ControlMode _controlMode = ControlMode.BUTTON;
	/** rotation direct */
	private Direct _direct = Direct.CCW;
	
	public int getScore() {return _score;}
	public int getLevel() {return _level;}
	public int getSpeed() {return _speed;}
	public void setControlMode(ControlMode m) {_controlMode = m;}
	public ControlMode getControlMode() {return _controlMode;}
	public void setDirect(Direct d) {_direct = d;}
	public Direct getDirect() {return _direct;}
	
	/** speed table as a level */
	private final int [] SPEED_TABLE = {
		250, 220, 200, 180, 160, 140, 120, 100, 90, 80, 70, 60, 50	
	};
	
	/** define game control modes */
	public enum ControlMode {
		FLING,
		BUTTON
	};
	
	/** define rotation directs */
	public enum Direct {
		CW, 		/** clockwise rotation */
		CCW,		/** count-clockwise rotation */
	};
	
	/**
	 * creating BControl
	 */
	public BControl(Context context) {
		_context = context;
		loadRankData();
		loadLocalData();
		init();
	}
	
	/**
	 * initialize all data (when starting the game)
	 */
	public void init() {_score = 0;	_level = 0;	_speed = SPEED_TABLE[0];}
	
	/**
	 * load ranking data
	 */
	private void loadRankData() {
		
	}
	
	/**
	 * update ranking data
	 */
	public void updateRank() {
		
	}
	
	/**
	 * load preference settings
	 */
	public void loadLocalData() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(_context);
		String str = pref.getString("control_mode", "");
		_controlMode = (str.equals("1") ? 
				ControlMode.BUTTON : ControlMode.FLING);
		
		str = pref.getString("direct", "");
		_direct = (str.equals("1") ? Direct.CCW : Direct.CW);
	}
	
	/**
	 * add the score as the number of eliminated line
	 * @param removed the number of eliminated line
	 * @return the game speed
	 */
	public int addScore(int eliminated) {
		_score += eliminated*eliminated*10;
		_level = _score/100;
		if (_level < SPEED_TABLE.length) _speed = SPEED_TABLE[_level];
		else _speed = SPEED_TABLE[SPEED_TABLE.length - 1];
		return _speed;
	}
}