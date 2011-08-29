package org.jivesoftware.spark.component.panes;

import javax.swing.Icon;

public interface ICollapsibleTitlePane {
	void setIcon(Icon icon);

	void setTitle(String title);

	boolean isCollapsed();

	void setCollapsed(boolean collapsed);

	void setSubPane(boolean subPane);
}
