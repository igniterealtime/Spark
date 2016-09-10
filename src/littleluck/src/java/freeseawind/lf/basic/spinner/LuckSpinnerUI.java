package freeseawind.lf.basic.spinner;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;

import freeseawind.lf.controller.LuckArrowButton;

/**
 * SpinnerUI实现类， 取消文本框边框，设置文本区域为不完全透明。
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckSpinnerUI extends BasicSpinnerUI
{
    public static ComponentUI createUI(JComponent c)
    {
        return new LuckSpinnerUI();
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);
    }

    protected JComponent createEditor()
    {
        JComponent editor = super.createEditor();

        // 设置文本框属性
        if(editor instanceof DefaultEditor)
        {
            ((DefaultEditor)editor).getTextField().setBorder(null);

            ((DefaultEditor)editor).getTextField().setOpaque(false);

            ((DefaultEditor)editor).setOpaque(false);
        }

        return editor;
    }

    protected Component createNextButton()
    {
        Component c = createArrowButton(SwingConstants.NORTH);

        c.setName("Spinner.nextButton");

        installNextButtonListeners(c);

        return c;
    }

    protected Component createPreviousButton()
    {
        Component c = createArrowButton(SwingConstants.SOUTH);

        c.setName("Spinner.previousButton");

        installPreviousButtonListeners(c);

        return c;
    }

    /**
     * 根据传入的方向创建箭头按钮并设置边框
     * @param direction
     * @return
     */
    private Component createArrowButton(int direction)
    {
        JButton b = new LuckArrowButton(direction);

        b.setBorder(UIManager.getBorder("Spinner.arrowButtonBorder"));

        b.setInheritsPopupMenu(true);

        return b;
    }
}
