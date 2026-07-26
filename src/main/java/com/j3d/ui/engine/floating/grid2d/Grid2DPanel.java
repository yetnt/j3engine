/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.j3d.ui.engine.floating.grid2d;

import com.j3d.engine.geometry.Dim;
import com.j3d.engine.geometry.ScreenPoint;
import com.j3d.engine.geometry.geo2d.CartesianPoint;
import com.j3d.engine.geometry.geo3d.AxisPlane;
import com.j3d.engine.geometry.geo3d.matrix.Vector3;
import com.j3d.engine.interact.input.keyboard.KeyBindings;
import com.j3d.ui.engine.FloatingPanel;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.utility.generic.SamePair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.j3d.StaticRefs.*;

/**
 *
 * @author yetnt
 */
public class Grid2DPanel extends javax.swing.JPanel {
    
    public FloatingPanel floatingPanel = new FloatingPanel("History Panel");
    public static CartesianPoint mousePosInPanel = new CartesianPoint(0, 0);

    private final int radius = 10;
    private final UUID overlapId = UUID.randomUUID();

    private double scale = 20;
    private final KeyBindings keyBindings;

    // default parameters.
    private Vector3 v1 = Vector3.X;
    private Vector3 v2 = Vector3.Z;
    private Vector3 origin = Vector3.ZERO;

    /**
     * Creates new form Grid2DPanel
     */
    public Grid2DPanel() {
        initComponents();
        keyBindings = new KeyBindings(this.getInputMap(), this.getActionMap());
        floatingPanel.finish(this, (c) -> {
            if (!(c instanceof JPanel p)) return;
            p.setBounds(0, 0, p.getPreferredSize().width, p.getPreferredSize().height);
            p.setVisible(true);
        });
        initMouse();

        ((Grid)drawPanel).setConsumer((g) -> {
            CartesianPoint cp = new CartesianPoint(0, 0);
            ScreenPoint sp =
                    cp.toScreenWithProps(
                            scale,
                            new Dim(drawPanel.getWidth(), drawPanel.getHeight())
                    );

            g.drawOval(sp.x - radius, sp.y - radius, radius*2, radius*2);
            pts.forEach(p -> {
                g.drawLine(
                        p.first.x,
                        p.first.y,
                        p.second.x,
                        p.second.y
                );
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
                                            .toScreen(getSceneManager());
                    g.setColor(Color.cyan);
                    g.drawOval(sp.x - radius, sp.y - radius, radius*2, radius*2);
                    g.setColor(Color.white);
                    getMainPanel().repaint();
                }
        );
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
        CartesianPoint cp = p.toPointWithProps(scale, dim);
        if (round) {
            return new CartesianPoint(
                    Math.round(cp.x),
                    Math.round(cp.y)
            );
        }

        return cp;
    }

    private void gridRepaint(MouseEvent e) {
        pts.clear();
        grid(new ScreenPoint(e.getX(), e.getY()));
    }

    private void initMouse() {
        final Grid2DPanel t = this;
        drawPanel.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        t.requestFocus();
                        ScreenPoint sp = new ScreenPoint(e.getX(), e.getY());
                        System.out.println(toPoint(sp, false));
                        drawPanel.repaint();
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
                                        .toPointWithProps(scale, getGrid().sizeDim());
                        gridRepaint(e);
                        drawPanel.repaint();
                    }
                }
        );
        drawPanel.addMouseWheelListener(
                new MouseAdapter() {
                    @Override
                    public void mouseWheelMoved(MouseWheelEvent e) {
                        super.mouseWheelMoved(e);
//                        scale = Math.max(1, scale+e.getWheelRotation());
                        scale *= Math.pow(1.1, -e.getWheelRotation());
                        gridRepaint(e);
                        drawPanel.repaint();
                    }
                }
        );
    }

    public void toggleHidden()  {
        floatingPanel.toggleHidden();
    }

    // grid stuff
    private ArrayList<SamePair<ScreenPoint>> pts = new ArrayList<>();

    private Grid getGrid() {
        return (Grid) drawPanel;
    }

    private void regInGrid(CartesianPoint p, CartesianPoint s) {
        Dim dim = getGrid().sizeDim();
        pts.add(
                new SamePair<>(
                        p.toScreenWithProps(scale, dim),
                        s.toScreenWithProps(scale, dim)
                )
        );
    }

    private void grid(ScreenPoint sp) {
        CartesianPoint cp = toPoint(sp, true);
        int offset = 4;
        CartesianPoint rightBottom = new CartesianPoint(
                cp.x + offset,
                cp.y - offset
        );
        CartesianPoint leftTop = new CartesianPoint(
                cp.x - offset,
                cp.y + offset
        );
        CartesianPoint rightTop = new CartesianPoint(
                cp.x + offset,
                cp.y + offset
        );
        for (int i = offset*2; i >= 0; i--) {
            regInGrid(
                    new CartesianPoint(leftTop.x, leftTop.y + i - offset),
                    new CartesianPoint(rightTop.x, rightTop.y + i - offset)
            );
            regInGrid(
                    new CartesianPoint(rightBottom.x - i, rightBottom.y + offset),
                    new CartesianPoint(rightTop.x - i, rightTop.y + offset)
            );
        }
        drawPanel.repaint();
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
        drawPanel = new Grid();

        setLayout(new java.awt.BorderLayout());

        presetComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "XY", "XZ", "YZ" }));
        presetComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                presetComboBoxActionPerformed(evt);
            }
        });

        jLabel1.setText("Presets");

        renderBtn.setText("Render");
        renderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renderBtnActionPerformed(evt);
            }
        });

        propertiesLbl.setText("Grid Properties Label (like obj count)");

        xCompBtn.setText("Change X component");
        xCompBtn.setToolTipText("Change the vector of the 2d plane. Rather use presets.");
        xCompBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xCompBtnActionPerformed(evt);
            }
        });

        yCompBtn.setText("Change Y component");
        yCompBtn.setToolTipText("Change the vector of the 2d plane. Rather use presets.");
        yCompBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yCompBtnActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Grid Definition");

        jSeparator1.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator1.setOpaque(true);

        setOriginBtn.setText("Set Grid Origin");
        setOriginBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setOriginBtnActionPerformed(evt);
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
                        .addGap(93, 93, 93)
                        .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(renderBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(propertiesLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(7, Short.MAX_VALUE))
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
                        .addComponent(renderBtn))
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

        drawPanel.setMinimumSize(new java.awt.Dimension(496, 397));
        drawPanel.setPreferredSize(new java.awt.Dimension(496, 397));

        javax.swing.GroupLayout drawPanelLayout = new javax.swing.GroupLayout(drawPanel);
        drawPanel.setLayout(drawPanelLayout);
        drawPanelLayout.setHorizontalGroup(
            drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 547, Short.MAX_VALUE)
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
        // TODO add your handling code here:
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel btnPanel;
    private javax.swing.JPanel drawPanel;
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
