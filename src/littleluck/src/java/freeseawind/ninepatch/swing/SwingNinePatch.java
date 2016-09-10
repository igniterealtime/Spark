package freeseawind.ninepatch.swing;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import freeseawind.ninepatch.common.AbstractNinePatch;
import freeseawind.ninepatch.common.RepeatType;

public class SwingNinePatch extends AbstractNinePatch<BufferedImage, Graphics2D>
{
    public SwingNinePatch(BufferedImage image)
    {
        super(image);
    }
    
    public SwingNinePatch(BufferedImage image, RepeatType repeatType)
    {
        super(image, repeatType);
    }

    @Override
	protected BufferedImage toCompatibleImage(BufferedImage image)
	{
    	GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    	
    	GraphicsConfiguration config = device.getDefaultConfiguration();
    	
    	BufferedImage bufImg = config.createCompatibleImage(image.getWidth(), image.getHeight(), Transparency.TRANSLUCENT);
    	
        Graphics2D g2d = bufImg.createGraphics();
        
        g2d.drawImage(image, 0, 0, null);
        
        g2d.dispose();
        
        return bufImg;
	}

    @Override
    public int[] getPixels(BufferedImage img, int x, int y, int w, int h)
    {
        int[] pixels = new int[w * h];

        int imageType = img.getType();

        if (imageType == BufferedImage.TYPE_INT_ARGB || imageType == BufferedImage.TYPE_INT_RGB)
        {
            Raster raster = img.getRaster();

            return (int[]) raster.getDataElements(x, y, w, h, pixels);
        }

        return img.getRGB(x, y, w, h, pixels, 0, w);
    }

    @Override
    public int getImageWidth(BufferedImage img)
    {
        return img.getWidth();
    }

    @Override
    public int getImageHeight(BufferedImage img)
    {
        return img.getHeight();
    }

    @Override
    public void translate(Graphics2D g2d, int x, int y)
    {
        g2d.translate(x, y);
    }

    @Override
    public void drawImage(Graphics2D g2d,
                          BufferedImage image,
                          int x,
                          int y,
                          int scaledWidth,
                          int scaledHeight)
    {
        g2d.drawImage(image, x, y, scaledWidth, scaledHeight, null);
    }

    @Override
    public void drawImage(Graphics2D g2d,
                          BufferedImage image,
                          int sx,
                          int sy,
                          int sw,
                          int sh,
                          int dx,
                          int dy,
                          int dw,
                          int dh)
    {
        g2d.drawImage(image, dx, dy, dx + dw, dy + dh, sx, sy, sx + sw, sy + sh, null);
    }
}
