import com.j3d.engine.interact.cmd.commands.clipboard.PasteCmd;
import com.j3d.utility.Parsing;
import com.j3d.utility.generators.JLabelRichText;

import java.awt.*;
import java.util.Arrays;

public class playground {
    public static void main(String[] args) {
        String html = new JLabelRichText(" this")
                .font(Color.GREEN, "43", Color.GREEN)
                .subscript()
                .wrapUsing(
                        new JLabelRichText(" boom")
                                .wrapDiv(40)
                ).add(
                        new JLabelRichText("fr").font(Color.GREEN, "43", Color.GREEN)
                                .subscript()
                                .wrapUsing(
                                        new JLabelRichText("g")
                                                .wrapDiv(40)
                                )
                )
                .wrapHTML();
        System.out.println(html);
        System.out.println(Parsing.removeHTML(html));
    }
}
