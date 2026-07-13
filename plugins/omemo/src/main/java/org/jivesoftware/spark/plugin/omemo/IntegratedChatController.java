package org.jivesoftware.spark.plugin.omemo;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.ui.ChatInputEditor;
import org.jivesoftware.spark.ui.MessageEventListener;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension;
import org.jivesoftware.smackx.omemo.element.OmemoElement;
import org.jivesoftware.smackx.omemo.element.OmemoElement_VAxolotl;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;

/**
 * Intègre OMEMO directement dans une conversation privée Spark.
 * <p>
 * Le bouton et la touche Entrée sont interceptés uniquement lorsque le
 * cadenas OMEMO est actif. En mode désactivé, Spark conserve son comportement
 * natif.
 */
public final class IntegratedChatController implements MessageEventListener {
    private static final String ENTER_ACTION = "avhiral-omemo-send-enter";

    private final ChatRoomImpl room;
    private final AvhOmemoPlugin plugin;
    private final ChatInputEditor editor;
    private final JButton sendButton;
    private final JToggleButton lockButton;

    private final ActionListener[] originalSendListeners;
    private final Object originalEnterActionKey;
    private final Action originalEnterAction;

    private volatile boolean enabled;
    private volatile boolean operationInProgress;

    public IntegratedChatController(
        ChatRoomImpl room,
        AvhOmemoPlugin plugin) {

        this.room = room;
        this.plugin = plugin;
        this.editor = room.getChatInputEditor();
        this.sendButton = room.getSendButton();

        this.originalSendListeners = sendButton.getActionListeners();

        InputMap inputMap = editor.getInputMap(JComponent.WHEN_FOCUSED);

        ActionMap actionMap = editor.getActionMap();

        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");

        this.originalEnterActionKey = inputMap.get(enter);
        this.originalEnterAction = originalEnterActionKey == null ? null : actionMap.get(originalEnterActionKey);

        this.lockButton = createLockButton();

        installSendInterception();
        installEnterInterception();
        room.addMessageEventListener(this);

        // addEditorComponent place le composant dans la barre inférieure
        // de saisie, au même niveau que les autres outils de Spark.
        room.addEditorComponent(lockButton);
        updateVisualState();
    }

    private JToggleButton createLockButton() {
        final JToggleButton button = new JToggleButton("🔓 OMEMO");
        button.setToolTipText("Activer le chiffrement OMEMO pour cette conversation");
        button.setFocusable(false);
        button.addActionListener(event -> {
            if (button.isSelected()) {
                enableOmemo();
            } else {
                enabled = false;
                updateVisualState();
            }
        });
        return button;
    }

    private void installSendInterception() {
        for (ActionListener listener : originalSendListeners) {
            sendButton.removeActionListener(listener);
        }

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (enabled) {
                    sendEncryptedFromEditor();
                } else {
                    invokeOriginalSend(event);
                }
            }
        });
    }

    private void installEnterInterception() {
        final InputMap inputMap = editor.getInputMap(JComponent.WHEN_FOCUSED);
        final ActionMap actionMap = editor.getActionMap();
        final KeyStroke enter = KeyStroke.getKeyStroke("ENTER");

        inputMap.put(enter, ENTER_ACTION);

        actionMap.put(ENTER_ACTION, new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                if (enabled) {
                    sendEncryptedFromEditor();
                } else if (originalEnterAction != null) {
                    originalEnterAction.actionPerformed(event);
                } else {
                    invokeOriginalSend(event);
                }
            }
        });
    }

    private void enableOmemo() {
        operationInProgress = true;
        updateVisualState();
        new SwingWorker<Void, Void>() {
            private Throwable failure;

            @Override
            protected Void doInBackground() {
                try {
                    /*
                     * Ne pas appeler requestDeviceListUpdateFor() ici.
                     *
                     * Sur Openfire, certains nœuds PEP peuvent être renvoyés
                     * sous forme de SimplePayload. Smack 4.4.6 tente alors de
                     * les convertir directement en OmemoDeviceListElement,
                     * ce qui provoque la ClassCastException observée.
                     *
                     * OmemoManager.encrypt() effectue lui-même la résolution
                     * des appareils au moment utile. L'activation du cadenas
                     * ne doit donc qu'initialiser le runtime local.
                     */
                    plugin.ensureRuntime();
                    enabled = true;
                } catch (Throwable error) {
                    failure = error;
                    enabled = false;
                }
                return null;
            }

            @Override
            protected void done() {
                operationInProgress = false;
                updateVisualState();
                if (failure != null) {
                    plugin.reportError("Activation OMEMO impossible", failure);
                    JOptionPane.showMessageDialog(room,
                        "Activation OMEMO impossible.\n\n"
                            + plugin.safeMessage(failure)
                            + "\n\nAucun message en clair ne sera envoyé.",
                        "AVHIRAL OMEMO",
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    lockButton.setToolTipText(
                        "OMEMO actif. La liste des appareils sera "
                            + "résolue automatiquement lors de l'envoi.");
                }
            }
        }.execute();
    }

    private void sendEncryptedFromEditor() {
        if (operationInProgress) {
            return;
        }
        final String body = editor.getText();
        if (body == null || body.trim().isEmpty()) {
            return;
        }
        operationInProgress = true;
        setInputEnabled(false);
        updateVisualState();

        new SwingWorker<Void, Void>() {
            private Throwable failure;

            @Override
            protected Void doInBackground() {
                try {
                    plugin.ensureRuntime();
                    plugin.getRuntime().sendEncrypted(room.getBareJid(), body);
                } catch (Throwable error) {
                    failure = error;
                }
                return null;
            }

            @Override
            protected void done() {
                operationInProgress = false;
                setInputEnabled(true);
                if (failure == null) {
                    editor.setText("");
                    room.addToTranscript("Moi 🔒", body, "#167D2D", new Date());
                    room.scrollToBottom();
                    lockButton.setToolTipText("Message envoyé avec chiffrement OMEMO.");
                } else {
                    plugin.reportError("Envoi OMEMO impossible", failure);
                    String details = plugin.safeMessage(failure);
                    if (plugin.isSimplePayloadCast(failure)) {
                        details =
                            "Le service PEP a renvoyé une ancienne charge "
                                + "OMEMO non typée. Réessaie dans quelques "
                                + "secondes ou demande au correspondant de "
                                + "republier ses appareils OMEMO.";
                    }

                    JOptionPane.showMessageDialog(
                        room,
                        "Envoi OMEMO impossible.\n\n"
                            + details
                            + "\n\nLe message n'a pas été envoyé en clair.",
                        "AVHIRAL OMEMO",
                        JOptionPane.ERROR_MESSAGE);
                }

                updateVisualState();
                editor.requestFocusInWindow();
            }
        }.execute();
    }

    private void invokeOriginalSend(ActionEvent event) {
        for (ActionListener listener : originalSendListeners) {
            listener.actionPerformed(event);
        }
    }

    private void setInputEnabled(boolean state) {
        editor.setEnabled(state);
        sendButton.setEnabled(state);
    }

    private void updateVisualState() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (operationInProgress) {
                    lockButton.setText("⌛ OMEMO");
                    lockButton.setForeground(new Color(185, 115, 0));
                    return;
                }
                if (enabled) {
                    lockButton.setSelected(true);
                    lockButton.setText("🔒 OMEMO");
                    lockButton.setForeground(new Color(20, 125, 45));
                    lockButton.setToolTipText("OMEMO actif — aucun repli en clair.");
                } else {
                    lockButton.setSelected(false);
                    lockButton.setText("🔓 OMEMO");
                    lockButton.setForeground(Color.DARK_GRAY);
                    lockButton.setToolTipText("Clique pour activer OMEMO.");
                }
            }
        });
    }


    /**
     * Spark affiche normalement le corps de repli OMEMO envoyé par Monal
     * (par exemple "This message is OMEMO encrypted"). Le listener Spark est
     * appelé avant l'insertion dans le transcript : on neutralise uniquement
     * ce corps de repli lorsque la stanza contient réellement une extension
     * OMEMO. Le texte déchiffré est ensuite inséré par OmemoRuntime.Listener.
     */
    @Override
    public void receivingMessage(Message message) {
        if (message == null) {
            return;
        }

        if (containsOmemoPayload(message)) {
            enabled = true;
            updateVisualState();
        }

        CarbonExtension carbon = CarbonExtension.from(message);
        if (carbon != null) {
            if (carbon.getForwarded() != null && carbon.getForwarded().getForwardedStanza() instanceof Message) {
                Message forwarded = carbon.getForwarded().getForwardedStanza();
                if (containsOmemoPayload(forwarded)) {
                    enabled = true;
                    updateVisualState();
                }
            }
        }
    }

    @Override
    public void sendingMessage(Message message) {
        // Les envois OMEMO sont effectués directement par OmemoRuntime.
    }

    private boolean containsOmemoPayload(Message message) {
        if (message == null) {
            return false;
        }
        if (message.hasExtension(OmemoElement.NAME_ENCRYPTED, OmemoElement_VAxolotl.NAMESPACE)) {
            return true;
        }

        /*
         * Smack 4.4.6 sait déchiffrer le namespace legacy Axolotl.
         * Ne pas masquer un éventuel corps de repli OMEMO 2 tant qu'aucun
         * moteur OMEMO 2 n'est présent : cela éviterait de perdre un message.
         */
        return false;
    }

    public void markSecureInbound() {
        enabled = true;
        updateVisualState();
    }

    public ChatRoom getRoom() {
        return room;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void displayIncomingSecureMessage(final String body) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Message decrypted = new Message();
                decrypted.setFrom(room.getBareJid());
                decrypted.setTo(SparkManager.getSessionManager().getUserBareAddress());
                decrypted.setType(Message.Type.chat);
                decrypted.setBody(body);

                /*
                 * addToTranscript(String,...) ne dessine rien à l'écran dans
                 * Spark 3.0.2 : cette méthode ne fait qu'alimenter la liste
                 * persistée. L'affichage réel doit passer par
                 * TranscriptWindow.insertMessage().
                 */
                room.getTranscriptWindow().insertMessage(
                    room.getRoomTitle() + " 🔒",
                    decrypted,
                    new Color(20, 125, 45));

                room.addToTranscript(decrypted, true);

                room.getTranscriptWindow().validate();
                room.getTranscriptWindow().repaint();
                room.scrollToBottom();
            }
        });
    }

    public void dispose() {
        room.removeMessageEventListener(this);
        sendButton.removeActionListener(sendButton.getActionListeners()[sendButton.getActionListeners().length - 1]);

        for (ActionListener listener : originalSendListeners) {
            sendButton.addActionListener(listener);
        }

        InputMap inputMap = editor.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = editor.getActionMap();
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");

        if (originalEnterActionKey != null) {
            inputMap.put(enter, originalEnterActionKey);
            if (originalEnterAction != null) {
                actionMap.put(originalEnterActionKey, originalEnterAction);
            }
        } else {
            inputMap.remove(enter);
        }
        room.removeEditorComponent(lockButton);
    }
}
