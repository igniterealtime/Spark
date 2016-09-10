package freeseawind.lf.basic.filechooser;

import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalFileChooserUI;

/**
 * FileChooserUI实现类, 使用系统默认图标来展示。
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

    public FileView getFileView(JFileChooser fc)
    {
        return fileView;
    }

    /**
     * 参考WindowsFileView实现
     * @author freeseawind@github
     * @version 1.0
     *
     */
    protected class LuckFileView extends BasicFileView
    {
        public Icon getIcon(File f)
        {
            Icon icon = getCachedIcon(f);

            if (icon != null)
            {
                return icon;
            }

            if (f != null)
            {
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
