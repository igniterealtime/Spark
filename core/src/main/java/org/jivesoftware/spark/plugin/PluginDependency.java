package org.jivesoftware.spark.plugin;

import org.jivesoftware.spark.util.log.Log;

public class PluginDependency
{
	private String name;
	private String version;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public boolean compareVersion(String version) {
        if (version == null || getVersion() == null) {
            return false;
        }
			String[] checkVersion = version.split("\\.");
			String[] originalVersion = getVersion().split("\\.");
			int maxlength = Math.max(originalVersion.length, checkVersion.length);
			// go through all Version-parts
			for(int i= 0; i < maxlength; i++) {
				// if the checked version is too short
				if ( checkVersion.length <= i 
					&& originalVersion[i].equals("0"))
					return true;
				else if (checkVersion.length <= i)
					return false;
				// if the original version is long enough
				if(originalVersion.length > i) {
                    int originalVersNumber;
                    int checkVersNumber;
                    try {
                        originalVersNumber = Integer.parseInt(originalVersion[i]);
                        checkVersNumber = Integer.parseInt(checkVersion[i]);
					}
					catch(Exception e) {
						Log.error("Version " + version + " contains letters.", e);
						return false;
					}
                    // check the numbers
                    if (checkVersNumber > originalVersNumber) {
                        return true;
                    } else if (checkVersNumber < originalVersNumber) {
                        return false;
                    }
				}
				else {
					return true;
				}
			}
			return true;
	}

    @Override
    public String toString() {
        return name + ":" + version;
    }
}
