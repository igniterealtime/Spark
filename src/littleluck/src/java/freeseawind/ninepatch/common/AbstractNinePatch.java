package freeseawind.ninepatch.common;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import freeseawind.ninepatch.common.Row.Type;

/**
 * @author freeseawind@github
 *
 */
public abstract class AbstractNinePatch<T, E>
{
    /**
     * 拉伸区域，像素值必须为黑色 0xFF000000
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
		// 修复BUG防止拉伸大小小于等于原图大小
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

            // 逐行绘制
            for(List<Row> rows : columns)
            {
                int rowCount = 0;

                int height = patchHeight;

                boolean isFirst = true;

                int preRowHeight = 0;
                
                // 防止图片拉伸高度大于实际需要拉伸高度
                if(startY >= scaledHeight)
                {
                    break;
                }

                for(Row row : rows)
                {
                    Rectangle rect = row.getRectangle();

                    int width = rect.width;
                    
                    // 防止图片拉伸宽度大于实际需要拉伸宽度
                    if(startX >= scaledWidth)
                    {
                        break;
                    }

                    if(Type.HORIZONTALPATCH == row.getType() || Type.TILEPATCH == row.getType())
                    {
                        // 计算拉伸的宽度
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
                        // 计算拉伸的高度
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

                    // 绘制固定区域
                    if(Type.FIX == row.getType())
                    {
                        drawImage(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, rect.width, rect.height);

                        startX += rect.width;

                        preRowHeight = rect.height;
                    }
                    else if(Type.HORIZONTALPATCH == row.getType())
                    {
                        // 绘制水平拉伸区域
                        drawImage(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, width, rect.height);

                        startX += width;

                        preRowHeight = rect.height;
                    }
                    else if(Type.VERTICALPATCH == row.getType())
                    {
                        // 垂直拉伸
                        drawImage(g2d, image, rect.x, rect.y, rect.width, rect.height, startX, startY, rect.width, height);

                        startX += rect.width;

                        preRowHeight = height;
                    }
                    else if(Type.TILEPATCH == row.getType())
                    {
                        // 平铺
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

    public List<List<Row>> countColumn(NinePatchRegion xRegions, NinePatchRegion yRegions)
    {
        boolean isPatchY = false; // 当前是否处于拉伸区域
        int i = 0; // 固定区域起始索引
        int j = 0;// 拉伸区域起始索引
        int patchNum = yRegions.getPatchRegions().size();
        int fixNum = yRegions.getFixRegions().size();

        Region yRegion = null; // 循环出口条件

        List<List<Row>> columns = new LinkedList<List<Row>>();// 九宫格行集合

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
     * 计算点九图的每一列区域
     * @param yRegion
     * @param xRegions
     * @param isPatchY
     * @return 返回当前行
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
     * 获取内容显示区域的间距
     * @param w 内容面板的宽度
     * @param h 内容面板的高度
     * @param xRegions x坐标集合
     * @param yRegions y坐标集合
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
     * 根据像素值集合计算当前像素区域中的固定区域和拉伸区域
     * @param pixels 需要查找的像素集合
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

            // 区间对象
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

        // 像素集合中没有找到特殊像素
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
     * 平铺图片
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

            // Ë®Æ½À­Éì
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
     * 计算点九图片信息
     * <p>|1|   2  |3|</p>
     * <p>|4|   5  |6|</p>
     * <p>|7|   8  |9|</p>
     *  计算图片中每一行里的固定区域、垂直拉伸区、水平拉伸区和平铺区域以及内容显示间距
     * @param image
     */
    protected void countPatch(T image)
    {
        // 图片实际宽度, 不包含左右控制区域
        int width = getImageWidth(image) - 2;

        // 图片实际高度, 不包含上下控制区域
        int height = getImageHeight(image) - 2;

        // 行
        int[] row = null;

        // 列
        int[] column = null;

        // 获取左侧拉伸区域的像素
        column = getPixels(image, 0, 1, 1, height);

        // 获取左侧垂直列
        NinePatchRegion left = getPatches(column);

        // 获取顶部拉伸区域的像素值, 不包含左右控制区域，所以向右偏移一个像素
        row = getPixels(image, 1, 0, width, 1);

        // 获取顶部水平行
        NinePatchRegion top = getPatches(row);

        // 水平拉伸区域素数量
        this.horizontalPatchNum = top.getPatchRegions().size();

        // 垂直拉伸区域数量
        this.verticalPatchNum = left.getPatchRegions().size();

        this.columns = countColumn(top, left);

        // 获取底部水平内容拉伸区域像素集合
        row = getPixels(image, 1, height + 1, width, 1);

        // 获取右侧垂直内容拉伸区域像素集合
        column = getPixels(image, width + 1, 1, 1, height);

        NinePatchRegion bottom = getPatches(row);

        NinePatchRegion right = getPatches(column);

        this.padding = getPadding(width, height, bottom.getPatchRegions(), right.getPatchRegions());
    }
    
    protected T toCompatibleImage(T image)
    {
    	return image;
    }

    /**
     * 从指定的矩形区域中读取像素数据
     * @param img
     * @param x 起始x坐标
     * @param y 起始y坐标
     * @param w 矩形的宽度
     * @param h 矩形的高度
     * @return 指定矩形区域像素数据整型数组
     */
    public abstract int[] getPixels(T img, int x, int y, int w, int h);

    /**
     * 获取图片宽度
     * @param img
     * @return
     */
    public abstract int getImageWidth(T img);

    /**
     * 获取图片高度
     * @param img
     * @return
     */
    public abstract int getImageHeight(T img);

    /**
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
                if(Type.FIX == row.getType() && isFirst)
                {
                    patchWidth -= row.getRectangle().width;
                }

                //BUG FIX: 修复显示区域计算问题
                if(Type.FIX == row.getType() && isNewColumn)
                {
                    patchHeight -= row.getRectangle().height;

                    isNewColumn = false;
                }
            }

            isNewColumn = true;

            isFirst = false;
        }
    }

    private Type getRowType(boolean isPatchX, boolean isPatchY)
    {
        if (!isPatchX && !isPatchY)
        {
            return Type.FIX;
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
