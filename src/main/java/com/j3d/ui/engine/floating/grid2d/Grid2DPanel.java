/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.j3d.ui.engine.floating.grid2d;

import com.j3d.StaticRefs;
import com.j3d.engine.math.ConversionProperties;
import com.j3d.engine.math.Dim;
import com.j3d.engine.math.ScreenPoint;
import com.j3d.engine.math.CartesianPoint;
import com.j3d.engine.math.plane.AxisPlane;
import com.j3d.engine.math.matrix.Vector3;
import com.j3d.engine.scene.find.FindResult;
import com.j3d.engine.scene.find.Finder;
import com.j3d.engine.scene.nodes.Thing;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.gen.grid.GridObject;
import com.j3d.gen.grid.Line;
import com.j3d.gen.grid.Point;
import com.j3d.ui.engine.FloatingPanel;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generic.SamePair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.j3d.StaticRefs.*;

/**
 *
 * @author yetnt
 */
public class Grid2DPanel extends javax.swing.JPanel {
    
    public FloatingPanel floatingPanel = new FloatingPanel("History Panel");
    public static CartesianPoint mousePosInPanel = new CartesianPoint(0, 0);

    private ArrayList<GridObject<?>> gridObjects = new ArrayList<>();
    private final int radius = 10;
    private final UUID overlapId = UUID.randomUUID();
    private HashMap<UUID, Consumer<Graphics2D>> temp = new HashMap<>();

    private double scale = 20;

    // default parameters.
    private Vector3 v1 = Vector3.X;
    private Vector3 v2 = Vector3.Z;
    private Vector3 origin = Vector3.ZERO;

    private boolean deleteMode = false;
    private CartesianPoint to;
    private CartesianPoint from;

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

        ((Grid)drawPanel).setConsumer((g) -> {
            // Draw the origin point
            ConversionProperties props = new ConversionProperties(
                    scale,
                    new Dim(drawPanel.getWidth(), drawPanel.getHeight())
            );
            CartesianPoint cp = new CartesianPoint(0, 0);
            ScreenPoint sp =
                    cp.toScreen(
                            props
                    );
            g.setColor(J3DTheme.TEXT_PRIMARY.color());
            g.fillOval(sp.x - radius/2, sp.y - radius/2, radius, radius);

            // Draw all the grid lines
            pts.forEach(p -> {
                g.drawLine(
                        p.first.x,
                        p.first.y,
                        p.second.x,
                        p.second.y
                );
            });
            gridObjects.forEach(
                    gr -> gr.draw(g, props)
            );
            temp.forEach((i, s) -> {
                s.accept(g);
            });
        });
        getSceneManager().scheduleOverlap(
                overlapId,
                (g) -> {
                    if (floatingPanel.isHidden()) return;
                    drawMouse(g, mousePosInPanel, new AxisPlane(origin, v1, v2));
                    Vector3 point = new AxisPlane(origin, v1, v2)
                            .toWorld(mousePosInPanel);
                    ScreenPoint sp =
                            point.toPoint(getCamera())
                                            .toScreen();
                    g.setColor(Color.cyan);
                    g.drawOval(sp.x - radius, sp.y - radius, radius*2, radius*2);
                    g.setColor(Color.white);
                    getMainPanel().repaint();
                    gridObjects.forEach(gr -> gr.drawWorld(g, new AxisPlane(origin, v1, v2)));
                }
        );
        this.addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        gridRepaint();
                    }
                }
        );
        grid();
        theme();
    }

    public static void drawMouse(Graphics2D g, CartesianPoint mousePos, AxisPlane plane) {
        int arrowSize = 1;
        double sideLength = 0.25;
        // not really the side length since this is more the offset to make the diagonal
        // which is the actual line we care about.
        // the actual side length will be sqrt(4)
        double x = mousePos.x;
        double y = mousePos.y;
        ArrayList<CartesianPoint> points = new ArrayList<>(List.of(mousePos));

        // tip is the mouse position (first element)
        points.add(new CartesianPoint(x, y - 3*arrowSize)); // tail
        points.add(new CartesianPoint(x - sideLength, y - sideLength)); // Bottom-left
        points.add(new CartesianPoint(x + sideLength, y - sideLength)); // Bottom-right

        ArrayList<Vector3> points2 = points
                .stream().map(plane::toWorld)
                .collect(Collectors.toCollection(ArrayList::new));

        getSceneManager().drawLine3D(
                g, points2.getFirst(), points2.get(1), getCamera()
        );
        getSceneManager().drawLine3D(
                g, points2.getFirst(), points2.get(2), getCamera()
        );
        getSceneManager().drawLine3D(
                g, points2.getFirst(), points2.get(3), getCamera()
        );

        getSceneManager().drawText3D(
                g, points2.getFirst(),
                mousePos.friendlyString() +" -> " + points2.getFirst().toCommandPaletteString(),
                getCamera(),
                J3DTheme.UI_SURFACE.color(),
                J3DTheme.TEXT_PRIMARY.color()
        );
    }

    private CartesianPoint toPoint(ScreenPoint p, boolean round) {
        Dim dim = getGrid().sizeDim();
        CartesianPoint cp = p.toPoint(new ConversionProperties(scale, dim));
        if (round) {
            return new CartesianPoint(
                    Math.round(cp.x),
                    Math.round(cp.y)
            );
        }

        return cp;
    }

    private void gridRepaint() {
        pts.clear();
        grid();
        drawPanel.repaint();
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
                        if (deleteMode) {
                            delete(snapped);
                        } else existing(() -> new Point(snapped));
                        System.out.println(snapped);
                        drawPanel.repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
//                        t.requestFocus();
                        temp.remove(drag);
                        ScreenPoint sp = new ScreenPoint(e.getX(), e.getY());
                        CartesianPoint snapped = toPoint(sp, true);

                        ScreenPoint mousePos = mousePosInPanel.toScreen(new ConversionProperties(
                                scale,
                                getGrid().sizeDim()
                        ));
                        CartesianPoint cp = toPoint(mousePos, true);

                        System.out.println("From " + cp + " to " + snapped);
                        Line l = new Line(snapped, cp);
                        if (deleteMode) {
                            delete(l);
                        } else {
                            existing(() -> new Point(snapped));
                            existing(() -> new Point(cp));
                            gridObjects.add(l);
                        }
                    }

                }
        );
        drawPanel.addMouseMotionListener(
                new MouseAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        super.mouseMoved(e);
                        mousePosInPanel =
                                new ScreenPoint(e.getX(), e.getY())
                                        .toPoint(new ConversionProperties(scale, getGrid().sizeDim()));
                        drawPanel.repaint();
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        super.mouseDragged(e);
                        ScreenPoint sp = new ScreenPoint(e.getX(), e.getY());
                        to = toPoint(sp, true);

                        ConversionProperties cp = new ConversionProperties(
                                scale,
                                getGrid().sizeDim()
                        );
                        from = toPoint(mousePosInPanel.toScreen(cp), true);

                        gridRepaint();
                        if (!temp.containsKey(drag)) {
                            temp.put(drag, (g) -> Line.drawLine(
                                    () -> deleteMode ? Color.RED : J3DTheme.TEXT_SECONDARY.color(),
                                    () -> to,
                                    () -> from,
                                    g,
                                    cp
                            ));
                        }
                    }
                }
        );
        drawPanel.addMouseWheelListener(
                new MouseAdapter() {
                    @Override
                    public void mouseWheelMoved(MouseWheelEvent e) {
                        super.mouseWheelMoved(e);
                        scale *= Math.pow(1.1, -e.getWheelRotation());
                        gridRepaint();
                    }
                }
        );
    }

    private void existing(Supplier<Point> p) {
        Point o = p.get();
        Point p2 = gridObjects
                .stream()
                .filter(
                        o2 -> o2 instanceof Point
                )
                .map(
                        o2 -> (Point)o2
                )
                .filter(
                        point -> point.getPoint().equals(o.getPoint())
                )
                .findFirst()
                .orElse(null);
        if (p2 == null) {
            gridObjects.add(o);
        }
    }

    private void delete(CartesianPoint snapped) {
        // look for a point at the current position, if any matches delete said points.
        new ArrayList<>(gridObjects).stream()
                .filter(
                        o -> o instanceof Point
                )
                .map(
                        o -> (Point) o
                )
                .filter(
                        point -> point.getPoint().equals(snapped)
                )
                .forEach(gridObjects::remove);
    }

    private void delete(Line l) {
        // if this line intersects with any other line in this list, delete that line
        new ArrayList<>(gridObjects)
                .stream()
                .peek(o -> {
                    if (o instanceof Point p)
                        if (p.getPoint().equals(l.getP1()) || p.getPoint().equals(l.getP2()))
                            gridObjects.remove(p);
                })
                .filter(
                        lw -> lw instanceof Line
                )
                .map(
                        lw -> (Line) lw
                )
                .filter(
                        lw ->
                                (lw.getP1().equals(l.getP1()) && lw.getP2().equals(l.getP2()))
                        || (lw.getP1().equals(l.getP2()) && lw.getP2().equals(l.getP1()))
                        || Line.intersects(l, lw)
                )
                .forEach(gridObjects::remove);
    }

    public void toggleHidden()  {
        floatingPanel.toggleHidden();
    }

    // grid stuff
    private ArrayList<SamePair<ScreenPoint>> pts = new ArrayList<>();

    private Grid getGrid() {
        return (Grid) drawPanel;
    }

    private void  regInGrid(CartesianPoint p, CartesianPoint s) {
        Dim dim = getGrid().sizeDim();
        ConversionProperties conversionProperties = new ConversionProperties(scale, dim);
        pts.add(
                new SamePair<>(
                        p.toScreen(conversionProperties),
                        s.toScreen(conversionProperties)
                )
        );
    }

    private void grid() {
        Dim panelSize = getGrid().sizeDim();
        if (panelSize.width == 0 || panelSize.height == 0) return;

        // Convert screen corners to Cartesian points to find the bounds
        ConversionProperties conversionProperties = new ConversionProperties(scale, panelSize);
        CartesianPoint topLeft = new ScreenPoint(0, 0).toPoint(conversionProperties);
        CartesianPoint bottomRight = new ScreenPoint(panelSize.width, panelSize.height).toPoint(conversionProperties);

        double startX = Math.floor(topLeft.x);
        double endX = Math.ceil(bottomRight.x);
        double startY = Math.floor(bottomRight.y);
        double endY = Math.ceil(topLeft.y);

        // Draw vertical lines
        for (double x = startX; x <= endX; x++) {
            regInGrid(new CartesianPoint(x, startY), new CartesianPoint(x, endY));
        }

        // Draw horizontal lines
        for (double y = startY; y <= endY; y++) {
            regInGrid(new CartesianPoint(startX, y), new CartesianPoint(endX, y));
        }
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
        }
        return v;
    }

    public void theme() {
        J3DTheme.commitAsGenericLbl(propertiesLbl, false);
        J3DTheme.commitAsGenericLbl(jLabel1, false);
        J3DTheme.commitAsGenericLbl(renderBtn, true);
        J3DTheme.commitAsGenericLbl(xCompBtn, true);
        J3DTheme.commitAsGenericLbl(yCompBtn, true);
        J3DTheme.commitAsGenericLbl(setOriginBtn, true);
        J3DTheme.commitAsGenericLbl(presetComboBox, true);
        J3DTheme.commitAsGenericLbl(jLabel2, false);
        J3DTheme.commitAsGenericLbl(jSeparator1, false);
        J3DTheme.commitAsGenericUi(btnPanel);
        J3DTheme.commitAsGenericUi(drawPanel);
        J3DTheme.commitAsGenericUi(floatingPanel);
        J3DTheme.commitAsGenericLbl(jCheckBox1, true);
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
        jLabel1 = new javax.swing.JLabel();
        renderBtn = new javax.swing.JButton();
        propertiesLbl = new javax.swing.JLabel();
        xCompBtn = new javax.swing.JButton();
        yCompBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        setOriginBtn = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        drawPanel = new Grid();

        setLayout(new java.awt.BorderLayout());

        btnPanel.setBackground(J3DTheme.UI_SURFACE.color());

        presetComboBox.setBackground(J3DTheme.BACKGROUND.color());
        presetComboBox.setForeground(J3DTheme.TEXT_PRIMARY.color());
        presetComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "XY", "XZ", "YZ" }));
        presetComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                presetComboBoxActionPerformed(evt);
            }
        });

        jLabel1.setForeground(J3DTheme.TEXT_PRIMARY.color());
        jLabel1.setText("Presets");

        renderBtn.setBackground(J3DTheme.BACKGROUND.color());
        renderBtn.setForeground(J3DTheme.TEXT_PRIMARY.color());
        renderBtn.setText("Render");
        renderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renderBtnActionPerformed(evt);
            }
        });

        propertiesLbl.setForeground(J3DTheme.TEXT_PRIMARY.color());
        propertiesLbl.setText("Grid Properties Label (like obj count)");

        xCompBtn.setBackground(J3DTheme.BACKGROUND.color());
        xCompBtn.setForeground(J3DTheme.TEXT_PRIMARY.color());
        xCompBtn.setText("Change X component");
        xCompBtn.setToolTipText("Change the vector of the 2d plane. Rather use presets.");
        xCompBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xCompBtnActionPerformed(evt);
            }
        });

        yCompBtn.setBackground(J3DTheme.BACKGROUND.color());
        yCompBtn.setForeground(J3DTheme.TEXT_PRIMARY.color());
        yCompBtn.setText("Change Y component");
        yCompBtn.setToolTipText("Change the vector of the 2d plane. Rather use presets.");
        yCompBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yCompBtnActionPerformed(evt);
            }
        });

        jLabel2.setForeground(J3DTheme.TEXT_PRIMARY.color());
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Grid Definition");

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setOpaque(true);

        setOriginBtn.setBackground(J3DTheme.BACKGROUND.color());
        setOriginBtn.setForeground(J3DTheme.TEXT_PRIMARY.color());
        setOriginBtn.setText("Set Grid Origin");
        setOriginBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setOriginBtnActionPerformed(evt);
            }
        });

        jCheckBox1.setForeground(J3DTheme.TEXT_PRIMARY.color());
        jCheckBox1.setText("delete mode");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout btnPanelLayout = new javax.swing.GroupLayout(btnPanel);
        btnPanel.setLayout(btnPanelLayout);
        btnPanelLayout.setHorizontalGroup(
            btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(btnPanelLayout.createSequentialGroup()
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(xCompBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(yCompBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(presetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(btnPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(renderBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(propertiesLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(btnPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(setOriginBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        btnPanelLayout.setVerticalGroup(
            btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(btnPanelLayout.createSequentialGroup()
                        .addComponent(setOriginBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(propertiesLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(renderBtn)
                            .addComponent(jCheckBox1)))
                    .addGroup(btnPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(xCompBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(presetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(yCompBtn)))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );

        add(btnPanel, java.awt.BorderLayout.NORTH);

        drawPanel.setBackground(J3DTheme.UI_SURFACE.color());
        drawPanel.setMinimumSize(new java.awt.Dimension(496, 397));
        drawPanel.setPreferredSize(new java.awt.Dimension(496, 397));

        javax.swing.GroupLayout drawPanelLayout = new javax.swing.GroupLayout(drawPanel);
        drawPanel.setLayout(drawPanelLayout);
        drawPanelLayout.setHorizontalGroup(
            drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 553, Short.MAX_VALUE)
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
        origin = v;
    }//GEN-LAST:event_setOriginBtnActionPerformed

    private void xCompBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xCompBtnActionPerformed
        Vector3 v = ask("Input v1 plane vector Vector3 e.g. (0, 0, 0)");
        if (v == null) return;
        v1 = v;
    }//GEN-LAST:event_xCompBtnActionPerformed

    private void yCompBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yCompBtnActionPerformed
        Vector3 v = ask("Input v2 plane vector Vector3 e.g. (0, 0, 0)");
        if (v == null) return;
        v2 = v;
    }//GEN-LAST:event_yCompBtnActionPerformed

    private void renderBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renderBtnActionPerformed
        ArrayList<GridObject<?>> gridObjects1 = new ArrayList<>(gridObjects);
                gridObjects.stream()
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
            if (go == null) continue;
            objects.add(go);
        }

        // find all stuff named render within the entire thing
        ArrayList<FindResult> result = getSceneManager()
                .finder.find(Thing.class, Finder.nameQuery(), "render");

        // create a new thing.
        Thing thing = new Thing(
                StaticRefs.getSceneManager().usableLayer(),
                "render" + result.size()
        );

        thing.addObjs(
                objects.toArray(GObject[]::new)
        );

        // clear.
        gridObjects.clear();

    }//GEN-LAST:event_renderBtnActionPerformed

    private void presetComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_presetComboBoxActionPerformed
        String item = presetComboBox.getSelectedItem().toString();
        // XY XZ YZ
        switch (item) {
            case "XY" -> {
                v1 = Vector3.X; v2 = Vector3.Y; // same as default.
            }
            case "XZ" -> {
                v1 = Vector3.X; v2 = Vector3.Z;
            }
            case "YZ" -> {
                v1 = Vector3.Y; v2 = Vector3.Z;
            }
            default ->  {
                v1 = Vector3.X; v2 = Vector3.Y;
            }
        }
    }//GEN-LAST:event_presetComboBoxActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        deleteMode = !deleteMode;
    }//GEN-LAST:event_jCheckBox1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel btnPanel;
    private javax.swing.JPanel drawPanel;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JComboBox<String> presetComboBox;
    private javax.swing.JLabel propertiesLbl;
    private javax.swing.JButton renderBtn;
    private javax.swing.JButton setOriginBtn;
    private javax.swing.JButton xCompBtn;
    private javax.swing.JButton yCompBtn;
    // End of variables declaration//GEN-END:variables
}
