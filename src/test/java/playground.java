import com.j3d.Static;
import com.j3d.engine.interact.cmd.CommandsManager;
import com.j3d.engine.interact.cmd.commands.debug.DebugCmd;
import com.j3d.engine.interact.cmd.commands.transform.TransformCmd;
import com.j3d.errors.ErrorHandler;
import com.j3d.errors.SomeError;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;

public class playground {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
    }

    public static void fb() {
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0) System.out.print("Fizz");
            if (i % 5 == 0) System.out.print("Buzz");
            if (i % 5 != 0 && i % 3 != 0) System.out.print(i);
            System.out.println();
        }
    }
}
