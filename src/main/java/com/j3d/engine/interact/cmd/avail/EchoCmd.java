package com.j3d.engine.interact.cmd.avail;

import com.j3d.Main;
import com.j3d.engine.interact.cmd.base.Command;

public class EchoCmd extends Command {
    public EchoCmd() {
        super("echo", "Echoes the input string.");
        this.aliases("e", "say")
            .args(
                new com.j3d.engine.interact.cmd.base.TypedArg("message", "The message to echo", false, String.class)
            );
    }
    @Override
    public void run(javax.swing.JLabel logLabel, Object... args) {
        if (args.length != 1 || !(args[0] instanceof String message)) {
            logLabel.setText("Invalid arguments. Usage: echo <message: String>");
            return;
        }
        logLabel.setText(message);
        Main.log.println(message);
    }
}
