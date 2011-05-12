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
package org.jivesoftware.spark.plugin;

/**
 * The Plugin interface is the required implementation to have your Sparkplugs work within the Spark client.
 * Users will implement this interface, register the class in their own plugin.xml and do any initialization
 * within the initialize method. It's also a good idea to unregister any components, listeners and other resources
 * in the #uninstall method to allow for better usability.
 */
public interface Plugin {


    /**
     * Invoked by the <code>PluginManager</code> after the instance of the
     * <code>Plugin</code> is instantiated.  When invoked, The <code>Plugin</code>
     * should register with whatever listeners they may need to use or are required
     * for use during this classes lifecycle. <code>Plugin</code> authors should take
     * care to ensure that any extraneous initialization is not preformed on this method, and
     * any startup code that can be delayed until a later time is delayed, as
     * the <code>Plugin</code>'s are synchronously initialized during the
     * startup of Spark, and each <code>Plugin</code> has the potential to
     * negatively impact the startup time of the product.
     *
     * @see org.jivesoftware.spark.PluginManager
     */
    public void initialize();

    /**
     * This method is invoked by the <code>PluginManager</code> when Spark
     * wishes you to remove any temporary resources (in memory) such as installed
     * components, or settings.  Any non java resources (file handles, database connections,
     * etc) which are still being held by this <code>Plugin</code> should be
     * released by this method immediately.  This method is not guaranteed to
     * be called, but on normal terminations of Spark, this method will be
     * invoked.
     */
    public void shutdown();

    /**
     * This method is invoked by the <code>PluginManager</code> before Spark
     * terminates. <code>Plugin</code>'s should NOT use this method to release resources.
     * They should only use this method to give users the opportunity to
     * cancel the exit process if there is some process started by this
     * plugin still running.
     * <p/>
     * Implementations should return <CODE>false</CODE> to cancel the shutdown
     * process.
     *
     * @return true if the plugin can shut currently.
     */
    public boolean canShutDown();


    /**
     * This method is invoked by the <code>PluginManager</code> when a Spark user
     * asks that this plugin be uninstalled. Before this method is called, you
     * will need to release all your in-memory resources in the #shutdown method.  This
     * method should be used to remove on disk resources such as files, images, etc.
     */
    public void uninstall();

}