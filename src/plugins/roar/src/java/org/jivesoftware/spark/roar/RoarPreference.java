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
package org.jivesoftware.spark.roar;

import java.awt.EventQueue;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.spark.roar.gui.RoarPreferencePanel;
import org.jivesoftware.spark.util.log.Log;

/**
 * Awesome Preference
 * @author wolf.posdorfer
 *
 */
public class RoarPreference implements Preference {

    
    private RoarPreferencePanel _prefPanel;
    private RoarProperties _props;

    public RoarPreference() {

	_props = RoarProperties.getInstance();
	
	try {
	    if (EventQueue.isDispatchThread()) {
		_prefPanel = new RoarPreferencePanel();
	    } else {
		EventQueue.invokeAndWait(new Runnable() {
		    @Override
		    public void run() {
			_prefPanel = new RoarPreferencePanel();
		    }
		});
	    }
	} catch (Exception e) {
	    Log.error(e);
	}

    }
    
    @Override
    public String getTitle() {
	return "ROAR";
    }

    @Override
    public Icon getIcon() {
	ClassLoader cl = getClass().getClassLoader();
	return new ImageIcon(cl.getResource("roar-logo.png"));
    }

    @Override
    public String getTooltip() {
	return "ROAR";
    }

    @Override
    public String getListName() {
	return "ROAR";
    }

    @Override
    public String getNamespace() {
	return "roar";
    }

    @Override
    public JComponent getGUI() {
	return _prefPanel;
    }

    @Override
    public void load() {

	_prefPanel.setColor(RoarPreferencePanel.ColorTypes.BACKGROUNDCOLOR, _props.getBackgroundColor());
	_prefPanel.setColor(RoarPreferencePanel.ColorTypes.HEADERCOLOR, _props.getHeaderColor());
	_prefPanel.setColor(RoarPreferencePanel.ColorTypes.TEXTCOLOR, _props.getTextColor());
	
	_prefPanel.setDisplayType(_props.getDisplayType());
	
	_prefPanel.setShowingPopups(_props.getShowingPopups());
	_prefPanel.setDuration(_props.getDuration());
	_prefPanel.setAmount(_props.getMaximumPopups());
    }

    @Override
    public void commit() {
	_props.setDuration(_prefPanel.getDuration());
	_props.setShowingPopups(_prefPanel.getShowingPopups());
	
	
	_props.setBackgroundColor(_prefPanel.getColor(RoarPreferencePanel.ColorTypes.BACKGROUNDCOLOR));
	_props.setHeaderColor(_prefPanel.getColor(RoarPreferencePanel.ColorTypes.HEADERCOLOR));
	_props.setTextColor(_prefPanel.getColor(RoarPreferencePanel.ColorTypes.TEXTCOLOR));
	
	_props.setDisplayType(_prefPanel.getDisplayType());

	
	_props.setMaximumPopups(_prefPanel.getAmount());
	_props.save();

    }

    @Override
    public boolean isDataValid() {
	return true;
    }

    @Override
    public String getErrorMessage() {
	return "roooooooooaaaaaar";
    }

    @Override
    public Object getData() {
	return _props;
    }

    @Override
    public void shutdown() {

    }

}
