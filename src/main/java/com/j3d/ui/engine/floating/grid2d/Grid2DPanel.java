/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.j3d.ui.engine.floating.grid2d;

import com.j3d.StaticRefs;
import com.j3d.engine.math.Dim;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.math.convert.ConversionWithOffset;
import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.find.FindResult;
import com.j3d.engine.scene.find.Finder;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.gen.grid.*;
import com.j3d.gen.grid.Point;
import com.j3d.ui.engine.FloatingPanel;
import com.j3d.ui.theme.J3DTheme;
import com.yetnt.utils.builders.InlineHTML;
import com.yetnt.utils.functional.QuadFunction;
import com.yetnt.utils.tuple.MutablePair;
import com.yetnt.utils.tuple.Triple;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.j3d.StaticRefs.*;

/**
 *
 * @author yetnt
 */
public class Grid2DPanel extends javax.swing.JPanel {

    // TODO: JSlider doing nothing right now, write later.

    public FloatingPanel floatingPanel = new FloatingPanel("Grid2d!");
    public static CartesianPoint mousePosInPanel = new CartesianPoint(0, 0);
    public static MutablePair<Integer, Integer> offset = new MutablePair<>(0, 0);

    private final UUID overlapId = UUID.randomUUID();

    private double scale = 20;

    private CartesianPoint to;
    private CartesianPoint from;

    // default parameters.
    private Vector3 v1 = Vector3.X;
    private Vector3 v2 = Vector3.Z;
    private Vector3 origin = Vector3.ZERO;

    private GridManager gm;

    /**
     * Creates new form Grid2DPanel
     */
    public Grid2DPanel() {
        initComponents();
        floatingPanel.finish(this, (c) -> {
            if (!(c instanceof JPanel p)) return;
            p.setBounds(0, 0, p.getPreferredSize().width, p.getPreferredSize().height);
            p.setVisible(true);
        });
        initMouse();

        gm = new GridManager(this);

//        discoverExisting();
        handleSpinners();
        theme();
    }

    private CartesianPoint toPoint(ScreenPoint p, boolean round) {
        Dim dim = getGrid().sizeDim();
        CartesianPoint cp = p.toPoint(new ConversionWithOffset(scale, dim, GridManager.fromMut()));
        if (round)
            return roundPoint(cp);

        return cp;
    }

    public CartesianPoint roundPoint(CartesianPoint cp) {
        return new CartesianPoint(
                Math.round(cp.x),
                Math.round(cp.y)
        );
    }

    UUID drag = UUID.randomUUID();

    private void initMouse() {
        final Grid2DPanel t = this;
        drawPanel.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        t.requestFocus();
                        ScreenPoint sp = new ScreenPoint(e.getX(), e.getY());
                        CartesianPoint snapped = toPoint(sp, true);
                        if (gm.isDeleteMode()) {
                            gm.delete(snapped);
                        } else gm.existing(() -> new Point(snapped));
                        System.out.println(snapped);
                        drawPanel.repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
//                        t.requestFocus();
                        if (e.getButton() != MouseEvent.BUTTON1) return;
                        gm.getTemporaryDrawConsumers().remove(drag);
                        ScreenPoint sp = new ScreenPoint(e.getX(), e.getY());
                        CartesianPoint snapped = toPoint(sp, true);

                        ScreenPoint mousePos = mousePosInPanel.toScreen(new ConversionWithOffset(
                                scale,
                                getGrid().sizeDim(),
                                GridManager.fromMut()
                        ));
                        CartesianPoint cp = toPoint(mousePos, true);

                        System.out.println("From " + cp + " to " + snapped);
                        Line l = new Line(snapped, cp);
                        if (gm.isDeleteMode()) {
                            gm.delete(l);
                        } else {
                            gm.existing(() -> new Point(snapped));
                            gm.existing(() -> new Point(cp));
                            gm.getObjects().add(l);
                        }
                    }

                }
        );
        drawPanel.addMouseMotionListener(
                new MouseAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        super.mouseMoved(e);
                        old = e.getPoint();
                        mousePosInPanel =
                                new ScreenPoint(e.getX(), e.getY())
                                        .toPoint(new ConversionWithOffset(scale, getGrid().sizeDim(), GridManager.fromMut()));
                        drawPanel.repaint();
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        super.mouseDragged(e);
                        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                            ScreenPoint sp = new ScreenPoint(e.getX(), e.getY());
                            setTo(toPoint(sp, true));

                            ConversionWithOffset cp = new ConversionWithOffset(
                                    scale,
                                    getGrid().sizeDim(),
                                    GridManager.fromMut()
                            );
                            setFrom(toPoint(mousePosInPanel.toScreen(cp), true));

                            gm.repaint();
                            if (!gm.getTemporaryDrawConsumers().containsKey(drag)) {
                                gm.getTemporaryDrawConsumers().put(drag, (g) -> Line.drawLine(
                                        () -> gm.isDeleteMode() ? Color.RED : J3DTheme.TEXT_SECONDARY.color(),
                                        Grid2DPanel.this::getTo,
                                        Grid2DPanel.this::getFrom,
                                        g, cp
                                ));
                            }
                        } else {
                            // i forgor the delta.
                            int dx_cart = (int) ((old.x - e.getX()) / scale);
                            int dy_cart = (int) ((old.y - e.getY()) / scale);

                            int mult = 1;

//                            if (scale > 20) {
//                                mult = (int)Math.floor(scale/4);
//                            }

                            offset.setFirst(offset.getFirst() - (mult * dx_cart));
                            offset.setSecond(offset.getSecond() + (mult * dy_cart));
                        }
                        old = e.getPoint();
                    }
                }
        );
        drawPanel.addMouseWheelListener(
                new MouseAdapter() {
                    @Override
                    public void mouseWheelMoved(MouseWheelEvent e) {
                        super.mouseWheelMoved(e);
                        scale *= Math.pow(1.1, -e.getWheelRotation());
                        gm.repaint();
                    }
                }
        );
    }

    private java.awt.Point old = new java.awt.Point(0, 0);

    public void toggleHidden()  {
        floatingPanel.toggleHidden();
    }

    public Grid getGrid() {
        return (Grid) drawPanel;
    }

    public UUID getOverlapId() {
        return overlapId;
    }

    public JPanel getDrawPanel() {
        return drawPanel;
    }

    public Vector3 getOrigin() {
        return origin;
    }

    public Vector3 getV1() {
        return v1;
    }

    public Vector3 getV2() {
        return v2;
    }

    public double getScale() {
        return scale;
    }

    public CartesianPoint getFrom() {
        return from;
    }

    public void setFrom(CartesianPoint from) {
        this.from = from;
    }

    public CartesianPoint getTo() {
        return to;
    }

    public void setTo(CartesianPoint to) {
        this.to = to;
    }

    private Vector3 parseV3Str(String accumulator) {
        accumulator = accumulator.trim();
        if (accumulator.charAt(0) == '(' && accumulator.charAt(accumulator.length() - 1) == ')') {
            // Now check for parenthesis
            String[] nums = accumulator.substring(1, accumulator.length() - 1).split(",");
            ArrayList<Double> parsedNums = new ArrayList<>();
            for (String num : nums) {
                try {
                    parsedNums.add(Double.parseDouble(num.trim()));
                } catch (NumberFormatException e) {
//                    label.error("Invalid number format: " + SafeJLabel.EMPH, num);
                    return null;
                }
            }
            if (parsedNums.size() != 3) {
//                label.error("Invalid number of values in Vector3. Expected "+SafeJLabel.EMPH+" got "+SafeJLabel.EMPH ,3, parsedNums.size());
                return null;
            }

            return new Vector3(
                    parsedNums.getFirst(),
                    parsedNums.get(1), parsedNums.getLast()
            );
        }
        return null;
    }

    private Vector3 ask(String string) {
        String input = JOptionPane.showInputDialog(
                getMainFrame(),
                string
        );
        Vector3 v = parseV3Str(input);
        if (v == null) {
            JOptionPane.showMessageDialog(
                    getMainFrame(),
                    "Invalid Vector3 format. Please use (x,y,z) format.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        return v;
    }

    public void discoverExisting() {
        gm.invalidateAndRemoveAllReactive();
        gm.addExistingPoints();
    }

    private void log(String string) {
        logLbl.setText(new InlineHTML(string).bold().underline().wrapHTML());
    }

    public void theme() {
        J3DTheme.commitAsGenericLbl(renderBtn, true);
        J3DTheme.commitAsGenericLbl(xCompBtn, true);
        J3DTheme.commitAsGenericLbl(yCompBtn, true);
        J3DTheme.commitAsGenericLbl(setOriginBtn, true);
        J3DTheme.commitAsGenericLbl(presetComboBox, true);
        J3DTheme.commitAsGenericLbl(normalizeV1Btn, true);
        J3DTheme.commitAsGenericLbl(normalizeV2Btn, true);
        J3DTheme.commitAsGenericLbl(jSeparator1, false);
        J3DTheme.commitAsGenericLbl(logLbl, false);
        J3DTheme.commitAsGenericUi(btnPanel);
        J3DTheme.commitAsGenericUi(drawPanel);
        J3DTheme.commitAsGenericUi(floatingPanel);
        J3DTheme.commitAsGenericLbl(jCheckBox1, true);
    }

    void setOrigin(Vector3 origin) {
        this.origin = origin;
    }

    void setV1(Vector3 v1) {
        this.v1 = v1;
    }

    void setV2(Vector3 v2) {
        this.v2 = v2;
    }

    enum SpinnerIdentifier {
        ORIGIN, V1, V2
    }

    private HashMap<SpinnerIdentifier, Triple<JSpinner>> spinnerMap = new HashMap<>();

    private void handleSpinners() {
        QuadFunction<JSpinner, Supplier<Vector3>, BiFunction<Vector3, Double, Vector3>, Consumer<Vector3>, ChangeListener> bi = (sp, v3, applier, setter) -> (ChangeListener) e -> {
            double v = (double) sp.getValue();
            setter.accept(applier.apply(v3.get(), v));
            gm.invalidateAndRemoveAllReactive();
        };

        originXSpinner.addChangeListener(bi.apply(originXSpinner, this::getOrigin, Vector3::setXComponent, this::setOrigin));
        originYSpinner.addChangeListener(bi.apply(originYSpinner, this::getOrigin, Vector3::setYComponent, this::setOrigin));
        originZSpinner.addChangeListener(bi.apply(originZSpinner, this::getOrigin, Vector3::setZComponent, this::setOrigin));

        v1XSpinner.addChangeListener(bi.apply(v1XSpinner, this::getV1, Vector3::setXComponent, this::setV1));
        v1YSpinner.addChangeListener(bi.apply(v1YSpinner, this::getV1, Vector3::setYComponent, this::setV1));
        v1ZSpinner.addChangeListener(bi.apply(v1ZSpinner, this::getV1, Vector3::setZComponent, this::setV1));

        v2XSpinner.addChangeListener(bi.apply(v2XSpinner, this::getV2, Vector3::setXComponent, this::setV2));
        v2YSpinner.addChangeListener(bi.apply(v2YSpinner, this::getV2, Vector3::setYComponent, this::setV2));
        v2ZSpinner.addChangeListener(bi.apply(v2ZSpinner, this::getV2, Vector3::setZComponent, this::setV2));

        spinnerMap.put(SpinnerIdentifier.ORIGIN, new Triple<>(originXSpinner, originYSpinner, originZSpinner));
        spinnerMap.put(SpinnerIdentifier.V1, new Triple<>(v1XSpinner, v1YSpinner, v1ZSpinner));
        spinnerMap.put(SpinnerIdentifier.V2, new Triple<>(v2XSpinner, v2YSpinner, v2ZSpinner));
    }

    private void setSpinnerState(SpinnerIdentifier spinnerIdentifier, Vector3 v) {
        Triple<JSpinner> sp = spinnerMap.get(spinnerIdentifier);
        if (sp == null) return;

        sp.v1().setValue(v.getX());
        sp.v2().setValue(v.getY());
        sp.v3().setValue(v.getZ());

        log("Set " + spinnerIdentifier.name().toLowerCase() + " vector to " + v.toCommandPaletteString() + ".");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnPanel = new javax.swing.JPanel();
        presetComboBox = new javax.swing.JComboBox<>();
        renderBtn = new javax.swing.JButton();
        xCompBtn = new javax.swing.JButton();
        yCompBtn = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        setOriginBtn = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        v2ZSpinner = new javax.swing.JSpinner();
        v2XSpinner = new javax.swing.JSpinner();
        v2YSpinner = new javax.swing.JSpinner();
        v1ZSpinner = new javax.swing.JSpinner();
        v1YSpinner = new javax.swing.JSpinner();
        v1XSpinner = new javax.swing.JSpinner();
        originZSpinner = new javax.swing.JSpinner();
        originYSpinner = new javax.swing.JSpinner();
        originXSpinner = new javax.swing.JSpinner();
        directionAxesLengthSlider = new javax.swing.JSlider();
        queryBtn = new javax.swing.JButton();
        logLbl = new javax.swing.JLabel();
        normalizeV1Btn = new javax.swing.JButton();
        normalizeV2Btn = new javax.swing.JButton();
        drawPanel = new Grid();

        setLayout(new java.awt.BorderLayout());

        btnPanel.setBackground(J3DTheme.UI_SURFACE.color());

        presetComboBox.setBackground(J3DTheme.BACKGROUND.color());
        presetComboBox.setForeground(J3DTheme.TEXT_PRIMARY.color());
        presetComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "XY", "XZ", "YZ" }));
        presetComboBox.setToolTipText("Allows setting of default planes");
        presetComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                presetComboBoxActionPerformed(evt);
            }
        });

        renderBtn.setBackground(J3DTheme.BACKGROUND.color());
        renderBtn.setForeground(J3DTheme.TEXT_PRIMARY.color());
        renderBtn.setText("Render");
        renderBtn.setToolTipText("Renders all construction into the 3D viewport.");
        renderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renderBtnActionPerformed(evt);
            }
        });

        xCompBtn.setBackground(J3DTheme.BACKGROUND.color());
        xCompBtn.setForeground(J3DTheme.TEXT_PRIMARY.color());
        xCompBtn.setText("Set v1");
        xCompBtn.setToolTipText("Change the vector of the 2d plane. Rather use presets.");
        xCompBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xCompBtnActionPerformed(evt);
            }
        });

        yCompBtn.setBackground(J3DTheme.BACKGROUND.color());
        yCompBtn.setForeground(J3DTheme.TEXT_PRIMARY.color());
        yCompBtn.setText("Set v2");
        yCompBtn.setToolTipText("Change the vector of the 2d plane. Rather use presets.");
        yCompBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yCompBtnActionPerformed(evt);
            }
        });

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setOpaque(true);

        setOriginBtn.setBackground(J3DTheme.BACKGROUND.color());
        setOriginBtn.setForeground(J3DTheme.TEXT_PRIMARY.color());
        setOriginBtn.setText("Set Origin");
        setOriginBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setOriginBtnActionPerformed(evt);
            }
        });

        jCheckBox1.setForeground(J3DTheme.TEXT_PRIMARY.color());
        jCheckBox1.setText("Delete");
        jCheckBox1.setToolTipText("Enables delete mode to delete construction.");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        v2ZSpinner.setModel(new javax.swing.SpinnerNumberModel(1.0d, null, null, 0.5d));
        v2ZSpinner.setToolTipText("Change the z component of the v2 vector");

        v2XSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 0.5d));
        v2XSpinner.setToolTipText("Change the x component of the v2 vector");

        v2YSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 0.5d));
        v2YSpinner.setToolTipText("Change the y component of the v2 vector");

        v1ZSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 0.5d));
        v1ZSpinner.setToolTipText("Change the z component of the v1 vector");

        v1YSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 0.5d));
        v1YSpinner.setToolTipText("Change the y component of the v1 vector");

        v1XSpinner.setModel(new javax.swing.SpinnerNumberModel(1.0d, null, null, 0.5d));
        v1XSpinner.setToolTipText("Change the x component of the v1 vector");

        originZSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 0.5d));
        originZSpinner.setToolTipText("Change the z component of the origin of the plane.");

        originYSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 0.5d));
        originYSpinner.setToolTipText("Change the y component of the origin of the plane.");

        originXSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 0.5d));
        originXSpinner.setToolTipText("Change the x component of the origin of the plane.");

        directionAxesLengthSlider.setMaximum(20);
        directionAxesLengthSlider.setMinimum(1);
        directionAxesLengthSlider.setToolTipText("Changes the length of the annotative 3D direction vector arrow heads");
        directionAxesLengthSlider.setValue(10);

        queryBtn.setBackground(J3DTheme.BACKGROUND.color());
        queryBtn.setForeground(J3DTheme.TEXT_PRIMARY.color());
        queryBtn.setMnemonic('Q');
        queryBtn.setText("Query");
        queryBtn.setToolTipText("Queries the scene to display points which lie within the working plane.");
        queryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryBtnActionPerformed(evt);
            }
        });

        logLbl.setForeground(J3DTheme.TEXT_PRIMARY.color());
        logLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logLbl.setText("logs here");

        normalizeV1Btn.setBackground(J3DTheme.BACKGROUND.color());
        normalizeV1Btn.setForeground(J3DTheme.TEXT_PRIMARY.color());
        normalizeV1Btn.setText("norm");
        normalizeV1Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normalizeV1BtnActionPerformed(evt);
            }
        });

        normalizeV2Btn.setBackground(J3DTheme.BACKGROUND.color());
        normalizeV2Btn.setForeground(J3DTheme.TEXT_PRIMARY.color());
        normalizeV2Btn.setText("norm");
        normalizeV2Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normalizeV2BtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout btnPanelLayout = new javax.swing.GroupLayout(btnPanel);
        btnPanel.setLayout(btnPanelLayout);
        btnPanelLayout.setHorizontalGroup(
            btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(btnPanelLayout.createSequentialGroup()
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(btnPanelLayout.createSequentialGroup()
                                .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(xCompBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(yCompBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(normalizeV1Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(normalizeV2Btn, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                            .addComponent(setOriginBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(btnPanelLayout.createSequentialGroup()
                                .addComponent(v2XSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19)
                                .addComponent(v2YSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(v2ZSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(btnPanelLayout.createSequentialGroup()
                                .addComponent(v1XSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19)
                                .addComponent(v1YSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(v1ZSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(btnPanelLayout.createSequentialGroup()
                                .addComponent(originXSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19)
                                .addComponent(originYSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(originZSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(btnPanelLayout.createSequentialGroup()
                                .addComponent(jCheckBox1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(presetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(queryBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(directionAxesLengthSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(renderBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(17, 17, 17)))
                .addContainerGap())
        );
        btnPanelLayout.setVerticalGroup(
            btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnPanelLayout.createSequentialGroup()
                .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(btnPanelLayout.createSequentialGroup()
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(presetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox1)
                            .addComponent(queryBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(directionAxesLengthSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(renderBtn))
                    .addGroup(btnPanelLayout.createSequentialGroup()
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(setOriginBtn)
                            .addComponent(originZSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(originYSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(originXSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(xCompBtn)
                            .addComponent(v1ZSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(v1YSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(v1XSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(normalizeV1Btn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(yCompBtn)
                            .addComponent(v2ZSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(v2YSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(v2XSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(normalizeV2Btn)))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logLbl)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        add(btnPanel, java.awt.BorderLayout.PAGE_START);

        drawPanel.setBackground(J3DTheme.UI_SURFACE.color());
        drawPanel.setMinimumSize(new java.awt.Dimension(496, 397));
        drawPanel.setPreferredSize(new java.awt.Dimension(496, 397));

        javax.swing.GroupLayout drawPanelLayout = new javax.swing.GroupLayout(drawPanel);
        drawPanel.setLayout(drawPanelLayout);
        drawPanelLayout.setHorizontalGroup(
            drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 596, Short.MAX_VALUE)
        );
        drawPanelLayout.setVerticalGroup(
            drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
        );

        add(drawPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void setOriginBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setOriginBtnActionPerformed
        Vector3 v = ask("Input the origin Vector3 e.g. (0, 0, 0)");
        if (v == null) return;
        setSpinnerState(SpinnerIdentifier.ORIGIN, v);
        origin = v;
    }//GEN-LAST:event_setOriginBtnActionPerformed

    private void xCompBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xCompBtnActionPerformed
        Vector3 v = ask("Input v1 plane vector Vector3 e.g. (0, 0, 0)");
        if (v == null) return;
        setSpinnerState(SpinnerIdentifier.V1, v);
        v1 = v;
    }//GEN-LAST:event_xCompBtnActionPerformed

    private void yCompBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yCompBtnActionPerformed
        Vector3 v = ask("Input v2 plane vector Vector3 e.g. (0, 0, 0)");
        if (v == null) return;
        setSpinnerState(SpinnerIdentifier.V2, v);
        v2 = v;
    }//GEN-LAST:event_yCompBtnActionPerformed

    private void renderBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renderBtnActionPerformed
        ArrayList<GridObject<?>> gridObjects1 = new ArrayList<>(gm.getObjects());
                gm.getObjects().stream()
                        .filter(g -> g instanceof Line)
                        .map(g -> (Line) g)
                        .filter(g -> g.getP1().equals(g.getP2()))
                        .forEach(gridObjects1::remove);
        gridObjects1.sort((p1, p2) -> {
            // sort Point over Line
            if (p1 instanceof Point && p2 instanceof Line) {
                return -1;
            } else if (p1 instanceof Line && p2 instanceof Point) {
                return 1;
            }
            return 0;
        });
        ArrayList<GObject> objects = new ArrayList<>();
        for (GridObject<?> g : gridObjects1) {
            GObject go = g.render(
                    new AxisPlane(origin, v1, v2),
                    new ArrayList<>(objects)
            );
            if (go == null) {
                continue;
            }
            objects.add(go);
        }

        Thing thing = null;

        if (gridObjects1.stream().anyMatch(g -> g instanceof ReactivePoint)) {
            // prefer we just take the thing one of the points already is in.

            thing = StaticRefs.getSceneManager().findObjectParent(
                    gridObjects1.stream()
                            .filter(g -> g instanceof ReactivePoint)
                            .map(ReactivePoint.class::cast)
                            .map(ReactivePoint::getGPoint)
                            .findFirst()
                            .get()
            );
        }

        if (thing == null) {

            // find all stuff named render within the entire thing
            ArrayList<FindResult> result = getSceneManager()
                    .finder().find(Thing.class, Finder.nameQuery(), "render");

            // create a new thing.
            thing = new Thing(
                    StaticRefs.getSceneManager().usableLayer(),
                    "render" + result.size()
            );
        }

        thing.addObjs(
                objects.toArray(GObject[]::new)
        );

        // clear.
        gm.getObjects().clear();

    }//GEN-LAST:event_renderBtnActionPerformed

    private void presetComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_presetComboBoxActionPerformed
        String item = presetComboBox.getSelectedItem().toString();
        // XY XZ YZ
        switch (item) {
            case "XY" -> {
                setSpinnerState(SpinnerIdentifier.V1, Vector3.X);
                setSpinnerState(SpinnerIdentifier.V2, Vector3.Y);
                v1 = Vector3.X; v2 = Vector3.Y; // same as default.
            }
            case "XZ" -> {
                setSpinnerState(SpinnerIdentifier.V1, Vector3.X);
                setSpinnerState(SpinnerIdentifier.V2, Vector3.Z);
                v1 = Vector3.X; v2 = Vector3.Z;
            }
            case "YZ" -> {
                setSpinnerState(SpinnerIdentifier.V1, Vector3.Y);
                setSpinnerState(SpinnerIdentifier.V2, Vector3.Z);
                v1 = Vector3.Y; v2 = Vector3.Z;
            }
            default ->  {
                setSpinnerState(SpinnerIdentifier.V1, Vector3.X);
                setSpinnerState(SpinnerIdentifier.V2, Vector3.Y);
                v1 = Vector3.X; v2 = Vector3.Y;
            }
        }
    }//GEN-LAST:event_presetComboBoxActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        gm.setDeleteMode(!gm.isDeleteMode());
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void queryBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queryBtnActionPerformed
        discoverExisting();
    }//GEN-LAST:event_queryBtnActionPerformed

    private void normalizeV1BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normalizeV1BtnActionPerformed
        setSpinnerState(
                SpinnerIdentifier.V1,
                v1.normalize()
        );
    }//GEN-LAST:event_normalizeV1BtnActionPerformed

    private void normalizeV2BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normalizeV2BtnActionPerformed
        setSpinnerState(
                SpinnerIdentifier.V2,
                v2.normalize()
        );
    }//GEN-LAST:event_normalizeV2BtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel btnPanel;
    private javax.swing.JSlider directionAxesLengthSlider;
    private javax.swing.JPanel drawPanel;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel logLbl;
    private javax.swing.JButton normalizeV1Btn;
    private javax.swing.JButton normalizeV2Btn;
    private javax.swing.JSpinner originXSpinner;
    private javax.swing.JSpinner originYSpinner;
    private javax.swing.JSpinner originZSpinner;
    private javax.swing.JComboBox<String> presetComboBox;
    private javax.swing.JButton queryBtn;
    private javax.swing.JButton renderBtn;
    private javax.swing.JButton setOriginBtn;
    private javax.swing.JSpinner v1XSpinner;
    private javax.swing.JSpinner v1YSpinner;
    private javax.swing.JSpinner v1ZSpinner;
    private javax.swing.JSpinner v2XSpinner;
    private javax.swing.JSpinner v2YSpinner;
    private javax.swing.JSpinner v2ZSpinner;
    private javax.swing.JButton xCompBtn;
    private javax.swing.JButton yCompBtn;
    // End of variables declaration//GEN-END:variables
}
