package org.jivesoftware.spark.ui;

import org.junit.Ignore;
import org.junit.Test;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import static org.junit.Assert.*;

@Ignore
public class ContactItemTest {

    @Test
    public void getDisplayName() throws XmppStringprepException {
        ContactItem o1;
        ContactItem o2;
        int comp;
        o1 = new ContactItem(null, null, null);
        o2 = new ContactItem(null, null, null);
        assertEquals("", o1.getDisplayName());
        assertEquals("", o2.toString());
        assertEquals("", o2.getAlias());
        assertEquals("", o2.getNickname());
        comp = ContactItem.CONTACT_ITEM_COMPARATOR.compare(o1, o2);
        assertEquals(0, comp);

        o1 = new ContactItem(null, null, JidCreate.from("juliet@capulet.lit").asBareJid());
        o2 = new ContactItem(null, null, JidCreate.from("romeo@montague.lit").asBareJid());
        assertEquals("juliet@capulet.lit", o1.getDisplayName());
        assertEquals("romeo@montague.lit", o2.toString());
        comp = ContactItem.CONTACT_ITEM_COMPARATOR.compare(o1, o2);
        assertTrue(comp < 0);

        o1 = new ContactItem(null, "a", JidCreate.from("juliet@capulet.lit").asBareJid());
        o2 = new ContactItem(null, "b", JidCreate.from("romeo@montague.lit").asBareJid());
        assertEquals("a", o1.getDisplayName());
        assertEquals("b", o2.toString());
        comp = ContactItem.CONTACT_ITEM_COMPARATOR.compare(o1, o2);
        assertTrue(comp < 0);

        o1 = new ContactItem("Á", "a", JidCreate.from("juliet@capulet.lit").asBareJid());
        o2 = new ContactItem("A", "b", JidCreate.from("romeo@montague.lit").asBareJid());
        assertEquals("Á", o1.getDisplayName());
        assertEquals("A", o2.toString());
        comp = ContactItem.CONTACT_ITEM_COMPARATOR.compare(o1, o2);
        assertTrue(comp > 0);
    }
}
