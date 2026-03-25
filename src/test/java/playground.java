import com.j3d.utility.db.ThemesDB;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class playground {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        System.out.println(
                ThemesDB.getTheme(4)
        );

    }
}
