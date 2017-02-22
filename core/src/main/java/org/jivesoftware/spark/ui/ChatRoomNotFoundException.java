/**
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
package org.jivesoftware.spark.ui;

/**
 * Thrown when a Chat Room was not found.
 *
 * @author Derek DeMoro
 */
public class ChatRoomNotFoundException extends Exception {
	private static final long serialVersionUID = 517234944941907783L;

	public ChatRoomNotFoundException() {
        super();
    }

    public ChatRoomNotFoundException(String msg) {
        super(msg);
    }
}