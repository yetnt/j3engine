import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.math.MathException;
import com.j3d.engine.scene.DefaultObjectDeletionException;
import com.j3d.engine.scene.copy.InvalidCopyException;
import com.j3d.errors.J3DError;
import com.j3d.gen.settings.PrefsGenException;
import com.j3d.storage.errs.ProjectFileException;

import java.util.ArrayList;
import java.util.List;

public class playground {
    public static void main(String[] args) {
        CommandsManager.commands.getCommands().forEach(
                cmd -> {
                    System.out.println(cmd.getClass().getSimpleName());
                    System.out.println("Aliuases: " + cmd.aliases);
                    System.out.println("Args: " + cmd.args);
                    System.out.println("Usages:" );
                    cmd.getUsages().forEach(u -> System.out.println("\t (alias) " + u));
                    System.out.println();
                }
        );
    }
}
