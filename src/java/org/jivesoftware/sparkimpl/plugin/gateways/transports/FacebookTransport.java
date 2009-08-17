package org.jivesoftware.sparkimpl.plugin.gateways.transports;

import javax.swing.Icon;

import org.jivesoftware.resource.Res;
import org.jivesoftware.resource.SparkRes;

public class FacebookTransport implements Transport {
	
    private String serviceName;

    public FacebookTransport(String serviceName){
        this.serviceName = serviceName;
    }

    public String getTitle(){
        return Res.getString("title.facebook.registration");
    }

    public String getInstructions() {
        return Res.getString("message.enter.facebook");
    }

    public Icon getIcon() {
        return SparkRes.getImageIcon(SparkRes.FACEBOOK_TRANSPORT_ACTIVE_IMAGE);
    }

    public Icon getInactiveIcon() {
        return SparkRes.getImageIcon(SparkRes.FACEBOOK_TRANSPORT_INACTIVE_IMAGE);
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getName(){
        return "Facebook";
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Boolean requiresUsername() {
        return true;
    }

    public Boolean requiresPassword() {
        return true;
    }

    public Boolean requiresNickname() {
        return false;
    }
}
