package com.j3d.ui.engine;

import com.j3d.ui.engine.contextMenu.ContextSubMenu;
import com.j3d.ui.theme.J3DTheme;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ContextMenu extends JPopupMenu {
    public ContextMenu() {
        super("Context Menu");
        styleSelf();
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
//        item.setAccelerator(accelerator);
        item.setFont(Font.getFont("Segoe UI"));
//        item.setForeground(J3DTheme.TEXT_PRIMARY.color());
        item.setBackground(J3DTheme.UI_SURFACE.color());
        J3DTheme.commitAsGenericUi(this);
        item.addActionListener(e -> action.run());
        add(item);
        separator();
        return this;
    }

    public ContextMenu menu(String text, Consumer<ContextSubMenu> builder) {
        ContextSubMenu menu = new ContextSubMenu(text);
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
