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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BScoreBoard extends LinearLayout {
	/** current value*/
	private int _value;
	
	/** image resources */
	private final int [] _imageResource = {
		R.drawable.n_0,
		R.drawable.n_1,
		R.drawable.n_2,
		R.drawable.n_3,
		R.drawable.n_4,
		R.drawable.n_5,
		R.drawable.n_6,
		R.drawable.n_7,
		R.drawable.n_8,
		R.drawable.n_9
	};
	
	/** image view resources */
	private final int [] _numberViewResource = {
		R.id.IDC_SCORE_0,
		R.id.IDC_SCORE_1,
		R.id.IDC_SCORE_2,
		R.id.IDC_SCORE_3,
		R.id.IDC_SCORE_4
	};
	
	/** image views */
	private ImageView [] _numberView;
	
	public BScoreBoard(Context context) {
		super(context);
		makeView();
		init();
	}
	
	public BScoreBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		makeView();
		init();
	}
	
	/**
	 * initialize view widgets
	 */
	protected void makeView() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);
		View v = li.inflate(R.layout.score_board, this, false);
		addView(v);
		
		int i, size;
		size = _numberViewResource.length;
		_numberView = new ImageView [size];
		for (i = 0; i < size; i++) {
			_numberView[i] = (ImageView)findViewById(_numberViewResource[i]);
		}
		
		setFocusable(false);
		setFocusableInTouchMode(false);
	}
	
	/**
	 * initialize the value into '0'
	 */
	public void init() {
		_value = 0;
		updateData();
	}
	
	/**
	 * setting the value
	 * 
	 * @param value value to be display
	 */
	public void setValue(int value) {
		_value = value;
		updateData();
	}
	
	/**
	 * redraw score board
	 */
	protected void updateData() {
		int i, size ,value;
		value = _value;
		size = _numberViewResource.length;
		for (i = 0; i < size; i++) {
			if (value > 0 )
				_numberView[i].setImageResource(_imageResource[value%10]);
			else 
				_numberView[i].setImageResource(0);
			value /= 10;
		}
		
		ImageView v = (ImageView)findViewById(R.id.IDC_COMMA);
		if (_value > 1000) v.setImageResource(R.drawable.comma);
		else v.setImageResource(0);

	}
}