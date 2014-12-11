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

class BBoard extends View {
	/** the UI thread handler */
	private Handler _handler;
	/** the view bounds. */
	Rect _rect = new Rect();
	/** board width */
	public static final int WIDTH = 10;
	/** board height */
	public static final int HEIGHT = 20;
	/** board data */
	private int [][] _board = new int [HEIGHT][WIDTH];
	/** current block */
	private BBlock _block = new BBlock();
	/** block width in pixel */
	private int _width;
	/** block height in pixel */
	private int _height;
	/** keep the data for scoring */
	private int _recentlyRemoved = 0;
	
	public BBoard(Context context) {
		super(context);
		_handler = new Handler();
	}

	public BBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		_handler = new Handler();
	}
	
	public void setDirect(boolean d) {_block.setDirect(d);}
	/**
	 * set new block on the board
	 * 
	 * @param block the new block
	 * @return true if game is on, false if game is over
	 */
	public boolean setBlock(int block) {
        if (block <= 0 || block > BBlock.BLOCK_NUM) _block.setCurrentBlock(1);
        else _block.setCurrentBlock(block);
        _block.resetPos();
        
        return isAbleToBe(0, 0, false);
    }

	public int getRecentlyRemoved() {return _recentlyRemoved;}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.getClipBounds(_rect);
		_width = _rect.width() / WIDTH - 1;
		_height = _rect.height() / HEIGHT - 1;
		drawBackground(canvas);
		drawBlock(canvas);
	}
	
	/**
	 * draw background as the board data
	 * 
	 * @param canvas the canvas to paint to
	 */
	protected void drawBackground(Canvas canvas) {
		Paint paint = new Paint();
		
		int h, w;
		for (h = 0; h < HEIGHT; h++) {
			for (w = 0; w < WIDTH; w++) {
				paint.setColor(BBlock.getBlockColor(_board[h][w]));
				canvas.drawRect(new Rect(w*_width, h*_height,
						(w+1)*_width-1,	(h+1)*_height-1), paint);
			}
		}
	}
	
	/**
	 * draw current block
	 * 
	 * @param canvas the canvas to paint to
	 */
	protected void drawBlock(Canvas canvas) {
        int w, h;
        Paint paint = new Paint();
        boolean [][] block;
        block = _block.getCurrentBlockMatrix();

        for (h = 0; h < 4; h++) {
        	for (w = 0; w < 4; w++) {
                if (block[h][w]) {
                    paint.setColor(BBlock.getBlockColor(_block.getCurrentBlock()));
                }
                else {
                    continue;
                }
                canvas.drawRect(new Rect(
                		(_block.getPosX()+w)*_width, 
                		(_block.getPosY()+h)*_height,
                        (_block.getPosX()+w+1)*_width-1,
                        (_block.getPosY()+h+1)*_height-1), paint);
            }
        }
	}
	
	/**
	 * start the game
	 * 
	 * @param block the first block to be shown on the game
	 */
	public void start(int block) {
		_block.setCurrentBlock(block);
		redraw();
	}

	/**
	 * initialize board data
	 */
	public void init() {
		int h, w;
		for (h = 0; h < HEIGHT; h++) {
			for (w = 0; w < WIDTH; w++) {
				_board[h][w] = BBlock.BLOCK_NONE;
			}
		}
	}
	/**
	 * enumeration for user command.
	 *
	 */
	public enum CommandType {
		COMMAND_NONE,		// not used
		MOVE_RIGHT,
		MOVE_LEFT,
		MOVE_DOWN,
		MOVE_TO_BOTTOM,
		ROTATE
	}

	/**
	 * process the command from the user.
	 * 
	 * @param command the command from the user.
	 * @return false if game is over, otherwise return true.
	 */
	public boolean runCommand(CommandType command) {
		if (command == CommandType.MOVE_RIGHT) {
			if (isAbleToMoveRight()) _block.moveToRight();
		}
		else if (command == CommandType.MOVE_LEFT) {
			if (isAbleToMoveLeft()) _block.moveToLeft();
		}
		else if (command == CommandType.MOVE_DOWN) {
			if (isAbleToMoveDown()) _block.moveToDown();
			else {
				_recentlyRemoved = attachBlock();
				return false;
			}
		}
		else if (command == CommandType.MOVE_TO_BOTTOM) {
			while (isAbleToMoveDown()) {
				_block.moveToDown();
			}
			_recentlyRemoved = attachBlock();
			return false;
		}
		else if (command == CommandType.ROTATE) {
			if (isAbleToRotate()) _block.rotate();
			return true;
		}
		return true;
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
	 * return the possibility to move to the right 
	 * 
	 * @return true if it is possible to move to the right, false otherwise
	 */
	protected boolean isAbleToMoveRight() {
        if (isAbleToBe(1, 0, false)) return true;
        else return false;
	}
	
	/**
	 * return the possibility to move to the left 
	 * 
	 * @return true if it is possible to move to left, false otherwise
	 */
	protected boolean isAbleToMoveLeft() {
        if (isAbleToBe(-1, 0, false)) return true;
        else return false;
	}
	
	/**
	 * return the possibility to move to the down 
	 * 
	 * @return true if it is possible to move to the down, false otherwise
	 */
	protected boolean isAbleToMoveDown() {
        if (isAbleToBe(0, 1, false)) return true;
        else return false;
	}
	
	/**
	 * return the possibility to rotate 
	 * 
	 * @return true if it is possible to rotate, false otherwise
	 */
	protected boolean isAbleToRotate() {
        if (isAbleToBe(0, 0, true)) return true;
        else return false;
	}
	
	/**
	 * return the possibility that the block can move to delta x and delta y.
	 * 
	 * @param block current block data
	 * @param deltaX distance to move horizontally
	 * @param deltaY distance to move vertically
	 * @return true if it can move to the specified position
	 */
	protected boolean isAbleToBe(int deltaX, int deltaY, boolean rotate) {
        boolean [][] block;
        if (rotate) block = BBlock.getBlockMatrix(
        		_block.getCurrentBlock(), _block.getNextRotation());
        else block = _block.getCurrentBlockMatrix();

        int posX = _block.getPosX() + deltaX;
        int posY = _block.getPosY() + deltaY;
        
		int w, h;
		for (h = 0; h < 4; h++) {
			for (w = 0; w < 4; w++) {
                if (block[h][w] && (posX+w < 0 || posX+w >= WIDTH || posY+h >= HEIGHT)) return false;
                if (block[h][w] && _board[posY+h][posX+w] > 0) return false;
            }
        }
		return true;
	}
	
	/**
	 * attach the current block to the board and remove the line which is full.
	 * 
	 * @return the number of removed line.
	 */
	private int attachBlock() {
		boolean [][] block;
		block = _block.getCurrentBlockMatrix();

        int deltaX = _block.getPosX();
        int deltaY = _block.getPosY();
        int current = _block.getCurrentBlock();

        int w, h;
        
        for (h = 0; h < 4; h++) {
        	for (w = 0; w < 4; w++) {
                if (block[h][w]) {
                    _board[deltaY+h][deltaX+w] = current;
                }
            }
        }
        
        return removeFullLine();
	}
	
	/**
	 * find and remove the full line.
	 * 
	 * @return the number of removed line.
	 */
	private int removeFullLine() {
        int w, h, count;
        int removed = 0;

        for (h = 0; h < HEIGHT; h++) {
            count = 0;
            for (w = 0; w < WIDTH; w++) {
                if (_board[h][w] == BBlock.BLOCK_NONE) break;
                else count++;
            }
            if (count == WIDTH) {
            	removeShift(h);
                removed++;
            }
        }

        return removed;
	}
	
	/**
	 * remove the full line and shift blocks to the empty space.
	 */
	private void removeShift(int line) {
		int w, h;
        for (h = line; h > 0; h--) {
            for (w = 0; w < WIDTH; w++)
                _board[h][w] = _board[h-1][w];
        }

        // clear first line on the board
        for (w = 0; w < WIDTH; w++)
            _board[w][0] = 0;
	}
}
