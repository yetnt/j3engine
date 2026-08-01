import com.j3d.engine.interact.cmd.commands.clipboard.PasteCmd;

import java.util.Arrays;

public class playground {
    public static void main(String[] args) {
        PasteCmd p = new PasteCmd();
        System.out.println(
                Arrays.toString(p.getUsages().values().toArray(new String[0]))
        );
    }
}
