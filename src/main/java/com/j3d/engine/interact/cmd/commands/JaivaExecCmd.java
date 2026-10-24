package com.j3d.engine.interact.cmd.commands;

import com.j3d.StaticRefs;
import com.j3d.engine.interact.cmd.Invoker;
import com.j3d.engine.interact.cmd.args.TaggedArgValue;
import com.j3d.engine.interact.cmd.base.Command;
import com.j3d.engine.scene.nodes.geometry.GObject;
import com.j3d.jaiva.JaivaRunException;
import com.j3d.jaiva.TypeConverter;
import com.j3d.ui.SafeJLabel;
import com.jaiva.errors.JaivaException;
import com.yetnt.utils.builders.AnsiColour;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;

public class JaivaExecCmd extends Command {

    public JaivaExecCmd() {
        super("jaiva-exec", "Jaiva Execution command");
        this.addNoArgUsage().parseUsages();
    }

    @Override
    public void run(@NotNull Invoker invoker, SafeJLabel logLabel, String aliasUsed, @NotNull Object[] args, @NotNull ArrayList<TaggedArgValue<?>> taggedArgs) {
        super.run(invoker, logLabel, aliasUsed, args, taggedArgs);

        ArrayList<Object> arr = new ArrayList<>();
        if (args.length > 0 && args[0] instanceof GObject g) {
            arr = TypeConverter.toObject(g);
        }
        try {
            StaticRefs.getJaivaInstanceManager().getCommandBundler().execute(
                    "C:\\Users\\ACER\\Documents\\code\\Jaiva3dEngine\\src\\main\\resources\\file.jiv",
                    arr // input to pass to the file that jaiva gets via getCallerValue()
            );
        } catch (Exception e) {
            if (e instanceof JaivaException j) {
                logLabel.setText("Jaiva Execution Failed");
                StaticRefs.getErrs().handle(new JaivaRunException(j));
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
