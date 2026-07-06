import com.j3d.Static;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.commands.debug.DebugCmd;
import com.j3d.engine.interact.cmd.commands.transform.TransformCmd;
import com.j3d.errors.Err;
import com.j3d.errors.ErrorHandler;
import com.j3d.errors.J3DError;
import com.j3d.utility.Parsing;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;

public class playground {
    public static void main(String[] args) {
//        Static.none();
//        CommandsManager.commands.getCommands().forEach(
//                c -> {
//                    c.getUsages().values().forEach(u -> {
//                        System.out.println(c.aliases.getFirst() + " " + u);
//                    });
//                }
//        );
        String input = "tp (0, 0, 1) #FFEEFFrr# arg:\"ol dsfl\" \"something me (\" dandling (0 . improper";
        System.out.println(Parsing.split(input, ' '));
    }
}
