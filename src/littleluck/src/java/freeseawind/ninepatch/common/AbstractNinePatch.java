package freeseawind.ninepatch.common;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import freeseawind.ninepatch.common.Row.Type;

/**
 * Android ninepatch images analytic class.
 * 
 * @author freeseawind@github
 * @version 1.0
 *
 */
public abstract class AbstractNinePatch<T, E>
{
    /**
     * the patch pixel 0xFF000000
     */
    public static final int PATCH_PIXES = 0xFF000000;
    private int lastWidth;
    private int lastHeight;
    private int patchWidth;
    private int patchHeight;
    private int horizontalPatchNum;
    private int verticalPatchNum;
    private List<List<Row>> columns;
    private Padding padding;
    private T image;
    private RepeatType repeatType;

    public AbstractNinePatch(T image)
    {
        this(image, null);
    }
    
    public AbstractNinePatch(T image, RepeatType repeatType)
    {
        image = toCompatibleImage(image);
        
        countPatch(image);
        
        this.image = image;
        
        this.repeatType = repeatType;
    }

    /**
     *
     * @param g2d
     * @param x
     * @param y
     * @param scaledWidth
     * @param scaledHeight
     */
	public void drawNinePatch(E g2d, int x, int y, int scaledWidth, int scaledHeight)
	{
        if (scaledWidth <= 1 || scaledHeight <= 1)
        {
            return;
        }

        try
        {
            if(lastWidth != scaledWidth || lastHeight != scaledHeight)
            {
                lastWidth = scaledWidth;

                lastHeight = scaledHeight;

                resetData(scaledWidth, scaledHeight);
            }

            if(patchWidth == scaledWidth && patchHeight == scaledHeight)
            {
                drawImage(g2d, image, x, y, scaledWidth, scaledHeight);

                return;
            }

            translate(g2d, x, y);

            int startX = 0;
            int startY = 0;
            int minWidth = patchWidth;
            int minHeight = patchHeight;

            if(horizontalPatchNum > 1)
            {
                minWidth = (patchWidth / horizontalPatchNum);
            }

            if(verticalPatchNum > 1)
            {
                minHeight = (patchHeight / verticalPatchNum);
            }

            int columnCount = 0;

            // draw by row.
            for(List<Row> rows : columns)
            {
                int rowCount = 0;

                int height = patchHeight;

                boolean isFirst = true;

                int preRowHeight = 0;
                
                if(startY >= scaledHeight)
                {
                    break;
                }

                for(Row row : rows)
                {
                    Rectangle rect = row.getRectangle();

                    int width = rect.width;
                    
                    if(startX >= scaledWidth)
                    {
                        break;
                    }

                    if(Type.HORIZONTALPATCH == row.getType() || Type.TILEPATCH == row.getType())
                    {
                        width = (patchWidth - minWidth * (rowCount + 1));

                        if(width < minWidth)
                        {
                            width = patchWidth - (minWidth * rowCount);
                        }
                        else
                        {
                            width = minWidth;
                        }

                        rowCount++;
                    }
                    else if(Type.HORIZONTALPATCH == row.getType())
                    {
                        if(isFirst)
                        {
                            height = (patchHeight - minHeight * (columnCount + 1));

                            if(height < minHeight)
                            {
                                height = patchHeight - (minHeight * columnCount);
                            }
                            else
                            {
                                height = minHeight;
                            }

                            columnCount++;

                            isFirst = false;
                        }
                    }

                    if(Type.FIXED == row.getType())
                    {
                        drawImage(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, rect.width, rect.height);

                        startX += rect.width;

                        preRowHeight = rect.height;
                    }
                    else if(Type.HORIZONTALPATCH == row.getType())
                    {
                        drawImage(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, width, rect.height);

                        startX += width;

                        preRowHeight = rect.height;
                    }
                    else if(Type.VERTICALPATCH == row.getType())
                    {
                        drawImage(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, rect.width, height);

                        startX += rect.width;

                        preRowHeight = height;
                    }
                    else if(Type.TILEPATCH == row.getType())
                    {
                        if(repeatType != null)
                        {
                            repeatImage(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, width, height);
                        }
                        else
                        {
                            drawImage(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, width, height);
                        }

                        startX += width;

                        preRowHeight = height;
                    }
                }

                startX = 0;
                startY += preRowHeight;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            translate(g2d, -x, -y);
        }
    }

    /**
     * count ninePatch image column.
     * 
     * @param xRegions
     * @param yRegions
     * @return
     */
    public List<List<Row>> countColumn(NinePatchRegion xRegions, NinePatchRegion yRegions)
    {
        boolean isPatchY = false; // is patch area.
        int i = 0; // fixed area start index.
        int j = 0;// patch area start index.
        int patchNum = yRegions.getPatchRegions().size();
        int fixNum = yRegions.getFixRegions().size();

        Region yRegion = null; // break point

        List<List<Row>> columns = new LinkedList<List<Row>>();// row array.

        do
        {
            yRegion = null;

            if (isPatchY && patchNum >= j + 1)
            {
                yRegion = yRegions.getPatchRegions().get(j++);
            }

            if (!isPatchY && fixNum >= i + 1)
            {
                yRegion = yRegions.getFixRegions().get(i++);
            }

            if(yRegion != null)
            {
                columns.add(countRow(yRegion, xRegions, isPatchY));
            }

            isPatchY = !isPatchY;
        }
        while (yRegion != null);

        return columns;
    }

    /**
     * count ninePatch image row info.
     * 
     * @param yRegion
     * @param xRegions
     * @param isPatchY
     * @return row array.
     */
    public List<Row> countRow(Region yRegion, NinePatchRegion xRegions, boolean isPatchY)
    {
        boolean isPatchX = false;
        int i = 0;
        int j = 0;
        int patchNum = xRegions.getPatchRegions().size();
        int fixNum = xRegions.getFixRegions().size();

        Region xRegion = null;

        List<Row> column = new LinkedList<Row>();

        do
        {
            xRegion = null;

            if (isPatchX && patchNum >= j + 1)
            {
                xRegion = xRegions.getPatchRegions().get(j++);
            }

            if (!isPatchX && fixNum >= i + 1)
            {
                xRegion = xRegions.getFixRegions().get(i++);
            }

            if(xRegion != null)
            {
                Row.Type rowType = getRowType(isPatchX, isPatchY);

                int height = yRegion.getEnd() - yRegion.getStart();

                int width = xRegion.getEnd() - xRegion.getStart();

                Rectangle rect = new Rectangle(xRegion.getStart() + 1, yRegion.getStart() + 1, width, height);

                Row row = new Row(rect, rowType);

                column.add(row);
            }

            isPatchX = !isPatchX;
        }
        while (xRegion != null);

        Collections.sort(column);

        return column;
    }



    /**
     * get content padding.
     * 
     * @param w content width.
     * @param content height.
     * @param xRegions x coordinates.
     * @param yRegions y coordinates.
     * @return
     */
    public Padding getPadding(int w, int h, List<Region> xRegions, List<Region> yRegions)
    {
        Region xRegion = xRegions.get(0);
        Region yRegion = yRegions.get(0);

        int left = xRegion.getStart();
        int top = yRegion.getStart();
        int right = w - xRegion.getEnd();
        int bottom = h - yRegion.getEnd();

        return new Padding(left, top, right, bottom);
    }

    /**
     * count area.
     * 
     * @param pixels pixels array.
     * @return
     */
    public NinePatchRegion getPatches(int[] pixels)
    {
        int start = 0;

        int lastPixel = pixels[0];

        List<Region> fixArea = new LinkedList<Region>();

        List<Region> patchArea = new LinkedList<Region>();

        for(int i = 1; i <= pixels.length; i++)
        {
            if(i < pixels.length && lastPixel == pixels[i])
            {
                continue;
            }

            Region region = new Region(start, i);

            if (PATCH_PIXES == lastPixel)
            {
                patchArea.add(region);
            }
            else
            {
                fixArea.add(region);
            }

            start = i;

            if(i < pixels.length)
            {
                lastPixel = pixels[i];
            }
        }

        // lose patch pixel.
        if(start == 0)
        {
            Region region = new Region(start, pixels.length);

            if (PATCH_PIXES == lastPixel)
            {
                patchArea.add(region);
            }
            else
            {
                fixArea.add(region);
            }
        }

        return new NinePatchRegion(fixArea, patchArea);
    }
    
    /**
     * repeat image.
     * 
     * @param g2d
     * @param image
     * @param x
     * @param y
     * @param sw
     * @param sh
     * @param dx
     * @param dy
     * @param dw
     * @param dh
     */
    public void repeatImage(E g2d,
                            T image,
                            int x,
                            int y,
                            int sw,
                            int sh,
                            int dx,
                            int dy,
                            int dw,
                            int dh)
    {
        if (repeatType == null)
        {
            return;
        }

        if (repeatType == RepeatType.HORIZONTAL)
        {
            int hornaizeW = dw;

            // 
            do
            {
                if (hornaizeW - sw < 0)
                {
                    sw = hornaizeW;
                }

                hornaizeW -= sw;

                drawImage(g2d, image, x, y, sw, sh, dx, dy, sw, dh);

                dx += sw;

            }
            while (hornaizeW > 0);
        }
        else if (repeatType == RepeatType.VERTICAL)
        {
            int verticalH = dh;

            do
            {
                if (verticalH - sh < 0)
                {
                    sh = verticalH;
                }

                verticalH -= sh;

                drawImage(g2d, image, x, y, sw, sh, dx, dy, dw, sh);

                dy += sh;

            }
            while (verticalH > 0);
        }
    }
    
    /**
     * Analytic ninepatch image info. find fix area and patch area.
     * following show the different area:
     * <pre>
     * |1|   2  |3|
     * |4|   5  |6|
     * |7|   8  |9|
     * 
     * under normal conditions:
     * 
     * <li>1-3-7-9 is fixed area.</li>
     * <li>2-8 is horizontal tension area.</li>
     * <li>4-6 is vertical tension area. </li>
     * <li>5 is both.</li>
     *  </pre>
     * @param image the ninepatch image
     */
    protected void countPatch(T image)
    {
        // get image actual width
        int width = getImageWidth(image) - 2;

        // get image actual height
        int height = getImageHeight(image) - 2;

        int[] row = null;

        int[] column = null;

        // get left patch pixels.
        column = getPixels(image, 0, 1, 1, height);

        // count left area.
        NinePatchRegion left = getPatches(column);

        // get top patch pixels.
        row = getPixels(image, 1, 0, width, 1);

        // count top area.
        NinePatchRegion top = getPatches(row);

        // get horizontal patch number
        this.horizontalPatchNum = top.getPatchRegions().size();

        // get vertical patch number
        this.verticalPatchNum = left.getPatchRegions().size();

        // count total area.
        this.columns = countColumn(top, left);

        // get bottom patch pixels.
        row = getPixels(image, 1, height + 1, width, 1);

        // get right patch pixels.
        column = getPixels(image, width + 1, 1, 1, height);

        //
        NinePatchRegion bottom = getPatches(row);

        NinePatchRegion right = getPatches(column);

        // count content padding.
        this.padding = getPadding(width, height, bottom.getPatchRegions(), right.getPatchRegions());
    }
    
    protected T toCompatibleImage(T image)
    {
    	return image;
    }

    /**
     * get pixels from image.
     * @param img
     * @param start x coordinates. 
     * @param start y coordinates. 
     * @param w read the width.
     * @param h read the height.
     * @return pixels arrays.
     */
    public abstract int[] getPixels(T img, int x, int y, int w, int h);

    /**
     * get image width.
     * 
     * @param img
     * @return
     */
    public abstract int getImageWidth(T img);

    /**
     * get image height.
     * 
     * @param img
     * @return
     */
    public abstract int getImageHeight(T img);

    /**
     * translate image.
     * 
     * @param g2d
     * @param x
     * @param y
     */
    public abstract void translate(E g2d, int x, int y);

    /**
     *
     * @param g2d
     * @param image
     * @param x
     * @param y
     * @param scaledWidth
     * @param scaledHeight
     */
    public abstract void drawImage(E g2d,
                                   T image,
                                   int x,
                                   int y,
                                   int scaledWidth,
                                   int scaledHeight);

    /**
     *
     * @param g2d
     * @param image
     * @param sx
     * @param sy
     * @param sw
     * @param sh
     * @param dx
     * @param dy
     * @param dw
     * @param dh
     */
    public abstract void drawImage(E g2d,
                                   T image,
                                   int sx,
                                   int sy,
                                   int sw,
                                   int sh,
                                   int dx,
                                   int dy,
                                   int dw,
                                   int dh);

    /**
     * count actual patch width and height.
     *
     * @param scaleWidth
     * @param scaleHeight
     */
    private void resetData(int scaleWidth, int scaleHeight)
    {
        this.patchWidth = scaleWidth;
        this.patchHeight = scaleHeight;
        boolean isFirst = true;
        boolean isNewColumn = true;

        for(List<Row> rows : columns)
        {
            for(Row row : rows)
            {
                if(Type.FIXED == row.getType() && isFirst)
                {
                    patchWidth -= row.getRectangle().width;
                }

                // BUG FIX: lose type.
                if (Type.FIXED == row.getType() && isNewColumn)
                {
                    patchHeight -= row.getRectangle().height;

                    isNewColumn = false;
                }
            }

            isNewColumn = true;

            isFirst = false;
        }
    }

    /**
     * get center area type.
     * 
     * @param isPatchX
     * @param isPatchY
     * @return
     */
    private Type getRowType(boolean isPatchX, boolean isPatchY)
    {
        if (!isPatchX && !isPatchY)
        {
            return Type.FIXED;
        }

        if (!isPatchX && isPatchY)
        {
            return Type.VERTICALPATCH;
        }

        if (isPatchX && !isPatchY)
        {
            return Type.HORIZONTALPATCH;
        }

        return Type.TILEPATCH;
    }

    
    public Padding getPadding()
    {
        return padding;
    }
}
