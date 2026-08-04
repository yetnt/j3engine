import com.j3d.engine.math.MathException;
import com.j3d.engine.scene.DefaultObjectDeletionException;
import com.j3d.engine.scene.copy.InvalidCopyException;
import com.j3d.errors.J3DError;
import com.j3d.gen.settings.PrefsGenException;
import com.j3d.storage.errs.ProjectFileException;

import java.util.ArrayList;
import java.util.List;

public class playground {
    public static void main(String[] args) {
        ArrayList<J3DError> errs = new ArrayList<>(List.of(
                new ProjectFileException("Sd").code(23),
                new PrefsGenException("d").code(4),
                new DefaultObjectDeletionException("d", "G").code(901),
                new MathException("dumb math").code(1),
                new InvalidCopyException("D").code(101)
        ));
        errs.forEach(r -> System.out.println(r.codeToStr()));
    }
}
