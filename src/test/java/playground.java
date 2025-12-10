import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.avail.DebugCmd;
import com.j3d.engine.interact.cmd.avail.PointCmd;
import com.j3d.engine.interact.cmd.base.Command;

public class playground {
    public static void main(String[] args) {
        for (Command cmd : new CommandsManager().commands.values()) {
            for (String usage : cmd.getUsages().values()) {
                System.out.println(cmd.aliases.getFirst() + usage);
            }
        }
    }
}
