package freeseawind.lf.utils;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * read image resources.
 * <ul>
 * <li>image resources(images directory)</li>
 * </ul>
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckRes
{
    private static ClassLoader cl;

	static
	{
	    cl = LuckRes.class.getClassLoader();
	}

    /**
     * get image
     *
     * @param imagePath 
     * @return <code>BufferedImage</code>
     */
    public static BufferedImage getImage(String imagePath)
    {
    	BufferedImage bufImg = null;

        try
        {
            URL url = cl.getResource("freeseawind/images/" + imagePath);

            bufImg = ImageIO.read(url);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }

        return bufImg;
    }
}
