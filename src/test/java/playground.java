import com.j3d.ui.J3DTheme;

import java.util.Arrays;

public class playground {
    public static void main(String[] args) {
        Arrays.stream(J3DTheme.values()).map(J3DTheme::toDbFieldName).forEach(System.out::println);
    }
}
