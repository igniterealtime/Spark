package org.jivesoftware.spark.filetransfer;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.sparkimpl.updater.AcceptAllCertsConnectionManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

public class HttpDownloader {

    private static final long MAX_FILE_SIZE = 1_048_576; // 1MB in bytes

    public static byte[] downloadContent(URI uri) {
        Log.debug("Start downloading " + uri.toString());
        try (final CloseableHttpClient httpClient =
                 HttpClients.custom().useSystemProperties()
                     .setConnectionManager(AcceptAllCertsConnectionManager.getInstance()) // FIXME: do not use acceptallcdertsconnectionmanager! It is unsafe. Only use trusted certificates!
                     .setDefaultRequestConfig(RequestConfig.custom().setResponseTimeout(SmackConfiguration.getDefaultReplyTimeout() / 10, TimeUnit.MILLISECONDS).build())
                     .build()
        ) {
            // First, make a pre-flight HEAD request to check the file size.
            // This also helps avoid calls to unsecure API that use GET method (e.g. routers cgi-bin scripts)
            final ClassicHttpRequest headRequest = ClassicRequestBuilder.head(uri)
                .setHeader("Accept", "image/*")
                .setHeader("User-Agent", "Spark HttpFileUpload")
                .build();

            Boolean sizeCheck = httpClient.execute(headRequest, response -> {
                if (response.getCode() != 200) {
                    // ok, that image is probably removed (404), forbidden (403), or this wasn't an image link but an API.
                    return false;
                }
                // Check the Content-Length
                Header contentLengthHeader = response.getFirstHeader("Content-Length");
                if (contentLengthHeader == null) {
                    return false;
                }
                long contentLength = Long.parseLong(contentLengthHeader.getValue());
                if (contentLength > MAX_FILE_SIZE || contentLength <= 0) {
                    Log.debug("File size " + contentLength + " bytes exceeds maximum limit of " + MAX_FILE_SIZE + " bytes for " + uri);
                    return false;
                }
                return true;
            });

            if (!sizeCheck) {
                Log.debug("File size check failed for " + uri);
                return null;
            }

            // Proceed with the GET request to download the file
            final ClassicHttpRequest request = ClassicRequestBuilder.get(uri)
                .setHeader("Accept", "image/*")
                .setHeader("User-Agent", "Spark HttpFileUpload")
                .build();

            byte[] contentBytes = httpClient.execute(request, response -> {
                if (response.getCode() != 200 || response.getEntity() == null) {
                    return null;
                }
                // double-check for the content length
                long contentLength = response.getEntity().getContentLength();
                if (contentLength > MAX_FILE_SIZE || contentLength <= 0) {
                    Log.debug("File size " + contentLength + " bytes exceeds maximum limit of " + MAX_FILE_SIZE + " bytes for " + uri);
                    return null;
                }
                byte[] content;
                try {
                    // First, read the content fully to avoid broken images
                    content = EntityUtils.toByteArray(response.getEntity());
                    return content;
                } catch (IOException e) {
                    Log.warning("Network error while loading picture from " + uri, e);
                    return null;
                } finally {
                    // if the connection is "prematurely closed," clean up the local resources
                    EntityUtils.consumeQuietly(response.getEntity());
                }
            });
            return contentBytes;
        } catch (Exception e) {
            Log.warning("Unable to download content from " + uri + ": " + e);
            return null;
        }
    }
}
