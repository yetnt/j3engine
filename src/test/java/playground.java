import com.j3d.engine.interact.cmd.commands.clipboard.PasteCmd;
import com.j3d.engine.math.EngineMathException;
import com.j3d.engine.scene.DefaultObjectDeletionException;
import com.j3d.errors.J3DError;
import com.j3d.gen.settings.PrefsGenException;
import com.j3d.storage.db.ConnectionReason;
import com.j3d.storage.errs.DBException;
import com.j3d.storage.errs.ProjectFileException;
import com.j3d.utility.Parsing;
import com.j3d.utility.generators.JLabelRichText;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class playground {
    public static void main(String[] args) {
        ArrayList<J3DError> errs = new ArrayList<>(List.of(
                new ProjectFileException("Sd").code(23),
                new PrefsGenException("d").code(4),
                new DefaultObjectDeletionException("d", "G").code(901),
                new EngineMathException("dumb math").code(1)
        ));
        errs.forEach(r -> System.out.println(r.codeToStr()));
    }
}
