package freeseawind.lf.basic.text;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JTextField;

import freeseawind.lf.border.LuckBorderField;
import freeseawind.lf.border.LuckShapeBorder;

public class LuckTextBorder extends LuckShapeBorder
{
    private static final long serialVersionUID = 152864944146484889L;

    public LuckTextBorder(Insets i, boolean isFocusBorder)
    {
        super(i);
    }

    @Override
    public LuckBorderField getBorderField(Component c)
    {
        if (c instanceof JTextField)
        {
            JTextField textField = (JTextField) c;

            if (textField.getUI() instanceof LuckBorderField)
            {
                return (LuckBorderField) textField.getUI();
            }
        }

        return null;
    }
}
