package org.jivesoftware.spark.plugin;

import org.jivesoftware.spark.plugin.PluginDependency;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PluginDependencyTest
{
	@Test
	public void testCompare()
	{
		PluginDependency depend = new PluginDependency();
		depend.setName("Test");
		depend.setVersion("1.0.0");
		
		assertTrue(depend.compareVersion("1.0.0"));
		assertTrue(depend.compareVersion("1.1"));
		assertTrue(depend.compareVersion("1.1.1"));
		assertFalse(depend.compareVersion("0.9.0"));
		assertTrue(depend.compareVersion("1.9.0.0"));
		assertFalse(depend.compareVersion("0.9.1"));
		assertTrue(depend.compareVersion("1.9.1"));
		
		
		depend.setVersion("2.0.0.0");
		assertTrue(depend.compareVersion("2.1"));
		assertTrue(depend.compareVersion("2.1.1"));
		assertFalse(depend.compareVersion("0.9.0"));
		assertTrue(depend.compareVersion("2.9.0.0"));
		assertFalse(depend.compareVersion("0.9.1"));
		assertTrue(depend.compareVersion("2.9.1"));
		
		depend.setVersion("2.1");
		assertTrue(depend.compareVersion("2.1"));
		assertTrue(depend.compareVersion("2.1.1"));
		assertTrue(depend.compareVersion("2.1.0"));
		assertFalse(depend.compareVersion("0.9.0"));
		assertTrue(depend.compareVersion("2.9.0.0"));
		assertFalse(depend.compareVersion("0.9.1"));
		assertTrue(depend.compareVersion("2.9.1"));

	}
}
