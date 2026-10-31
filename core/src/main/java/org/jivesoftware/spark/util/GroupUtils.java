package org.jivesoftware.spark.util;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.spark.SparkManager;

import javax.swing.JComboBox;

public class GroupUtils {
    public static void loadGroupBox(JComboBox<String> groupBox) {
        groupBox.setEditable(true);
        Roster roster = SparkManager.getRoster();
        for (RosterGroup group : roster.getGroups()) {
            groupBox.addItem(group.getName());
        }
        if (groupBox.getItemCount() == 0) {
            groupBox.addItem(Res.getString("contact.group.colleagues"));
            groupBox.addItem(Res.getString("contact.group.colleagues.team"));
            groupBox.addItem(Res.getString("contact.group.buddies"));
            groupBox.addItem(Res.getString("contact.group.buddies.friends"));
            groupBox.addItem(Res.getString("contact.group.buddies.sports"));
            groupBox.addItem(Res.getString("contact.group.relatives"));
            groupBox.addItem(Res.getString("contact.group.relatives.family"));
            groupBox.addItem(Res.getString("contact.group.relatives.family.kids"));
            groupBox.addItem(Res.getString("contact.group.education"));
            groupBox.addItem(Res.getString("contact.group.education.school"));
            groupBox.addItem(Res.getString("contact.group.education.university"));
            groupBox.addItem(Res.getString("contact.group.business"));
            groupBox.addItem(Res.getString("contact.group.business.clients"));
            groupBox.addItem(Res.getString("contact.group.business.partners"));
            groupBox.addItem(Res.getString("contact.group.business.suppliers"));
            groupBox.addItem(Res.getString("contact.group.neighbours"));
            groupBox.addItem(Res.getString("contact.group.services"));
            groupBox.addItem(Res.getString("contact.group.services.doctors"));
            groupBox.addItem(Res.getString("contact.group.services.repairmans"));
        }
        groupBox.setSelectedIndex(0);
    }
}
