package org.jivesoftware.spark.otrplug;

import org.jivesoftware.spark.plugin.Plugin;

public class OTRPlugin implements Plugin{

    OTRManager _manager; 
    
    @Override
    public void initialize() {
        System.out.println("initialized");
        _manager.getInstance();
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean canShutDown() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void uninstall() {
        // TODO Auto-generated method stub
        
    }

}
