package org.jivesoftware.sparkplugin.calllog;

import java.util.Collection;

import org.jivesoftware.sparkplugin.callhistory.HistoryCall;

public interface LogManager
{
	public boolean isRemoteLogging();
	
	public void setRemoteLogging(boolean remoteLogging);
	
	public void showCallHistory();
	
	public void commit();
	
	public Collection<HistoryCall> getCallHistory();
	
	public void deleteCall(HistoryCall call);
}
