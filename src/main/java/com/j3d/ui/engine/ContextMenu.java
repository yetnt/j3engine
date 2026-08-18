package com.j3d.ui.engine;

import com.j3d.ui.engine.contextMenu.ContextSubMenu;
import com.j3d.ui.theme.J3DTheme;
import com.j3d.ui.theme.updator.Locator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ContextMenu extends JPopupMenu {

    Consumer<ContextMenu> builder;
    ArrayList<Locator> locators = new ArrayList<>();

    public ContextMenu(Consumer<ContextMenu> builder) {
        super("Context Menu");
        this.builder = builder;
        styleSelf();
    }

    public void build() {
        // remove all elements currently within
        removeAll();
        // build
        builder.accept(this);
    }

    @Override
    public void removeAll() {
        super.removeAll();
        locators.clear();
    }

    public void styleSelf() {
        J3DTheme.commitAsGenericUi(this);
        setBackground(J3DTheme.UI_SURFACE.color());
        setForeground(J3DTheme.TEXT_PRIMARY.color());
        setFont(Font.getFont("Segoe UI"));
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    public ContextMenu item(String text, int keyMnemonic, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setMnemonic(keyMnemonic);
        item.setFont(Font.getFont("Segoe UI"));
        item.setBackground(J3DTheme.UI_SURFACE.color());
        Locator l = J3DTheme.commitAsGenericUi(this);
        locators.add(l);
        item.addActionListener(e -> action.run());
        add(item);
        separator();
        return this;
    }

    public ContextMenu menu(String text, Consumer<ContextSubMenu> builder) {
        ContextSubMenu menu = new ContextSubMenu(text);
        locators.addAll(menu.getLocators());
        builder.accept(menu);

        add(menu);
        separator();

        return this;
    }

    public ContextMenu separator() {
        addSeparator();
        return this;
    }
}
