import com.j3d.Static;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;

public class playground {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        new HashSet<>(Static.commandManager.commandsAliasMap.values()).stream().flatMap(
                c -> c.getUsages().values().stream().map(
                        s -> c.aliases.getFirst() + " " + s
                )
        ).forEach(System.out::println);
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
