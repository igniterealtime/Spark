package org.jivesoftware.spark.plugin.otr.pref;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jivesoftware.spark.plugin.otr.util.OTRProperties;
import org.jivesoftware.spark.plugin.otr.util.OTRResources;
import org.jivesoftware.spark.preference.Preference;

/**
 * Implementation of Preference interface provided by Spark
 * 
 * @author Bergunde Holger
 */
public class OTRPreferences implements Preference {
    private static final String NAMESPACE = "OTR";
    private OTRPrefPanel pref;

    @Override
    public String getTitle() {
        return OTRResources.getString("otr.title");
    }

    @Override
    public Icon getIcon() {
        return OTRResources.PLUGIN_ICON;
    }

    @Override
    public String getTooltip() {
        return OTRResources.getString("otr.title");
    }

    @Override
    public String getListName() {
        return OTRResources.getString("otr.list.entry");
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public JComponent getGUI() {
        pref = new OTRPrefPanel();
        return pref.getGUI();
    }

    @Override
    public void load() {
    }

    @Override
    public void commit() {
        OTRProperties.getInstance().setIsOTREnabled(pref.isOTREnabled());
        OTRProperties.getInstance().setOTRCloseOnChatClose(pref.isCloseOnChatClose());
        OTRProperties.getInstance().setOTRCloseOnDisconnect(pref.isCloseOnDisc());
        OTRProperties.getInstance().save();
    }

    @Override
    public boolean isDataValid() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
