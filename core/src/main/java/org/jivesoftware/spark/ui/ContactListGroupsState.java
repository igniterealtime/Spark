package org.jivesoftware.spark.ui;

import org.jivesoftware.Spark;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

class ContactListGroupsState {
    private final Properties props;
    private final File propertiesFile;

    ContactListGroupsState() {
        props = new Properties();
        propertiesFile = new File(Spark.getSparkUserHome(), "/groups.properties");
        try {
            props.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            // File does not exist.
        }
    }

    void saveState() {
        if (props == null) {
            return;
        }
        // remove from props all false values
        props.entrySet().removeIf(entry -> !Boolean.parseBoolean((String) entry.getValue()));
        try {
            props.store(new FileOutputStream(propertiesFile), null);
        } catch (IOException e) {
            Log.error("Unable to save group properties.", e);
        }
    }

    boolean isGroupCollapsed(ContactGroup group) {
        // Check state
        String prop = props.getProperty(group.getGroupName());
        return Boolean.parseBoolean(prop);
    }

    void setGroupCollapsed(ContactGroup contactGroup, boolean collapsed) {
        if (!collapsed) {
            props.remove(contactGroup.getGroupName());
        } else {
            props.put(contactGroup.getGroupName(), "true");
        }
    }
}
