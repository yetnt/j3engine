/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.j3d;

import com.j3d.artefacts.Artefact;
import com.j3d.gen.settings.Settings;
import com.j3d.ui.theme.J3DTheme;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        StaticRefs.none();
        J3DTheme.loadTheme(
                Settings.changeTheme.getValue()
        );
        if (args.length > 0) {
            String path = args[0];
            Startup.engine(new File(path), false);
        } else
            Startup.run();
    }

    public static void test(Artefact artifact) {
        // Static Refs was already instantiated.
        J3DTheme.loadTheme(
                Settings.changeTheme.getValue()
        );

        if (artifact == null) Startup.run(); // go through projects frame
        else Startup.engine(artifact, false);
    }
}
