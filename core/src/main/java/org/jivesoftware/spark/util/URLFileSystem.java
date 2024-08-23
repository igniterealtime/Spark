/**
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
package org.jivesoftware.spark.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;

/**
 * <code>URLFileSystem</code> class handles some of the most common
 * functionallity when working with URLs.
 *
 * @version 1.0, 03/12/14
 */

public class URLFileSystem {
    public static String getContents(URL url) {
        try {
            return getContents(url.openStream());
        }
        catch (IOException e) {
            return null;
        }
    }

    public static String getContents(InputStream is) {
        byte[] buffer = new byte[2048];
        int length;
        StringBuilder sb = new StringBuilder();
        try {
            while ((length = is.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, length));
            }
            return sb.toString();
        }
        catch (IOException e) {
            return null;
        }
    }

    public static String getContents(File file) {
        try {
            return getContents(file.toURI().toURL());
        }
        catch (MalformedURLException e) {
            return "";
        }
    }

    /**
     * Copies the contents at <CODE>src</CODE> to <CODE>dst</CODE>.
     *
     * @param src URL to copy to local file.
     * @param dst File to pull information from to copy to.
     * @throws IOException if there is an error during copy.
     */
    public static void copy(URL src, File dst) throws IOException {
        try (InputStream in = src.openStream();
             OutputStream out = new FileOutputStream(dst)) {
            dst.mkdirs();
            copy(in, out);
        }
    }

    /**
     * Common code for copy routines.  By convention, the streams are
     * closed in the same method in which they were opened.  Thus,
     * this method does not close the streams when the copying is done.
     *
     * @param in Source stream
     * @param out Destination stream
     * @throws IOException if there is an error during copy.
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        final byte[] buffer = new byte[4096];
        while (true) {
            final int bytesRead = in.read(buffer);
            if (bytesRead < 0) {
                break;
            }
            out.write(buffer, 0, bytesRead);
        }

        out.flush();
    }

    //--------------------------------------------------------------------------
    //  URLFileSystemHelper public API...
    //--------------------------------------------------------------------------

    /**
     * Returns a canonical form of the {@link URL}, if one is available.
     * <p/>
     * <p/>
     * The default implementation just returns the specified {@link URL}
     * as-is.
     *
     * @param url URL to convert.
     * @return Convert url.
     */
    public URL canonicalize(URL url) {
        return url;
    }

    /**
     * Tests whether the application can read the resource at the
     * specified {@link URL}.
     *
     * @return <CODE>true</CODE> if and only if the specified
     *         {@link URL} points to a resource that exists <EM>and</EM> can be
     *         read by the application; <CODE>false</CODE> otherwise.
     *
     * @param url URL to check if we can read from it.
     */
    public boolean canRead(URL url) {
        try {
            final URLConnection urlConnection = url.openConnection();
            return urlConnection.getDoInput();
        }
        catch (Exception e) {
            return false;
        }
    }


    /**
     * Tests whether the application can modify the resource at the
     * specified {@link URL}.
     *
     * @return <CODE>true</CODE> if and only if the specified
     *         {@link URL} points to a file that exists <EM>and</EM> the
     *         application is allowed to write to the file; <CODE>false</CODE>
     *         otherwise.
     *
     * @param url URL to check if we can write to.
     */
    public boolean canWrite(URL url) {
        try {
            final URLConnection urlConnection = url.openConnection();
            return urlConnection.getDoOutput();
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Tests whether the application can create the resource at the specified
     * {@link URL}.
     *
     * @return <CODE>true</CODE> if the resource at the specified {@link URL}
     *         exists or can be created; <CODE>false</CODE> otherwise.
     *
     * @param url URL to check if we can create things at.
     */
    public boolean canCreate(URL url) {
        return true;
    }

    /**
     * Tests whether the specified {@link URL} is valid. If the resource
     * pointed by the {@link URL} exists the method returns <CODE>true</CODE>.
     * If the resource does not exist, the method tests that all components
     * of the path can be created.
     *
     * @return <CODE>true</CODE> if the {@link URL} is valid.
     *
     * @param url URL to check for validity.
     */
    public boolean isValid(URL url) {
        if (exists(url)) {
            return true;
        }

        return canCreate(url);
    }

    /**
     * Returns <CODE>true</CODE> if the specified {@link URL} points to a
     * resource that currently exists; returns <CODE>false</CODE>
     * otherwise.<P>
     * <p/>
     * The default implementation simply returns <CODE>false</CODE>
     * without doing anything.
     *
     * @param url URL to test for existance
     * @return True if url exists
     */
    public static boolean exists(URL url) {
        return url2File(url).exists();
    }

    public static boolean mkdirs(URL url) {
        final File file = url2File(url);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }


    /**
     * Returns the name of the file contained by the {@link URL}, not
     * including any protocol, hostname authentication, directory path,
     * anchor, or query.  This simply returns the simple filename.  For
     * example, if you pass in an {@link URL} whose string representation
     * is:
     * <p/>
     * <BLOCKQUOTE><CODE>
     * protocol://host:1010/dir1/dir2/file.ext#anchor?query
     * </CODE></BLOCKQUOTE>
     * <p/>
     * the returned value is "<CODE>file.ext</CODE>" (without the
     * quotes).<P>
     * <p/>
     * The returned file name should only be used for display purposes
     * and not for opening streams or otherwise trying to locate the
     * resource indicated by the {@link URL}.
     *
     * @param url URL of resource to get filename of
     * @return File name determined
     */
    public static String getFileName(URL url) {
        if (url == null) {
            return "";
        }

        final String path = url.getPath();
        if (path.equals("/")) {
            return "/";
        }
        final int lastSep = path.lastIndexOf('/');
        if (lastSep == path.length() - 1) {
            final int lastSep2 = path.lastIndexOf('/', lastSep - 1);
            return path.substring(lastSep2 + 1, lastSep);
        }
        else {
            return path.substring(lastSep + 1);
        }
    }


    /**
     * Returns the number of bytes contained in the resource that the
     * specified {@link URL} points to.  If the length cannot be
     * determined, <CODE>-1</CODE> is returned.<P>
     * <p/>
     * The default implementation attempts to get the content length from
     * the {@link URLConnection} associated with the {@link URL}.  If that
     * fails for some reason (e.g. the resource does not exist, there was
     * some other an I/O exception, etc.), <CODE>-1</CODE> is returned.
     *
     * @see URLConnection
     * @param url URL to get length of resource of
     * @return Content length
     */
    public long getLength(URL url) {
        try {
            final URLConnection urlConnection = url.openConnection();
            return urlConnection.getContentLength();
        }
        catch (Exception e) {
            return -1;
        }
    }


    /**
     * Returns the name of the file contained by the {@link URL}, not
     * including any protocol, hostname authentication, directory path,
     * anchor, or query.  This simply returns the simple filename.  For
     * example, if you pass in an {@link URL} whose string representation
     * is:
     * <p/>
     * <BLOCKQUOTE><CODE>
     * protocol://host:1010/dir1/dir2/file.ext1.ext2#anchor?query
     * </CODE></BLOCKQUOTE>
     * <p/>
     * the returned value is "<CODE>file</CODE>" (without the quotes).<P>
     * <p/>
     * The returned file name should only be used for display purposes
     * and not for opening streams or otherwise trying to locate the
     * resource indicated by the {@link URL}.<P>
     * <p/>
     * The default implementation first calls {@link #getFileName(URL)} to
     * get the file name part.  Then all characters starting with the
     * first occurrence of '.' are removed.  The remaining string is then
     * returned.
     *
     * @param url URL of resource
     * @return Name for URL
     */
    public static String getName(URL url) {
        final String fileName = getFileName(url);
        final int firstDot = fileName.lastIndexOf('.');
        return firstDot > 0 ? fileName.substring(0, firstDot) : fileName;
    }


    /**
     * Returns the path part of the {@link URL}.
     * <p/>
     * The default implementation delegates to {@link URL#getPath()}.
     *
     * @param url URL of resource
     * @return Path of URL
     */
    public String getPath(URL url) {
        return url.getPath();
    }


    /**
     * Returns the path part of the {@link URL} without the last file
     * extension.  To clarify, the following examples demonstrate the
     * different cases that come up:
     * <p/>
     * <TABLE BORDER COLS=2 WIDTH="100%">
     * <TR>
     * <TD><CENTER>Path part of input {@link URL}</CENTER></TD>
     * <TD><CENTER>Output {@link String}</CENTER</TD>
     * </TR>
     * <TR>
     * <TD><CODE>/dir/file.ext</CODE></TD>
     * <TD><CODE>/dir/file</CODE></TD>
     * <TR>
     * <TR>
     * <TD><CODE>/dir/file.ext1.ext2</CODE></TD>
     * <TD><CODE>/dir/file.ext1</CODE></TD>
     * <TR>
     * <TR>
     * <TD><CODE>/dir1.ext1/dir2.ext2/file.ext1.ext2</CODE></TD>
     * <TD><CODE>/dir1.ext1/dir2.ext2/file.ext1</CODE></TD>
     * <TR>
     * <TR>
     * <TD><CODE>/file.ext</CODE></TD>
     * <TD><CODE>/file</CODE></TD>
     * <TR>
     * <TR>
     * <TD><CODE>/dir.ext/file</CODE></TD>
     * <TD><CODE>/dir.ext/file</CODE></TD>
     * <TR>
     * <TR>
     * <TD><CODE>/dir/file</CODE></TD>
     * <TD><CODE>/dir/file</CODE></TD>
     * <TR>
     * <TR>
     * <TD><CODE>/file</CODE></TD>
     * <TD><CODE>/file</CODE></TD>
     * <TR>
     * <TR>
     * <TD><CODE>/.ext</CODE></TD>
     * <TD><CODE>/</CODE></TD>
     * <TR>
     * </TABLE>
     * <p/>
     * The default implementation gets the path from {@link
     * #getPath(URL)} and then trims off all of the characters beginning
     * with the last "." in the path, if and only if the last "." comes
     * after the last "/" in the path.  If the last "." comes before
     * the last "/" or if there is no "." at all, then the entire path
     * is returned.
     *
     * @param url URL of resource
     * @return Path without extension
     */
    public String getPathNoExt(URL url) {
        final String path = getPath(url);
        final int lastSlash = path.lastIndexOf("/");
        final int lastDot = path.lastIndexOf(".");
        if (lastDot <= lastSlash) {
            //  When the lastDot < lastSlash, it means that one of the
            //  directories has an extension, but the filename itself has
            //  no extension.  In this case, returning the whole path is
            //  the correct behavior.
            //
            //  The only time that lastDot and lastSlash can be equal occurs
            //  when both of them are -1.  In that case, returning the whole
            //  path is the correct behavior.
            return path;
        }
        //  At this point, we know that lastDot must be non-negative, so
        //  we can return the whole path string up to the last dot.
        return path.substring(0, lastDot);
    }


    /**
     * Returns the platform-dependent String representation of the
     * {@link URL}; the returned string should be considered acceptable
     * for users to read.  In general, the returned string should omit
     * as many parts of the {@link URL} as possible.  For the "file"
     * protocol, therefore, the platform pathname should just be the
     * pathname alone (no protocol) using the appropriate file separator
     * character for the current platform.  For other protocols, it may
     * be necessary to reformat the {@link URL} string into a more
     * human-readable form.  That decision is left to each
     * <CODE>URLFileSystemHelper</CODE> implementor.
     * <p/>
     * The default implementation returns <CODE>url.toString()</CODE>.
     * If the {@link URL} is <CODE>null</CODE>, the empty string is
     * returned.
     *
     * @return The path portion of the specified {@link URL} in
     *         platform-dependent notation.  This value should only be used for
     *         display purposes and not for opening streams or otherwise trying
     *         to locate the document.
     *
     * @param url URL of resource
     */
    public String getPlatformPathName(URL url) {
        return url != null ? url.toString() : "";
    }

    /**
     * This "sanitizes" the specified string path by converting all
     * {@link File#separatorChar} characters to forward slash ('/').
     * Also, a leading forward slash is prepended if the path does
     * not begin with one.
     *
     * @param path Path to sanitize
     * @return Sanitized path
     */
    private static String sanitizePath(String path) {
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    public static File url2File(URL url) {
        final String path = url.getPath();
        return new File(path);
    }

    public static URL getParent(URL url) {
        final File file = url2File(url);
        final File parentFile = file.getParentFile();
        if (parentFile != null && !file.equals(parentFile)) {
            try {
                return parentFile.toURI().toURL();
            }
            catch (Exception ex) {
                return null;
            }
        }
        return null;
    }

    /**
     * Allows for the copying of entire directories/sub-dirs and their files.
     *
     * @param src the root directory to copy.
     * @param dst the destination directory.
     * @throws IOException thrown if there is an issue copying over the files.
     */
    public static void copyDir(File src, File dst) throws IOException {
        // Create the destination directory
        dst.mkdirs();

        // Loop through the files and directories in the source directory and copy them
        File[] files = src.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                copyFile(file, new File(dst, file.getName()));
            } else if (file.isDirectory()) {
                copyDir(file, new File(dst, file.getName()));
            }
        }
    }

    private static void copyFile(File src, File dst) throws IOException {
        FileChannel in = new FileInputStream(src).getChannel();
        FileChannel out = new FileOutputStream(dst).getChannel();

        // Fast and efficient way to copy a file
        in.transferTo(0, in.size(), out);

        in.close();
        out.close();
    }
}
