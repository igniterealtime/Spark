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
package org.jivesoftware.fastpath.workspace.assistants;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;

import org.jivesoftware.fastpath.FastpathPlugin;
import org.jivesoftware.fastpath.FpRes;
import org.jivesoftware.fastpath.workspace.macros.MacrosEditor;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.workgroup.ext.macros.Macro;
import org.jivesoftware.smackx.workgroup.ext.macros.MacroGroup;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.util.ResourceUtils;
import org.jivesoftware.spark.util.log.Log;

/*
 * $RCSfile$
 * $Revision: 19484 $
 * $Date: 2005-08-11 08:34:10 -0700 (Thu, 11 Aug 2005) $
 *
 * Copyright (C) 1999-2008 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

/**
 * Creates a drop down menu list of all macros, both personal and global.
 */
public class ChatMacroMenu {
    private final JPopupMenu popup = new JPopupMenu();
    private final ChatRoom chatRoom;

    private final JMenuItem editMacros;

    /**
     * Create a new ChatMacroMenu to use in the specified ChatRoom.
     *
     * @param room the <code>ChatRoom</code> to use the menu with.
     */
    public ChatMacroMenu(ChatRoom room) {
        // Set the chat room we are working with.
        chatRoom = room;

        // Load global folder
        MacroGroup globalMacros = null;
        try {
            globalMacros = FastpathPlugin.getAgentSession().getMacros(true);
        }
        catch (XMPPException e) {
            // Not Global Macros Set
            Log.error("No global macros have been set.");
        }

        if (globalMacros != null) {
            JMenu globalMenu = new JMenu(FpRes.getString("menuitem.global.canned.responses"));
            addSubMenus(globalMenu, globalMacros.getMacroGroups());
            addMenuItems(globalMenu, globalMacros.getMacros());

            popup.add(globalMenu);

            // Iterator through objects and look for JMenu with JMenuItem
            Component[] components = globalMenu.getMenuComponents();
            final int no = components != null ? components.length : 0;

            if (no == 0) {
                addNoMacroItem(globalMenu);
            }
        }

        MacroGroup personalGroup = null;
        try {
            personalGroup = FastpathPlugin.getAgentSession().getMacros(false);
        }
        catch (XMPPException e) {
            Log.error("No personal macros set.");
        }

        if (personalGroup != null) {
            JMenu personalMenu = new JMenu(personalGroup.getTitle());

            addSubMenus(personalMenu, personalGroup.getMacroGroups());
            addMenuItems(personalMenu, personalGroup.getMacros());
            popup.add(personalMenu);

            Component[] components = personalMenu.getMenuComponents();
            final int noPersonal = components != null ? components.length : 0;
            for (int i = 0; i < noPersonal; i++) {
                Component comp = components[i];
                if (comp instanceof JMenu) {
                    JMenu jmenu = (JMenu)comp;
                    if (jmenu.getMenuComponentCount() == 0) {
                        addNoMacroItem(jmenu);
                    }
                }
            }

            if (noPersonal == 0) {
                addNoMacroItem(personalMenu);
            }
        }

        editMacros = new JMenuItem();
        ResourceUtils.resButton(editMacros, FpRes.getString("button.edit.canned.responses"));
        popup.addSeparator();
        popup.add(editMacros);

        editMacros.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MacrosEditor editor = new MacrosEditor();
                editor.showEditor(editMacros);
            }
        });


    }

    private void addSubMenus(JMenu menu, List<MacroGroup> macroGroups) {
        Iterator<MacroGroup> subFolders = macroGroups.iterator();
        while (subFolders.hasNext()) {
            MacroGroup folder = subFolders.next();
            JMenu subMenu = new JMenu(folder.getTitle());
            menu.add(subMenu);

            addMenuItems(subMenu, folder.getMacros());


            addSubMenus(subMenu, folder.getMacroGroups());

        }
    }

    private void addMenuItems(JMenu menu, List<Macro> macros) {
        Iterator<Macro> items = macros.iterator();
        while (items.hasNext()) {
            final Macro newItem = items.next();
            final JMenuItem item = new JMenuItem(newItem.getTitle());
            menu.add(item);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String text = newItem.getResponse() + " ";
                    try {
                        chatRoom.getChatInputEditor().insertText(text);
                    }
                    catch (BadLocationException e1) {
                        Log.error("Error inserting macro", e1);
                    }
                }
            });
        }
    }


    private void addNoMacroItem(JMenu menu) {
        final JMenuItem item = new JMenuItem(FpRes.getString("menuitem.no.entries"));
        item.setEnabled(false);
        menu.add(item);
    }

    /**
     * Show the macro menu.
     *
     * @param comp the parent component.
     * @param x    the mouse x pos.
     * @param y    the mouse y pos.
     */
    public void show(Component comp, int x, int y) {
        popup.show(comp, x, y);
    }


}
