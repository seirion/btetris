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

import android.graphics.Color;

public class BBlock {
	/** size of single block in pixel */
    public final static int DEFAULT_BLOCK_SIZE = 30; 
    /** define blocks */
    public final static char   BLOCK_NONE      = 0, // it shell not be used
					           BLOCK_BAR       = 1,
					           BLOCK_SQUARE    = 2,
					           BLOCK_T		   = 3,
					           BLOCK_L         = 4,
					           BLOCK_REV_L     = 5,
					           BLOCK_N         = 6,
					           BLOCK_REV_N     = 7;
    /** the number of blocks */
    public final static int BLOCK_NUM = 7;
    /** transparency */
    private final static int TRANSPARENCY = 0xA0000000;
    /** the color of blocks */
    private final static int[] _color = {
    	~TRANSPARENCY & Color.BLACK, 	// BLOCK_NONE
    	~TRANSPARENCY & 0xFF238956, 		// BLOCK_BAR
    	~TRANSPARENCY & Color.BLUE, 		// BLOCK_SQUARE
    	~TRANSPARENCY & Color.CYAN, 		// BLOCK_T
    	~TRANSPARENCY & Color.RED, 		// BLOCK_L
    	~TRANSPARENCY & Color.GREEN, 	// BLOCK_REV_L
    	~TRANSPARENCY & Color.MAGENTA, 	// BLOCK_N
    	~TRANSPARENCY & Color.YELLOW 	// BLOCK_REV_N
    };

    /**
     * return the color of the block
     * 
     * @param block the block
     * @return the color of the block
     */
    public static int getBlockColor(int block) {
        if (block < 0 || block > BLOCK_NUM) return (TRANSPARENCY | 0x00FFFFFF);
        return _color[block];
    }
    
    /** the shapes of blocks */
    private final static boolean[][][] _blocks = 
    {
        { // NONE
            {false, false, false, false},
            {false, false, false, false},
            {false, false, false, false},
            {false, false, false, false}
        },
        { // BAR
            {false, false, true, false},
            {false, false, true, false},
            {false, false, true, false},
            {false, false, true, false}
        },
        { // SQUARE
            {false, false, false, false},
            {false, true, true, false},
            {false, true, true, false},
            {false, false, false, false}
        },
        { // T
            {false, false, false, false},
            {false, true, false, false},
            {true, true, true, false},
            {false, false, false, false}
        },
        { // L
            {false, false, false, false},
            {false, true, false, false},
            {false, true, false, false},
            {false, true, true, false}
        },
        { // REV_L
            {false, false, false, false},
            {false, false, true, false},
            {false, false, true, false},
            {false, true, true, false}
        },
        { // N
            {false, true, false, false},
            {false, true, true, false},
            {false, false, true, false},
            {false, false, false, false}
        },
        { // REV_N
            {false, false, true, false},
            {false, true, true, false},
            {false, true, false, false},
            {false, false, false, false}
        }
    };
    
    /** current block on the field */
    private int _currentBlock; 
	/** status of current block */
    private int _rotation;
    /** current x-position on the board */
    private int _posX;
    /** current y-position on the board */
    private int _posY;
	/** rotation direct (true : CCW, false : CW) */
	private boolean _direct;
    
    /**
     * Create new Block
     */
    public BBlock() {
        _currentBlock = BLOCK_SQUARE;
        resetPos();
    }
    
    /**
     * set the block initial position on the board
     */
    public void resetPos() { _posX = 3; _posY = 0; _rotation = 0;}
    
    public void setCurrentBlock(int block) {
        if (block <= 0 || block > BLOCK_NUM) _currentBlock = 1;
        else _currentBlock = block;
    }

    public int getCurrentBlock() { return _currentBlock; }
    public void setPosX(int x) { _posX = x; }
    public void setPosY(int y) { _posY = y; }
    public int getPosX() { return _posX; }
    public int getPosY() { return _posY; }
	public void setDirect(boolean d) {_direct = d;}
    
    public void rotate() {
		if (_direct) _rotation = (_rotation + 1) % 4;
		else _rotation = (_rotation - 1 + 4) % 4;
    }
    
    public int getRotation() {return _rotation;}
    public int getNextRotation() {
		if (_direct) return (_rotation+1) % 4;
		return (_rotation-1+4) % 4;
	}
    
    /**
     * return the current block matrix.
     * 
     * @return 2*2 boolean-type matrix.
     */
    public boolean[][] getCurrentBlockMatrix() {
    	return getBlockMatrix(_currentBlock, _rotation);
    }
    
    /**
     * return the specified block matrix.
     * @param block the block specified block and rotation. 
     * @param rotation rotation state.
     * @return 2*2 boolean-type matrix represent a block.
     */
    public static boolean[][] getBlockMatrix(int block, int rotation) {
    	boolean[][] matrix = new boolean[4][4];
        int w, h;
        if (rotation == 0) {
            for (h = 0; h < 4; h++) {
                for (w = 0; w < 4; w++) {
                    matrix[h][w] = _blocks[block][h][w];
                }
            }
        } else if (rotation == 3) {
        	for (h = 0; h < 4; h++) {
                for (w = 0; w < 4; w++) {
                    matrix[w][3-h] = _blocks[block][h][w];
                }
            }
        } else if (rotation == 2) {
        	for (h = 0; h < 4; h++) {
                for (w = 0; w < 4; w++) {
                    matrix[3-h][3-w] = _blocks[block][h][w];
                }
            }
        } else if (rotation == 1) {
        	for (h = 0; h < 4; h++) {
                for (w = 0; w < 4; w++) {
                    matrix[h][w] = _blocks[block][w][3-h];
                }
            }
        }
        return matrix;
    }
    
    /**
     * change block position.
     */
    public void moveToDown() { _posY++; }
    public void moveToRight() { _posX++; }
    public void moveToLeft() { _posX--; }
}

