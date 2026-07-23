/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j3d;

import com.j3d.threads.FakeLongTask;
import com.j3d.ui.engine.EngineFrame;
import com.j3d.ui.engine.J3Splash;
import com.j3d.ui.auth.Login;
import com.j3d.ui.home.Projects;

import javax.swing.*;
import java.io.File;

/**
 *
 * @author ACER
 */
public class Startup {
    public static Runnable runnable = () -> {
        Projects frame = new Projects();
        frame.setVisible(true);
    };
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        // Just set the user
        run();
    }

    public static void run() {
        Login login = new Login(runnable);
        login.setVisible(true);
    }

    public static void run(Runnable runnable) {
        Login login = new Login(runnable);
        login.setVisible(true);
    }

    public static void engine(Object o) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        FakeLongTask flt = getFakeLongTask(o);
//        flt.iAmImpatient();
        try {
            flt.run();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static FakeLongTask getFakeLongTask(Object o) {
        J3Splash splash = new J3Splash();
        FakeLongTask flt = new FakeLongTask(() -> {
            splash.setVisible(true);
        }, () -> {}, () -> {
            EngineFrame e = switch (o) {
                case File f -> new EngineFrame(f);
                case Boolean b -> new EngineFrame(b);
                default -> throw new IllegalStateException("Unexpected value: " + o);
            };
            e.setResizable(true);
            e.setVisible(true);
            Timer t = new Timer(3000, ae -> {
                splash.dispose();
            });
            t.setRepeats(false);
            t.start();
        }, 9.3);
        return flt;
    }
}
