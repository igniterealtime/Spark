package org.jivesoftware.spark.ui;

import org.junit.Ignore;
import org.junit.Test;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import static org.junit.Assert.*;

public class ContactItemTest {

    @Test
    public void getDisplayName() throws XmppStringprepException {
        ContactItem o1;
        ContactItem o2;
        int comp;

        o1 = new ContactItem(null, null, JidCreate.entityBareFrom("juliet@capulet.lit"));
        o2 = new ContactItem(null, null, JidCreate.entityBareFrom("romeo@montague.lit"));
        assertEquals("juliet@capulet.lit", o1.getDisplayName());
        assertEquals("romeo@montague.lit", o2.toString());
        comp = ContactItem.CONTACT_ITEM_COMPARATOR.compare(o1, o2);
        assertTrue(comp < 0);

        o1 = new ContactItem(null, "a", JidCreate.entityBareFrom("juliet@capulet.lit"));
        o2 = new ContactItem(null, "b", JidCreate.entityBareFrom("romeo@montague.lit"));
        assertEquals("a", o1.getDisplayName());
        assertEquals("b", o2.toString());
        comp = ContactItem.CONTACT_ITEM_COMPARATOR.compare(o1, o2);
        assertTrue(comp < 0);

        o1 = new ContactItem("Á", "a", JidCreate.entityBareFrom("juliet@capulet.lit"));
        o2 = new ContactItem("A", "b", JidCreate.entityBareFrom("romeo@montague.lit"));
        assertEquals("Á", o1.getDisplayName());
        assertEquals("A", o2.toString());
        comp = ContactItem.CONTACT_ITEM_COMPARATOR.compare(o1, o2);
        assertTrue(comp > 0);
    }
}
