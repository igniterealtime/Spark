package org.jivesoftware.spark.plugin.omemo;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomListener;
import org.jivesoftware.spark.ui.MessageFilter;
import org.jivesoftware.spark.ui.transctipt.TranscriptWindow;
import org.jivesoftware.spark.ui.transctipt.TranscriptWindowInterceptor;
import org.jivesoftware.spark.ui.rooms.ChatRoomImpl;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smackx.omemo.provider.OmemoBundleVAxolotlProvider;
import org.jivesoftware.smackx.omemo.provider.OmemoDeviceListVAxolotlProvider;
import org.jivesoftware.smackx.omemo.provider.OmemoVAxolotlProvider;
import org.jivesoftware.smackx.omemo.element.OmemoElement_VAxolotl;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;

import static org.jivesoftware.smackx.omemo.util.OmemoConstants.OMEMO_NAMESPACE_V_AXOLOTL;

public final class AvhOmemoPlugin
    implements Plugin, MessageFilter, ChatRoomListener, OmemoRuntime.Listener, OmemoFallbackSuppressor.Listener, TranscriptWindowInterceptor {

    private static final Logger LOG =
        Logger.getLogger(AvhOmemoPlugin.class.getName());

    private final Map<ChatRoom, IntegratedChatController> controllers =
        new HashMap<ChatRoom, IntegratedChatController>();

    private JMenu avhiralMenu;
    private OmemoRuntime runtime;
    private OmemoDiagnosticService diagnostic;
    private OmemoFallbackSuppressor fallbackSuppressor;

    @Override
    public void initialize() {
        /*
         * Spark ne charge pas automatiquement le fichier de configuration
         * Smack du module smack-omemo contenu dans un Sparkplug.
         *
         * Sans ces providers, les charges OMEMO et PEP sont décodées comme
         * SimplePayload. Conséquences :
         * - ClassCastException sur la liste des appareils ;
         * - aucun appel à OmemoMessageListener ;
         * - affichage du corps de repli
         *   "[This message is OMEMO encrypted]".
         *
         * L'enregistrement doit intervenir avant le traitement de toute
         * stanza entrante.
         */
        registerOmemoProviders();

        ChatManager.getInstance().addChatRoomListener(this);
        ChatManager.getInstance().addMessageFilter(this);
        ChatManager.getInstance().addTranscriptWindowInterceptor(this);
        installMenu();
        showInstallationNoticeOnce();
        warmUpRuntimeAsync();
    }

    private static void registerOmemoProviders() {
        final String legacyNamespace = OMEMO_NAMESPACE_V_AXOLOTL;
        ProviderManager.addExtensionProvider("encrypted", legacyNamespace, new OmemoVAxolotlProvider());
        ProviderManager.addExtensionProvider("list", legacyNamespace, new OmemoDeviceListVAxolotlProvider());
        ProviderManager.addExtensionProvider("bundle", legacyNamespace, new OmemoBundleVAxolotlProvider());

        LOG.info("Providers OMEMO legacy enregistrés dans Smack.");
    }

    /**
     * Initialise OMEMO silencieusement dès que Spark est authentifié.
     * <p>
     * Cela supprime le besoin d'ouvrir manuellement le diagnostic avant
     * d'utiliser le cadenas dans une conversation.
     */
    private void warmUpRuntimeAsync() {
        Thread worker = new Thread(() -> {
            for (int attempt = 0; attempt < 30; attempt++) {
                try {
                    AbstractXMPPConnection connection =
                        SparkManager.getConnection();

                    if (connection != null
                        && connection.isAuthenticated()) {
                        installFallbackSuppressor(connection);
                        ensureRuntime();
                        startWireDiagnosticSilently();
                        LOG.info("Runtime OMEMO préinitialisé.");
                        return;
                    }

                    Thread.sleep(1000L);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (Throwable error) {
                    /*
                     * Le préchauffage ne doit jamais bloquer Spark
                     * ni afficher une fenêtre. L'erreur reste
                     * disponible dans le journal, puis le clic sur
                     * le cadenas pourra réessayer.
                     */
                    reportError(
                        "Préinitialisation OMEMO impossible",
                        error);
                    return;
                }
            }
        },
            "AVHIRAL-OMEMO-Warmup");

        worker.setDaemon(true);
        worker.start();
    }

    private void installMenu() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                avhiralMenu = new JMenu("AVHIRAL");
                JMenuItem diagnostic = new JMenuItem("Diagnostic OMEMO");
                diagnostic.addActionListener(
                    new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(
                            java.awt.event.ActionEvent event) {
                            showDiagnostics();
                        }
                    });

                JMenuItem purge = new JMenuItem("Republier mes appareils OMEMO");
                purge.addActionListener(event -> purgeOwnDevices());
                JMenuItem startDiagnostic = new JMenuItem("Démarrer diagnostic filaire");

                startDiagnostic.addActionListener(event -> startWireDiagnostic());

                JMenuItem stopDiagnostic = new JMenuItem("Arrêter diagnostic filaire");
                stopDiagnostic.addActionListener(event -> stopWireDiagnostic());

                JMenuItem openDiagnostic = new JMenuItem("Ouvrir journal filaire");
                openDiagnostic.addActionListener(event -> openWireDiagnostic());

                JMenuItem clearDiagnostic = new JMenuItem("Effacer journal filaire");
                clearDiagnostic.addActionListener(event -> clearWireDiagnostic());

                avhiralMenu.add(diagnostic);
                avhiralMenu.add(purge);
                avhiralMenu.addSeparator();
                avhiralMenu.add(startDiagnostic);
                avhiralMenu.add(stopDiagnostic);
                avhiralMenu.add(openDiagnostic);
                avhiralMenu.add(clearDiagnostic);

                SparkManager.getMainWindow().getJMenuBar().add(avhiralMenu);
                SparkManager.getMainWindow().getJMenuBar().revalidate();
                SparkManager.getMainWindow().getJMenuBar().repaint();
            }
        });
    }

    @Override
    public void chatRoomOpened(ChatRoom room) {
        attachRoom(room);
    }

    @Override
    public void chatRoomActivated(ChatRoom room) {
        attachRoom(room);
    }

    private synchronized void attachRoom(ChatRoom room) {
        if (!(room instanceof ChatRoomImpl)) {
            return;
        }
        if (controllers.containsKey(room)) {
            return;
        }
        IntegratedChatController controller = new IntegratedChatController((ChatRoomImpl) room, this);
        controllers.put(room, controller);
    }

    @Override
    public synchronized void chatRoomClosed(ChatRoom room) {
        detachRoom(room);
    }

    @Override
    public synchronized void chatRoomLeft(ChatRoom room) {
        detachRoom(room);
    }

    private void detachRoom(ChatRoom room) {
        IntegratedChatController controller = controllers.remove(room);
        if (controller != null) {
            controller.dispose();
        }
    }

    @Override
    public void userHasJoined(ChatRoom room, String user) {
        // Non utilisé pour les conversations privées.
    }

    @Override
    public void userHasLeft(ChatRoom room, String user) {
        // Non utilisé pour les conversations privées.
    }


    private synchronized void installFallbackSuppressor(AbstractXMPPConnection connection) {
        if (fallbackSuppressor != null) {
            return;
        }
        fallbackSuppressor = new OmemoFallbackSuppressor(this);
        /*
         * Le listener synchrone intervient avant la chaîne asynchrone de Spark
         * qui ajoute le corps du message au transcript.
         */
        connection.addSyncStanzaListener(fallbackSuppressor, StanzaTypeFilter.MESSAGE);
        LOG.info("Suppresseur synchrone du fallback OMEMO installé.");
    }

    private synchronized void removeFallbackSuppressor() {
        AbstractXMPPConnection connection = SparkManager.getConnection();
        if (connection != null && fallbackSuppressor != null) {
            connection.removeSyncStanzaListener(fallbackSuppressor);
        }
        fallbackSuppressor = null;
    }

    @Override
    public void onFallbackSuppressed(String from, String stanzaId, String originalBody) {
        LOG.info("Fallback OMEMO supprimé : from=" + from + ", stanzaId=" + stanzaId);
        if (diagnostic != null) {
            diagnostic.logRuntimeEvent(
                "FALLBACK SUPPRESSED",
                "from=" + from + "\n"
                    + "stanzaId=" + stanzaId + "\n"
                    + "body=" + originalBody);
        }
    }

    public synchronized void ensureRuntime() throws Exception {
        if (runtime != null && runtime.isReady()) {
            return;
        }

        AbstractXMPPConnection connection =
            SparkManager.getConnection();

        if (connection == null || !connection.isAuthenticated()) {
            throw new IllegalStateException(
                "Spark n'est pas authentifié.");
        }

        installFallbackSuppressor(connection);
        File base = new File(
            System.getenv("APPDATA") != null
                ? System.getenv("APPDATA")
                : System.getProperty("user.home"));

        File store = new File(
            new File(base, "Spark"),
            "avhiral-omemo");

        runtime = new OmemoRuntime(connection, store);
        runtime.addListener(this);
        runtime.initialize();
    }

    public synchronized OmemoRuntime getRuntime() {
        return runtime;
    }


    private static boolean containsLegacyOmemo(MessageBuilder message) {
        return message != null && message.hasExtension(OmemoElement_VAxolotl.NAMESPACE);
    }

    private static boolean containsLegacyOmemo(Message message) {
        return message != null && message.hasExtension(OmemoElement_VAxolotl.NAMESPACE);
    }

    /**
     * Dernière barrière juste avant l'affichage graphique par
     * TranscriptWindow.insertMessage().
     */
    @Override
    public boolean isMessageIntercepted(TranscriptWindow window, String userid, Message message) {
        if (!containsLegacyOmemo(message)) {
            return false;
        }

        String body = message.getBody();
        if (body == null || "[This message is OMEMO encrypted]".equals(body.trim())) {
            if (diagnostic != null) {
                diagnostic.logRuntimeEvent(
                    "TRANSCRIPT FALLBACK BLOCKED",
                    "userid=" + userid + "\n"
                        + "stanzaId=" + message.getStanzaId() + "\n"
                        + "body=" + body);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onSecureMessage(final String from, final String body) {
        if (diagnostic != null) {
            diagnostic.logRuntimeEvent("DECRYPTED MESSAGE", "from=" + from + "\n" + "body=" + body);
        }
        SwingUtilities.invokeLater(() -> {
            try {
                EntityBareJid jid = JidCreate.entityBareFrom(from);
                ChatRoom existing = null;
                for (ChatRoom room : controllers.keySet()) {
                    if (room.getBareJid() != null
                        && room.getBareJid().equals(jid)) {
                        existing = room;
                        break;
                    }
                }
                if (existing == null) {
                    existing = ChatManager.getInstance().getChatRoom(jid);
                }

                attachRoom(existing);
                IntegratedChatController controller = controllers.get(existing);

                if (controller == null) {
                    for (Map.Entry<ChatRoom, IntegratedChatController> entry : controllers.entrySet()) {
                        if (entry.getKey().getBareJid() != null
                            && entry.getKey().getBareJid().equals(jid)) {
                            controller = entry.getValue();
                            break;
                        }
                    }
                }
                if (controller != null) {
                    controller.markSecureInbound();
                    controller.displayIncomingSecureMessage(body);
                } else {
                    reportError("Aucun contrôleur Spark trouvé pour " + jid,
                        new IllegalStateException("controllers=" + controllers.size()));
                }
            } catch (Throwable error) {
                reportError("Affichage du message OMEMO impossible", error);
            }
        });
    }

    @Override
    public void onStatus(String status) {
        LOG.info(status);
        if (diagnostic != null) {
            diagnostic.logRuntimeEvent("STATUS", status);
        }
    }

    public boolean isItemNotFound(Throwable error) {
        Throwable current = error;
        while (current != null) {
            String text = String.valueOf(current.getMessage()).toLowerCase();
            if (text.contains("item-not-found") || text.contains("item_not_found")) {
                return true;
            }
            current = current.getCause();
        }

        return false;
    }

    public boolean isSimplePayloadCast(Throwable error) {
        Throwable current = error;
        while (current != null) {
            String className = current.getClass().getName();
            String message = String.valueOf(current.getMessage());
            if ("java.lang.ClassCastException".equals(className)
                && message.contains("SimplePayload")
                && message.contains("OmemoDeviceListElement")) {
                return true;
            }
            current = current.getCause();
        }

        return false;
    }

    public String safeMessage(Throwable error) {
        if (error == null) {
            return "(erreur inconnue)";
        }
        String message = error.getMessage();
        return message == null || message.trim().isEmpty()
            ? error.getClass().getName()
            : message;
    }

    public void reportError(String context, Throwable error) {
        LOG.log(Level.SEVERE, context, error);
        if (diagnostic != null) {
            diagnostic.logRuntimeError(context, error);
        }

        File target = diagnosticFile();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(target, true));
            writer.println("==================================================");
            writer.println(context);
            writer.println("Java: " + System.getProperty("java.version"));
            writer.println("JVM: " + System.getProperty("java.vm.name"));
            writer.println("Error: " + error.getClass().getName());
            writer.println("Message: " + safeMessage(error));

            error.printStackTrace(writer);
            writer.flush();
        } catch (Exception logError) {
            LOG.log(Level.SEVERE, "Impossible d'écrire le diagnostic OMEMO", logError);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


    private File wireDiagnosticFile() {
        File base = new File(System.getenv("APPDATA") != null
            ? System.getenv("APPDATA")
            : System.getProperty("user.home"));

        File directory = new File(new File(base, "Spark"), "logs");

        return new File(directory, "avhiral-omemo-wire-0.13.log");
    }

    private synchronized void startWireDiagnosticSilently() {
        try {
            if (diagnostic != null && diagnostic.isRunning()) {
                return;
            }
            AbstractXMPPConnection connection = SparkManager.getConnection();
            if (connection == null || !connection.isAuthenticated()) {
                return;
            }
            diagnostic = new OmemoDiagnosticService(connection, wireDiagnosticFile());
            diagnostic.start();
            diagnostic.logRuntimeEvent("PLUGIN", "AVHIRAL Secure OMEMO 0.14.2 chargé.");
        } catch (Throwable error) {
            reportError("Démarrage diagnostic filaire impossible", error);
        }
    }

    private void startWireDiagnostic() {
        startWireDiagnosticSilently();

        JOptionPane.showMessageDialog(
            SparkManager.getMainWindow(),
            diagnostic != null && diagnostic.isRunning()
                ? "Capture filaire active.\n\n"
                  + wireDiagnosticFile().getAbsolutePath()
                : "Capture non démarrée : Spark n'est peut-être "
                  + "pas encore authentifié.",
            "Diagnostic OMEMO",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private synchronized void stopWireDiagnostic() {
        if (diagnostic != null) {
            diagnostic.stop();
        }

        JOptionPane.showMessageDialog(
            SparkManager.getMainWindow(),
            "Capture filaire arrêtée.",
            "Diagnostic OMEMO",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void openWireDiagnostic() {
        try {
            if (diagnostic == null) {
                diagnostic = new OmemoDiagnosticService(
                    SparkManager.getConnection(),
                    wireDiagnosticFile());
            }

            diagnostic.openLog();
        } catch (Throwable error) {
            reportError(
                "Ouverture du journal filaire impossible",
                error);

            JOptionPane.showMessageDialog(
                SparkManager.getMainWindow(),
                safeMessage(error)
                    + "\n\nJournal :\n"
                    + wireDiagnosticFile().getAbsolutePath(),
                "Diagnostic OMEMO",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearWireDiagnostic() {
        try {
            if (diagnostic == null) {
                diagnostic = new OmemoDiagnosticService(
                    SparkManager.getConnection(),
                    wireDiagnosticFile());
            }

            diagnostic.clear();

            JOptionPane.showMessageDialog(
                SparkManager.getMainWindow(),
                "Journal filaire effacé.",
                "Diagnostic OMEMO",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Throwable error) {
            reportError(
                "Effacement du journal filaire impossible",
                error);

            JOptionPane.showMessageDialog(
                SparkManager.getMainWindow(),
                safeMessage(error),
                "Diagnostic OMEMO",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDiagnostics() {
        String text = "Version : 0.14.2\n" +
            "Java : " +
            System.getProperty("java.version") +
            '\n' +
            "Spark connecté : " +
            (SparkManager.getConnection() != null
                && SparkManager.getConnection()
                .isAuthenticated()) +
            '\n' +
            "Runtime OMEMO prêt : " +
            (runtime != null && runtime.isReady()) +
            '\n' +
            "Conversations intégrées : " +
            controllers.size() +
            '\n' +
            "Suppresseur fallback actif : " +
            (fallbackSuppressor != null) +
            '\n' +
            "Capture filaire active : " +
            (diagnostic != null && diagnostic.isRunning()) +
            '\n' +
            "Journal filaire : " +
            wireDiagnosticFile().getAbsolutePath() +
            '\n' +
            "Rapport : " +
            diagnosticFile().getAbsolutePath();

        JOptionPane.showMessageDialog(
            SparkManager.getMainWindow(),
            text,
            "Diagnostic OMEMO",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void purgeOwnDevices() {
        try {
            ensureRuntime();
            runtime.purgeOwnDeviceList();

            JOptionPane.showMessageDialog(
                SparkManager.getMainWindow(),
                "La liste des appareils OMEMO a été republiée.",
                "AVHIRAL OMEMO",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Throwable error) {
            reportError(
                "Republication des appareils impossible",
                error);

            JOptionPane.showMessageDialog(
                SparkManager.getMainWindow(),
                safeMessage(error),
                "AVHIRAL OMEMO",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showInstallationNoticeOnce() {
        final File marker =
            installationNoticeMarker();

        if (marker.isFile()) {
            return;
        }

        File parent = marker.getParentFile();

        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try {
            FileWriter writer =
                new FileWriter(marker);

            try {
                writer.write(
                    "AVHIRAL Secure OMEMO 0.14.2 installed");
                writer.flush();
            } finally {
                writer.close();
            }
        } catch (Exception error) {
            LOG.log(
                Level.WARNING,
                "Impossible d'enregistrer l'avis d'installation.",
                error);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(
                    SparkManager.getMainWindow(),
                    "AVHIRAL Secure OMEMO 0.14.2 installé.\n"
                        + "Le cadenas apparaît dans les conversations privées.",
                    "AVHIRAL Secure OMEMO",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private static File installationNoticeMarker() {
        File base = new File(
            System.getenv("APPDATA") != null
                ? System.getenv("APPDATA")
                : System.getProperty("user.home"));

        File directory = new File(
            new File(base, "Spark"),
            "avhiral-omemo");

        return new File(
            directory,
            "installation-notice-0.14.2.flag");
    }

    private static File diagnosticFile() {
        File base = new File(
            System.getenv("APPDATA") != null
                ? System.getenv("APPDATA")
                : System.getProperty("user.home"));

        File directory = new File(
            new File(base, "Spark"),
            "logs");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return new File(
            directory,
            "avhiral-omemo-error.log");
    }

    @Override
    public void shutdown() {
        removeFallbackSuppressor();

        ChatManager.getInstance()
            .removeMessageFilter(this);
        ChatManager.getInstance()
            .removeTranscriptWindowInterceptor(this);
        ChatManager.getInstance()
            .removeChatRoomListener(this);

        for (IntegratedChatController controller
            : controllers.values()) {
            controller.dispose();
        }

        controllers.clear();

        if (runtime != null) {
            runtime.removeListener(this);
            runtime.close();
            runtime = null;
        }

        if (diagnostic != null) {
            diagnostic.close();
            diagnostic = null;
        }

        if (avhiralMenu != null) {
            SparkManager.getMainWindow()
                .getJMenuBar()
                .remove(avhiralMenu);

            SparkManager.getMainWindow()
                .getJMenuBar()
                .revalidate();

            SparkManager.getMainWindow()
                .getJMenuBar()
                .repaint();

            avhiralMenu = null;
        }
    }

    @Override
    public boolean canShutDown() {
        return true;
    }

    @Override
    public void uninstall() {
        shutdown();
    }

    @Override
    public void filterOutgoing(ChatRoom room, MessageBuilder messageBuilder) {
        // Les envois OMEMO sont gérés par OmemoRuntime.
    }

    /**
     * Filtre natif Spark. Il est exécuté dans ChatRoom.insertMessage() avant
     * l'ajout à l'historique de la conversation.
     */
    @Override
    public void filterIncoming(ChatRoom room, MessageBuilder messageBuilder) {
        if (!containsLegacyOmemo(messageBuilder)) {
            return;
        }
        String body = messageBuilder.getBody();
        if (body != null) {
            messageBuilder.setBody(null);
            if (diagnostic != null) {
                diagnostic.logRuntimeEvent(
                    "SPARK MESSAGE FILTER",
                    "room=" + room.getBareJid() + "\n"
                        + "stanzaId=" + messageBuilder.getStanzaId() + "\n"
                        + "removedBody=" + body);
            }
        }
    }
}
