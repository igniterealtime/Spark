package freeseawind.lf.basic.filechooser;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckResourceBundle;

/**
 * <p>FileChooserUI资源绑定类</p>
 *
 * <p>FileChooserUI resource bundle class.<p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckFileChooserUIBundle extends LuckResourceBundle
{
    /**
     * <p>返回上级目录按钮图片属性key</p>
     *
     * <p>up folder button icon properties.</p>
     */
    public static final String UPFOLDERICON = "FileChooser.upFolderIcon";

    /**
     * <p>创建文件夹按钮图片属性key</p>
     *
     * <p>create folder button icon properties.</p>
     */
    public static final String NEWFOLDERICON = "FileChooser.newFolderIcon";

    /**
     * <p>返回home目录按钮图片属性key</p>
     *
     * <p>Return to the home directory button icon properties.</p>
     */
    public static final String HOMEFOLDERICON = "FileChooser.homeFolderIcon";

    /**
     * <p>查看文件列表按钮图片属性key</p>
     *
     * <p>list view button icon properties.</p>
     */
    public static final String LISTVIEWICON = "FileChooser.listViewIcon";

    /**
     * <p>查看文件详情按钮图片属性key</p>
     *
     * <p>view details button icon properties.</p>
     */
    public static final String DETAILSVIEWICON = "FileChooser.detailsViewIcon";

    /**
     * <p>文件夹按钮图标属性key</p>
     *
     * <p>folder button icon properties.</p>
     */
    public static final String DIRECTORYICON = "FileView.directoryIcon";

    /**
     * <p>计算机按钮图标属性key</p>
     *
     * <p>Computer button icon properties.</p>
     */
    public static final String COMPUTERICON = "FileView.computerIcon";

    /**
     * <p>文件按钮图标属性key</p>
     *
     * <p>file button icon properties.</p>
     */
    public static final String FILEICON = "FileView.fileIcon";

    /**
     * <p>硬盘按钮图标属性key</p>
     *
     * <p>HardDrive button icon properties.</p>
     */
    public static final String HARDDRIVE = "FileView.hardDriveIcon";

    /**
     * <p>软盘按钮图标属性key</p>
     *
     * <p>FloppyDrive button icon properties.</p>
     */
    public static final String FLOPPYDRIVE = "FileView.floppyDriveIcon";

    /**
     * <p>FileChooser遍历文件视图风格属性key, 默认值true</p>
     *
     * <p>List view windows style properties, default value true.</p>
     */
    public static final String LISTVIEWWINDOWSSTYLE = "FileChooser.listViewWindowsStyle";


    @Override
    protected void loadImages(UIDefaults table)
    {
        UIManager.put(UPFOLDERICON, getIconRes("filechooser/up_folder.png"));

        UIManager.put(NEWFOLDERICON, getIconRes("filechooser/new_folder.png"));

        UIManager.put(HOMEFOLDERICON, getIconRes("filechooser/home.png"));

        UIManager.put(LISTVIEWICON, getIconRes("filechooser/list.png"));

        UIManager.put(DETAILSVIEWICON, getIconRes("filechooser/details.png"));

        UIManager.put(DIRECTORYICON, getIconRes("filechooser/directory.png"));

        //------------------------------------------------------------------//

        UIManager.put(FILEICON, getIconRes("filechooser/file.png"));

        UIManager.put(HARDDRIVE, getIconRes("filechooser/harddrive.png"));

        UIManager.put(FLOPPYDRIVE, getIconRes("filechooser/floppyDrive.png"));
    }

    @Override
    protected void installOther(UIDefaults table)
    {
        UIManager.put(LISTVIEWWINDOWSSTYLE, Boolean.TRUE);
    }
}
