package org.jivesoftware.spark.ui.themes;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;

public class ColorPreferencePanel extends SparkTabbedPane {

    private static final long serialVersionUID = -3594152276094474130L;

    private JScrollPane _jScrollPane;
    private JList _colorliste;
    private ColorSettings _colorsettings;

    public ColorPreferencePanel() {

	createUI();
    }

    private void createUI() {

	_colorsettings = ColorSettingManager.getColorSettings();
	Set<String> sets  = _colorsettings.getKeys();
	
	
	Vector<String> keys= new Vector<String>();
	for(String s : sets)
	{
	    keys.add(s);
	}	
	sortList(keys);
	
	JPanel rightpanel = new JPanel(new GridBagLayout());
	
	final JTextField red = new JTextField();
	red.setPreferredSize(new Dimension(80,20));
	JLabel redlabel = new JLabel("(0-255) Red:");
	
	final JTextField green = new JTextField();
	green.setPreferredSize(new Dimension(80,20));
	JLabel greenlabel = new JLabel("(0-255) Green:");
	
	final JTextField blue = new JTextField();
	blue.setPreferredSize(new Dimension(80,20));
	JLabel bluelabel = new JLabel("(0-255) Blue:");
	
	final JTextField alfa = new JTextField();
	alfa.setPreferredSize(new Dimension(80,20));
	JLabel alfalabel = new JLabel("(0-100%) Opacity:"); //100 = 100%Visible
	
	final JButton savebutton = new JButton("Save Color");
	
	final JLabel errorlabel = new JLabel("");
	errorlabel.setForeground(Color.red);
	


	final JLabel colorPreview = new JLabel();
	colorPreview.setBackground(new Color(0,0,0,0));
	colorPreview.setPreferredSize(new Dimension(80,20));
	colorPreview.setMaximumSize(new Dimension(80,20));
	colorPreview.setMinimumSize(new Dimension(80,20));
	
	rightpanel.add(colorPreview, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

	
	rightpanel.add(redlabel , new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(red, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	rightpanel.add(greenlabel , new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(green, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	rightpanel.add(bluelabel , new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(blue, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	rightpanel.add(alfalabel , new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(alfa, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	
	rightpanel.add(savebutton, new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
	rightpanel.add(errorlabel, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

	
	_colorliste = new JList(keys);
	_jScrollPane = new JScrollPane(_colorliste);
	
	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	add(_jScrollPane);
	add(rightpanel);
	
	
	_colorliste.addListSelectionListener(new ListSelectionListener() {
	
	
	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		
		String v = (String) _colorliste.getSelectedValue();
		
		Color c = _colorsettings.getColorFromProperty(v);

		red.setText(""+c.getRed());
		green.setText(""+c.getGreen());
		blue.setText(""+c.getBlue());
		
		alfa.setText(""+(c.getAlpha()*100/255));
		
		colorPreview.setBackground(new Color(c.getRed(),c.getGreen(),c.getBlue()));
		colorPreview.repaint();
		colorPreview.revalidate();
		errorlabel.setText("");
	    }
	});
	
	savebutton.addActionListener(new ActionListener() {
	    
	    @Override
	    public void actionPerformed(ActionEvent e) {
		
		try{
		    
		    // convert to value between 0 and 255
		final int redvalue =   Math.max(0,Math.min(255,Integer.parseInt(red.getText())));
		final int greenvalue = Math.max(0,Math.min(255,Integer.parseInt(green.getText())));
		final int bluevalue =  Math.max(0,Math.min(255,Integer.parseInt(blue.getText())));
		// convert to percent
		int alfavalue =  Math.max(0,Math.min(100,Integer.parseInt(alfa.getText())));

		alfavalue = alfavalue*255/100;
		
		Color c = new Color(redvalue,greenvalue,bluevalue,alfavalue);	
		_colorsettings.setColorForProperty((String)_colorliste.getSelectedValue(), c);
		
		UIManager.put((String)_colorliste.getSelectedValue(), c);
		
		EventQueue.invokeLater(new Runnable() {
		    
		    @Override
		    public void run() {
			colorPreview.setBackground(new Color(redvalue,greenvalue,bluevalue));
			colorPreview.repaint();
			colorPreview.revalidate();
			
		    }
		});
			
		errorlabel.setText("Saved Color");
		
		}
		catch(Exception ex)
		{
		    errorlabel.setText("Insert valid number between 0-255");
		    errorlabel.revalidate();
		}
	    }
	});

    }
    
    private void sortList(Vector<String> set)
    {
	
	Collections.sort(set);
    }
    

    private BufferedImage createImageLabel(int[] pixels)
    {
        BufferedImage image = new BufferedImage(80, 20, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = image.getRaster();
        raster.setPixels(0, 0, 80, 20, pixels);

        return image;
    }

}
