import com.j3d.storage.db.Theme;
import com.j3d.storage.db.ThemesTable;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class playground {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Theme theme = ThemesTable.getTheme(1);
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
