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

import android.os.Handler;

public abstract class BTimer {
	/** handler for time schedule */
	private Handler _handler;
	/** timer cycle in milli seconds */
	private long _cycle;
	/** runnable looper */
	private TimerRunnable _runnable;
	/** */
	private boolean _running = false;
	
	class TimerRunnable implements Runnable {
		public void run() {
			if (_running) {
				looper();
				_handler.postDelayed(this, _cycle);
			}
		}
	}
	
	public BTimer(long cycle) {
		_handler = new Handler();
		_runnable = new TimerRunnable();
		_cycle = cycle;
	}
	
	public void resetTimer(int cycle) {_cycle = cycle;}
	
	public void start() {
		_running = true;
		_handler.postDelayed(_runnable, _cycle);
	}
	
	public  void stop() {
		_running = false;
	}
	abstract protected void looper();
}