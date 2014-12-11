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

import com.seirion.btetris.db.BRecordManager;
import com.seirion.btetris.util.BTimer;
import com.seirion.btetris.util.BVirtualKeyboardUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class BTetrisActivity extends Activity implements View.OnTouchListener {
	
	/** game board */
	private BBoard _board;
	/** next block board */
	private BNextBlockBoard _nextBlockBoard;
	/** score board to display the game score */
	private BScoreBoard _scoreBoard;
	/** game is running */
	private boolean _running = false;
	/** x position when ACTION_DOWN */
	private float _xDownPosition;
	/** y position when ACTION_DOWN */
	private float _yDownPosition;
	/** keeping game status for control */
	private BControl _control;
	/** timer */
	private BTimer _timer = null;
	/** moving left margin when it's button mode to control */
	private int _leftMargin;
	/** rotation margin when it's button mode to control */
	private int _rotationMargin;
	/** for debugging */
	private TextView _test;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		_control = new BControl(this);
		_board = (BBoard)findViewById(R.id.IDV_MAIN_BOARD);
		_board.setDirect(_control.getDirect() == BControl.Direct.CCW ? true : false);
		FrameLayout fl = (FrameLayout)findViewById(R.id.IDV_FULL_SCREEN);
		fl.setOnTouchListener(BTetrisActivity.this);
		_nextBlockBoard = (BNextBlockBoard)findViewById(R.id.IDV_NEXT_BLOCK_BOARD);
		_scoreBoard = (BScoreBoard)findViewById(R.id.IDC_SCORE_BOARD);
		_test = (TextView)findViewById(R.id.test);
		
		_timer = new BTimer(_control.getSpeed()) {
			protected void looper() {
				BTetrisActivity.this.looper();
			}
		};
		
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();   
		int width = display.getWidth();
		_leftMargin = width/3;
		_rotationMargin = width*2/3;
		start();
	}
	
	/**
	 * start timer
	 */
	public void start() {
		_running = true;
		int block = _nextBlockBoard.makeNext();
		_board.start(block);
		_timer.start();
	}
	
	/**
	 * game looper
	 */
	public void looper() {
		if (_running) {
			runCommand(BBoard.CommandType.MOVE_DOWN);
		}
	}
	
	public void stop() {
		_running = false;
		_timer.stop();
	}
	
	public void restart() {
		init();
		start();
	}
	
	private void init() {
		_control.init();
		_board.init();
		_timer.resetTimer(_control.getSpeed());
		_scoreBoard.setValue(_control.getScore());
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!_running) return false;
		
		BControl.ControlMode mode = _control.getControlMode();
		if (mode == BControl.ControlMode.FLING) {
			return touchControlWithFlingMode(event);
		}
		else if (mode == BControl.ControlMode.BUTTON) {
			return touchControlWithButtonMode(event);
		}
		return false;
	}
	
	/**
	 * touch control with fling mode
	 * @param event motion event
	 * @return always true
	 */
	protected boolean touchControlWithFlingMode(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			_xDownPosition = event.getX();
			_yDownPosition = event.getY();
		}
		else if(event.getAction() == MotionEvent.ACTION_UP){
			float x = event.getX();
			float y = event.getY();
			
			float moveHorizontal = x - _xDownPosition;
			float moveVertical = y - _yDownPosition;
			
			BBoard.CommandType command = BBoard.CommandType.COMMAND_NONE;
			if (moveHorizontal == 0 && moveVertical == 0) {
				command = BBoard.CommandType.COMMAND_NONE; // not determined
			}
			else if (Math.abs(moveHorizontal) >= Math.abs(moveVertical)) {
				if (moveHorizontal > 0) command = BBoard.CommandType.MOVE_RIGHT;
				else command = BBoard.CommandType.MOVE_LEFT;
			}
			else {
				if (moveVertical > 0) command = BBoard.CommandType.MOVE_DOWN;
				else command = BBoard.CommandType.ROTATE;
			}
			runCommand(command);
		}
		return true;
	}
	
	/**
	 * touch control with fling mode
	 * @param event motion event
	 * @return always true
	 */
	protected boolean touchControlWithButtonMode(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			BBoard.CommandType command;
			int x = (int)event.getX();
			if (x < _leftMargin) command = BBoard.CommandType.MOVE_LEFT;
			else if (x < _rotationMargin) command = BBoard.CommandType.ROTATE;
			else command = BBoard.CommandType.MOVE_RIGHT;
			
			runCommand(command);
		}
		return true;
	}
	
	/**
	 * pass the user command to BBoard.
	 * 
	 * @param command user command.
	 * @return
	 */
	protected boolean runCommand(BBoard.CommandType command) {
		if (!_board.runCommand(command)) { // current block reaches to the bottom
			if (!_board.setBlock(_nextBlockBoard.makeNext())) { // game is over
				stop();
				gameOver();
			}
			int removed = _board.getRecentlyRemoved();
			if (removed > 0) {
				int speed = _control.addScore(removed);
				_scoreBoard.setValue(_control.getScore());
				_timer.resetTimer(speed);
			}
		}
		_board.redraw();
		return _running;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
    public void onBackPressed() {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);

    	dialog.setMessage(getString(R.string.IDS_EXIT_MESSAGE));

    	dialog.setPositiveButton(getString(R.string.IDS_YES), 
    			new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) { 
    			//BTetrisActivity.this.finish();
    			System.exit(0); // kill !
    		}
    	});
    	dialog.setNegativeButton(getString(R.string.IDS_NO), null);
    	dialog.show();
    }
	
	/**
	 * creating option menu
	 */
	private static final int ID_RESTART = 0;
	private static final int ID_RECORD = 1;
	private static final int ID_SETTING = 2;
	private static final int PREFER_REQUSET_CODE = 1;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ID_RESTART, ID_RESTART, R.string.IDS_RESTART);
		menu.add(0, ID_RECORD, ID_RECORD, R.string.IDS_RECORD);
		menu.add(0, ID_SETTING, ID_SETTING, R.string.IDS_SETTING).setIcon(R.drawable.setting_icon);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == ID_RESTART){
			 if (!_running) restart();
		}
		else if(item.getItemId() == ID_RECORD){
			gotoScoreView(-1);
		}
		else if(item.getItemId() == ID_SETTING){
			onSetting();
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * go to the preference page.
	 * @return true.
	 */
	protected boolean onSetting() {
		stop();
		Intent i = new Intent();
		i.setClassName(BTetrisActivity.this.getPackageName(), BTetrisPreferenceActivity.class.getName());
		i.putExtra("control_code", _control.getControlMode() == BControl.ControlMode.FLING ? 0 : 1);
		i.putExtra("direct", _control.getDirect() == BControl.Direct.CW ? 0 : 1);
		startActivityForResult(i, PREFER_REQUSET_CODE);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PREFER_REQUSET_CODE) {
			if (resultCode == RESULT_OK) {
				int get = data.getIntExtra("control_mode", 1);
				_control.setControlMode(get == 0 ? 
						BControl.ControlMode.FLING : BControl.ControlMode.BUTTON);
				get = data.getIntExtra("direct", 1);
				_control.setDirect(get == 0 ? 
						BControl.Direct.CW : BControl.Direct.CCW);
				_board.setDirect(get == 1);
			}
		}
		start();
	}
	
	/**
	 * process when game is over.
	 */
	protected void gameOver() {
		final int score = _control.getScore();
		
		final BRecordManager rm = new BRecordManager(this);
		final int rank = rm.getRank(score);
		
		if (rank <= 4 && score > 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = LayoutInflater.from(this).inflate(R.layout.get_name_dialog, null);
			final EditText nameEdit = (EditText)view.findViewById(R.id.IDC_NAME_EDIT_TEXT);
			builder.setView(view);
			nameEdit.setSelectAllOnFocus(true);
			nameEdit.requestFocus();
			
			builder.setTitle(R.string.IDS_NEW_RECORD_MSG);
			builder.setPositiveButton(R.string.IDS_OK, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					BVirtualKeyboardUtil.hideInputMethod(BTetrisActivity.this);
					String name = nameEdit.getText().toString();
					if (name.trim().length() == 0) name = "babotetris";
					rm.insertRecord(name, score);
					gotoScoreView(rank);
				}
			})
			.setNegativeButton(R.string.IDS_CANCEL, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					BVirtualKeyboardUtil.hideInputMethod(BTetrisActivity.this);
					rm.insertRecord("babotestris", score);
					gotoScoreView(rank);
				}
			}).show();
			
			BVirtualKeyboardUtil.showInputMethod(this, nameEdit);
		}
		else if (score > 0) {
			gotoScoreView(-1);
		}
		
		_control.init();
	}
	
	/**
	 * move to the score view
	 * @param score the current score
	 */
	public void gotoScoreView(int rank) {
		Intent i = new Intent();
		i.setClassName(BTetrisActivity.this.getPackageName(), BRecordActivity.class.getName());
		i.putExtra("rank", rank);
		startActivity(i);
	}
}
