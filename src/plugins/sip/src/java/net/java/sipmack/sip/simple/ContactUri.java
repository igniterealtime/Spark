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
package net.java.sipmack.sip.simple;

/**
 * The <contact> element contains a URL of the contact address. It optionally
 * has a 'priority' attribute, whose value means a relative priority of this
 * contact address over the others. The value of the attribute MUST be a decimal
 * number between 0 and 1 inclusive with at most 3 digits after the decimal
 * point. Higher values indicate higher priority. Examples of priority values
 * are 0, 0.021, 0.5, 1.00. If the 'priority' attribute is omitted, applications
 * MUST assign the contact address the lowest priority. If the 'priority' value
 * is out of the range, applications just SHOULD ignore the value and process it
 * as if the attribute was not present. Applications SHOULD handle contacts with
 * a higher priority as they have precedence over those with lower priorities.
 * How they are actually treated is beyond this specification. Also, how to
 * handle contacts with the same priority is up to implementations.
 *
 * @author Emil Ivov
 * @version 1.0
 */

public class ContactUri implements Comparable<ContactUri> {
    /**
     */
    private float priority = 0;

    /**
     */
    private String contactValue = null;

    /**
     * Sets the value of the priority attribute.
     *
     * @param priority a three digit float value indicating contact priority
     * @uml.property name="priority"
     */
    public void setPriority(float priority) {
        this.priority = priority;
    }

    /**
     * Sets the value of this contact.
     *
     * @param contactValue the value to give to this contact
     * @uml.property name="contactValue"
     */
    public void setContactValue(String contactValue) {
        this.contactValue = contactValue;
    }

    /**
     * Returns the value of the priority attribute.
     *
     * @return a three digit float value indicating contact priority
     * @uml.property name="priority"
     */
    public float getPriority() {
        return priority;
    }

    /**
     * Returns the value of this contact
     *
     * @return the value of this contact
     * @uml.property name="contactValue"
     */
    public String getContactValue() {
        return contactValue;
    }

    /**
     * Compares this object with the specified object for order.
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is
     *         less than, equal to, or greater than the specified object.
     */
//    public int compareTo(Object o) {
//        if (!(o instanceof ContactUri))
//            return Integer.MAX_VALUE;
//        return -(((int)(getPriority() * 1000)) - (int)(((ContactUri)o)
//                .getPriority() * 1000));
//    }

    public Object clone() {
        ContactUri clone = new ContactUri();
        clone.setContactValue(new String(getContactValue()));
        clone.setPriority(getPriority());
        return clone;
	}

    @Override
    public int compareTo(ContactUri o) {
	return -(((int)(getPriority() * 1000)) - (int)(((ContactUri)o)
                .getPriority() * 1000));
    }
}
