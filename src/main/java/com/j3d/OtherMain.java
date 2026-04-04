/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j3d;

import com.j3d.ui.engine.EngineFrame;
import com.j3d.ui.home.Login;

import javax.swing.*;

/**
 *
 * @author ACER
 */
public class OtherMain {
    public static Runnable runnable = () -> {
        EngineFrame frame = new EngineFrame();
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

        Login login = new Login(runnable);
        login.setVisible(true);
    }
}
