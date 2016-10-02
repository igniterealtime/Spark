package freeseawind.lf.basic.filechooser;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalFileChooserUI;

/**
 * <p>
 * FileChooserUI实现类，优先使用系统默认图标，找不到则使用LookAndFeel定义的图标。
 * </p>
 *
 * <p>
 * JFileChooser implementation class, the use of the system default icon, you
 * can not find the icon using the LookAndFeel definition.
 * </p>
 *
 * @see LuckFileChooserUIBundle
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckFileChooserUI extends MetalFileChooserUI
{
    private BasicFileView fileView = new LuckFileView();

    public LuckFileChooserUI(JFileChooser filechooser)
    {
        super(filechooser);
    }

    public static ComponentUI createUI(JComponent c)
    {
        return new LuckFileChooserUI((JFileChooser) c);
    }

    /**
     * use custom <code>FileView</code>
     *
     * @see LuckFileView
     * @return <code>FileView</code>
     */
    public FileView getFileView(JFileChooser fc)
    {
        return fileView;
    }

    /**
     * <p>
     * 参考<code>WindowsFileView</code>实现,优先使用系统默认图标，找不到则使用LookAndFeel定义的图标。
     * </p>
     *
     * <p>
     * Refer to the <code>WindowsFileView</code> implementation,the use of the system
     * default icon, you can not find the icon using the LookAndFeel definition.
     * </p>
     */
    protected class LuckFileView extends BasicFileView
    {
        public Icon getIcon(File f)
        {
            Icon icon = null;

            if (f != null)
            {
                icon = getCachedIcon(f);

                if (icon != null)
                {
                    return icon;
                }

                icon = getFileChooser().getFileSystemView().getSystemIcon(f);
            }

            if (icon == null)
            {
                icon = super.getIcon(f);
            }

            cacheIcon(f, icon);

            return icon;
        }
    }
}
