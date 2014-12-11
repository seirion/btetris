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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BRecordActivity extends Activity {
	/** user's current score */
	private int _rank = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		_rank = extras.getInt("rank");
		
		setContentView(R.layout.record);
	}

	/**
	 * return current score
	 */
	public int getRank() {return _rank;}
}
