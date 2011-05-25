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
package org.jivesoftware.spark.ui;

import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.View;
import javax.swing.text.html.HTMLDocument;

import org.jivesoftware.spark.util.log.Log;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Used to print any item contained with a TextArea, such as a Chat.
 */
public class ChatPrinter implements Printable {
/*  DocumentRenderer prints objects of type Document. Text attributes, including
    fonts, color, and small icons, will be rendered to a printed page.
    DocumentRenderer computes line breaks, paginates, and performs other
    formatting.

    An HTMLDocument is printed by sending it as an argument to the
    print(HTMLDocument) method. A PlainDocument is printed the same way. Other
    types of documents must be sent in a JEditorPane as an argument to the
    print(JEditorPane) method. Printing Documents in this way will automatically
    display a print dialog.

    As objects which implement the Printable Interface, instances of the
    DocumentRenderer class can also be used as the argument in the setPrintable
    method of the PrinterJob class. Instead of using the print() methods
    detailed above, a programmer may gain access to the formatting capabilities
    of this class without using its print dialog by creating an instance of
    DocumentRenderer and setting the document to be printed with the
    setDocument() or setJEditorPane(). The Document may then be printed by
    setting the instance of DocumentRenderer in any PrinterJob.
*/
    private int currentPage = -1;               //Used to keep track of when
    //the page to print changes.

    private JEditorPane JEditorPane;            //Container to hold the
    //Document. This object will
    //be used to lay out the
    //Document for printing.

    private double pageEndY;                //Location of the current page
    //end.

    private double pageStartY;              //Location of the current page
    //start.

    private boolean scaleWidthToFit = true;     //boolean to allow control over
    //whether pages too wide to fit
    //on a page will be scaled.

/*    The DocumentRenderer class uses pFormat and pJob in its methods. Note
      that pFormat is not the variable name used by the print method of the
      DocumentRenderer. Although it would always be expected to reference the
      pFormat object, the print method gets its PageFormat as an argument.
*/
    private PageFormat pFormat;
    private PrinterJob pJob;

    /**
     * The constructor initializes the pFormat and PJob variables.
     */
    public ChatPrinter() {
        pFormat = new PageFormat();
        pJob = PrinterJob.getPrinterJob();
    }

    /**
     * Method to get the current Document.
     *
     * @return the Chat document object.
     */
    public Document getDocument() {
        if (JEditorPane != null)
            return JEditorPane.getDocument();
        else
            return null;
    }

    /**
     * Method to get the current choice the width scaling option.
     *
     * @return true if it should scale the width.
     */
    public boolean getScaleWidthToFit() {
        return scaleWidthToFit;
    }

    /**
     * pageDialog() displays a page setup dialog.
     */
    public void pageDialog() {
        pFormat = pJob.pageDialog(pFormat);
    }

    /**
     * may be called to render a page more than once, each page is painted in
     * order. We may, therefore, keep track of changes in the page being rendered
     * by setting the currentPage variable to equal the pageIndex, and then
     * comparing these variables on subsequent calls to this method. When the two
     * variables match, it means that the page is being rendered for the second or
     * third time. When the currentPage differs from the pageIndex, a new page is
     * being requested.
     * <p/>
     * The highlights of the process used print a page are as follows:
     * <p/>
     * I.    The Graphics object is cast to a Graphics2D object to allow for
     * scaling.
     * II.   The JEditorPane is laid out using the width of a printable page.
     * This will handle line breaks. If the JEditorPane cannot be sized at
     * the width of the graphics clip, scaling will be allowed.
     * III.  The root view of the JEditorPane is obtained. By examining this root
     * view and all of its children, printView will be able to determine
     * the location of each printable element of the document.
     * IV.   If the scaleWidthToFit option is chosen, a scaling ratio is
     * determined, and the graphics2D object is scaled.
     * V.    The Graphics2D object is clipped to the size of the printable page.
     * VI.   currentPage is checked to see if this is a new page to render. If so,
     * pageStartY and pageEndY are reset.
     * VII.  To match the coordinates of the printable clip of graphics2D and the
     * allocation rectangle which will be used to lay out the views,
     * graphics2D is translated to begin at the printable X and Y
     * coordinates of the graphics clip.
     * VIII. An allocation Rectangle is created to represent the layout of the
     * Views.
     * <p/>
     * The Printable Interface always prints the area indexed by reference
     * to the Graphics object. For instance, with a standard 8.5 x 11 inch
     * page with 1 inch margins the rectangle X = 72, Y = 72, Width = 468,
     * and Height = 648, the area 72, 72, 468, 648 will be painted regardless
     * of which page is actually being printed.
     * <p/>
     * To align the allocation Rectangle with the graphics2D object two
     * things are done. The first step is to translate the X and Y
     * coordinates of the graphics2D object to begin at the X and Y
     * coordinates of the printable clip, see step VII. Next, when printing
     * other than the first page, the allocation rectangle must start laying
     * out in coordinates represented by negative numbers. After page one,
     * the beginning of the allocation is started at minus the page end of
     * the prior page. This moves the part which has already been rendered to
     * before the printable clip of the graphics2D object.
     * <p/>
     * X.    The printView method is called to paint the page. Its return value
     * will indicate if a page has been rendered.
     * <p/>
     * Although public, print should not ordinarily be called by programs other
     * than PrinterJob.
     *
     * @param graphics   the Graphic Object used to print.
     * @param pageFormat the page formatter.
     * @param pageIndex  the page to print.
     * @return the page number printed.
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        double scale = 1.0;
        Graphics2D graphics2D;
        View rootView;
//  I
        graphics2D = (Graphics2D)graphics;
//  II
        JEditorPane.setSize((int)pageFormat.getImageableWidth(), Integer.MAX_VALUE);
        JEditorPane.validate();
//  III
        rootView = JEditorPane.getUI().getRootView(JEditorPane);
//  IV
        if ((scaleWidthToFit) && (JEditorPane.getMinimumSize().getWidth() >
                pageFormat.getImageableWidth())) {
            scale = pageFormat.getImageableWidth() /
                    JEditorPane.getMinimumSize().getWidth();
            graphics2D.scale(scale, scale);
        }
//  V
        graphics2D.setClip((int)(pageFormat.getImageableX() / scale),
                (int)(pageFormat.getImageableY() / scale),
                (int)(pageFormat.getImageableWidth() / scale),
                (int)(pageFormat.getImageableHeight() / scale));
//  VI
        if (pageIndex > currentPage) {
            currentPage = pageIndex;
            pageStartY += pageEndY;
            pageEndY = graphics2D.getClipBounds().getHeight();
        }
//  VII
        graphics2D.translate(graphics2D.getClipBounds().getX(),
                graphics2D.getClipBounds().getY());
//  VIII
        Rectangle allocation = new Rectangle(0,
                (int)-pageStartY,
                (int)(JEditorPane.getMinimumSize().getWidth()),
                (int)(JEditorPane.getPreferredSize().getHeight()));
//  X
        if (printView(graphics2D, allocation, rootView)) {
            return Printable.PAGE_EXISTS;
        }
        else {
            pageStartY = 0;
            pageEndY = 0;
            currentPage = -1;
            return Printable.NO_SUCH_PAGE;
        }
    }

    /**
     * print(HTMLDocument) is called to set an HTMLDocument for printing.
     *
     * @param htmlDocument the HtmlDocument to print.
     */
    public void print(HTMLDocument htmlDocument) {
        setDocument(htmlDocument);
        printDialog();
    }

    /**
     * print(JEditorPane) prints a Document contained within a JEditorPane.
     *
     * @param jedPane the JEditorPane to print.
     */
    public void print(JEditorPane jedPane) {
        setDocument(jedPane);
        printDialog();
    }

    /**
     * print(PlainDocument) is called to set a PlainDocument for printing.
     *
     * @param plainDocument the PlainDocument to print.
     */
    public void print(PlainDocument plainDocument) {
        setDocument(plainDocument);
        printDialog();
    }

    /**
     * A private method, printDialog(), displays the print dialog and initiates
     * printing in response to user input.
     */
    private void printDialog() {
        if (pJob.printDialog()) {
            pJob.setPrintable(this, pFormat);
            try {
                pJob.print();
            }
            catch (PrinterException printerException) {
                pageStartY = 0;
                pageEndY = 0;
                currentPage = -1;
                Log.error("Error Printing Document",printerException);
            }
        }
    }


    private boolean printView(Graphics2D graphics2D, Shape allocation,
                              View view) {
        boolean pageExists = false;
        Rectangle clipRectangle = graphics2D.getClipBounds();
        Shape childAllocation;
        View childView;

        if (view.getViewCount() > 0) {
            for (int i = 0; i < view.getViewCount(); i++) {
                childAllocation = view.getChildAllocation(i, allocation);
                if (childAllocation != null) {
                    childView = view.getView(i);
                    if (printView(graphics2D, childAllocation, childView)) {
                        pageExists = true;
                    }
                }
            }
        }
        else {
//  I
            if (allocation.getBounds().getMaxY() >= clipRectangle.getY()) {
                pageExists = true;
//  II
                if ((allocation.getBounds().getHeight() > clipRectangle.getHeight()) &&
                        (allocation.intersects(clipRectangle))) {
                    view.paint(graphics2D, allocation);
                }
                else {
//  III
                    if (allocation.getBounds().getY() >= clipRectangle.getY()) {
                        if (allocation.getBounds().getMaxY() <= clipRectangle.getMaxY()) {
                            view.paint(graphics2D, allocation);
                        }
                        else {
//  IV
                            if (allocation.getBounds().getY() < pageEndY) {
                                pageEndY = allocation.getBounds().getY();
                            }
                        }
                    }
                }
            }
        }
        return pageExists;
    }


    private void setContentType(String type) {
        JEditorPane.setContentType(type);
    }

    /**
     * Method to set an HTMLDocument as the Document to print.
     *
     * @param htmlDocument sets the html document.
     */
    public void setDocument(HTMLDocument htmlDocument) {
        JEditorPane = new JEditorPane();
        setDocument("text/html", htmlDocument);
    }

    /**
     * Method to set the Document to print as the one contained in a JEditorPane.
     * This method is useful when Java does not provide direct access to a
     * particular Document type, such as a Rich Text Format document. With this
     * method such a document can be sent to the DocumentRenderer class enclosed
     * in a JEditorPane.
     *
     * @param jedPane the JEditorPane document container.
     */
    public void setDocument(JEditorPane jedPane) {
        JEditorPane = new JEditorPane();
        setDocument(jedPane.getContentType(), jedPane.getDocument());
    }

    /**
     * Method to set a PlainDocument as the Document to print.
     *
     * @param plainDocument the PlainDocument to use.
     */
    public void setDocument(PlainDocument plainDocument) {
        JEditorPane = new JEditorPane();
        setDocument("text/plain", plainDocument);
    }

    private void setDocument(String type, Document document) {
        setContentType(type);
        JEditorPane.setDocument(document);
    }

    /**
     * Method to set the current choice of the width scaling option.
     *
     * @param scaleWidth the width to scale to.
     */
    public void setScaleWidthToFit(boolean scaleWidth) {
        scaleWidthToFit = scaleWidth;
    }
}
