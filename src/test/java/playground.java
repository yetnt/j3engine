import com.j3d.storage.db.ThemesDB;
import com.j3d.storage.db.User;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class playground {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User me = User.getUser(3);
        me.firstName.setValue("Ledlel");
        me.lastName.setValue("Poleol");
        me.update();
        System.out.println();

    }
}
