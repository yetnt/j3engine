import com.j3d.engine.interact.input.keyboard.DefaultKeys;
import com.j3d.engine.interact.input.keyboard.J3Key;
import com.j3d.storage.db.*;
import com.j3d.storage.db.api.SQLOperator;
import com.j3d.storage.db.themes.CThemes;
import com.j3d.storage.db.themes.Theme;
import com.j3d.storage.db.users.CUsers;
import com.j3d.storage.db.users.User;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public class playground {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        J3Key key = DefaultKeys.MOVE_CAM_FORWARD.getKey();
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
