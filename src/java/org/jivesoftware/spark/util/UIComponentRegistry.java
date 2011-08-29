package org.jivesoftware.spark.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.jivesoftware.LoginDialog;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
import org.jivesoftware.spark.ui.CommandPanel;
import org.jivesoftware.spark.ui.ContactGroup;
import org.jivesoftware.spark.ui.ContactInfoWindow;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.conferences.ConferenceServices;
import org.jivesoftware.spark.ui.status.StatusBar;
import org.jivesoftware.spark.ui.themes.ThemePanel;
import org.jivesoftware.spark.util.log.Log;

/**
 * This is a registry for components that may be replaced by plugins. Also
 * doubles as a factory to instantiate those components.
 *
 */
public final class UIComponentRegistry {
	// use Spark defaults, so without any plugins we still have Spark's
	// functionality
	private static Class<? extends ContactItem> contactItemClass = ContactItem.class;
	private static Class<? extends ContactInfoWindow> contactInfoWindowClass = ContactInfoWindow.class;
	private static Class<? extends ContactGroup> contactGroupClass = ContactGroup.class;
	private static Class<? extends ContactList> contactListClass = ContactList.class;
	private static Class<? extends StatusBar> statusBarClass = StatusBar.class;
	private static Class<? extends CommandPanel> commandPanelClass = CommandPanel.class;
	private static Class<? extends SparkTabbedPane> workspaceTabPaneClass = SparkTabbedPane.class;
	private static Class<? extends LoginDialog> loginDialogClass = LoginDialog.class;
	private static Class<? extends ThemePanel> themePanelClass = ThemePanel.class;
	private static Class<? extends ConferenceServices> conferenceServicesClass = ConferenceServices.class;

	private UIComponentRegistry() {
		// disable instantiation
	}

	/**
	 * Registers a new class implementing a contact item.
	 *
	 * @param clazz
	 */
	public static void registerLoginDialog(Class<? extends LoginDialog> clazz) {
		if (loginDialogClass != clazz) {
			Log.debug("Registering new contract item class: "
					+ clazz.getName());
			loginDialogClass = clazz;
		} else {
			Log.warning("Class " + clazz.getName() + " already registered.");
		}
	}

	/**
	 * Registers a new class implementing a contact item.
	 *
	 * @param clazz
	 */
	public static void registerContactItem(Class<? extends ContactItem> clazz) {
		if (contactItemClass != clazz) {
			Log.debug("Registering new contract item class: "
					+ clazz.getName());
			contactItemClass = clazz;
		} else {
			Log.warning("Class " + clazz.getName() + " already registered.");
		}
	}

	/**
	 * Registers a new class implementing a contact info window.
	 *
	 * @param clazz
	 */
	public static void registerContactInfoWindow(
			Class<? extends ContactInfoWindow> clazz) {
		if (contactInfoWindowClass != clazz) {
			Log.debug("Registering new contact info window class: "
					+ clazz.getName());
			contactInfoWindowClass = clazz;
		} else {
			Log.warning("Class " + clazz.getName() + " already registered.");
		}
	}

	/**
	 * Registers a new class implementing a contact group.
	 *
	 * @param clazz
	 */
	public static void registerContactGroup(Class<? extends ContactGroup> clazz) {
		if (contactGroupClass != clazz) {
			Log.debug("Registering new contact group class: "
					+ clazz.getName());
			contactGroupClass = clazz;
		} else {
			Log.warning("Class " + clazz.getName() + " already registered.");
		}
	}

	/**
	 * Registers a new class implementing a contact group.
	 *
	 * @param clazz
	 */
	public static void registerStatusBar(Class<? extends StatusBar> clazz) {
		if (statusBarClass != clazz) {
			Log.debug("Registering new status bar class: " + clazz.getName());
			statusBarClass = clazz;
		} else {
			Log.warning("Class " + clazz.getName() + " already registered.");
		}
	}

	/**
	 * Registers a new class implementing a command panel.
	 *
	 * @param clazz
	 */
	public static void registerCommandPanel(Class<? extends CommandPanel> clazz) {
		if (commandPanelClass != clazz) {
			Log.debug("Registering new command panel class: "
					+ clazz.getName());
			commandPanelClass = clazz;
		} else {
			Log.warning("Class " + clazz.getName() + " already registered.");
		}
	}

	/**
	 * Registers a new class implementing a contact group. Registers a new class
	 * implementing a contact list.
	 *
	 * @param clazz
	 */
	public static void registerContactList(Class<? extends ContactList> clazz) {
		if (contactListClass != clazz) {
			Log.debug("Registering new contact list class: " + clazz.getName());
			contactListClass = clazz;
		} else {
			Log.warning("Class " + clazz.getName() + " already registered.");
		}
	}

	/**
	 * Registers a new class implementing a theme panel.
	 *
	 * @param clazz
	 */
	public static void registerThemePanel(Class<? extends ThemePanel> clazz) {
		if (themePanelClass != clazz) {
			Log.debug("Registering new theme panel class: " + clazz.getName());
			themePanelClass = clazz;
		} else {
			Log.warning("Class " + clazz.getName() + " already registered.");
		}
	}

	/**
	 * Registers a new class implementing conference services.
	 *
	 * @param clazz
	 */
	public static void registerConferenceServices(Class<? extends ConferenceServices> clazz) {
		if (conferenceServicesClass != clazz) {
			Log.debug("Registering new conference services class: " + clazz.getName());
			conferenceServicesClass = clazz;
		} else {
			Log.warning("Class " + clazz.getName() + " already registered.");
		}
	}

	/**
	 * Creates a new contact item object.
	 *
	 * @param alias
	 * @param nickname
	 * @param fullyQualifiedJID
	 *
	 * @return A new instance of the class currently registered as contact item.
	 */
	public static ContactItem createContactItem(String alias, String nickname,
			String fullyQualifiedJID) {
		ContactItem instance = null;
		try {
			final Constructor<? extends ContactItem> ctor = contactItemClass
					.getDeclaredConstructor(String.class, String.class,
							String.class);
			instance = ctor.newInstance(alias, nickname, fullyQualifiedJID);
		} catch (final Exception e) {
			// not pretty but we're catching several exceptions we can do little
			// about
			Log.error(
					"Error calling constructor for "
							+ contactItemClass.getName(), e);
		}

		return instance;
	}

	/**
	 * Retrieves the contact info window instance (this is implemented as a
	 * singleton).
	 *
	 * @return The instance of the class currently registered as contact info
	 *         window.
	 */
	public static ContactInfoWindow getContactInfoWindow() {
		ContactInfoWindow instance = null;
		try {
			final Method m = contactInfoWindowClass.getMethod("getInstance");
			Log.debug(m.toString());
			final Object o = m.invoke(contactInfoWindowClass);
			Log.debug("Casting " + o.getClass().getName() + " to "
					+ contactInfoWindowClass.getName());
			instance = contactInfoWindowClass.cast(o);
		} catch (final Exception e) {
			// not pretty but we're catching 5 exceptions we can do little about
			Log.error(
					"Error calling getInstance for "
							+ contactInfoWindowClass.getName(), e);
		}

		return instance;
	}

	/**
	 * Creates a new contact group object.
	 *
	 * @param name
	 *
	 * @return A new instance of the class currently registered as contact
	 *         group.
	 */
	public static ContactGroup createContactGroup(String name) {
		ContactGroup instance = null;
		try {
			final Constructor<? extends ContactGroup> ctor = contactGroupClass
					.getDeclaredConstructor(String.class);
			instance = ctor.newInstance(name);
		} catch (final Exception e) {
			// not pretty but we're catching several exceptions we can do little
			// about
			Log.error(
					"Error calling constructor for "
							+ contactGroupClass.getName(), e);
		}

		return instance;
	}

	/**
	 * Creates a new contact group object
	 *
	 * @param name
	 *
	 * @return A new instance of the class currently registered as status bar.
	 */
	public static StatusBar createStatusBar() {
		StatusBar instance = null;
		try {
			final Constructor<? extends StatusBar> ctor = statusBarClass
					.getDeclaredConstructor();
			instance = ctor.newInstance();
		} catch (final Exception e) {
			// not pretty but we're catching several exceptions we can do little
			// about
			Log.error(
					"Error calling constructor for " + statusBarClass.getName(),
					e);
		}

		return instance;
	}

	/**
	 * Creates a new command panel object
	 *
	 * @param name
	 *
	 * @return A new instance of the class currently registered as status bar.
	 */
	public static CommandPanel createCommandPanel() {
		CommandPanel instance = null;
		try {
			final Constructor<? extends CommandPanel> ctor = commandPanelClass
					.getDeclaredConstructor();
			instance = ctor.newInstance();
		} catch (final Exception e) {
			// not pretty but we're catching several exceptions we can do little
			// about
			Log.error(
					"Error calling constructor for "
							+ commandPanelClass.getName(), e);
		}

		return instance;
	}

	/**
	 * Registers a new class implementing a tab panel, for use within the main
	 * application window.
	 *
	 * @param clazz
	 */
	public static void registerWorkspaceTabPanel(
			Class<? extends SparkTabbedPane> clazz) {
		if (workspaceTabPaneClass != clazz) {
			Log.debug("Registering new search panel class: "
					+ clazz.getName());
			workspaceTabPaneClass = clazz;
		} else {
			Log.warning("Class " + clazz.getName() + " already registered.");
		}
	}

	/**
	 * Creates a new workspace tab panel object
	 *
	 * @param name
	 *
	 * @return
	 */
	public static SparkTabbedPane createWorkspaceTabPanel(int tabPosition) {
		SparkTabbedPane instance = null;
		try {
			final Constructor<? extends SparkTabbedPane> ctor = workspaceTabPaneClass
					.getDeclaredConstructor(Integer.class);
			instance = ctor.newInstance(tabPosition);
		} catch (final Exception e) {
			// not pretty but we're catching several exceptions we can do little
			// about
			Log.error("Error calling constructor for "+ workspaceTabPaneClass.getName(), e);
		}

		return instance;
	}

	/**
	 * Creates a new login dialog panel object
	 *
	 * @param name
	 *
	 * @return
	 */
	public static LoginDialog createLoginDialog() {
		LoginDialog instance = null;
		try {
			final Constructor<? extends LoginDialog> ctor = loginDialogClass
					.getDeclaredConstructor();
			instance = ctor.newInstance();
		} catch (final Exception e) {
			// not pretty but we're catching several exceptions we can do little
			// about
			Log.error("Error calling constructor for "+ loginDialogClass.getName(), e);
		}

		return instance;
	}

	/**
	 * Creates a new contact list object.
	 *
	 * @return A new instance of the class currently registered as contact list.
	 */
	public static ContactList createContactList() {
		ContactList instance = null;
		try {
			final Constructor<? extends ContactList> ctor = contactListClass.getDeclaredConstructor();
			instance = ctor.newInstance();
		} catch (final Exception e) {
			// not pretty but we're catching several exceptions we can do little
			// about
			Log.error("Error calling constructor for " + contactListClass.getName(), e);
		}
		return instance;
	}

	/**
	 * Creates a new theme panel object.
	 *
	 * @return A new instance of the class currently registered as theme panel.
	 */
	public static ThemePanel createThemePanel() {
		ThemePanel instance = null;
		try {
			final Constructor<? extends ThemePanel> ctor = themePanelClass.getDeclaredConstructor();
			instance = ctor.newInstance();
		} catch (final Exception e) {
			// not pretty but we're catching several exceptions we can do little
			// about
			Log.error("Error calling constructor for " + themePanelClass.getName(), e);
		}
		return instance;
	}

	/**
	 * Creates a new conference services object.
	 *
	 * @return A new instance of the class currently registered as conference
	 *         services.
	 */
	public static ConferenceServices createConferenceServices() {
		ConferenceServices instance = null;
		try {
			final Constructor<? extends ConferenceServices> ctor = conferenceServicesClass.getDeclaredConstructor();
			instance = ctor.newInstance();
		} catch (final Exception e) {
			// not pretty but we're catching several exceptions we can do little
			// about
			Log.error("Error calling constructor for " + conferenceServicesClass.getName(), e);
		}
		return instance;
	}

}
