package com.j3d.ui.engine.contextMenu;

import com.j3d.ui.engine.ContextMenu;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.ui.theme.updator.Locator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ContextSubMenu extends JMenu {

    ArrayList<Locator> locators = new ArrayList<>();

    public ContextSubMenu(String text) {
        super(text);
        setFont(Font.getFont("Segoe UI"));
        setBackground(J3DTheme.UI_SURFACE.color());
//        setForeground(J3DTheme.TEXT_PRIMARY.color());
    }

    public ArrayList<Locator> getLocators() {
        return locators;
    }

    public ContextSubMenu item(String text, int mnemonic, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setMnemonic(mnemonic);
        item.setFont(Font.getFont("Segoe UI"));
        item.addActionListener(e -> action.run());

        add(item);
        return this;
    }

    public ContextSubMenu separator() {
        addSeparator();
        return this;
    }
}