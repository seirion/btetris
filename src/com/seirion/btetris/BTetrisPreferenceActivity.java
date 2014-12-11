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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class BTetrisPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private SharedPreferences _preference;
	/** preferences - control mode */
	private int _controlMode;
	/** preferences - rotation direct */
	private int _direct;
	
	public static final String KEY_CONTROL_MODE = "control_mode";
	public static final String KEY_DIRECT = "direct";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		_preference = PreferenceManager.getDefaultSharedPreferences(this);
		
		Intent intent = getIntent();
		_controlMode = intent.getIntExtra(KEY_CONTROL_MODE, 1);
		_direct = intent.getIntExtra(KEY_DIRECT, 1);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		_preference.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		_preference.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(KEY_CONTROL_MODE)) {
			String s = sharedPreferences.getString(key, "");
			if (s.equals("0")) _controlMode = 0;
			else _controlMode = 1;
		}
		else if (key.equals(KEY_DIRECT)) {
			String s = sharedPreferences.getString(key, "");
			if (s.equals("0")) _direct = 0;
			else _direct = 1;
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent i = getIntent();
		i.putExtra(KEY_CONTROL_MODE, _controlMode);
		i.putExtra(KEY_DIRECT, _direct);
		setResult(RESULT_OK, i);
		finish();
	}
}