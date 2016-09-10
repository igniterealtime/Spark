package freeseawind.lf.utils;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

/**
 * 资源读取类, 主要读取两类资源
 * <ul>
 * <li>图片资源(images目录下)</li>
 * <li>国际化语言包(i18n/littleluck_i18n)</li>
 * </ul>
 * @author freeseawind@github
 * @version 1.0
 */
public class LuckRes
{
    private static ClassLoader cl;

	private static PropertyResourceBundle prb;

	static
	{
	    cl = LuckRes.class.getClassLoader();

	    prb = (PropertyResourceBundle) ResourceBundle.getBundle("i18n/littleluck_i18n");
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
            URL url = cl.getResource("images/" + imagePath);

            bufImg = ImageIO.read(url);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }

        return bufImg;
    }

    /**
     * 根据key获取所对应的本地化文本值
     *
     * @param propertyName 在配置文件中的属性
     * @return 本地化文本值
     */
    public static String getString(String propertyName)
    {
        try
		{
			return prb.getString(propertyName);
		}
        catch (Exception e)
		{
			e.printStackTrace();
		}

        return propertyName;
    }
}
