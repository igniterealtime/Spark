package freeseawind.lf.utils;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * 资源读取类, 主要读取两类资源
 * <ul>
 * <li>图片资源(images目录下)</li>
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
     * 获取图片资源
     *
     * @param imagePath 图片在images目录下的路径
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
