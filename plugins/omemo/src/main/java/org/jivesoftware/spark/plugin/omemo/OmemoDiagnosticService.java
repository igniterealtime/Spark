package org.jivesoftware.spark.plugin.omemo;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;

/**
 * Capture passive des stanzas XMPP pour diagnostiquer l'interopérabilité
 * OMEMO entre Spark/Smack 4.4.6 et les clients distants.
 * <p>
 * Cette classe ne modifie aucune stanza et ne journalise pas les clés privées.
 */
public final class OmemoDiagnosticService implements AutoCloseable {
    private static final String LEGACY_NAMESPACE = "eu.siacs.conversations.axolotl";
    private static final String OMEMO2_NAMESPACE = "urn:xmpp:omemo:2";

    private final Object fileLock = new Object();
    private final AbstractXMPPConnection connection;
    private final File logFile;

    private StanzaListener incomingListener;
    private StanzaListener outgoingListener;
    private volatile boolean running;

    public OmemoDiagnosticService(
        AbstractXMPPConnection connection,
        File logFile) {

        this.connection = connection;
        this.logFile = logFile;
    }

    public synchronized void start() {
        if (running) {
            return;
        }

        if (connection == null || !connection.isAuthenticated()) {
            throw new IllegalStateException("Connexion XMPP non authentifiée.");
        }

        incomingListener = new StanzaListener() {
            @Override
            public void processStanza(Stanza stanza) {
                logMessage("IN", stanza);
            }
        };

        outgoingListener = new StanzaListener() {
            @Override
            public void processStanza(Stanza stanza) {
                logMessage("OUT", stanza);
            }
        };

        connection.addAsyncStanzaListener(
            incomingListener,
            StanzaTypeFilter.MESSAGE);

        connection.addStanzaSendingListener(
            outgoingListener,
            StanzaTypeFilter.MESSAGE);

        running = true;

        appendSection(
            "DIAGNOSTIC START",
            "localJid=" + safe(connection.getUser()) + "\n"
                + "connectionClass="
                + connection.getClass().getName() + "\n"
                + providerStatus());
    }

    public synchronized void stop() {
        if (!running) {
            return;
        }

        if (incomingListener != null) {
            connection.removeAsyncStanzaListener(incomingListener);
        }

        if (outgoingListener != null) {
            connection.removeStanzaSendingListener(outgoingListener);
        }

        incomingListener = null;
        outgoingListener = null;
        running = false;

        appendSection("DIAGNOSTIC STOP", "Capture arrêtée.");
    }

    public boolean isRunning() {
        return running;
    }

    public File getLogFile() {
        return logFile;
    }

    public void clear() throws Exception {
        synchronized (fileLock) {
            if (logFile.exists() && !logFile.delete()) {
                throw new IllegalStateException("Impossible d'effacer : " + logFile.getAbsolutePath());
            }
        }
    }

    public void openLog() throws Exception {
        ensureParent();

        if (!logFile.exists()) {
            appendSection(
                "DIAGNOSTIC",
                "Journal créé manuellement.");
        }

        if (!Desktop.isDesktopSupported()) {
            throw new IllegalStateException(
                "Desktop.open n'est pas disponible. Journal : "
                    + logFile.getAbsolutePath());
        }

        Desktop.getDesktop().open(logFile);
    }

    public void logRuntimeEvent(String category, String details) {
        appendSection("RUNTIME " + category, details == null ? "(null)" : details);
    }

    public void logRuntimeError(String category, Throwable error) {
        StringBuilder report = new StringBuilder();
        Throwable current = error;
        int depth = 0;

        while (current != null && depth < 16) {
            report.append("cause[").append(depth).append("].class=")
                .append(current.getClass().getName())
                .append('\n');
            report.append("cause[").append(depth).append("].message=")
                .append(safe(current.getMessage()))
                .append('\n');

            current = current.getCause();
            depth++;
        }

        appendSection(
            "RUNTIME ERROR " + category,
            report.toString());
    }

    private void logMessage(
        String direction,
        Stanza stanza) {

        if (!(stanza instanceof Message)) {
            return;
        }

        Message message = (Message) stanza;
        StringBuilder report = new StringBuilder();

        report.append("direction=").append(direction).append('\n');
        report.append("messageClass=")
            .append(message.getClass().getName())
            .append('\n');
        report.append("from=").append(safe(message.getFrom())).append('\n');
        report.append("to=").append(safe(message.getTo())).append('\n');
        report.append("type=").append(safe(message.getType())).append('\n');
        report.append("stanzaId=")
            .append(safe(message.getStanzaId()))
            .append('\n');
        report.append("body=").append(safe(message.getBody())).append('\n');

        boolean legacyEncrypted = false;
        boolean omemo2Encrypted = false;
        boolean carbon = false;
        int extensionIndex = 0;

        for (var extension : message.getExtensions()) {
            extensionIndex++;

            String element = safe(extension.getElementName());
            String namespace = safe(extension.getNamespace());

            report.append("extension[")
                .append(extensionIndex)
                .append("].class=")
                .append(extension.getClass().getName())
                .append('\n');

            report.append("extension[")
                .append(extensionIndex)
                .append("].element=")
                .append(element)
                .append('\n');

            report.append("extension[")
                .append(extensionIndex)
                .append("].namespace=")
                .append(namespace)
                .append('\n');

            if ("encrypted".equals(element)
                && LEGACY_NAMESPACE.equals(namespace)) {
                legacyEncrypted = true;
            }

            if ("encrypted".equals(element)
                && OMEMO2_NAMESPACE.equals(namespace)) {
                omemo2Encrypted = true;
            }

            if ("sent".equals(element)
                || "received".equals(element)) {
                if ("urn:xmpp:carbons:2".equals(namespace)) {
                    carbon = true;
                }
            }
        }

        report.append("detected.legacyAxolotl=")
            .append(legacyEncrypted)
            .append('\n');
        report.append("detected.omemo2=")
            .append(omemo2Encrypted)
            .append('\n');
        report.append("detected.carbon=")
            .append(carbon)
            .append('\n');

        report.append("provider.legacy.encrypted=")
            .append(providerClass(
                "encrypted",
                LEGACY_NAMESPACE))
            .append('\n');
        report.append("provider.legacy.list=")
            .append(providerClass(
                "list",
                LEGACY_NAMESPACE))
            .append('\n');
        report.append("provider.legacy.bundle=")
            .append(providerClass(
                "bundle",
                LEGACY_NAMESPACE))
            .append('\n');
        report.append("provider.omemo2.encrypted=")
            .append(providerClass(
                "encrypted",
                OMEMO2_NAMESPACE))
            .append('\n');

        report.append("xml=")
            .append(safeXml(message))
            .append('\n');

        appendSection(
            "XMPP MESSAGE " + direction,
            report.toString());
    }

    private String providerStatus() {
        StringBuilder report = new StringBuilder();

        report.append("provider.legacy.encrypted=")
            .append(providerClass(
                "encrypted",
                LEGACY_NAMESPACE))
            .append('\n');
        report.append("provider.legacy.list=")
            .append(providerClass(
                "list",
                LEGACY_NAMESPACE))
            .append('\n');
        report.append("provider.legacy.bundle=")
            .append(providerClass(
                "bundle",
                LEGACY_NAMESPACE))
            .append('\n');
        report.append("provider.omemo2.encrypted=")
            .append(providerClass(
                "encrypted",
                OMEMO2_NAMESPACE))
            .append('\n');

        return report.toString();
    }

    private static String providerClass(
        String element,
        String namespace) {

        Object provider =
            ProviderManager.getExtensionProvider(
                element,
                namespace);

        return provider == null
            ? "(none)"
            : provider.getClass().getName();
    }

    private static String safeXml(Stanza stanza) {
        try {
            CharSequence xml = stanza.toXML((String) null);
            return xml == null
                ? "(null)"
                : xml.toString();
        } catch (Throwable error) {
            return "(XML impossible: "
                + error.getClass().getName()
                + ": "
                + safe(error.getMessage())
                + ")";
        }
    }

    private void appendSection(
        String title,
        String content) {

        synchronized (fileLock) {
            PrintWriter writer = null;

            try {
                ensureParent();

                writer = new PrintWriter(
                    new FileWriter(logFile, true));

                writer.println(
                    "============================================================");
                writer.println(
                    new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS",
                        Locale.ROOT).format(new Date()));
                writer.println(title);
                writer.print(content);

                if (!content.endsWith("\n")) {
                    writer.println();
                }

                writer.flush();
            } catch (Exception ignored) {
                // Le diagnostic ne doit jamais bloquer Spark.
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }

    private void ensureParent() {
        File parent = logFile.getParentFile();

        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    private static String safe(Object value) {
        return value == null
            ? "(null)"
            : String.valueOf(value);
    }

    @Override
    public void close() {
        stop();
    }
}
