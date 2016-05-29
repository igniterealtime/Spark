/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tic.tac.toe;

/**
 * Represents a Pair of Coordinates with the Mark underneath
 * 
 * @author wolf.posdorfer
 * @version 16.06.2011
 */
public class Pair {

    private int _x;
    private int _y;
    private Mark _m;

    public Pair(int x, int y, Mark m) {
	_x = x;
	_y = y;
	_m = m;
    }

    public int getX() {
	return _x;
    }

    public int getY() {
	return _y;
    }

    public Mark getMark() {
	return _m;
    }
    
    public String toString() {
	return "[" + _x + "," + _y + ";" + _m + "]";
    }

}
