/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.sparkplugin;

import javax.sound.sampled.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeNode;
import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Pure Java Audio Mixer. Control Volume and Settings for any Sound device in the OS.
 *
 * @author Thiago Camargo
 */

public class JavaMixer {

    private static final Line.Info[] EMPTY_PORT_INFO_ARRAY = new Line.Info[0];
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Sound Mixers", true);
    JTree tree = new JTree(root);

    public JavaMixer() {
        List<Mixer> portMixers = getPortMixers();
        if (portMixers.size() == 0) System.err.println("No Mixers Found.");
        for (Mixer mixer : portMixers) {
            JavaMixer.MixerNode mixerNode = new JavaMixer.MixerNode(mixer);
            createMixerChildren(mixerNode);
            root.add(mixerNode);
        }
    }

    public JTree getTree() {
        return tree;
    }

    public Component getPrefferedMasterVolume() {
        TreePath path = findByName(new TreePath(root), new String[]{"SPEAKER", "Volume"});

        if (path == null) {
            path = findByName(new TreePath(root), new String[]{"Master target", "Master", "Mute"});
        }

        if (path != null) {
            if (path.getLastPathComponent() instanceof JavaMixer.ControlNode)
                return ((JavaMixer.ControlNode) path.getLastPathComponent()).getComponent();
        }
        return null;
    }

    public Component getPrefferedInputVolume() {
        TreePath path = findByName(new TreePath(root), new String[]{"MICROPHONE", "Volume"});

        if (path == null) {
            path = findByName(new TreePath(root), new String[]{"Capture source", "Capture", "Volume"});
        }

        if (path != null) {
            if (path.getLastPathComponent() instanceof JavaMixer.ControlNode)
                return ((JavaMixer.ControlNode) path.getLastPathComponent()).getComponent();
        }
        return null;
    }

    public void setMicrophoneInput() {
        TreePath path = findByName(new TreePath(root), new String[]{"MICROPHONE", "Select"});

        if (path == null) {
            path = findByName(new TreePath(root), new String[]{"Capture source", "Capture", "Mute"});
        }

        if (path != null) {
            if (path.getLastPathComponent() instanceof JavaMixer.ControlNode) {
                BooleanControl bControl = (BooleanControl) (((JavaMixer.ControlNode) path.getLastPathComponent()).getControl());
                bControl.setValue(true);
            }
        }
    }

    public void setMuteForMicrophoneOutput() {
        TreePath path = findByName(new TreePath(root), new String[]{"SPEAKER", "Microfone", "Mute"});

        if (path == null) {
            path = findByName(new TreePath(root), new String[]{"MIC target", "mic", "Mute"});
        }

        if (path != null) {
            if (path.getLastPathComponent() instanceof JavaMixer.ControlNode) {
                BooleanControl bControl = (BooleanControl) (((JavaMixer.ControlNode) path.getLastPathComponent()).getControl());
                bControl.setValue(true);
            }
        }
    }

    /**
     * Returns the Mixers that support Port lines.
     *
     * @return List<Mixer> Port Mixers
     */
    private List<Mixer> getPortMixers() {
        List<Mixer> supportingMixers = new ArrayList<Mixer>();
        Mixer.Info[] aMixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info aMixerInfo : aMixerInfos) {
            Mixer mixer = AudioSystem.getMixer(aMixerInfo);
            boolean bSupportsPorts = arePortsSupported(mixer);
            if (bSupportsPorts) {
                supportingMixers.add(mixer);
            }
        }
        return supportingMixers;
    }

    private boolean arePortsSupported(Mixer mixer) {
        Line.Info[] infos;
        infos = mixer.getSourceLineInfo();
        for (Line.Info info : infos) {
            if (info instanceof Port.Info) {
                return true;
            } else if (info instanceof DataLine.Info) {
                return true;
            }
        }
        infos = mixer.getTargetLineInfo();
        for (Line.Info info : infos) {
            if (info instanceof Port.Info) {
                return true;
            } else if (info instanceof DataLine.Info) {
                return true;
            }
        }
        return false;
    }

    private void createMixerChildren(JavaMixer.MixerNode mixerNode) {
        Mixer mixer = mixerNode.getMixer();
        Line.Info[] infosToCheck = getPortInfo(mixer);
        for (Line.Info anInfosToCheck : infosToCheck) {
            if (mixer.isLineSupported(anInfosToCheck)) {
                Port port = null;
                DataLine dLine = null;

                int maxLines = mixer.getMaxLines(anInfosToCheck);
                // Workaround to prevent a JVM crash on Mac OS X (Intel) 1.5.0_07 JVM
                if (maxLines > 0) {
                    try {
                        if (anInfosToCheck instanceof Port.Info) {
                            port = (Port) mixer.getLine(anInfosToCheck);
                            port.open();
                        }
                        else if (anInfosToCheck instanceof DataLine.Info) {
                            dLine = (DataLine) mixer.getLine(anInfosToCheck);
                            if (!dLine.isOpen()) {
                                dLine.open();
                            }
                        }
                    }
                    catch (LineUnavailableException e) {
                        e.printStackTrace();
                    }
                    catch (Exception e) {
                        // Do Nothing
                    }
                }
                if (port != null) {
                    JavaMixer.PortNode portNode = new JavaMixer.PortNode(port);
                    createPortChildren(portNode);
                    mixerNode.add(portNode);
                } else if (dLine != null) {
                    JavaMixer.PortNode portNode = new JavaMixer.PortNode(dLine);
                    createPortChildren(portNode);
                    mixerNode.add(portNode);
                }
            }
        }
    }

    private Line.Info[] getPortInfo(Mixer mixer) {
        Line.Info[] infos;
        List<Line.Info> portInfoList = new ArrayList<Line.Info>();
        infos = mixer.getSourceLineInfo();
        for (Line.Info info : infos) {
            if (info instanceof Port.Info || info instanceof DataLine.Info) {
                portInfoList.add((Line.Info) info);
            }
        }
        infos = mixer.getTargetLineInfo();
        for (Line.Info info1 : infos) {
            if (info1 instanceof Port.Info || info1 instanceof DataLine.Info) {
                portInfoList.add((Line.Info) info1);
            }
        }
        return portInfoList.toArray(EMPTY_PORT_INFO_ARRAY);
    }

    private void createPortChildren(JavaMixer.PortNode portNode) {
        Control[] aControls = portNode.getPort().getControls();
        for (Control aControl : aControls) {
            JavaMixer.ControlNode controlNode = new JavaMixer.ControlNode(aControl);
            createControlChildren(controlNode);
            portNode.add(controlNode);
        }
    }

    private void createControlChildren(JavaMixer.ControlNode controlNode) {
        if (controlNode.getControl() instanceof CompoundControl) {
            CompoundControl control = (CompoundControl) controlNode.getControl();
            Control[] aControls = control.getMemberControls();
            for (Control con : aControls) {
                JavaMixer.ControlNode conNode = new JavaMixer.ControlNode(con);
                createControlChildren(conNode);
                controlNode.add(conNode);
            }
        }
    }

    /**
     * Returns whether the type of a FloatControl is BALANCE or PAN.
     *
     * @param control FloatControl control
     * @return boolean is Balance or Pan
     */
    private static boolean isBalanceOrPan(FloatControl control) {
        Control.Type type = control.getType();
        return type.equals(FloatControl.Type.PAN) || type.equals(FloatControl.Type.BALANCE);
    }

    public class MixerNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = -987278469391244202L;
		private Mixer mixer;

        public MixerNode(Mixer mixer) {
            super(mixer.getMixerInfo(), true);
            this.mixer = mixer;
        }

        public Mixer getMixer() {
            return mixer;
        }

    }

    public class PortNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = -7774055649714159518L;
		private Line port;

        public PortNode(Line port) {
            super(port.getLineInfo(), true);
            this.port = port;
        }

        public Line getPort() {
            return port;
        }

    }

    public class ControlNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = 2014062750235264630L;
		private Control control;
		private Component component;

        public ControlNode(Control control) {
            super(control.getType(), true);
            this.control = control;
            if (control instanceof BooleanControl) {
                component = createControlComponent((BooleanControl) control);
            } else if (control instanceof EnumControl) {
                component = createControlComponent((EnumControl) control);
            } else if (control instanceof FloatControl) {
                component = createControlComponent((FloatControl) control);
            } else {
                component = null;
            }
        }

        public Control getControl() {
            return control;
        }

        public Component getComponent() {
            return component;
        }

        private JComponent createControlComponent(BooleanControl control) {
            AbstractButton button;
            String strControlName = control.getType().toString();
            ButtonModel model = new JavaMixer.BooleanControlButtonModel(control);
            button = new JCheckBox(strControlName);
            button.setModel(model);
            return button;
        }

        private JComponent createControlComponent(EnumControl control) {
            JPanel component = new JPanel();
            String strControlName = control.getType().toString();
            component.setBorder(new TitledBorder(new EtchedBorder(), strControlName));
            return component;
        }

        private JComponent createControlComponent(FloatControl control) {
            int orientation = isBalanceOrPan(control) ? JSlider.HORIZONTAL : JSlider.VERTICAL;
            BoundedRangeModel model = new JavaMixer.FloatControlBoundedRangeModel(control);
            JSlider slider = new JSlider(model);
            slider.setOrientation(orientation);
            slider.setPaintLabels(true);
            slider.setPaintTicks(true);
            slider.setSize(10, 50);
            return slider;
        }

    }

    public class BooleanControlButtonModel extends DefaultButtonModel {
		private static final long serialVersionUID = -4667054823378068382L;
		private BooleanControl control;

        public BooleanControlButtonModel(BooleanControl control) {
            this.control = control;
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setSelected(!isSelected());
                }
            });
        }

        public void setSelected(boolean bSelected) {
            control.setValue(bSelected);
        }

        public boolean isSelected() {
            return control.getValue();
        }
    }

    public class FloatControlBoundedRangeModel extends DefaultBoundedRangeModel {
		private static final long serialVersionUID = 4469386606588434901L;
		private FloatControl control;
        private float factor;

        public FloatControlBoundedRangeModel(FloatControl control) {
            this.control = control;
            float range = 100;
            float steps = range / 100;
            factor = range / steps;
            int min = (int) (control.getMinimum() * factor);
            int max = (int) (control.getMaximum() * factor);
            int value = (int) (control.getValue() * factor);
            setRangeProperties(value, 0, min, max, false);
        }

        private float getScaleFactor() {
            return factor;
        }

        public void setValue(int nValue) {
            super.setValue(nValue);
            control.setValue((float) nValue / getScaleFactor());
        }

        public int getValue() {
            return (int) (control.getValue() * getScaleFactor());
        }

    }

    public static void main(String[] args) {
        final JavaMixer sm = new JavaMixer();
        final JFrame jf = new JFrame("Mixer Test");
        final JPanel jp = new JPanel();
        jf.add(jp);
        jp.add(sm.getTree());
        jf.setSize(600, 500);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        sm.getTree().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                if (path.getLastPathComponent() instanceof JavaMixer.ControlNode) {
                    JavaMixer.ControlNode controlNode = (JavaMixer.ControlNode) path.getLastPathComponent();
                    if (!(controlNode.getControl() instanceof CompoundControl)) {
                        if (jp.getComponentCount() > 1)
                            jp.remove(1);
                        jp.add(controlNode.getComponent(), 1);
                        jp.repaint();
                    }
                }
            }
        });
        jp.add(sm.getPrefferedMasterVolume());
        jp.add(sm.getPrefferedMasterVolume());
        jp.add(sm.getPrefferedInputVolume());
        jp.repaint();
        sm.setMicrophoneInput();
        sm.setMuteForMicrophoneOutput();
    }

    public TreePath find(TreePath path, Object[] nodes) {
        return find2(path, nodes, 0, false);
    }

    public TreePath findByName(TreePath path, String[] names) {
        return find2(path, names, 0, true);
    }

    private TreePath find2(TreePath parent, Object[] nodes, int depth, boolean byName) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (depth > nodes.length - 1) {
            return parent;
        }

        if (node.getChildCount() >= 0) {
            for (Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
                TreeNode n = e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                boolean find;

                if (byName) {
                    find = n.toString().toUpperCase().indexOf(nodes[depth].toString().toUpperCase()) > -1;
                } else {
                    find = n.equals(nodes[depth]);
                }

                if (find) {
                    TreePath result = find2(path, nodes, depth + 1, byName);
                    if (result != null) {
                        return result;
                    }
                } else {
                    TreePath result = find2(path, nodes, depth, byName);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }

        return null;
    }
}
