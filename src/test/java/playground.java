import com.j3d.Static;
import com.j3d.engine.interact.cmd.CommandParser;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.commands.debug.DebugCmd;
import com.j3d.engine.interact.cmd.commands.transform.TransformCmd;
import com.j3d.errors.Err;
import com.j3d.errors.ErrorHandler;
import com.j3d.errors.J3DError;
import com.j3d.gen.docs.J3DocsReader;
import com.j3d.utility.Parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;

public class playground {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("C:\\Users\\ACER\\Documents\\code\\Jaiva3dEngine\\src\\main\\resources\\docs\\intro.j3.md");
        J3DocsReader.parseFile(file);
    }
}
