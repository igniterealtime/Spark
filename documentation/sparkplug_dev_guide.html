<html>
<head>
    <title>Sparkplug Development Guide</title>
    <link href="style.css" rel="stylesheet" type="text/css">
</head>

<body>
<div id="bannerbox">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
        <tr>
            <td width="99%" class="spring" title="Spark"></td>
            <td width="1%"><a href="spark" title="Developing Sparkplugs"><img src="images/banner-spark.gif" border="0" alt="Spark" /></a></td>
        </tr>
      </table>
</div>
<br/>
<h3>What Are Sparkplugs?</h3>

<p>Sparkplugs dynamically extend the features of the <a href="http://www.jivesoftware.org/spark">Spark</a> instant messaging client.
Use Sparkplugs to customize Spark for your business or organization or to add an innovative twist to instant messaging. The
extensive plugin API allows for complete client flexibility but is still simple and (we hope!) fun to use.</p>

<p>This guide provides a high level overview of the Sparkplug API and examples of several common client customizations. Or, jump
directly into the <a href="api/index.html">Javadocs</a>.
</p>

<h3>How to build</h3>

<a href="sparkplug_compile.html">Spark Plugin Compile Guide</a></p>

<h3>Once I Build It, Then What?</h3>
<p>After you've built your amazingly cool Sparkplug, it's easy to rollout to your users. Just have them drop it in the plugins directory of
their Spark installation. If your Sparkplug is generally useful, we hope you'll share it with the whole Spark community! To make your Sparkplug 
available via the <a href="http://www.jivesoftware.org/spark/sparkplugs">public repository</a> at jivesoftware.org, submit it to 
<a href="mailto:plugins@jivesoftware.org">plugins@jivesoftware.org</a>.</p>


<h3>Contents</h3>

This document contains the following information:
<ul>
<li><a href="#clientOverview">Overview of the Spark Client</a>
<li><a href="#overview">Overview of the Sparkplug API</a>
<li><a href="api/index.html">Sparkplug Javadocs</a>
<li><a href="#plugins">Structure Of A Plugin</a></li>
<li><a href="#gettingStarted">Getting started writing your first plugin</a></li>
<li><a href="#examples">Spark Examples and How-To's. Many examples of the most commonly asked questions when developing with Spark.</a>
</ul>

<h3><a id="clientOverview">Overview of the Spark Client</a></h3>
<p>
The Spark client is designed with the idea that most users find the different aspects of a chat client familiar and easy to use.
All components you see below are either accessible from the Workspace or ChatRoom object and can be manipulated based on your
needs.
</p>

<img src="images/contact-list.png" >
<br/>
<img src="images/chat-room.png">



<h3><a id="overview">Overview of the Spark API</a></h3>

The Spark API provides a framework for adding extensions on top of the protocol and/or UI of the Spark client. For example, you could write your own message filter or add a button to a chat room and send files using the File Transfer API. The Spark API has the following characteristics:
<ul>
<li>Several event listeners to either intercept, be informed of, or execute custom code in response to a particular IM event.
<li>Thorough tie-ins to the UI to allow for customization from simple icon changes, to adding buttons, to adding your own menu items.
<li>Ability to add your own XMPP functions using the SMACK API.
<li>Managers - Managers allow for better (lazy) loading of particular areas within the Spark client as well as providing access points to the system. Some of the more relevant managers are:
<ul>
<li> <a href="api/org/jivesoftware/spark/SparkManager.html">SparkManager</a> -- Acts as the central manager for all of Spark.  You use this manager to get instances of ChatManager, SessionManager, SoundManager, and UserManager.
<li> <a href="api/org/jivesoftware/spark/ChatManager.html">ChatManager</a> -- Handles registration of most chat listeners and filters, as well as creation and retrieval of chat rooms. It is also used to retrieve the UI of the ChatFrame.
<li> <a href="api/org/jivesoftware/spark/SessionManager.html">SessionManager</a> -- Contains the information about the current session, such as the server connected to, the handling of connection errors and notification of personal presence changes.
<li> <a href="api/org/jivesoftware/spark/SoundManager.html">SoundManager</a> -- Used to play sounds.
</ul>
<li> Event Handlers -- Spark contains numerous listeners and handlers to allow more pluggability into the Spark client. Some of the more common listeners and handlers are:
<ul>
<li> <a href="api/org/jivesoftware/spark/ui/ChatRoomListener.html">ChatRoomListener</a> (and ChatRoomListenerAdapter) -- Allows the plugin to listen for chat rooms being opened, closed and activated. You would generally use this to customize individual chat rooms.


<li> <a href="api/org/jivesoftware/spark/ui/MessageListener.html">MessageListener</a> -- Allows for notification when a message has been received or sent.
<li> <a href="api/org/jivesoftware/spark/ui/ContactGroupListener.html">ContactGroupListener</a> -- Allows for notification of changes to a Contact Group.
<li> <a href="api/org/jivesoftware/spark/ui/ContactListListener.html">ContactListListener</a> -- Allows for notification of changes to the Contact List.
<li> <a href="api/org/jivesoftware/spark/plugin/impl/filetransfer/transfer/TransferListener.html">TransferListener</a> -- Allows you to intercept File transfers.
<li> <a href="api/org/jivesoftware/spark/plugin/ContextMenuListener.html">ContextMenuListener</a> -- Allows for the addition or removal of actions or menu items to right-click (context menu) popups.
<li> <a href="api/org/jivesoftware/spark/ui/PresenceListener.html">PresenceListener</a> -- Allows for notification when Spark presence changes.
<li> <a href="api/org/jivesoftware/spark/ui/ContactItemHandler.html">ContactItemHandler</a> -- Allows the plugin to control the effects of presence changes within a ContactItem and the associated invoke call.
</ul>
<li> Components -- Spark contains many Swing components that will regularly be used during the creation of your plugin.  Some of the more commonly used components are :
<ul>
<li> <a href="api/org/jivesoftware/MainWindow.html">MainWindow</a> -- The frame containing the Contact List. You use MainWindow to add new tabs, menu items, or force focus.
<li> <a href="api/org/jivesoftware/spark/ui/ChatRoom.html">ChatRoom</a> -- The base abstract class of all chat rooms within Spark. Known implementations are ChatRoomImpl and GroupChatRoom.
<li> <a href="api/org/jivesoftware/spark/ui/ChatArea.html">ChatArea</a> -- The base chat viewer for both the TranscriptWindow and ChatInputEditor.
<li> <a href="api/org/jivesoftware/spark/ui/ContactList.html">ContactList</a> -- The ContactList UI in Spark.
<li> <a href="api/org/jivesoftware/spark/ui/ChatRoomButton.html">ChatRoomButton</a> -- The button that should be used to conform to the look and feel of other buttons within a ChatRoom.
</ul>
</ul>

<h3><a id="plugins">Structure of a Plugin</a></h3>

<p>Plugins are shipped as compressed JAR (Java Archive) files. The files in a plugin archive are as follows:</p>

<fieldset>
    <legend>Plugin Structure</legend>
<pre>myplugin.jar!/
 |- plugin.xml     &lt;- Plugin definition file
 |- libs/          &lt;- Contains all the class archives needed to run this plugin.

</pre>
</fieldset>

<p>
The <tt>plugin.xml</tt> file specifies the main Plugin class. A sample
file might look like the following:
</p>

<fieldset>
    <legend>Sample plugin.xml</legend>
<pre class="xml">
&lt;?xml version="1.0" encoding="UTF-8"?&gt;

  <span class="comment">&lt;!-- Google Plugin Structure --&gt;</span>
      &lt;plugin&gt;
        &lt;name&gt;Google Desktop Plugin&lt;/name&gt;
        &lt;class&gt;com.examples.plugins.GooglePlugin&lt;/class&gt;
        &lt;author&gt;Derek DeMoro&lt;/author&gt;
        &lt;version&gt;1.0&lt;/version&gt;
        &lt;description&gt;Enables users to find files and emails relating to users using Google Desktop technology.&lt;/description&gt;
        &lt;email&gt;ddman@jivesoftware.com&lt;/email&gt;
        &lt;minSparkVersion&gt;2.6.0&lt;/minSparkVersion&gt;
        &lt;os&gt;Windows,Linux,Mac&lt;/os&gt;
      &lt;/plugin&gt;
      
</pre>
</fieldset>

<h3>Installing your Plugin</h3>

<p>
You only need to drop your newly created <tt>jar file</tt> into the plugins directory of your Spark client install.

</p>

<fieldset>
    <legend>Directory Structure</legend>
<pre>Spark/
 |- plugins/     &lt;- Put your Sparkplug jar file here
 |- lib/       &lt;- The main classes and libraries needed to run Live Assistant
 |- resources/ &lt;- Contains other supportive documents and libraries
 |- docs/ &lt;- Help Manuals and the JavaDoc to help you develop your own plugins.
</pre>
</fieldset>


<p>Your plugin class must implement the
<tt><a href="api/org/jivesoftware/spark/plugin/plugin.html">Plugin</a></tt>

interface from the <a href="api/index.html">Spark Client API</a>. The Plugin interface has
methods for initializing and shutting down the plugin.
</p>

<h2><a id="gettingStarted">Getting Started Writing Sparkplugs</a></h2>
<p>
In order to build your own Sparkplugs, you will need the Spark source code, which can be acquired with svn from
<b>http://svn.igniterealtime.org/svn/repos/spark/</b> </p>
To build an example plugin follow <a href="sparkplug_compile.html">Spark Plugin Compile Guide</a>

<h3><a id="examples">Spark How-To's</a></h3>

<ul>
<li><a href="#simplePlugin">How do I create a simple plugin?</a>
<li><a href="#addTab">How do I add my own Tab to the Spark Workspace?</a>
<li><a href="#addContactListContextListener">How do I add a context menu listener to the contact list?</a>
<li><a href="#addChatRoomContextListener">How do I add my own ContextMenu Listener to a ChatRoom?</a>
<li><a href="#addMenu">How do I add my own Menu to Spark?</a>
<li><a href="#addButton">How do I add a button to a Chat Room?</a>
<li><a href="#addSearch">How do I add my own searching feature in Spark like the User Search or Google Search in Firefox?</a>
<li><a href="#interceptFile">How can I intercept a File Transfer request?</a>
<li><a href="#sendFile">How can I send a file to another user?</a>
<li><a href="#contactUI">How can I control the UI and event handling of a ContactItem?</a>
<li><a href="#changePresence">How can I be notified when the Spark user changes their presence?</a>
<li><a href="#messageFilter">How can I add a message filter?</a>
<li><a href="#createChatRoom">How can I create a person-to-person Chat Room?</a>
<li><a href="#createConferenceRoom">How can I create a public Conference room?</a>
<li><a href="#addPreferences">How can I add my own Preferences?</a>
<li><a href="#showAlert">How can I flash the chat frame, like when a new message comes in?</a>
</ul>

<h3 id="simplePlugin">How do I create a simple plugin?</h3>

<ol>
  <li>Implement Plugin.
</ol>

<fieldset>

    <legend>Simple Plugin</legend>
<pre class="java">
package org.jivesoftware.spark.examples;

import org.jivesoftware.spark.plugin.Plugin;

/**
 * Implements the Spark Plugin framework to display the different possibilities using
 * Spark.
 */
public class ExamplePlugin implements Plugin {

    /**
     * Called after Spark is loaded to initialize the new plugin.
     */
    public void initialize() {
        System.out.println("Welcome To Spark");

    }

    /**
     * Called when Spark is shutting down to allow for persistence of information
     * or releasing of resources.
     */
    public void shutdown() {

    }

    /**
     * Return true if the Spark can shutdown on users request.
     * @return true if Spark can shutdown on users request.
     */
    public boolean canShutDown() {
        return true;
    }

    /**
    * Is called when a user explicitly asked to uninstall this plugin.
    * The plugin owner is responsible to clean up any resources and
    * remove any components install in Spark.
    */
    public void uninstall(){
       // Remove all resources belonging to this plugin.
    }
}

</pre>
</fieldset>

<h3 id="addTab">How to add your own Tab to the Spark Workspace?</h3>

<ol>
  <li>Implement Plugin.
  <li>Retrieve the Workspace which is the UI for Spark.
  <li>Retrieve the WorkspacePane which is the Tabbed Pane used by Spark.
  <li>Add your own tab.
</ol>

<fieldset>

    <legend>Add a Tab to Spark</legend>
<pre class="java">
public class ExamplePlugin implements Plugin {
.
.
.
    /**
     * Adds a tab to Spark
     */
    private void addTabToSpark(){
         // Get Workspace UI from SparkManager
        Workspace workspace = SparkManager.getWorkspace();

        // Retrieve the Tabbed Pane from the WorkspaceUI.
        JTabbedPane tabbedPane = workspace.getWorkspacePane();

        // Add own Tab.
        tabbedPane.addTab("My Plugin", new JButton("Hello"));
    }
.
.
.
}
</pre>
</fieldset>

<h3 id="addContactListContextListener">How do I add a context menu listener to the contact list?</h3>

<ol>
  <li>Implement Plugin.
  <li>Retrieve the ContactList which is part of Spark's Workspace.
  <li>Add ContactListListener.
</ol>

<fieldset>

    <legend>Add a ContextMenu Listener to ContactList</legend>
<pre class="java">
  private void addContactListListener(){
         // Get Workspace UI from SparkManager
        Workspace workspace = SparkManager.getWorkspace();

        // Retrieve the ContactList from the Workspace
        ContactList contactList = workspace.getContactList();

        // Create an action to add to the Context Menu
        final Action sayHelloAction = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(SparkManager.getMainWindow(), "Welcome to Spark");
            }
        };

        sayHelloAction.putValue(Action.NAME, "Say Hello To Me");


        // Add own Tab.
        contactList.addContextMenuListener(new ContextMenuListener() {
            public void poppingUp(Object object, JPopupMenu popup) {
                if(object instanceof ContactItem){
                    popup.add(sayHelloAction);
                }
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        });
    }
</pre>
</fieldset>


<h3 id="addChatRoomContextListener">How do I add my own ContextMenu Listener to a ChatRoom</h3>

<ol>
  <li>Implement Plugin.
  <li>Add a ChatRoomListener to the ChatManager.
  <li>Get either the TranscriptWindow or ChatInputEditor from the ChatRoom.
  <li>Add a ContactMenuListener to the ChatArea.
</ol>

<fieldset>

    <legend>Add a ContextMenuListener to a ChatRoom, TranscriptWindow or ChatInputEditor</legend>
<pre class="java">
    private void addContactListenerToChatRoom() {
       // Retrieve a ChatManager from SparkManager
        ChatManager chatManager = SparkManager.getChatManager();

        final ContextMenuListener listener = new ContextMenuListener() {
            public void poppingUp(Object object, JPopupMenu popup) {
                final TranscriptWindow chatWindow = (TranscriptWindow)object;
                Action clearAction = new AbstractAction() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        try {
                            chatWindow.insert("My own text :)");
                        }
                        catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                };

                clearAction.putValue(Action.NAME, "Insert my own text");
                popup.add(clearAction);
            }

            public void poppingDown(JPopupMenu popup) {

            }

            public boolean handleDefaultAction(MouseEvent e) {
                return false;
            }
        };

        // Add a ChatRoomListener to the ChatManager to allow for notifications
        // when a room is being opened. Note: I will use a ChatRoomListenerAdapter for brevity.
        chatManager.addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(ChatRoom room) {
                room.getTranscriptWindow().addContextMenuListener(listener);
            }

            public void chatRoomLeft(ChatRoom room) {
                room.getTranscriptWindow().removeContextMenuListener(listener);
            }
        });
    }
</pre>
</fieldset>

<h3 id="addMenu">How do I add my own Menu to Spark?</h3>

<ol>
  <li>Implement Plugin.
  <li>Retrieve the MainWindow from SparkManager.
  <li>Either create a new Menu or add a MenuItem to one of the pre-existing menus.
</ol>

<fieldset>

    <legend>Add a Menu To Spark</legend>
<pre class="java">
    /**
     * Adds a new menu and child menu item to Spark.
     */
    private void addMenuToSpark(){
        // Retrieve the MainWindow UI from Spark.
        final MainWindow mainWindow = SparkManager.getMainWindow();

        // Create new Menu
        JMenu myPluginMenu = new JMenu("My Plugin Menu");

        // Create Action to test Menu install.
        Action showMessage = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(mainWindow, "Yeah, It works.");
            }
        };

        // Give the menu item a name.
        showMessage.putValue(Action.NAME, "Check if it works");

        // Add to Menu
        myPluginMenu.add(showMessage);

        // Add Menu To Spark
        mainWindow.getJMenuBar().add(myPluginMenu);
    }
</pre>
</fieldset>

<h3 id="addButton">How do I add a button to a Chat Room?</h3>

<ol>
  <li>Implement Plugin.
  <li>Add a ChatRoomListener to ChatManager.
  <li>When the room is opened, add your ChatRoomButton to the ToolBar of the ChatRoom.
</ol>

<fieldset>

    <legend>Add a button to a Chat Room</legend>
<pre class="java">
    /**
     * Adds a button to each Chat Room that is opened.
     */
    private void addChatRoomButton(){
        // Retrieve ChatManager from the SparkManager
        ChatManager chatManager = SparkManager.getChatManager();

        // Create a new ChatRoomButton.
        final ChatRoomButton button = new ChatRoomButton("Push Me");


        // Add to a new ChatRoom when the ChatRoom opens.
        chatManager.addChatRoomListener(new ChatRoomListenerAdapter() {
            public void chatRoomOpened(ChatRoom room) {
                room.getToolBar().addChatRoomButton(button);
            }

            public void chatRoomLeft(ChatRoom room) {
                room.getToolBar().removeChatRoomButton(button);
            }
        });
    }
</pre>
</fieldset>

<h3 id="addSearch">How do I add my own searching feature in Spark like the User Search or Google Search in Firefox?</h3>

<ol>
  <li>Implement Plugin.
  <li>Create a searchable object by implementing the Searchable interface.
  <li>Add the Searchable implementation to the SearchManager.
</ol>

<fieldset>

    <legend>Add a search feature to Spark like User Search or Google Search in Firefox</legend>
<pre class="java">
    /**
     * Called after Spark is loaded to initialize the new plugin.
     */
    public void initialize() {
        // Register new Searchable object "SearchMe" with the SearchManager.
        SearchManager searchManager = SparkManager.getSearchManager();
        searchManager.addSearchService(new SearchMe());
    } 
</pre>
See the SearchMe code below.
<pre class="java"> 
    
package org.jivesoftware.spark.examples;

import org.jivesoftware.spark.search.Searchable;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.resource.LaRes;

import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 * A simple example of how to integrate ones own search into Spark.
 */
public class SearchMe implements Searchable {

    /**
     * The icon to show in the search box.
     * @return the icon.
     */
    public Icon getIcon() {
        return LaRes.getImageIcon(LaRes.SMALL_AGENT_IMAGE);
    }

    /**
     * Returns the name of this search object that is displayed in the drop down box.
     * @return the name.
     */
    public String getName() {
        return &quot;Searches Nothing Really&quot;;
    }

    /**
     * Returns the text that should be displayed in grey when this searchable object
     * is initially selected.
     * @return the text.
     */
    public String getDefaultText() {
        return &quot;Click to search me.&quot;;
    }

    /**
     * Returns the text to display in the tooltip.
     * @return the tooltip text.
     */
    public String getToolTip() {
        return &quot;Shows an example of integrating ones own search into Spark.&quot;;
    }

    /**
     * Is called when a user hits &quot;Enter&quot; key.
     * @param query the query the user is searching for.
     */
    public void search(String query) {
        JOptionPane.showMessageDialog(SparkManager.getMainWindow(), &quot;Nothing Found :(&quot;);
    }
}
</pre>
</fieldset>

<h3 id="interceptFile">How can I intercept a File Transfer request?</h3>

<ol>
  <li>Implement Plugin.
  <li>Implement TransferListener.
  <li>Register your TransferListener.
</ol>

<fieldset>

    <legend>Intercept File Transfer Requests</legend>
<pre class="java">
    /**
     * Listen for incoming transfer requests and either handle them yourself, or pass them
     * off to be handled by the next listener. If no one handles it, then Spark will handle it.
     */
    private void addTransferListener(){

        SparkTransferManager transferManager = SparkManager.getTransferManager();

        transferManager.addTransferListener(new TransferListener() {
            public boolean handleTransfer(FileTransferRequest request) {
                // If I wanted to handle it, take the request, accept it and get the inputstream.
                
                // Otherwise, return false.
                return false;
            }
        });
    }
</pre>
</fieldset>

<h3 id="sendFile">How can I send a file to another user?</h3>

<ol>
  <li>Implement Plugin.
  <li>Get the full jid of the user via the UserManager.
  <li>Get the SparkTransferManager and send the file.
</ol>

<fieldset>

    <legend>Send a file to another user</legend>
<pre class="java">
    /**
     * Sends a file to a user in your ContactList.
     */
    private void sendFile(){
        // Retrieve SparkTransferManager from the SparkManager.
        SparkTransferManager transferManager = SparkManager.getTransferManager();

        // In order to send a file to a person, you will need to know their full Jabber
        // ID.

        // Retrieve the Jabber ID for a user via the UserManager. This can
        // return null if the user is not in the ContactList or is offline.
        UserManager userManager = SparkManager.getUserManager();
        String jid = userManager.getJIDFromNickname("Matt");
        if(jid != null){
            transferManager.sendFile(new File("MyFile.txt"), jid);
        }
    }
</pre>
</fieldset>

<h3 id="contactUI">How can I control the UI and event handling of a ContactItem?</h3>

<ol>
  <li>Implement Plugin.
  <li>Get the ContactList.
  <li>Get a ContactItem(s) based on a user's jid.
  <li>Add your own ContactItemHandler to the ContactItem.
</ol>

<fieldset>

    <legend>Control the UI and event handling of a ContactItem</legend>
<pre class="java">
    /**
     * Controls the UI of a ContactItem.
     */
    private void handleUIAndEventsOfContactItem(){

        ContactList contactList = SparkManager.getWorkspace().getContactList();

        ContactItem item = contactList.getContactItemByJID("paul@jivesoftware.com/spark");

        ContactItemHandler handler = new ContactItemHandler() {
            /**
             * Called when this users presence changes. You are responsible for changing the
             * icon (or not) of this contact item.
             * @param presence the users new presence.
             */
            public void handlePresence(Presence presence) {

            }

            /**
             * Is called when a user double-clicks the item.
             * @return true if you are handling the event.
             */
            public boolean handleDoubleClick() {
                return false;
            }
        };

        item.setHandler(handler);
    }
</pre>
</fieldset>


<h3 id="changePresence">How can I be notified when the Spark user changes their presence?</h3>

<ol>
  <li>Implement Plugin.
  <li>Get the SessionManager from SparkManager.
  <li>Add your own PresenceListener to SessionManager.
</ol>

<fieldset>

    <legend>Receive notification when the Spark user changes their presence</legend>
<pre class="java">
    /**
     * Allows a plugin to be notified when the Spark users changes their
     * presence.
     */
    private void addPersonalPresenceListener(){
        SessionManager sessionManager = SparkManager.getSessionManager();

        sessionManager.addPresenceListener(new PresenceListener() {

            /**
             * Spark user changed their presence.
             * @param presence the new presence.
             */
            public void presenceChanged(Presence presence) {
                
            }
        });
    }
</pre>
</fieldset>

<h3 id="messageFilter">How can I add a message filter?</h3>

<ol>
  <li>Implement Plugin.
  <li>Get the ChatManager from SparkManager.
  <li>Create an instance of Message Filter.
  <li>Register with the ChatManager.
</ol>

<fieldset>

    <legend>Adding own Message Filter</legend>
<pre class="java">
    /**
     * Installs a new MessageFilter.
     */
    private void installMessageFilter() {
        // Retrieve the ChatManager from SparkManager
        ChatManager chatManager = SparkManager.getChatManager();

        MessageFilter messageFilter = new MessageFilter() {
            public void filter(Message message) {
                String currentBody = message.getBody();
                currentBody = currentBody.replaceAll("bad words", "good words");
                message.setBody(currentBody);
            }
        };

        chatManager.addMessageFilter(messageFilter);

        // Just remember to remove your filter if need be.
    }
</pre>
</fieldset>

<h3 id="createChatRoom">How can I create a person-to-person Chat Room</h3>

<ol>
  <li>Implement Plugin.
  <li>Get the ChatManager from SparkManager.
  <li>Create a new ChatRoom using the ChatManager.
  <li>Optionally make it the active ChatRoom using the ChatContainer.
</ol>

<fieldset>

    <legend>Creating Person-to-Person Chat Room</legend>
<pre class="java">
    /**
     * Creates a person to person Chat Room and makes it the active chat.
     */
    private void createPersonToPersonChatRoom(){
        
        // Get the ChatManager from Sparkmanager
        ChatManager chatManager = SparkManager.getChatManager();
        
        // Create the room.
        ChatRoom chatRoom = chatManager.createChatRoom("don@jivesoftware.com", "Don The Man", "The Chat Title");
        
        // If you wish to make this the active chat room.
        
        // Get the ChatContainer (This is the container for all Chat Rooms)
        ChatContainer chatContainer = chatManager.getChatContainer();
        
        // Ask the ChatContainer to make this chat the active chat.
        chatContainer.activateChatRoom(chatRoom);
    }
</pre>
</fieldset>

<h3 id="createConferenceRoom">How can I create a public Conference room?</h3>

<ol>
  <li>Implement Plugin.
  <li>Get the ChatManager from SparkManager.
  <li>Create a new conference ChatRoom using the ChatManager.
  <li>Optionally make it the active ChatRoom using the ChatContainer.
</ol>

<fieldset>

    <legend>Creating a Conference Room</legend>
<pre class="java">
    /**
     * Creates a person to person Chat Room and makes it the active chat.
     */
    private void createConferenceRoom() {

        // Get the ChatManager from Sparkmanager
        ChatManager chatManager = SparkManager.getChatManager();

        Collection serviceNames = null;

        // Get the service name you wish to use.
        try {
            serviceNames = MultiUserChat.getServiceNames(SparkManager.getConnection());
        }
        catch (XMPPException e) {
            e.printStackTrace();
        }

        // Create the room.
        ChatRoom chatRoom = chatManager.createConferenceRoom("BusinessChat", (String)serviceNames.toArray()[0]);

        // If you wish to make this the active chat room.

        // Get the ChatContainer (This is the container for all Chat Rooms)
        ChatContainer chatContainer = chatManager.getChatContainer();

        // Ask the ChatContainer to make this chat the active chat.
        chatContainer.activateChatRoom(chatRoom);
    }
}
</pre>
</fieldset>


<h3 id="addPreferences">How can I add my own Preferences?</h3>

<ol>
  <li>Implement Plugin.
  <li>Create a class that implements Preference.
  <li>Create a UI to associate with the Preference.
  <li>Register your new Preference with the PreferenceManager.
</ol>

<fieldset>

    <legend>Creating a Preference</legend>
<ul>
<li>Create a class that implements Preference. 
<pre class="java">
/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date:  $
 *
 * Copyright (C) 1999-2005 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software.
 * Use is subject to license terms.
 */
package org.jivesoftware.spark.examples.preferences;

import org.jivesoftware.spark.preference.Preference;
import org.jivesoftware.resource.LaRes;

import javax.swing.Icon;
import javax.swing.JComponent;

public class MyPreferences implements Preference {

    private MyPreferenceUI ui;

    public MyPreferences(){
        ui = new MyPreferenceUI();
    }


    public String getTitle() {
        return "Example Preferences";
    }

    public Icon getIcon() {
        return LaRes.getImageIcon(LaRes.ADD_IMAGE_24x24);
    }

    public String getTooltip() {
        return "Example tooltip in preference dialog";
    }

    public String getListName() {
        return "Examples";
    }

    public String getNamespace() {
        return "EXAMPLE";
    }

    public JComponent getGUI() {
        return ui;
    }

    public void load() {
        // Would load persisted information from file or server and
        // set the UI appropriately.
        ui.setShowChatHistory(true);
    }

    public void commit() {
        // Would persist the current state of the preferences.
        boolean showChatHistory = ui.isChatHistoryShown();

    }

    public boolean isDataValid() {
        return true;
    }

    public String getErrorMessage() {
        return null;
    }

    public Object getData() {
        return null;
    }

    public void shutdown() {
        // Do nothing.
    }
}

</pre>

<li>Create a UI class that your Preference will use.
<pre class="java">
package org.jivesoftware.spark.examples.preferences;

import org.jivesoftware.spark.util.ResourceUtils;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import java.awt.FlowLayout;

/**
 * Demonstrates a simple panel used to display a UI that can be used as
 * the Preference UI in the Preferences Dialog. This panel shows a simple
 * UI with accessors for setting the preference values / persistence.
 */
public class MyPreferenceUI extends JPanel {

    private JCheckBox showChatHistory;

    /**
     * Creates the default panel using FlowLayout as the Layout. But
     * GridBagLayout is the really only true layout :)
     */
    public MyPreferenceUI() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        buildUI();
    }

    private void buildUI() {
        showChatHistory = new JCheckBox();

        // Use Mnemonics for the CheckBox using ResourceUtils.
        ResourceUtils.resButton(showChatHistory, "&Show Chat History in Chat Window");

        // Add Button
        add(showChatHistory);
    }

    /**
     * Sets the UI based on previous preferences.
     * @param show true if Chat History to show up.
     */
    public void setShowChatHistory(boolean show){
        showChatHistory.setSelected(show);
    }

    /**
     * Returns true if Chat History should be shown.
     * @return true if history shown.
     */
    public boolean isChatHistoryShown(){
        return showChatHistory.isSelected();
    }


}

</pre>
<li>Register Preference class with Preference Manager.
<pre class="java">
  public void addPreference(){
     PreferenceManager preferenceManager = SparkManager.getPreferenceManager();
     preferenceManager.addPreference(new MyPreferences());
  }
</pre>
</ul>
</fieldset>



<h3 id="showAlert">How to show an alert, like when a new message comes in?</h3>

<fieldset><legend>How to show an alert, like when a new message comes in?</legend>

<pre class="java">
    // Get the ChatContainer from the ChatManager.
    
     ChatContainer chatContainer = ChatManager.getChatContainer();
     
     // Get the room you wish to be notified.
     ChatRoom chatRoom = chatContainer.getActiveChatRoom();
     chatContainer.startFlashing(chatRoom);
</pre>

</fieldset>

</body>
</html>


