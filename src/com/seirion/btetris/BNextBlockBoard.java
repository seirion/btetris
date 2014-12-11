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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public  class BNextBlockBoard extends View {
	/** the UI thread handler */
	private Handler _handler;
	/** the view bounds. */
	private Rect _rect = new Rect();
	/** block width in pixel */
	private int _width;
	/** block height in pixel */
	private int _height;
	/** current block */
	private int _block;
	/** ratio of block and background */
	private static int BG_SIZE = 5;
	
	public BNextBlockBoard(Context context) {
		super(context);
		_handler = new Handler();
		_block = getRandomBlock();
	}

	public BNextBlockBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		_handler = new Handler();
		_block = getRandomBlock();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (_block != BBlock.BLOCK_NONE) {
			_rect = canvas.getClipBounds();
			_width = _rect.width() / BG_SIZE;
			_height = _rect.height() / BG_SIZE;
		}
		//drawBackground(canvas);
		drawBlock(canvas);
	}
	
	/**
	 * draw background
	 * 
	 * @param canvas the canvas to paint to
	 */
	protected void drawBackground(Canvas canvas) {
		Paint paint = new Paint();
		
		int h, w;
		for (h = 0; h < BG_SIZE; h++) {
			for (w = 0; w < BG_SIZE; w++) {
				paint.setColor(BBlock.getBlockColor(BBlock.BLOCK_NONE));
				canvas.drawRect(new Rect(w*_width, h*_height,
						(w+1)*_width-1,	(h+1)*_height-1), paint);
			}
		}
	}
	
	/**
	 * draw next block
	 * 
	 * @param canvas the canvas to paint to
	 */
	protected void drawBlock(Canvas canvas) {
        int w, h;
        Paint paint = new Paint();
        boolean [][] block;
        block = BBlock.getBlockMatrix(_block, 0);

        for (h = 0; h < 4; h++) {
        	for (w = 0; w < 4; w++) {
                if (block[h][w]) {
                    paint.setColor(BBlock.getBlockColor(_block));
                }
                else {
                    continue;
                }
                canvas.drawRect(new Rect(
                		(w)*_width, (h)*_height, 
                		(w+1)*_width-1, (h+1)*_height-1), paint);
            }
        }
	}
	
	/**
	 * generate new block randomly.
	 * 
	 * @return previous block type.
	 */
	public int makeNext() {
		int oldBlock = _block;
		_block = getRandomBlock();
		redraw();
		return oldBlock;
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
	
	/**
	 * @return the random block number (between 1 and 7)
	 */
	protected int getRandomBlock() {
		return (int)(Math.random()*7)+1;
	}
}