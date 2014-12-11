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

package com.seirion.btetris.util;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class BVirtualKeyboardUtil {
	
	private BVirtualKeyboardUtil() {}
	
	/**
	 * show virtual keyboard.
	 * @param activity the activity to show the keyboard.
	 * @param input edit which gets focus  
	 */
	public static void showInputMethod(final Activity activity, final EditText input){
		TimerTask myTask = new TimerTask(){
			public void run(){
				InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
		    }
		};
		
		Timer timer = new Timer();
		timer.schedule(myTask, 100);
	}
	
	/**
	 * hide virtual keyboard.
	 * @param activity the activity.
	 */
	public static void hideInputMethod(final Activity activity){
		
		TimerTask myTask = new TimerTask(){
			public void run(){
				InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(myTask, 100);
	}
}