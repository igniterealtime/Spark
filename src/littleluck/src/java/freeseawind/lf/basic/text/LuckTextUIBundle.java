package freeseawind.lf.basic.text;

import java.awt.Insets;

import javax.swing.UIManager;

import freeseawind.lf.cfg.LuckResourceBundle;

/**
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckTextUIBundle extends LuckResourceBundle
{
    public static final String TEXTFIELD_BORDER = "TextField.border";
    
    public static final String TEXTFIELD_FOCUSBORDER = "TextField.focusBorder";

    public static final String PASSWORDFIELD_BORDER = "PasswordField.border";
    
    public static final String PASSWORDFIELD_FOCUSBORDER = "PasswordField.focusBorder";

    public static final String FORMATTEDTEXTFIELD_BORDER = "FormattedTextField.border";
    
    public static final String FORMATTEDTEXTFIELD_FOCUSBORDER = "FormattedTextField.focusBorder";

    public static final String TEXTAREA_BORDER = "TextArea.border";

    public static final String TEXTPANE_BORDER = "TextPane.border";

    public static final String EDITORPANE_BORDER = "EditorPane.border";
    //TextPane

    @Override
    protected void installBorder()
    {
        Insets insets = new Insets(4, 5, 4, 5);
        
        LuckTextBorder normalBorder = new LuckTextBorder(insets, false);

        LuckTextBorder focusBorder = new LuckTextBorder(insets, true);
        
        UIManager.put(TEXTFIELD_BORDER, normalBorder);
        
        UIManager.put(TEXTFIELD_FOCUSBORDER, focusBorder);
        
        UIManager.put(PASSWORDFIELD_BORDER, normalBorder);
        
        UIManager.put(PASSWORDFIELD_FOCUSBORDER, focusBorder);
        
        UIManager.put(FORMATTEDTEXTFIELD_BORDER, normalBorder);
        
        UIManager.put(FORMATTEDTEXTFIELD_FOCUSBORDER, focusBorder);
    }
}
