package freeseawind.lf.basic.spinner;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;

import freeseawind.lf.controller.LuckArrowButton;

/**
 * <p>
 * SpinnerUI实现类， 取消文本框边框，设置文本区域为不完全透明, 使用LittleLuck按钮替换默认按钮。
 * </p>
 *
 * <p>
 * SpinnerUI implementation class, cancel the text box border, set the text area
 * is not completely transparent, use the LittleLuck button to replace the default button.
 * </p>
 *
 * @author freeseawind@github
 * @version 1.0
 *
 */
public class LuckSpinnerUI extends BasicSpinnerUI
{
    protected Border border;

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
        // set text frame info.
        if(editor instanceof DefaultEditor)
        {
            ((DefaultEditor)editor).getTextField().setBorder(null);

            ((DefaultEditor)editor).getTextField().setOpaque(false);

            ((DefaultEditor)editor).setOpaque(false);
        }

        return editor;
    }

    /**
     * <p>使用{@link LuckArrowButton}替换默认按钮。</p>
     *
     * <p>use {@link LuckArrowButton} to replace the default button.</p>
     */
    protected Component createNextButton()
    {
        Component c = createArrowButton(SwingConstants.NORTH);

        c.setName("Spinner.nextButton");

        installNextButtonListeners(c);

        return c;
    }

    /**
     * <p>使用{@link LuckArrowButton}替换默认按钮。</p>
     *
     * <p>use {@link LuckArrowButton} to replace the default button.</p>
     */
    protected Component createPreviousButton()
    {
        Component c = createArrowButton(SwingConstants.SOUTH);

        c.setName("Spinner.previousButton");

        installPreviousButtonListeners(c);

        return c;
    }

    /**
     * <p>
     * 根据传入的方向创建箭头按钮并设置边框。
     * </p>
     *
     * <p>
     * Create an arrow button and set the border according to the direction of the incoming.
     * </p>
     *
     * @param direction <code>SwingConstants.SOUTH</code>,
     *            <code>SwingConstants.NORTH</code>
     * @return
     */
    private Component createArrowButton(int direction)
    {
        JButton b = new LuckArrowButton(direction);

        b.setBorder(UIManager.getBorder(LuckSpinnerUIBundle.ARROWBUTTONBORDER));

        b.setInheritsPopupMenu(true);

        return b;
    }
}
