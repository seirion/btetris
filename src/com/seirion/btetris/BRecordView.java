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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

class BRecordView extends View {
	private Context _context;
	/** the UI thread handler */
	private Handler _handler;
	/** the view bounds. */
	private Rect _rect = new Rect();
	/** the number of valid data */
	private int _validNum = 0;
	/** the height of bar */
	private int _barHeight;
	/** the width of graph bar */
	private int _maxBarWidth;
	/** ranking data */
	private BRecordManager.Rank [] _rank = null;
	
	public BRecordView(Context context) {
		super(context);
		_context = context;
		init();
	}

	public BRecordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
		init();
	}
	
	private void init() {
		_handler = new Handler();
		
		BRecordManager _recordManager = new BRecordManager(_context);
		_rank = _recordManager.loadRecord();
		_validNum = _recordManager.getRecordNum();
		redraw();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.getClipBounds(_rect);
		_maxBarWidth = (int)(_rect.width() * 0.7f);
		_barHeight = _rect.height() / 8 / 4; 
		
		drawData(canvas);
	}
	
	/**
	 * drawing ranking data
	 * @param canvas the canvas to paint to
	 */
	protected void drawData(Canvas canvas) {
		if (_validNum == 0) {
			// draw no record messages
			return;
		}
		int i;
		for (i = 0; i < _validNum; i++) {
			drawEach(canvas, i);
		}
	}
	
	/**
	 * drawing each ranking data (name and score)
	 * @param canvas the canvas to paint to
	 */
	protected void drawEach(Canvas canvas, int pos) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0xFF000000);
		paint.setTextSize(20);
		int y = (pos+3) * 80;
		int x = _rect.width() / 15;
		int width = _maxBarWidth * _rank[pos]._score / _rank[0]._score;
		
		
		canvas.drawText((pos+1) + ". " + _rank[pos].toString(), x, y, paint);
		y += 5;
		RectF area = new RectF(x, y, x + width, y + _barHeight);
		paint.setColor(0xff005aff);
		canvas.drawRoundRect(area, _barHeight / 2.0f, _barHeight / 2.0f, paint);
		
		//RectF area = new RectF(y - _barHeight * 0.8f, startY, x, startY + barHeight);
		RectF lightArea = new RectF(
				area.left + area.width()/2,
				area.top + 2,
				area.right - 5,
				area.bottom -2);

		LinearGradient grad;
		grad = new LinearGradient(lightArea.right, 0, lightArea.left, 0, 
				0xffffcd97, 0x00ffcd97, Shader.TileMode.MIRROR);
		paint.setColor(Color.rgb(0xff, 0x84, 0x00));
		paint.setShader(grad);

		canvas.drawRoundRect(lightArea, _barHeight / 2.2f,
				_barHeight / 2.2f, paint);
		paint.setShader(null);
		
		// score 
		paint.setColor(0xFF154454);
		canvas.drawText("" + _rank[pos]._score, x+width+4, y+16, paint);
	}
	
	/**
	 * schedule re-drawing
	 */
	public void redraw() {
		_handler.post(new Runnable() {
			public void run() {
				invalidate();
			}
		});
	}
}
