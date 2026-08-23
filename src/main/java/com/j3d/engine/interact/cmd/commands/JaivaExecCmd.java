package com.j3d.engine.interact.cmd.commands;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.ui.SafeJLabel;
import com.j3d.utility.Parsing;
import com.j3d.utility.generators.JLabelRichText;
import com.jaiva.errors.JaivaException;
import com.jaiva.utils.generator.CCol;

import javax.swing.*;
import java.util.ArrayList;

public class JaivaExecCmd extends Command {

    public JaivaExecCmd() {
        super("jaiva-exec", "Jaiva Execution command");
        this.addNoArgUsage().parseUsages();
    }

    @Override
    public void run(Invoker invoker, SafeJLabel logLabel, String aliasUsed, Object[] args, ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);

        try {
            StaticRefs.getJaivaInstanceManager().getCommandBundler().execute(
                    "C:\\Users\\ACER\\Documents\\code\\Jaiva3dEngine\\lib\\me.jiv",
                    1
            );
        } catch (Exception e) {
            if (e instanceof JaivaException j) {
                logLabel.setText("Jaiva Execution Failed");
                JOptionPane.showMessageDialog(StaticRefs.getMainFrame(), Parsing.removeCCol(j.getRawMessage()),  "Jaiva Execution Failed", JOptionPane.ERROR_MESSAGE);
                StaticRefs.getLog().error("Jaiva Execution Failed: " + Parsing.removeCCol(j.getRawMessage()));
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
