package freeseawind.lf.basic.filechooser;

import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * FileChooserUI资源绑定类
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckFileChooserUIBundle extends LuckResourceBundle
{
    /**
     * 返回上级目录按钮图片属性key
     */
    public static final String UPFOLDERICON = "FileChooser.upFolderIcon";

    /**
     * 创建文件夹按钮图片属性key
     */
    public static final String NEWFOLDERICON = "FileChooser.newFolderIcon";

    /**
     * 返回home目录按钮图片属性key
     */
    public static final String HOMEFOLDERICON = "FileChooser.homeFolderIcon";

    /**
     * 查看文件列表按钮图片属性key
     */
    public static final String LISTVIEWICON = "FileChooser.listViewIcon";

    /**
     * 查看文件详情按钮图片属性key
     */
    public static final String DETAILSVIEWICON = "FileChooser.detailsViewIcon";

    /**
     * FileChooser遍历文件视图风格属性key
     */
    public static final String LISTVIEWWINDOWSSTYLE = "FileChooser.listViewWindowsStyle";

    @Override
    protected void loadImages()
    {
        UIManager.put(UPFOLDERICON, getIconRes("filechooser/up_folder.png"));

        UIManager.put(NEWFOLDERICON, getIconRes("filechooser/new_folder.png"));

        UIManager.put(HOMEFOLDERICON, getIconRes("filechooser/home.png"));

        UIManager.put(LISTVIEWICON, getIconRes("filechooser/list.png"));

        UIManager.put(DETAILSVIEWICON, getIconRes("filechooser/details.png"));
    }

    @Override
    protected void installOther()
    {
        UIManager.put(LISTVIEWWINDOWSSTYLE, Boolean.TRUE);
    }
}
