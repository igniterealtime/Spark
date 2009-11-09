package org.jivesoftware.spark.component.tabbedPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JPanel;

public class SparkTab extends JPanel 
{
	private static final long serialVersionUID = 2027267184472260195L;
	private SparkTabbedPane pane = null;
	private Component component = null;
	
	public SparkTab(SparkTabbedPane pane, Component comp)
	{
		this.pane = pane;
		this.component = comp;
		this.setLayout(new BorderLayout());
		add(comp);
	}
	
	public void setTabTitle(String title)
	{
		pane.setTitleAt(pane.getTabPosition(this),title);
	}
	
	public void setIcon(Icon icon)
	{
		pane.setIconAt(pane.getTabPosition(this), icon);
	}
	
	public Component getComponent()
	{
		return component;
	}

	public String getTitleLabel()
	{
		return pane.getTitleAt(pane.getTabPosition(this));
	}

	public String getActualText()
	{
		return pane.getTitleAt(pane.getTabPosition(this));
	}
	
	public void setTitleColor(Color color)
	{
		pane.setTitleColorAt(pane.getTabPosition(this),color);
	}
	
	public void setTabBold(boolean bold)
	{
		pane.setTitleBoldAt(pane.getTabPosition(this),bold);
	}
	
	public Font getDefaultFont()
	{
		return pane.getDefaultFontAt(pane.getTabPosition(this));
	}
	
	public void setTabFont(Font font)
	{
		pane.setTitleFontAt(pane.getTabPosition(this),font);
	}
	
	public void validateTab()
	{
		invalidate();
		validate();
		repaint();
	}
}
