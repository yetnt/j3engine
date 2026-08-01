import com.j3d.engine.interact.cmd.commands.copyPaste.PasteCmd;
import com.j3d.gen.docs.reader.J3DocsReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class playground {
    public static void main(String[] args) {
        PasteCmd p = new PasteCmd();
        System.out.println(
                Arrays.toString(p.getUsages().values().toArray(new String[0]))
        );
    }
}
