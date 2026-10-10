/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j3d.ui.home;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author yetnt
 */
public class ProjectsScrollPanel extends JPanel {
    public ProjectsScrollPanel() {
        super();
    }

    @Override
    public Component add(Component comp) {
        Component r = super.add(comp);
        set(comp.getPreferredSize().width);
        return r;
    }

    @Override
    public void remove(Component comp) {
        super.remove(comp);
        set(-comp.getPreferredSize().width);
    }
    
    public void set(int w) {
        Dimension dim = new Dimension(this.getMaximumSize().width+w, 20);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setMinimumSize(dim);
    }
    
    
}
